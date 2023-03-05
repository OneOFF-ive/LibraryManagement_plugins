package com.five.library.dao;

import com.five.Book;
import com.five.data.DataAccess;
import com.five.library.sql.DbUtil;
import com.five.library.sql.SqlSession;
import java.util.List;


public class BookDao implements DataAccess {
    private final SqlSession sqlSession;

    public BookDao() {
        sqlSession = new SqlSession(DbUtil.getConnection());
    }

    @Override
    public void insertData(Book book) {
        sqlSession.execute("com.five.plugin.insert", book);
    }

    @Override
    public void removeData(String isbn) {
        sqlSession.execute("com.five.plugin.delete", isbn);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Book> getDataBy(String field, Object para) {
        String mapperId = "com.five.plugin.selectBy" + capitalize(field);
        List<Book> res = null;
        var hasRes = sqlSession.execute(mapperId, para);
        if (hasRes) {
            res = (List<Book>) sqlSession.getResult();
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Book> getAllData() {
        List<Book> res = null;
        var hasRes = sqlSession.execute("com.five.plugin.selectAll", null);
        if (hasRes) {
            res = (List<Book>) sqlSession.getResult();
        }
        return res;
    }

    @Override
    public void updateData(Book book) {
        sqlSession.execute("com.five.plugin.update", book);
    }

    @Override
    public void saveData() {
    }

    @Override
    public void readData() {
    }

    public static String capitalize(String str) {
        if (str == null) return null;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
