package com.five.library;

import com.five.Application;
import com.five.data.DataAccess;
import com.five.library.dao.BookDao;
import com.five.library.ioc.IocContainer;
import com.five.library.pool.*;
import com.five.logger.Logger;
import com.five.plugin.IPlugin;
import com.five.plugin.PluginContext;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;

public class MysqlSupplyPlugin implements IPlugin {
    private PluginConfig pluginConfig;

    @Override
    public void apply(Application application) {
        var bookManger = application.getBookManager();

        var iocContainer = new IocContainer();
        try {
            registerBean(iocContainer);
            bookManger.setDataAccess((DataAccess) iocContainer.getBean("com.five.library.dao.BookDao"));
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                 ClassNotFoundException e) {
            Logger.warn("Library Mysql Supply: apply error");
            e.printStackTrace();
        }
    }

    void registerBean(IocContainer iocContainer) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        iocContainer.registerBean(DatabaseConfig.class.getName(), DatabaseConfig.class, pluginConfig.url, pluginConfig.user, pluginConfig.password);
        iocContainer.registerBean(PoolConfig.class.getName(), PoolConfig.class, pluginConfig.maxSize, pluginConfig.maxIdleTime, pluginConfig.heartBeat, pluginConfig.checkTimeOut, pluginConfig.validateConnection, pluginConfig.checkAlways);
        iocContainer.registerBean(BookDao.class.getName(), BookDao.class);
    }

    public MysqlSupplyPlugin(PluginContext pluginContext) {
        File connfigFile = pluginContext.config;
        try {
            String content = new String(Files.readAllBytes(connfigFile.toPath()));
            Gson gson = new Gson();
            pluginConfig = gson.fromJson(content, PluginConfig.class);
        } catch (IOException e) {
            Logger.warn("[Library Mysql Supply] init error");
            e.printStackTrace();
        }
    }

}


