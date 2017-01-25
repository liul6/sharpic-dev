package com.sharpic.dao;

import com.sharpic.common.SharpICException;
import com.sharpic.domain.ClientProduct;
import com.sharpic.domain.ClientProductMapper;
import com.sharpic.domain.SizeMapper;
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
    private RecipeItemDao recipeItemDao;

    @Autowired
    private AuditRecipeItemDao auditRecipeItemDao;

    @Autowired
    private EntryDao entryDao;

    @Autowired
    private SizeMapper sizeMapper;

    public List<ClientProduct> getClientProducts(String clientName) {
        List<ClientProduct> clientProducts = clientProductMapper.getClientProducts(clientName);
        if (clientProducts == null)
            return null;

        return clientProducts;
    }

    public List<ClientProduct> getLinkedClientProducts(int parentProductId) {
        return clientProductMapper.getLinkedClientProducts(parentProductId);
    }

    public void deleteClientProduct(int productId) throws SharpICException {
        int ocurrensInRecipeItem = recipeItemDao.getNumberOfRecipeItemsByProductId(productId);
        int occurensInAuditRecipeItem = auditRecipeItemDao.getNumberOfRecipeItemsByProductId(productId);
        int occurensInEntry = entryDao.getNumberOfEntriesByProductId(productId);

        if (ocurrensInRecipeItem > 0 || occurensInAuditRecipeItem > 0 || occurensInEntry > 0)
            throw new SharpICException("The client specific product is used in recipe or linked with sales or audits, cannot be deleted");

        clientProductMapper.getClientProduct(productId);
    }

    public void deleteSize(int sizeId) throws SharpICException {
        int occurrencesClientProduct = clientProductMapper.getNumberOfClientProductsBySizeId(sizeId);
        if (occurrencesClientProduct > 0)
            throw new SharpICException("The size is linked to some cient products, cannot be deleted");

        sizeMapper.deleteSize(sizeId);
    }
}
