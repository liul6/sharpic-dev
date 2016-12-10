package com.sharpic.controller;

import com.sharpic.domain.AuditMapper;
import com.sharpic.domain.Entry;
import com.sharpic.domain.EntryMapper;
import com.sharpic.domain.Product;
import com.sharpic.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by joey on 2016-12-08.
 */

@Controller
public class AuditController {
    @Autowired
    private AuditMapper auditMapper;

    @Autowired
    private EntryMapper entryMapper;

    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/audit/getEntries")
    @ResponseBody
    public List<Entry> getEntries(String auditDateStr) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date auditDate = null;

        try {
            auditDate = format.parse(auditDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (auditDate == null)
            auditDate = new Date();

        int auditId = auditMapper.getAuditId(auditDate);
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
