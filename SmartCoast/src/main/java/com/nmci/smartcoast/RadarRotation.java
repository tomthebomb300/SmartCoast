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
    
    public void analyseTargets(){
//        for each spoke{
//	extract contiguous echo sequence cells n...m
//	existing = false
//	for each existing target{
//		if n..m has >=1 common cell(s) with spoke s-1 in target
//			add to existing target
//			existing = true
//			break for loop as target found (cell can only be in one target)
//		}//if
//	}//for
//	if existing == false then create new target entry
//}//for
  
    rotationTargets = new RadarTargetTable();
    
    for(RadarSpoke rs: spokes)
    {
        rotationTargets.updateTargetsFromSpoke(rs);
    }
    
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