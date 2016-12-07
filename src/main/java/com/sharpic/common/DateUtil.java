package com.sharpic.common;

import java.text.SimpleDateFormat;
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
}
