package com.sharpic.dao;

import com.sharpic.common.SharpICException;
import com.sharpic.domain.AuditRecipe;
import com.sharpic.domain.Sale;
import com.sharpic.domain.SaleMapper;
import com.sharpic.service.IServerCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private RecipeDao recipeDao;

    public List<Sale> uploadSales(String clientName, int auditId, List<Sale> sales) throws SharpICException {
        saleMapper.deleteSales(clientName, auditId);
        recipeDao.deleteAuditRecipes(auditId);

        if (sales == null)
            return null;

        for(int i=0; i<sales.size(); i++) {
            Sale sale = sales.get(i);

            int newRecipeId = recipeDao.createAuditRecipe(auditId, sale.getRecipe());
            sale.setRecipeId(newRecipeId);

            saleMapper.insertSale(sale);
        }

//        List<Sale> tempSales = new ArrayList<Sale>();
//        for (int i = 0; i < sales.size(); i++) {
//            tempSales.add(sales.get(i));
//            if ((i % INSERT_BATCH_SIZE == 0 && i != 0) || (i == sales.size() - 1)) {
//                saleMapper.insertSales(tempSales);
//                tempSales.clear();
//            }
//        }

        return getAuditSales(auditId);
    }

    public List<Sale> getAuditSales(int auditId) throws SharpICException {
        List<Sale> sales = saleMapper.getAuditSales(auditId);
        Map<Integer, AuditRecipe> auditRecipeMap = recipeDao.getAuditRecipesMap(auditId);

        if (sales == null)
            return null;

        for (int i = 0; i < sales.size(); i++) {
            Sale sale = sales.get(i);

            AuditRecipe auditRecipe = auditRecipeMap.get(sale.getRecipeId());
            sale.setRecipe(auditRecipe);
            if (auditRecipe == null)
                throw new SharpICException("Cannot find audit recipe with id: " + auditRecipe.getId());
        }

        return sales;
    }
}
