/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nmci.smartcoast;

/**
 * Provides various helper methods for number conversions (such as degree to radian
 * conversion, decimal degree to radians etc)
 * @author Cormac Gebruers
 * @version 1.0
 * @since 1.0
 */

public class NumUtil {
    /**
     * Rounds a number
     * @param Rval number to be rounded
     * @param Rpl number of decimal places
     * @return rounded number
     * @since 0.1
     */
    public float Round(float Rval, int Rpl) {
        float p = (float)Math.pow(10,Rpl);
        Rval = Rval * p;
        float tmp = Math.round(Rval);
        return (float)tmp/p;
    }
}



