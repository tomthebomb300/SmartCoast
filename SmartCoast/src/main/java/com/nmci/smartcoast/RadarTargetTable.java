/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nmci.smartcoast;

import java.util.ArrayList;

/**
 *
 * @author cormac
 */
class RadarTargetTable {
    public static final int TARGET_VESSEL = 0;
    public static final int TARGET_FIXEDMARK = 1;
    public static final int TARGET_FLOATINGMARK = 2;
    public static final int TARGET_LAND = 3;
    public static final int TARGET_PRECIPITATION = 4;
    public static final int TARGET_WAVEFRONT = 5;
    public static final int TARGET_AIRCRAFT = 6;
    public static final int TARGET_UNDEFINED = 7;
    
    
    ArrayList<RadarTarget> targets;
    
    RadarTargetTable(ArrayList<RadarTarget> targets){
       this.targets = targets;
    }//constructor
    
    //use by RadarSession->masterTargets
    public void updateTargetsFromRotation(RadarRotation rot){
        
    }//updateTargetsFromRotation
    
    public ArrayList<RadarTarget> getTargets(){
        return targets;
    }//getTargets
    
    
    
    
    
}//TargetTable