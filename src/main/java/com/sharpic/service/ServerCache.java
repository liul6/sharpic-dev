package com.sharpic.service;

import com.sharpic.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by joey on 2016-12-08.
 */

@Service
@ManagedResource
public class ServerCache implements IServerCache {
    @Autowired
    private SizeMapper sizeMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RecipeMapper recipeMapper;

    private Map<Integer, Size> sizeMap = new ConcurrentHashMap<Integer, Size>();
    private Map<Integer, Product> productMap = new ConcurrentHashMap<Integer, Product>();
    private Map<Integer, Recipe> recipeMap = new ConcurrentHashMap<Integer, Recipe>();

    @PostConstruct
    @ManagedOperation
    public void reloadCache() {
        sizeMap.clear();
        productMap.clear();
        recipeMap.clear();

        this.fillSizeCache();
        this.fillProductCache();
        this.fillRecipeCache();
    }

    @Override
    public Size findSize(int sizeId) {
        if (sizeMap.isEmpty())
            this.fillSizeCache();

        return sizeMap.get(sizeId);
    }

    @Override
    public Product findProduct(int productId) {
        if (productMap.isEmpty())
            this.fillProductCache();

        return productMap.get(productId);
    }

    @Override
    public Recipe findRecipe(int recipeId) {
        if (recipeMap.isEmpty())
            this.fillRecipeCache();

        return recipeMap.get(recipeId);
    }

    public void clearSizeCache() {
        sizeMap.clear();
    }

    public void clearProductCache() {
        productMap.clear();
    }

    private void fillSizeCache() {
        sizeMap.clear();

        List<Size> sizes = sizeMapper.getSizes();
        if (sizes == null)
            return;

        for (int i = 0; i < sizes.size(); i++) {
            Size size = sizes.get(i);
            sizeMap.put(size.getId(), size);
        }
    }

    private void fillProductCache() {
        productMap.clear();

        List<Product> products = productMapper.getProducts();
        if (products == null)
            return;

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            productMap.put(product.getId(), product);
        }
    }

    private void fillRecipeCache() {
        recipeMap.clear();

        List<Recipe> recipes = recipeMapper.getActiveRecipes();
        if (recipes == null)
            return;

        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            recipeMap.put(recipe.getId(), recipe);
        }
    }

    public List<Product> getProducts() {
        if (productMap == null || productMap.size() <= 0)
            this.fillProductCache();

        List<Product> allProducts = new ArrayList<Product>();
        allProducts.addAll(productMap.values());

        return allProducts;
    }

    public List<Size> getSizes() {
        if (sizeMap == null || sizeMap.size() <= 0)
            this.fillSizeCache();

        List<Size> allSizes = new ArrayList<Size>();
        allSizes.addAll(sizeMap.values());

        return allSizes;
    }

    public List<Recipe> getRecipes(String clientName) {
        if (recipeMap == null || recipeMap.size() <= 0)
            this.fillRecipeCache();

        List<Recipe> clientRecipes = new ArrayList<Recipe>();
        Iterator iter = recipeMap.values().iterator();
        while (iter.hasNext()) {
            Recipe recipe = (Recipe) iter.next();
            if (recipe.getClientName().equals(clientName))
                clientRecipes.add(recipe);
        }

        return clientRecipes;
    }
}
