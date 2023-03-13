package com.five.library;

import com.five.Application;
import com.five.library.dao.BookDao;
import com.five.library.ioc.IocContainer;
import com.five.library.pool.*;
import com.five.library.sql.SqlSessionFactory;
import com.five.plugin.IPlugin;
import com.five.plugin.PluginContext;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;

public class MysqlSupplyPlugin implements IPlugin {
    private final Config config;

    @Override
    public void apply(Application application) {
        var bookManger = application.getBookManager();
//        var databaseConfig = new DatabaseConfig(config.url, config.user, config.password);
//        var poolConfig = new PoolConfig(config.maxSize, config.maxIdleTime, config.heartBeat, config.checkTimeOut, config.validateConnection, config.checkAlways);
//        var sqlConnectionFactory = new SQLConnectionFactory(databaseConfig);
//        var sqlSessionFactory = new SqlSessionFactory(new MyConnectionPool<>(poolConfig, sqlConnectionFactory));

        var iocContainer = new IocContainer();
        try {
            registerBean(iocContainer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            var sqlSessionFactory = (SqlSessionFactory) iocContainer.getBean(SqlSessionFactory.class.getName());
            bookManger.setDataAccess(new BookDao(sqlSessionFactory));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void registerBean(IocContainer iocContainer) throws Exception {
        iocContainer.registerBean(DatabaseConfig.class.getName(), DatabaseConfig.class, config.url, config.user, config.password);
        iocContainer.registerBean(PoolConfig.class.getName(), PoolConfig.class, config.maxSize, config.maxIdleTime, config.heartBeat, config.checkTimeOut, config.validateConnection, config.checkAlways);
        iocContainer.registerBean(SQLConnectionFactory.class.getName(), SQLConnectionFactory.class);
        iocContainer.registerBean(MyConnectionPool.class.getName(), MyConnectionPool.class);
        iocContainer.registerBean(SqlSessionFactory.class.getName(), SqlSessionFactory.class);
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


