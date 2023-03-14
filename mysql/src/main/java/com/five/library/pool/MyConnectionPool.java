package com.five.library.pool;

import com.five.library.ioc.Inject;
import com.five.library.pool.thread.MyThreadPool;
import com.five.logger.Logger;

import java.util.*;
import java.util.concurrent.CountDownLatch;


public class MyConnectionPool<T> {

    private List<T> connectionPool;
    @Inject(clz = "com.five.library.pool.PoolConfig")
    private PoolConfig poolConfig;
    @Inject(clz = "com.five.library.pool.SQLConnectionFactory")
    private ConnectionFactory<T> connectionFactory;
    private Map<T, Long> connBuildTime;
    private Object lock;
    private Timer timer;
    private MyThreadPool threadPool;

    public MyConnectionPool() {}

    public void initByIoc() {
        connectionPool = Collections.synchronizedList(new ArrayList<>(poolConfig.maxSize));
        connBuildTime = Collections.synchronizedMap(new HashMap<>());
        lock = new Object();
        timer = new Timer();
        threadPool = new MyThreadPool(poolConfig.maxSize);
        init();
    }

    public MyConnectionPool(PoolConfig poolConfig, ConnectionFactory<T> connectionFactory) {
        this.connectionPool = Collections.synchronizedList(new ArrayList<>(poolConfig.maxSize));
        this.poolConfig = poolConfig;
        this.connectionFactory = connectionFactory;
        connBuildTime = Collections.synchronizedMap(new HashMap<>());
        lock = new Object();
        timer = new Timer();
        threadPool = new MyThreadPool(poolConfig.maxSize);
        init();
    }

    public void init() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Logger.info("[Library Mysql Supply] connect pool: start heartBeat");
                connectionPool.forEach((conn) -> {
                    if (!isConnectionValid(conn)) {
                        connBuildTime.remove(conn);
                        conn = connectionFactory.buildConnection();
                        connBuildTime.put(conn, System.currentTimeMillis());
                    }
                });
                Logger.info("[Library Mysql Supply] connect pool: finish heartBeat");
            }
        }, poolConfig.maxIdleTime, poolConfig.maxIdleTime);

        try {
            CountDownLatch countDownLatch = new CountDownLatch(poolConfig.maxSize);
            for (int i = 0; i < poolConfig.maxSize; i++) {
                threadPool.execute(() -> {
                    var conn = connectionFactory.buildConnection();
                    connBuildTime.put(conn, System.currentTimeMillis());
                    connectionPool.add(conn);
                    countDownLatch.countDown();
                });
            }
            countDownLatch.await();
            threadPool.shutdown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public T getConnection() {
        synchronized (lock) {

            while (connectionPool.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            T conn = connectionPool.remove(connectionPool.size() - 1);

            if (poolConfig.checkAlways && !isConnectionValid(conn)) {
                connBuildTime.remove(conn);
                conn = connectionFactory.buildConnection();
                connBuildTime.put(conn, System.currentTimeMillis());
            }
            Logger.info("[Library Mysql Supply] connect pool: rent a conn");
            return conn;
        }
    }

    public boolean releaseConnection(T conn) {
        synchronized (lock) {
            if (connBuildTime.remove(conn) != null) {
                if (poolConfig.checkAlways && !isConnectionValid(conn)) {
                    conn = connectionFactory.buildConnection();
                    connBuildTime.put(conn, System.currentTimeMillis());
                }
                connectionPool.add(conn);

                try {
                    lock.notify();
                } catch (IllegalMonitorStateException ignored) {}
                Logger.info("[Library Mysql Supply] connect pool: return a conn");
                return true;
            }
            return false;
        }

    }

    public synchronized int available() {
        return connectionPool.size();
    }

    public boolean isConnTimeOut(T conn) {
        var buildTime = connBuildTime.get(conn);
        if (buildTime != null) {
            return System.currentTimeMillis() - buildTime > poolConfig.maxIdleTime;
        }
        return false;
    }

    private boolean isConnectionValid(T conn) {
        if (poolConfig.maxIdleTime > 0) {
            return (!poolConfig.checkTimeOut || !isConnTimeOut(conn))
                    && (!poolConfig.validateConnection || connectionFactory.validateConnection(conn));
        }
        return false;
    }

    public void close() {
        timer.cancel();
        connectionPool.clear();
    }


    public List<T> getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(List<T> connectionPool) {
        this.connectionPool = connectionPool;
    }

    public PoolConfig getPoolConfig() {
        return poolConfig;
    }

    public void setPoolConfig(PoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }

    public ConnectionFactory<T> getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory<T> connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Map<T, Long> getConnBuildTime() {
        return connBuildTime;
    }

    public void setConnBuildTime(Map<T, Long> connBuildTime) {
        this.connBuildTime = connBuildTime;
    }

    public Object getLock() {
        return lock;
    }

    public void setLock(Object lock) {
        this.lock = lock;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public MyThreadPool getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(MyThreadPool threadPool) {
        this.threadPool = threadPool;
    }
}