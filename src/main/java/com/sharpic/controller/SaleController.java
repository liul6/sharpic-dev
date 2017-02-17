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
import java.util.Collections;
import java.util.List;

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
    private ClientDao clientDao;

    @Autowired
    private ClientController clientController;

    @Autowired
    private ProductController productController;

    @RequestMapping(value = "/sale/getAuditInfo")
    @ResponseBody
    public SharpICResponse getAuditInfo(String clientName, String auditDateStr) {
        LocalDate auditDate = LocalDate.parse(auditDateStr);

        if (auditDate == null)
            auditDate = LocalDate.now();

        SharpICResponse response = new SharpICResponse();

        int auditId = audiDao.getAuditId(clientName, DateUtil.toDate(auditDate));
        if (auditId < 0)
            return response;

        try {
            List<Sale> auditSales = saleDao.getAuditSales(auditId);
            Collections.sort(auditSales);

            response.addToModel("audit", audiDao.getAudit(auditId));
            response.addToModel("sales", auditSales);
            response.addToModel("recipes", clientDao.getClientApplicableRecipes(clientName, auditId));
            response.addToModel("products", productController.getProducts());
            response.setSuccessful(true);
        } catch (Exception e) {
            log.error(e);
            response.setSuccessful(false);
        }

        return response;
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(method = RequestMethod.POST, value = "/sale/saveAuditRecipe", consumes = "application/json")
    @ResponseBody
    public SharpICResponse saveAuditRecipe(@RequestBody AuditRecipe auditRecipe) {
        SharpICResponse response = new SharpICResponse();
        try {

            if (auditRecipe.getId() <= 0) {
                auditRecipe = auditRecipeDao.createAuditRecipe(auditRecipe);
            }

            auditRecipeItemDao.deleteRecipeItemsByRecipeId(auditRecipe.getId());
            List<RecipeItem> recipeItems = auditRecipe.getRecipeItems();

            if (recipeItems != null) {
                for (int i = 0; i < recipeItems.size(); i++) {
                    RecipeItem recipeItem = recipeItems.get(i);
                    recipeItem.setProduct(serverCache.findProduct(recipeItem.getProductId()));

                    AuditRecipeItem auditRecipeItem = new AuditRecipeItem(auditRecipe.getAuditId(), auditRecipe.getId(), recipeItem);

                    auditRecipeItemDao.insertAuditRecipeItem(auditRecipeItem);
                }
            }

            auditRecipe = auditRecipeDao.getAuditRecipe(auditRecipe.getId());
            objectTransientFieldsPopulator.populateRecipeTransientFields(auditRecipe);
            response.setSuccessful(true);
            response.addToModel("auditRecipe", auditRecipe);
        } catch (Exception e) {
            response.setSuccessful(false);
            response.setErrorText(e.getMessage());
        }

        return response;
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(method = RequestMethod.POST, value = "/sale/saveRecipeFully", consumes = "application/json")
    @ResponseBody
    public SharpICResponse saveRecipeFully(@RequestBody AuditRecipe auditRecipe) {
        SharpICResponse sharpICResponse = new SharpICResponse();

        try {
            SharpICResponse sharpICResponse1 = saveAuditRecipe(auditRecipe);
            if (!sharpICResponse1.isSuccessful())
                return sharpICResponse1;

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

            SharpICResponse sharpICResponse2 = clientController.saveRecipe(recipe);
            if (!sharpICResponse2.isSuccessful())
                return sharpICResponse2;

            sharpICResponse.setSuccessful(true);
            sharpICResponse.addToModel("recipe", sharpICResponse.getModel().get("recipe"));
            sharpICResponse.addToModel("auditRecipe", sharpICResponse.getModel().get("auditRecipe"));
        } catch (Exception e) {
            sharpICResponse.setSuccessful(false);
            sharpICResponse.setErrorText(e.getMessage());
        }

        return sharpICResponse;
    }


    @PreAuthorize("hasRole('USER')")
    @RequestMapping(method = RequestMethod.POST, value = "/sale/saveSale", consumes = "application/json")
    @ResponseBody
    public SharpICResponse saveSale(@RequestBody Sale sale) {
        SharpICResponse sharpICResponse = new SharpICResponse();
        try {
            saleDao.saveSale(sale);
            sharpICResponse.setSuccessful(true);
            return sharpICResponse;
        } catch (Exception e) {
            sharpICResponse.setErrorText(e.getMessage());
            sharpICResponse.setSuccessful(false);

            return sharpICResponse;
        }
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(method = RequestMethod.POST, value = "/sale/deleteSale", consumes = "application/json")
    @ResponseBody
    public SharpICResponse deleteSale(@RequestBody Sale sale) {
        SharpICResponse sharpICResponse = new SharpICResponse();
        if (sale.getId() <= 0) {
            sharpICResponse.setSuccessful(true);
            return sharpICResponse;
        }

        try {
            saleDao.deleteSale(sale.getId());
            sharpICResponse.setSuccessful(true);
        } catch (Exception e) {
            sharpICResponse.setErrorText(e.getMessage());
            sharpICResponse.setSuccessful(false);
        }
        return sharpICResponse;
    }
}
