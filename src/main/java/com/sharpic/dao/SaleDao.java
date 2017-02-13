package com.sharpic.dao;

import com.sharpic.common.SharpICException;
import com.sharpic.domain.AuditRecipe;
import com.sharpic.domain.Sale;
import com.sharpic.domain.SaleMapper;
import com.sharpic.service.IObjectTransientFieldsPopulator;
import com.sharpic.service.IServerCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by joey on 2016-12-15.
 */

@Service
public class SaleDao {
    private static final int INSERT_BATCH_SIZE = 25;

    @Autowired
    private SaleMapper saleMapper;

    @Autowired
    private IServerCache serverCache;

    @Autowired
    private AuditRecipeDao auditRecipeDao;

    @Autowired
    private IObjectTransientFieldsPopulator objectTransientFieldsPopulator;

    public void deleteSales(String clientName, int auditId) {
        saleMapper.deleteSales(clientName, auditId);
        auditRecipeDao.deleteAuditRecipes(auditId);
    }

    public List<Sale> uploadSales(String clientName, int auditId, List<Sale> sales) throws SharpICException {
        deleteSales(clientName, auditId);

        if (sales == null)
            return null;

        for (int i = 0; i < sales.size(); i++) {
            Sale sale = sales.get(i);
            AuditRecipe auditRecipe = auditRecipeDao.getAuditRecipeByName(auditId, sale.getRecipe().getRecipeName());
            if (auditRecipe == null) {
                int newRecipeId = auditRecipeDao.createAuditRecipe(auditId, sale.getRecipe());
                auditRecipe = auditRecipeDao.getAuditRecipeByName(auditId, sale.getRecipe().getRecipeName());
            }
            sale.setRecipeId(auditRecipe.getId());
            sale.setRecipe(auditRecipe);

            saleMapper.insertSale(sale);
        }

        return getAuditSales(auditId);
    }

    public List<Sale> getAuditSales(int auditId) throws SharpICException {
        List<Sale> sales = saleMapper.getAuditSales(auditId);
        Map<Integer, AuditRecipe> auditRecipeMap = auditRecipeDao.getAuditRecipesMap(auditId);

        if (sales == null)
            return null;

        for (int i = 0; i < sales.size(); i++) {
            Sale sale = sales.get(i);

            AuditRecipe auditRecipe = auditRecipeMap.get(sale.getRecipeId());
            sale.setRecipe(auditRecipe);
            objectTransientFieldsPopulator.populateRecipeTransientFields(auditRecipe);
            if (auditRecipe == null)
                throw new SharpICException("Cannot find audit recipe with id: " + auditRecipe.getId());
        }

        Collections.sort(sales);

        return sales;
    }

    public void saveSale(Sale sale) throws SharpICException {
        if (sale.getRecipe() == null) {
            throw new SharpICException("Invalid recipe in Sale!");
        }

        if (!(sale.getRecipe() instanceof AuditRecipe)) {
            String recipeName = sale.getRecipe().getRecipeName();
            AuditRecipe auditRecipe = auditRecipeDao.getAuditRecipeByName(sale.getAuditId(), recipeName);

            if (auditRecipe == null) {
                sale.setRecipeId(auditRecipeDao.createAuditRecipe(sale.getAuditId(), sale.getRecipe()));
            } else {
                sale.setRecipeId(auditRecipe.getId());
            }
        }

        if (sale.getId() <= 0) {
            insertSale(sale);
        } else {
            updateSale(sale);
        }
    }

    public void insertSale(Sale sale) {
        saleMapper.insertSale(sale);
    }

    public void updateSale(Sale sale) {
        saleMapper.updateSale(sale);
    }

    public void deleteSale(int saleId) {
        saleMapper.deleteSale(saleId);
    }
}
