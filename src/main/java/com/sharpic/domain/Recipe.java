package com.sharpic.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by joey on 2016-12-11.
 */
public class Recipe extends BaseObject implements Comparable<Recipe> {
    private String clientName;
    private String recipeName;
    private boolean ignore;
    private String objectId;
    private Date updatedDatetime;
    private List<RecipeItem> recipeItems;
    private String description;

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

    public List<RecipeItem> getRecipeItems() {
        return recipeItems;
    }

    public void setRecipeItems(List<RecipeItem> recipeItems) {
        this.recipeItems = recipeItems;

        if (recipeItems == null)
            this.recipeItems = new ArrayList<RecipeItem>();
    }

    public void addRecipeItem(RecipeItem recipeItem) {
        if (recipeItems == null)
            recipeItems = new ArrayList<RecipeItem>();
        recipeItems.add(recipeItem);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
