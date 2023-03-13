package com.five.library;

import com.five.Application;
import com.five.data.DataAccess;
import com.five.library.dao.BookDao;
import com.five.library.ioc.IocContainer;
import com.five.library.pool.*;
import com.five.library.sql.SqlSessionFactory;
import com.five.plugin.IPlugin;
import com.five.plugin.PluginContext;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;

public class MysqlSupplyPlugin implements IPlugin {
    private final Config config;

    @Override
    public void apply(Application application) {
        var bookManger = application.getBookManager();

        var iocContainer = new IocContainer();
        try {
            registerBean(iocContainer);
            bookManger.setDataAccess((DataAccess) iocContainer.getBean("com.five.library.dao.BookDao"));
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    void registerBean(IocContainer iocContainer) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        iocContainer.registerBean(DatabaseConfig.class.getName(), DatabaseConfig.class, config.url, config.user, config.password);
        iocContainer.registerBean(PoolConfig.class.getName(), PoolConfig.class, config.maxSize, config.maxIdleTime, config.heartBeat, config.checkTimeOut, config.validateConnection, config.checkAlways);
        iocContainer.registerBean(BookDao.class.getName(), BookDao.class);
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


