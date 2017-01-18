package com.sharpic.controller;

import com.sharpic.common.DateUtil;
import com.sharpic.dao.*;
import com.sharpic.domain.*;
import com.sharpic.service.IObjectTransientFieldsPopulator;
import com.sharpic.service.IServerCache;
import com.sharpic.web.SharpICResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by joey on 2016-12-13.
 */

@Controller
public class SaleController {
    private static Log log = LogFactory.getLog(SaleController.class.getName());

    @Autowired
    private SaleDao saleDao;

    @Autowired
    private AuditDao audiDao;

    @Autowired
    private RecipeDao recipeDao;

    @Autowired
    private AuditRecipeItemDao auditRecipeItemDao;

    @Autowired
    private AuditRecipeDao auditRecipeDao;

    @Autowired
    private IServerCache serverCache;

    @Autowired
    private IObjectTransientFieldsPopulator objectTransientFieldsPopulator;

    @Autowired
    private ClientProductDao clientProductDao;

    @Autowired
    private ClientController clientController;

    @RequestMapping(value = "/sale/getSales")
    @ResponseBody
    public SharpICResponse getSales(String clientName, String auditDateStr) {
        LocalDate auditDate = LocalDate.parse(auditDateStr);

        if (auditDate == null)
            auditDate = LocalDate.now();

        SharpICResponse response = new SharpICResponse();

        int auditId = audiDao.getAuditId(clientName, DateUtil.toDate(auditDate));
        if (auditId < 0)
            return response;

        try {
            List<Sale> auditSales = saleDao.getAuditSales(auditId);
            response.addToModel("sales", auditSales);
            response.addToModel("recipes", getClientApplicableRecipes(clientName, auditId));
            response.addToModel("clientProducts", clientController.getClientProducts(clientName));
            response.setSuccessful(true);
        } catch (Exception e) {
            log.error(e);
            response.setSuccessful(false);
        }

        return response;
    }

    private List<Recipe> getClientApplicableRecipes(String clientName, int auditId) {
        Map<Integer, AuditRecipe> clientAuditRecipeMap = auditRecipeDao.getAuditRecipesMap(auditId);
        List<Recipe> clientRecipes = serverCache.getRecipes(clientName);

        List<Recipe> clientAppicableRecipes = new ArrayList<Recipe>();
        for (int i = 0; i < clientRecipes.size(); i++) {
            Recipe recipe = clientRecipes.get(i);

            if (clientAuditRecipeMap.containsKey(recipe.getRecipeName()))
                recipe = clientAuditRecipeMap.get(recipe.getRecipeName());
            clientAppicableRecipes.add(recipe);
        }

        objectTransientFieldsPopulator.populateRecipeTransientFields(clientAppicableRecipes);
        return clientAppicableRecipes;
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(method = RequestMethod.POST, value = "/sale/saveAuditRecipe", consumes = "application/json")
    @ResponseBody
    public Recipe saveAuditRecipe(@RequestBody AuditRecipe auditRecipe) {
        if (auditRecipe.getId() <= 0) {
            auditRecipe = auditRecipeDao.createAuditRecipe(auditRecipe);
        }

        auditRecipeItemDao.deleteRecipeItemsByRecipeId(auditRecipe.getId());
        List<RecipeItem> recipeItems = auditRecipe.getRecipeItems();

        if (recipeItems != null) {
            for (int i = 0; i < recipeItems.size(); i++) {
                RecipeItem recipeItem = recipeItems.get(i);
                recipeItem.setClientProduct(clientProductDao.getClientProduct(recipeItem.getProductId()));

                AuditRecipeItem auditRecipeItem = new AuditRecipeItem(auditRecipe.getAuditId(), auditRecipe.getId(), recipeItem);

                auditRecipeItemDao.insertAuditRecipeItem(auditRecipeItem);
            }
        }

        auditRecipe = auditRecipeDao.getAuditRecipe(auditRecipe.getId());
        objectTransientFieldsPopulator.populateRecipeTransientFields(auditRecipe);

        return auditRecipe;
    }


    @PreAuthorize("hasRole('USER')")
    @RequestMapping(method = RequestMethod.POST, value = "/sale/saveRecipeFully", consumes = "application/json")
    @ResponseBody
    public Recipe saveRecipeFully(@RequestBody AuditRecipe auditRecipe) {
        saveAuditRecipe(auditRecipe);

        Recipe recipe = recipeDao.getRecipeByName(auditRecipe.getClientName(), auditRecipe.getRecipeName());

        if (auditRecipe.getRecipeItems() != null) {
            for (int i = 0; i < auditRecipe.getRecipeItems().size(); i++) {
                RecipeItem recipeItem = auditRecipe.getRecipeItems().get(i);
                recipeItem.setRecipeId(recipe.getId());
            }
            recipe.setRecipeItems(auditRecipe.getRecipeItems());
        }

        if (recipe.getId() <= 0) {
            recipe = recipeDao.createRecipe(recipe);
        }

        return clientController.saveRecipe(recipe);
    }

}
