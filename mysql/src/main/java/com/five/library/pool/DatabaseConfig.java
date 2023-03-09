package com.five.library.pool;

public class DatabaseConfig {
    public String url;
    public String user;
    public String password;

    public DatabaseConfig(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }
}
