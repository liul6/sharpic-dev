package com.sharpic.controller;

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
    @Autowired
    private IServerCache serverCache;

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

    @RequestMapping(value = "/product/getSizes")
    @ResponseBody
    public List<Size> getSizes() {
        List<Size> sizes = serverCache.getSizes();

        Collections.sort(sizes);
        return sizes;
    }
}
