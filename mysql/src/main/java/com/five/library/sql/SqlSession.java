package com.five.library.sql;

import com.five.library.mirror.ObjectMirror;
import com.five.library.type.TypeHandler;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

import static com.five.library.type.TypeHandler.isJavaBean;

public class SqlSession {
    XMLMapperParser xmlMapperParser;
    SqlBuilder sqlBuilder;
    Connection connection;
    Statement statement;
    ResultInfo resultInfo;


    public class SqlBuilder {
        String buildSql(String id, Object parameter) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
            var id2TagInfo = xmlMapperParser.getId2TagInfo();

            XMLMapperParser.TagInfo tagInfo = id2TagInfo.get(id);
            if (tagInfo == null) return null;

            String clzName = tagInfo.paraType;
            String sql = tagInfo.sql;
            if (clzName == null) return sql;

            Class<?> paraClz = TypeHandler.handle(clzName);
            if (isJavaBean(paraClz)) {
                var objMirror = ObjectMirror.createFromJavaBean(paraClz);
                for (var field : paraClz.getDeclaredFields()) {
                    var fieldName = field.getName();
                    var placeholder = "#{" + fieldName + "}";
                    var fieldValue = objMirror.get(parameter, fieldName);
                    if (fieldValue == null) {
                        sql = sql.replace(placeholder, "NULL");
                    } else if (fieldValue instanceof String) {
                        sql = sql.replace(placeholder, "'" + fieldValue + "'");
                    } else {
                        sql = sql.replace(placeholder, fieldValue.toString());
                    }
                }
            } else if (Objects.equals(String.class, paraClz)) {
                sql = sql.replaceAll("#\\{.*?}", "'" + parameter + "'");
            } else {
                sql = sql.replaceAll("#\\{.*?}", parameter.toString());
            }

            return sql;
        }

    }

    public SqlSession(Connection connection, XMLMapperParser xmlMapperParser) {
        try {
            this.xmlMapperParser = xmlMapperParser;
            sqlBuilder = new SqlBuilder();
            this.connection = connection;
            this.statement = this.connection.createStatement();
            this.resultInfo = null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isActive() {
        return connection != null;
    }

    public boolean execute(String mapperId, Object parameter) {
        try {
            var sql = sqlBuilder.buildSql(mapperId, parameter);
            System.out.println(sql);
            if (statement.execute(sql)) {
                var sqlMap = xmlMapperParser.getId2TagInfo();
                XMLMapperParser.TagInfo tagInfo = sqlMap.get(mapperId);
                resultInfo = new ResultInfo(tagInfo.resType, statement.getResultSet());
                return true;
            }
            return false;
        } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<?> parseResult(String resType, ResultSet resultSet) throws SQLException {
        return ResultParser.parseResult(resType, resultSet);
    }

    public List<?> getResult() {
        try {
            if (resultInfo == null) return null;
            return parseResult(resultInfo.resType, resultInfo.resultSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
