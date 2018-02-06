/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.geo;

/**
 *
 * @author Elias
 */
public class GeoPoint {
    private double lon;
    private double lat;
    private String name;

    public GeoPoint() {
    }
    
    public GeoPoint(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }
    
    public GeoPoint(double lon, double lat, String name) {
        this.lon = lon;
        this.lat = lat;
        this.name = name;
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
