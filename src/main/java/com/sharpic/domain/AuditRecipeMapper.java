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

    public void deleteAuditRecipes(int auditId);

    public void createDummyAuditRecipe(AuditRecipe recipe);

    public void insertAuditRecipe(@Param("auditId") int auditId, @Param("auditRecipe") Recipe auditRecipe);

    public AuditRecipe getAuditRecipeByName(@Param("auditId") int auditId,
                                            @Param("clientName") String clientName, @Param("recipeName") String recipeName);

}
