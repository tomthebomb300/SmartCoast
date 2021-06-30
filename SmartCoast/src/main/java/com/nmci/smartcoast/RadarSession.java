/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nmci.smartcoast;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author cormac
 */
class RadarSession {
    //Directory related information
    File directoryPath, filesList[];
    int numRotations;
    String siteName;
    Position radarSite;
    int siteElevation;
    RadarTargetTable masterTargets;
    
    private static final int TARGET_ANALYSIS_REALTIME = 0;
    private static final int TARGET_ANALYSIS_SINGLE_ROTATION = 1;
    private static final int TARGET_ANALYSIS_MULTIPLE_ROTATIONS = 2;
    
    RadarSession(File directoryPath){
        //read station info. from station.txt
         try ( Scanner sc = new Scanner(new File(directoryPath + "/station.txt"))) {
                siteName = sc.nextLine();
                //System.out.println(siteName);
                double lat = Double.valueOf(sc.nextLine());
                //System.out.println(lat);
                double lng = Double.valueOf(sc.nextLine());
                //System.out.println(lng);
                try{
                    radarSite = new Position(lat,lng);
                }
                    catch(InvalidPositionException ipe){   
                    }
                siteElevation = Integer.valueOf(sc.nextLine());
         }
         catch(Exception e){
             //graceful recovery... just later ;-)
         }
        
        
        
        
        filesList = directoryPath.listFiles();
        //Sort by Timestamp...
        Arrays.sort(filesList, (f1, f2) -> f1.compareTo(f2));
    }//constructor
    
    public File[] getRotFiles(){
        return filesList;
    }//getRotFiles
    
    public int getNumRotations(){
        return filesList.length;
    }//getNumRotations
    
    public String getSiteName(){
        return siteName;
    }//getSiteName
    
    public String getSiteLat(){
        return radarSite.toStringDegMinLat();
    }//getSiteLat
    
    public String getSiteLng(){
        return radarSite.toStringDegMinLng();
    }//getSiteLng
    
    public String getElevation(){
        return String.valueOf(siteElevation);
    }//getElevation
    
    public RadarTargetTable getMasterTargets(){
        return masterTargets;
    }//getMasterTargets
    
    public void runTargetsAnalysis(){
        //in real time (includes target updating & deletion across time)
        //for a single rotation (static once-off target identification)
        //for multiple rotations (includes target updating & deletion across time)
        //for all rotations (includes target updating & deletion across time)
        
        //process each rotation
            //for each rotation, process each spoke
            //update rotation target table
        //update master target table
        
    }//runTargetAnalysis
    
    public void identifyMaskTargets(){
        //identify what targets should be masked as they're permanent, non-moving
        //and constant
        
        //note; there will need to be a mechanism to exempt some such targets
        //as they are of interest e.g. buoys, anchored ships etc.
        
    }//identifyMaskTargets
    
    public void classifyTargets(){
        
    }//classifyTargets
    
}//RadarSession
