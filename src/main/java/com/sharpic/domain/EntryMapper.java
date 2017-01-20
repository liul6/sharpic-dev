package com.sharpic.domain;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by joey on 2016-12-08.
 */

@Mapper
public interface EntryMapper {
    public List<Entry> getAuditEntries(int auditId);

    public void deleteAuditEntries(int auditId);

    public void deleteAuditEntry(int entryId);

    public int getNumberOfEntriesByProductId(int productId);
}
