package com.five.library;

class Config {
    String url;
    String user;
    String password;
    int maxSize;
    long maxIdleTime;
    boolean heartBeat;
    boolean checkTimeOut;
    boolean validateConnection;
    boolean checkAlways;

    public Config(String url, String user, String password, int maxSize, long maxIdleTime, boolean heartBeat, boolean checkTimeOut, boolean validateConnection, boolean checkAlways) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.maxSize = maxSize;
        this.maxIdleTime = maxIdleTime;
        this.heartBeat = heartBeat;
        this.checkTimeOut = checkTimeOut;
        this.validateConnection = validateConnection;
        this.checkAlways = checkAlways;
    }
}
