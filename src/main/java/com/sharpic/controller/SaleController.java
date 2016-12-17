package com.sharpic.controller;

import com.sharpic.common.DateUtil;
import com.sharpic.dao.SaleDao;
import com.sharpic.domain.AuditMapper;
import com.sharpic.domain.Sale;
import com.sharpic.service.IServerCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private static Log log = LogFactory.getLog(SaleController.class.getName());

    @Autowired
    private SaleDao saleDao;

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

        try {
            List<Sale> auditSales = saleDao.getAuditSales(auditId);
            return auditSales;
        }
        catch (Exception e){
            log.error(e);
        }

        return null;
    }
}
