package com.gov.common.util;


import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 时间工具类
 *
 * @author ruoyi
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";
    public static String YYYYMM = "yyyyMM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDD = "yyyyMMdd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static String YYYYMMDDCN="yyyy年MM月dd日";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM",
            "EEE MMM dd HH:mm:ss 'GMT'Z yyyy"};

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算相差天数
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        return Math.abs((int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24)));
    }

    /**
     * 计算时间差
     *
     * @param endTime   最后时间
     * @param startTime 开始时间
     * @return 时间差（天/小时/分钟）
     */
    public static String timeDistance(Date endDate, Date startTime) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - startTime.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 增加 LocalDateTime ==> Date
     */
    public static Date toDate(LocalDateTime temporalAccessor) {
        ZonedDateTime zdt = temporalAccessor.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 增加 LocalDate ==> Date
     */
    public static Date toDate(LocalDate temporalAccessor) {
        LocalDateTime localDateTime = LocalDateTime.of(temporalAccessor, LocalTime.of(0, 0, 0));
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 字符串时间格式转换
     * @param dateStr 转换前字符串时间格式
     * @param srcFormat 转换后字符串时间格式
     * @param destFormat 字符串
     * @return
     * @throws ParseException
     */

    public static String formatDate(String dateStr, String srcFormat, String destFormat) throws ParseException {
        Date date = dateTime(dateStr,destFormat);
        return  parseDateToStr(srcFormat,date);
    }

    /**
     * 获取当年12 个月的数据
     * @return
     */
    public static List<String> getLast12Months(){
        List<String> list=new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        list.add(parseDateToStr(YYYY_MM,cal.getTime()));

        for(int i=0;i<5;i++){
            cal.add(Calendar.MONTH,-1);
            list.add(parseDateToStr(YYYY_MM,cal.getTime()));
        }
        Collections.reverse(list);
        return list;
    }

    /**
     * 根据传入的时间字符串、时间格式、时间数量和时间单位来判断与当前时间的时间差是否超过指定时间
     *
     * @param timeString   时间字符串
     * @param formatString 时间格式字符串
     * @param timeAmount   时间数量
     * @param timeUnit     时间单位
     * @return true表示时间差距超过指定时间，false表示时间差距不足指定时间
     */
    public static boolean isTimeOverLimit(String timeString, String formatString, int timeAmount, TimeUnit timeUnit) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 解析时间格式字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatString);
        LocalDateTime time = LocalDateTime.parse(timeString, formatter);

        // 计算时间差距
        long diff = timeUnit.convert(ChronoUnit.NANOS.between(time, now), TimeUnit.NANOSECONDS);

        // 判断时间差距是否超过指定时间
        if (diff > timeAmount) {
            return true;
        } else {
            return false;
        }
    }

    public static Date parseZonedDate(String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'GMT'z uuuu", Locale.ENGLISH);
        ZonedDateTime date = ZonedDateTime.parse(dateString, formatter);
        return Date.from(date.toInstant());
    }


    public static void main(String[] args) {
        try {
            String dateString = "Thu Apr 11 09:46:25 GMT+08:00 2024";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'GMT'z uuuu", Locale.ENGLISH);
            ZonedDateTime date = ZonedDateTime.parse(dateString, formatter);
            System.out.println("Date object: " + date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
