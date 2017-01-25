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
    public static final String UNDEFINED = "***UNDEFINED***";

    @Autowired
    private IServerCache serverCache;

    public void populateProductTransientFields(Product product) {
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

    public void populateProductTransientFields(List<Product> products) {
        if (products == null)
            return;

        for (int i = 0; i < products.size(); i++) {
            populateProductTransientFields(products.get(i));
        }
    }

    public void populateClientProductTransientField(ClientProduct clientProduct) {
        if (clientProduct.getProductId() > 0)
            clientProduct.setProduct(serverCache.findProduct(clientProduct.getProductId()));
    }

    public void populateClientProductTransientFields(List<ClientProduct> clientProducts) {
        if (clientProducts == null)
            return;

        for (int i = 0; i < clientProducts.size(); i++) {
            ClientProduct clientProduct = clientProducts.get(i);
            populateClientProductTransientField(clientProduct);
        }
    }

    public void populateRecipeTransientFields(Recipe recipe) {
        if (recipe == null)
            return;

        List<RecipeItem> recipeItems = recipe.getRecipeItems();
        if (recipeItems == null || recipeItems.size() == 0) {
            recipe.setDescription(UNDEFINED);
            return;
        }

        String result = "";
        for (int i = 0; i < recipeItems.size(); i++) {
            RecipeItem recipeItem = recipeItems.get(i);
            Product product = recipeItem.getProduct();
            populateProductTransientFields(product);
            result += product.getDescription();
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
