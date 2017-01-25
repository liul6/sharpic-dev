package com.sharpic.domain;

import java.util.Date;

/**
 * Created by joey on 2016-12-17.
 */

public class ClientProduct extends BaseObject implements Comparable<ClientProduct> {
    private int productId;
    private double retailPrice;
    private Date updatedDatetime;

    //transient field
    private Product product;

    public double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public Date getUpdatedDatetime() {
        return updatedDatetime;
    }

    public void setUpdatedDatetime(Date updatedDatetime) {
        this.updatedDatetime = updatedDatetime;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int compareTo(ClientProduct clientProduct) {
        if (product != null && clientProduct.getProduct() != null)
            return product.getDescription().compareTo(clientProduct.getProduct().getDescription());

        return String.valueOf(this.getId()).compareTo(String.valueOf(clientProduct.getId()));
    }
}
