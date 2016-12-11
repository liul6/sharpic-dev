package com.sharpic.domain;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface AuditMapper {
    List<Audit> getClientAudits(String clientName);

    Integer getAuditId(@Param("clientName") String clientName, @Param("auditDate") Date auditDate);

    void insertAudit(@Param("clientName") String clientName, @Param("auditDate") Date auditDate);

    void deleteAudit(@Param("clientName") String clientName, @Param("auditDate") Date auditDate);
}