package edu.tutoringtrain.data.geo;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class LocationQueryParameter {
    double lon, lat;

    public LocationQueryParameter(double lat, double lon) {
        this.lon = lon;
        this.lat = lat;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return "lon=" + Double.toString(lon) + "&lat=" + Double.toString(lat);
    }
}
