package com.yaofu.basesdk.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Auth:long3.yang
 * Date:2020/10/21
 **/
public class ClassUtils {

    public static <T> T getT(Object o, int i) {
        Type type = o.getClass().getGenericSuperclass();
        if (type != null) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<T> params = (Class<T>) parameterizedType.getActualTypeArguments()[i];
                if (params != null) {
                    try {
                        return params.newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        /*try {
            return ((Class<T>) ((ParameterizedType) (o.getClass()
                    .getGenericSuperclass())).getActualTypeArguments()[i])
                    .newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }*/
        return null;
    }

    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
