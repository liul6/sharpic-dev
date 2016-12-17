package com.sharpic.dao;

import com.sharpic.domain.AuditMapper;
import com.sharpic.domain.SaleMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by joey on 2016-12-17.
 */

@Service
public class AuditDao {
    @Autowired
    private AuditMapper auditMapper;

    @Autowired
    private SaleMapper saleMapper;

    public Integer getAuditId(@Param("clientName") String clientName, @Param("auditDate") Date auditDate) {
        return auditMapper.getAuditId(clientName, auditDate);
    }

    public void insertAudit(@Param("clientName") String clientName, @Param("auditDate") Date auditDate) {
        auditMapper.insertAudit(clientName, auditDate);
    }

    public void deleteAudit(@Param("clientName") String clientName, @Param("auditDate") Date auditDate) {
        int auditId = auditMapper.getAuditId(clientName, auditDate);
        saleMapper.deleteSales(clientName, auditId);
        auditMapper.deleteAudit(clientName, auditDate);
    }
}
