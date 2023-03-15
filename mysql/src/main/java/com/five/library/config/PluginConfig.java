package com.five.library.config;

import com.five.library.pool.Observable;
import com.five.library.pool.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PluginConfig that = (PluginConfig) o;
        return maxSize == that.maxSize && maxIdleTime == that.maxIdleTime && heartBeat == that.heartBeat && checkTimeOut == that.checkTimeOut && validateConnection == that.validateConnection && checkAlways == that.checkAlways && Objects.equals(url, that.url) && Objects.equals(user, that.user) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, user, password, maxSize, maxIdleTime, heartBeat, checkTimeOut, validateConnection, checkAlways);
    }

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

    void copy(PluginConfig newConfig) {
        this.url = newConfig.url;
        this.user = newConfig.user;
        this.password = newConfig.password;
        this.maxSize = newConfig.maxSize;
        this.maxIdleTime = newConfig.maxIdleTime;
        this.heartBeat = newConfig.heartBeat;
        this.checkTimeOut = newConfig.checkTimeOut;
        this.validateConnection = newConfig.validateConnection;
        this.checkAlways = newConfig.checkAlways;
    }
}
