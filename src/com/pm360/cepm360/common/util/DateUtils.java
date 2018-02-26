package com.pm360.cepm360.common.util;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.pm360.cepm360.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类 默认使用 "yyyy-MM-dd HH:mm:ss" 格式化日期
 * 
 */
@SuppressLint("SimpleDateFormat") 
public final class DateUtils {
	/**
     * 英文简写（默认）如：2010-12-01
     */
    public static String FORMAT_SHORT = "yyyy-MM-dd";
    /**
     * 英文全称 如：2010-12-01 23:15:06
     */
    public static String FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";
    public static String FORMAT_LONG1 = "yyyy-MM-dd HH:mm";
    /**
     * 精确到毫秒的完整时间 如：yyyy-MM-dd HH:mm:ss.S
     */
    public static String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.S";
    public static String FORMAT_FULL_2 = "yyyyMMddHHmmss";
    /**
     * 中文简写 如：2010年12月01日
     */
    public static String FORMAT_SHORT_CN = "yyyy年MM月dd";
    public static String FORMAT_SHORT_TIME = "HH:mm:ss";
    /**
     * 中文全称 如：2010年12月01日 23时15分06秒
     */
    public static String FORMAT_LONG_CN = "yyyy年MM月dd日  HH时mm分ss秒";
    /**
     * 精确到毫秒的完整中文时间
     */
    public static String FORMAT_FULL_CN = "yyyy年MM月dd日  HH时mm分ss秒SSS毫秒";

	
	public static SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");
	public static Calendar mCalendar = Calendar.getInstance();

	/**
	 * 
	 * @param dateString
	 * @param formatStr
	 * @return
	 */
	public static String FormatDateStrng(String dateString) {
		if (dateString == null || dateString.equals("null")) {
			return "";
		}
		if (dateString.length() > 0) {
			dateString = dateString.substring(0, 10);
		}
		return dateString;
	}
		
	public static String getToday() {  
		Date curDate = new Date(System.currentTimeMillis());   
		return mFormatter.format(curDate);   
	}
	
	public static String getCurrentDate() {		
		Calendar c = Calendar.getInstance();
		return new SimpleDateFormat("yyyy-MM").format(c.getTime());
	}
	
	public static String getCurrentDate(String format) {		
		Calendar c = Calendar.getInstance();
		return new SimpleDateFormat(format).format(c.getTime());
	}
	
	public static String getNextMonth(String current) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(stringToDate("yyyy-MM", current));
		calendar.set(2, calendar.get(Calendar.MONTH) + 1);
		return new SimpleDateFormat("yyyy-MM").format(calendar.getTime());
	}
	
	public static String getFirstDayOfMonth(String month) {
		mCalendar.setTime(stringToDate("yyyy-MM", month));
		mCalendar.add(Calendar.MONTH, 0);
		mCalendar.set(Calendar.DAY_OF_MONTH, 1);
		return mFormatter.format(mCalendar.getTime());
	}
	
	public static String getLastDayOfMonth(String month) {
		mCalendar.setTime(stringToDate("yyyy-MM", month));
		mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return mFormatter.format(mCalendar.getTime());
	}
	
	public static Date stringToDate(String style, String date) {
		if (date == null || date.equals("")) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(style);
		try {
			return formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return new Date();
		}
	}

	public static String dateToString(String style, Date date) {
		if ((date == null) || (date.equals(""))) {
			return "";
		}
		SimpleDateFormat formatter = new SimpleDateFormat(style);
		return formatter.format(date);
	}
	
	public static String calculateDuration(Activity activity, Date startTime, Date endTime) {
		String duration = 1 + (endTime.getTime() - startTime.getTime())
				/ (24 * 3600 * 1000)
				+ activity.getString(R.string.plan_day);
		return duration;
	}
	
	public static int calculateDuration(Date startTime, Date endTime) {
		int duration = (int)(1 + (endTime.getTime() - startTime.getTime())
				/ (24 * 3600 * 1000));
		return duration;
	}
	
	/*
	 * 是否同一天
	 */
	public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        boolean isSameMonth = isSameYear && (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH));
        boolean isSameDate = isSameMonth && (cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH));

        return isSameDate;
    }  
	
	/*
	 * 是否同一年的同一月
	 */
	public static boolean isSameMonth(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        boolean isSameMonth = isSameYear && (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH));

        return isSameMonth;
    }    

	/*
	 * 是否同一年的同一周
	 */
	public static boolean isSameWeek(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        
        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        boolean isSameWeek = isSameYear && (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR));

        return isSameWeek;
    } 	

}
