package com.sharpic.service;

import com.sharpic.common.Util;
import com.sharpic.domain.Product;
import com.sharpic.domain.Recipe;
import com.sharpic.domain.RecipeItem;
import com.sharpic.domain.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by joey on 2016-12-21.
 */

@Service
public class ObjectDescriptor implements IObjectDescriptor{
    @Autowired
    private IServerCache serverCache;

    public String getDescription(Product product) {
        if (product == null)
            return null;

        Size size = serverCache.findSize(product.getSizeId());
        if (size != null) {
            return product.getName() + " " + size.getName();
        }

        return "UNKNOWN SIZE with size id: " + product.getSizeId();
    }

    public String getDescription(Recipe recipe) {
        if (recipe == null)
            return null;

        List<RecipeItem> recipeItems = recipe.getRecipeItems();
        if (recipeItems == null)
            return null;

        String result = "";
        for (int i = 0; i < recipeItems.size(); i++) {
            RecipeItem recipeItem = recipeItems.get(i);
            result += this.getDescription(serverCache.findProduct(recipeItem.getProductId()));
            if (Util.isCloseToZero(recipeItem.getFulls()))
                result += (" ounces " + recipeItem.getOunces());
            else
                result += (" fulls " + (int) recipeItem.getFulls());

            if (i != recipeItems.size() - 1)
                result += ", ";
        }
        return result;
    }
}
