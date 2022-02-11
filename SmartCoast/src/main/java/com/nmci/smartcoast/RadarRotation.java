/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nmci.smartcoast;

import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;

/**
 *
 * @author cormac
 */
class RadarRotation {
    //Date related information
    String year;
    String month;
    String day;
    String hh;
    String mm;
    String ss;
    String rotationDate;
    String startTime;
    ArrayList<RadarSpoke> spokes;
    //Target related information (for this rotation, see RadarSession->masterTargets for the session targetTable)
    RadarTargetTable rotationTargets;
    private long recursionCount;
    private int[][] rotation;
    
    
    RadarRotation(File rotationFile) {
        year = rotationFile.getName().substring(0, 4);
        month = rotationFile.getName().substring(4, 6);
        day = rotationFile.getName().substring(6, 8);
        hh = rotationFile.getName().substring(8, 10);
        mm = rotationFile.getName().substring(10, 12);
        ss = rotationFile.getName().substring(12, 14);
        rotationDate = year + "-" + month + "-" + day;
        startTime = hh + ":" + mm + ":" + ss + " utc";
        
        spokes = new <RadarSpoke>ArrayList();
        
        try ( Scanner sc = new Scanner(rotationFile)) {
            // read each spoke from the data file
            while (sc.hasNextLine()) {
                RadarSpoke rs = new RadarSpoke(sc.nextLine());
                spokes.add(rs);
            }
        } catch (Exception e) {

        }
    }//constructor
    
    public void findTargets(){
        ArrayList<RadarTarget> targets = new ArrayList<RadarTarget>();
        int targetId = 0;
        
        int numSplits = 1;
        ArrayList<int[][]> splits = this.splitRotation(numSplits, true);
        
        int spokeIndex = 0;
        int cellIndex = 0;
        int caught = 0;
        int splitIndex = 0;        
        
        for(int[][] split : splits){
            spokeIndex = 0;
            rotation = split;
            ArrayList<RadarTarget> splitsTargets = new ArrayList<RadarTarget>();
            
            while(spokeIndex < split.length){
                cellIndex = 0;

                while(cellIndex < split[spokeIndex].length){
                    
                    if(split[spokeIndex][cellIndex] != 0){
                        ArrayList<RadarCell> cells = new ArrayList<RadarCell>();

                        try{
                            while(!findCellsOfTarget(cells, spokeIndex, cellIndex, 0)){
                                RadarCell lastCell = cells.get(cells.size()-1);
                                spokeIndex = lastCell.spokeIdx;
                                cellIndex = lastCell.cellIdx;
                            }
                        }
                        catch(StackOverflowError error){
                            caught++;
                        }

                        RadarTarget target = new RadarTarget(cells, ++targetId);
                        target.assignAttributes(spokes.get(0).mPC, spokes.get(0).overscanRange, spokes.size());
                        splitsTargets.add(target);
                    }
                    cellIndex++;
                }
                spokeIndex++;
            }
            splitIndex++;
            //join targets from split and oldSplit
            targets.addAll(splitsTargets);
        }
        System.out.println("targets size: "+targets.size());
        System.out.println("target 0: "+targets.get(0).latest.size());
        System.out.println("caught: "+caught);
    }


    private boolean findCellsOfTarget(ArrayList<RadarCell> target, int spokeIndex, int cellIndex, int recursiveCount) throws StackOverflowError{
        target.add(new RadarCell(spokeIndex-1, cellIndex-1, rotation[spokeIndex][cellIndex]));
        rotation[spokeIndex][cellIndex] = 0;
        
        if(++recursiveCount == 3000)
            return false;
        
        
        //left
        if(rotation[spokeIndex][cellIndex-1] != 0){
            boolean targetFound = findCellsOfTarget(target, spokeIndex, cellIndex-1, recursiveCount);
            
            if(!targetFound)
                return false;
        }

        //right
        if(rotation[spokeIndex][cellIndex+1] != 0){
            boolean targetFound = findCellsOfTarget(target, spokeIndex, cellIndex+1, recursiveCount);
            
             if(!targetFound)
                return false;
        }

        //top
        if(rotation[spokeIndex-1][cellIndex] != 0){
            boolean targetFound = findCellsOfTarget(target, spokeIndex-1, cellIndex, recursiveCount);
            
             if(!targetFound)
                return false;
        }

        //bottom
        if(rotation[spokeIndex+1][cellIndex] != 0){
            boolean targetFound = findCellsOfTarget(target, spokeIndex+1, cellIndex, recursiveCount);
            
             if(!targetFound)
                return false;
        }

        //top left
        if(rotation[spokeIndex-1][cellIndex-1] != 0){
            boolean targetFound = findCellsOfTarget(target, spokeIndex-1, cellIndex-1, recursiveCount);
            
             if(!targetFound)
                return false;
        }

        //top right
        if(rotation[spokeIndex-1][cellIndex+1] != 0){
            boolean targetFound = findCellsOfTarget(target, spokeIndex-1, cellIndex+1, recursiveCount);
            
             if(!targetFound)
                return false;
        }

        //bottom left
        if(rotation[spokeIndex+1][cellIndex-1] != 0){
            boolean targetFound = findCellsOfTarget(target, spokeIndex+1, cellIndex-1, recursiveCount);
            
             if(!targetFound)
                return false;
        }

        //bottom right
        if(rotation[spokeIndex+1][cellIndex+1] != 0){
            boolean targetFound = findCellsOfTarget(target, spokeIndex+1, cellIndex+1, recursiveCount);
            
             if(!targetFound)
                return false;
        }
        return true;
    }
    
    private ArrayList<int[][]> splitRotation(int numSplits, boolean padding){
        ArrayList<int[][]> splits = new ArrayList<int[][]>();
        int pad = 0;
        
        if(padding)
            pad = 1;
        int splitIndex = 0;
        int spokeIndex = 0;
        int cellIndex;


        //split iterate
        while(splitIndex < numSplits){
            int[][] split = new int[(spokes.size()/numSplits)+(pad*2)][RadarSpoke.maxCells+(pad*2)];
            
            //spoke iterate
            while(spokeIndex < spokes.size() && spokes.get(spokeIndex).spokeNum < spokes.size()/numSplits*(splitIndex+1)){
                cellIndex = 0;
                ArrayList<RadarCell> cells = spokes.get(spokeIndex).getCells();
                
                //cell iterate
                while(cellIndex < cells.size()){
                    RadarCell cell = cells.get(cellIndex);
                    split[(cell.spokeIdx % (spokes.size()/numSplits))+pad][cell.cellIdx+pad] = cell.echo;
                    cellIndex++;
                }//end cell
                spokeIndex++;
            }//end spoke
            splits.add(split);
            splitIndex++;
        }//end split
        
        return splits;
    }
    
    public void analyseTargets(){
        
    }//analyseTargets
    
    public String getDate(){
        return rotationDate;
    }
    
    public String getStartTime(){
        return startTime;
    }
    
    public ArrayList<RadarSpoke> getSpokes(){
        return spokes;
    }//getSpokes
     
    
    
    
}//RadarRotation