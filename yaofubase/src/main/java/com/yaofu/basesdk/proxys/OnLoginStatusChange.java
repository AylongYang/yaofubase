package com.yaofu.basesdk.proxys;

/**
 * Auth:long3.yang
 * Date:2020/10/27
 **/
public interface OnLoginStatusChange {
    void onLoginIn(String uid);
    void onLoginOut(String msg);
    void onOtherStatus(String msg);
}
