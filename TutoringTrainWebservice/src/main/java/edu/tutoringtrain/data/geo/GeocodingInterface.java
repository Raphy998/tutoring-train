/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.geo;

import edu.tutoringtrain.data.geo.data.LocationAddress;
import edu.tutoringtrain.data.geo.data.LocationObject;
import java.util.Locale;
import oracle.spatial.geometry.JGeometry;

/**
 *
 * @author Elias
 */
public class GeocodingInterface {
    public static String getLocationName(JGeometry g) throws Exception {
        LocationQueryParameter loc = new LocationQueryParameter(g.getPoint()[1], g.getPoint()[0]);
        LocationObject lo = LocationQuery.query(loc, Locale.UK);
        LocationAddress la = lo.getAddress();
        
        StringBuilder locName = new StringBuilder();
        
        if (la != null) {
            if (la.getCity() != null) 
                locName.append(la.getCity());
            else if (la.getTown() != null)
                locName.append(la.getTown());
            else if (la.getVillage() != null)
                locName.append(la.getVillage());
            locName.append(", ");

            locName.append(la.getPostcode());
            locName.append(", ");
            locName.append(la.getState());
            locName.append(", ");
            locName.append(la.getCountry());
        }

        return locName.toString();
    }
}
