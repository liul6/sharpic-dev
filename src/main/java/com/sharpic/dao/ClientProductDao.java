package com.sharpic.dao;

import com.sharpic.domain.ClientProduct;
import com.sharpic.domain.ClientProductMapper;
import com.sharpic.domain.RecipeItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by joey on 2016-12-17.
 */

@Service
public class ClientProductDao {
    @Autowired
    private ClientProductMapper clientProductMapper;

    public List<ClientProduct> getClientProducts(String clientName) {
        List<ClientProduct> clientProducts = clientProductMapper.getClientProducts(clientName);
        if (clientProducts == null)
            return null;

        return clientProducts;
    }

    public Map<Integer, ClientProduct> getClientProducts(List<RecipeItem> recipeItems) {
        Set<Integer> productIds = new HashSet<Integer>();

        if (recipeItems != null) {
            for (int i = 0; i < recipeItems.size(); i++) {
                productIds.add(recipeItems.get(i).getProductId());
            }
        }

        return getClientProductsWithIdsMap(new ArrayList<Integer>(productIds));
    }

    public Map<Integer, ClientProduct> getClientProductsWithIdsMap(List<Integer> productIds) {
        Map<Integer, ClientProduct> clientProductMap = new HashMap<Integer, ClientProduct>();
        if (productIds.size() <= 0)
            return clientProductMap;

        int batches = (productIds.size() + 1) % 1000 == 0 ? (productIds.size() + 1) / 1000 : ((productIds.size() + 1) / 1000 + 1);

        for (int i = 0; i < batches; i++) {
            int startIdx = i * 1000;
            int endIdx = (i + 1) * 1000;
            if (endIdx > productIds.size())
                endIdx = productIds.size();

            System.out.println("StartIdx = " + startIdx + " EndIdx = " + endIdx);
            List<ClientProduct> clientProducts = clientProductMapper.getClientProductsWithIds(productIds.subList(startIdx, endIdx));

            if (clientProducts != null) {
                for (int j = 0; j < clientProducts.size(); j++) {
                    ClientProduct clientProduct = clientProducts.get(j);
                    clientProductMap.put(clientProduct.getId(), clientProduct);
                }
            }
        }

        return clientProductMap;
    }
}
