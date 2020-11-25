package com.yaofu.basesdk.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.yaofu.basesdk.log.Log;

import java.io.File;
import java.security.MessageDigest;
import java.util.List;

/**
 * PackageUtils
 * <p>
 * <ul>
 * <strong>Install package</strong>
 * <li>{@link PackageUtils#installNormal(Context, String)}
 * <li>{@link PackageUtils#installSilent(Context, String)}
 * <li>{@link PackageUtils#install(Context, String)}
 * </ul>
 * <p>
 * <ul>
 * <strong>Uninstall package</strong>
 * <li>{@link PackageUtils#uninstallNormal(Context, String)}
 * <li>{@link PackageUtils#uninstallSilent(Context, String)}
 * <li>{@link PackageUtils#uninstall(Context, String)}
 * </ul>
 * <p>
 * <ul>
 * <strong>Is system application</strong>
 * <li>{@link PackageUtils#isSystemApplication(Context)}
 * <li>{@link PackageUtils#isSystemApplication(Context, String)}
 * <li>{@link PackageUtils#isSystemApplication(PackageManager, String)}
 * </ul>
 * <p>
 * <ul>
 * <strong>Others</strong>
 * <li>{@link PackageUtils#getInstallLocation()} get system install location
 * <li>{@link PackageUtils#isTopActivity(Context, String)} whether the app whost package's name is
 * packageName is on the top of the stack
 * <li>{@link PackageUtils#startInstalledAppDetails(Context, String)} start InstalledAppDetails
 * Activity
 * </ul>
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-15
 */
public class PackageUtils {

    public static final String TAG = "PackageUtils";
    /**
     * App installation location settings values, same to {@link #}
     */
    public static final int APP_INSTALL_AUTO = 0;

    public static final int APP_INSTALL_INTERNAL = 1;
    public static final int APP_INSTALL_EXTERNAL = 2;
    /**
     * Installation return code<br>
     * install success.
     */
    public static final int INSTALL_SUCCEEDED = 1;
    /**
     * Installation return code<br>
     * the package is already installed.
     */
    public static final int INSTALL_FAILED_ALREADY_EXISTS = -1;
    /**
     * Installation return code<br>
     * the package archive file is invalid.
     */
    public static final int INSTALL_FAILED_INVALID_APK = -2;
    /**
     * Installation return code<br>
     * the URI passed in is invalid.
     */
    public static final int INSTALL_FAILED_INVALID_URI = -3;
    /**
     * Installation return code<br>
     * the package manager service found that the device didn't have enough storage space to install
     * the app.
     */
    public static final int INSTALL_FAILED_INSUFFICIENT_STORAGE = -4;
    /**
     * Installation return code<br>
     * a package is already installed with the same name.
     */
    public static final int INSTALL_FAILED_DUPLICATE_PACKAGE = -5;
    /**
     * Installation return code<br>
     * the requested shared user does not exist.
     */
    public static final int INSTALL_FAILED_NO_SHARED_USER = -6;
    /**
     * Installation return code<br>
     * a previously installed package of the same name has a different signature than the new package
     * (and the old package's data was not removed).
     */
    public static final int INSTALL_FAILED_UPDATE_INCOMPATIBLE = -7;
    /**
     * Installation return code<br>
     * the new package is requested a shared user which is already installed on the device and does
     * not have matching signature.
     */
    public static final int INSTALL_FAILED_SHARED_USER_INCOMPATIBLE = -8;
    /**
     * Installation return code<br>
     * the new package uses a shared library that is not available.
     */
    public static final int INSTALL_FAILED_MISSING_SHARED_LIBRARY = -9;
    /**
     * Installation return code<br>
     * the new package uses a shared library that is not available.
     */
    public static final int INSTALL_FAILED_REPLACE_COULDNT_DELETE = -10;
    /**
     * Installation return code<br>
     * the new package failed while optimizing and validating its dex files, either because there was
     * not enough storage or the validation failed.
     */
    public static final int INSTALL_FAILED_DEXOPT = -11;
    /**
     * Installation return code<br>
     * the new package failed because the current SDK version is older than that required by the
     * package.
     */
    public static final int INSTALL_FAILED_OLDER_SDK = -12;
    /**
     * Installation return code<br>
     * the new package failed because it contains a content provider with the same authority as a
     * provider already installed in the system.
     */
    public static final int INSTALL_FAILED_CONFLICTING_PROVIDER = -13;
    /**
     * Installation return code<br>
     * the new package failed because the current SDK version is newer than that required by the
     * package.
     */
    public static final int INSTALL_FAILED_NEWER_SDK = -14;
    /**
     * Installation return code<br>
     * the new package failed because it has specified that it is a test-only package and the caller
     * has not supplied the {@link #} flag.
     */
    public static final int INSTALL_FAILED_TEST_ONLY = -15;

    /*

     * Converts a byte array to hex string

     */
    /**
     * Installation return code<br>
     * the package being installed contains native code, but none that is compatible with the the
     * device's CPU_ABI.
     */
    public static final int INSTALL_FAILED_CPU_ABI_INCOMPATIBLE = -16;
    /**
     * Installation return code<br>
     * the new package uses a feature that is not available.
     */
    public static final int INSTALL_FAILED_MISSING_FEATURE = -17;
    /**
     * Installation return code<br>
     * a secure container mount point couldn't be accessed on external media.
     */
    public static final int INSTALL_FAILED_CONTAINER_ERROR = -18;
    /**
     * Installation return code<br>
     * the new package couldn't be installed in the specified install location.
     */
    public static final int INSTALL_FAILED_INVALID_INSTALL_LOCATION = -19;
    /**
     * Installation return code<br>
     * the new package couldn't be installed in the specified install location because the media is
     * not available.
     */
    public static final int INSTALL_FAILED_MEDIA_UNAVAILABLE = -20;
    /**
     * Installation return code<br>
     * the new package couldn't be installed because the verification timed out.
     */
    public static final int INSTALL_FAILED_VERIFICATION_TIMEOUT = -21;
    /**
     * Installation return code<br>
     * the new package couldn't be installed because the verification did not succeed.
     */
    public static final int INSTALL_FAILED_VERIFICATION_FAILURE = -22;
    /**
     * Installation return code<br>
     * the package changed from what the calling program expected.
     */
    public static final int INSTALL_FAILED_PACKAGE_CHANGED = -23;
    /**
     * Installation return code<br>
     * the new package is assigned a different UID than it previously held.
     */
    public static final int INSTALL_FAILED_UID_CHANGED = -24;
    /**
     * Installation return code<br>
     * if the parser was given a path that is not a file, or does not end with the expected '.apk'
     * extension.
     */
    public static final int INSTALL_PARSE_FAILED_NOT_APK = -100;
    /**
     * Installation return code<br>
     * if the parser was unable to retrieve the AndroidManifest.xml file.
     */
    public static final int INSTALL_PARSE_FAILED_BAD_MANIFEST = -101;
    /**
     * Installation return code<br>
     * if the parser encountered an unexpected exception.
     */
    public static final int INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION = -102;
    /**
     * Installation return code<br>
     * if the parser did not find any certificates in the .apk.
     */
    public static final int INSTALL_PARSE_FAILED_NO_CERTIFICATES = -103;
    /**
     * Installation return code<br>
     * if the parser found inconsistent certificates on the files in the .apk.
     */
    public static final int INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES = -104;
    /**
     * Installation return code<br>
     * if the parser encountered a CertificateEncodingException in one of the files in the .apk.
     */
    public static final int INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING = -105;
    /**
     * Installation return code<br>
     * if the parser encountered a bad or missing package name in the manifest.
     */
    public static final int INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME = -106;
    /**
     * Installation return code<br>
     * if the parser encountered a bad shared user id name in the manifest.
     */
    public static final int INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID = -107;
    /**
     * Installation return code<br>
     * if the parser encountered some structural problem in the manifest.
     */
    public static final int INSTALL_PARSE_FAILED_MANIFEST_MALFORMED = -108;
    /**
     * Installation return code<br>
     * if the parser did not find any actionable tags (instrumentation or application) in the
     * manifest.
     */
    public static final int INSTALL_PARSE_FAILED_MANIFEST_EMPTY = -109;
    /**
     * Installation return code<br>
     * if the system failed to install the package because of system issues.
     */
    public static final int INSTALL_FAILED_INTERNAL_ERROR = -110;
    /**
     * Installation return code<br>
     * other reason
     */
    public static final int INSTALL_FAILED_OTHER = -1000000;
    /**
     * Uninstall return code<br>
     * uninstall success.
     */
    public static final int DELETE_SUCCEEDED = 1;
    /**
     * Uninstall return code<br>
     * uninstall fail if the system failed to delete the package for an unspecified reason.
     */
    public static final int DELETE_FAILED_INTERNAL_ERROR = -1;
    /**
     * Uninstall return code<br>
     * uninstall fail if the system failed to delete the package because it is the active DevicePolicy
     * manager.
     */
    public static final int DELETE_FAILED_DEVICE_POLICY_MANAGER = -2;
    /**
     * Uninstall return code<br>
     * uninstall fail if pcakge name is invalid
     */
    public static final int DELETE_FAILED_INVALID_PACKAGE = -3;
    /**
     * Uninstall return code<br>
     * uninstall fail if permission denied
     */
    public static final int DELETE_FAILED_PERMISSION_DENIED = -4;

    /**
     * install according conditions
     * <p>
     * <ul>
     * <li>if system application or rooted, see {@link #installSilent(Context, String)}
     * <li>else see {@link #installNormal(Context, String)}
     * </ul>
     */
    public static final int install(Context context, String filePath) {
        if (PackageUtils.isSystemApplication(context) || ShellUtils.checkRootPermission()) {
            return installSilent(context, filePath);
        }
        return installNormal(context, filePath) ? INSTALL_SUCCEEDED : INSTALL_FAILED_INVALID_URI;
    }

    /**
     * install package normal by system intent
     *
     * @param filePath file path of package
     * @return whether apk exist
     */
    public static boolean installNormal(Context context, String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (file == null || !file.exists() || !file.isFile() || file.length() <= 0) {
            return false;
        }

        i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        return true;
    }

    /**
     * install package silent by root
     * <p>
     * <ul>
     * <strong>Attentions:</strong>
     * <li>Don't call this on the ui thread, it may costs some times.
     * <li>You should add <strong>android.permission.INSTALL_PACKAGES</strong> in manifest, so no
     * need to request root permission, if you are system app.
     * <li>Default pm install params is "-r".
     * </ul>
     *
     * @param filePath file path of package
     * @return {@link PackageUtils#INSTALL_SUCCEEDED} means install success, other means failed.
     * details see {@link PackageUtils} .INSTALL_FAILED_*. same to {@link
     * PackageManager}.INSTALL_*
     * @see #installSilent(Context, String, String)
     */
    public static int installSilent(Context context, String filePath) {
        return installSilent(context, filePath, " -r " + getInstallLocationParams());
    }

    /**
     * install package silent by root
     * <p>
     * <ul>
     * <strong>Attentions:</strong>
     * <li>Don't call this on the ui thread, it may costs some times.
     * <li>You should add <strong>android.permission.INSTALL_PACKAGES</strong> in manifest, so no
     * need to request root permission, if you are system app.
     * </ul>
     *
     * @param filePath file path of package
     * @param pmParams pm install params
     * @return {@link PackageUtils#INSTALL_SUCCEEDED} means install success, other means failed.
     * details see {@link PackageUtils} .INSTALL_FAILED_*. same to {@link
     * PackageManager}.INSTALL_*
     */
    public static int installSilent(Context context, String filePath, String pmParams) {
        if (filePath == null || filePath.length() == 0) {
            return INSTALL_FAILED_INVALID_URI;
        }

        File file = new File(filePath);
        if (file == null || file.length() <= 0 || !file.exists() || !file.isFile()) {
            return INSTALL_FAILED_INVALID_URI;
        }

        /**
         * if context is system app, don't need root permission, but should add <uses-permission
         * android:name="android.permission.INSTALL_PACKAGES" /> in mainfest
         */
        StringBuilder command =
                new StringBuilder()
                        .append("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install ")
                        .append(pmParams == null ? "" : pmParams)
                        .append(" ")
                        .append(filePath.replace(" ", "\\ "));
        ShellUtils.CommandResult commandResult =
                ShellUtils.execCommand(command.toString(), !isSystemApplication(context), true);
        if (commandResult.successMsg != null
                && (commandResult.successMsg.contains("Success")
                || commandResult.successMsg.contains("success"))) {
            return INSTALL_SUCCEEDED;
        }

        if (commandResult.errorMsg == null) {
            return INSTALL_FAILED_OTHER;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_ALREADY_EXISTS")) {
            return INSTALL_FAILED_ALREADY_EXISTS;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_INVALID_APK")) {
            return INSTALL_FAILED_INVALID_APK;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_INVALID_URI")) {
            return INSTALL_FAILED_INVALID_URI;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_INSUFFICIENT_STORAGE")) {
            return INSTALL_FAILED_INSUFFICIENT_STORAGE;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_DUPLICATE_PACKAGE")) {
            return INSTALL_FAILED_DUPLICATE_PACKAGE;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_NO_SHARED_USER")) {
            return INSTALL_FAILED_NO_SHARED_USER;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_UPDATE_INCOMPATIBLE")) {
            return INSTALL_FAILED_UPDATE_INCOMPATIBLE;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_SHARED_USER_INCOMPATIBLE")) {
            return INSTALL_FAILED_SHARED_USER_INCOMPATIBLE;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_MISSING_SHARED_LIBRARY")) {
            return INSTALL_FAILED_MISSING_SHARED_LIBRARY;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_REPLACE_COULDNT_DELETE")) {
            return INSTALL_FAILED_REPLACE_COULDNT_DELETE;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_DEXOPT")) {
            return INSTALL_FAILED_DEXOPT;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_OLDER_SDK")) {
            return INSTALL_FAILED_OLDER_SDK;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_CONFLICTING_PROVIDER")) {
            return INSTALL_FAILED_CONFLICTING_PROVIDER;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_NEWER_SDK")) {
            return INSTALL_FAILED_NEWER_SDK;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_TEST_ONLY")) {
            return INSTALL_FAILED_TEST_ONLY;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_CPU_ABI_INCOMPATIBLE")) {
            return INSTALL_FAILED_CPU_ABI_INCOMPATIBLE;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_MISSING_FEATURE")) {
            return INSTALL_FAILED_MISSING_FEATURE;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_CONTAINER_ERROR")) {
            return INSTALL_FAILED_CONTAINER_ERROR;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_INVALID_INSTALL_LOCATION")) {
            return INSTALL_FAILED_INVALID_INSTALL_LOCATION;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_MEDIA_UNAVAILABLE")) {
            return INSTALL_FAILED_MEDIA_UNAVAILABLE;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_VERIFICATION_TIMEOUT")) {
            return INSTALL_FAILED_VERIFICATION_TIMEOUT;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_VERIFICATION_FAILURE")) {
            return INSTALL_FAILED_VERIFICATION_FAILURE;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_PACKAGE_CHANGED")) {
            return INSTALL_FAILED_PACKAGE_CHANGED;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_UID_CHANGED")) {
            return INSTALL_FAILED_UID_CHANGED;
        }
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_NOT_APK")) {
            return INSTALL_PARSE_FAILED_NOT_APK;
        }
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_BAD_MANIFEST")) {
            return INSTALL_PARSE_FAILED_BAD_MANIFEST;
        }
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION")) {
            return INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION;
        }
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_NO_CERTIFICATES")) {
            return INSTALL_PARSE_FAILED_NO_CERTIFICATES;
        }
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES")) {
            return INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES;
        }
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING")) {
            return INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING;
        }
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME")) {
            return INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME;
        }
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID")) {
            return INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID;
        }
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_MANIFEST_MALFORMED")) {
            return INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
        }
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_MANIFEST_EMPTY")) {
            return INSTALL_PARSE_FAILED_MANIFEST_EMPTY;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_INTERNAL_ERROR")) {
            return INSTALL_FAILED_INTERNAL_ERROR;
        }
        return INSTALL_FAILED_OTHER;
    }

    /**
     * uninstall according conditions
     * <p>
     * <ul>
     * <li>if system application or rooted, see {@link #uninstallSilent(Context, String)}
     * <li>else see {@link #uninstallNormal(Context, String)}
     * </ul>
     *
     * @param packageName package name of app
     */
    public static final int uninstall(Context context, String packageName) {
        if (PackageUtils.isSystemApplication(context) || ShellUtils.checkRootPermission()) {
            return uninstallSilent(context, packageName);
        }
        return uninstallNormal(context, packageName) ? DELETE_SUCCEEDED : DELETE_FAILED_INVALID_PACKAGE;
    }

    /**
     * uninstall package normal by system intent
     *
     * @param packageName package name of app
     * @return whether package name is empty
     */
    public static boolean uninstallNormal(Context context, String packageName) {
        if (packageName == null || packageName.length() == 0) {
            return false;
        }

        Intent i =
                new Intent(
                        Intent.ACTION_DELETE,
                        Uri.parse(new StringBuilder(32).append("package:").append(packageName).toString()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        return true;
    }

    /**
     * uninstall package and clear data of app silent by root
     *
     * @param packageName package name of app
     * @see #uninstallSilent(Context, String, boolean)
     */
    public static int uninstallSilent(Context context, String packageName) {
        return uninstallSilent(context, packageName, true);
    }

    /**
     * uninstall package silent by root
     * <p>
     * <ul>
     * <strong>Attentions:</strong>
     * <li>Don't call this on the ui thread, it may costs some times.
     * <li>You should add <strong>android.permission.DELETE_PACKAGES</strong> in manifest, so no
     * need to request root permission, if you are system app.
     * </ul>
     *
     * @param context     file path of package
     * @param packageName package name of app
     * @param isKeepData  whether keep the data and cache directories around after package removal
     * @return <ul>
     * <li>{@link #DELETE_SUCCEEDED} means uninstall success
     * <li>{@link #DELETE_FAILED_INTERNAL_ERROR} means internal error
     * <li>{@link #DELETE_FAILED_INVALID_PACKAGE} means package name error
     * <li>{@link #DELETE_FAILED_PERMISSION_DENIED} means permission denied
     */
    public static int uninstallSilent(Context context, String packageName, boolean isKeepData) {
        if (packageName == null || packageName.length() == 0) {
            return DELETE_FAILED_INVALID_PACKAGE;
        }

        /**
         * if context is system app, don't need root permission, but should add <uses-permission
         * android:name="android.permission.DELETE_PACKAGES" /> in mainfest
         */
        StringBuilder command =
                new StringBuilder()
                        .append("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm uninstall")
                        .append(isKeepData ? " -k " : " ")
                        .append(packageName.replace(" ", "\\ "));
        ShellUtils.CommandResult commandResult =
                ShellUtils.execCommand(command.toString(), !isSystemApplication(context), true);
        if (commandResult.successMsg != null
                && (commandResult.successMsg.contains("Success")
                || commandResult.successMsg.contains("success"))) {
            return DELETE_SUCCEEDED;
        }
        Log.e(
                TAG,
                new StringBuilder()
                        .append("uninstallSilent successMsg:")
                        .append(commandResult.successMsg)
                        .append(", ErrorMsg:")
                        .append(commandResult.errorMsg)
                        .toString());
        if (commandResult.errorMsg == null) {
            return DELETE_FAILED_INTERNAL_ERROR;
        }
        if (commandResult.errorMsg.contains("Permission denied")) {
            return DELETE_FAILED_PERMISSION_DENIED;
        }
        return DELETE_FAILED_INTERNAL_ERROR;
    }

    /**
     * whether context is system application
     */
    public static boolean isSystemApplication(Context context) {
        if (context == null) {
            return false;
        }
        return isSystemApplication(context, context.getPackageName());
    }

    /**
     * whether packageName is system application
     */
    public static boolean isSystemApplication(Context context, String packageName) {
        if (context == null) {
            return false;
        }

        return isSystemApplication(context.getPackageManager(), packageName);
    }

    /**
     * whether packageName is system application
     *
     * @return <ul>
     * <li>if packageManager is null, return false
     * <li>if package name is null or is empty, return false
     * <li>if package name not exit, return false
     * <li>if package name exit, but not system app, return false
     * <li>else return true
     * </ul>
     */
    public static boolean isSystemApplication(PackageManager packageManager, String packageName) {
        if (packageManager == null || packageName == null || packageName.length() == 0) {
            return false;
        }
        try {
            ApplicationInfo app = packageManager.getApplicationInfo(packageName, 0);
            return (app != null && (app.flags & ApplicationInfo.FLAG_SYSTEM) > 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * whether the app whost package's name is packageName is on the top of the stack
     * <p>
     * <ul>
     * <strong>Attentions:</strong>
     * <li>You should add <strong>android.permission.GET_TASKS</strong> in manifest
     * </ul>
     *
     * @return if params error or task stack is null, return null, otherwise retun whether the app is
     * on the top of stack
     */
    public static boolean isTopActivity(Context context, String packageName) {
        if (context == null || StringUtils.isEmpty(packageName)) {
            return false;
        }

        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(100);

        try {
            return packageName.equals(tasksInfo.get(0).topActivity.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static int cacheVersionCode = -1;

    /**
     * get app version code
     */
    public static int getAppVersionCode(Context context) {
        if (cacheVersionCode > 0) {
            return cacheVersionCode;
        }
        if (context != null) {
            PackageManager pm = context.getPackageManager();
            if (pm != null) {
                PackageInfo pi;
                try {
                    pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
                    if (pi != null) {
                        cacheVersionCode = pi.versionCode;
                        return cacheVersionCode;
                    }
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    /**
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值 ， 或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo =
                        packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultData;
    }

    /**
     * get apk MD5
     */
    public static String getMD5(Context context) {
        String md5 = null;
        if (null != context) {

            try {

                PackageInfo pi =
                        context
                                .getPackageManager()
                                .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

                Signature signatures = pi.signatures[0];

                MessageDigest md = MessageDigest.getInstance("MD5");

                md.update(signatures.toByteArray());

                byte[] digest = md.digest();

                md5 = toHexString(digest);
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return md5;
    }

    private static void byte2hex(byte b, StringBuffer buf) {

        char[] hexChars = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };

        int high = ((b & 0xf0) >> 4);

        int low = (b & 0x0f);

        buf.append(hexChars[high]);

        buf.append(hexChars[low]);
    }

    private static String toHexString(byte[] block) {

        StringBuffer buf = new StringBuffer();

        int len = block.length;

        for (int i = 0; i < len; i++) {

            byte2hex(block[i], buf);

            if (i < len - 1) {

                buf.append(":");
            }
        }

        return buf.toString();
    }

    private static String cacheVersionName;

    /**
     * @param @param  context
     * @param @return 设定文件
     * @return String 返回类型 @Title: getAppVersionName @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public static String getAppVersionName(Context context) {
        if (!TextUtils.isEmpty(cacheVersionName)) {
            return cacheVersionName;
        }
        String versionName = "";
        if (context != null) {
            PackageManager pm = context.getPackageManager();
            if (pm != null) {
                PackageInfo pi;
                try {
                    pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
                    if (pi != null) {
                        versionName = pi.versionName;
                    }
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        cacheVersionName = versionName;
        return versionName;
    }

    /**
     * get system install location<br>
     * can be set by System Menu Setting->Storage->Prefered install location
     */
    public static int getInstallLocation() {
        ShellUtils.CommandResult commandResult =
                ShellUtils.execCommand(
                        "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm get-install-location", false, true);
        if (commandResult.result == 0
                && commandResult.successMsg != null
                && commandResult.successMsg.length() > 0) {
            try {
                int location = Integer.parseInt(commandResult.successMsg.substring(0, 1));
                switch (location) {
                    case APP_INSTALL_INTERNAL:
                        return APP_INSTALL_INTERNAL;
                    case APP_INSTALL_EXTERNAL:
                        return APP_INSTALL_EXTERNAL;
                    default:
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Log.e(TAG, "pm get-install-location error");
            }
        }
        return APP_INSTALL_AUTO;
    }

    /**
     * get params for pm install location
     */
    private static String getInstallLocationParams() {
        int location = getInstallLocation();
        switch (location) {
            case APP_INSTALL_INTERNAL:
                return "-f";
            case APP_INSTALL_EXTERNAL:
                return "-s";
            default:
        }
        return "";
    }

    /**
     * start InstalledAppDetails Activity
     */
    public static void startInstalledAppDetails(Context context, String packageName) {
        Intent intent = new Intent();
        int sdkVersion = Build.VERSION.SDK_INT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", packageName, null));
        } else {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra(
                    (sdkVersion == Build.VERSION_CODES.FROYO
                            ? "pkg"
                            : "com.android.settings.ApplicationPkgName"),
                    packageName);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 必更新
     */
    public static Boolean shouldUpdate(Context context, String cltVersion) {
        String old = getVersionName(context);
        if (TextUtils.isEmpty(cltVersion)) {
            return false;
        }
        String[] olds = old.split("\\.");
        String[] cver = cltVersion.split("\\.");
        for (int i = 0; i < cver.length; i++) {
            if (i > olds.length - 1) {
                return true;
            }
            if (Integer.valueOf(cver[i]) > Integer.valueOf(olds[i])) {
                return true;
            }
        }
        return false;
    }

    public static int getVersionCode(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            if (info == null) {
                return 0;
            }
            return info.versionCode;
        } catch (NameNotFoundException e) {
            return 0;
        }
    }

    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            if (info == null) {
                return "1.0";
            }
            return info.versionName;
        } catch (NameNotFoundException e) {
            return "1.0";
        }
    }

    /**
     * 检测是否安装微信
     *
     * @param context
     * @return
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }


    public static String getApkFilePath(Context context, String downLoadUrl) {
        if (context == null) {
            return "";
        }
        File externalFile = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            externalFile = context.getExternalFilesDir(null);
        }
        if (externalFile == null) {
            externalFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        }
        if (externalFile == null) {
            externalFile = context.getFilesDir();
        }
        String filePath = null;
        if (externalFile != null) {
            filePath = externalFile.getAbsolutePath();
        }
        String fileName;
        if (downLoadUrl.endsWith(".apk")) {
            int index = downLoadUrl.lastIndexOf("/");
            if (index != -1) {
                fileName = downLoadUrl.substring(index);
            } else {
                fileName = context.getPackageName() + ".apk";
            }
        } else {
            fileName = context.getPackageName() + ".apk";
        }

        File file = new File(filePath, fileName);
        return file.getAbsolutePath();
    }

    public static Intent openApkFile(Context context, File outputFile) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider.appupdatefileprovider", outputFile);
        } else {
            uri = Uri.fromFile(outputFile);
        }

        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }
}
