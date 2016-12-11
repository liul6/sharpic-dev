package com.sharpic.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by joey on 2016-12-06.
 */
public class DateUtil {
    public final static String YYYY_MM_DD = "yyyy-MM-dd";
    public static String format(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String format(Date date) {
        return format(date, YYYY_MM_DD);
    }

    public static Date fromString(String dateStr, String formatStr) {
        DateFormat format = new SimpleDateFormat(formatStr);
        Date date = null;

        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static Date toDate(LocalDate localDate){
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
}
