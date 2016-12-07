package com.sharpic.domain;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AuditMapper {
    List<Audit> getClientAudits(String clientName);
}