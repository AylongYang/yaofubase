package com.yaofu.basesdk.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;


import com.yaofu.basesdk.log.YLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Auth:long3.yang
 * Date:2020/10/21
 **/
public class DeviceUtils {
    /**
     * 得到Android系统的版本，如7.0.0
     */
    public static String getSDKVersionName() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 得到手机的品牌，如Xiaomi，Meizu，HUAWEI...
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 得到品牌对应的设备型号，如MI2S，Mate10...
     */
    public static String getModel() {
        String model = Build.MODEL;
        if (model != null) {
            model = model.trim().replaceAll("\\s*", "");
        } else {
            model = "";
        }
        return model;
    }

    /**
     * 友盟测试机
     *
     * @deprecated
     */
    @SuppressLint("HardwareIds")
    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = null;
            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                device_id = tm.getDeviceId();
            }
            String mac = getMac(context);
            json.put("mac", mac);
            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }
            if (TextUtils.isEmpty(device_id)) {
                device_id = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
            json.put("device_id", device_id);
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("HardwareIds")
    public static String getAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static String getIMEI(Context context) {
        String imei = "";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //4.0以下 直接获取
            imei = getImeiOrMeid(context);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            //5。0，6。0系统
            Map imeiMaps = getImeiAndMeid(context);
            try {
                imei = getTransform(imeiMaps);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Map imeiMaps = getIMEIforO(context);
            try {
                imei = getTransform(imeiMaps);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return imei;
    }

    @SuppressLint("HardwareIds")
    private static String getImeiOrMeid(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            if (manager != null) {
                return manager.getDeviceId();
            }
        }
        return null;
    }

    @SuppressLint({"PrivateApi", "HardwareIds"})
    @TargetApi(Build.VERSION_CODES.M)
    private static Map getImeiAndMeid(Context context) {
        Map<String, String> map = new HashMap<>();
        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            try {
                Class<?> clazz = Class.forName("android.os.SystemProperties");
                Method method = clazz.getMethod("get", String.class, String.class);
                String gsm = (String) method.invoke(null, "ril.gsm.imei", "");
                String meid = (String) method.invoke(null, "ril.cdma.meid", "");
                map.put("meid", meid);
                if (!TextUtils.isEmpty(gsm)) {
                    //the value of gsm like:xxxxxx,xxxxxx
                    String[] imeiArray = gsm.split(",");
                    if (imeiArray.length > 0) {
                        map.put("imei1", imeiArray[0]);
                        if (imeiArray.length > 1) {
                            map.put("imei2", imeiArray[1]);
                        } else {
                            map.put("imei2", mTelephonyManager.getDeviceId(1));
                        }
                    } else {
                        map.put("imei1", mTelephonyManager.getDeviceId(0));
                        map.put("imei2", mTelephonyManager.getDeviceId(1));
                    }
                } else {
                    map.put("imei1", mTelephonyManager.getDeviceId(0));
                    map.put("imei2", mTelephonyManager.getDeviceId(1));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static Map getIMEIforO(Context context) {
        Map<String, String> map = new HashMap<>();
        TelephonyManager tm = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
        if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            try {
                String imei1 = tm.getImei(0);
                String imei2 = tm.getImei(1);
                if (TextUtils.isEmpty(imei1) && TextUtils.isEmpty(imei2)) {
                    //如果CDMA制式手机返回MEID
                    map.put("imei1", tm.getMeid());
                } else {
                    map.put("imei1", imei1);
                    map.put("imei2", imei2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    private static String getTransform(Map imeiMaps) {
        String imei = "";
        if (imeiMaps != null) {
            String imei1 = (String) imeiMaps.get("imei1");
            if (TextUtils.isEmpty(imei1)) {
                return imei;
            }
            String imei2 = (String) imeiMaps.get("imei2");
            if (imei2 != null) {
                if (imei1.trim().length() == 15 && imei2.trim().length() == 15) {
                    //如果两个位数都是15。说明都是有效IMEI。根据从大到小排列
                    long i1 = Long.parseLong(imei1.trim());
                    long i2 = Long.parseLong(imei2.trim());
                    if (i1 > i2) {
                        imei = imei2 + ";" + imei1;
                    } else {
                        imei = imei1 + ";" + imei2;
                    }
                } else {
                    if (imei1.trim().length() == 15) {
                        //如果只有imei1是有效的
                        imei = imei1;
                    } else if (imei2.trim().length() == 15) {
                        //如果只有imei2是有效的
                        imei = imei2;
                    } else {
                        //如果都无效那么都为meid。只取一个就可以
                        imei = imei1;
                    }
                }
            } else {
                imei = imei1;
            }
        }
        return imei;
    }

    public static String getSerialNumber() {
        return Build.SERIAL;
    }

    private static String getMac(Context context) {
        String mac = "";
        if (context == null) {
            return mac;
        }
        if (Build.VERSION.SDK_INT < 23) {
            mac = getMacBySystemInterface(context);
        } else {
            mac = getMacByJavaAPI();
            if (TextUtils.isEmpty(mac)) {
                mac = getMacBySystemInterface(context);
            }
        }
        return mac;

    }

    @TargetApi(9)
    private static String getMacByJavaAPI() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface netInterface = interfaces.nextElement();
                if ("wlan0".equals(netInterface.getName()) || "eth0".equals(netInterface.getName())) {
                    byte[] addr = netInterface.getHardwareAddress();
                    if (addr == null || addr.length == 0) {
                        return null;
                    }
                    StringBuilder buf = new StringBuilder();
                    for (byte b : addr) {
                        buf.append(String.format("%02X:", b));
                    }
                    if (buf.length() > 0) {
                        buf.deleteCharAt(buf.length() - 1);
                    }
                    return buf.toString().toLowerCase(Locale.getDefault());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("HardwareIds")
    private static String getMacBySystemInterface(Context context) {
        if (context == null) {
            return "";
        }
        try {
            WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (checkPermission(context, Manifest.permission.ACCESS_WIFI_STATE)) {
                WifiInfo info = wifi.getConnectionInfo();
                return info.getMacAddress();
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (context == null) {
            return result;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                if (rest == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (Throwable e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }


    public static String getMacAddress(Context context) {
        String mac = "02:00:00:00:00:00";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//6.0
            mac = getMacDefault(context);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mac = getMacFromFile();
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            mac = getMacFromHardware();
        }
        return mac;
    }

    @SuppressLint("HardwareIds")
    private static String getMacDefault(Context context) {
        String mac = "02:00:00:00:00:00";
        if (context == null) {
            return mac;
        }
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) {
            return mac;
        }
        WifiInfo wifiInfo = null;
        try {
            wifiInfo = wifi.getConnectionInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (wifiInfo == null) {
            return null;
        }
        mac = wifiInfo.getMacAddress();
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.ENGLISH);
        }
        return mac;
    }

    private static String getMacFromFile() {
        String wifiAddress = "02:00:00:00:00:00";
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        try {
            File file = new File("/sys/class/net/wlan0/address");
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            wifiAddress = bufferedReader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return wifiAddress;
    }

    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0"))
                    continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    private static final FileFilter CPU_FILTER = new FileFilter() {
        @Override
        public boolean accept(File arg0) {
            if (arg0 == null) {
                return false;
            }
            String path = arg0.getName();
            if (path.startsWith("cpu")) {
                try {
                    for (int i = 3, len = path.length(); i < len; i++) {
                        if (path.charAt(i) < '0' || path.charAt(i) > '9') {
                            return false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        }
    };

    public static String getCpuInfo() {
        int cores = 0;
        try {
            File file = new File("/sys/devices/system/cpu/");
            File[] files = file.listFiles(CPU_FILTER);
            if (files != null) {
                cores = files.length;
            }
        } catch (SecurityException | NullPointerException e) {
            YLog.exc(e);
        }
        String maxRate = "0";
        FileReader fr1 = null;
        BufferedReader bufferedReader = null;
        try {
            fr1 = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
            bufferedReader = new BufferedReader(fr1);
            String text1 = bufferedReader.readLine();
            int maxRate1 = Integer.parseInt(text1);
            double temp = maxRate1 / 1000.0;
            maxRate = String.format(Locale.CHINA, "%.1f", temp);
        } catch (Exception e) {
            YLog.exc(e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fr1 != null) {
                try {
                    fr1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "型号：" + Build.HARDWARE + " 架构：" + Build.CPU_ABI
                + " 核心数：" + cores + " 最高频率：" + maxRate + "MHz";
    }

    public static String getMemInfo(Context context) {
        String dir = "/proc/meminfo";
        BufferedReader br = null;
        FileReader fr = null;
        long initial_memory = 0;
        try {
            fr = new FileReader(dir);
            br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = TextUtils.isEmpty(memoryLine) ? "0" : memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            initial_memory = Integer.parseInt(subMemoryLine.replaceAll("\\D+", "")) * 1024L;
        } catch (Exception e) {
            YLog.exc(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fr != null) {
                try {
                    fr.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // Byte转换为KB或者MB，内存大小规格化
        String fileSize = "异常";
        try {
            fileSize = Formatter.formatFileSize(context, initial_memory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileSize;
    }

    private static float getProcessCpuRate() {

        float totalCpuTime1 = getTotalCpuTime();
        float processCpuTime1 = getAppCpuTime();
        try {
            Thread.sleep(360);  //sleep一段时间
        } catch (Exception e) {
        }

        float totalCpuTime2 = getTotalCpuTime();
        float processCpuTime2 = getAppCpuTime();

        float cpuRate = 100 * (processCpuTime2 - processCpuTime1) / (totalCpuTime2 - totalCpuTime1);//百分比

        return cpuRate;
    }

    // 获取系统总CPU使用时间
    public static long getTotalCpuTime() {
        String[] cpuInfos = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long totalCpu = Long.parseLong(cpuInfos[2])
                + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
                + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
                + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
        return totalCpu;
    }

    // 获取应用占用的CPU时间
    public static long getAppCpuTime() {
        String[] cpuInfos = null;
        try {
            int pid = android.os.Process.myPid();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + pid + "/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long appCpuTime = Long.parseLong(cpuInfos[13])
                + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
                + Long.parseLong(cpuInfos[16]);
        return appCpuTime;
    }

    // 获取应用占用的内存(单位为KB)
    public static String getAppMemory() {
        String info = null;
        try {
            int pid = android.os.Process.myPid();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + pid + "/status")), 1000);
            String load;
            while ((load = reader.readLine()) != null) {
                load = load.replace(" ", "");
                String[] Info = load.split("[: k K]");
                if (Info[0].equals("VmRSS")) {
                    info = Info[1];
                    break;
                }

            }
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return info;
    }

    public static int getFreeMem(Context context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Activity.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);
        // 单位Byte
        Double result = info.availMem * 1.0 / 1024 / 1024;
        return result.intValue();
    }

    public static float getFreeMemRate(Context context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Activity.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);
        return info.availMem * 1.0f / info.totalMem;
    }

    private static float getCpuDataForO() {
        int min = 20;
        int max = 40;
        Random random = new Random();
        return 1.0f * (random.nextInt(max) % (max - min + 1) + min);
    }


    public static float getCpuRate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return getCpuDataForO();
        } else {
            float cpuRate = (float) getProcessCpu() + 1; // 防止取小数后为0
            cpuRate = cpuRate > 100 ? 100 : cpuRate;
            return cpuRate;
        }
    }

    private static int pid = -1;

    public static double getProcessCpu() {
        RandomAccessFile reader = null;
        String load = null;
        try {
            reader = new RandomAccessFile("/proc/stat", "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (reader != null) {
                load = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        double totalCpuTime1 = 0.0;
        String[] toks = new String[0];
        int len = 0;
        if (load != null) {
            try {
                toks = load.split(" ");
                len = toks.length;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                for (int i = 2; i < len; i++) {
                    totalCpuTime1 += Double.parseDouble(toks[i]);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (pid < 0) {
            pid = android.os.Process.myPid();
        }
        RandomAccessFile reader2 = null;
        try {
            reader2 = new RandomAccessFile("/proc/" + pid + "/stat", "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        double processCpuTime1 = 0;
        String load2 = null;
        double utime;
        double stime;
        double cutime;
        double cstime;
        try {
            if (reader2 != null) {
                load2 = reader2.readLine();
                String[] toks2 = load2.split(" ");
                utime = Double.parseDouble(toks2[13]);
                stime = Double.parseDouble(toks2[14]);
                cutime = Double.parseDouble(toks2[15]);
                cstime = Double.parseDouble(toks2[16]);
                processCpuTime1 = utime + stime + cutime + cstime;
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(360);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (reader != null) {
                reader.seek(0);
                load = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        double totalCpuTime2 = 0.0;
        if (load != null) {
            try {
                toks = load.split(" ");
                len = toks.length;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                for (int i = 2; i < len; i++) {
                    totalCpuTime2 += Double.parseDouble(toks[i]);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        try {
            if (reader2 != null) {
                reader2.seek(0);
                load2 = reader2.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader2 != null) {
                    reader2.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        double processCpuTime2 = 0.0;
        if (load2 != null) {
            String[] toks3 = load2.split(" ");
            utime = Double.parseDouble(toks3[13]);
            stime = Double.parseDouble(toks3[14]);
            cutime = Double.parseDouble(toks3[15]);
            cstime = Double.parseDouble(toks3[16]);
            processCpuTime2 = utime + stime + cutime + cstime;
        }
        double totalCpu = (totalCpuTime2 - totalCpuTime1);
        if (totalCpu == 0) {
            return 0;
        }
        return (processCpuTime2 - processCpuTime1) * 100.00 / totalCpu;
    }

    public  enum PhoneManufacture{
        MEIZU("MeiZu"),
        HUAWEI("HuaWei"),
        XIAOMI("XiaoMi"),
        OPPO("Oppo"),
        VIVO("Vivo"),
        SAMSUNG("Samsung"),
        LENOVO("Lenovo");

        private String manufacture;
        private PhoneManufacture(String strName){
            manufacture = strName;
        }
        public String getManufacture(){
            return manufacture;
        }
    }

    public static String getManufactureName(){
        String brand = Build.BRAND;
        String manufacture = Build.MANUFACTURER;

        if( brand.toLowerCase().contains(DeviceUtils.PhoneManufacture.HUAWEI.getManufacture().toLowerCase())
                || manufacture.toLowerCase().contains(DeviceUtils.PhoneManufacture.HUAWEI.getManufacture().toLowerCase())){
            return DeviceUtils.PhoneManufacture.HUAWEI.getManufacture();
        }else if (brand.toLowerCase().contains(DeviceUtils.PhoneManufacture.MEIZU.getManufacture().toLowerCase())
                || manufacture.toLowerCase().contains(DeviceUtils.PhoneManufacture.MEIZU.getManufacture().toLowerCase())){
            return DeviceUtils.PhoneManufacture.MEIZU.getManufacture();
        }else if (brand.toLowerCase().contains(DeviceUtils.PhoneManufacture.XIAOMI.getManufacture().toLowerCase())
                || manufacture.toLowerCase().contains(DeviceUtils.PhoneManufacture.XIAOMI.getManufacture().toLowerCase())){
            return DeviceUtils.PhoneManufacture.XIAOMI.getManufacture();
        }else if (brand.toLowerCase().contains(DeviceUtils.PhoneManufacture.VIVO.getManufacture().toLowerCase())
                || manufacture.toLowerCase().contains(DeviceUtils.PhoneManufacture.VIVO.getManufacture().toLowerCase())){
            return DeviceUtils.PhoneManufacture.VIVO.getManufacture();
        }else if (brand.toLowerCase().contains(DeviceUtils.PhoneManufacture.OPPO.getManufacture().toLowerCase())
                || manufacture.toLowerCase().contains(DeviceUtils.PhoneManufacture.OPPO.getManufacture().toLowerCase())){
            return DeviceUtils.PhoneManufacture.OPPO.getManufacture();
        }else if (brand.toLowerCase().contains(DeviceUtils.PhoneManufacture.SAMSUNG.getManufacture().toLowerCase())
                || manufacture.toLowerCase().contains(DeviceUtils.PhoneManufacture.SAMSUNG.getManufacture().toLowerCase())){
            return DeviceUtils.PhoneManufacture.SAMSUNG.getManufacture();
        }else if (brand.toLowerCase().contains(DeviceUtils.PhoneManufacture.LENOVO.getManufacture().toLowerCase())
                || manufacture.toLowerCase().contains(DeviceUtils.PhoneManufacture.LENOVO.getManufacture().toLowerCase())){
            return DeviceUtils.PhoneManufacture.LENOVO.getManufacture();
        }/*else if (brand.toLowerCase().contains(PhoneManufacture.QH360.getManufacture().toLowerCase())
               || manufacture.toLowerCase().contains(PhoneManufacture.QH360.getManufacture().toLowerCase())){
           return PhoneManufacture.QH360.getManufacture();
       }else {
           return PhoneManufacture.OTHERS.getManufacture();
       }*/
        return "";
    }

    private static final String TAG = "Rom";

    public static final String ROM_MIUI = "MIUI";
    public static final String ROM_EMUI = "EMUI";
    public static final String ROM_FLYME = "FLYME";
    public static final String ROM_OPPO = "OPPO";
    public static final String ROM_SMARTISAN = "SMARTISAN";
    public static final String ROM_VIVO = "VIVO";
    public static final String ROM_QIKU = "QIKU";

    private static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";
    private static final String KEY_VERSION_EMUI = "ro.build.version.emui";
    private static final String KEY_VERSION_OPPO = "ro.build.version.opporom";
    private static final String KEY_VERSION_SMARTISAN = "ro.smartisan.version";
    private static final String KEY_VERSION_VIVO = "ro.vivo.os.version";

    private static String sName;
    private static String sVersion;

    //华为
    public static boolean isEmui() {
        return check(ROM_EMUI);
    }
    //小米
    public static boolean isMiui() {
        return check(ROM_MIUI);
    }
    //vivo
    public static boolean isVivo() {
        return check(ROM_VIVO);
    }
    //oppo
    public static boolean isOppo() {
        return check(ROM_OPPO);
    }
    //魅族
    public static boolean isFlyme() {
        return check(ROM_FLYME);
    }
    //360手机
    public static boolean is360() {
        return check(ROM_QIKU) || check("360");
    }

    public static boolean isSmartisan() {
        return check(ROM_SMARTISAN);
    }

    public static String getName() {
        if (sName == null) {
            check("");
        }
        return sName;
    }

    public static String getVersion() {
        if (sVersion == null) {
            check("");
        }
        return sVersion;
    }

    public static boolean check(String rom) {
        if (sName != null) {
            return sName.equals(rom);
        }

        if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_MIUI))) {
            sName = ROM_MIUI;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_EMUI))) {
            sName = ROM_EMUI;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_OPPO))) {
            sName = ROM_OPPO;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_VIVO))) {
            sName = ROM_VIVO;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_SMARTISAN))) {
            sName = ROM_SMARTISAN;
        } else {
            sVersion = Build.DISPLAY;
            if (sVersion.toUpperCase().contains(ROM_FLYME)) {
                sName = ROM_FLYME;
            } if (sVersion.toUpperCase().contains(ROM_OPPO)) {
                sName = ROM_OPPO;
            } else {
                sVersion = Build.UNKNOWN;
                sName = Build.MANUFACTURER.toUpperCase();
            }
        }
        return sName.equals(rom);
    }

    public static String getProp(String name) {
        String line = null;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + name);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }
}
