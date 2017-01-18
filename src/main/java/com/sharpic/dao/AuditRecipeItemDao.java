package com.sharpic.dao;

import com.sharpic.domain.AuditRecipeItem;
import com.sharpic.domain.AuditRecipeItemMapper;
import com.sharpic.domain.ClientProduct;
import com.sharpic.domain.RecipeItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by joey on 2017-01-07.
 */
@Service
public class AuditRecipeItemDao {
    @Autowired
    private AuditRecipeItemMapper auditRecipeItemMapper;
    @Autowired
    private ClientProductDao clientProductDao;

    public List<RecipeItem> getAuditRecipeItems(int auditId) {
        List<RecipeItem> auditRecipeItems = auditRecipeItemMapper.getAuditRecipeItems(auditId);
        this.normalizeAuditRecipeItems(auditRecipeItems);

        return auditRecipeItems;
    }

    public List<RecipeItem> getAuditRecipeItemsByRecipeId(int auditRecipeId) {
        List<RecipeItem> auditRecipeItems = auditRecipeItemMapper.getAuditRecipeItemsByRecipeId(auditRecipeId);
        this.normalizeAuditRecipeItems(auditRecipeItems);

        return auditRecipeItems;
    }

    public void deleteAuditRecipeItems(int auditId) {
        auditRecipeItemMapper.deleteAuditRecipeItems(auditId);
    }

    public void deleteRecipeItemsByRecipeId(int recipeId) {
        auditRecipeItemMapper.deleteRecipeItemsByRecipeId(recipeId);
    }

    public void insertAuditRecipeItem(AuditRecipeItem auditRecipeItem) {
        auditRecipeItemMapper.insertAuditRecipeItem(auditRecipeItem);
    }

    private List<RecipeItem> normalizeAuditRecipeItems(List<RecipeItem> auditRecipeItems) {
        Map<Integer, ClientProduct> clientProductMap = clientProductDao.getClientProducts(auditRecipeItems);

        if (auditRecipeItems != null) {
            for (int i = 0; i < auditRecipeItems.size(); i++) {
                RecipeItem auditRecipeItem = auditRecipeItems.get(i);
                auditRecipeItem.setClientProduct(clientProductMap.get(auditRecipeItem.getProductId()));
            }
        }
        return auditRecipeItems;
    }
}
