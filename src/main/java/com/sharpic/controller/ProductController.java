package com.sharpic.controller;

import com.sharpic.dao.ClientProductDao;
import com.sharpic.dao.ProductDao;
import com.sharpic.domain.ClientProduct;
import com.sharpic.domain.Product;
import com.sharpic.domain.Size;
import com.sharpic.service.IObjectTransientFieldsPopulator;
import com.sharpic.service.IServerCache;
import com.sharpic.web.SharpICResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @Autowired
    private ProductDao productDao;

    @Autowired
    private IObjectTransientFieldsPopulator transientFieldsPopulator;

    @RequestMapping(value = "/product/getProducts")
    @ResponseBody
    public List<Product> getProducts() {
        List<Product> products = serverCache.getProducts();
        transientFieldsPopulator.populateProductTransientFields(products);

        Collections.sort(products);
        return products;
    }

    @RequestMapping(value = "/product/getClientProducts")
    @ResponseBody
    public List<ClientProduct> getClientProducts(String clientName) {
        List<ClientProduct> clientProducts = clientProductDao.getClientProducts(clientName);
        transientFieldsPopulator.populateClientProductTransientFields(clientProducts);


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

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(method = RequestMethod.POST, value = "/product/deleteProduct", consumes = "application/json")
    @ResponseBody
    public SharpICResponse deleteProduct(@RequestBody Product product) {
        SharpICResponse sharpICResponse = new SharpICResponse();
        if (product.getId() <= 0) {
            sharpICResponse.setSuccessful(true);
            return sharpICResponse;
        }
        try {
            productDao.deleteProduct(product.getId());
            sharpICResponse.setSuccessful(true);
        } catch (Exception e) {
            sharpICResponse.setSuccessful(false);
            sharpICResponse.setErrorText(e.getMessage());
        }

        return sharpICResponse;
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(method = RequestMethod.POST, value = "/product/deleteClientProduct", consumes = "application/json")
    @ResponseBody
    public SharpICResponse deleteClientProduct(@RequestBody ClientProduct clientProduct) {
        SharpICResponse sharpICResponse = new SharpICResponse();
        if (clientProduct.getId() <= 0) {
            sharpICResponse.setSuccessful(true);
            return sharpICResponse;
        }
        try {
            clientProductDao.deleteClientProduct(clientProduct.getId());
            sharpICResponse.setSuccessful(true);
        } catch (Exception e) {
            sharpICResponse.setSuccessful(false);
            sharpICResponse.setErrorText(e.getMessage());
        }

        return sharpICResponse;
    }


    @PreAuthorize("hasRole('USER')")
    @RequestMapping(method = RequestMethod.POST, value = "/product/deleteSize", consumes = "application/json")
    @ResponseBody
    public SharpICResponse deleteSize(@RequestBody Size size) {
        SharpICResponse sharpICResponse = new SharpICResponse();
        if (size.getId() <= 0) {
            sharpICResponse.setSuccessful(true);
            return sharpICResponse;
        }
        try {
            clientProductDao.deleteSize(size.getId());
            sharpICResponse.setSuccessful(true);
        } catch (Exception e) {
            sharpICResponse.setSuccessful(false);
            sharpICResponse.setErrorText(e.getMessage());
        }

        return sharpICResponse;
    }
}