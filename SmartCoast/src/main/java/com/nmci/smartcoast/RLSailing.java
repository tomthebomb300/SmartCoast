/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nmci.smartcoast;

/**
 * A utility class to package up a rhumb line sailing
 * 
 * @author Cormac Gebruers
 * @version 1.0
 * @since 1.0
 */
public class RLSailing {

    private double course;
    private double distNM;

    public RLSailing(double pCourse, double pDistNM) {
        course = pCourse;
        distNM = pDistNM;
    }

    public double getCourse() {
        return course;
    }

    public double getDistNM() {
        return distNM;
    }
}
