package com.five.library;

import java.sql.ResultSet;

public class ResultInfo {
    public String resType;
    public ResultSet resultSet;

    public ResultInfo(String resType, ResultSet resultSet) {
        this.resType = resType;
        this.resultSet = resultSet;
    }
}
