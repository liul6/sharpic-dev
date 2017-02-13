package com.sharpic.domain;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by joey on 2016-12-20.
 */

@Mapper
public interface AuditRecipeMapper {
    public List<AuditRecipe> getAuditRecipes(int auditId);

    public AuditRecipe getAuditRecipe(int auditRecipeId);

    public void deleteAuditRecipes(int auditId);

    public void createDummyAuditRecipe(AuditRecipe recipe);

    public void insertAuditRecipe(@Param("auditRecipe") AuditRecipe auditRecipe);

    public AuditRecipe getAuditRecipeByName(@Param("auditId") int auditId, @Param("recipeName") String recipeName);
}
