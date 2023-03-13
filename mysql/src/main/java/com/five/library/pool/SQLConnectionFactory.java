package com.five.library.pool;

import com.five.library.ioc.Inject;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLConnectionFactory implements ConnectionFactory<Connection> {
    @Inject(clz = "com.five.library.pool.DatabaseConfig")
    private DatabaseConfig config;

    public SQLConnectionFactory() {}

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
