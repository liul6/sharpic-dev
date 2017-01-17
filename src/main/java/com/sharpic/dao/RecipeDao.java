package com.sharpic.dao;

import com.sharpic.domain.*;
import com.sharpic.service.IObjectTransientFieldsPopulator;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by joey on 2016-12-15.
 */

@Service
public class RecipeDao {
    @Autowired
    private RecipeMapper recipeMapper;

    @Autowired
    private RecipeItemMapper recipeItemMapper;

    @Autowired
    private RecipeItemDao recipeItemDao;

    @Autowired
    private ClientProductDao clientProductDao;

    @Autowired
    private IObjectTransientFieldsPopulator objectDescriptor;

    public Recipe createRecipe(String clientName, String recipeName) {
        Recipe recipe = new Recipe();
        recipe.setClientName(clientName);
        recipe.setRecipeName(recipeName);

        return createRecipe(recipe);
    }

    public Recipe createRecipe(Recipe recipe) {
        recipeMapper.createRecipe(recipe);
        return recipeMapper.getRecipeByName(recipe.getClientName(), recipe.getRecipeName());
    }

    public Map<Integer, Recipe> getClientRecipesMap(String clientName) {
        Map<Integer, Recipe> clientRecipeMap = new HashMap<Integer, Recipe>();

        List<Recipe> clientRecipes = recipeMapper.getClientRecipes(clientName);
        if (clientRecipes == null)
            return null;

        return clientRecipeMap;
    }

    public Recipe getRecipe(int recipeId) {
        Recipe recipe = recipeMapper.getRecipe(recipeId);
        if (recipe == null)
            return null;

        List<RecipeItem> recipeItems = recipeItemMapper.getRecipeItems(recipeId);
        if (recipeItems != null) {
            for (int i = 0; i < recipeItems.size(); i++) {
                RecipeItem recipeItem = recipeItems.get(i);
                recipeItem.setClientProduct(clientProductDao.getClientProduct(recipeItem.getProductId()));
            }
        }

        recipe.setRecipeItems(recipeItems);

        return recipe;
    }

    public Recipe getRecipeByName(@Param("clientName") String clientName, @Param("recipeName") String recipeName) {
        return recipeMapper.getRecipeByName(clientName, recipeName);
    }

    public Map<Integer, Recipe> getRecipesMap() {
        Map<Integer, Recipe> recipeMap = new HashMap<Integer, Recipe>();
        List<Recipe> recipes = recipeMapper.getAllRecipes();
        List<RecipeItem> recipeItems = recipeItemDao.getAllRecipeItems();

        if (recipes == null)
            return null;

        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            recipeMap.put(recipe.getId(), recipe);
        }

        for (int i = 0; i < recipeItems.size(); i++) {
            RecipeItem recipeItem = recipeItems.get(i);

            Recipe recipe = recipeMap.get(recipeItem.getRecipeId());
            if (recipe == null)
                continue;

            recipe.addRecipeItem(recipeItem);
        }

        return recipeMap;
    }

}
