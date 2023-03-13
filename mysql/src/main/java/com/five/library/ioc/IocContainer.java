package com.five.library.ioc;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class IocContainer {

    // 存储Bean对象的Map
    private final Map<String, Object> beanMap = new HashMap<>();

    // 注册Bean对象
    public void registerBean(String beanName, Class<?> beanClass, Object... constructorArgs) throws InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        // 利用反射创建Bean对象
        Object bean = createBeanInstance(beanClass, constructorArgs);
        // 将Bean对象存储到Map中
        beanMap.put(beanName, bean);
        // 注入Bean对象的依赖
        injectBeanDependencies(bean);

        // 如果有initByIoc方法则执行
        try {
            var mtd = beanClass.getMethod("initByIoc");
            mtd.invoke(bean);
        } catch (NoSuchMethodException ignore) {}
    }

    // 获取Bean对象
    public Object getBean(String beanName) {
        return beanMap.get(beanName);
    }

    // 利用反射创建Bean对象
    @SuppressWarnings("unchecked")
    private <T> T createBeanInstance(Class<T> beanClass, Object... constructorArgs) throws InvocationTargetException, InstantiationException, IllegalAccessException {

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

    // 注入Bean对象的依赖
    private void injectBeanDependencies(Object bean) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
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
                    registerBean(annotation.clz(), Class.forName(annotation.clz()));
                    dependency = getBean(annotation.clz());
                }
                // 设置字段可访问
                field.setAccessible(true);
                // 注入依赖
                field.set(bean, dependency);
            }
        }
    }

}
