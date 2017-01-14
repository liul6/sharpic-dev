package com.sharpic.controller;

import com.sharpic.common.DateUtil;
import com.sharpic.dao.AuditDao;
import com.sharpic.dao.AuditRecipeDao;
import com.sharpic.dao.RecipeDao;
import com.sharpic.dao.SaleDao;
import com.sharpic.domain.AuditRecipe;
import com.sharpic.domain.Recipe;
import com.sharpic.domain.Sale;
import com.sharpic.service.IObjectTransientFieldsPopulator;
import com.sharpic.service.IServerCache;
import com.sharpic.web.SharpICResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
    private AuditRecipeDao auditRecipeDao;

    @Autowired
    private IServerCache serverCache;

    @Autowired
    private IObjectTransientFieldsPopulator objectTransientFieldsPopulator;

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
}
