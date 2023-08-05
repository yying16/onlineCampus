package com.campus.gateway.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 */
public class TimeUtil {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getCurrentTime() {
        return format.format(new Date());
    }

    public static long getTime() {
        return new Date().getTime();
    }

    public static Date parse(String source) {
        try {
            return format.parse(source);
        } catch (Exception e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static long getTimeInterval(String time1, String time2) {
        long t1 = getTimeStamp(time1);
        long t2 = getTimeStamp(time2);
        return t1 > t2 ? t1 - t2 : t2 - t1;
    }

    public static long getTimeStamp(String time) {
        return parse(time).getTime();
    }

    public static String format(Date date) {
        return format.format(date);
    }
}
