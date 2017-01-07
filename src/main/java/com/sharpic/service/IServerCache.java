package com.sharpic.service;

import com.sharpic.domain.Product;
import com.sharpic.domain.Recipe;
import com.sharpic.domain.Size;

import java.util.List;

/**
 * Created by joey on 2016-12-08.
 */

public interface IServerCache {
    List<Size> getSizes();

    List<Product> getProducts();

    List<Recipe> getRecipes(String clientName);

    Size findSize(int sizeId);

    Product findProduct(int productId);

    Recipe findRecipeById(int recipeId);

    Recipe findRecipeByName(String clientName, String recipeName);

    void addRecipe(Recipe recipe);
}
