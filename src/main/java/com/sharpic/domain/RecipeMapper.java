package com.sharpic.domain;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by joey on 2016-12-11.
 */

@Mapper
public interface RecipeMapper {
    public List<Recipe> getActiveRecipes();

    public List<Recipe> getClientActiveRecipes(String clientName);

    public Recipe getRecipe(int recipeId);

    public Recipe getRecipeByName(@Param("clientName") String clientName, @Param("recipeName") String recipeName);

    public void createDummyRecipe(@Param("clientName") String clientName, @Param("recipeName") String recipeName);
}
