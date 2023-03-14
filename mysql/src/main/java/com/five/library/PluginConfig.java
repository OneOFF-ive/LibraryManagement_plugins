package com.five.library;

import com.five.library.pool.Observable;
import com.five.library.pool.Observer;

import java.util.ArrayList;
import java.util.List;

public class PluginConfig implements Observable {
    public String url;
    public String user;
    public String password;
    public int maxSize;
    public long maxIdleTime;
    public boolean heartBeat;
    public boolean checkTimeOut;
    public boolean validateConnection;
    public boolean checkAlways;
    private List<Observer> observers;

    public PluginConfig(String url, String user, String password, int maxSize, long maxIdleTime, boolean heartBeat, boolean checkTimeOut, boolean validateConnection, boolean checkAlways) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.maxSize = maxSize;
        this.maxIdleTime = maxIdleTime;
        this.heartBeat = heartBeat;
        this.checkTimeOut = checkTimeOut;
        this.validateConnection = validateConnection;
        this.checkAlways = checkAlways;
        this.observers = new ArrayList<>();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    public void setMaxIdleTime(long maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    public boolean isHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(boolean heartBeat) {
        this.heartBeat = heartBeat;
    }

    public boolean isCheckTimeOut() {
        return checkTimeOut;
    }

    public void setCheckTimeOut(boolean checkTimeOut) {
        this.checkTimeOut = checkTimeOut;
    }

    public boolean isValidateConnection() {
        return validateConnection;
    }

    public void setValidateConnection(boolean validateConnection) {
        this.validateConnection = validateConnection;
    }

    public boolean isCheckAlways() {
        return checkAlways;
    }

    public void setCheckAlways(boolean checkAlways) {
        this.checkAlways = checkAlways;
    }

    public List<Observer> getObservers() {
        return observers;
    }

    public void setObservers(List<Observer> observers) {
        this.observers = observers;
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
