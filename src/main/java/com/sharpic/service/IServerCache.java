package com.sharpic.service;

import com.sharpic.domain.Product;
import com.sharpic.domain.Recipe;
import com.sharpic.domain.RecipeItem;
import com.sharpic.domain.Size;

import java.util.List;

/**
 * Created by joey on 2016-12-08.
 */

public interface IServerCache {
    Size findSize(int sizeId);

    Product findProduct(int productId);

    Recipe findRecipe(int recipeId);

    List<Size> getSizes();

    List<Product> getProducts();

    List<Recipe> getRecipes(String clientName);

    List<RecipeItem> findRecipeItems(int recipeId);
}
