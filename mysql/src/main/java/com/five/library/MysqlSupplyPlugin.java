package com.five.library;

import com.five.Application;
import com.five.data.DataAccess;
import com.five.library.dao.BookDao;
import com.five.library.ioc.IocContainer;
import com.five.logger.Logger;
import com.five.plugin.IPlugin;
import com.five.plugin.PluginContext;
import com.google.gson.Gson;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;

public class MysqlSupplyPlugin implements IPlugin {
    private PluginConfig pluginConfig;

    @Override
    public void apply(Application application) {
        var bookManger = application.getBookManager();

        var iocContainer = new IocContainer();
        try {
            registerBean(iocContainer);
            bookManger.setDataAccess((DataAccess) iocContainer.getBean("com.five.library.dao.BookDao"));
        } catch (Exception e) {
            Logger.warn("Library Mysql Supply: apply error");
            e.printStackTrace();
        }
    }

    void registerBean(IocContainer iocContainer) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        iocContainer.registerBean(PluginConfig.class.getName(), pluginConfig);
        iocContainer.registerBeanByClass(BookDao.class.getName(), BookDao.class);
    }

    public MysqlSupplyPlugin(PluginContext pluginContext) {
        File connfigFile = pluginContext.config;
        try {
            String content = new String(Files.readAllBytes(connfigFile.toPath()));
            Gson gson = new Gson();
            pluginConfig = gson.fromJson(content, PluginConfig.class);
            pluginConfig.setObservers(new ArrayList<>());
        } catch (Exception e) {
            Logger.warn("[Library Mysql Supply] init error");
            e.printStackTrace();
        }
    }

}


