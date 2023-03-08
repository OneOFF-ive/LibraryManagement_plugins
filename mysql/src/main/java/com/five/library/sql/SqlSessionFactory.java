package com.five.library.sql;

import com.five.library.pool.SQLConnectionFactory;
import com.five.pool.MyConnectionPool;
import com.five.pool.PoolConfig;

import java.sql.Connection;


public class SqlSessionFactory {
    private final MyConnectionPool<Connection> connectionPool;
    private final XMLMapperParser xmlMapperParser;

    public SqlSessionFactory() {
        connectionPool = new MyConnectionPool<>(new PoolConfig(), new SQLConnectionFactory());
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

