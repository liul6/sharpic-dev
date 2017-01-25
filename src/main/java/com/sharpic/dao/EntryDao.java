package com.sharpic.dao;

import com.sharpic.domain.Entry;
import com.sharpic.domain.EntryMapper;
import com.sharpic.domain.Location;
import com.sharpic.service.IObjectTransientFieldsPopulator;
import com.sharpic.service.IServerCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by joey on 2016-12-17.
 */

@Service
public class EntryDao {
    @Autowired
    private EntryMapper entryMapper;

    @Autowired
    private ClientProductDao clientProductDao;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private IServerCache serverCache;

    @Autowired
    private IObjectTransientFieldsPopulator objectTransientFieldsPopulator;

    public List<Entry> getAuditEntries(int auditId) {
        List<Entry> entries = entryMapper.getAuditEntries(auditId);

        if (entries != null) {
            Map<String, Location> clientLocationMap = clientDao.getLocationMap(auditId);

            for (int i = 0; i < entries.size(); i++) {
                Entry entry = entries.get(i);
                entry.setProduct(serverCache.findProduct(entry.getProductId()));
                objectTransientFieldsPopulator.populateProductTransientFields(entry.getProduct());
                entry.setClientLocation(clientLocationMap.get(entry.getLocation()));
            }
        }

        return entries;
    }

    public void deleteAuditEntries(int auditId) {
        if (auditId > 0)
            entryMapper.deleteAuditEntries(auditId);
    }

    public void deleteAuditEnty(int entrytId) {
        entryMapper.deleteAuditEntry(entrytId);
    }

    public int getNumberOfEntriesByProductId(int productId) {
        return entryMapper.getNumberOfEntriesByProductId(productId);
    }

    public void updateAuditEntry(Entry entry) {
        if (entry.getId() > 0)
            entryMapper.updateAuditEntry(entry);
        else
            entryMapper.insertAuditEntry(entry);
    }
}