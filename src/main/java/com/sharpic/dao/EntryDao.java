package com.sharpic.dao;

import com.sharpic.domain.ClientProduct;
import com.sharpic.domain.Entry;
import com.sharpic.domain.EntryMapper;
import com.sharpic.service.IObjectTransientFieldsPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private IObjectTransientFieldsPopulator objectTransientFieldsPopulator;

    public List<Entry> getAuditEntries(int auditId) {
        List<Entry> entries = entryMapper.getAuditEntries(auditId);
        List<Integer> clientProductIds = new ArrayList<Integer>();

        if (entries != null) {
            for (int i = 0; i < entries.size(); i++) {
                Entry entry = entries.get(i);
                clientProductIds.add(entry.getProductId());
            }

            Map<Integer, ClientProduct> clientProductMap = clientProductDao.getClientProductsWithIdsMap(clientProductIds);

            for (int i = 0; i < entries.size(); i++) {
                Entry entry = entries.get(i);
                entry.setClientProduct(clientProductMap.get(entry.getProductId()));
                objectTransientFieldsPopulator.populateProductTransientFields(entry.getClientProduct());
            }
        }

        return entries;
    }

    public void deleteAuditEntries(int auditId) {
        if (auditId > 0)
            entryMapper.deleteAuditEntries(auditId);
    }
}
