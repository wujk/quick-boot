package com.wujk.utils.date;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.wujk.utils.pojo.ObjectUtil;

/**
 * 日期工具类
 */
public class DateUtil {
	public static final String FORMAT_DATE_YMDHMS = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_DATE_YMD = "yyyy-MM-dd";
	public static final String FORMAT_DATE_YM = "yyyy-MM";
	public static final String FORMAT_DATE_HM = "HH:mm";

	/**
	 * 将Date类型转换为字符串
	 *
	 * @param date
	 *            日期类型
	 * @return 日期字符串
	 */
	public static String format(Date date) {
		return format(date, FORMAT_DATE_YMD);
	}

	public static Date convertStringToDate(String date, String pattern) {
		if (date == "")
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	/**
	 * 将Date类型转换为字符串
	 *
	 * @param date
	 *            日期类型
	 * @param pattern
	 *            字符串格式
	 * @return 日期字符串
	 */
	public static String format(Date date, String pattern) {
		if (date == null) {
			return "null";
		}
		if (pattern == null || pattern.equals("") || pattern.equals("null")) {
			pattern = FORMAT_DATE_YMDHMS;
		}
		return new SimpleDateFormat(pattern).format(date);
	}

	/**
	 * 将字符串转换为Date类型
	 *
	 * @param date
	 *            字符串类型
	 * @return 日期类型
	 */
	public static Date format(String date) {
		return format(date, null);
	}

	/**
	 * 将字符串转换为Date类型
	 *
	 * @param date
	 *            字符串类型
	 * @param pattern
	 *            格式
	 * @return 日期类型
	 */
	public static Date format(String date, String pattern) {
		if (pattern == null || pattern.equals("") || pattern.equals("null")) {
			pattern = FORMAT_DATE_YMDHMS;
		}
		if (date == null || date.equals("") || date.equals("null")) {
			return new Date();
		}
		Date d = null;
		try {
			d = new SimpleDateFormat(pattern).parse(date);
		} catch (ParseException pe) {
		}
		return d;
	}

	/**
	 * 得到年月日时分秒字符串
	 *
	 * @return 字符串
	 */
	public static String getDateYmdHms() {
		Date date = new Date();
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmmss");
		String now = formatter1.format(date);
		return now;
	}

	public static Date getNextDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		date = c.getTime();
		return date;
	}

	public static Date getNextHour(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.HOUR_OF_DAY, 1);
		date = c.getTime();
		return date;
	}

	public static Date getPreviousMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, -1);
		date = c.getTime();
		return date;
	}

	public static Date getPreviousWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, -7);
		date = c.getTime();
		return date;
	}

	// 获得当前月的最后一天
	public static Date getLastDayOfMonth() {
		String dateFormat = FORMAT_DATE_YMD;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, 1);// 加一个月，变为下月的1号
		lastDate.add(Calendar.DATE, -1);// 减去一天，变为当月最后一天

		return strToDate(sdf.format(lastDate.getTime()), dateFormat);
	}

	// 获得当前月的最后一天
	public static String getLastDayOfMonthStr() {
		String dateFormat = FORMAT_DATE_YMD;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, 1);// 加一个月，变为下月的1号
		lastDate.add(Calendar.DATE, -1);// 减去一天，变为当月最后一天

		return sdf.format(lastDate.getTime());
	}

	// 获得上月的第一天
	public static String getFirstDayOfPreviousMonth() {
		String dateFormat = FORMAT_DATE_YMDHMS;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, -1);// 变为当月的1号

		lastDate.set(Calendar.HOUR_OF_DAY, 0);
		lastDate.set(Calendar.MINUTE, 0);
		lastDate.set(Calendar.SECOND, 0);
		lastDate.set(Calendar.MILLISECOND, 0);
		// lastDate.add(Calendar.DATE, -1);//减去一天，变为当月最后一天

		return sdf.format(lastDate.getTime());
	}

	// 获得当上月的最后一天
	public static String getLastDayOfBeforeMonth() {
		String dateFormat = FORMAT_DATE_YMDHMS;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, 0);// 变为当月的1号
		lastDate.add(Calendar.DATE, -1);// 减去一天，变为当月最后一天

		lastDate.set(Calendar.HOUR_OF_DAY, 23);
		lastDate.set(Calendar.MINUTE, 59);
		lastDate.set(Calendar.SECOND, 59);
		lastDate.set(Calendar.MILLISECOND, 999);

		return sdf.format(lastDate.getTime());
	}

	// 获得当上月的最后一天
	public static String getLastDayOfBeforeMonthYMD() {
		String dateFormat = FORMAT_DATE_YMD;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, 0);// 变为当月的1号
		lastDate.add(Calendar.DATE, -1);// 减去一天，变为当月最后一天

		return sdf.format(lastDate.getTime());
	}

	// 获取当天时间
	public static String getNowDate() {
		String dateFormat = FORMAT_DATE_YMD;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Calendar lastDate = Calendar.getInstance();

		return sdf.format(lastDate.getTime());

	}

	// 获取当月第一天
	public static Date getFirstDayOfMonth() {
		String dateFormat = FORMAT_DATE_YMD;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		return strToDate(sdf.format(lastDate.getTime()), dateFormat);
	}

	// 获取当月第一天
	public static String getFirstDayOfTheMonth(int i) {
		String dateFormat = FORMAT_DATE_YMD;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1 + i);// 设为当前月的1号
		return sdf.format(lastDate.getTime());
	}

	// 获取上月第一天
	public static String getFirstDayOfTheBefordeMonth(int i) {
		String dateFormat = FORMAT_DATE_YMD;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Calendar lastDate = Calendar.getInstance();
		lastDate.add(Calendar.MONTH, -1);// 变为上月
		lastDate.set(Calendar.DATE, 1 + i);
		return sdf.format(lastDate.getTime());
	}

	// 获取当天时间
	public static Date getNowTime(String dateformat) {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(dateformat);// 可以方便地修改日期格式
		return strToDate(dateFormat.format(now), dateformat);

	}

	// 获取当天的开始时间
	public static String getStartTime(String date) {
		String dateFromFormat = FORMAT_DATE_YMD;
		String dateToFormat = FORMAT_DATE_YMDHMS;
		SimpleDateFormat dateFormatFrom = new SimpleDateFormat(dateFromFormat);
		SimpleDateFormat dateFormatTo = new SimpleDateFormat(dateToFormat);
		Calendar todayStart = Calendar.getInstance();
		try {
			Date parsedDate = dateFormatFrom.parse(date);

			todayStart.setTime(parsedDate);
			todayStart.set(Calendar.HOUR_OF_DAY, 0);
			todayStart.set(Calendar.MINUTE, 0);
			todayStart.set(Calendar.SECOND, 0);
			todayStart.set(Calendar.MILLISECOND, 0);

		} catch (ParseException ex) {
			ex.printStackTrace();// TODO
		}

		return dateFormatTo.format(todayStart.getTime());

	}

	// 获取当天的开始时间
	public static String getDayStartTime(String date, String dateFromFormat, String dateToFormat) {
		// String dateFromFormat = FORMAT_DATE_YMD;
		// String dateToFormat = FORMAT_DATE_YMDHMS;
		SimpleDateFormat dateFormatFrom = new SimpleDateFormat(dateFromFormat);
		SimpleDateFormat dateFormatTo = new SimpleDateFormat(dateToFormat);
		Calendar todayStart = Calendar.getInstance();
		try {
			Date parsedDate = dateFormatFrom.parse(date);

			todayStart.setTime(parsedDate);
			todayStart.set(Calendar.HOUR_OF_DAY, 0);
			todayStart.set(Calendar.MINUTE, 0);
			todayStart.set(Calendar.SECOND, 0);
			todayStart.set(Calendar.MILLISECOND, 0);

		} catch (ParseException ex) {
			ex.printStackTrace();// TODO
		}

		return dateFormatTo.format(todayStart.getTime());

	}

	// 获取当天的结束时间
	public static String getEndTime(String date) {
		String dateFromFormat = FORMAT_DATE_YMD;
		String dateToFormat = FORMAT_DATE_YMDHMS;
		SimpleDateFormat dateFormatFrom = new SimpleDateFormat(dateFromFormat);
		SimpleDateFormat dateFormatTo = new SimpleDateFormat(dateToFormat);
		Calendar todayEnd = Calendar.getInstance();
		try {
			Date parsedDate = dateFormatFrom.parse(date);
			todayEnd.setTime(parsedDate);
			todayEnd.set(Calendar.HOUR_OF_DAY, 23);
			todayEnd.set(Calendar.MINUTE, 59);
			todayEnd.set(Calendar.SECOND, 59);
			todayEnd.set(Calendar.MILLISECOND, 999);
		} catch (ParseException ex) {
			ex.printStackTrace();// TODO
		}
		return dateFormatTo.format(todayEnd.getTime());
	}

	// 获取当天的结束时间
	public static String getDayEndTime(String date, String dateFromFormat, String dateToFormat) {
		// String dateFromFormat = FORMAT_DATE_YMD;
		// String dateToFormat = FORMAT_DATE_YMDHMS;
		SimpleDateFormat dateFormatFrom = new SimpleDateFormat(dateFromFormat);
		SimpleDateFormat dateFormatTo = new SimpleDateFormat(dateToFormat);
		Calendar todayEnd = Calendar.getInstance();
		try {
			Date parsedDate = dateFormatFrom.parse(date);
			todayEnd.setTime(parsedDate);
			todayEnd.set(Calendar.HOUR_OF_DAY, 23);
			todayEnd.set(Calendar.MINUTE, 59);
			todayEnd.set(Calendar.SECOND, 59);
			todayEnd.set(Calendar.MILLISECOND, 999);
		} catch (ParseException ex) {
			ex.printStackTrace();// TODO
		}
		return dateFormatTo.format(todayEnd.getTime());
	}

	// 获取当天的开始时间
	public static Date getStartDate(Date date) {
		Calendar todayStart = Calendar.getInstance();
		todayStart.setTime(date);
		todayStart.set(Calendar.HOUR_OF_DAY, 0);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		return todayStart.getTime();

	}

	// 获取当天的结束时间
	public static Date getEndDate(Date date) {
		Calendar todayEnd = Calendar.getInstance();
		todayEnd.setTime(date);
		todayEnd.set(Calendar.HOUR_OF_DAY, 23);
		todayEnd.set(Calendar.MINUTE, 59);
		todayEnd.set(Calendar.SECOND, 59);
		todayEnd.set(Calendar.MILLISECOND, 999);
		return todayEnd.getTime();
	}

	public static Date getYesterday() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	public static String getYesterdayStr() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		String yesterday = new SimpleDateFormat(FORMAT_DATE_YMD).format(cal.getTime());
		return yesterday;
	}

	public static Date computeDate(Date date, int diffDay) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, diffDay);
		date = c.getTime();
		return date;
	}

	public static String dateToString(Date d) {
		return dateToString(d, FORMAT_DATE_YMD);
	}

	public static String dateToString(Date d, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}

	public static Date strToDate(String dateStr) {
		return strToDate(dateStr, "MM/dd/yyyy");
	}

	public static Date strParseDate(String dateStr) {
		return strParseDate(dateStr, FORMAT_DATE_YMD);
	}

	public static Date strToDate(String dateStr, String format) {
		if (ObjectUtil.isEmpty(dateStr)) {
			return null;
		}
		SimpleDateFormat dateFmt = new SimpleDateFormat(format);

		dateFmt.setLenient(false);
		ParsePosition pos = new ParsePosition(0);
		return dateFmt.parse(dateStr, pos);
	}

	public static Date strParseDate(String dateStr, String format) {
		if (ObjectUtil.isEmpty(dateStr)) {
			return null;
		}
		SimpleDateFormat dateFmt = new SimpleDateFormat(format);

		try {
			return dateFmt.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean compareDate(Date date1, Date date2) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DATE_YMD);
		return simpleDateFormat.format(date1).equals(simpleDateFormat.format(date2));
	}

	public static String getDayOfWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		String DayOfWeek = "";
		switch (dayOfWeek) {
		case 1: {
			DayOfWeek = "Sun";
			break;
		}
		case 2: {
			DayOfWeek = "Mon";
			break;
		}
		case 3: {
			DayOfWeek = "Tue";
			break;
		}
		case 4: {
			DayOfWeek = "Wed";
			break;
		}
		case 5: {
			DayOfWeek = "Thu";
			break;
		}
		case 6: {
			DayOfWeek = "Fri";
			break;
		}
		case 7: {
			DayOfWeek = "Sat";
			break;
		}
		}
		return DayOfWeek;
	}

	public static Integer getIndexOfDayOfWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		return dayOfWeek;
	}

	public static Integer getWeekOfYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.WEEK_OF_YEAR);
	}

	public static boolean checkTodayPassHalfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		if (day >= 15) {
			return true;
		}
		return false;
	}

	public static Date addMinutes(Date date, int amount) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MINUTE, amount);
		return c.getTime();
	}

	public static Date addHours(Date date, int amount) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.HOUR_OF_DAY, amount);
		return c.getTime();
	}

	public static Date addDays(Date date, int amount) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, amount);
		return c.getTime();
	}

	public static Date addMonths(Date date, int amount) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, amount);
		return c.getTime();
	}

	public static Date[] getDateArrays(Date start, Date end, int calendarType) {
		ArrayList<Date> ret = new ArrayList<Date>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(start);
		Date tmpDate = calendar.getTime();
		long endTime = end.getTime();
		while (tmpDate.before(end) || tmpDate.getTime() == endTime) {
			ret.add(calendar.getTime());
			calendar.add(calendarType, 1);
			tmpDate = calendar.getTime();
		}
		Date[] dates = new Date[ret.size()];
		return (Date[]) ret.toArray(dates);
	}

	public static Date getCurrYearFirst() {
		Calendar currCal = Calendar.getInstance();
		int currentYear = currCal.get(Calendar.YEAR);
		return getYearFirst(currentYear);
	}

	public static Date getYearFirst(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		Date currYearFirst = calendar.getTime();
		return currYearFirst;
	}

	public static int getCurrentYear() {
		Calendar currCal = Calendar.getInstance();
		int currentYear = currCal.get(Calendar.YEAR);
		return currentYear;

	}

	public static int getCurrentMonth() {
		Calendar currCal = Calendar.getInstance();
		int currentMonth = currCal.get(Calendar.MONTH);
		return currentMonth+1;
	}
	
	/** 
	   * 得到几天前的时间 
	   * @param d 
	   * @param day 
	   * @return 
	   */  
	  public static Date getDateBefore(Date d,int day){  
	   Calendar now =Calendar.getInstance();  
	   now.setTime(d);  
	   now.set(Calendar.DATE,now.get(Calendar.DATE)-day);  
	   return now.getTime();  
	  }  
	    
	  /** 
	   * 得到几天后的时间 
	   * @param d 
	   * @param day 
	   * @return 
	   */  
	  public static String getDateAfter(Date d,int day){  
		  
	   String dateFormat = FORMAT_DATE_YMDHMS;
	   SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			
	   Calendar now =Calendar.getInstance();  
	   now.setTime(d);  
	   now.set(Calendar.DATE,now.get(Calendar.DATE)+day);  
	   return sdf.format(now.getTime());
	  }
}
