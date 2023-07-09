package com.campus.common.util;

import lombok.Data;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ObjectCloner {
    public static <T> T cloneObject(T source) throws Exception {
        Class<?> sourceClass = source.getClass();
        T target;

        try {
            Constructor<?> constructor = sourceClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            target = (T) constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Default constructor not found", e);
        }

        Field[] fields = sourceClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(source);
            field.set(target, value);
        }

        Method[] methods = sourceClass.getDeclaredMethods();
        for (Method method : methods) {
            if (isGetterMethod(method)) {
                Object value = method.invoke(source);
                String propertyName = getPropertyNameFromGetter(method.getName());
                Method setterMethod = sourceClass.getDeclaredMethod("set" + propertyName, method.getReturnType());
                setterMethod.invoke(target, value);
            }
        }

        return target;
    }

    private static boolean isGetterMethod(Method method) {
        String name = method.getName();
        return (name.startsWith("get") || name.startsWith("is")) &&
                !name.equals("getClass") &&
                method.getParameterCount() == 0;
    }

    private static String getPropertyNameFromGetter(String getterName) {
        if (getterName.startsWith("get")) {
            return getterName.substring(3);
        } else if (getterName.startsWith("is")) {
            return getterName.substring(2);
        }
        return "";
    }
}
