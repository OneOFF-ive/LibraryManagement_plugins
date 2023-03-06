package com.five.library.dao;

import com.five.Book;
import com.five.data.DataAccess;
import com.five.library.sql.SqlSessionFactory;

import java.util.List;


public class BookDao implements DataAccess {
    private final SqlSessionFactory sqlSessionFactory;

    public BookDao() {
        this.sqlSessionFactory = new SqlSessionFactory("database-setting.xml");
    }

    @Override
    public void insertData(Book book) {
        var sqlSession = sqlSessionFactory.build();
        sqlSession.execute("com.five.plugin.insert", book);
        sqlSession.close();
    }

    @Override
    public void removeData(String isbn) {
        var sqlSession = sqlSessionFactory.build();
        sqlSession.execute("com.five.plugin.delete", isbn);
        sqlSession.close();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Book> getDataBy(String field, Object para) {
        String mapperId = "com.five.plugin.selectBy" + capitalize(field);
        List<Book> res = null;
        var sqlSession = sqlSessionFactory.build();
        var hasRes = sqlSession.execute(mapperId, para);
        if (hasRes) {
            res = (List<Book>) sqlSession.getResult();
        }
        sqlSession.close();
        return res;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Book> getAllData() {
        List<Book> res = null;
        var sqlSession = sqlSessionFactory.build();
        var hasRes = sqlSession.execute("com.five.plugin.selectAll", null);
        if (hasRes) {
            res = (List<Book>) sqlSession.getResult();
        }
        sqlSession.close();
        return res;
    }

    @Override
    public void updateData(Book book) {
        var sqlSession = sqlSessionFactory.build();
        sqlSession.execute("com.five.plugin.update", book);
        sqlSession.close();
    }

    @Override
    public void open() {

    }

    @Override
    public void close() {

    }

    public static String capitalize(String str) {
        if (str == null) return null;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
