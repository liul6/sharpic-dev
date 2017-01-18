package com.sharpic.domain;

/**
 * Created by joey on 2016-12-20.
 */

public class AuditRecipeItem extends RecipeItem {
    private int auditId;

    public AuditRecipeItem(){
    }

    public AuditRecipeItem(int auditId, int recipeId, RecipeItem recipeItem){
        this.auditId = auditId;
        this.setOunces(recipeItem.getOunces());
        this.setFulls(recipeItem.getFulls());
        this.setRecipeId(recipeId);
        this.setProductId(recipeItem.getProductId());
    }

    public int getAuditId() {
        return auditId;
    }

    public void setAuditId(int auditId) {
        this.auditId = auditId;
    }
}
