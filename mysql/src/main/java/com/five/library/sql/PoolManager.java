package com.five.library.sql;

import java.sql.Connection;

public interface PoolManager {
    Connection getConnection();
    boolean releaseConnection(Connection connection);
}
