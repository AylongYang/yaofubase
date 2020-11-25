package com.yaofu.basesdk.http;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.yaofu.basesdk.http.ukhttp.UKHttpRetrofit;
import com.yaofu.basesdk.log.YLog;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Auth:long3.yang
 * Date:2020/10/27
 * Des:优客网络管理类，使用方式：
 *
 *  HashMap<String ,String> multiHost =  new HashMap<>();
 *         multiHost.put(Constant.YOUKE_HOST,Constant.YOUK_URL);
 *         multiHost.put(Constant.MOBILE_HOST,Constant.MOBILE_URL);
 *         OkHttpClient.Builder client = new OkHttpClient.Builder()
 *                 .dns(HttpDnsUtil.getInstance())
 *                 .readTimeout(30, TimeUnit.SECONDS)
 *                 .connectTimeout(30, TimeUnit.SECONDS)
 *                 .writeTimeout(30, TimeUnit.SECONDS);
 *         UKRetrofitManager.getInstance().setBaseUrl(Constant.BASE_URL,client);
 *         UKRetrofitManager.getInstance().initDomains(multiHost,client);
 *         UKRetrofitManager.getInstance().getService(YouKeApiInterface.class).geeTestApi1(null);
 *
 *这个类的定义YouKeApiInterface 需要在类上指定host，如果不指定，则使用base host如下
 * @UKHostUrl(hosturl = Constant.MOBILE_HOST)
 * public interface YouKeApiInterface {
 * 。。。
 * }
 *
 **/
public class UKRetrofitManager {
    public static final String TAG = "aylong";
    private static String BASE_HOST = "base_host";
    private ConcurrentHashMap<String,IHttp> mDomainMap = new ConcurrentHashMap<>();
    private String mBaseUrl;

    private static class RetrofitUrlManagerHolder {
        private static final UKRetrofitManager INSTANCE = new UKRetrofitManager();
    }

    public static final UKRetrofitManager getInstance() {
        return RetrofitUrlManagerHolder.INSTANCE;
    }

    private UKRetrofitManager(){

    }

    /**
     * 获取Api service实例,
     * 使用方式： getService（ApiService.class）
     * 注意 ApiService 如果需要制定哪个host ，需要在类上添加 UKHost 注解，否则使用默认basehost
     * @param clazz
     * @param <T>
     * @return
     */
    public synchronized  <T> T getService(@NonNull Class<T> clazz) {
        T temp = null;
        try{
            UKHost domain = clazz.getAnnotation(UKHost.class);
            if(domain != null){
                String host = domain.hostname();
                System.out.println("aylong: initmanager host::"+host);
                if(!TextUtils.isEmpty(host)){
                    temp = mDomainMap.get(host).getService(clazz);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("aylong: initmanager dizhi::"+temp);
        if(temp == null){
            YLog.w(TAG,"use base host instead of , do not find host inited,please init host first");
            mDomainMap.get(BASE_HOST).getService(clazz);
        }
        return temp;
    }

    /**
     * 必须设置baseUrl, 优先设置
     * @param baseurl
     * @param config
     * @param <C>
     */
    public synchronized  <C> void setBaseUrl(@NonNull String baseurl,C config){
        mBaseUrl = baseurl;
        mDomainMap.put(BASE_HOST,new UKHttpRetrofit().setBaseUrl(baseurl).setConfig(config).build());
    }

    public synchronized <MC> void initHosts(HashMap<String, String> urlMap,MC con) {
        if(urlMap != null){
            for (Map.Entry<String, String> entry : urlMap.entrySet()) {
                mDomainMap.put(entry.getKey(),new UKHttpRetrofit().setBaseUrl(entry.getValue()).setConfig(con).build());
                System.out.println("aylong: inithost::"+entry.getValue());
            }
        }
    }

    /**
     * release http obj by host
     * @param host
     */
    public void release(@NonNull String host){
        if(mDomainMap != null){
            try{
                mDomainMap.get(host).release();
                mDomainMap.remove(host);
            }catch (Exception e){

            }
        }
    }

    /**
     * release host http obj
     */
    public void releaseAll(){
        if(mDomainMap != null){
            try{
                for (Map.Entry<String, IHttp> entry : mDomainMap.entrySet()) {
                    entry.getValue().release();
                }
                mDomainMap.clear();
            }catch (Exception e){

            }
        }
    }

}
