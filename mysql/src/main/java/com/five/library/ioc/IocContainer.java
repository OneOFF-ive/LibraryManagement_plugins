package com.five.library.ioc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class IocContainer {

    // 存储Bean对象的Map
    private final Map<String, Object> beanMap = new HashMap<>();

    // 注册Bean对象
    public void registerBean(String beanName, Class<?> beanClass, Object... constructorArgs) throws Exception {
        // 利用反射创建Bean对象
        Object bean = createBeanInstance(beanClass, constructorArgs);
        // 将Bean对象存储到Map中
        beanMap.put(beanName, bean);
        // 注入Bean对象的依赖
        injectBeanDependencies(bean);
    }

    // 获取Bean对象
    public Object getBean(String beanName) {
        return beanMap.get(beanName);
    }

    // 利用反射创建Bean对象
    private <T> T createBeanInstance(Class<T> beanClass, Object... constructorArgs) throws Exception {
//        var parameterTypes = getParameterTypes(constructorArgs);
//        Constructor<?> constructor = null;
//        // 获取Bean对象的构造函数
//        try {
//            constructor = beanClass.getDeclaredConstructor(parameterTypes);
//        } catch (NoSuchMethodException e) {
//            try {
//                constructor = beanClass.getDeclaredConstructor(unwrapPrimitiveClassArray(parameterTypes));
//            } catch (NoSuchMethodException e1) {
//
//                Constructor<?>[] constructors = beanClass.getDeclaredConstructors();
//
//                throw new RuntimeException(e1);
//            }
//        }
//
//        // 设置构造函数可访问
//        constructor.setAccessible(true);
//        // 创建Bean对象
//        return constructor.newInstance(constructorArgs);

        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length != constructorArgs.length) {
                continue;
            }
            boolean match = true;
            for (int i = 0; i < parameterTypes.length; i++) {
                if (!parameterTypes[i].isAssignableFrom(constructorArgs[i].getClass())) {
                    match = false;
                    break;
                }
            }
            if (match) {
                constructor.setAccessible(true);
                return (T) constructor.newInstance(constructorArgs);
            }
        }
        throw new RuntimeException("No matching constructor found");

    }

    // 获取构造函数参数类型
    private Class<?>[] getParameterTypes(Object... args) {
        Class<?>[] parameterTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        return parameterTypes;
    }

    // 注入Bean对象的依赖
    private void injectBeanDependencies(Object bean) throws Exception {
        // 获取Bean对象的所有字段
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 判断字段是否有@Inject注解
            if (field.isAnnotationPresent(Inject.class)) {
                Inject annotation = field.getAnnotation(Inject.class);
                // 获取字段的类型
                Class<?> fieldType = field.getType();
                // 根据类型获取对应的Bean对象
                Object dependency = beanMap.get(annotation.clz());
                if (dependency == null) {
                    dependency = createBeanInstance(Class.forName(annotation.clz()));
                }
                // 设置字段可访问
                field.setAccessible(true);
                // 注入依赖
                field.set(bean, dependency);
            }
        }
    }

    private static Class<?>[] unwrapPrimitiveClassArray(Class<?>[] wrappedClassArray) {
        Class<?>[] unwrappedClassArray = new Class[wrappedClassArray.length];
        for (int i = 0; i < wrappedClassArray.length; i++) {
            Class<?> wrappedClass = wrappedClassArray[i];
            if (wrappedClass == Integer.class) {
                unwrappedClassArray[i] = int.class;
            } else if (wrappedClass == Long.class) {
                unwrappedClassArray[i] = long.class;
            } else if (wrappedClass == Float.class) {
                unwrappedClassArray[i] = float.class;
            } else if (wrappedClass == Double.class) {
                unwrappedClassArray[i] = double.class;
            } else if (wrappedClass == Boolean.class) {
                unwrappedClassArray[i] = boolean.class;
            } else if (wrappedClass == Byte.class) {
                unwrappedClassArray[i] = byte.class;
            } else if (wrappedClass == Character.class) {
                unwrappedClassArray[i] = char.class;
            } else if (wrappedClass == Short.class) {
                unwrappedClassArray[i] = short.class;
            }
            else {
                unwrappedClassArray[i] = wrappedClass;
            }
        }
        return unwrappedClassArray;
    }

    public static Object[] unwrapPrimitiveArray(Object[] wrappedArray) {
        Object[] unwrappedArray = new Object[wrappedArray.length];
        for (int i = 0; i < wrappedArray.length; i++) {
            Object wrapped = wrappedArray[i];
            Class<?> wrappedClass = wrapped.getClass();
            if (wrappedClass == Integer.class) {
                unwrappedArray[i] = ((Integer) wrapped).intValue();
            } else if (wrappedClass == Long.class) {
                unwrappedArray[i] = ((Long) wrapped).longValue();
            } else if (wrappedClass == Float.class) {
                unwrappedArray[i] = ((Float) wrapped).floatValue();
            } else if (wrappedClass == Double.class) {
                unwrappedArray[i] = ((Double) wrapped).doubleValue();
            } else if (wrappedClass == Boolean.class) {
                unwrappedArray[i] = ((Boolean) wrapped).booleanValue();
            } else if (wrappedClass == Byte.class) {
                unwrappedArray[i] = ((Byte) wrapped).byteValue();
            } else if (wrappedClass == Character.class) {
                unwrappedArray[i] = ((Character) wrapped).charValue();
            } else if (wrappedClass == Short.class) {
                unwrappedArray[i] = ((Short) wrapped).shortValue();
            } else {
                unwrappedArray[i] = wrappedArray[i];
            }
        }
        return unwrappedArray;
    }
}
