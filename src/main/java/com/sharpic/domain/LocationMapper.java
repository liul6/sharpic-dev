package com.sharpic.domain;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LocationMapper {
    List<Location> getClientLocationsByAuditId(int auditId);
    List<Location> getClientLocations(String clientName);
    List<Location> getLocations();
}