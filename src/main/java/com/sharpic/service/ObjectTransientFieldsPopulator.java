package com.sharpic.service;

import com.sharpic.common.Util;
import com.sharpic.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by joey on 2016-12-21.
 */

@Service
public class ObjectTransientFieldsPopulator implements IObjectTransientFieldsPopulator {
    @Autowired
    private IServerCache serverCache;

    public void populateProductTransientFields(ClientProduct product) {
        if (product == null)
            return;
        if (product.getSize() == null) {
            product.setSize(serverCache.findSize(product.getSizeId()));
        }
        Size size = product.getSize();
        if (size != null) {
            product.setDescription(product.getName() + " " + size.getName());
        } else {
            product.setDescription(product.getName() + " UNKNOWN SIZE with size id: " + product.getSizeId());
        }
    }

    public void populateProductTransientFields(List<ClientProduct> clientProducts) {
        if (clientProducts == null)
            return;

        for (int i = 0; i < clientProducts.size(); i++) {
            populateProductTransientFields(clientProducts.get(i));
        }
    }

    public void populateRecipeTransientFields(Recipe recipe) {
        if (recipe == null)
            return;

        List<RecipeItem> recipeItems = recipe.getRecipeItems();
        if (recipeItems == null)
            return;

        String result = "";
        for (int i = 0; i < recipeItems.size(); i++) {
            RecipeItem recipeItem = recipeItems.get(i);
            ClientProduct clientProduct = recipeItem.getClientProduct();
            populateProductTransientFields(clientProduct);
            result += clientProduct.getDescription();
            if (Util.isCloseToZero(recipeItem.getFulls()))
                result += (" ounces " + recipeItem.getOunces());
            else
                result += (" fulls " + (int) recipeItem.getFulls());

            if (i != recipeItems.size() - 1)
                result += ", ";
        }
        recipe.setDescription(result);
    }

    public void populateRecipeTransientFields(List<Recipe> recipes) {
        if (recipes == null)
            return;

        for (int i = 0; i < recipes.size(); i++) {
            populateRecipeTransientFields(recipes.get(i));
        }

    }
}
