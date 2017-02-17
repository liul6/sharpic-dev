package com.sharpic.domain;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    public void insertAuditEntry(@Param("entry") Entry entry);

    public void updateAuditEntry(@Param("entry") Entry entry);

    public int getMaxEntryId(@Param("auditId") int auditId, @Param("productId") int productId);

    public Entry getEntry(@Param("id") int id);
}
