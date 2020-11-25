package com.yaofu.basesdk.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Auth:long3.yang
 * Date:2020/10/21
 **/
public class DateUtils {

    private static final String DEFAULT_TIME = "yyyy.MM.dd";

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT =
            new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    public static final SimpleDateFormat NO_SPLIT_DATE_FORMAT =
            new SimpleDateFormat("yyyyMMddHHmmss");
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy.MM.dd");

    public static final SimpleDateFormat DATE_FORMAT_DATE_TIME =
            new SimpleDateFormat("yyyy.MM.dd HH:mm");

    public static final SimpleDateFormat DATE_FORMAT_TIME = new SimpleDateFormat("HH:mm");

    public static final SimpleDateFormat DATE_FORMATE_WEEK =
            new SimpleDateFormat("MMMdd'日' EEE HH:mm");

    public static final SimpleDateFormat DATE_FORMATE_MONTH =
            new SimpleDateFormat("MMMdd'日' HH:mm");

    public static final SimpleDateFormat DATE_FORMAT_DATE_CHINESE =
            new SimpleDateFormat("yyyy.MM.dd");

    public static final SimpleDateFormat DATA_FORMAT_MONTH_DATA_HOURS =
            new SimpleDateFormat("MM.dd HH:mm");

    /**
     * long time to string
     */
    public static synchronized String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * 两个时间的 2018.07.31  13:00-13:30
     * 2018.11.21 19:25 - 19:35
     */
    public static synchronized  String getTime(long startTime, long endTime) {

        return DATE_FORMAT_DATE_TIME.format(new Date(startTime)) + " - " + DATE_FORMAT_TIME.format(new Date(endTime));
    }

    /**
     * 带星期
     */
    public static synchronized String getTimeWithWeek(long startTime, long endTime) {
        return DATE_FORMATE_WEEK.format(new Date(startTime)) + " - " + DATE_FORMAT_TIME.format(new Date(endTime));
    }

    public static synchronized String getTimeWithMonth(long startTime, long endTime) {
        return DATE_FORMATE_MONTH.format(new Date(startTime)) + " - " + DATE_FORMAT_TIME.format(new Date(endTime));
    }

    public static synchronized String getTimeWithHour(long startTime, long endTime) {
        return DATE_FORMAT_TIME.format(new Date(startTime)) + " - " + DATE_FORMAT_TIME.format(new Date(endTime));
    }

    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     */
    public static synchronized String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }

    /**
     * get current time in milliseconds
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    public static Date strToDate(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            if (!TextUtils.isEmpty(dateStr)) {
                date = format.parse(dateStr);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 格式化时间显示
     *
     * @param time 时间 (秒)
     * @return 字符串 格式为 HH:mm:ss
     */
    public static String formatTimeValue(int time) {
        time = time < 0 ? 0 : time;
        int i = time;
        // i /= 1000;//毫秒的转换
        int minute = i / 60;
        int hour = minute / 60;
        int second = i % 60;
        minute %= 60;
        return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, minute, second);
    }

    public static final String DEFAULT_PATTERN = "yyyy.MM.dd HH:mm:ss";

    /**
     * HH:mm    15:44
     * h:mm a    3:44 下午
     * HH:mm z    15:44 CST
     * HH:mm Z    15:44 +0800
     * HH:mm zzzz    15:44 中国标准时间
     * HH:mm:ss    15:44:40
     * yyyy-MM-dd    2016-08-12
     * yyyy-MM-dd HH:mm    2016-08-12 15:44
     * yyyy-MM-dd HH:mm:ss    2016-08-12 15:44:40
     * yyyy-MM-dd HH:mm:ss zzzz    2016-08-12 15:44:40 中国标准时间
     * EEEE yyyy-MM-dd HH:mm:ss zzzz    星期五 2016-08-12 15:44:40 中国标准时间
     * yyyy-MM-dd HH:mm:ss.SSSZ    2016-08-12 15:44:40.461+0800
     * yyyy-MM-dd'T'HH:mm:ss.SSSZ    2016-08-12T15:44:40.461+0800
     * yyyy.MM.dd G 'at' HH:mm:ss z    2016.08.12 公元 at 15:44:40 CST
     * K:mm a    3:44 下午
     * EEE, MMM d, ''yy    周五, 八月 12, '16
     * hh 'o''clock' a, zzzz    03 o'clock 下午, 中国标准时间
     * yyyyy.MMMMM.dd GGG hh:mm aaa    02016.八月.12 公元 03:44 下午
     * EEE, d MMM yyyy HH:mm:ss Z    星期五, 12 八月 2016 15:44:40 +0800
     * yyMMddHHmmssZ    160812154440+0800
     * yyyy-MM-dd'T'HH:mm:ss.SSSZ    2016-08-12T15:44:40.461+0800
     * EEEE 'DATE('yyyy-MM-dd')' 'TIME('HH:mm:ss')' zzzz    星期五 DATE(2016-08-12) TIME(15:44:40) 中国标准时间
     */
    public static String getTime(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date());
    }

    public static String getTime(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static String getTime(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static String longToStr(long l) {
        Date addTime = new Date(l);
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd  HH:mm",Locale.SIMPLIFIED_CHINESE);
        String time = format.format(addTime);
        return time;
    }

    public static String longToStr2(long time) {
        Date addTime = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_TIME,Locale.SIMPLIFIED_CHINESE);
        return format.format(addTime);
    }

    public static String longToStr3(long l) {
        Date addTime = new Date(l);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm",Locale.SIMPLIFIED_CHINESE);
        String time = format.format(addTime);
        return time;
    }

    /**
     * 比较日期前后
     *
     * @param str1
     * @param str2
     * @return str1在str2后为true，否则为false
     */
    public static boolean compareDate(String str1, String str2) {
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_TIME,Locale.SIMPLIFIED_CHINESE);
        Date date1 = null;
        Date date2 = null;
        try {
            if (!TextUtils.isEmpty(str1)) {
                date1 = format.parse(str1);
            }
            if (!TextUtils.isEmpty(str2)) {
                date2 = format.parse(str2);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date1 == null || date2 == null) return false;
        if (date1.after(date2)) {
            return true;
        }
        return false;
    }

    //毫秒换成00:00:00
    public static String[] getCountTimeByLong(long finishTime) {
        int totalTime = (int) (finishTime / 1000);//秒
        int hour = 0, minute = 0, second = 0;
        String[] timeString = new String[3];

        if (3600 <= totalTime) {
            hour = totalTime / 3600;
            totalTime = totalTime - 3600 * hour;
        }
        if (60 <= totalTime) {
            minute = totalTime / 60;
            totalTime = totalTime - 60 * minute;
        }
        if (0 <= totalTime) {
            second = totalTime;
        }

        if (hour < 10) {
            timeString[0] = "0" + hour;
        } else {
            timeString[0] = hour + "";
        }
        if (minute < 10) {
            timeString[1] = "0" + minute;
        } else {
            timeString[1] = minute + "";
        }
        if (second < 10) {
            timeString[2] = "0" + second;
        } else {
            timeString[2] = second + "";
        }
        return timeString;

    }

    /**
     * 将时间段改成**天**时**分**秒的格式
     *
     * @param finishTime    截止时间
     * @param containSecond 是否包含秒
     * @return
     */
    public static String getTimeStringByLong(long finishTime, boolean containSecond) {
        int ONE_MINUT = 60;
        int ONE_HOUR = 60 * ONE_MINUT;
        int ONE_DAY = 24 * ONE_HOUR;
        int dayNum = 0, hourNum = 0, minuteNum = 0, secondNum = 0;
        int totalTime = (int) (finishTime / 1000);

        if (totalTime >= ONE_DAY) { // 大于1天
            dayNum = totalTime / ONE_DAY;
            totalTime = totalTime - dayNum * ONE_DAY;
        }
        if (totalTime >= ONE_HOUR) {
            hourNum = totalTime / ONE_HOUR;
            totalTime = totalTime - hourNum * ONE_HOUR;
        }
        if (totalTime >= ONE_MINUT) {
            minuteNum = totalTime / ONE_MINUT;
            totalTime = totalTime - minuteNum * ONE_MINUT;
        }
        if (totalTime >= 0) {
            secondNum = totalTime;
        }

        StringBuilder sb = new StringBuilder();
        if (dayNum < 10) {
            sb.append("0").append(dayNum).append("天");
        } else {
            sb.append(dayNum).append("天");
        }
        if (hourNum < 10) {
            sb.append("0").append(hourNum).append("小时");
        } else {
            sb.append(hourNum).append("小时");
        }
        if (minuteNum < 10) {
            sb.append("0").append(minuteNum).append("分钟");
        } else {
            sb.append(minuteNum).append("分钟");
        }
        if (containSecond) {
            if (secondNum < 10) {
                sb.append("0").append(secondNum).append("秒");
            } else {
                sb.append(secondNum).append("秒");
            }
        }
        return sb.toString();
    }

    public static String getNoSplitTimeString(long timeValue){
        return getTime(timeValue,NO_SPLIT_DATE_FORMAT);

    }

    /**
     * nowTime是否在[beginTime--endTime]之间
     * @param nowTime
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean WithinTimePeriod(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

}
