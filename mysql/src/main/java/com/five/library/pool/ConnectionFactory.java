package com.five.library.pool;

public interface ConnectionFactory<T> {
    T buildConnection();
    boolean validateConnection(T connection);
}
