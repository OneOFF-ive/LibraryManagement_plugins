package com.five.library;

import com.five.library.mirror.ObjectMirror;
import com.five.library.type.TypeHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResultParser {
    @SuppressWarnings("unchecked")
    static public <T> List<T> parseResult(String resType, ResultSet resultSet) throws SQLException {
        List<T> resList = new ArrayList<>();
        Class<T> resClz = TypeHandler.handle(resType);

        if (isJavaBean(resClz)) {
            var objMirror = ObjectMirror.createFromJavaBean(resClz);
            T res = objMirror.create();

            while (resultSet.next()) {
                for (var field : resClz.getDeclaredFields()) {
                    var fieldName = field.getName();
                    var fieldValue = resultSet.getObject(fieldName);
                    objMirror.set(res, fieldName, fieldValue);
                }
                resList.add(res);
            }
        }
        else {
            while (resultSet.next()) {
                T res = (T) resultSet.getObject(1);
                resList.add(res);
            }
        }

        return resList;
    }

    public static <T> boolean isJavaBean(Class<T> clz) {
        return !(TypeHandler.isWrapperClz(clz) || Objects.equals(String.class, clz));
    }
}
