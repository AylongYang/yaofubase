package com.yaofu.basesdk.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.yaofu.basesdk.BaseAppManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by kyle on 2016/4/25. 连接网络的工具类
 */
public class NetworkUtils {
    /**
     * Network type is unknown
     */
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    /**
     * Current network is GPRS
     */
    public static final int NETWORK_TYPE_GPRS = 1;
    /**
     * Current network is EDGE
     */
    public static final int NETWORK_TYPE_EDGE = 2;
    /**
     * Current network is UMTS
     */
    public static final int NETWORK_TYPE_UMTS = 3;
    /**
     * Current network is CDMA: Either IS95A or IS95B
     */
    public static final int NETWORK_TYPE_CDMA = 4;
    /**
     * Current network is EVDO revision 0
     */
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    /**
     * Current network is EVDO revision A
     */
    public static final int NETWORK_TYPE_EVDO_A = 6;
    /**
     * Current network is 1xRTT
     */
    public static final int NETWORK_TYPE_1xRTT = 7;
    /**
     * Current network is HSDPA
     */
    public static final int NETWORK_TYPE_HSDPA = 8;
    /**
     * Current network is HSUPA
     */
    public static final int NETWORK_TYPE_HSUPA = 9;
    /**
     * Current network is HSPA
     */
    public static final int NETWORK_TYPE_HSPA = 10;
    /**
     * Current network is iDen
     */
    public static final int NETWORK_TYPE_IDEN = 11;
    /**
     * Current network is EVDO revision B
     */
    public static final int NETWORK_TYPE_EVDO_B = 12;
    /**
     * Current network is LTE
     */
    public static final int NETWORK_TYPE_LTE = 13;
    /**
     * Current network is eHRPD
     */
    public static final int NETWORK_TYPE_EHRPD = 14;
    /**
     * Current network is HSPA+
     */
    public static final int NETWORK_TYPE_HSPAP = 15;
    private static final int NETWORK_TYPE_UNAVAILABLE = -1;
    // private static final int NETWORK_TYPE_MOBILE = -100;
    private static final int NETWORK_TYPE_WIFI = -101;
    private static final int NETWORK_CLASS_WIFI = -101;
    private static final int NETWORK_CLASS_UNAVAILABLE = -1;
    /**
     * Unknown network class.
     */
    private static final int NETWORK_CLASS_UNKNOWN = 0;
    /**
     * Class of broadly defined "2G" networks.
     */
    private static final int NETWORK_CLASS_2_G = 1;
    /**
     * Class of broadly defined "3G" networks.
     */
    private static final int NETWORK_CLASS_3_G = 2;
    /**
     * Class of broadly defined "4G" networks.
     */
    private static final int NETWORK_CLASS_4_G = 3;

    /*
     *判断是否有网络连接
     * */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /*
     * 判断WIFI网络是否连接
     * */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo != null) {
                    return networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
                }
            }
        }
        return false;
    }

    /*
     * 判断MOBILE网络是否可用
     * */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo =
                    mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /*
     *获取当前网络连接的类型信息
     * */
    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

    public static int getCurrentNetworkType(Context mcontext) {
        int networkClass = getNetworkClass(mcontext);
        int type = 0;
        switch (networkClass) {
            case NETWORK_CLASS_UNAVAILABLE:
                type = 0;
                break;
            case NETWORK_CLASS_WIFI:
                type = 5;
                break;
            case NETWORK_CLASS_2_G:
                type = 2;
                break;
            case NETWORK_CLASS_3_G:
                type = 3;
                break;
            case NETWORK_CLASS_4_G:
                type = 4;
                break;
            case NETWORK_CLASS_UNKNOWN:
                type = 5;
                break;
            default:
        }
        return type;
    }

    public static String getCurrentNetworkTypeName(Context mcontext) {
        int networkClass = getNetworkClass(mcontext);
        String type = "";
        switch (networkClass) {
            case NETWORK_CLASS_UNAVAILABLE:
                type = "UNKNOWN";
                break;
            case NETWORK_CLASS_WIFI:
                type = "WIFI";
                break;
            case NETWORK_CLASS_2_G:
                type = "2G";
                break;
            case NETWORK_CLASS_3_G:
                type = "3G";
                break;
            case NETWORK_CLASS_4_G:
                type = "4G";
                break;
            case NETWORK_CLASS_UNKNOWN:
                type = "UNKNOWN";
                break;
            default:
        }
        return type;
    }

    private static int getNetworkClass(Context mcontext) {
        int networkType = NETWORK_TYPE_UNKNOWN;
        try {
            final NetworkInfo network =
                    ((ConnectivityManager) mcontext.getSystemService(Context.CONNECTIVITY_SERVICE))
                            .getActiveNetworkInfo();
            if (network != null && network.isAvailable() && network.isConnected()) {
                int type = network.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    networkType = NETWORK_TYPE_WIFI;
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    TelephonyManager telephonyManager =
                            (TelephonyManager) mcontext.getSystemService(Context.TELEPHONY_SERVICE);
                    networkType = telephonyManager.getNetworkType();
                }
            } else {
                networkType = NETWORK_TYPE_UNAVAILABLE;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return getNetworkClassByType(networkType);
    }

    private static int getNetworkClassByType(int networkType) {
        switch (networkType) {
            case NETWORK_TYPE_UNAVAILABLE:
                return NETWORK_CLASS_UNAVAILABLE;
            case NETWORK_TYPE_WIFI:
                return NETWORK_CLASS_WIFI;
            case NETWORK_TYPE_GPRS:
            case NETWORK_TYPE_EDGE:
            case NETWORK_TYPE_CDMA:
            case NETWORK_TYPE_1xRTT:
            case NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;
            case NETWORK_TYPE_UMTS:
            case NETWORK_TYPE_EVDO_0:
            case NETWORK_TYPE_EVDO_A:
            case NETWORK_TYPE_HSDPA:
            case NETWORK_TYPE_HSUPA:
            case NETWORK_TYPE_HSPA:
            case NETWORK_TYPE_EVDO_B:
            case NETWORK_TYPE_EHRPD:
            case NETWORK_TYPE_HSPAP:
                return NETWORK_CLASS_3_G;
            case NETWORK_TYPE_LTE:
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }

    /**
     * 判断设备 是否使用代理上网
     *
     * @return 是否使用了代理 true:使用了代理,false:没有使用代理
     */
    public static boolean isWifiProxy(Context context) {

        final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;

        String proxyAddress;

        int proxyPort;

        if (IS_ICS_OR_LATER) {

            proxyAddress = System.getProperty("http.proxyHost");

            String portStr = System.getProperty("http.proxyPort");

            proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
        } else {

            proxyAddress = android.net.Proxy.getHost(context);

            proxyPort = android.net.Proxy.getPort(context);
        }

        return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
    }

    /**
     * 获取当前连接Wi-Fi的信号强弱
     *
     * @param context
     * @return [0-1] 信号较弱
     */
    public static int getWifiLevel(Context context) {
//    int level; //信号强度值
//    int wifi_level = 0;
        WifiInfo wifiInfo = null;
        WifiManager wifiManager = null; //Wifi管理器
        // 获得WifiManager
        try {
            wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (wifiManager == null) {
            return 0;
        }
        try {
            wifiInfo = wifiManager.getConnectionInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (wifiInfo == null) {
            return 0;
        }
        return WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5);
        //获得信号强度值
//    level = wifiInfo.getRssi();
//    //根据获得的信号强度发送信息
//    if (level <= 0 && level >= -50) {
//      //信号最好
//      wifi_level = 4;
//    } else if (level < -50 && level >= -70) {
//      //信号较好
//      wifi_level = 3;
//    } else if (level < -70 && level >= -80) {
//      //信号一般
//      wifi_level = 2;
//    } else if (level < -80 && level >= -100) {
//      //信号较差
//      wifi_level = 1;
//    } else {
//      //无信号
//      wifi_level = 0;
//    }
//
//    return wifi_level;
    }


    public static String getDelay(String ipAddress) {
        String delay = "";
        try {
            Process p = Runtime.getRuntime().exec("ping -c 4 -w 1 " + ipAddress);
            BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String str;
            while ((str = buf.readLine()) != null) {
//            if (str.contains("packet loss")) {
//                int i = str.indexOf("received");
//                int j = str.indexOf("%");
//                System.out.println("丢包率:" + str.substring(i + 10, j + 1));
//                lost = str.substring(i + 10, j + 1);
//            }
                if (str.contains("avg")) {
                    int i = str.indexOf("/", 20);
                    int j = str.indexOf(".", i);
                    System.out.println("延迟:" + str.substring(i + 1, j));
                    delay = str.substring(i + 1, j);
                    delay = delay + "ms";
                }
                if (!TextUtils.isEmpty(delay)) {
                    return delay;
                }
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 解析域名获取IP数组
     *
     * @param host
     * @return
     */
    public static String[] parseHostGetIPAddress(String host) {
        String[] ipAddressArr = null;
        try {
            InetAddress[] inetAddressArr = InetAddress.getAllByName(host);
            if (inetAddressArr != null && inetAddressArr.length > 0) {
                ipAddressArr = new String[inetAddressArr.length];
                for (int i = 0; i < inetAddressArr.length; i++) {
                    ipAddressArr[i] = inetAddressArr[i].getHostAddress();
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
        return ipAddressArr;
    }


    /***
     * 判断当前是否在网络环境下
     *
     * @return
     */
    public static boolean isNetworkConnected() {
        boolean bret = false;
        NetworkInfo mNetworkInfo = getCurNetworkInfo();
        if (mNetworkInfo != null) {
            bret = mNetworkInfo.isAvailable();
        }
        return bret;
    }

    /***
     * 判断当前是否使用wifi网络
     *
     * @return
     */
    public static boolean isWifiNetwork() {
        return "WIFI".equalsIgnoreCase(getApnType());
    }

    /***
     * 判断当前是否使用移动网络包括2G,3G,4G
     *
     * @return
     */
    public static boolean isMobileNetwork() {
        return "MOBILE".equalsIgnoreCase(getApnType());
    }

    private static String getApnType() {
        String ret = "";
        NetworkInfo mNetworkInfo = getCurNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
            ret = mNetworkInfo.getTypeName();
        }
        return ret;
    }

    private static NetworkInfo getCurNetworkInfo() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) BaseAppManager.getInstance().getApplicationContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnectivityManager != null) {
            return mConnectivityManager.getActiveNetworkInfo();
        }
        return null;
    }

}
