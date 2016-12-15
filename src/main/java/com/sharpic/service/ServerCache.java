package com.sharpic.service;

import com.sharpic.common.Util;
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

    @Autowired
    private RecipeItemMapper recipeItemMapper;

    private Map<Integer, Size> sizeMap = new ConcurrentHashMap<Integer, Size>();
    private Map<Integer, Product> productMap = new ConcurrentHashMap<Integer, Product>();
    private Map<Integer, Recipe> recipeMap = new ConcurrentHashMap<Integer, Recipe>();
    private Map<Integer, List<RecipeItem>> recipeItemMap = new ConcurrentHashMap<Integer, List<RecipeItem>>();

    @PostConstruct
    @ManagedOperation
    public void reloadCache() {
        sizeMap.clear();
        productMap.clear();
        recipeMap.clear();
        recipeItemMap.clear();

        this.fillSizeCache();
        this.fillProductCache();
        this.fillRecipeItemCache();
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

        Recipe recipe = recipeMap.get(recipeId);
        if(recipe!=null)
            return recipe;

        return recipeMapper.getRecipe(recipeId);
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

        List<Product> products = productMapper.getProducts();
        if (products == null)
            return;

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            if (Util.isValidName(product.getName())) {
                productMap.put(product.getId(), product);
                product.setDescription(this.getProductDescription(product));
            }
        }
    }

    private void fillRecipeCache() {
        recipeMap.clear();

        List<Recipe> recipes = recipeMapper.getActiveRecipes();
        if (recipes == null)
            return;

        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            if (Util.isValidName(recipe.getRecipeName())) {
                recipeMap.put(recipe.getId(), recipe);

                recipe.setRecipeItems(this.findRecipeItems(recipe.getId()));
                recipe.setDescription(getRecipeDescription(recipe));
            }
        }
    }

    private void fillRecipeItemCache() {
        recipeItemMap.clear();

        List<RecipeItem> recipeItems = recipeItemMapper.getRecipeItems();
        if (recipeItems == null)
            return;

        for (int i = 0; i < recipeItems.size(); i++) {
            RecipeItem recipeItem = recipeItems.get(i);
            if (!recipeItemMap.containsKey(recipeItem.getRecipeId())) {
                recipeItemMap.put(recipeItem.getRecipeId(), new ArrayList<RecipeItem>());
            }

            List<RecipeItem> recipeRecipeItems = recipeItemMap.get(recipeItem.getRecipeId());
            recipeRecipeItems.add(recipeItem);
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
            if (recipe.getClientName().equals(clientName)) {
                clientRecipes.add(recipe);
            }
        }

        return clientRecipes;
    }

    public List<RecipeItem> findRecipeItems(int recipeId) {
        if (recipeItemMap == null || recipeItemMap.size() <= 0)
            return new ArrayList<RecipeItem>();

        return recipeItemMap.get(recipeId);
    }

    private String getProductDescription(Product product) {

        Size size = findSize(product.getSizeId());
        if (size != null) {
            return product.getName() + " " + size.getName();
        }

        return "UNKNOWN SIZE with size id: " + product.getSizeId();
    }

    private String getRecipeDescription(Recipe recipe) {
        List<RecipeItem> recipeItems = recipe.getRecipeItems();
        if (recipeItems == null)
            return null;

        String result = "";
        for (int i = 0; i < recipeItems.size(); i++) {
            RecipeItem recipeItem = recipeItems.get(i);
            result += this.getProductDescription(this.findProduct(recipeItem.getProductId()));
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
