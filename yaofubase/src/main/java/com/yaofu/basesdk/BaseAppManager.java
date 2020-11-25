package com.yaofu.basesdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;


import java.lang.ref.WeakReference;

/**
 * Auth:long3.yang
 * Date:2020/10/21
 **/
public class BaseAppManager {

    /**
     * 初始化网络库
     */
    private volatile boolean isHttpInited = false;
    /**
     * 初始化下载库
     */
    private volatile boolean isDownloadInited = true;
    /**
     * 初始化图片加载库
     */
    private volatile boolean isImageLoadInited = false;

    private  Context ApplicationContext;

    /**
     * 初始化Context
     */
    public void init(Application app) {
        ApplicationContext = app.getApplicationContext();
        app.registerActivityLifecycleCallbacks(mCallbacks);
    }

    /**
     * 返回得到的ApplicationContext
     */
    public  Context getApplicationContext() {
        if (ApplicationContext != null) {
            return ApplicationContext;
        } else {
            return getRadiateContext();
        }
    }

    private  Context getRadiateContext() {
        try {
            @SuppressLint("PrivateApi") Application application = (Application) Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication")
                    .invoke(null, (Object[]) null);
            if (application != null) {
                ApplicationContext = application;
                return application;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            @SuppressLint("PrivateApi") Application application = (Application) Class.forName("android.app.AppGlobals")
                    .getMethod("getInitialApplication")
                    .invoke(null, (Object[]) null);
            if (application != null) {
                ApplicationContext = application;
                return application;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("application context must init");
    }

    private  Application.ActivityLifecycleCallbacks mCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {

            topActivityWeak(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            topActivityWeak(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };

    private  WeakReference<Activity> weakReference;

    private  void topActivityWeak(Activity activity) {
        if (weakReference == null || !activity.equals(weakReference.get())) {
            weakReference = new WeakReference<>(activity);
        }
    }

    public  Activity topActivity() {
        return weakReference == null ? null : weakReference.get();
    }


    private static class SingletonHolder {
        private static final BaseAppManager INSTANCE = new BaseAppManager();

    }

    private BaseAppManager() {
    }

    public static final BaseAppManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public  boolean initHttp(){
        isHttpInited = true;
        return true;
    }

    public boolean initDownload(){
        isDownloadInited = true;
        return true;
    }

    public boolean initImageLoad(){
        isImageLoadInited = true;
        return true;
    }

    public synchronized void initlalize(Application app){
        init(app);
        initHttp();
        initDownload();
        initImageLoad();
    }


}
