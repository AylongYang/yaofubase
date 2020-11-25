package com.yaofu.basesdk.log;


import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.Settings;

public class YLog {

    private static final String RELEASE = "release";
    private static final String VIVO_RELEASE = "vivoRelease";
    private static final String TAG_LOG = "zmYouKe";
    private static String sBuildType = RELEASE;

    public static void init(String buildType) {
        sBuildType = buildType;
        Settings settings = Logger.init(TAG_LOG)
                .logLevel((RELEASE.equals(buildType) || VIVO_RELEASE.equals(buildType)) ? LogLevel.NONE : LogLevel.FULL)
                .methodCount(1)/*方法栈打印的个数，默认是5*/
                .methodOffset(1);
    }

    public static void v(String msg, Object... args) {
        Logger.v(msg, args);
    }

    public static void v(String tag, String msg, Object... args) {
        Logger.t(tag).v(msg, args);
    }

    public static void d(String msg, Object... args) {
        Logger.d(msg, args);
    }

    public static void d(String tag, String msg, Object... args) {
        Logger.t(tag).d(msg, args);
    }

    public static void i(String msg, Object... args) {
        Logger.i(msg, args);
    }

    public static void i(String tag, String msg, Object... args) {
        Logger.t(tag).i(msg, args);
    }

    public static void w(String msg) {
        Logger.w(msg);
    }

    public static void w(String tag, String msg) {
        Logger.t(tag).w(msg);
    }

    public static void e(String msg, Object... args) {
        if (RELEASE.equals(sBuildType) || VIVO_RELEASE.equals(sBuildType)) {
            logError(TAG_LOG, msg, args);
        } else {
            Logger.e(msg, args);
        }
    }

    public static void e(String tag, String msg, Object... args) {
        if (RELEASE.equals(sBuildType) || VIVO_RELEASE.equals(sBuildType)) {
            logError(tag, msg, args);
        } else {
            Logger.t(tag).e(msg, args);
        }
    }

    private static void logError(String tag, String msg, Object... args) {
        StackTraceElement traceElement = new Throwable().getStackTrace()[1];
        String fileName = traceElement.getFileName();
        String calssName = traceElement.getClassName() + "::";
        calssName += traceElement.getMethodName() + "::";
        calssName += traceElement.getLineNumber() + ">>>";

        android.util.Log.e(tag + "::" + fileName, calssName + ">>>"
                + (args == null || args.length == 0 ? msg : String.format(msg, args)));
    }

    public static void json(String msg) {
        Logger.json(msg);
    }

    public static void json(String tag, String msg) {
        Logger.t(tag).json(msg);
    }

    public static void xml(String msg) {
        Logger.xml(msg);
    }

    public static void xml(String tag, String msg) {
        Logger.t(tag).xml(msg);
    }

    public static void wtf(String msg) {
        Logger.wtf(msg);
    }

    public static void wtf(String tag, String msg) {
        Logger.t(tag).wtf(msg);
    }

    public static void file(String msg) {
        Logger.json(msg);
    }

    public static void exc(Exception exc, Object... args) {
        if (RELEASE.equals(sBuildType) || VIVO_RELEASE.equals(sBuildType)) {
            logError(TAG_LOG, exc.getLocalizedMessage(), args);
        } else {
            exc.printStackTrace();
        }
    }
}
