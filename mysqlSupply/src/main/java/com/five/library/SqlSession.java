package com.five.library;

import com.mysql.cj.jdbc.result.ResultSetImpl;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SqlSession {
    XMLMapperParser xmlMapperParser;
    SqlBuilder sqlBuilder;
    Connection connection;
    Statement statement;
    ResultSet resultSet;

    public SqlSession(Connection connection) throws ParserConfigurationException, IOException, SAXException, SQLException {
        xmlMapperParser = new XMLMapperParser("book-mapper.xml");
        xmlMapperParser.paresXml();
        sqlBuilder = new SqlBuilder();
        this.connection = connection;
        this.statement = this.connection.createStatement();
        this.resultSet = null;
    }

    public class SqlBuilder {
        String buildSql(String id, Object parameter) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
            var sqlMap = xmlMapperParser.getSqlMap();

            XMLMapperParser.MapInfo mapInfo = sqlMap.get(id);
            if (mapInfo == null) return null;

            String clzName = mapInfo.paraType;
            String sql = mapInfo.sql;
            if (clzName == null) return sql;

            Class<?> clz = null;
            try {
                clz = Class.forName(clzName);
                for (var field : clz.getDeclaredFields()) {
                    String fieldName = field.getName();
                    String placeholder = "#{" + fieldName + "}";
                    Object value = clz.getMethod("get" + capitalize(fieldName)).invoke(parameter);
                    if (value == null) {
                        sql = sql.replace(placeholder, "NULL");
                    } else if (value instanceof String) {
                        sql = sql.replace(placeholder, "'" + value + "'");
                    } else {
                        sql = sql.replace(placeholder, value.toString());
                    }
                }
            } catch (NoSuchMethodException e) {
                if (Objects.equals(clzName, "java.lang.String")) {
                    sql = sql.replaceAll("#\\{.*?}", "'" + parameter + "'");
                } else {
                    sql = sql.replaceAll("#\\{.*?}", parameter.toString());
                }
            }

            return sql;
        }

    }

    public Boolean execute(String mapperId, Object parameter) throws SQLException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        var sql = sqlBuilder.buildSql(mapperId, parameter);
        System.out.println(sql);
        if (statement.execute(sql)) {
            var sqlMap = xmlMapperParser.getSqlMap();
            XMLMapperParser.MapInfo mapInfo = sqlMap.get(mapperId);
            parseResult(mapInfo.resType, statement.getResultSet());
            return true;
        }
        return false;
    }

    public void parseResult(String resType, ResultSet resultSet) throws SQLException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ResultParser.parseResult(resType, resultSet);
    }

    public static class ResultParser {
        static public <T> List<T> parseResult(String resType, ResultSet resultSet) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
            Class<T> resClz = null;
            T res = null;
            List<T> resList = new ArrayList<>();
            resClz = (Class<T>) Class.forName(resType);
            while (resultSet.next()) {
                try {
                    res = resClz.getConstructor().newInstance();
                    for (var field : resClz.getDeclaredFields()) {
                        var fieldName = field.getName();
                        var fieldClz = field.getType();
                        resClz.getMethod("set" + capitalize(fieldName), String.class).invoke(res, fieldClz.cast(resultSet.getObject(fieldName)));
                    }
                    resList.add(res);
                } catch (NoSuchMethodException e) {
                    resList.add(resClz.cast(resultSet.getObject(0)));
                }
            }
            return resList;
        }
    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}