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
    int sizeAsCellCount; //done
    double areaAsM2; //done
    int volumeAsM3;
    double minRange; //done
    double maxRange; //done
    int minBearing;
    int maxBearing;
    double avEchoStrength; //done
    double minEchoStrength; //done
    double maxEchoStrength; //done
    int designation; //see RadarTargetTable for constants
    int course;
    double speed;
    int age;
    double growRate; //over x rotations/seconds how quickly the echo is growing in area
    double decayRate; //over x rotations/seconds how quickly the echo is decaying in area
    double persistance; //over x rotations/seconds how many is the echo present
    double stability; //over x rotations/seconds, how unchanging is the echo
    int cluster;
    int sCI;
    int lCI;
    int sSI;
    int lSI;
    int recursionCount;
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
        
        sCI = latest.get(0).cellIdx;
        lCI = latest.get(0).cellIdx;
        sSI = latest.get(0).spokeIdx;
        lSI = latest.get(0).spokeIdx;
        
        for(RadarCell cell : latest){
            double rSC = cell.cellIdx*mPC;
            double rBC = (cell.cellIdx+1)*mPC;
            
            double aSC = Math.PI*(rSC*rSC);
            double aBC = Math.PI*(rBC*rBC);
            
            double iA = aBC-aSC;
            double cA = (((double)360/numSpokes)/360)*iA;
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
            
            if(cell.cellIdx > lCI)
                lCI = cell.cellIdx;
            if(cell.cellIdx < sCI)
                sCI = cell.cellIdx;
            if(cell.spokeIdx > lSI)
                lSI = cell.spokeIdx;
            if(cell.spokeIdx < sSI)
                sSI = cell.spokeIdx;
            
        }

        minRange = mPC*minCellIndex;
        maxRange = mPC*maxCellIndex;
        
        sizeAsCellCount = latest.size();
        
        areaAsM2 = Math.round(areaAsM2);
        
        avEchoStrength = echoTotal/latest.size();
        assignOutline();
    }
    
    
    
    
    
    public void assignOutline(){
        recursionCount = 0;
        int[][] targetArea = new int[(lSI-sSI+3)][(lCI-sCI+3)];
        outline = new ArrayList<RadarCell>();
        
        for(RadarCell cell : latest){
            targetArea[(cell.spokeIdx-sSI+1)][(cell.cellIdx-sCI+1)] = cell.echo;
        }
        
        int si = 0;
        int ci = 0;
        int caught = 0;
        boolean notFound = true;
                
        while(si < targetArea.length && notFound){
            ci = 0;
            
            while(ci < targetArea[si].length && notFound){
                if(targetArea[si][ci] != 0 && isOutline(targetArea, si, ci)){
                    try{
                        ArrayList<RadarCell> notSearched = new ArrayList<RadarCell>();
                        
                        while((notFound = !cycleOutline(targetArea, si, ci, notSearched)) || !notSearched.isEmpty()){
                            RadarCell firstCell = notSearched.get(0);
                            notSearched.remove(0);
                            si = firstCell.spokeIdx;
                            ci = firstCell.cellIdx;
                            recursionCount = 0;
                        }
                    } catch(StackOverflowError e){
                        caught++;
                    }
                }
                ci++;
            }
            si++;
        }
//        System.out.println("Outline StackOverflow caught: "+caught);
    }
    
    
    
    
    
    private boolean cycleOutline(int[][] targetArea, int si, int ci, ArrayList<RadarCell> notSearched){
        outline.add(new RadarCell(si+sSI-1, ci+sCI-1, targetArea[si][ci]));
        targetArea[si][ci] = 16;
        
        if(++recursionCount == 2500){
            notSearched.add(new RadarCell(si, ci, targetArea[si][ci]));
            return false;
        }
        
        //top left
        if(targetArea[si-1][ci-1] != 0 && targetArea[si-1][ci-1] != 16 && isOutline(targetArea, si-1, ci-1)){
            boolean complete = cycleOutline(targetArea, si-1, ci-1, notSearched);
            if(!complete){
                notSearched.add(new RadarCell(si, ci, targetArea[si][ci]));
                return false;
            }
                
        }
        
        //top
        if(targetArea[si-1][ci] != 0 && targetArea[si-1][ci] != 16 && isOutline(targetArea, si-1, ci)){
            boolean complete = cycleOutline(targetArea, si-1, ci, notSearched);
            if(!complete){
                notSearched.add(new RadarCell(si, ci, targetArea[si][ci]));
                return false;
            }
        }
        
        //top right
        if(targetArea[si-1][ci+1] != 0 && targetArea[si-1][ci+1] != 16 && isOutline(targetArea, si-1, ci+1)){
            boolean complete = cycleOutline(targetArea, si-1, ci+1, notSearched);
            if(!complete){
                notSearched.add(new RadarCell(si, ci, targetArea[si][ci]));
                return false;
            }
        }
        
        //left
        if(targetArea[si][ci-1] != 0 && targetArea[si][ci-1] != 16 && isOutline(targetArea, si, ci-1)){
            boolean complete = cycleOutline(targetArea, si, ci-1, notSearched);
            if(!complete){
                notSearched.add(new RadarCell(si, ci, targetArea[si][ci]));
                return false;
            }
        }
        
        //right
        if(targetArea[si][ci+1] != 0 && targetArea[si][ci+1] != 16 &&  isOutline(targetArea, si, ci+1)){
            boolean complete = cycleOutline(targetArea, si, ci+1, notSearched);
            if(!complete){
                notSearched.add(new RadarCell(si, ci, targetArea[si][ci]));
                return false;
            }
        }
        
        //bottom left
        if(targetArea[si+1][ci-1] != 0 && targetArea[si+1][ci-1] != 16 &&  isOutline(targetArea, si+1, ci-1)){
            boolean complete = cycleOutline(targetArea, si+1, ci-1, notSearched);
            if(!complete){
                notSearched.add(new RadarCell(si, ci, targetArea[si][ci]));
                return false;
            }
        }
        
        //bottom
        if(targetArea[si+1][ci] != 0 && targetArea[si+1][ci] != 16 &&  isOutline(targetArea, si+1, ci)){
            boolean complete = cycleOutline(targetArea, si+1, ci, notSearched);
            if(!complete){
                notSearched.add(new RadarCell(si, ci, targetArea[si][ci]));
                return false;
            }
        }
        
        //bottom right
        if(targetArea[si+1][ci+1] != 0 && targetArea[si+1][ci+1] != 16 &&  isOutline(targetArea, si+1, ci+1)){
            boolean complete = cycleOutline(targetArea, si+1, ci+1, notSearched);
            if(!complete){
                return false;
            }
        }
        return true;
    }
    
    
    
    
    
    private boolean isOutline(int[][] targetArea, int si, int ci){
        
        //top
        if(targetArea[si-1][ci] == 0)
            return true;
        
        //bottom
        if(targetArea[si+1][ci] == 0)
            return true;
        
        //left
        if(targetArea[si][ci-1] == 0)
            return true;
        
        //right
        if(targetArea[si][ci+1] == 0)
            return true;
        
        return false;
    }
    
    public void updateTargetCharacteristics(){
        //recalculate all target characteristics. Call after all target parts
        //have been added or whenever you wish to update target characteristics
        //Will work with a partially defined target as well as a complete
        //target. Is that of any use..?
        
    }//updateTargetCharacteristics
    
   
    //GET RID OF SPOKE CELL LIST - NOT NEEDED DUE MODIFIED RADARCELL CLASS
}//RadarTarget