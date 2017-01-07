package com.sharpic.domain;

import java.util.Date;

/**
 * Created by joey on 2016-12-08.
 */

public class Size extends BaseObject implements Comparable<Size> {
    private String name;
    private double ounces;
    private Date updatedDatetime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getOunces() {
        return ounces;
    }

    public void setOunces(double ounces) {
        this.ounces = ounces;
    }

    public Date getUpdatedDatetime() {
        return updatedDatetime;
    }

    public void setUpdatedDatetime(Date updatedDatetime) {
        this.updatedDatetime = updatedDatetime;
    }

    public int compareTo(Size size) {
        return this.getName().compareTo(size.getName());
    }
}
