package com.campus.common.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
public class TimeUtil {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getCurrentTime() {
        Date now = new Date();
        DateUtil.offset(now, DateField.HOUR, 8);
        return format.format(now);
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


    public static Integer getLastDay(Integer year,Integer month){
        // 设置日期为指定年份和月份的第一天
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1); // 月份从0开始，所以需要减1
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        // 将日期设置为下个月的第一天
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        // 获取最后一天日期
        Date lastDayOfMonth = calendar.getTime();

        // 使用SimpleDateFormat将日期转换成指定格式输出
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        return Integer.parseInt(dateFormat.format(lastDayOfMonth));
    }
}
