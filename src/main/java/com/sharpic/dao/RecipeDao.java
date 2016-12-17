package com.sharpic.dao;

import com.sharpic.domain.Recipe;
import com.sharpic.domain.RecipeMapper;
import com.sharpic.service.IServerCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by joey on 2016-12-15.
 */

@Service
public class RecipeDao {
    @Autowired
    private RecipeMapper recipeMapper;

    @Autowired
    private IServerCache serverCache;

    public Recipe createDummyRecipe(String clientName, String recipeName) {
        recipeMapper.createDummyRecipe(clientName, recipeName);

        Recipe recipe = recipeMapper.getRecipeByName(clientName, recipeName);

        serverCache.loadActiveRecipe(recipe);

        return recipe;
    }
}
