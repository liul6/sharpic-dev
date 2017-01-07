package com.sharpic.dao;

import com.sharpic.domain.*;
import com.sharpic.service.IObjectDescriptor;
import com.sharpic.service.IServerCache;
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
    private AuditRecipeMapper auditRecipeMapper;

    @Autowired
    private AuditRecipeItemMapper auditRecipeItemMapper;

    @Autowired
    private IServerCache serverCache;

    @Autowired
    private IObjectDescriptor objectDescriptor;

//    public AuditRecipe createAuditRecipe(int auditId, String clientName, String recipeName) {
//        AuditRecipe auditRecipe = new AuditRecipe();
//        auditRecipe.setAuditId(auditId);
//        auditRecipe.setClientName(clientName);
//        auditRecipe.setRecipeName(recipeName);
//
//        auditRecipeMapper.createDummyAuditRecipe(auditRecipe);
//        createRecipe(clientName, recipeName);
//
//        return auditRecipeMapper.getAuditRecipeByName(auditId, clientName, recipeName);
//    }

    public Recipe createRecipe(String clientName, String recipeName) {
        Recipe recipe = new Recipe();
        recipe.setClientName(clientName);
        recipe.setRecipeName(recipeName);

        recipeMapper.createRecipe(recipe);
        return recipeMapper.getRecipeByName(clientName, recipeName);
    }

    public Map<Integer, Recipe> getClientRecipesMap(String clientName) {
        Map<Integer, Recipe> clientRecipeMap = new HashMap<Integer, Recipe>();

        List<Recipe> clientRecipes = recipeMapper.getClientRecipes(clientName);
        if (clientRecipes == null)
            return null;

        return clientRecipeMap;
    }

    public Recipe getRecipe(int recipeId) {
        return recipeMapper.getRecipe(recipeId);
    }

    public Recipe getRecipeByName(@Param("clientName") String clientName, @Param("recipeName") String recipeName) {
        return recipeMapper.getRecipeByName(clientName, recipeName);
    }

    public Map<Integer, Recipe> getRecipesMap() {
        Map<Integer, Recipe> recipeMap = new HashMap<Integer, Recipe>();
        List<Recipe> recipes = recipeMapper.getAllRecipes();
        List<RecipeItem> recipeItems = recipeItemMapper.getAllRecipeItems();

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

        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            recipe.setDescription(objectDescriptor.getDescription(recipe));
        }

        return recipeMap;
    }

    public Map<Integer, AuditRecipe> getAuditRecipesMap(int auditId) {
        Map<Integer, AuditRecipe> auditRecipeMap = new HashMap<Integer, AuditRecipe>();
        List<AuditRecipe> auditRecipes = auditRecipeMapper.getAuditRecipes(auditId);
        List<AuditRecipeItem> auditRecipeItems = auditRecipeItemMapper.getAuditRecipeItems(auditId);

        if (auditRecipes == null)
            return auditRecipeMap;

        for (int i = 0; i < auditRecipes.size(); i++) {
            AuditRecipe auditRecipe = auditRecipes.get(i);
            auditRecipeMap.put(auditRecipe.getId(), auditRecipe);
        }

        for (int i = 0; i < auditRecipeItems.size(); i++) {
            AuditRecipeItem auditRecipeItem = auditRecipeItems.get(i);
            AuditRecipe auditRecipe = auditRecipeMap.get(auditRecipeItem.getRecipeId());

            if (auditRecipe != null)
                auditRecipe.addRecipeItem(auditRecipeItem);
        }

        for (int i = 0; i < auditRecipes.size(); i++) {
            AuditRecipe auditRecipe = auditRecipes.get(i);
            auditRecipe.setDescription(objectDescriptor.getDescription(auditRecipe));
        }

        return auditRecipeMap;
    }

    public void deleteAuditRecipes(int auditId) {
        auditRecipeItemMapper.deleteAuditRecipeItems(auditId);
        auditRecipeMapper.deleteAuditRecipes(auditId);
    }

    public int createAuditRecipe(int auditId, Recipe recipe) {
        auditRecipeMapper.insertAuditRecipe(auditId, recipe);
        AuditRecipe auditRecipe = auditRecipeMapper.getAuditRecipeByName(auditId, recipe.getClientName(), recipe.getRecipeName());

        List<RecipeItem> recipeItems = recipe.getRecipeItems();
        if (recipeItems != null) {
            for (int i = 0; i < recipeItems.size(); i++) {
                RecipeItem recipeItem = recipeItems.get(i);
                auditRecipeItemMapper.insertAuditRecipeItem(auditId, auditRecipe.getId(), recipeItem);
            }
        }

        return auditRecipe.getId();
    }
}
