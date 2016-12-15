package com.sharpic.controller;

import com.sharpic.common.DateUtil;
import com.sharpic.domain.AuditMapper;
import com.sharpic.domain.Recipe;
import com.sharpic.domain.Sale;
import com.sharpic.domain.SaleMapper;
import com.sharpic.service.IServerCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joey on 2016-12-13.
 */

@Controller
public class SaleController {
    @Autowired
    private SaleMapper saleMapper;

    @Autowired
    private AuditMapper auditMapper;

    @Autowired
    private IServerCache serverCache;

    @RequestMapping(value = "/sale/getSales")
    @ResponseBody
    public List<Sale> getSales(String clientName, String auditDateStr) {
        LocalDate auditDate = LocalDate.parse(auditDateStr);

        if (auditDate == null)
            auditDate = LocalDate.now();

        int auditId = auditMapper.getAuditId(clientName, DateUtil.toDate(auditDate));
        if (auditId < 0)
            return new ArrayList<Sale>();

        List<Sale> auditSales = saleMapper.getAuditSales(auditId);
        if (auditSales != null) {
            for (int i = 0; i < auditSales.size(); i++) {
                Sale sale = auditSales.get(i);
                Recipe recipe = serverCache.findRecipe(sale.getRecipeId());
                sale.setRecipe(recipe);
                if (recipe != null)
                    sale.setRecipeDescription(recipe.getDescription());
                else {
                    System.out.println("Cannot find recipe with id: " + recipe.getId());
                }
            }
        }

        return auditSales;
    }
}
