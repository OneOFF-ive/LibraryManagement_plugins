package com.five.library.pool;

import com.five.library.config.PluginConfig;
import com.five.library.ioc.Inject;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLConnectionFactory implements ConnectionFactory<Connection> {
    @Inject(clz = "com.five.library.config.PluginConfig")
    private PluginConfig pluginConfig;

    public SQLConnectionFactory() {}

    public SQLConnectionFactory(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    @Override
    public Connection buildConnection() {
        return DbUtil.getConnection(pluginConfig.url, pluginConfig.user, pluginConfig.password);
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
