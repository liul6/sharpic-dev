package com.sharpic.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by joey on 2016-12-13.
 */

public class Util {
    public static boolean isCloseToZero(double d) {
        return Math.abs(d) < 0.009;
    }

    public static boolean isValidName(String name) {
        return name != null && name.trim().length() > 0;
    }

    public static double round(double value, int places) {
        if (!Double.isFinite(value) || Double.isNaN(value))
            return 0.0D;
        if (places < 0)
            throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
