package com.campus.parttime.dto;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 解析表单
 *
 * 将表单中的数据注入对应类的对象中
 * */
public class FormTemplate {
    public static <T> T analyzeTemplate(Object form, Class<T> clazz) {
        // 检查form是否为空
        if (form == null) {
            throw new IllegalArgumentException("Form表单为空");
        }
        T object;
        try {
            // 使用默认构造函数创建目标类的实例
            object = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            // 如果对象创建失败，则抛出运行时异常
            throw new RuntimeException("无法创建类型为 " + clazz.getSimpleName() + " 的对象", e);
        }

        // 获取表单对象的字段
        Field[] formFields = form.getClass().getDeclaredFields();
        // 创建字段名称与字段对象的映射关系的Hash射映
        Map<String, Field> fieldMap = new HashMap<>();
        for (Field formField : formFields) {
            // 将字段名称和字段添加到映射关系中
            fieldMap.put(formField.getName(), formField);
        }

        // 获取目标类的字段
        Field[] targetFields = clazz.getDeclaredFields();
        for (Field targetField : targetFields) {
            try {
                // 获取与目标类字段对应的表单字段
                Field formField = fieldMap.get(targetField.getName());
                if (formField != null) {
                    // 设置表单字段可访问
                    formField.setAccessible(true);
                    // 获取表单字段的值
                    Object value = formField.get(form);

                    // 获取目标类字段对应的 setter 方法
                    Method setterMethod = getSetterMethod(clazz, targetField.getName(), targetField.getType());
                    if (setterMethod != null) {
                        // 使用反射调用 setter 方法将值设置给目标类的属性
                        setterMethod.invoke(object, value);
                    }

                    // 获取目标类字段对应的 getter 方法
                    Method getterMethod = getGetterMethod(clazz, targetField.getName(), targetField.getType());
                    if (getterMethod != null) {
                        // 使用反射调用 getter 方法获取属性值
                        Object fieldValue = getterMethod.invoke(object);
                    }
                }
            } catch (Exception e) {
                // 如果设置属性值失败，则抛出运行时异常
                throw new RuntimeException("无法为字段 " + targetField.getName() + " 设置属性值", e);
            }
        }

        // 返回填充完数据的目标类对象
        return object;
    }

    public static Method getSetterMethod(Class<?> clazz, String fieldName, Class<?> fieldType) {
        // 构造目标类字段对应的 setter 方法名称
        String setterMethodName = "set" + capitalize(fieldName);
        try {
            // 获取目标类中声明的 setter 方法
            return clazz.getDeclaredMethod(setterMethodName, fieldType);
        } catch (NoSuchMethodException e) {
            // 如果没有找到对应的 setter 方法，则返回 null
            return null;
        }
    }

    public static Method getGetterMethod(Class<?> clazz, String fieldName, Class<?> fieldType) {
        // 构造目标类字段对应的 getter 方法名称
        String getterMethodName = "get" + capitalize(fieldName);
        try {
            // 获取目标类中声明的 getter 方法
            return clazz.getDeclaredMethod(getterMethodName);
        } catch (NoSuchMethodException e) {
            // 如果没有找到对应的 getter 方法，则返回 null
            return null;
        }
    }

    private static String capitalize(String str) {
        //字母大写化
        if (str == null || str.isEmpty()) {
            return str;
        }
        // 将字符串的首字母大写
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}




