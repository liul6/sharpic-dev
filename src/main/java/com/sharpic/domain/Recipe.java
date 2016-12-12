package com.sharpic.domain;

import java.util.Date;

/**
 * Created by joey on 2016-12-11.
 */
public class Recipe implements Comparable<Recipe> {
    private int id;
    private String clientName;
    private String recipeName;
    private boolean ignore;
    private Date updatedDatetime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public Date getUpdatedDatetime() {
        return updatedDatetime;
    }

    public void setUpdatedDatetime(Date updatedDatetime) {
        this.updatedDatetime = updatedDatetime;
    }

    public int compareTo(Recipe recipe) {
        return this.recipeName.compareTo(recipe.recipeName);
    }
}
