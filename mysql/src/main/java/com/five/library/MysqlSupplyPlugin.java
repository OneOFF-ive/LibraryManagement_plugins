package com.five.library;

import com.five.Application;
import com.five.library.dao.BookDao;
import com.five.plugin.PluginService;

public class MysqlSupplyPlugin implements PluginService {
    @Override
    public void server(Application application) {
        var bookManger = application.getBookManager();
        bookManger.setDataAccess(new BookDao());
    }
}
