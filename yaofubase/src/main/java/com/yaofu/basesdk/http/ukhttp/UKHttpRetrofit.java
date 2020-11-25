package com.yaofu.basesdk.http.ukhttp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.yaofu.basesdk.http.IHttp;


import java.util.concurrent.ConcurrentHashMap;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/**
 * Auth:long3.yang
 * Date:2020/10/26
 **/
public class UKHttpRetrofit implements IHttp {

    private OkHttpClient.Builder mConfig;
    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;
    private String mBaseUrl;
    private ConcurrentHashMap<Class,Object> mServiceList =  new ConcurrentHashMap<>();

    @Override
    public UKHttpRetrofit setBaseUrl(@Nullable String url) {
        this.mBaseUrl =url;
        return this;
    }

    @Override
    public String getBaseUrl() {
        return mBaseUrl;
    }

    @Override
    public <C> UKHttpRetrofit setConfig(C config) {
        if(config instanceof OkHttpClient.Builder){
            mConfig = (OkHttpClient.Builder)config;
            System.out.println("aylong:OkHttpClient");
        }else{
            mConfig = null;
            System.out.println("aylong:null");
        }
        return this;
    }


    @Override
    public UKHttpRetrofit build() {
        if( this.mConfig == null){
            mOkHttpClient = new OkHttpClient();
        }else{
            mOkHttpClient = mConfig.build();
        }
        mRetrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .client(mOkHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create((new GsonBuilder()).setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return this;
    }

    @Override
    public  OkHttpClient.Builder getConfig() {
        return mConfig;
    }

    @Override
    public <T> T getService(@NonNull Class<T> service) {
        if(mRetrofit != null){
            T temp = (T)mServiceList.get(service);
            if( temp== null){
                temp = mRetrofit.create(service);
                mServiceList.put(service,temp);
            }
            System.out.println("aylong: servicedizhi:"+temp);
           return temp;
        }
        return null;
    }

    @Override
    public retrofit2.Retrofit obtainNewRetrofit(@NonNull String url) {
        retrofit2.Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(mOkHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create((new GsonBuilder()).setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }

    @Override
    public void release() {
        mConfig = null;
        mRetrofit = null;
        mOkHttpClient = null;
        mBaseUrl = null;
        mServiceList.clear();
        mServiceList = null;
    }
}
