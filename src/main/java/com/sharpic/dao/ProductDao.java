package com.sharpic.dao;

import com.sharpic.common.Util;
import com.sharpic.domain.Product;
import com.sharpic.domain.ProductMapper;
import com.sharpic.service.IObjectDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by joey on 2017-01-07.
 */
@Service
public class ProductDao {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private IObjectDescriptor objectDescriptor;


    public List<Product> getProducts() {
        List<Product> products = productMapper.getProducts();

        if (products == null)
            return products;

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            if (Util.isValidName(product.getName())) {
                product.setDescription(objectDescriptor.getDescription(product));
            }
        }

        return products;
    }
}
