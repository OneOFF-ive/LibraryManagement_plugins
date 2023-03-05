package com.five.library.mirror;


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.function.Supplier;

public class ObjectMirror<T> {
    public final Supplier<T> constructor;
    public final HashMap<String, Prop> name2Prop;

    public ObjectMirror(Supplier<T> constructor, HashMap<String, Prop> name2Prop) {
        this.constructor = constructor;
        this.name2Prop = name2Prop;
    }

    public T create() {
        return constructor.get();
    }

    public void set(Object receiver, String field, Object value) {
        var prop = this.name2Prop.get(capitalize(field));
        if (prop == null) {
            throw new RuntimeException("No such property " + field);
        }
        if (prop.setter == null) {
            throw new RuntimeException(prop + "has no setter.");
        }
        prop.setter.set(receiver, value);
    }

    @SuppressWarnings("unchecked")
    public <R> R get(Object receiver, String field) {
        var prop = this.name2Prop.get(capitalize(field));
        if (prop == null) {
            throw new RuntimeException("No such property " + field);
        }
        if (prop.getter == null) {
            throw new RuntimeException(prop + "has no getter.");
        }
        return (R) prop.getter.get(receiver);
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static <T> ObjectMirror<T> createFromJavaBean(Class<T> clz) {
        try {
            var ctorObj = clz.getConstructor();
            Supplier<T> ctor = () -> ctor(ctorObj);
            var paramName2Prop = new HashMap<String, Prop>();
            for (var mtd : clz.getMethods()) {
                var name = mtd.getName();
                if (name.startsWith("set")) {
                    name = name.substring(3);
                    var prop = getOrCreateProp(paramName2Prop, name);
                    prop.setter = (receiver, value) -> invokeSetter(mtd, receiver, value);
                } else if (name.startsWith("get")) {
                    name = name.substring(3);
                    var prop = getOrCreateProp(paramName2Prop, name);
                    prop.getter = (receiver) -> invokeGetter(mtd, receiver);
                }
            }
            return new ObjectMirror<>(ctor, paramName2Prop);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Prop getOrCreateProp(
            HashMap<String, Prop> paramName2Prop,
            String name
    ) {
        var prop = paramName2Prop.get(name);
        if (prop == null) {
            prop = new Prop();
            paramName2Prop.put(name, prop);
        }
        return prop;
    }

    private static <T> T ctor(Constructor<T> ctor) {
        try {
            return ctor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <Owner, TV> void invokeSetter(Method mtd, Owner owner, TV value) {
        try {
            mtd.invoke(owner, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <Owner, TV> TV invokeGetter(Method mtd, Owner owner) {
        try {
            return (TV) mtd.invoke(owner);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
