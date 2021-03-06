package com.sharpic.dao;

import com.sharpic.domain.Audit;
import com.sharpic.domain.AuditMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by joey on 2016-12-17.
 */

@Service
public class AuditDao {
    @Autowired
    private AuditMapper auditMapper;

    @Autowired
    private SaleDao saleDao;

    @Autowired
    private EntryDao entryDao;

    public List<Audit> getClientAudits(String clientName) {
        return auditMapper.getClientAudits(clientName);
    }

    public Integer getAuditId(@Param("clientName") String clientName, @Param("auditDate") Date auditDate) {
        return auditMapper.getAuditId(clientName, auditDate);
    }

    public Audit getAudit(Integer auditId) {
        return auditMapper.getAudit(auditId);
    }

    public void insertAudit(@Param("clientName") String clientName, @Param("auditDate") Date auditDate) {
        auditMapper.insertAudit(clientName, auditDate);
    }

    public void deleteAudit(@Param("clientName") String clientName, @Param("auditDate") Date auditDate) {
        int auditId = auditMapper.getAuditId(clientName, auditDate);
        saleDao.deleteSales(clientName, auditId);
        entryDao.deleteAuditEntries(auditId);
        auditMapper.deleteAudit(clientName, auditDate);
    }
}
