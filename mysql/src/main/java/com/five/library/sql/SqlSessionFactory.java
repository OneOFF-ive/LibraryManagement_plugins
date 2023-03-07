package com.five.library.sql;

import com.five.library.pool.ConnectionPool;
import com.five.plugin.PluginInfo;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class SqlSessionFactory implements PoolManager {
    private final ConnectionPool connectionPool;

    public SqlSessionFactory(String settingFilePath) throws IOException, SQLException {

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(settingFilePath)) {
            String settingsContext = new String(Objects.requireNonNull(inputStream).readAllBytes(), StandardCharsets.UTF_8);
            Gson gson = new Gson();
            DatabaseConfig config = gson.fromJson(settingsContext, DatabaseConfig.class);
            connectionPool = new ConnectionPool(config.url, config.user, config.password);
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
