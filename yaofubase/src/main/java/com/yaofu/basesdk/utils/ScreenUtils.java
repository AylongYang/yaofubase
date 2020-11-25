package com.yaofu.basesdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.widget.Toolbar;

import com.yaofu.basesdk.BaseAppManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Auth:long3.yang
 * Date:2020/10/21
 **/
public class ScreenUtils {
    /**
     * -1无请求
     * 0 false
     * 1 true
     */
    private static int x86CPU = -1;
    private static int isPad = -1;

    /**
     * 得到状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public static int getActionBarHeight(Context context, Toolbar toolbar) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true))
            return TypedValue.complexToDimensionPixelSize(typedValue.data, context.getResources().getDisplayMetrics());
        return toolbar.getHeight();
    }

    /**
     * 获得屏幕的密度
     *
     * @param context
     * @return 0.75/1/1.5/2
     */
    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static Point getRealScreenPoint() {
        return getRealWithNavigationPoint(BaseAppManager.getInstance().getApplicationContext());
    }

    private static Point getRealWithNavigationPoint(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        if (Build.VERSION.SDK_INT >= 19) {
            display.getRealSize(outPoint);
        } else {
            display.getSize(outPoint);
        }
        return outPoint;
    }

    public static int getRealScreenWidth() {
        return getScreenWidthWithNavigation(BaseAppManager.getInstance().getApplicationContext());
    }

    public static int getScreenWidthWithNavigation(Context context) {
        Point outPoint = getRealWithNavigationPoint(context);
        return outPoint.x;
    }


    public static int getRealScreenHeight() {
        return getScreenHeightWithNavigation(BaseAppManager.getInstance().getApplicationContext());
    }

    public static int getScreenHeightWithNavigation(Context context) {
        Point outPoint = getRealWithNavigationPoint(context);
        return outPoint.y;
    }


    /**
     * 获取屏幕的宽度,不包含虚拟按键（单位：px）
     *
     * @return 屏幕宽px
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenWidth() {
        return getScreenWidth(BaseAppManager.getInstance().getApplicationContext());
    }

    /**
     * 获取屏幕的高度（单位：px）
     *
     * @return 屏幕高px
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenHeight() {
        return getScreenHeight(BaseAppManager.getInstance().getApplicationContext());
    }

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int dp2px(float dpValue) {
        final float scale = BaseAppManager.getInstance().getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(float dpValue) {
        final float scale = BaseAppManager.getInstance().getApplicationContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     *
     * @param pxValue px值
     * @return dp值
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2dp(float pxValue) {
        Context context = BaseAppManager.getInstance().getApplicationContext();
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 判断是否是pad
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isPad(Context context) {
        if (isPad == 0) {
            return false;
        } else if (isPad == 1) {
            return true;
        }
        if (context == null) {
            context = BaseAppManager.getInstance().getApplicationContext().getApplicationContext();
        }
        if (context != null) {
            isPad = (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                    >= Configuration.SCREENLAYOUT_SIZE_LARGE ? 1 : 0;
        }
        return isPad == 1;
    }

    /**
     * 获取底部 navigation bar 高度
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    private int getNavigationBarHeight(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }
        int realHeight = realDisplayMetrics.heightPixels;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);
        int displayHeight = displayMetrics.heightPixels;
        return realHeight - displayHeight > 0 ? (realHeight - displayHeight) : 0;
    }

    /**
     * 获取是否存在底部 navigation bar
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            if (systemPropertiesClass == null) {
                return false;
            }
            Method m = systemPropertiesClass.getMethod("get", String.class);
            if (m == null) {
                return false;
            }
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }

    public static int hasNotchScreenHight(Context context) {
        if (hasNotchInHuawei(context)) {
            int[] notch = getNotchSize(context);
            return notch[1];
        }
        if (hasNotchInOppo(context)) {
            //其显示屏宽度为1080px，高度为2280px。刘海区域则都是宽度为324px, 高度为80px
            return 80;
        }
        if (hasNotchInScreenVoio(context)) {
            //目前vivo的刘海宽为100dp,高为27dp
            return dp2px(27);
        }
        return 0;
    }

    /**
     * 检测是否存在刘海屏 华为
     */
    public static boolean hasNotchInHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInHuawei");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e("test", "hasNotchInHuawei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("test", "hasNotchInHuawei NoSuchMethodException");
        } catch (Exception e) {
            Log.e("test", "hasNotchInHuawei Exception");
        }
        return ret;
    }

    /**
     * 获取刘海屏的参数 华为
     */
    public static int[] getNotchSize(Context context) {
        //int[0]值为刘海宽度 int[1]值为刘海高度
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getNotchSize");
            ret = (int[]) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e("test", "getNotchSize ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("test", "getNotchSize NoSuchMethodException");
        } catch (Exception e) {
            Log.e("test", "getNotchSize Exception");
        }
        return ret;
    }

    /**
     * 检测是否存在刘海屏 ooppo
     */
    public static boolean hasNotchInOppo(Context context) {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    public static String getSystemProperties(String key) {
        String value = "";
        Class<?> cls = null;
        try {
            cls = Class.forName("android.os.SystemProperties");
            Method hideMethod = cls.getMethod("get", String.class);
            Object object = cls.newInstance();
            value = (String) hideMethod.invoke(object, key);
        } catch (ClassNotFoundException e) {
            Log.e("error", "get error() ", e);
        } catch (NoSuchMethodException e) {
            Log.e("error", "get error() ", e);
        } catch (InstantiationException e) {
            Log.e("error", "get error() ", e);
        } catch (IllegalAccessException e) {
            Log.e("error", "get error() ", e);
        } catch (IllegalArgumentException e) {
            Log.e("error", "get error() ", e);
        } catch (InvocationTargetException e) {
            Log.e("error", "get error() ", e);
        }
        return value;
    }

    /**
     * vivo
     */
    public static final int NOTCH_IN_SCREEN_VOIO = 0x00000020;//是否有凹槽

    public static final int ROUNDED_IN_SCREEN_VOIO = 0x00000008;//是否有圆角

    public static boolean hasNotchInScreenVoio(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class FtFeature = cl.loadClass("com.util.FtFeature");
            Method get = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) get.invoke(FtFeature, NOTCH_IN_SCREEN_VOIO);

        } catch (ClassNotFoundException e) {
            Log.e("test", "hasNotchInHuawei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("test", "hasNotchInHuawei NoSuchMethodException");
        } catch (Exception e) {
            Log.e("test", "hasNotchInHuawei Exception");
        }
        return ret;

    }

    public static boolean isX86CPU() {
        if (x86CPU >= 0) {
            return x86CPU == 1;
        }
        String cpuAbi = Build.CPU_ABI;
        String hardware = "";
        String arch = "";
        try {
            hardware = getSystemProperty("ro.hardware");
            arch = System.getProperty("os.arch");
        } catch (Exception var4) {
            var4.printStackTrace();
        }
        boolean x86 = cpuAbi.toLowerCase().startsWith("x86") || hardware.toLowerCase().equals("intel") || arch.toLowerCase().equals("i686");
        x86CPU = x86 ? 1 : 0;
        return x86;
    }

    private static String getSystemProperty(String name) throws Exception {
        Class systemPropertyClazz = Class.forName("android.os.SystemProperties");
        return (String) systemPropertyClazz.getMethod("get", new Class[]{String.class})
                .invoke(systemPropertyClazz, new Object[]{name});
    }


    public static String getSystem() {
        String sysName = "";
        FileInputStream inputStream = null;
        try {
            Properties prop = new Properties();
            inputStream = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
            prop.load(inputStream);
            if (prop.getProperty("ro.miui.ui.version.code", null) != null
                    || prop.getProperty("ro.miui.ui.version.name", null) != null
                    || prop.getProperty("ro.miui.internal.storage", null) != null) {
                sysName = "sys_miui";//小米
            } else if (prop.getProperty("ro.build.hw_emui_api_level", null) != null
                    || prop.getProperty("ro.build.version.emui", null) != null
                    || prop.getProperty("ro.confg.hw_systemversion", null) != null) {
                sysName = "sys_emui";//华为
            } else if (getMeizuFlymeOSFlag().toLowerCase().contains("flyme")) {
                sysName = "sys_flyme";//魅族
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sysName;
    }

    public static String getMeizuFlymeOSFlag() {
        String flag = "";
        try {
            flag = getSystemProperty("ro.build.display.id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    public static boolean isHuaweiPhone() {
        return "sys_emui".equals(getSystem());
    }

    /**
     * 隐藏虚拟栏 ，显示的时候再隐藏掉
     */
    public static void hideNavigationBar(final Window window) {
        if (window == null) {
            return;
        }
        final View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        decorView.setOnSystemUiVisibilityChangeListener(
                new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                //布局位于状态栏下方
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                //全屏
                                View.SYSTEM_UI_FLAG_FULLSCREEN |
                                //隐藏导航栏
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                        if (Build.VERSION.SDK_INT >= 19) {
                            uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                        } else {
                            uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                        }
                        decorView.setSystemUiVisibility(uiOptions);
                    }
                });
    }

    /**
     * dialog 需要全屏的时候用，和clearFocusNotAle() 成对出现
     * 在show 前调用  focusNotAle   show后调用clearFocusNotAle
     */
    public static void focusNotAle(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }


    /**
     * dialog 需要全屏的时候用，focusNotAle() 成对出现
     * 在show 前调用  focusNotAle   show后调用clearFocusNotAle
     */
    public static void clearFocusNotAle(Window window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    /**
     * 获取虚拟功能键高度
     */

    public static int getVirtualBarHeigh(Context context) {

        int vh = 0;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();

        try {

            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");

            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);

            method.invoke(display, dm);
            vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;
    }

    public static class ScreenSize {
        int width;
        int height;

        public ScreenSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

}
