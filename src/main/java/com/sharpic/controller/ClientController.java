package com.sharpic.controller;

import com.sharpic.common.DateUtil;
import com.sharpic.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joey on 2016-12-05.
 */
@Controller
public class ClientController {
    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private AuditMapper auditMapper;

    @Autowired
    private LocationMapper locationMapper;

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

        List<Audit> allAudits = auditMapper.getClientAudits(clientName);

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
}
