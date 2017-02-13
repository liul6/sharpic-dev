package com.sharpic.dao;

import com.sharpic.domain.*;
import com.sharpic.service.IObjectTransientFieldsPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by joey on 2017-01-07.
 */
@Service
public class AuditRecipeDao {
    @Autowired
    private AuditRecipeMapper auditRecipeMapper;

    @Autowired
    private AuditRecipeItemDao auditRecipeItemDao;

    @Autowired
    private IObjectTransientFieldsPopulator objectDescriptor;

    public List<AuditRecipe> getAuditRecipes(int auditId) {
        List<RecipeItem> auditRecipeItems = auditRecipeItemDao.getAuditRecipeItems(auditId);
        Map<Integer, List<RecipeItem>> auditRecipeItemMap = new HashMap<Integer, List<RecipeItem>>();

        if (auditRecipeItems != null) {
            for (int i = 0; i < auditRecipeItems.size(); i++) {
                AuditRecipeItem auditRecipeItem = (AuditRecipeItem) auditRecipeItems.get(i);

                if (!auditRecipeItemMap.containsKey(auditRecipeItem.getRecipeId())) {
                    auditRecipeItemMap.put(auditRecipeItem.getRecipeId(), new ArrayList<RecipeItem>());
                }

                List<RecipeItem> auditRecipeItemList = auditRecipeItemMap.get(auditRecipeItem.getRecipeId());
                auditRecipeItemList.add(auditRecipeItem);
            }
        }

        List<AuditRecipe> auditRecipes = auditRecipeMapper.getAuditRecipes(auditId);
        if (auditRecipes != null) {
            for (int i = 0; i < auditRecipes.size(); i++) {
                AuditRecipe auditRecipe = auditRecipes.get(i);
                auditRecipe.setRecipeItems(auditRecipeItemMap.get(auditRecipe.getId()));
            }
        }

        return auditRecipes;
    }


    public Map<Integer, AuditRecipe> getAuditRecipesMap(int auditId) {
        List<AuditRecipe> auditRecipes = getAuditRecipes(auditId);

        Map<Integer, AuditRecipe> auditRecipeMap = new HashMap<Integer, AuditRecipe>();

        if (auditRecipes == null)
            return auditRecipeMap;

        for (int i = 0; i < auditRecipes.size(); i++) {
            AuditRecipe auditRecipe = auditRecipes.get(i);
            auditRecipeMap.put(auditRecipe.getId(), auditRecipe);
        }

        return auditRecipeMap;
    }

    public int createAuditRecipe(int auditId, Recipe recipe) {
        insertAuditRecipe(auditId, recipe);
        AuditRecipe auditRecipe = getAuditRecipeByName(auditId, recipe.getRecipeName());

        return auditRecipe.getId();
    }

    public AuditRecipe createAuditRecipe(AuditRecipe auditRecipe) {
        insertAuditRecipe(auditRecipe);
        AuditRecipe auditRecipeDB = getAuditRecipeByName(auditRecipe.getAuditId(), auditRecipe.getRecipeName());

        return auditRecipeDB;
    }

    public void deleteAuditRecipes(int auditId) {
        auditRecipeItemDao.deleteAuditRecipeItems(auditId);
        auditRecipeMapper.deleteAuditRecipes(auditId);
    }

    public void createDummyAuditRecipe(AuditRecipe recipe) {
        auditRecipeMapper.createDummyAuditRecipe(recipe);
    }

    public void insertAuditRecipe(AuditRecipe auditRecipe) {
        auditRecipeMapper.insertAuditRecipe(auditRecipe);
        AuditRecipe auditRecipeDB = auditRecipeMapper.getAuditRecipeByName(auditRecipe.getAuditId(), auditRecipe.getRecipeName());

        List<RecipeItem> recipeItems = auditRecipe.getRecipeItems();
        if (recipeItems != null) {
            for (int i = 0; i < recipeItems.size(); i++) {

                RecipeItem recipeItem = recipeItems.get(i);
                if (recipeItem.getProductId() < 0) {
                    System.out.println("not good here!");
                }

                AuditRecipeItem auditRecipeItem = new AuditRecipeItem();
                auditRecipeItem.setAuditId(auditRecipeDB.getAuditId());
                auditRecipeItem.setRecipeId(auditRecipe.getId());
                auditRecipeItem.setProductId(recipeItem.getProductId());
                auditRecipeItem.setFulls(recipeItem.getFulls());
                auditRecipeItem.setOunces(recipeItem.getOunces());

                auditRecipeItemDao.insertAuditRecipeItem(auditRecipeItem);
            }
        }
    }

    public void insertAuditRecipe(int auditId, Recipe recipe) {
        AuditRecipe auditRecipe = new AuditRecipe();
        auditRecipe.setAuditId(auditId);
        auditRecipe.setClientName(recipe.getClientName());
        auditRecipe.setRecipeName(recipe.getRecipeName());
        auditRecipe.setObjectId(String.valueOf(recipe.getId()));

        auditRecipeMapper.insertAuditRecipe(auditRecipe);
        auditRecipe = auditRecipeMapper.getAuditRecipeByName(auditId, recipe.getRecipeName());

        List<RecipeItem> recipeItems = recipe.getRecipeItems();
        if (recipeItems != null) {
            for (int i = 0; i < recipeItems.size(); i++) {

                RecipeItem recipeItem = recipeItems.get(i);
                if (recipeItem.getProductId() < 0) {
                    System.out.println("not good here!");
                }

                AuditRecipeItem auditRecipeItem = new AuditRecipeItem();
                auditRecipeItem.setAuditId(auditId);
                auditRecipeItem.setRecipeId(auditRecipe.getId());
                auditRecipeItem.setProductId(recipeItem.getProductId());
                auditRecipeItem.setFulls(recipeItem.getFulls());
                auditRecipeItem.setOunces(recipeItem.getOunces());

                auditRecipeItemDao.insertAuditRecipeItem(auditRecipeItem);
            }
        }
    }

    public AuditRecipe getAuditRecipeByName(int auditId, String recipeName) {
        return auditRecipeMapper.getAuditRecipeByName(auditId, recipeName);
    }

    public AuditRecipe getAuditRecipe(int auditRecipeId) {
        AuditRecipe auditRecipe = auditRecipeMapper.getAuditRecipe(auditRecipeId);
        if (auditRecipe == null)
            return null;

        List<RecipeItem> recipeItems = auditRecipeItemDao.getAuditRecipeItemsByRecipeId(auditRecipeId);
        auditRecipe.setRecipeItems(recipeItems);

        return auditRecipe;
    }
}
