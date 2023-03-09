package com.five.library;

import com.five.Application;
import com.five.library.dao.BookDao;
import com.five.library.pool.DatabaseConfig;
import com.five.library.pool.SQLConnectionFactory;
import com.five.library.sql.SqlSessionFactory;
import com.five.plugin.IPlugin;
import com.five.plugin.PluginContext;
import com.five.pool.MyConnectionPool;
import com.five.pool.PoolConfig;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;

public class MysqlSupplyPlugin implements IPlugin {
    private final Config config;

    @Override
    public void apply(Application application) {
        var bookManger = application.getBookManager();
        var databaseConfig = new DatabaseConfig(config.url, config.user, config.password);
        var poolConfig = new PoolConfig(config.maxSize, config.maxIdleTime, config.heartBeat, config.checkTimeOut, config.validateConnection, config.checkAlways);
        var sqlConnectionFactory = new SQLConnectionFactory(databaseConfig);
        var sqlSessionFactory = new SqlSessionFactory(new MyConnectionPool<>(poolConfig, sqlConnectionFactory));

        try {
            bookManger.setDataAccess(new BookDao(sqlSessionFactory));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MysqlSupplyPlugin(PluginContext pluginContext) {
        File connfigFile = pluginContext.config;
        try {
            String content = new String(Files.readAllBytes(connfigFile.toPath()));
            Gson gson = new Gson();
            config = gson.fromJson(content, Config.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


