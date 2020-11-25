package com.yaofu.basesdk.managers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;


import com.yaofu.basesdk.log.YLog;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 不允许被继承
 * Created by liujing on 2018/3/9.
 */

public final class NetPingManager {
    public static final String DOMAIN_BAIDU = "www.baidu.com";
    private final WeakReference<Context> contextWeakReference;
    private String mDomain; // 接口域名
    private InetAddress[] mAddress;
    private List<String> mAddressIpList;
    private IOnNetPingListener mIOnNetPingListener; // 将监控日志上报到前段页面
    private HandlerThread mHandlerThread;

    //统计10秒一次直播延迟，和网络延迟
    private static int DELAY_TIME = 10 * 1000;
    private ConnectivityManager manager;
    private final Handler mHandleMessage;
    private static final String TAG = NetPingManager.class.getSimpleName();

    /**
     * 延迟
     */
    public void setDuration(int delay) {
        DELAY_TIME = delay;
    }

    /**
     * 初始化网络诊断服务
     */
    public NetPingManager(Context context, String domain, IOnNetPingListener theListener) {
        contextWeakReference = new WeakReference<>(context);
        this.mDomain = domain;
        this.mIOnNetPingListener = theListener;
        this.mAddressIpList = new ArrayList<>();
        if (null != contextWeakReference.get()) {
            this.manager = (ConnectivityManager) contextWeakReference.get().getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        this.mHandlerThread = new HandlerThread("ping");
        this.mHandlerThread.start();
        this.mHandleMessage = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        if (mIOnNetPingListener != null) {
                            mIOnNetPingListener.onLiveDelay();
                        }
                        //每次请求清空上传集合
                        if (null != mAddressIpList) {
                            mAddressIpList.clear();
                        }
                        startNetDiagnosis();
                        if (first) {
                            first = false;
                            if (mIOnNetPingListener != null) {
                                mIOnNetPingListener.firstExecute();
                            }
                        }
                        if (null != mHandlerThread) {
                            mHandleMessage.sendEmptyMessageDelayed(0, DELAY_TIME);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private boolean first = true;

    public void startNetMonitor() {
        if (null != this.mHandleMessage) {
            mHandleMessage.sendEmptyMessage(0);
        }
    }

    public void takeOnceNetDiagnosis() {
        if (null != mAddressIpList) {
            mAddressIpList.clear();
        }
        startNetDiagnosis();
    }

    public void release() {
        synchronized (NetPingManager.class) {
            if (null != this.manager)
                this.manager = null;
            if (null != this.mHandleMessage) {
                this.mHandleMessage.removeMessages(0);
            }
            if (null != mHandlerThread) {
                Looper looper = this.mHandlerThread.getLooper();
                if (looper != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        looper.quitSafely();
                    } else {
                        looper.quit();
                    }
                }
            }
            this.mHandlerThread = null;
            this.mIOnNetPingListener = null;
            if (null != mAddressIpList) {
                mAddressIpList.clear();
                this.mAddressIpList = null;
            }
            this.manager = null;
        }
    }

    /**
     * 监控网络诊断的跟踪信息
     */
    public interface IOnNetPingListener {
        void onDelay(long log);

        void onLiveDelay();

        void onError();

        void firstExecute();
    }


    /**
     * 开始诊断网络
     */
    private void startNetDiagnosis() {
        if (!TextUtils.isEmpty(this.mDomain)) {
            // 网络状态
            if (isNetworkConnected()) {
                // 域名解析
                parseDomain(this.mDomain);
                // TCP三次握手时间测试
                execUseJava();
            } else {
                if (null != mIOnNetPingListener) {
                    mIOnNetPingListener.onError();
                }
                YLog.e(TAG, "当前主机未联网,请检查网络！");
            }
        }
    }

    /**
     * 使用java执行connected
     */
    private boolean execUseJava() {
        boolean isConnected = false;
        if (mAddress != null && mAddressIpList != null) {
            int len = mAddress.length;
            if (len >=1) {
                isConnected = execIP(mAddress[0], mAddressIpList.get(0));
            }
        }
        if (null != mIOnNetPingListener && !isConnected) {
            mIOnNetPingListener.onError();
        }
        return false;
    }

    private static final int PORT = 80;
    private static final int CONN_TIMES = 3;
    // 设置每次连接的timeout时间
    private int TIME_OUT = 3000;
    private final long[] RttTimes = new long[CONN_TIMES];// 用于存储三次测试中每次的RTT值

    /**
     * 返回某个IP进行5次connect的最终结果
     */
    private boolean execIP(InetAddress inetAddress, String ip) {
        boolean isConnected = true;
        InetSocketAddress socketAddress;
        TIME_OUT = 3000;
        if (inetAddress != null && ip != null) {
            socketAddress = new InetSocketAddress(inetAddress, PORT);
            int flag = 0;
            for (int i = 0; i < CONN_TIMES; i++) {
                execSocket(socketAddress, i);
                if (RttTimes[i] == -1) {// 一旦发生timeOut,则尝试加长连接时间
                    TIME_OUT += 4000;
                    if (i > 0 && RttTimes[i - 1] == -1) {// 连续两次连接超时,停止后续测试
                        flag = -1;
                        break;
                    }
                } else if (RttTimes[i] == -2) {
                    if (i > 0 && RttTimes[i - 1] == -2) {// 连续两次出现IO异常,停止后续测试
                        flag = -2;
                        break;
                    }
                }
            }
            long time = 0;
            int count = 0;
            if (flag == -1) {
                isConnected = false;
            } else if (flag == -2) {
                isConnected = false;
            } else {
                for (int i = 0; i < CONN_TIMES; i++) {
                    if (RttTimes[i] > 0) {
                        time += RttTimes[i];
                        count++;
                    }
                }
                if (count > 0) {
                    if (mIOnNetPingListener != null) {
                        mIOnNetPingListener.onDelay(time / count);
                    }
                }
            }
        } else {
            isConnected = false;
        }
        return isConnected;
    }


    /**
     * 针对某个IP第index次connect
     */
    private void execSocket(InetSocketAddress socketAddress, int index) {
        long start;
        long end;
        Socket mSocket = new Socket();
        try {
            start = System.currentTimeMillis();
            mSocket.connect(socketAddress, TIME_OUT);
            end = System.currentTimeMillis();
            RttTimes[index] = end - start;
        } catch (SocketTimeoutException e) {
            RttTimes[index] = -1;// 作为TIMEOUT标识
            e.printStackTrace();
        } catch (IOException e) {
            RttTimes[index] = -2;// 作为IO异常标识
            e.printStackTrace();
        } finally {
            if (mSocket != null) {
                try {
                    mSocket.close();
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断网络是否连接
     */
    private Boolean isNetworkConnected() {
        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = null;
        try {
            networkinfo = manager.getActiveNetworkInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !(networkinfo == null || !networkinfo.isAvailable());
    }


    /**
     * 域名解析
     */
    private boolean parseDomain(String domain) {
        boolean flag = false;
        Map<String, Object> map = getDomainIp(domain);
        if (map == null) {
            return flag;
        }
        Object remoteObj = map.get("remoteInet");
        if (remoteObj != null) {
            mAddress = (InetAddress[]) remoteObj;
        } else {
            mAddress = null;
        }
        if (mAddress != null && mAddress.length > 0) {
            // 解析正确
            if (mAddressIpList == null) {
                return false;
            }
            mAddressIpList.add(mAddress[0].getHostAddress());
            flag = true;
        } else {
            // 解析不到，判断第一次解析耗时，如果大于10s进行第二次解析
            Object useTimeObj = map.get("useTime");
            if (useTimeObj != null) {
                String useTime = String.valueOf(useTimeObj);
                if (Integer.parseInt(useTime) > 10000) {
                    map = getDomainIp(domain);
                    Object remoteObjRetry = map.get("remoteInet");
                    if (remoteObjRetry != null) {
                        mAddress = (InetAddress[]) remoteObjRetry;
                    }
                    if (mAddress != null && mAddressIpList != null) {
                        mAddressIpList.add(mAddress[0].getHostAddress());
                        flag = true;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 解析IP
     */
    private Map<String, Object> getDomainIp(String domain) {
        Map<String, Object> map = new HashMap<>(2);
        long start = 0;
        long end;
        String time = null;
        InetAddress[] remoteInet = null;
        try {
            start = System.currentTimeMillis();
            remoteInet = InetAddress.getAllByName(domain);
            if (remoteInet != null) {
                end = System.currentTimeMillis();
                time = (end - start) + "";
            }
        } catch (UnknownHostException e) {
            end = System.currentTimeMillis();
            time = (end - start) + "";
            remoteInet = null;
            e.printStackTrace();
        } finally {
            map.put("remoteInet", remoteInet);
            map.put("useTime", time);
        }
        return map;
    }
}
