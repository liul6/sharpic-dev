package com.sharpic.dao;

import com.sharpic.domain.ModifierItem;
import com.sharpic.domain.ModifierItemMapper;
import com.sharpic.domain.ModifierMapper;
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
                modifierItem.setProduct(serverCache.findProduct(modifierItem.getProductId()));
            }

            if (modifierItem.getRecipeId() > 0) {
                modifierItem.setRecipe(serverCache.findRecipeById(modifierItem.getRecipeId()));
            }
        }

        return modifierItems;
    }


}
