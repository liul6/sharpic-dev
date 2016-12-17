package com.sharpic.controller;

import com.sharpic.common.DateUtil;
import com.sharpic.dao.AuditDao;
import com.sharpic.domain.*;
import com.sharpic.service.IServerCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by joey on 2016-12-05.
 */

@Controller
public class ClientController {
    private static Log log = LogFactory.getLog(ClientController.class.getName());

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private AuditDao auditDao;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private ModifierMapper modifierMapper;

    @Autowired
    private IServerCache serverCache;

    @RequestMapping(value = "/client/getClientNames")
    @ResponseBody
    public List<String> getClientNames() {
        List<Client> allClients = clientMapper.getClients();

        List<String> allClientNames = new ArrayList<String>();
        if (allClients != null) {
            for (int i = 0; i < allClients.size(); i++) {
                Client client = allClients.get(i);
                allClientNames.add(client.getName());
            }
        }

        System.out.println("#####The number of clients####:" + allClientNames.size());
        return allClientNames;
    }

    @RequestMapping(value = "/client/getAuditDates")
    @ResponseBody
    public List<String> getClientAuditDates(String clientName) {
        if (clientName == null || clientName.isEmpty())
            return new ArrayList<String>();

        List<Audit> allAudits = auditDao.getClientAudits(clientName);

        List<String> allAuditDates = new ArrayList<String>();
        if (allAudits != null) {
            for (int i = 0; i < allAudits.size(); i++) {
                Audit audit = allAudits.get(i);
                allAuditDates.add(DateUtil.format(audit.getAuditDate()));
            }
        }

        System.out.println("#####The number of audits for client####:" + allAuditDates.size());
        return allAuditDates;
    }

    @RequestMapping(value = "/client/getLocations")
    @ResponseBody
    public List<String> getClientLocations(String clientName) {
        if (clientName == null || clientName.isEmpty())
            return new ArrayList<String>();

        List<Location> allLocations = locationMapper.getClientLocations(clientName);

        List<String> allLocationNames = new ArrayList<String>();
        if (allLocations != null) {
            for (int i = 0; i < allLocations.size(); i++) {
                Location location = allLocations.get(i);
                allLocationNames.add(location.getLocationName());
            }
        }

        System.out.println("#####The number of locations for client####:" + allLocationNames.size());
        return allLocationNames;
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(method = RequestMethod.POST, value = "/client/addAudit")
    @ResponseBody
    public String addAudit(String clientName) {
        Date auditDate = DateUtil.toDate(LocalDate.now());
        if (auditDao.getAuditId(clientName, auditDate) != null) {
            log.error("Cannot create audit  ," + auditDate.toString() + "since it is already existing!!!");
            return null;
        }

        auditDao.insertAudit(clientName, auditDate);
        return DateUtil.format(auditDate);
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(method = RequestMethod.POST, value = "/client/deleteAudit")
    @ResponseBody
    public String deleteAudit(String clientName, String auditDateStr) {
        LocalDate auditDate = LocalDate.parse(auditDateStr);
        if (auditDate == null) {
            log.error(auditDateStr + " is not a valid date");
            return null;
        }
        Date aDate = DateUtil.toDate(auditDate);

        if (auditDao.getAuditId(clientName, aDate) < 0) {
            log.error("Cannot delete audit with " + auditDateStr + ", since it does not exist!!!");
            return null;
        }

        auditDao.deleteAudit(clientName, aDate);

        return DateUtil.format(aDate);
    }

    @RequestMapping(value = "/client/getClients")
    @ResponseBody
    public List<Client> getClients() {
        List<Client> clients = clientMapper.getClients();

        for (int i = 0; i < clients.size(); i++) {
            Client client = clients.get(i);
            String clientName = client.getName();

            client.setLocations(locationMapper.getClientLocations(clientName));
            client.setModifiers(modifierMapper.getClientModifiers(clientName));
        }

        System.out.println("#####The number of locations for client####:" + clients.size());
        return clients;
    }

    @RequestMapping(value = "/client/getClientRecipes")
    @ResponseBody
    public List<Recipe> getRecipes(String clientName) {
        if (clientName == null || clientName.isEmpty())
            return new ArrayList<Recipe>();

        List<Recipe> clientRecipes = serverCache.getRecipes(clientName);
        if (clientRecipes != null)
            Collections.sort(clientRecipes);


        return clientRecipes;
    }

}
