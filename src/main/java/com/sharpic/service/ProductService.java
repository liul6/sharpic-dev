package com.sharpic.service;

import com.sharpic.domain.Product;
import com.sharpic.domain.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by joey on 2016-12-09.
 */

@Service
public class ProductService {
    @Autowired
    private ServerCache serverCache;

    public Product getProduct(int productId) {
        return serverCache.findProduct(productId);
    }

    public String getProductDescription(Product product) {
        Size size = serverCache.findSize(product.getSizeId());

        return product.getName() + " " + size.getName();
    }
}
