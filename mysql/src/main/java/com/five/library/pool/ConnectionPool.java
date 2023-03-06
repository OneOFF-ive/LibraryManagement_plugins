package com.five.library.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {
    private final String url;
    private final String username;
    private final String password;
    private final List<Connection> connectionPool;
    private final List<Connection> usedConnections = new ArrayList<>();
    private int initialPoolSize = 10;
    private int maxPoolSize = 20;

    public ConnectionPool(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        connectionPool = new ArrayList<>(initialPoolSize);
        for (int i = 0; i < initialPoolSize; i++) {
            connectionPool.add(createConnection());
        }
    }

    private Connection createConnection() {
        return DbUtil.getConnection(url, username, password);
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connectionPool.isEmpty()) {
            if (usedConnections.size() < maxPoolSize) {
                connectionPool.add(createConnection());
            } else {
                throw new SQLException("Maximum pool size reached, no available connections!");
            }
        }
        Connection connection = connectionPool.remove(connectionPool.size() - 1);
        usedConnections.add(connection);
        return connection;
    }

    public synchronized boolean releaseConnect(Connection connection) {
        if (connection != null) {
            connectionPool.add(connection);
            return usedConnections.remove(connection);
        }
        return false;
    }

    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getPoolSize() {
        return connectionPool.size() + usedConnections.size();
    }

    public int getAvailableConnections() {
        return connectionPool.size();
    }

    public int getUsedConnections() {
        return usedConnections.size();
    }
}
