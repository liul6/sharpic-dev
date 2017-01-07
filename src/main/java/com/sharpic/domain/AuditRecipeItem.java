package com.sharpic.domain;

/**
 * Created by joey on 2016-12-20.
 */

public class AuditRecipeItem extends RecipeItem {
    private int auditId;

    public int getAuditId() {
        return auditId;
    }

    public void setAuditId(int auditId) {
        this.auditId = auditId;
    }
}
