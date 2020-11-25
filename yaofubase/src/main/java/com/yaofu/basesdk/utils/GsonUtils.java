package com.yaofu.basesdk.utils;

import android.os.Looper;

import androidx.annotation.WorkerThread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Auth:long3.yang
 * Date:2020/10/21
 **/
public class GsonUtils {


    //不用创建对象,直接使用Gson.就可以调用方法
    private static Gson gson = null;

    //判断gson对象是否存在了,不存在则创建对象
    static {
        //gson = new Gson();
        //当使用GsonBuilder方式时属性为空的时候输出来的json字符串是有键值key的,显示形式是"key":null，而直接new出来的就没有"key":null的
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    //无参的私有构造方法
    private GsonUtils() {
    }

    private static void checkThread() {
        if (Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()) {

        }
    }

    /**
     * 把指定的json字符串解析为指定的对象
     */
    @WorkerThread
    public static <T> T parse(String json, TypeToken<T> token) {
        checkThread();
        Type type = token.getType();
        try {
            json = json.trim();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将对象转成json格式
     *
     * @param object
     * @return String
     */
    @WorkerThread
    public static String beanToString(Object object) {
        checkThread();
        String gsonString = null;
        if (gson != null) {
            gsonString = gson.toJson(object);
        }
        return gsonString;
    }

    /**
     * 将json转成特定的cls的对象
     *
     * @param gsonString
     * @param cls
     * @return
     */
    @WorkerThread
    public static <T> T toBean(String gsonString, Class<T> cls) {
        checkThread();
        T t = null;
        if (gson != null) {
            //传入json对象和对象类型,将json转成对象
            t = gson.fromJson(gsonString, cls);
        }
        return t;
    }

    /**
     * json字符串转成list
     *
     * @param gsonString
     * @param cls
     * @return
     */
    @WorkerThread
    public static <T> List<T> toList(String gsonString, Class<T> cls) {
        checkThread();
        List<T> list = null;
        if (gson != null) {
            Type type = new ParameterizedTypeImpl(cls);
            list = new Gson().fromJson(gsonString, type);
            return list;
        }
        return list;
    }


    private static class ParameterizedTypeImpl implements ParameterizedType {
        Class clazz;

        public ParameterizedTypeImpl(Class clz) {
            clazz = clz;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

    /**
     * 将对象转化为json字符串
     */
    @WorkerThread
    public static <T> String toJson(T t) {
        checkThread();
        return new Gson().toJson(t);
    }

    @WorkerThread
    public static String mapToString(Map<String, String> params) {
        checkThread();
        StringBuffer buffer = new StringBuffer();
        Set<String> keys = params.keySet();
        int count = keys.size();
        for (String key : keys) {
            buffer.append(key).append("=").append(params.get(key));
            count--;
            if (count > 0) {
                buffer.append("&");
            }
        }
        return buffer.toString();
    }
}
