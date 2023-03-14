package com.five.library.pool;

import java.util.ArrayList;
import java.util.List;

public class DatabaseConfig implements Observable {
    public String url;
    public String user;
    public String password;
    private final List<Observer> observers = new ArrayList<>();

    public DatabaseConfig(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        notifyObservers();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
        notifyObservers();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyObservers();
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
