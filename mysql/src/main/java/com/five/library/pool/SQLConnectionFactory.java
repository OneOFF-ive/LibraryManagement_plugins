package com.five.library.pool;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class SQLConnectionFactory implements com.five.pool.ConnectionFactory<Connection> {
    private final DatabaseConfig config;

    public SQLConnectionFactory(DatabaseConfig config) {
        this.config = config;
    }

    @Override
    public Connection buildConnection() {
        return DbUtil.getConnection(config.url, config.user, config.password);
    }

    @Override
    public boolean validateConnection(Connection connection) {
        try {
            return connection.isValid(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
