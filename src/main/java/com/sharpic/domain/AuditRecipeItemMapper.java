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

    void deleteAuditRecipeItems(int auditId);

    void insertAuditRecipeItem(@Param("auditRecipeItem") AuditRecipeItem auditRecipeItem);
}
