package com.five.library;

import com.five.library.sql.DbUtil;

import java.sql.Connection;

public class BookDAOFactory {

    public static BookDAO getBookDAO() {
        // 获取数据库连接
        Connection conn = DbUtil.getConnection();

        // 返回与数据库连接相关联的BookDAO实例
        return new BookDAOImpl(conn);
    }
}
