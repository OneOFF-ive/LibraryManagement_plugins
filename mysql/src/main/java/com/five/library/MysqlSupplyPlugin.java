package com.five.library;

import com.five.Application;
import com.five.data.DataAccess;
import com.five.library.config.ConfigLoader;
import com.five.library.config.FileListener;
import com.five.library.config.PluginConfig;
import com.five.library.dao.BookDao;
import com.five.library.ioc.IocContainer;
import com.five.logger.Logger;
import com.five.plugin.IPlugin;
import com.five.plugin.PluginContext;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class MysqlSupplyPlugin implements IPlugin {
    private FileListener listener;
    private final ConfigLoader configLoader;

    @Override
    public void apply(Application application) {
        var bookManger = application.getBookManager();

        var iocContainer = new IocContainer();
        try {
            registerBean(iocContainer);
            bookManger.setDataAccess((DataAccess) iocContainer.getBean("com.five.library.dao.BookDao"));
        } catch (Exception e) {
            Logger.warn("[Library Mysql Supply] apply error");
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        listener.close();
    }

    void registerBean(IocContainer iocContainer) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var pluginConfig = configLoader.getPluginConfig();
        iocContainer.registerBean(PluginConfig.class.getName(), pluginConfig);
        iocContainer.registerBeanByClass(BookDao.class.getName(), BookDao.class);
    }

    public MysqlSupplyPlugin(PluginContext pluginContext) {
        var configFile = pluginContext.config;
        configLoader = new ConfigLoader(configFile);
        try {
            configLoader.loadConfig();
            listener = new FileListener(configFile, configLoader);
            listener.start();
        } catch (IOException e) {
            Logger.warn("[Library Mysql Supply] init error");
            e.printStackTrace();
        }
    }

}


