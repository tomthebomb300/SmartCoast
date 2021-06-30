/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nmci.smartcoast;

/**
 * @author Cormac Gebruers
 * @author Benjamin Jakobus
 * @since 1.0
 * @version 1.0
 */
public class GeoPosition {

    private double latitude;
    private double longitude;

    public GeoPosition(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
