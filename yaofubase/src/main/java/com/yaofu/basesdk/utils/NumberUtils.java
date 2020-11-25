package com.yaofu.basesdk.utils;

import androidx.annotation.NonNull;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Random;

/**
 * Auth:long3.yang
 * Date:2020/10/21
 **/
public class NumberUtils {
    /**
     * compare two object
     *
     * @param actual
     * @param expected
     * @return
     *     <ul>
     *       <li>if both are null, return true
     *       <li>return actual.{@link Object#equals(Object)}
     *     </ul>
     */
    public static boolean isEquals(Object actual, Object expected) {
        return actual == expected || (actual == null ? expected == null : actual.equals(expected));
    }

    /**
     * convert long array to Long array
     *
     * @param source
     * @return
     */
    public static Long[] transformLongArray(long[] source) {
        Long[] destin = new Long[source.length];
        for (int i = 0; i < source.length; i++) {
            destin[i] = source[i];
        }
        return destin;
    }

    /**
     * convert Long array to long array
     *
     * @param source
     * @return
     */
    public static long[] transformLongArray(Long[] source) {
        long[] destin = new long[source.length];
        for (int i = 0; i < source.length; i++) {
            destin[i] = source[i];
        }
        return destin;
    }

    /**
     * convert int array to Integer array
     *
     * @param source
     * @return
     */
    public static Integer[] transformIntArray(int[] source) {
        Integer[] destin = new Integer[source.length];
        for (int i = 0; i < source.length; i++) {
            destin[i] = source[i];
        }
        return destin;
    }

    /**
     * convert Integer array to int array
     *
     * @param source
     * @return
     */
    public static int[] transformIntArray(Integer[] source) {
        int[] destin = new int[source.length];
        for (int i = 0; i < source.length; i++) {
            destin[i] = source[i];
        }
        return destin;
    }

    /**
     * compare two object
     *
     * <ul>
     *   <strong>About result</strong>
     *   <li>if v1 > v2, return 1
     *   <li>if v1 = v2, return 0
     *   <li>if v1 < v2, return -1
     * </ul>
     *
     * <ul>
     *   <strong>About rule</strong>
     *   <li>if v1 is null, v2 is null, then return 0
     *   <li>if v1 is null, v2 is not null, then return -1
     *   <li>if v1 is not null, v2 is null, then return 1
     *   <li>return v1.{@link Comparable#compareTo(Object)}
     * </ul>
     *
     * @param v1
     * @param v2
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <V> int compare(V v1, V v2) {
        return v1 == null ? (v2 == null ? 0 : -1) : (v2 == null ? 1 : ((Comparable) v1).compareTo(v2));
    }

    public static final String NUMBERS_AND_LETTERS =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMBERS = "0123456789";
    public static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String CAPITAL_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWER_CASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";

    /**
     * get a fixed-length random string, its a mixture of uppercase, lowercase letters and numbers
     *
     */
    public static String getRandomNumbersAndLetters(int length) {
        return getRandom(NUMBERS_AND_LETTERS, length);
    }

    /**
     * get a fixed-length random string, its a mixture of numbers
     *
     */
    public static String getRandomNumbers(int length) {
        return getRandom(NUMBERS, length);
    }

    /**
     * get a fixed-length random string, its a mixture of uppercase and lowercase letters
     *
     */
    public static String getRandomLetters(int length) {
        return getRandom(LETTERS, length);
    }

    /**
     * get a fixed-length random string, its a mixture of uppercase letters
     *
     */
    public static String getRandomCapitalLetters(int length) {
        return getRandom(CAPITAL_LETTERS, length);
    }

    /**
     * get a fixed-length random string, its a mixture of lowercase letters
     *
     */
    public static String getRandomLowerCaseLetters(int length) {
        return getRandom(LOWER_CASE_LETTERS, length);
    }

    /**
     * get a fixed-length random string, its a mixture of chars in source
     *
     * @return <ul> <li>if source is null or empty, return null <li>else see {@link
     */
    public static String getRandom(String source, int length) {
        return StringUtils.isEmpty(source) ? null : getRandom(source.toCharArray(), length);
    }

    /**
     * get a fixed-length random string, its a mixture of chars in sourceChar
     *
     * @return <ul> <li>if sourceChar is null or empty, return null <li>if length less than 0,
     * return null </ul>
     */
    public static String getRandom(char[] sourceChar, int length) {
        if (sourceChar == null || sourceChar.length == 0 || length < 0) {
            return null;
        }

        StringBuilder str = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            str.append(sourceChar[random.nextInt(sourceChar.length)]);
        }
        return str.toString();
    }

    /**
     * get random int between 0 and max
     *
     * @return <ul> <li>if max <= 0, return 0 <li>else return random int between 0 and max </ul>
     */
    public static int getRandom(int max) {
        return getRandom(0, max);
    }

    /**
     * get random int between min and max
     *
     * @return <ul> <li>if min > max, return 0 <li>if min == max, return min <li>else return random
     * int between min and max </ul>
     */
    public static int getRandom(int min, int max) {
        if (min > max) {
            return 0;
        }
        if (min == max) {
            return min;
        }
        return min + new Random().nextInt(max - min);
    }

    /**
     * Shuffling algorithm, Randomly permutes the specified array using a default source of
     * randomness
     */
    public static boolean shuffle(Object[] objArray) {
        if (objArray == null) {
            return false;
        }

        return shuffle(objArray, getRandom(objArray.length));
    }

    /**
     * Shuffling algorithm, Randomly permutes the specified array
     */
    public static boolean shuffle(Object[] objArray, int shuffleCount) {
        int length;
        if (objArray == null || shuffleCount < 0 || (length = objArray.length) < shuffleCount) {
            return false;
        }

        for (int i = 1; i <= shuffleCount; i++) {
            int random = getRandom(length - i);
            Object temp = objArray[length - i];
            objArray[length - i] = objArray[random];
            objArray[random] = temp;
        }
        return true;
    }

    /**
     * Shuffling algorithm, Randomly permutes the specified int array using a default source of
     * randomness
     */
    public static int[] shuffle(int[] intArray) {
        if (intArray == null) {
            return null;
        }

        return shuffle(intArray, getRandom(intArray.length));
    }

    /**
     * Shuffling algorithm, Randomly permutes the specified int array
     */
    public static int[] shuffle(int[] intArray, int shuffleCount) {
        int length;
        if (intArray == null || shuffleCount < 0 || (length = intArray.length) < shuffleCount) {
            return null;
        }

        int[] out = new int[shuffleCount];
        for (int i = 1; i <= shuffleCount; i++) {
            int random = getRandom(length - i);
            out[i - 1] = intArray[random];
            int temp = intArray[length - i];
            intArray[length - i] = intArray[random];
            intArray[random] = temp;
        }
        return out;
    }

    static String[] units = {"", "十", "百", "千", "万", "十万", "百万", "千万", "亿",
            "十亿", "百亿", "千亿", "万亿"};
    static char[] numArray = {'零', '一', '二', '三', '四', '五', '六', '七', '八', '九'};

    /**
     * 将阿拉伯数字变为中文
     *
     * @param num
     * @return
     */
    public static String formatInteger(int num) {
        char[] val = String.valueOf(num).toCharArray();
        int len = val.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            String m = val[i] + "";
            int n = Integer.valueOf(m);
            boolean isZero = n == 0;
            String unit = units[(len - 1) - i];
            if (isZero) {
                if ('0' == val[i - 1]) {
                    //当前val[i]的下一个值val[i-1]为0则不输出零
                    continue;
                } else {
                    //只有当当前val[i]的下一个值val[i-1]不为0才输出零
                    sb.append(numArray[n]);
                }
            } else {
                sb.append(numArray[n]);
                sb.append(unit);
            }
        }
        if (num > 10 && num < 20) {
            if (sb.toString().startsWith("一")) {
                return sb.toString().substring(1, sb.length());
            }
        } else if (num % 10 == 0) {
            String s = null;
            if (sb.toString().endsWith("零")) {
                s = sb.toString().substring(0, sb.length() - 1);
            }
            if (s.startsWith("一") && num == 10) {
                s = s.substring(1, s.length());
            }
            return s;
        }
        return sb.toString();
    }

    /**
     * 是否是连续数字
     *
     * @param numOrStr
     * @return
     */
    public static boolean isOrderNumeric(String numOrStr) {
        boolean flag = true;
        for (int i = 0; i < numOrStr.length(); i++) {
            if (i > 0) {// 判断如123456
                int num = Integer.parseInt(numOrStr.charAt(i) + "");
                int num_ = Integer.parseInt(numOrStr.charAt(i - 1) + "") + 1;
                if (num != num_) {
                    flag = false;
                    break;
                }
            }
        }
        if (!flag) {
            for (int i = 0; i < numOrStr.length(); i++) {
                if (i > 0) {// 判断如654321
                    int num = Integer.parseInt(numOrStr.charAt(i) + "");
                    int num_ = Integer.parseInt(numOrStr.charAt(i - 1) + "") - 1;
                    if (num != num_) {
                        flag = false;
                        break;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 判断数字是否全部相同
     *
     * @param numOrStr
     * @return
     */
    public static boolean isAllRepeat(String numOrStr) {
        boolean isSame = true;
        int first = Integer.parseInt(numOrStr.charAt(0) + "");
        for (int i = 1; i < numOrStr.length(); i++) {
            if (first != Integer.parseInt(numOrStr.charAt(i) + "")) {
                isSame = false;
            }
        }
        return isSame;
    }


    /**
     * @param si whether using SI unit refer to International System of Units.
     */
    public static String humanReadableBytes(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format(Locale.ENGLISH, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * 去两位小数
     *
     * @param top
     * @param below
     * @return
     */
    public static float deciMal(float top, float below) {
        return (float) new BigDecimal(top / below).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    @NonNull
    public static String getSpeed(long startTime, long totalLength) {
        float loadTime = System.currentTimeMillis() - startTime;
        loadTime = deciMal(loadTime, 1000);
        if (loadTime < 0.5f) {
            loadTime = 0.5f;   //单位  s
        }
        float speed = deciMal(totalLength, loadTime * 1024);
        String speedStr;
        if (speed > 1024) {
            speedStr = deciMal(speed, 1024) + " MB/s";
        } else if (speed < 0) {
            speedStr = "0 K/S";
        } else {
            speedStr = speed + " K/S";
        }
        return speedStr;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

}
