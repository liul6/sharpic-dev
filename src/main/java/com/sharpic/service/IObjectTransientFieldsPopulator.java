package com.sharpic.service;

import com.sharpic.domain.ClientProduct;
import com.sharpic.domain.Recipe;

import java.util.List;

/**
 * Created by joey on 2016-12-21.
 */
public interface IObjectTransientFieldsPopulator {
    void populateProductTransientFields(ClientProduct product);

    void populateProductTransientFields(List<ClientProduct> clientProducts);

    void populateRecipeTransientFields(Recipe recipe);

    void populateRecipeTransientFields(List<Recipe> recipes);
}
