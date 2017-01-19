package com.sharpic.controller;

import com.sharpic.common.DateUtil;
import com.sharpic.dao.EntryDao;
import com.sharpic.dao.ModifierDao;
import com.sharpic.domain.AuditMapper;
import com.sharpic.domain.Entry;
import com.sharpic.domain.ModifierItem;
import com.sharpic.service.IServerCache;
import com.sharpic.service.ObjectTransientFieldsPopulator;
import com.sharpic.web.SharpICResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    private ObjectTransientFieldsPopulator objectDescriptor;

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

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(method = RequestMethod.POST, value = "/audit/deleteEntry", consumes = "application/json")
    @ResponseBody
    public SharpICResponse deleteEntry(@RequestBody Entry entry) {
        SharpICResponse sharpICResponse = new SharpICResponse();
        if (entry.getId() <= 0) {
            sharpICResponse.setSuccessful(true);
            return sharpICResponse;
        }

        try {
            entryDao.deleteAuditEnty(entry.getId());
            sharpICResponse.setSuccessful(true);
            return sharpICResponse;
        } catch (Exception e) {
            sharpICResponse.setErrorText(e.getMessage());
            sharpICResponse.setSuccessful(false);

            return sharpICResponse;
        }
    }

}
