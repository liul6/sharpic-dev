package com.sharpic.controller;

import com.sharpic.common.DateUtil;
import com.sharpic.dao.AuditDao;
import com.sharpic.dao.ClientDao;
import com.sharpic.dao.EntryDao;
import com.sharpic.dao.ModifierDao;
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
import java.util.Collections;
import java.util.List;

/**
 * Created by joey on 2016-12-08.
 */

@Controller
public class AuditController {
    private static Log log = LogFactory.getLog(AuditController.class.getName());

    @Autowired
    private AuditDao auditDao;

    @Autowired
    private EntryDao entryDao;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private IServerCache serverCache;

    @Autowired
    private ModifierDao modifierDao;

    @Autowired
    private ObjectTransientFieldsPopulator objectDescriptor;

    @RequestMapping(value = "/audit/getAuditInfo")
    @ResponseBody
    public SharpICResponse getAuditInfo(String clientName, String auditDateStr) {
        LocalDate auditDate = LocalDate.parse(auditDateStr);
        SharpICResponse sharpICResponse = new SharpICResponse();

        if (auditDate == null)
            auditDate = LocalDate.now();
        try {
            int auditId = auditDao.getAuditId(clientName, DateUtil.toDate(auditDate));
            if (auditId >= 0) {
                List<Entry> entries = entryDao.getAuditEntries(auditId);
                Collections.sort(entries);
                sharpICResponse.addToModel("entries", entries);

                List<ModifierItem> modifierItems = modifierDao.getAuditModifierItems(auditId);
                sharpICResponse.addToModel("modifierItems", modifierItems);

                sharpICResponse.addToModel("recipes", clientDao.getClientApplicableRecipes(clientName, auditId));
                sharpICResponse.addToModel("audit", auditDao.getAudit(auditId));

                sharpICResponse.setSuccessful(true);
            }
        } catch (Exception e) {
            log.error(e);
            sharpICResponse.setErrorText(e.getMessage());
            sharpICResponse.setSuccessful(false);
        }

        return sharpICResponse;
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

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(method = RequestMethod.POST, value = "/audit/saveEntry", consumes = "application/json")
    @ResponseBody
    public SharpICResponse saveEntry(@RequestBody Entry entry) {
        SharpICResponse sharpICResponse = new SharpICResponse();
        try {
            if (entry.getProductId() <= 0) {
                entry.setProductId(entry.getProduct().getId());
            }

            Entry savedEntry = entryDao.updateAuditEntry(entry);
            sharpICResponse.setSuccessful(true);
            savedEntry.setProduct(serverCache.findProduct(savedEntry.getProductId()));
            objectDescriptor.populateProductTransientFields(savedEntry.getProduct());
            sharpICResponse.addToModel("entry", savedEntry);
            return sharpICResponse;
        } catch (Exception e) {
            sharpICResponse.setErrorText(e.getMessage());
            sharpICResponse.setSuccessful(false);

            return sharpICResponse;
        }
    }
}
