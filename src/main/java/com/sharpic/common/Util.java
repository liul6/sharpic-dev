package com.sharpic.common;

/**
 * Created by joey on 2016-12-13.
 */
public class Util {
    public static boolean isCloseToZero(double d) {
        return Math.abs(d) < 0.009;
    }

    public static boolean isValidName(String name) {
        return name!=null && name.trim().length()>0;
    }
}
