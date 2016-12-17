package com.sharpic.dao;

import com.sharpic.domain.Entry;
import com.sharpic.domain.EntryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by joey on 2016-12-17.
 */

@Service
public class EntryDao {
    @Autowired
    private EntryMapper entryMapper;

    public List<Entry> getAuditEntries(int auditId) {
        return entryMapper.getAuditEntries(auditId);
    }

    public void deleteAuditEntries(int auditId) {
        if (auditId > 0)
            deleteAuditEntries(auditId);
    }
}
