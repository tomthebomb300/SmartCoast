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
    private int count;
    
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
            //System.out.println("Number of spokes: " + spokes.size());

        } catch (Exception e) {

        }
    }//constructor
    
    public int[][] get2DArray(){
        int[][] rotation = null;
        int spokeIndex = 0;
        int cellIndex = 0;
        int numSpokes = spokes.size();
        int numCells;
        
        if(numSpokes != 0 && spokes.get(0) != null){
            rotation = new int[numSpokes][RadarSpoke.maxCells];
            
            while(spokeIndex < numSpokes){
                RadarSpoke spoke = spokes.get(spokeIndex);
                ArrayList<RadarCell> cells = spoke.getCells();
                numCells = cells.size();
                cellIndex = 0;
                
                while(cellIndex < numCells){
                    RadarCell cell = cells.get(cellIndex);
                    rotation[cell.spokeIdx][cell.cellIdx] = cell.echo;
                    cellIndex++;
                }
                spokeIndex++;
            }
        }
        

        return rotation;
    }
    
    public void analyseTargets(){
        System.out.println("analyzeTargets");
        int[][] rotation = this.get2DArray();
        int spokeIndex = 0;
        int cellIndex;
        int numSpokes = rotation.length;
        int numCells;
        count = 0;
        
        ArrayList<RadarTarget> targets = new ArrayList<RadarTarget>();
        
        while(spokeIndex < numSpokes){
            cellIndex = 0;
            numCells = rotation[spokeIndex].length;
            
            while(cellIndex < numCells){
                
                if(rotation[spokeIndex][cellIndex] != 0){
                    ArrayList<RadarCell> targetCells = new ArrayList<RadarCell>();
                    this.getAllCellsOfTarget(rotation, targetCells, spokeIndex, cellIndex);
                    RadarTarget target = new RadarTarget(targetCells);
                    targets.add(target);
                }
                cellIndex++;
            }
            spokeIndex++;
        }
        System.out.println("number of targets: "+targets.size());
    }//analyseTargets
    
    //method to test recursion
    public void recursionTest(){
        count++;
        System.out.println("count: "+count);
        
        if(count < 5000)
            recursionTest();
    }
    //method to test recursion
    
    private void getAllCellsOfTarget(int[][] rotation, ArrayList<RadarCell> targetCells, int spokeIndex, int cellIndex){
        targetCells.add(new RadarCell(spokeIndex, cellIndex, rotation[spokeIndex][cellIndex]));
        rotation[spokeIndex][cellIndex] = 0;
        
        if(cellIndex > 0 && rotation[spokeIndex][cellIndex-1] != 0){//left
            --cellIndex;
        }
        if(cellIndex < RadarSpoke.maxCells-1 && rotation[spokeIndex][cellIndex+1] != 0){//right
            ++cellIndex;
        }
        if(spokeIndex > 0 && rotation[spokeIndex-1][cellIndex] != 0){//top
            --spokeIndex;
        }
        if(spokeIndex < rotation.length-1 && rotation[spokeIndex+1][cellIndex] != 0){//bottom
            ++spokeIndex;
        }
        if(spokeIndex > 0 && cellIndex > 0 && rotation[spokeIndex-1][cellIndex-1] != 0){//left top
            --spokeIndex;
            --cellIndex;
        }
        if(spokeIndex > 0 && cellIndex < RadarSpoke.maxCells-1 && rotation[spokeIndex-1][cellIndex+1] != 0){//right top
            --spokeIndex;
            ++cellIndex;
        }
        if(spokeIndex < rotation.length-1 && cellIndex > 0 && rotation[spokeIndex+1][cellIndex-1] != 0){//left bottom
            ++spokeIndex;
            --cellIndex;
        }
        if(spokeIndex < rotation.length-1 && cellIndex < rotation.length-1 && rotation[spokeIndex+1][cellIndex+1] != 0){//right bottom
            ++spokeIndex;
            ++cellIndex;
        }
        this.getAllCellsOfTarget(rotation, targetCells, spokeIndex, cellIndex);
    }//getAllCellsOfTarget
    
    
    
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