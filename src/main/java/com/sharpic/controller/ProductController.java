package com.sharpic.controller;

import com.sharpic.dao.ClientProductDao;
import com.sharpic.domain.ClientProduct;
import com.sharpic.domain.Product;
import com.sharpic.domain.Size;
import com.sharpic.service.IServerCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;

/**
 * Created by joey on 2016-12-11.
 */

@Controller
public class ProductController {
    private static final int PAGE_SIZE = 500;

    @Autowired
    private IServerCache serverCache;

    @Autowired
    private ClientProductDao clientProductDao;

    @RequestMapping(value = "/product/getProducts")
    @ResponseBody
    public List<Product> getProducts() {
        List<Product> products = serverCache.getProducts();

        if (products != null && products.size() > 0) {
            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                product.setSize(serverCache.findSize(product.getSizeId()));
            }
        }

        Collections.sort(products);
        return products;
    }

    @RequestMapping(value = "/product/getProductsByPage")
    @ResponseBody
    public List<Product> getProductsByPage(int pageNo) {
        List<Product> products = serverCache.getProducts();
        if (pageNo < 1 || pageNo > (products.size() / PAGE_SIZE + 1))
            return null;

        if (products != null && products.size() > 0) {
            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                product.setSize(serverCache.findSize(product.getSizeId()));
            }
        }

        Collections.sort(products);

        return products.subList((pageNo - 1) * PAGE_SIZE, Math.min(products.size(), pageNo * PAGE_SIZE));
    }

    @RequestMapping(value = "/product/getClientProducts")
    @ResponseBody
    public List<ClientProduct> getClientProducts(String clientName) {
        List<ClientProduct> clientProducts = clientProductDao.getClientProducts(clientName);

        Collections.sort(clientProducts);
        return clientProducts;
    }

    @RequestMapping(value = "/product/getSizes")
    @ResponseBody
    public List<Size> getSizes() {
        List<Size> sizes = serverCache.getSizes();

        Collections.sort(sizes);
        return sizes;
    }
}
