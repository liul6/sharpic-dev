package com.sharpic.dao;

import com.sharpic.domain.RecipeItem;
import com.sharpic.domain.RecipeItemMapper;
import com.sharpic.service.IServerCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by joey on 2017-01-07.
 */

@Service
public class RecipeItemDao {
    @Autowired
    private RecipeItemMapper recipeItemMapper;

    @Autowired
    private IServerCache serverCache;

    public List<RecipeItem> getAllRecipeItems() {
        List<RecipeItem> recipeItems = recipeItemMapper.getAllRecipeItems();

        for (int j = 0; j < recipeItems.size(); j++) {
            RecipeItem recipeItem = recipeItems.get(j);
            recipeItem.setProduct(serverCache.findProduct(recipeItem.getProductId()));
        }

        return recipeItems;
    }


    public void deleteRecipeItems(int recipeId) {
        recipeItemMapper.deleteRecipeItems(recipeId);
    }

    public void insertRecipeItem(RecipeItem recipeItem) {
        recipeItemMapper.insertRecipeItem(recipeItem);
    }


    public int getNumberOfRecipeItemsByProductId(int productId) {
        return recipeItemMapper.getNumberOfRecipeItemsByProductId(productId);
    }
}
