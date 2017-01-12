package com.sharpic.domain;

import java.util.Date;

/**
 * Created by joey on 2016-12-04.
 */

public class Entry extends BaseObject implements Comparable<Entry> {
    private int auditId;
    private String location;
    private int productId;
    private double amount;
    private double weight;
    private String weights;
    private double openBottles;
    private String bin;
    private Date updatedDatetime;

    //transient field
    private String productDescription;
    private ClientProduct clientProduct;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getOpenBottles() {
        return openBottles;
    }

    public void setOpenBottles(double openBottles) {
        this.openBottles = openBottles;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public Date getUpdatedDatetime() {
        return updatedDatetime;
    }

    public void setUpdatedDatetime(Date updatedDatetime) {
        this.updatedDatetime = updatedDatetime;
    }

    public int getAuditId() {
        return auditId;
    }

    public void setAuditId(int auditId) {
        this.auditId = auditId;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getWeights() {
        return weights;
    }

    public void setWeights(String weights) {
        this.weights = weights;
    }

    public int compareTo(Entry entry) {
        return this.getProductDescription().compareTo(entry.getProductDescription());
    }

    public ClientProduct getClientProduct() {
        return clientProduct;
    }

    public void setClientProduct(ClientProduct clientProduct) {
        this.clientProduct = clientProduct;
    }
}
