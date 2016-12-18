package com.sharpic.domain;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by joey on 2016-12-17.
 */

@Mapper
public interface ModifierItemMapper {
    List<ModifierItem> getAuditModifierItems(int auditId);
}
