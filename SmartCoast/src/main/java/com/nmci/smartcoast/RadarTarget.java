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
class RadarTarget {
    int targetID;
    String exteralTag; //a means to cross-reference this target to an external data source like AIS, ADSB etc. 
    int[] centroidAsSpokeIDCellID;
    Position centroidAsLatLng;
    int sizeAsCellCount; //use
    int areaAsM2;
    int volumeAsM3;
    int minRange;
    int maxRange;
    int minBearing;
    int maxBearing;
    double avEchoStrength; //use
    double minEchoStrength;
    double maxEchoStrength;
    int designation; //see RadarTargetTable for constants
    int course;
    double speed;
    int age;
    double growRate; //over x rotations/seconds how quickly the echo is growing in area
    double decayRate; //over x rotations/seconds how quickly the echo is decaying in area
    double persistance; //over x rotations/seconds how many is the echo present
    double stability; //over x rotations/seconds, how unchanging is the echo
    SpokeCellList latest;
    SpokeCellList outline;
    SpokeCellList[] history;
    
    RadarTarget(ArrayList tp){
        //creates a new target from a target part
        
    }//constructor
    
    public boolean targetPartOverlaps(ArrayList tp){
//        if(){
//            return true;
//        }
        return false;
    }//targetPartOverlaps
    
    public void addTargetPart(ArrayList tp){
        
    }//add a target part to this target
    
    public void updateTargetCharacteristics(){
        //recalculate all target characteristics. Call after all target parts
        //have been added or whenever you wish to update target characteristics
        //Will work with a partially defined target as well as a complete
        //target. Is that of any use..?
        
    }//updateTargetCharacteristics
    
   
    //GET RID OF SPOKE CELL LIST - NOT NEEDED DUE MODIFIED RADARCELL CLASS
    
    class SpokeCellList {
//    "spokeCellList" structure:
//[
//[spokeID,[cellIDHexVal,cellIDHexVal,...],
//...,
//[spokeID,[cellIDHexVal,cellIDHexVal,...]
//]
}//SpokeCellList

    class SpokeCell{
        int spokeID;
        RadarCell cell;


    }//SpokeCell
}//RadarTarget