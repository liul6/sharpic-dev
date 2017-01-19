package com.sharpic.domain;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by joey on 2016-12-13.
 */

@Mapper
public interface SaleMapper {
    public List<Sale> getAuditSales(int auditId);

    public void insertSales(@Param("sales") List<Sale> sales);

    public void insertSale(@Param("sale") Sale sale);

    public void deleteSales(@Param("clientName") String clientName, @Param("auditId") int auditId);

    public void deleteSale(@Param("saleId") int saleId);

}
