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
    int sizeAsCellCount;
    double areaAsM2;
    int volumeAsM3;
    double minRange;
    double maxRange;
    int minBearing;
    int maxBearing;
    double avEchoStrength;
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
    ArrayList<RadarCell> latest;
    ArrayList<RadarCell> outline;
    ArrayList<RadarTarget> history;
    
    RadarTarget(ArrayList tp, int[][] getRid){
        //creates a new target from a target part
    }//constructor
    
    RadarTarget(ArrayList<RadarCell> target, int targetId){
        latest = target;
        this.targetID = targetId;
    }
    
    public void assignAttributes(float mPC, float overscanRange, int numSpokes){
        areaAsM2 = 0;
        
        int minCellIndex = RadarSpoke.maxCells+1;
        int maxCellIndex = -1;
        
        int echoTotal = 0;
        avEchoStrength = -1;
        minEchoStrength = 16;
        maxEchoStrength = -1;
        
        for(RadarCell cell : latest){
            float rSC = cell.cellIdx*mPC;
            float rBC = cell.cellIdx+1*mPC;
            
            double aSC = Math.PI*(rSC*rSC);
            double aBC = Math.PI*(rBC*rBC);
            
            double iA = aBC-aSC;
            double cA = ((360/numSpokes)/360)*iA;
            areaAsM2 = areaAsM2 + cA;
            
            if(cell.cellIdx < minCellIndex)
                minCellIndex = cell.cellIdx;
            
            if(cell.cellIdx > maxCellIndex)
                maxCellIndex = cell.cellIdx;
            
            echoTotal = echoTotal + cell.echo;
            
            if(cell.echo > maxEchoStrength)
                maxEchoStrength = cell.echo;
            
            if(cell.echo < minEchoStrength)
                minEchoStrength = cell.echo;
            
        }
        
        minRange = mPC*minCellIndex;
        maxRange = mPC*maxCellIndex;
        
        avEchoStrength = echoTotal/latest.size();
    }
    
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
}//RadarTarget