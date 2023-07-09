package com.campus.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 */
public class TimeUtil {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getCurrentTime(){
        return format.format(new Date());
    }
    public static long getTime(){
       return new Date().getTime();
    }

    public static Date parse(String source) {
        try{
            return format.parse(source);
        }catch (Exception e){
            e.printStackTrace();
            return new Date();
        }
    }
    public static String format(Date date){
        return format.format(date);
    }
}
