package com.example.myapp.utils;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;


public class DateUtils {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    //获取当前年
    public static int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    //获取当前月
    public static int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
    }


    //获取当前号
    public static int getcurrentDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    // 获得本周的周一的日期对应的时间戳
    public static long getMondayOfWeektoEpochMilli() {
        LocalDate now = LocalDate.now();
        LocalDate monday = now.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)).plusDays(1);
        return monday.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    //给日期时间获取时间戳
    public static long getTimestamOfDate(int y,int m,int d,int h,int mim,int s){
        LocalDateTime dateTime = LocalDateTime.of(y, m, d, h, mim, s);
        return dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }


    public static String getCurrentWeekStartToEnd() {
        LocalDate now = LocalDate.now();
        LocalDate monday = now.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)).plusDays(1);
        LocalDate sunday = now.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).minusDays(1);
        return monday.getMonthValue() + "月" + monday.getDayOfMonth() + "日-" +
                sunday.getMonthValue() + "月" + sunday.getDayOfMonth() + "日";
    }

    public static String getCurrentMonthStartToEnd() {
        LocalDate now = LocalDate.now();
        LocalDate firstday = LocalDate.of(now.getYear(), now.getMonth(), 1);
        LocalDate lastDay = now.with(TemporalAdjusters.lastDayOfMonth());
        return firstday.getMonthValue() + "月" + firstday.getDayOfMonth() + "日-" +
                lastDay.getMonthValue() + "月" + lastDay.getDayOfMonth() + "日";
    }





}
