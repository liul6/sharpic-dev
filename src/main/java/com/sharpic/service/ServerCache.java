package com.sharpic.service;

import com.sharpic.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by joey on 2016-12-08.
 */

@Service
@ManagedResource
public class ServerCache implements IServerCache {
    @Autowired
    private SizeMapper sizeMapper;

    @Autowired
    private ProductMapper productMapper;

    private Map<Integer, Size> sizeMap = new ConcurrentHashMap<Integer, Size>();
    private Map<Integer, Product> productMap = new ConcurrentHashMap<Integer, Product>();

    @ManagedOperation
    public void reloadCache() {
        sizeMap.clear();
        productMap.clear();

        this.fillSizeCache();
        this.fillProductCache();
    }

    @Override
    public Size findSize(int sizeId) {
        if (sizeMap.isEmpty())
            this.fillSizeCache();

        return sizeMap.get(sizeId);
    }

    @Override
    public Product findProduct(int productId) {
        if (productMap.isEmpty())
            this.fillProductCache();

        return productMap.get(productId);
    }

    public void clearSizeCache() {
        sizeMap.clear();
    }

    public void clearProductCache() {
        productMap.clear();
    }

    private void fillSizeCache() {
        sizeMap.clear();

        List<Size> sizes = sizeMapper.getSizes();
        if (sizes == null)
            return;

        for (int i = 0; i < sizes.size(); i++) {
            Size size = sizes.get(i);
            sizeMap.put(size.getId(), size);
        }
    }

    private void fillProductCache() {
        productMap.clear();

        List<Product> products = productMapper.getProducts();
        if (products == null)
            return;

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            productMap.put(product.getId(), product);
        }
    }

}
