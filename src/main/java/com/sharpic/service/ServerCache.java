package com.sharpic.service;

import com.sharpic.domain.Product;
import com.sharpic.domain.ProductMapper;
import com.sharpic.domain.Size;
import com.sharpic.domain.SizeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<Product> getProducts() {
        if (productMap != null || productMap.size() <= 0)
            this.fillProductCache();

        List<Product> allProducts = new ArrayList<Product>();
        allProducts.addAll(productMap.values());

        return allProducts;
    }

    public List<Size> getSizes() {
        if (sizeMap != null || sizeMap.size() <= 0)
            this.fillSizeCache();

        List<Size> allSizes = new ArrayList<Size>();
        allSizes.addAll(sizeMap.values());

        return allSizes;
    }
}
