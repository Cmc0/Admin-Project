package com.cmc.common.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MyDateUtil {

    /**
     * 生成【昨天 00:59:59到 现在（yyyy-MM-dd HH:59:59）】的数据
     * 纬度：小时
     * %m-%d %H
     */
    public static List<DateTime> yesterdayToNowByHour() {
        DateTime end = DateUtil.endOfHour(new Date()); // 今天设置为：yyyy-MM-dd HH:59:59 格式
        DateTime start = DateUtil.offsetDay(end, -1); // 往前偏移一个天
        start = DateUtil.endOfHour(DateUtil.beginOfDay(start)); // 设置起始时间为：yyyy-MM-dd 00:59:59 格式
        return DateUtil.rangeToList(start, end, DateField.HOUR_OF_DAY);
    }

    /**
     * 生成【上个月全部（yyyy-MM-第一天 23:59:59） 到现在（yyyy-MM-dd 23:59:59）】的数据
     * 纬度：天
     * %Y-%m-%d
     */
    public static List<DateTime> lastMonthToNowByDay() {
        DateTime end = DateUtil.endOfDay(new Date()); // 今天设置为：yyyy-MM-dd 23:59:59 格式
        DateTime start = DateUtil.offsetMonth(end, -1); // 往前偏移一个月
        start = DateUtil.endOfDay(DateUtil.beginOfMonth(start)); // 设置起始时间为：yyyy-MM-第一天 23:59:59 格式
        return DateUtil.rangeToList(start, end, DateField.DAY_OF_YEAR);
    }

    /**
     * 生成【上一年全部（yyyy-第一月-最后一天 59:59:59） 到现在（yyyy-MM-最后一天 59:59:59）】的数据
     * 纬度：月
     * %Y-%m
     */
    public static List<DateTime> lastYearToNowByMonth() {
        DateTime end = DateUtil.beginOfMonth(new Date()); // 今天设置为：yyyy-MM-本月第一天 00:00:00 格式
        DateTime start = DateUtil.offset(end, DateField.YEAR, -1); // 往前偏移一年
        start = DateUtil.beginOfYear(start); // 设置起始时间为：yyyy-本年第一月-本月第一天 00:00:00 格式
        List<DateTime> timeList = DateUtil.rangeToList(start, end, DateField.MONTH);
        timeList =
            timeList.stream().map(DateUtil::endOfMonth).collect(Collectors.toList()); // 设置为：yyyy-MM-最后一天 59:59:59 格式
        return timeList;
    }

    /**
     * 生成【所有（年）】的数据
     * 纬度：年
     * %Y
     */
    public static List<DateTime> allYear() {
        DateTime end = DateUtil.beginOfYear(new Date()); // 今天设置为：yyyy-第一月-第一天 00:00:00 格式
        DateTime start = DateUtil.parse("2021", DatePattern.NORM_YEAR_PATTERN); // 2021-01-01 00:00:00
        List<DateTime> timeList = DateUtil.rangeToList(start, end, DateField.YEAR);
        timeList =
            timeList.stream().map(DateUtil::endOfYear).collect(Collectors.toList()); // 设置为：yyyy-最后一月-最后一天 59:59:59 格式
        return timeList;
    }

    /**
     * 通过日期，获取 Cron 表达式
     */
    public static String getCron(Date date) {
        return DateUtil.format(date, "ss mm HH dd MM ? yyyy");
    }

}
