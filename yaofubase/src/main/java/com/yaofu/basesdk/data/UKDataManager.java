package com.yaofu.basesdk.data;

import android.app.Application;

import com.yaofu.basesdk.proxys.OnLoginStatusChange;


/**
 * Auth:long3.yang
 * Date:2020/10/27
 **/
public class UKDataManager implements OnLoginStatusChange {

    private static final String TAG = "";
    private volatile boolean isInited = false;

    private static class RetrofitUrlManagerHolder {
        private static final UKDataManager INSTANCE = new UKDataManager();
    }

    public static final UKDataManager getInstance() {
        return RetrofitUrlManagerHolder.INSTANCE;
    }

    private UKDataManager(){

    }

    public synchronized boolean init(Application application) {
        SharedPreferenceUtil.getInstance().init(application);
        return true;
    }

    public Object getSharedPreferenceData(String key,Object def){
        return SharedPreferenceUtil.getInstance().get(key,def);
    }

    public void setSharedPreferenceData(String key,Object value){
        SharedPreferenceUtil.getInstance().put(key,value);
    }

    public OnLoginStatusChange getLoginStatusCallBack(){
        return this;
    }

    @Override
    public void onLoginIn(String uid) {

    }

    @Override
    public void onLoginOut(String msg) {

    }

    @Override
    public void onOtherStatus(String msg) {

    }


}
