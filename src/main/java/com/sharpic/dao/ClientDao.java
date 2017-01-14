package com.sharpic.dao;

import com.sharpic.domain.Location;
import com.sharpic.domain.LocationMapper;
import com.sharpic.domain.Modifier;
import com.sharpic.domain.ModifierMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


}
