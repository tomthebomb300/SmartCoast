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
    
    //used by RadarRotation->rotationTargets
    public void updateTargetsFromSpoke(RadarSpoke rs){
        ArrayList <ArrayList>targetParts;
        targetParts = rs.getTargetParts();
        for (ArrayList tp : targetParts) {
            boolean found=false;
            for (RadarTarget rt : targets){
                if(rt.targetPartOverlaps(tp)){
                    found = true;
                    rt.addTargetPart(tp);
                    rt.updateTargetCharacteristics();
                }
                //NO: fall out of this loop; matching target not found
            }
            if(found == false){
                //add as new target
//                targets.add(new RadarTarget(tp));
            }
        }
    }//updateTargetsFromSpoke
    
    //use by RadarSession->masterTargets
    public void updateTargetsFromRotation(RadarRotation rot){
        
    }//updateTargetsFromRotation
    
    public ArrayList<RadarTarget> getTargets(){
        return targets;
    }//getTargets
    
    
    
    
    
}//TargetTable