package com.sharpic.domain;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by joey on 2016-12-17.
 */

@Mapper
public interface ClientProductMapper {
    List<ClientProduct> getClientProducts(String clientName);

    ClientProduct getClientProduct(int productId);

    List<ClientProduct> getLinkedClientProducts(int parentProductId);

    void deleteClientProduct(int productId);

    int getNumberOfClientProductsBySizeId(int sizeId);
}
