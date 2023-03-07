package com.five.library;

import com.five.Application;
import com.five.library.dao.BookDao;
import com.five.library.sql.SqlSessionFactory;
import com.five.plugin.PluginService;

import java.sql.SQLException;

public class MysqlSupplyPlugin implements PluginService {
//    public void provideService(IServiceRegistry sr) {
//        sr.singleton(IDataAccess.class, () -> new BookDao());
//        sr.instance(IDataAccess.class, new BookDao());
//    }
//
//    public void inject(IServieCotainer sc) {
//        sc.resolve(ILoggerService.class);
//        sc.resolve(IConfigService.class);
//    }

    @Override
    public void server(Application application) {
        var bookManger = application.getBookManager();
        try {
            bookManger.setDataAccess(new BookDao());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
