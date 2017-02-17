package com.sharpic.controller;

import com.sharpic.common.DateUtil;
import com.sharpic.dao.*;
import com.sharpic.domain.*;
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

    @Autowired
    private ClientProductDao clientProductDao;

    @Autowired
    private RecipeItemDao recipeItemDao;

    @Autowired
    private RecipeDao recipeDao;

    @Autowired
    private ModifierDao modifierDao;

    @Autowired
    private EntryDao entryDao;

    @Autowired
    private ProductController productController;

    @Autowired
    private ObjectTransientFieldsPopulator objectTransientFieldsPopulator;

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
    public List<Location> getClientLocations(String clientName) {
        if (clientName == null || clientName.isEmpty())
            return new ArrayList<>();

        List<Location> locations = locationMapper.getClientLocations(clientName);

        System.out.println("#####The number of locations for client####:" + locations.size());
        return locations;
    }

    @RequestMapping(value = "/client/getClientProducts")
    @ResponseBody
    public List<ClientProduct> getClientProducts(String clientName) {
        if (clientName == null || clientName.isEmpty())
            return new ArrayList<>();

        List<ClientProduct> clientProducts = clientProductDao.getClientProducts(clientName);
        objectTransientFieldsPopulator.populateClientProductTransientFields(clientProducts);
        return clientProducts;
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(method = RequestMethod.POST, value = "/client/addAudit")
    @ResponseBody
    public SharpICResponse addAudit(String clientName) {
        SharpICResponse response = new SharpICResponse();
        try {
            Date auditDate = DateUtil.toDate(LocalDate.now());
            if (auditDao.getAuditId(clientName, auditDate) != null) {
                response.setErrorText("Cannot create audit  ," + auditDate.toString() + "since it is already existing!!!");
                response.setSuccessful(false);
                return response;
            }

            auditDao.insertAudit(clientName, auditDate);
            response.addToModel("addedAuditDate", DateUtil.format(auditDate));
            response.setSuccessful(true);
        } catch (Exception e) {
            response.setException(e);
        }

        return response;
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(method = RequestMethod.POST, value = "/client/deleteAudit")
    @ResponseBody
    public SharpICResponse deleteAudit(String clientName, String auditDateStr) {
        SharpICResponse response = new SharpICResponse();
        try {
            LocalDate auditDate = LocalDate.parse(auditDateStr);
            if (auditDate == null) {
                response.setErrorText(auditDateStr + " is not a valid date");
                response.setSuccessful(false);
                return response;
            }
            Date aDate = DateUtil.toDate(auditDate);

            int auditId = auditDao.getAuditId(clientName, aDate);
            if (auditDao.getAuditId(clientName, aDate) < 0) {
                response.setErrorText("Cannot delete audit with " + auditDateStr + ", since it does not exist!!!");
                response.setSuccessful(false);
                return response;
            }

            List<Entry> entries = entryDao.getAuditEntries(auditId);
            if(entries!=null && entries.size()>0) {
                response.setErrorText("Cannot delete audit with " + auditDateStr + ", since it contains entries!!!");
                response.setSuccessful(false);
                return response;
            }

            auditDao.deleteAudit(clientName, aDate);
            response.setSuccessful(true);
        } catch (Exception e) {
            response.setException(e);
        }

        return response;
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
    public SharpICResponse getRecipes(String clientName) {
        SharpICResponse sharpICResponse = new SharpICResponse();

        try {
            if (clientName == null || clientName.isEmpty())
                return sharpICResponse;

            List<Recipe> clientRecipes = serverCache.getRecipes(clientName);
            objectTransientFieldsPopulator.populateRecipeTransientFields(clientRecipes);

            if (clientRecipes != null)
                Collections.sort(clientRecipes);
            sharpICResponse.addToModel("clientRecipes", clientRecipes);
            sharpICResponse.addToModel("products", serverCache.getProducts());
            sharpICResponse.setSuccessful(true);
        } catch (Exception e) {
            sharpICResponse.setException(e);
        }

        return sharpICResponse;
    }

    @RequestMapping(value = "/client/getClientInfo")
    @ResponseBody
    public SharpICResponse getClientInfo(String clientName) {
        SharpICResponse sharpICResponse = new SharpICResponse();

        try {
            if (clientName == null || clientName.isEmpty())
                return sharpICResponse;

            sharpICResponse.addToModel("products", productController.getProducts());
            sharpICResponse.addToModel("clientLocations", getClientLocations(clientName));
            sharpICResponse.addToModel("clientAudits", getClientAuditDates(clientName));

            sharpICResponse.setSuccessful(true);
        } catch (Exception e) {
            log.error(e);
            sharpICResponse.setException(e);
        }

        return sharpICResponse;
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(method = RequestMethod.POST, value = "/client/saveRecipe", consumes = "application/json")
    @ResponseBody
    public SharpICResponse saveRecipe(@RequestBody Recipe recipe) {
        SharpICResponse response = new SharpICResponse();
        try {
            if (recipe.getId() <= 0) {
                recipe = recipeDao.createRecipe(recipe);
            }

            recipeItemDao.deleteRecipeItems(recipe.getId());

            List<RecipeItem> recipeItems = recipe.getRecipeItems();
            if (recipeItems != null) {
                for (int i = 0; i < recipeItems.size(); i++) {
                    RecipeItem recipeItem = recipeItems.get(i);
                    recipeItem.setProduct(serverCache.findProduct(recipeItem.getProductId()));
                    recipeItemDao.insertRecipeItem(recipeItem);
                }
            }

            serverCache.fillRecipeCache();

            recipe = recipeDao.getRecipe(recipe.getId());
            objectTransientFieldsPopulator.populateRecipeTransientFields(recipe);
            response.setSuccessful(true);
            response.addToModel("recipe", recipe);
        } catch (Exception e) {
            response.setException(e);
        }

        return response;
    }

}
