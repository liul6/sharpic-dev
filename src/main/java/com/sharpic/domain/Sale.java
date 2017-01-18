package com.sharpic.domain;

import java.util.Date;

/**
 * Created by joey on 2016-12-13.
 */
public class Sale extends BaseObject implements Comparable<Sale> {
    private int auditId;
    private int recipeId;
    private double amount;
    private double price;
    private Date updatedDatetime;
    private Recipe recipe;

    public int getAuditId() {
        return auditId;
    }

    public void setAuditId(int auditId) {
        this.auditId = auditId;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getUpdatedDatetime() {
        return updatedDatetime;
    }

    public void setUpdatedDatetime(Date updatedDatetime) {
        this.updatedDatetime = updatedDatetime;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public int compareTo(Sale sale) {
        return this.recipe.getDescription().compareTo(sale.recipe.getDescription());
    }
}
