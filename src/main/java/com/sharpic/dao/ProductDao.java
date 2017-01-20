package com.sharpic.dao;

import com.sharpic.common.SharpICException;
import com.sharpic.domain.ClientProduct;
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
    private ClientProductDao clientProductDao;

    @Autowired
    private IObjectTransientFieldsPopulator objectDescriptor;

    public List<Product> getProducts() {
        List<Product> products = productMapper.getProducts();

        if (products == null)
            return products;

        return products;
    }

    public void deleteProduct(int productId) throws SharpICException {
        List<ClientProduct> linkedClientProducts = clientProductDao.getLinkedClientProducts(productId);
        if (linkedClientProducts != null && linkedClientProducts.size() > 0)
            throw new SharpICException("The product is linked with some clients already, need to delete the client specific product setup first");

        productMapper.deleteProduct(productId);
    }
}
