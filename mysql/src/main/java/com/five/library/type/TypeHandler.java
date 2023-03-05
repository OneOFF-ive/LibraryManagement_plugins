package com.five.library.type;

import java.util.*;

public class TypeHandler {
    private final static Map<String, Class<?>> name2Class = new HashMap<>();
    private final static Set<Class<?>> wrapperClz = new HashSet<>();

    static {
        name2Class.put("int", Integer.class);
        wrapperClz.add(Integer.class);
        name2Class.put("double", Double.class);
        wrapperClz.add(Double.class);
        name2Class.put("float", Float.class);
        wrapperClz.add(Float.class);
        name2Class.put("byte", Byte.class);
        wrapperClz.add(Byte.class);
        name2Class.put("short", Short.class);
        wrapperClz.add(Short.class);
        name2Class.put("boolean", Boolean.class);
        wrapperClz.add(Boolean.class);
        name2Class.put("long", Long.class);
        wrapperClz.add(Long.class);
        name2Class.put("char", Character.class);
        wrapperClz.add(Character.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> handle(String typeName) {
        try {
            Class<T> resClz = (Class<T>) name2Class.get(typeName);
            if (resClz == null) {
                resClz = (Class<T>) Class.forName(typeName);
                name2Class.put(typeName, resClz);
            }
            return resClz;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isWrapperClz(Class<?> clz) {
        return wrapperClz.contains(clz);
    }

    public static <T> boolean isJavaBean(Class<T> clz) {
        return !(TypeHandler.isWrapperClz(clz) || Objects.equals(String.class, clz));
    }
}
