package com.yaofu.basesdk.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Auth:long3.yang
 * Date:2020/10/21
 **/
public class HandlerUtils {
    private static Handler mainPoster = null;

    /**
     * post一个runnable到主线程
     */
    public static boolean post(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
            return true;
        } else {
            return getMainPoster().post(runnable);
        }
    }

    /**
     * post一个runnable到主线程
     */
    public static boolean postDelayed(Runnable runnable, long delayMillis) {
        return getMainPoster().postDelayed(runnable, delayMillis);
    }

    private static Handler getMainPoster() {
        if (mainPoster == null) {
            synchronized (HandlerUtils.class) {
                if (mainPoster == null) {
                    mainPoster = new Handler(Looper.getMainLooper());
                }
            }
        }
        return mainPoster;
    }

    public static void disposeMessage() {
        if (mainPoster != null) {
            mainPoster.removeCallbacksAndMessages(null);
            mainPoster = null;
        }
    }
}
