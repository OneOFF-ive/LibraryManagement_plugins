package com.five.library.dao;

import com.five.Book;
import com.five.data.DataAccess;
import com.five.library.ioc.Inject;
import com.five.library.sql.SqlSessionFactory;

import java.sql.SQLException;
import java.util.List;


public class BookDao implements DataAccess {
    @Inject(clz = "com.five.library.sql.SqlSessionFactory")
    private SqlSessionFactory sqlSessionFactory;

    public BookDao(SqlSessionFactory sqlSessionFactory) throws SQLException {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public BookDao() {}

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Override
    public void insertData(Book book) {
        var sqlSession = sqlSessionFactory.openSqlSession();
        sqlSession.execute("com.five.plugin.insert", book);
        sqlSessionFactory.closeSqlSession(sqlSession);
    }

    @Override
    public void removeData(String isbn) {
        var sqlSession = sqlSessionFactory.openSqlSession();
        sqlSession.execute("com.five.plugin.delete", isbn);
        sqlSessionFactory.closeSqlSession(sqlSession);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Book> getDataBy(String field, Object para) {
        String mapperId = "com.five.plugin.selectBy" + capitalize(field);
        List<Book> res = null;
        var sqlSession = sqlSessionFactory.openSqlSession();
        var hasRes = sqlSession.execute(mapperId, para);
        if (hasRes) {
            res = (List<Book>) sqlSession.getResult();
        }
        sqlSessionFactory.closeSqlSession(sqlSession);
        return res;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Book> getAllData() {
        List<Book> res = null;
        var sqlSession = sqlSessionFactory.openSqlSession();
        var hasRes = sqlSession.execute("com.five.plugin.selectAll", null);
        if (hasRes) {
            res = (List<Book>) sqlSession.getResult();
        }
        sqlSessionFactory.closeSqlSession(sqlSession);
        return res;
    }

    @Override
    public void updateData(Book book) {
        var sqlSession = sqlSessionFactory.openSqlSession();
        sqlSession.execute("com.five.plugin.update", book);
        sqlSessionFactory.closeSqlSession(sqlSession);
    }

    @Override
    public void open() {}

    @Override
    public void close() {
        sqlSessionFactory.closeConnectPool();
    }

    public static String capitalize(String str) {
        if (str == null) return null;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
