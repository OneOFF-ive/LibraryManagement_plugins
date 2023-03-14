package com.five.library.pool;

import java.util.List;

public class PoolConfig implements Observable{
    public int maxSize;
    public long maxIdleTime;
    public boolean heartBeat;
    public boolean checkTimeOut;
    public boolean validateConnection;
    public boolean checkAlways;
    private List<Observer> observers;

    public PoolConfig() {
        this.maxSize = 5;
        this.maxIdleTime = 600000;
        heartBeat = true;
        checkTimeOut = true;
        validateConnection = true;
        checkAlways = true;
    }

    public PoolConfig(int maxSize, long maxIdleTime, boolean heartBeat, boolean checkTimeOut, boolean validateConnection, boolean checkAlways) {
        this.maxSize = maxSize;
        this.maxIdleTime = maxIdleTime;
        this.heartBeat = heartBeat;
        this.checkTimeOut = checkTimeOut;
        this.validateConnection = validateConnection;
        this.checkAlways = checkAlways;
    }

    public PoolConfig(Integer maxSize, Long maxIdleTime, Boolean heartBeat, Boolean checkTimeOut, Boolean validateConnection, Boolean checkAlways) {
        this.maxSize = maxSize;
        this.maxIdleTime = maxIdleTime;
        this.heartBeat = heartBeat;
        this.checkTimeOut = checkTimeOut;
        this.validateConnection = validateConnection;
        this.checkAlways = checkAlways;
    }

    public PoolConfig setMaxSize(int n) {
        this.maxSize = n;
        notifyObservers();
        return this;
    }

    public PoolConfig setMaxIdleTime(long maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
        notifyObservers();
        return this;
    }

    public PoolConfig setHeartBeat(boolean heartBeat) {
        this.heartBeat = heartBeat;
        notifyObservers();
        return this;
    }

    public PoolConfig setCheckTimeOut(boolean checkTimeOut) {
        this.checkTimeOut = checkTimeOut;
        notifyObservers();
        return this;
    }

    public PoolConfig setValidateConnection(boolean validateConnection) {
        this.validateConnection = validateConnection;
        notifyObservers();
        return this;
    }

    public PoolConfig setCheckAlways(boolean checkAlways) {
        this.checkAlways = checkAlways;
        notifyObservers();
        return this;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    public boolean isHeartBeat() {
        return heartBeat;
    }

    public boolean isCheckTimeOut() {
        return checkTimeOut;
    }

    public boolean isValidateConnection() {
        return validateConnection;
    }

    public boolean isCheckAlways() {
        return checkAlways;
    }

    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void remove(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (var observer : observers) {
            observer.update();
        }
    }
}
