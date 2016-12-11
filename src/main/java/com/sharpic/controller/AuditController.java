package com.sharpic.controller;

import com.sharpic.common.DateUtil;
import com.sharpic.domain.AuditMapper;
import com.sharpic.domain.Entry;
import com.sharpic.domain.EntryMapper;
import com.sharpic.domain.Product;
import com.sharpic.service.ProductService;
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
 * Created by joey on 2016-12-08.
 */

@Controller
public class AuditController {
    private static Log log = LogFactory.getLog(AuditController.class.getName());

    @Autowired
    private AuditMapper auditMapper;

    @Autowired
    private EntryMapper entryMapper;

    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/audit/getEntries")
    @ResponseBody
    public List<Entry> getEntries(String clientName, String auditDateStr) {
        LocalDate auditDate = LocalDate.parse(auditDateStr);

        if (auditDate == null)
            auditDate = LocalDate.now();

        int auditId = auditMapper.getAuditId(clientName, DateUtil.toDate(auditDate));
        if (auditId < 0)
            return new ArrayList<Entry>();

        List<Entry> entries = entryMapper.getAuditEntries(auditId);
        if (entries != null) {
            for (int i = 0; i < entries.size(); i++) {
                Entry entry = entries.get(i);
                Product product = productService.getProduct(entry.getProductId());
                entry.setProductDescription(productService.getProductDescription(product));
            }

        }
        System.out.println("The number of entries retrieved: " + entries.size());

        return entries;
    }

}
