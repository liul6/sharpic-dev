package com.sharpic.dao;

import com.sharpic.domain.*;
import com.sharpic.service.IServerCache;
import com.sharpic.service.ObjectTransientFieldsPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by joey on 2017-01-13.
 */

@Service
public class ClientDao {
    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private ModifierMapper modifierMapper;

    @Autowired
    private AuditRecipeDao auditRecipeDao;

    @Autowired
    private IServerCache serverCache;

    @Autowired
    private ObjectTransientFieldsPopulator objectTransientFieldsPopulator;

    public Map<String, Location> getLocationMap(String clientName) {
        Map<String, Location> clientLocationMap = new HashMap<String, Location>();

        List<Location> clientLocations = locationMapper.getClientLocations(clientName);
        if (clientLocations != null) {
            for (int i = 0; i < clientLocations.size(); i++) {
                Location location = clientLocations.get(i);
                clientLocationMap.put(location.getLocationName(), location);
            }
        }

        return clientLocationMap;
    }

    public Map<String, Location> getLocationMap(int auditId) {
        Map<String, Location> clientLocationMap = new HashMap<String, Location>();

        List<Location> clientLocations = locationMapper.getClientLocationsByAuditId(auditId);
        if (clientLocations != null) {
            for (int i = 0; i < clientLocations.size(); i++) {
                Location location = clientLocations.get(i);
                clientLocationMap.put(location.getLocationName(), location);
            }
        }

        return clientLocationMap;
    }

    public Map<String, Modifier> getModifierMap(String clientName) {
        Map<String, Modifier> clientModifierMap = new HashMap<String, Modifier>();

        List<Modifier> clientModifiers = modifierMapper.getClientModifiers(clientName);
        if (clientModifiers != null) {
            for (int i = 0; i < clientModifiers.size(); i++) {
                Modifier modifier = clientModifiers.get(i);
                clientModifierMap.put(modifier.getModifierName(), modifier);
            }
        }

        return clientModifierMap;
    }

    public List<Recipe> getClientApplicableRecipes(String clientName, int auditId) {
        Map<Integer, AuditRecipe> clientAuditRecipeMap = auditRecipeDao.getAuditRecipesMap(auditId);
        List<Recipe> clientRecipes = serverCache.getRecipes(clientName);

        List<Recipe> clientAppicableRecipes = new ArrayList<Recipe>();
        for (int i = 0; i < clientRecipes.size(); i++) {
            Recipe recipe = clientRecipes.get(i);

            if (clientAuditRecipeMap.containsKey(recipe.getRecipeName()))
                recipe = clientAuditRecipeMap.get(recipe.getRecipeName());
            clientAppicableRecipes.add(recipe);
        }

        objectTransientFieldsPopulator.populateRecipeTransientFields(clientAppicableRecipes);
        return clientAppicableRecipes;
    }

}
