package at.tutoringtrain.adminclient.data.entry;

import at.tutoringtrain.adminclient.data.mapper.DataMappingViews;
import com.fasterxml.jackson.annotation.JsonView;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class Location {
    @JsonView({
        DataMappingViews.Entry.In.Get.class,
    })
    private double lon;
    
    @JsonView({
        DataMappingViews.Entry.In.Get.class,
    })
    private double lat;
    
    @JsonView({
        DataMappingViews.Entry.In.Get.class,
    })
    private String name;

    public Location() {
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
