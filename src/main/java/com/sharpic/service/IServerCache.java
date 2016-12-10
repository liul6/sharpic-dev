package com.sharpic.service;

import com.sharpic.domain.Product;
import com.sharpic.domain.Size;

/**
 * Created by joey on 2016-12-08.
 */
public interface IServerCache {
    Size findSize(int sizeId);
    Product findProduct(int productId);


}
