package com.five.library.pool;

public interface Observable {
    void attach(Observer observer);
    void remove(Observer observer);
    void notifyObservers();
}
