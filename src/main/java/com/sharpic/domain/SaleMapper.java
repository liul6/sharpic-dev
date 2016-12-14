package com.sharpic.domain;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by joey on 2016-12-13.
 */

@Mapper
public interface SaleMapper {
    public List<Sale> getAuditSales(int auditId);

}
