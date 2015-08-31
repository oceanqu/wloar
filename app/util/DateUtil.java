package util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.time.FastDateFormat;

/**
 * 日期工具类
 * 
 * @author 
 * 
 */
public class DateUtil {

    private static final String dateFormatType = "yyyy-MM-dd";
    private static final String dateFormat_ss = "yyyy-MM-dd HH:mm:ss";

    private static FastDateFormat fdfWithTime = FastDateFormat.getInstance(dateFormat_ss, TimeZone.getDefault(), Locale.getDefault());
    private static FastDateFormat fdfWithoutTime = FastDateFormat.getInstance(dateFormatType, TimeZone.getDefault(), Locale.getDefault());

    /**
     * 获取当前时间
     * 
     * @return String
     */
    public static String getNowTime() {
        return fdfWithTime.format(new Date());
        // return DateFormatUtils.format(new Date(), dateFormat_ss);
    }

    /**
     * 获取24小时前的时间
     * 
     * @return String
     */
    public static String get24hAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return fdfWithTime.format(calendar.getTime());
    }

    public static long get24hAgoLong() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime().getTime();

    }

    /**
     * 获得当前日期与本周日相差的天数
     * 
     * @return int
     */
    private static int getMondayPlus() {
        Calendar calendar = Calendar.getInstance();
        // 获得今天是一周的第几天，星期日是第一天，星期一是第二天......
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        if (dayOfWeek == 1) {
            return 0;
        } else {
            return 1 - dayOfWeek;
        }
    }

    /**
     * 获得今日零时时间(String类型 yyyy-MM-dd HH:mm:ss)
     * 
     * @return String
     */
    public static String getTodayString() {
        Date today = new Date();
        String str = "";
        str = fdfWithoutTime.format(today);
        str = str + " 00:00:00";
        return str;
    }

    /**
     * 获得今日零时时间(Date类型)
     * 
     * @return Date
     */
    public static Date getTodayDate() {
        String str = getTodayString();
        Date today = null;
        try {
            // SimpleDateFormat不是线程安全的，所以在此new一个实例使用
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            today = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return today;
    }

    /**
     * 获得本周一零时的时间(String类型)
     * 
     * @return String
     */
    public static String getMondayStringOFWeek() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus);
        Date monday = currentDate.getTime();
        String preMonday = fdfWithoutTime.format(monday);
        String[] arr_time = preMonday.split("-");
        if (arr_time[1].length() == 1) {
            arr_time[1] = "0" + arr_time[1];
        }
        if (arr_time[2].length() == 1) {
            arr_time[2] = "0" + arr_time[2];
        }
        preMonday = arr_time[0] + "-" + arr_time[1] + "-" + arr_time[2];
        preMonday = preMonday + " 00:00:00";
        return preMonday;
    }

    /**
     * 获得本周一日期(String类型 yyyy-MM-dd)
     * 
     * @return String
     */
    public static String getFirstDayInWeek() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus);
        Date monday = currentDate.getTime();
        return date2String(monday, "");
    }

    /**
     * 获得本周一零时的时间(Date类型)
     * 
     * @return Date
     */
    public static Date getMondayDateOFWeek() {
        String preMonday = getMondayStringOFWeek();
        Date mondayDate = null;
        try {
            // SimpleDateFormat不是线程安全的，所以在此new一个实例使用
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mondayDate = sdf.parse(preMonday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mondayDate;
    }

    /**
     * 获取当月第一天零时的时间(String类型)
     * 
     * @return String
     */
    public static String getFirstDayStringOfMonth() {
        String str = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
        str = simpleDateFormat.format(lastDate.getTime());
        str = str + " 00:00:00";
        return str;
    }

    /**
     * 获取当月第一天日期(String类型 yyyy-MM-dd)
     * 
     * @return String
     */
    public static String getFirstDayInMonth() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
        String str = simpleDateFormat.format(lastDate.getTime());
        return str;
    }

    /**
     * 获取当月第一天零时的时间(Date类型)
     * 
     * @return Date
     */
    public static Date getFirstDayDateOfMonth() {
        String str = getFirstDayStringOfMonth();
        Date firstDayDate = null;
        try {
            // SimpleDateFormat不是线程安全的，所以在此new一个实例使用
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            firstDayDate = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return firstDayDate;
    }

    /**
     * 将java.sql.timestamp转为带时间的String
     * 
     * @param timestamp
     * @return String
     */
    public static String timestamp2String(Timestamp timestamp) {
        return fdfWithTime.format(timestamp);
        // return sdfWithTime.format(timestamp);
    }
    
    /**
     * 将java.sql.Date转化为不带时间的String
     * @param date
     * @return String
     */
    public static String date2String(java.sql.Date date){
    	return fdfWithoutTime.format(date);
    }
    /**
     * 将String转换为java.sql.Date
     * 
     * @param strDate
     * @param formatType
     * @return
     * @throws Exception
     */
    public static java.sql.Date string2Date(String strDate, String formatType) throws Exception {
        try {
            if (UtilValidate.isEmpty(formatType)) {
                formatType = dateFormatType;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(formatType);
            java.util.Date date = sdf.parse(strDate);
            long l = date.getTime();
            java.sql.Date sDate = new java.sql.Date(l);
            return sDate;
        } catch (ParseException e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 将String转换为java.util.Date
     * 
     * @param strDate
     * @param formatType
     * @return
     * @throws Exception
     */
    public static java.util.Date string2UtilDate(String strDate, String formatType) throws Exception {
        try {
            if (UtilValidate.isEmpty(formatType)) {
                formatType = dateFormatType;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(formatType);
            java.util.Date date = sdf.parse(strDate);
            return date;
        } catch (ParseException e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 将Time(到秒)转换为java.util.Date的String
     * 
     * @param strDate
     * @param formatType
     * @return
     * @throws Exception
     */
    public static String getDateStrFromTime(Long time) {
        if (time == null) {
            return "";
        }
        return date2String(new java.util.Date(time * 1000), "yyyy-MM-dd HH:mm:ss");
    }

    public static String getDateStrFromTime(Integer time) {
        if (time == null) {
            return "";
        }
        return date2String(new java.util.Date(time * 1000L), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 取得字符串日期对应的时间(转换到秒)
     * 
     * @param strDate
     * @param formatType
     * @return
     * @throws Exception
     */
    public static long getTimeFromDatestr(String strDate, String formatType) throws Exception {
        try {
            return string2UtilDate(strDate, formatType).getTime() / 1000;
        } catch (ParseException e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 将java.util.Date转换为formatType格式的String
     * 
     * @param date
     * @param formatType
     * @return
     * @throws Exception
     */
    public static String date2String(java.util.Date date, String formatType) {
        if (UtilValidate.isEmpty(formatType)) {
            formatType = dateFormatType;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(formatType);
        String strDate = sdf.format(date);
        return strDate;
    }

    /**
     * 取得当前日期,java.util.Date格式
     * 
     * @return
     */
    public static java.util.Date getNowDate() {
        return new java.util.Date();
    }

    /**
     * 取得当前日期,java.sql.Date格式
     * 
     * @return
     */
    public static java.sql.Date getNowDate4Sql() {
        return new java.sql.Date(new java.util.Date().getTime());
    }

    /**
     * 取得当前日期，String格式
     * 
     * @param formatType
     *            返回日期的格式，如yyyyMMdd
     * @return
     */
    public static String getNowDate4String(String formatType) {
        if (UtilValidate.isEmpty(formatType)) {
            formatType = dateFormatType;
        }
        return date2String(getNowDate(), formatType);
    }

    /**
     * 将inFormatType格式的日期字符串转换为outFormatType格式的日期字符串
     * 
     * @param strDate
     * @param inFormatType
     * @param outFormatType
     * @return
     * @throws Exception
     */
    public static String string2DateString(String strDate, String inFormatType, String outFormatType) throws Exception {
        if (UtilValidate.isEmpty(outFormatType)) {
            outFormatType = dateFormatType;
        }
        java.util.Date date = string2UtilDate(strDate, inFormatType);
        return date2String(date, outFormatType);
    }

    /**
     * 取得系统时间往后duration小时的时间（日期类型）
     * 
     * @return
     */
    public static java.sql.Date getDateAfterDuration(long duration) {
        java.util.Date date = getNowDate();
        long Time = date.getTime() + 1000 * 60 * 60 * duration;

        java.sql.Date sDate = new java.sql.Date(Time);
        return sDate;
    }

    /**
     * 取得系统时间往前duration小时的时间（日期类型）
     * 
     * @return
     */
    public static java.sql.Date getDateBeforeDuration(long duration) {
        java.util.Date date = getNowDate();
        long Time = date.getTime() - 1000 * 60 * 60 * duration;

        java.sql.Date sDate = new java.sql.Date(Time);
        return sDate;
    }

    /**
     * 取得系统时间往后duration小时的时间（字符串类型）
     * 
     * @param formatType
     *            指定返回日期格式
     * @return
     */
    public static String getDateAfterDuration(long duration, String formatType) {
        if (UtilValidate.isEmpty(formatType)) {
            formatType = dateFormatType;
        }
        java.util.Date date = getNowDate();
        long Time = date.getTime() + 1000 * 60 * 60 * duration;

        date = new java.util.Date(Time);
        return date2String(date, formatType);
    }

    /**
     * 取得7天前的时间（7*24小时前）
     * 
     * @return
     */
    public static String getDateAfterWeek() {
        return getDateAfterDuration(-7 * 24, dateFormat_ss);
    }

    /**
     * 取两个时间的时间差(单位毫秒)
     * 
     * @return
     */
    public static long getDateDuration(String startTime, String endTime) {
        try {
            Date sDate = string2Date(startTime, "yyyy-MM-dd HH:mm:ss");
            Date eDate = string2Date(endTime, "yyyy-MM-dd HH:mm:ss");
            // System.out.println(date1 + " dd " + date2);
            // System.out.println((date2.getTime() - date1.getTime()) / 60 /
            // 1000 / 60 + "");//
            // date2.getTime()得到的是毫秒，所以要除以60*1000
            return eDate.getTime() - sDate.getTime();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0L;
        }
    }

    /**
     * 取两个时间的时间差(单位小时)
     * 
     * @return
     */
    public static long getDateDurationD(String startTime, String endTime) {
        try {
            Date sDate = string2Date(startTime, "yyyy-MM-dd HH");
            Date eDate = string2Date(endTime, "yyyy-MM-dd HH");

            // date2.getTime()得到的是毫秒，所以要除以60*1000
            return (eDate.getTime() - sDate.getTime()) / 60 / 1000 / 60;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0L;
        }
    }

    /**
     * 取两个时间的时间差(单位毫秒)
     * 
     * @return
     */
    public static long getDateDuration(Date startTime, Date endTime) {
        try {
            return startTime.getTime() - endTime.getTime();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0L;
        }
    }

    /**
     * 获取一周前的开始日期
     * 
     * @return
     */
    public static String getDateBeforWeek() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.DATE, now.get(Calendar.DATE) - 6);
        return fdfWithoutTime.format(now.getTime());

    }

    /**
     * 获取一个月前的开始日期
     * 
     * @return
     */
    public static String getDateBeforMonth() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.MONTH, now.get(Calendar.MONTH) - 1);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + 1);
        return fdfWithoutTime.format(now.getTime());

    }

    /**
     * 获取当前的日期
     * 
     * @return
     */
    public static String getTodayDateStringNoTime() {
        return fdfWithoutTime.format(new Date());
    }

    /**
     * 
     * DateUtil.java
     * @param startDate 
     * @param endDate
     * @param formatType
     * @return :
     * 		1:   startDate>endDate
     * 		0:   startDate=endDate
     * 		-1:  startDate<endDate
     * 2015年3月16日
     */
    public static int compare_date(String startDate, String endDate,String formatType ) {
        
        
        DateFormat df = new SimpleDateFormat(formatType);
        try {
            Date dt1 = df.parse(startDate);
            Date dt2 = df.parse(endDate);
            if (dt1.getTime() > dt2.getTime()) {
                System.out.println("dt1 在dt2前");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1在dt2后");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }
    
    
    public static String currentTime() {
	SimpleDateFormat sf = new SimpleDateFormat(dateFormat_ss);
	return sf.format(new Date(System.currentTimeMillis()));
    }

    /**
     * 得到几天前的时间
     * 
     * @param d
     * @param day
     * @return Date
     */
    public static Date getDateBefore(Date d, int day) {
	Calendar now = Calendar.getInstance();
	now.setTime(d);
	now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
	return now.getTime();
    }

    /**
     * 得到几天前的时间
     * 
     * @param d
     * @param day
     * @return String
     */
    public static String getDayBefore(int day) {
	SimpleDateFormat sf = new SimpleDateFormat(dateFormat_ss);
	String todayString = sf.format(getDateBefore(new Date(), day));
	todayString = todayString.substring(0, 10) + " 00:00:00";
	return todayString;
    }

    /**
     * 得到几天前的日期 格式如2012-02-13
     * 
     * @param day
     * @return
     */
    public static String getDayBeforeNoTime(int day) {
	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	return sf.format(getDateBefore(new Date(), day));
    }

    /**
     * 得到几天后的时间
     * 
     * @param d
     * @param day
     * @return
     */
    public static Date getDateAfter(Date d, int day) {
	Calendar now = Calendar.getInstance();
	now.setTime(d);
	now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
	return now.getTime();
    }

    /**
     * 得到几天后的日期 格式如2012-02-13
     * 
     * @param day
     * @return
     */
    public static String getDateAfterNoTime(int day) {
	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	return sf.format(getDateAfter(new Date(), day));
    }

    /**
     * 获取今天的日期
     * 
     * @return
     */
    public static String getTodayDateNoTime() {
	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	return sf.format(new Date());
    }

    /**
     * 获取7天前的日期
     * 
     * @return
     */
    public static String getWeekAgoDateNoTime() {
	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	Calendar c = Calendar.getInstance();
	c.set(Calendar.DATE, c.get(Calendar.DATE) - 7);
	return sf.format(c.getTime());
    }

}
