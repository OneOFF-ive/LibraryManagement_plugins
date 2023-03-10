package com.five.library.ioc;

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
    private Object createBeanInstance(Class<?> beanClass, Object... constructorArgs) throws Exception {
        // 获取Bean对象的构造函数
        Constructor<?> constructor = beanClass.getDeclaredConstructor(getParameterTypes(constructorArgs));
        // 设置构造函数可访问
        constructor.setAccessible(true);
        // 创建Bean对象
        return constructor.newInstance(constructorArgs);
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
                // 获取字段的类型
                Class<?> fieldType = field.getType();
                // 根据类型获取对应的Bean对象
                Object dependency = beanMap.get(fieldType.getName());
                // 设置字段可访问
                field.setAccessible(true);
                // 注入依赖
                field.set(bean, dependency);
            }
        }
    }
}
