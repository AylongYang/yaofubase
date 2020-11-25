package com.yaofu.basesdk.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.content.ContextCompat;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * Auth:long3.yang
 * Date:2020/10/21
 **/
public class FileUtils {

    static final int BUFFER_SIZE = 2048;

    /**
     * * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * *
     *
     * @param context
     */
    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }


    /**
     * * 清除/data/data/com.xxx.xxx/files下的内容 * *
     *
     * @param context
     */
    public static void cleanFiles(Context context) {
        deleteFilesByDirectory(context.getFilesDir());
    }

    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     *
     * @param context
     */
    public static void cleanExternalCache(Context context) {
        if (existSDcard()) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    /**
     * * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * *
     * <p>
     * //     * @param directory
     */
    //    private static void deleteFilesByDirectory(File directory) {
    //        if (directory == null) {
    //            return;
    //        }
    //
    //        if (!directory.exists()) {
    //            return;
    //        }
    //        if (directory.isDirectory()) {
    //            for (File item : directory.listFiles()) {
    //                item.delete();
    //            }
    //        }else if(directory.isFile()){
    //            directory.delete();
    //        }
    //    }
    private static boolean deleteFilesByDirectory(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteFilesByDirectory(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir != null && dir.delete();
    }

    /**
     * 获取缓存大小
     */
    public static String getTotalCacheSize(Context context) {
        Context appContext = context.getApplicationContext();
        long cacheSize = 0;
        try {
            cacheSize = getFolderSize(appContext.getCacheDir());
        } catch (Exception e) {
            e.printStackTrace();
        }
        long fileSize = 0;
        try {
            cacheSize = getFolderSize(appContext.getFilesDir());
        } catch (Exception e) {
            e.printStackTrace();
        }

        long sdCardCacheSize = 0;
        if (existSDcard()) {
            try {
                sdCardCacheSize = getFolderSize(appContext.getExternalCacheDir());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        long totalSize = cacheSize + fileSize + sdCardCacheSize;
        return getFormatSize(totalSize);
    }

    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            if (fileList == null) {
                return size;
            }

            for (File childFile : fileList) {
                // 如果下面还有文件
                if (childFile.isDirectory()) {
                    size = size + getFolderSize(childFile);
                } else {
                    size = size + childFile.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        double megaByte = kiloByte / 1024;
        try {
            if (megaByte < 1) {
                BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
                return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
            }
            double gigaByte = megaByte / 1024;
            if (gigaByte < 1) {
                BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
                return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
            }
            double teraBytes = gigaByte / 1024;
            if (teraBytes < 1) {
                BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
                return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
            }
            //fix:#161559
            BigDecimal result4 = new BigDecimal(Double.toString(teraBytes));
            return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NaN";
    }

    public static boolean existSDcard() {
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        }
        return MEDIA_MOUNTED.equals(externalStorageState);
    }

    //定义缓存文件的名字，方便外部调用
    public static final String DOCS_CACHE_TXT = "docs_cache.txt";//缓存文件
    //缓存超时时间
    public static final int CACHE_SHORT_TIMEOUT = 1000 * 60 * 5; // 5 分钟


    /**
     * 设置缓存
     * content是要存储的内容，可以是任意格式的，不一定是字符串。
     */
    public static void setCache(String content, Context context, String cacheFileName, int mode) {
        FileOutputStream fos = null;
        try {
            //打开文件输出流，接收参数是文件名和模式
            fos = context.openFileOutput(cacheFileName, mode);
            fos.write(content.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //读取缓存，返回字符串（JSON）
    public static String getCache(Context context, String cacheFileName, String charset) {
        if (context == null) {
            return "";
        }
        FileInputStream fileInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        String result = null;
        try {
            fileInputStream = context.openFileInput(cacheFileName);
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] data = new byte[BUFFER_SIZE];
            int count = -1;
            while ((count = fileInputStream.read(data, 0, BUFFER_SIZE)) != -1) {
                byteArrayOutputStream.write(data, 0, count);
            }
            data = null;
            result = new String(byteArrayOutputStream.toByteArray(), charset);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String getCachePath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    private static final String FILE_EXTENSION_SEPARATOR = ".";

    private static final String FILE_ZM_YOUKE = "zm_youke";
    //本地化图片资源
    private static final String FILE_EVENT_IMAGE_LOCAL = "image_local";
    //本地化课件壳资源
    private static final String FILE_EVENT_WEB_LOCAL = "web_local";

    /**
     * read file
     *
     * @param charsetName The name of a supported {@link java.nio.charset.Charset
     *                    </code>charset<code>}
     * @return if file not exist, return null, else return content of file
     * @throws RuntimeException if an error occurs while operator BufferedReader
     */
    public static StringBuilder readFile(String filePath, String charsetName) {
        File file = new File(filePath);
        if (!file.isFile()) {
            return null;
        }
        StringBuilder fileContent = new StringBuilder("");
        BufferedReader reader = null;
        String line;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            while ((line = reader.readLine()) != null) {
                if (!TextUtils.isEmpty(fileContent.toString())) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileContent;
    }

    /**
     * write file
     *
     * @param append is append, if true, write to the end of file, else clear content of file and
     *               write into it
     * @return return false if content is empty, true otherwise
     * @throws RuntimeException if an error occurs while operator FileWriter
     */
    public static boolean writeFile(String filePath, String content, boolean append) {
        if (StringUtils.isEmpty(content)) {
            return false;
        }

        FileWriter fileWriter = null;
        try {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * write file
     *
     * @param append is append, if true, write to the end of file, else clear content of file and
     *               write into it
     * @return return false if contentList is empty, true otherwise
     * @throws RuntimeException if an error occurs while operator FileWriter
     */
    public static boolean writeFile(String filePath, List<String> contentList, boolean append) {

        FileWriter fileWriter = null;
        try {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, append);
            int i = 0;
            for (String line : contentList) {
                if (i++ > 0) {
                    fileWriter.write("\r\n");
                }
                fileWriter.write(line);
            }
            fileWriter.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * write file, the string will be written to the begin of the file
     */
    public static boolean writeFile(String filePath, String content) {
        return writeFile(filePath, content, false);
    }

    /**
     * write file, the string list will be written to the begin of the file
     */
    public static boolean writeFile(String filePath, List<String> contentList) {
        return writeFile(filePath, contentList, false);
    }

    /**
     * write file, the bytes will be written to the begin of the file
     *
     * @see {@link #writeFile(String, InputStream, boolean)}
     */
    public static boolean writeFile(String filePath, InputStream stream) {
        return writeFile(filePath, stream, false);
    }

    /**
     * write file
     *
     * @param stream the input stream
     * @param append if <code>true</code>, then bytes will be written to the end of the file rather
     *               than the beginning
     * @return return true
     * @throws RuntimeException if an error occurs while operator FileOutputStream
     */
    public static boolean writeFile(String filePath, InputStream stream, boolean append) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        return writeFile(new File(filePath), stream, append);
    }

    /**
     * write file, the bytes will be written to the begin of the file
     *
     * @see {@link #writeFile(File, InputStream, boolean)}
     */
    public static boolean writeFile(File file, InputStream stream) {
        return writeFile(file, stream, false);
    }

    /**
     * write file
     *
     * @param file   the file to be opened for writing.
     * @param stream the input stream
     * @param append if <code>true</code>, then bytes will be written to the end of the file rather
     *               than the beginning
     * @return return true
     * @throws RuntimeException if an error occurs while operator FileOutputStream
     */
    public static boolean writeFile(File file, InputStream stream, boolean append) {
        if (file == null) {
            return false;
        }
        OutputStream o = null;
        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, append);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (o != null) {
                try {
                    o.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * copy file
     */
    public static boolean copyFile(String sourceFilePath, String destFilePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(sourceFilePath);
            return writeFile(destFilePath, inputStream);
        } catch (Exception e) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * read file to string list, a element of list is a line
     *
     * @param charsetName The name of a supported {@link java.nio.charset.Charset
     *                    </code>charset<code>}
     * @return if file not exist, return null, else return content of file
     * @throws RuntimeException if an error occurs while operator BufferedReader
     */
    public static List<String> readFileToList(String filePath, String charsetName) {
        File file = new File(filePath);
        List<String> fileContent = new ArrayList<String>();
        if (file == null || !file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
            reader.close();
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * get file name from path, not include suffix
     * <p>
     * <pre>
     *      getFileNameWithoutExtension(null)               =   null
     *      getFileNameWithoutExtension("")                 =   ""
     *      getFileNameWithoutExtension("   ")              =   "   "
     *      getFileNameWithoutExtension("abc")              =   "abc"
     *      getFileNameWithoutExtension("a.mp3")            =   "a"
     *      getFileNameWithoutExtension("a.b.rmvb")         =   "a.b"
     *      getFileNameWithoutExtension("c:\\")              =   ""
     *      getFileNameWithoutExtension("c:\\a")             =   "a"
     *      getFileNameWithoutExtension("c:\\a.b")           =   "a"
     *      getFileNameWithoutExtension("c:a.txt\\a")        =   "a"
     *      getFileNameWithoutExtension("/home/admin")      =   "admin"
     *      getFileNameWithoutExtension("/home/admin/a.txt/b.mp3")  =   "b"
     * </pre>
     *
     * @return file name from path, not include suffix
     * @see
     */
    public static String getFileNameWithoutExtension(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int extensionPosition = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosition = filePath.lastIndexOf(File.separator);
        if (filePosition == -1) {
            return (extensionPosition == -1 ? filePath : filePath.substring(0, extensionPosition));
        }
        if (extensionPosition == -1) {
            return filePath.substring(filePosition + 1);
        }
        return (filePosition < extensionPosition
                ? filePath.substring(filePosition + 1, extensionPosition)
                : filePath.substring(filePosition + 1));
    }

    /**
     * get file name from path, include suffix
     * <p>
     * <pre>
     *      getFileName(null)               =   null
     *      getFileName("")                 =   ""
     *      getFileName("   ")              =   "   "
     *      getFileName("a.mp3")            =   "a.mp3"
     *      getFileName("a.b.rmvb")         =   "a.b.rmvb"
     *      getFileName("abc")              =   "abc"
     *      getFileName("c:\\")              =   ""
     *      getFileName("c:\\a")             =   "a"
     *      getFileName("c:\\a.b")           =   "a.b"
     *      getFileName("c:a.txt\\a")        =   "a"
     *      getFileName("/home/admin")      =   "admin"
     *      getFileName("/home/admin/a.txt/b.mp3")  =   "b.mp3"
     * </pre>
     *
     * @return file name from path, include suffix
     */
    public static String getFileName(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }
        int filePosition = filePath.lastIndexOf(File.separator);
        return (filePosition == -1) ? filePath : filePath.substring(filePosition + 1);
    }

    /**
     * get folder name from path
     * <p>
     * <pre>
     *      getFolderName(null)               =   null
     *      getFolderName("")                 =   ""
     *      getFolderName("   ")              =   ""
     *      getFolderName("a.mp3")            =   ""
     *      getFolderName("a.b.rmvb")         =   ""
     *      getFolderName("abc")              =   ""
     *      getFolderName("c:\\")              =   "c:"
     *      getFolderName("c:\\a")             =   "c:"
     *      getFolderName("c:\\a.b")           =   "c:"
     *      getFolderName("c:a.txt\\a")        =   "c:a.txt"
     *      getFolderName("c:a\\b\\c\\d.txt")    =   "c:a\\b\\c"
     *      getFolderName("/home/admin")      =   "/home"
     *      getFolderName("/home/admin/a.txt/b.mp3")  =   "/home/admin/a.txt"
     * </pre>
     */
    public static String getFolderName(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }
        int filePosition = filePath.lastIndexOf(File.separator);
        return (filePosition == -1) ? "" : filePath.substring(0, filePosition);
    }

    /**
     * get suffix of file from path
     * <p>
     * <pre>
     *      getFileExtension(null)               =   ""
     *      getFileExtension("")                 =   ""
     *      getFileExtension("   ")              =   "   "
     *      getFileExtension("a.mp3")            =   "mp3"
     *      getFileExtension("a.b.rmvb")         =   "rmvb"
     *      getFileExtension("abc")              =   ""
     *      getFileExtension("c:\\")              =   ""
     *      getFileExtension("c:\\a")             =   ""
     *      getFileExtension("c:\\a.b")           =   "b"
     *      getFileExtension("c:a.txt\\a")        =   ""
     *      getFileExtension("/home/admin")      =   ""
     *      getFileExtension("/home/admin/a.txt/b")  =   ""
     *      getFileExtension("/home/admin/a.txt/b.mp3")  =   "mp3"
     * </pre>
     */
    public static String getFileExtension(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return filePath;
        }
        int extensionPosition = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosition = filePath.lastIndexOf(File.separator);
        if (extensionPosition == -1) {
            return "";
        }
        return (filePosition >= extensionPosition)
                ? "" : filePath.substring(extensionPosition + 1);
    }


    /**
     * Creates the directory named by the trailing filename of this file, including the complete
     * directory path required to create this directory. <br>
     * <br>
     * <p>
     * <ul>
     * <strong>Attentions:</strong>
     * <li>makeDirs("C:\\Users\\Trinea") can only create users folder
     * <li>makeFolder("C:\\Users\\Trinea\\") can create Trinea folder
     * </ul>
     *
     * @return true if the necessary directories have been created or the target directory already
     * exists, false one of the directories can not be created.
     * <ul>
    * <li>if target directory already exists, return true
     * </ul>
     */
    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (StringUtils.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) || folder.mkdirs();
    }

    /**
     * @see #makeDirs(String)
     */
    public static boolean makeFolders(String filePath) {
        return makeDirs(filePath);
    }

    /**
     * Indicates if this file represents a file on the underlying file system.
     */
    public static boolean isFileExist(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    /**
     * Indicates if this file represents a directory on the underlying file system.
     */
    public static boolean isFolderExist(String directoryPath) {
        if (StringUtils.isBlank(directoryPath)) {
            return false;
        }
        File dire = new File(directoryPath);
        return (dire.exists() && dire.isDirectory());
    }

    /**
     * delete file or directory
     * <p>
     * <ul>
     * <li>if path is null or empty, return true
     * <li>if path not exist, return true
     * <li>if path exist, delete recursion. return true
     * <ul>
     */
    public static boolean deleteFile(String path) {
        if (StringUtils.isBlank(path)) {
            return true;
        }
        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        File[] childFileList = file.listFiles();
        if (childFileList != null) {
            for (File f : childFileList) {
                if (f == null) {
                    continue;
                }
                if (f.isFile()) {
                    f.delete();
                } else if (f.isDirectory()) {
                    deleteFile(f.getAbsolutePath());
                }
            }
        }
        return file.delete();
    }

    /**
     * get file size
     * <p>
     * <ul>
     * <li>if path is null or empty, return -1
     * <li>if path exist and it is a file, return file size, else return -1
     * <ul>
     *
     * @return returns the length of this file in bytes. returns -1 if the file does not exist.
     */
    public static long getFileSize(String path) {
        if (StringUtils.isBlank(path)) {
            return -1;
        }

        File file = new File(path);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }

    public static boolean WriteFileToSD(String content, String path) {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(path)) {

            return false;
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(path));
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(content + "\t\n");
            bw.close();
            osw.close();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public static File getPhotoTempPath() {
        String child = "/temp/" + System.currentTimeMillis() + ".jpg";
        //内置的SD卡 mnt/sdcard/ 即为SD卡根路径
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        if (externalStorageDirectory != null) {
            File file = new File(externalStorageDirectory, child);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            return file;
        }
        return null;
    }

    public static Uri getPhotoTempUri() {
        File file = getPhotoTempPath();
        if (file != null) {
            return Uri.fromFile(file);
        } else {
            return null;
        }
    }

    static String rootDic = "";

    public static String getFileExtension(String rootPath, String fileName) {
        File file = new File(rootPath);
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] list = file.listFiles();
                if (list == null) {
                    return rootDic;
                }
                for (File file1 : list) {
                    if (file1.isDirectory()) {
                        getFileExtension(file1.getPath(), fileName);
                    } else if (file1.isFile() && file1.getName().contains(fileName)) {
                        rootDic = getFileRootDic(file1.getPath());
                        return rootDic;
                    }
                }
            }
        }
        return rootDic;
    }

    public static String getFileRootDic(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(File.separator);
        if (extenPosi == -1) {
            return "";
        }
        return filePath.substring(0, extenPosi);
    }

    /**
     * ...zm_youke/image_local
     */
    private static String youkeImageLocalStorage(Context context) {
        String host = zmYouKeLocalPrivateHost(context);
        return addressImageLocal(host);
    }

    private static String addressImageLocal(String host) {
        // .../zm_youke/image_local 地址拼接
        return String.format("%1$s%2$s%3$s%4$s%5$s", host, File.separator,
                FILE_ZM_YOUKE, File.separator, FILE_EVENT_IMAGE_LOCAL);
    }


    private static void removeImageLocal(String path) {
        String youke = String.format("%1$s%2$s%3$s", path, File.separator, FILE_ZM_YOUKE);
        if (isFolderExist(youke)) {
            deleteFile(youke);
        }
    }

    //</editor-fold>


    //<editor-fold desc="web、课件本地化壳资源>

    /**
     * ...zm_youke/web_local
     */
    @WorkerThread
    public static String courseWareLocalPath(@NonNull Context context) {
        String host = zmYouKeLocalPrivateHost(context);
        return String.format("%1$s%2$s%3$s%4$s%5$s", host, File.separator,
                FILE_ZM_YOUKE, File.separator, FILE_EVENT_WEB_LOCAL);
    }

    //</editor-fold>


    /**
     * 获取存储host策略:尽可能私密性的
     */
    private static String zmYouKeLocalPrivateHost(Context context) {
        String localPath = null;
        boolean sdCardMounted = isSDCardMounted();
        //外部
        if (sdCardMounted) {
            localPath = externalDirPath(context);
            boolean permission = hasPermission(context);
            if (!TextUtils.isEmpty(localPath)) {
                //删除原有公用文件
                if (permission) {
                    removeImageLocal(externalStoragePath());
                }
            } else if (permission) {
                //公用
                localPath = externalStoragePath();
            }
        }
        //内部私密
        if (TextUtils.isEmpty(localPath)) {
            localPath = getFileDirPath(context);
        }
        //扩展卡
        if (TextUtils.isEmpty(localPath)) {
            localPath = getExtendedMemoryPath(context);
        }
        return localPath;
    }

    /**
     * 不需要权限
     */
    private static String externalDirPath(Context context) {
        File file = externalFilesDir(context);
        return file != null ? file.getAbsolutePath() : "";
    }

    /**
     * /storage/sdcard0/0/Android/data/xxx/files
     * 内置的SD卡 私有目录
     */
    private static File externalFilesDir(Context context) {
        return context == null ? null : context.getExternalFilesDir(null);
    }

    /**
     * 需要权限
     */
    private static String externalStoragePath() {
        File file = externalStorageFile();
        return file != null ? file.getAbsolutePath() : "";
    }

    /**
     * SD卡是指内置的SD卡，路径为：Environment.getExternalStorageDirectory()
     */
    private static boolean isSDCardMounted() {
        //fix:ArrayIndexOutOfBoundsException
        String externalStorageState = null;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Environment.MEDIA_MOUNTED.equals(externalStorageState);
    }

    /**
     * /storage/sdcard0/0
     * 内置的SD卡 公有目录
     */
    private static File externalStorageFile() {
        File externalStorageDirectory = null;
        try {
            externalStorageDirectory = Environment.getExternalStorageDirectory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return externalStorageDirectory;
    }

    /**
     * 存储权限 true--有 false--无
     */
    public static boolean hasPermission(Context context) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    /**
     * /data/user/0/com.xxx/files
     */
    private static String getFileDirPath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }


    /**
     * 扩展卡路径地址
     */
    private static String getExtendedMemoryPath(Context context) {
        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (removable) {
                    return path;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public enum ImagePathEnum {
        /**
         * 底部4图
         */
        MAIN_BOTTOM_NAV("首页底部导航图", "main_bottom_navigation", 103),
        WEL_AD_SCREEN("启动页广告图", "welcome_ad_screen", 107),
        CLASS_CHECK_IN("课堂中签到图", "class_check_in", 110),
        BG_HOME_TOP("首页顶部背景", "bg_home_top", 113),
        SEARCH_ICON("搜索icon", "search_icon", 114),
        SHOP_CART_ICON("购物车icon", "shop_cart_icon", 115),
        GRADE_SWITCH("年级切换", "grade_switch", 116),
        BG_HOME_BOTTOM("底部背景图", "bg_home_bottom", 117);

        private final String splicePath;
        private final int adPosition;

        ImagePathEnum(String name, String splicePath, int adPosition) {
            this.splicePath = splicePath;
            this.adPosition = adPosition;
        }

        public String getSplicePath() {
            return splicePath;
        }

        public int getAdPosition() {
            return adPosition;
        }
    }

    public static final String TAG = "ZIP";

    /**
     * 解压zip到指定的路径
     *
     * @param zipFileString ZIP的名称
     * @param outPathString 要解压缩路径
     * @throws Exception
     */
    public static boolean UnZipFolder(String zipFileString, String outPathString) {
        boolean isSuceess = false;
        ZipInputStream inZip = null;
        FileOutputStream outputStream = null;
        try {
            inZip = new ZipInputStream(new FileInputStream(zipFileString));
            ZipEntry zipEntry;
            String szName = "";
            while ((zipEntry = inZip.getNextEntry()) != null) {
                szName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    //获取部件的文件夹名
                    szName = szName.substring(0, szName.length() - 1);
                    File folder = new File(outPathString + File.separator + szName);
                    folder.mkdirs();
                } else {
                    String pathname = outPathString + File.separator + szName;
                    File file = new File(pathname);
                    if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    }
                    // 获取文件的输出流
                    outputStream = new FileOutputStream(file);
                    int len;
                    byte[] buffer = new byte[1024];
                    // 读取（字节）字节到缓冲区
                    while ((len = inZip.read(buffer)) != -1) {
                        // 从缓冲区（0）位置写入（字节）字节
                        outputStream.write(buffer, 0, len);
                        outputStream.flush();
                    }

                }
                isSuceess = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inZip != null) {
                try {
                    inZip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isSuceess;
    }

    /**
     * 解压zip到指定的路径
     *
     * @param zipFileString zip路径
     * @param outPathString 解压后的路径
     * @param szName        指定解压后的文件夹名
     * @throws Exception
     */
    public static void UnZipFolder(String zipFileString, String outPathString, String szName) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        while ((zipEntry = inZip.getNextEntry()) != null) {
            if (zipEntry.isDirectory()) {
                //获取部件的文件夹名
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {
                File file = new File(outPathString + File.separator + szName);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                // 获取文件的输出流
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // 读取（字节）字节到缓冲区
                try {
                    while ((len = inZip.read(buffer)) != -1) {
                        // 从缓冲区（0）位置写入（字节）字节
                        out.write(buffer, 0, len);
                        out.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        inZip.close();
    }

    /**
     * 压缩文件和文件夹
     *
     * @param srcFileString 要压缩的文件或文件夹
     * @param zipFileString 解压完成的Zip路径
     * @throws Exception
     */
    public static void ZipFolder(String srcFileString, String zipFileString) throws Exception {
        //创建ZIP
        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileString));
        //创建文件
        File file = new File(srcFileString);
        //压缩
        ZipFiles(file.getParent() + File.separator, file.getName(), outZip);
        //完成和关闭
        outZip.finish();
        outZip.close();
    }

    /**
     * 压缩文件
     *
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     * @throws Exception
     */
    private static void ZipFiles(String folderString, String fileString, ZipOutputStream zipOutputSteam) throws
            Exception {
        if (zipOutputSteam == null)
            return;
        File file = new File(folderString + fileString);
        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(fileString);
            FileInputStream inputStream = new FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[4096];
            try {
                while ((len = inputStream.read(buffer)) != -1) {
                    zipOutputSteam.write(buffer, 0, len);
                }
            } catch (IOException e) {
            }finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
            zipOutputSteam.closeEntry();
        } else {
            //文件夹
            String fileList[] = file.list();
            //没有子文件和压缩
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }
            //子文件和递归
            for (int i = 0; i < fileList.length; i++) {
                ZipFiles(folderString, fileString + File.separator + fileList[i], zipOutputSteam);
            }
        }
    }

    /**
     * 返回zip的文件输入流
     *
     * @param zipFileString zip的名称
     * @param fileString    ZIP的文件名
     * @return InputStream
     * @throws Exception
     */
    public static InputStream UpZip(String zipFileString, String fileString) throws Exception {
        ZipFile zipFile = new ZipFile(zipFileString);
        ZipEntry zipEntry = zipFile.getEntry(fileString);
        return zipFile.getInputStream(zipEntry);
    }

    /**
     * 返回ZIP中的文件列表（文件和文件夹）
     *
     * @param zipFileString  ZIP的名称
     * @param bContainFolder 是否包含文件夹
     * @param bContainFile   是否包含文件
     * @return
     * @throws Exception
     */
    public static List<File> GetFileList(String zipFileString, boolean bContainFolder, boolean bContainFile) throws
            Exception {
        List<File> fileList = new ArrayList<File>();
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";
        try {
            while ((zipEntry = inZip.getNextEntry()) != null) {
                szName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    // 获取部件的文件夹名
                    szName = szName.substring(0, szName.length() - 1);
                    File folder = new File(szName);
                    if (bContainFolder) {
                        fileList.add(folder);
                    }
                } else {
                    File file = new File(szName);
                    if (bContainFile) {
                        fileList.add(file);
                    }
                }
            }
        } catch (IOException e) {
        }finally {
            inZip.close();
        }
        return fileList;
    }

    /**
     * 将InputStream转换成String
     *
     * @param in InputStream
     * @return String
     * @throws Exception
     */
    public static String InputStreamTOString(InputStream in) throws Exception {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray(), "utf-8");
    }

    /**
     * 将InputStream转换成某种字符编码的String
     *
     * @param in
     * @param encoding
     * @return
     * @throws Exception
     */
    public static String InputStreamTOString(InputStream in, String encoding) throws Exception {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray(), "ISO-8859-1");
    }

    /**
     * 将String转换成InputStream
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static InputStream StringTOInputStream(String in) throws Exception {

        ByteArrayInputStream is = new ByteArrayInputStream(in.getBytes("ISO-8859-1"));
        return is;
    }

    /**
     * 将InputStream转换成byte数组
     *
     * @param in InputStream
     * @return byte[]
     * @throws IOException
     */
    public static byte[] InputStreamTOByte(InputStream in) throws IOException {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) outStream.write(data, 0, count);

        data = null;
        return outStream.toByteArray();
    }

    /**
     * 将byte数组转换成InputStream
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static InputStream byteTOInputStream(byte[] in) throws Exception {

        ByteArrayInputStream is = new ByteArrayInputStream(in);
        return is;
    }

    /**
     * 将byte数组转换成String
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static String byteTOString(byte[] in) throws Exception {

        InputStream is = byteTOInputStream(in);
        return InputStreamTOString(is);
    }

    public static File getParentFile(@NonNull Context context) {
        final File externalSaveDir = context.getExternalCacheDir();
        if (externalSaveDir == null) {
            return context.getCacheDir();
        } else {
            return externalSaveDir;
        }
    }


}
