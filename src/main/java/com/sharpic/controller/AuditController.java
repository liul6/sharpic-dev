package com.sharpic.controller;

import com.sharpic.common.DateUtil;
import com.sharpic.dao.EntryDao;
import com.sharpic.dao.ModifierDao;
import com.sharpic.domain.AuditMapper;
import com.sharpic.domain.Entry;
import com.sharpic.domain.ModifierItem;
import com.sharpic.service.IServerCache;
import com.sharpic.service.ObjectDescriptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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
    private EntryDao entryDao;

    @Autowired
    private IServerCache serverCache;

    @Autowired
    private ModifierDao modifierDao;

    @Autowired
    private ObjectDescriptor objectDescriptor;

    @RequestMapping(value = "/audit/getEntries")
    @ResponseBody
    public List<Entry> getEntries(String clientName, String auditDateStr) {
        LocalDate auditDate = LocalDate.parse(auditDateStr);

        if (auditDate == null)
            auditDate = LocalDate.now();

        int auditId = auditMapper.getAuditId(clientName, DateUtil.toDate(auditDate));
        if (auditId < 0)
            return new ArrayList<Entry>();

        List<Entry> entries = entryDao.getAuditEntries(auditId);

        System.out.println("The number of entries retrieved: " + entries.size());

        Collections.sort(entries);
        return entries;
    }

    @RequestMapping(value = "/audit/getModifierItems")
    @ResponseBody
    public List<ModifierItem> getModifierItems(String clientName, String auditDateStr) {
        LocalDate auditDate = LocalDate.parse(auditDateStr);

        if (auditDate == null)
            auditDate = LocalDate.now();

        int auditId = auditMapper.getAuditId(clientName, DateUtil.toDate(auditDate));
        if (auditId < 0)
            return new ArrayList<ModifierItem>();

        return modifierDao.getAuditModifierItems(auditId);
    }

}
