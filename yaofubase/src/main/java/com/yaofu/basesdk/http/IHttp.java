package com.yaofu.basesdk.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Auth:long3.yang
 * Date:2020/10/26
 **/
public interface IHttp {

    IHttp setBaseUrl(@Nullable String url);

    String getBaseUrl();

    <C> IHttp setConfig(C config);

    IHttp build();

    <C>C getConfig();

    <T>T getService(@NonNull Class<T> service);

    <S>S obtainNewRetrofit(@NonNull String url);

    void release();
}
