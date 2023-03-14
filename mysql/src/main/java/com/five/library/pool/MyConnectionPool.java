package com.five.library.pool;

import com.five.library.PluginConfig;
import com.five.library.ioc.Inject;
import com.five.library.pool.thread.MyThreadPool;
import com.five.logger.Logger;

import java.util.*;
import java.util.concurrent.CountDownLatch;


public class MyConnectionPool<T> implements Observer {

    private final List<T> connectionPool = Collections.synchronizedList(new ArrayList<>());
    @Inject(clz = "com.five.library.PluginConfig")
    private PluginConfig pluginConfig;
    @Inject(clz = "com.five.library.pool.SQLConnectionFactory")
    private ConnectionFactory<T> connectionFactory;
    private final Map<T, Long> connBuildTime = Collections.synchronizedMap(new HashMap<>());
    private final Object lock = new Object();
    private Timer timer;
    private MyThreadPool threadPool;

    public MyConnectionPool() {}

    public void initByIoc() {
        pluginConfig.attach(this);
        threadPool = new MyThreadPool(pluginConfig.maxSize);
        init();
    }

    public MyConnectionPool(PluginConfig pluginConfig, ConnectionFactory<T> connectionFactory) {
        this.pluginConfig = pluginConfig;
        this.pluginConfig.attach(this);
        this.connectionFactory = connectionFactory;
        threadPool = new MyThreadPool(pluginConfig.maxSize);
        init();
    }

    public void init() {
        timer = new Timer();
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
        }, pluginConfig.maxIdleTime, pluginConfig.maxIdleTime);

        try {
            CountDownLatch countDownLatch = new CountDownLatch(pluginConfig.maxSize);
            for (int i = 0; i < pluginConfig.maxSize; i++) {
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

            if (pluginConfig.checkAlways && !isConnectionValid(conn)) {
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
                if (pluginConfig.checkAlways && !isConnectionValid(conn)) {
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
            return System.currentTimeMillis() - buildTime > pluginConfig.maxIdleTime;
        }
        return false;
    }

    private boolean isConnectionValid(T conn) {
        if (pluginConfig.maxIdleTime > 0) {
            return (!pluginConfig.checkTimeOut || !isConnTimeOut(conn))
                    && (!pluginConfig.validateConnection || connectionFactory.validateConnection(conn));
        }
        return false;
    }

    public void close() {
        timer.cancel();
        connectionPool.clear();
    }

    @Override
    public void update() {
        close();
        threadPool = new MyThreadPool(pluginConfig.maxSize);
        init();
    }
}