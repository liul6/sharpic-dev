package com.sharpic.domain;

import java.util.Date;

/**
 * Created by joey on 2016-12-11.
 */
public class RecipeItem extends BaseObject {
    private int recipeId;
    private int productId;
    private double fulls;
    private double ounces;
    private String objectId;
    private Date updatedDatetime;

    //transient fields
    private ClientProduct clientProduct;

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public double getFulls() {
        return fulls;
    }

    public void setFulls(double fulls) {
        this.fulls = fulls;
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

    public ClientProduct getClientProduct() {
        return clientProduct;
    }

    public void setClientProduct(ClientProduct clientProduct) {
        this.clientProduct = clientProduct;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}

