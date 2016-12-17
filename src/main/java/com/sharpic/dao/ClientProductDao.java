package com.sharpic.dao;

import com.sharpic.domain.ClientProduct;
import com.sharpic.domain.ClientProductMapper;
import com.sharpic.domain.Size;
import com.sharpic.service.IServerCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by joey on 2016-12-17.
 */

@Service
public class ClientProductDao {
    @Autowired
    private ClientProductMapper clientProductMapper;

    @Autowired
    private IServerCache serverCache;

    public List<ClientProduct> getClientProducts(String clientName) {
        List<ClientProduct> clientProducts = clientProductMapper.getClientProducts(clientName);
        if(clientProducts==null)
            return null;

        for(int i=0; i<clientProducts.size(); i++) {
            ClientProduct clientProduct = clientProducts.get(i);
            if(clientProduct.getSizeId()>0) {
                Size size = serverCache.findSize(clientProduct.getSizeId());
                clientProduct.setSize(size);
            }
        }

        return clientProducts;
    }
}
