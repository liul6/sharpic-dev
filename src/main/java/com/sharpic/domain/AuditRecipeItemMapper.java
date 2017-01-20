package com.sharpic.domain;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by joey on 2016-12-20.
 */

@Mapper
public interface AuditRecipeItemMapper {
    List<RecipeItem> getAuditRecipeItems(int auditId);

    List<RecipeItem> getAuditRecipeItemsByRecipeId(int auditRecipeId);

    void deleteAuditRecipeItems(int auditId);

    void deleteRecipeItemsByRecipeId(int auditRecipeId);

    void insertAuditRecipeItem(@Param("auditRecipeItem") AuditRecipeItem auditRecipeItem);

    int getNumberOfRecipeItemsByProductId(int productId);
}
