package com.tbb.data.twitter.core.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

  public static boolean isSameDay(Date date1, Date date2, TimeZone timeZone) {
        if(date1 != null && date2 != null) {
            Calendar cal1 = Calendar.getInstance(timeZone);
            cal1.setTime(date1);
            Calendar cal2 = Calendar.getInstance(timeZone);
            cal2.setTime(date2);
            return isSameDay(cal1, cal2);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if(cal1 != null && cal2 != null) {
            return cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(6) == cal2.get(6);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }
}
