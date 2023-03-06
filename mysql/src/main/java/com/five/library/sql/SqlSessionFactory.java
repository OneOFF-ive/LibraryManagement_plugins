package com.five.library.sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.five.library.pool.ConnectionPool;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class SqlSessionFactory implements PoolManager {
    private final ConnectionPool connectionPool;

    public SqlSessionFactory(String settingFilePath) {
        var jsonFile = new File(settingFilePath);
        DatabaseConfig config = null;
        try {
            if (jsonFile.exists() || jsonFile.createNewFile()) {
                var objectMapper = new ObjectMapper();
                config = objectMapper.readValue(jsonFile, DatabaseConfig.class);
            }
            connectionPool =  new ConnectionPool(Objects.requireNonNull(config).url, config.user, config.password);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SqlSession build() {
        Connection connection = getConnection();
        if (connection == null) return null;
        return new SqlSession(connection, this);
    }

    @Override
    public Connection getConnection() {
        try {
            return connectionPool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean releaseConnection(Connection connection) {
        return connectionPool.releaseConnect(connection);
    }
}

class DatabaseConfig {
    String url;
    String user;
    String password;
}
