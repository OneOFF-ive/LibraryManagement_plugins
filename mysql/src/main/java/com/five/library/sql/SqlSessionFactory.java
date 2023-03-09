package com.five.library.sql;
import com.five.pool.MyConnectionPool;

import java.sql.Connection;


public class SqlSessionFactory {
    private final MyConnectionPool<Connection> connectionPool;
    private final XMLMapperParser xmlMapperParser;

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

