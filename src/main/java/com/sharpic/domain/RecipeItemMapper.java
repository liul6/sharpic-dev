package com.sharpic.domain;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by joey on 2016-12-11.
 */

@Mapper
public interface RecipeItemMapper {
    public List<RecipeItem> getAllRecipeItems();

    public List<RecipeItem> getRecipeItems(int recipeId);

    public void deleteRecipeItems(int recipeId);

    public void insertRecipeItem(@Param("recipeItem") RecipeItem recipeItem);

    public int getNumberOfRecipeItemsByProductId(int productId);
}
