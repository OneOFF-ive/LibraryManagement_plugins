package com.five.library.sql;
import com.five.library.ioc.Inject;
import com.five.library.pool.MyConnectionPool;

import java.sql.Connection;


public class SqlSessionFactory {
    @Inject(clz = "com.five.library.pool.MyConnectionPool")
    private MyConnectionPool<Connection> connectionPool;
    private final XMLMapperParser xmlMapperParser;

    public SqlSessionFactory() {
        try {
            xmlMapperParser = new XMLMapperParser("book-mapper.xml");
            xmlMapperParser.paresXml();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SqlSessionFactory(MyConnectionPool<Connection> connectionPool) {
        this.connectionPool = connectionPool;
        try {
            xmlMapperParser = new XMLMapperParser("book-mapper.xml");
            xmlMapperParser.paresXml();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SqlSession openSqlSession() {
        Connection connection = connectionPool.getConnection();
        return new SqlSession(connection, xmlMapperParser);
    }

    public boolean closeSqlSession(SqlSession sqlSession) {
        return connectionPool.releaseConnection(sqlSession.connection);
    }

    public void closeConnectPool() {
        connectionPool.close();
    }

}

