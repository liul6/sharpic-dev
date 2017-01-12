package com.sharpic.domain;

import java.util.Date;

/**
 * Created by joey on 2016-12-08.
 */
public class Product extends BaseObject implements Comparable<Product> {
    private String name;
    private double tare;
    private double cost;
    private double fulls;
    private int cases;
    private String upc;
    private String tags;
    private Date updatedDatetime;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTare() {
        return tare;
    }

    public void setTare(double tare) {
        this.tare = tare;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getFulls() {
        return fulls;
    }

    public void setFulls(double fulls) {
        this.fulls = fulls;
    }

    public int getCases() {
        return cases;
    }

    public void setCases(int cases) {
        this.cases = cases;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Date getUpdatedDatetime() {
        return updatedDatetime;
    }

    public void setUpdatedDatetime(Date updatedDatetime) {
        this.updatedDatetime = updatedDatetime;
    }

    public int compareTo(Product product) {
        return this.getName().compareTo(product.getName());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
