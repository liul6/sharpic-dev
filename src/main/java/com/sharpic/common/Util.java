package com.sharpic.common;

import com.sharpic.domain.BaseObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Map<Integer, BaseObject> createIDObjectMap(List baseObjects) {
        Map<Integer, BaseObject> idObjectMap = new HashMap<Integer, BaseObject>();

        if (baseObjects == null || baseObjects.size() <= 0)
            return idObjectMap;

        for (int i = 0; i < baseObjects.size(); i++) {
            BaseObject baseObject = (BaseObject) baseObjects.get(i);
            idObjectMap.put(baseObject.getId(), baseObject);
        }

        return idObjectMap;
    }

}
