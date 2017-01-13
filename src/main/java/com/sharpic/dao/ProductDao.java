package com.sharpic.dao;

import com.sharpic.domain.Product;
import com.sharpic.domain.ProductMapper;
import com.sharpic.service.IObjectTransientFieldsPopulator;
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
    private IObjectTransientFieldsPopulator objectDescriptor;


    public List<Product> getProducts() {
        List<Product> products = productMapper.getProducts();

        if (products == null)
            return products;

        return products;
    }
}
