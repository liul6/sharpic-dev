package com.sharpic.service;

import com.sharpic.domain.Product;
import com.sharpic.domain.Recipe;

/**
 * Created by joey on 2016-12-21.
 */
public interface IObjectDescriptor {
    String getDescription(Product product);
    String getDescription(Recipe recipe);
}
