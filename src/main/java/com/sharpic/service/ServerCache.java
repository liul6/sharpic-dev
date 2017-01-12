package com.sharpic.service;

import com.sharpic.common.Util;
import com.sharpic.dao.ProductDao;
import com.sharpic.dao.RecipeDao;
import com.sharpic.domain.Product;
import com.sharpic.domain.Recipe;
import com.sharpic.domain.Size;
import com.sharpic.domain.SizeMapper;
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
    private ProductDao productDao;

    @Autowired
    private RecipeDao recipeDao;

    private Map<Integer, Size> sizeMap = new ConcurrentHashMap<Integer, Size>();
    private Map<Integer, Product> productMap = new ConcurrentHashMap<Integer, Product>();
    private Map<Integer, Recipe> recipeMapById = new ConcurrentHashMap<Integer, Recipe>();
    private Map<String, Recipe> recipeMapByName = new ConcurrentHashMap<String, Recipe>();

    @PostConstruct
    @ManagedOperation
    public void reloadCache() {
        sizeMap.clear();
        productMap.clear();
        recipeMapById.clear();
        recipeMapByName.clear();

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
    public Recipe findRecipeById(int recipeId) {
        if (recipeMapById.isEmpty())
            this.fillRecipeCache();

        return recipeMapById.get(recipeId);
    }

    @Override
    public Recipe findRecipeByName(String clientName, String recipeName) {
        if (recipeMapByName.isEmpty())
            this.fillRecipeCache();

        Recipe recipe = recipeMapByName.get(clientName + "_" + recipeName);
        return recipe;
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
            if (Util.isValidName(size.getName()))
                sizeMap.put(size.getId(), size);
        }
    }

    private void fillProductCache() {
        productMap.clear();

        List<Product> products = productDao.getProducts();
        if (products == null)
            return;

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            if (Util.isValidName(product.getName())) {
                productMap.put(product.getId(), product);
            }
        }
    }

    private void fillRecipeCache() {
        recipeMapById.clear();

        Map<Integer, Recipe> recipeMap = recipeDao.getRecipesMap();
        if (recipeMap == null)
            return;

        Iterator iter = recipeMap.values().iterator();
        while (iter.hasNext()) {
            Recipe recipe = (Recipe) iter.next();
            if (Util.isValidName(recipe.getRecipeName())) {
                recipeMapById.put(recipe.getId(), recipe);
                recipeMapByName.put(recipe.getClientName() + "_" + recipe.getRecipeName(), recipe);
            }
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
        if (recipeMapById == null || recipeMapById.size() <= 0)
            this.fillRecipeCache();

        List<Recipe> clientRecipes = new ArrayList<Recipe>();
        Iterator iter = recipeMapById.values().iterator();
        while (iter.hasNext()) {
            Recipe recipe = (Recipe) iter.next();
            if (recipe.getClientName().equals(clientName)) {
                clientRecipes.add(recipe);
            }
        }

        return clientRecipes;
    }

    public void addRecipe(Recipe recipe) {
        recipeMapById.put(recipe.getId(), recipe);
        recipeMapByName.put(recipe.getClientName() + "_" + recipe.getRecipeName(), recipe);
    }

}
