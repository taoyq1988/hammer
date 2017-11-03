package com.tao.hammer.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 枚举的工具类
 *
 * @author tyq
 * @version 1.0, 2017/11/3
 */
public class EnumUtils {

    public static final String CODE = "code";

    /**
     * 查找具有对应注解的字段，如无，则查找字段名为code的字段
     *
     * @param clazz 类
     * @param codeAnnotations 需要查找的注解
     * @return 返回有对应注解的字段，或是code字段
     */
    public static Field findCodeField(Class clazz, Class... codeAnnotations) {
        Field codeField = null;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(CODE)) {
                codeField = field;
            }
            if (codeAnnotations != null && codeAnnotations.length > 0) {
                for (Class codeAnnotation : codeAnnotations) {
                    Annotation annotation = field.getAnnotation(codeAnnotation);
                    if (annotation != null) {
                        codeField = field;
                        break;
                    }
                }
            }
        }
        return codeField;
    }

    /**
     * 根据字段找到该枚举类，返回字段值与枚举的对应关系
     *
     * @param field 字段
     * @return 枚举的字段值与枚举的对应关系
     */
    public static Map<Object, Enum> getCodeToEnumMap(Field field) {
        Object[] enums = field.getDeclaringClass().getEnumConstants();
        Map<Object, Enum> codeToEnum = new HashMap<Object, Enum>();
        field.setAccessible(true);
        for (Object enumObject : enums) {
            try {
                Object code = field.get(enumObject);
                codeToEnum.put(code, (Enum) enumObject);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return codeToEnum;
    }

    /**
     * 根据字段找到该枚举类，返回字段值的字符串值与枚举的对应关系
     *
     * @param field 字段
     * @return 枚举的字段的字符串值与枚举的对应关系
     * @see EnumUtils#getCodeToEnumMap(Field)
     */
    public static Map<String, Enum> getStringCodeToEnumMap(Field field) {
        Object[] enums = field.getDeclaringClass().getEnumConstants();
        Map<String, Enum> codeToEnum = new HashMap<String, Enum>();
        field.setAccessible(true);
        for (Object enumObject : enums) {
            try {
                Object code = field.get(enumObject);
                String key = code == null ? null : String.valueOf(code);
                codeToEnum.put(key, (Enum) enumObject);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return codeToEnum;
    }


}
