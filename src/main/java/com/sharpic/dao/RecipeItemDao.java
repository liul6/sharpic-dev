package com.sharpic.dao;

import com.sharpic.domain.ClientProduct;
import com.sharpic.domain.RecipeItem;
import com.sharpic.domain.RecipeItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by joey on 2017-01-07.
 */

@Service
public class RecipeItemDao {
    @Autowired
    private RecipeItemMapper recipeItemMapper;

    @Autowired
    private ClientProductDao clientProductDao;

    public List<RecipeItem> getAllRecipeItems() {
        List<RecipeItem> recipeItems = recipeItemMapper.getAllRecipeItems();
        Map<Integer, ClientProduct> clientProductsMap = clientProductDao.getClientProducts(recipeItems);

        for (int j = 0; j < recipeItems.size(); j++) {
            RecipeItem recipeItem = recipeItems.get(j);
            recipeItem.setClientProduct(clientProductsMap.get(recipeItem.getProductId()));
        }

        return recipeItems;
    }


    public void deleteRecipeItems(int recipeId) {
        recipeItemMapper.deleteRecipeItems(recipeId);
    }

    public void insertRecipeItem(RecipeItem recipeItem) {
        recipeItemMapper.insertRecipeItem(recipeItem);
    }

}
