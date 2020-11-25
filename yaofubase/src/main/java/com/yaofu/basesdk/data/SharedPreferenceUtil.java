package com.yaofu.basesdk.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.yaofu.basesdk.log.YLog;


/**
 * Auth:long3.yang
 * Date:2020/10/27
 **/
public class SharedPreferenceUtil {
    public static final String TAG = "SharedPreferenceUtil";
    private static final String FILE_NAME = "yk_sp"; //文件名
    private volatile static SharedPreferenceUtil mInstance;
    private volatile SharedPreferences sharedPreferences;
    private volatile SharedPreferences.Editor editor;
    private volatile boolean isInited =false;

    private SharedPreferenceUtil(){}

    public static SharedPreferenceUtil getInstance(){
        if(mInstance == null){
            synchronized (SharedPreferenceUtil.class){
                if(mInstance == null){
                    mInstance = new SharedPreferenceUtil();
                }
            }
        }
        return mInstance;
    }

    public synchronized void init(Context context){
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isInited = true;
    }

    public synchronized void reinit(Context context,String uid){
        sharedPreferences = context.getSharedPreferences(uid+"_"+FILE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isInited = true;
    }


    /**
     * 存入键值对,到内存中
     * @param key
     * @param value
     */

    public synchronized void put( String key, Object value){
        if(!isInited){
            YLog.e(TAG,"SharedPreferenceUtil do not init");
            return;
        }
        //判断类型
        String type = value.getClass().getSimpleName();
        if("Integer".equals(type)){
            editor.putInt(key,(Integer)value);
        }else if ("Boolean".equals(type)){
            editor.putBoolean(key,(Boolean)value);
        }else if ("Float".equals(type)){
            editor.putFloat(key,(Float)value);
        }else if ("Long".equals(type)){
            editor.putLong(key,(Long)value);
        }else if ("String".equals(type)){
            editor.putString(key,(String) value);
        }
        editor.apply();
    }

    /**
     * 读取键的值，若无则返回默认值
     * @param key
     * @param defValue 默认值
     * @return
     */
    @Nullable
    public synchronized Object get( String key, Object defValue) {
        if(!isInited){
            YLog.e(TAG,"SharedPreferenceUtil do not init");
           return defValue;
        }
        String type = defValue.getClass().getSimpleName();
        if("Integer".equals(type)){
            return sharedPreferences.getInt(key,(Integer)defValue);
        }else if ("Boolean".equals(type)){
            return sharedPreferences.getBoolean(key,(Boolean)defValue);
        }else if ("Float".equals(type)){
            return sharedPreferences.getFloat(key,(Float)defValue);
        }else if ("Long".equals(type)){
            return sharedPreferences.getLong(key,(Long)defValue);
        }else if ("String".equals(type)){
            return sharedPreferences.getString(key,(String) defValue);
        }
        return defValue;
    }

    public void clearPriveData(){

    }

    public void clearPublicData(){

    }

    public void clearAllData(){

    }

    public synchronized void  wirteBack(){
        if(editor != null){
            editor.commit();
        }
    }
}
