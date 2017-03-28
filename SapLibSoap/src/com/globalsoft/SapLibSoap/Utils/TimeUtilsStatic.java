package com.globalsoft.SapLibSoap.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtilsStatic {
	
	public static String dateToStringFormat(Date d) {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy MMMM dd EEE HH:mm");
		return formater.format(d);
	}
	
	public static String dateToStringFormat2(Date d) {
		SimpleDateFormat formater = new SimpleDateFormat("yy MMM dd EEE hh:mm a");
		return formater.format(d);
	}
	
	public static String dateToStringFormat3(Date d) {
		SimpleDateFormat formater = new SimpleDateFormat("yy MMM dd EEE HH:mm");
		return formater.format(d);
	}
	
	private static final String DATE_TIME_FORMAT1 = "yyyyMMddHHmmss";
	//get the current date and time as a string in format1
	public static String getCurrentDateTimeFormat1Str() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT1);
		return sdf.format(cal.getTime());
	}
	
	public static long DateToMilli(Date pDate) {
		Calendar lCal = Calendar.getInstance();
		lCal.setTime(pDate);
		return lCal.getTimeInMillis();
	}
	
}//End of class TimeUtilsStatic


