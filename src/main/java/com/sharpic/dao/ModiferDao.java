package com.sharpic.dao;

import com.sharpic.domain.ModifierItem;
import com.sharpic.domain.ModifierItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by joey 2017-02-05.
 */
@Service
public class ModiferDao {
    @Autowired
    private ModifierItemMapper modifierItemMapper;

    List<ModifierItem> getModifierItems(int auditId) {
        return modifierItemMapper.getAuditModifierItems(auditId);
    }
}
