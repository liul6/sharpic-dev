package com.sharpic.dao;

import com.sharpic.domain.*;
import com.sharpic.service.IObjectTransientFieldsPopulator;
import com.sharpic.service.IServerCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by joey on 2016-12-17.
 */

@Service
public class ModifierDao {
    @Autowired
    private ModifierMapper modifierMapper;

    @Autowired
    private ModifierItemMapper modifierItemMapper;

    @Autowired
    private IServerCache serverCache;

    @Autowired
    private AuditRecipeDao auditRecipeDao;

    @Autowired
    private IObjectTransientFieldsPopulator objectTransientFieldsPopulator;

    public List<ModifierItem> getAuditModifierItems(int auditId) {
        List<ModifierItem> modifierItems = modifierItemMapper.getAuditModifierItems(auditId);

        if (modifierItems == null)
            return null;

        for (int i = 0; i < modifierItems.size(); i++) {
            ModifierItem modifierItem = modifierItems.get(i);

            if (modifierItem.getModifierId() > 0) {
                modifierItem.setModifier(modifierMapper.getModifier(modifierItem.getModifierId()));
            }

            if (modifierItem.getProductId() > 0) {
                Product product = serverCache.findProduct(modifierItem.getProductId());
                modifierItem.setProduct(product);
                if (product != null)
                    objectTransientFieldsPopulator.populateProductTransientFields(product);
            }

            if (modifierItem.getRecipeId() > 0) {
                Recipe recipe = auditRecipeDao.getAuditRecipe(modifierItem.getRecipeId());
                modifierItem.setRecipe(recipe);
                if (recipe != null)
                    objectTransientFieldsPopulator.populateRecipeTransientFields(recipe);
            }
        }

        return modifierItems;
    }


}
