/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nmci.smartcoast;

import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.opencsv.CSVWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

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
        
        int iteratingSpokeIndex = 0;
        int iteratingCellIndex = 0;
        int caught = 0;
        int splitIndex = 0;        
        
        for(int[][] split : splits){
            iteratingSpokeIndex = 0;
            rotation = split;
            
            while(iteratingSpokeIndex < split.length){
                iteratingCellIndex = 0;

                while(iteratingCellIndex < split[iteratingSpokeIndex].length){
                    
                    if(split[iteratingSpokeIndex][iteratingCellIndex] != 0){
                        ArrayList<RadarCell> cells = new ArrayList<RadarCell>();
                        int spokeIndex = iteratingSpokeIndex;
                        int cellIndex = iteratingCellIndex;
                        
                        try{
                            ArrayList<RadarCell> notSearched = new ArrayList<RadarCell>();
                            
                            while(!findCellsOfTarget(cells, spokeIndex, cellIndex, 0, notSearched) || !notSearched.isEmpty()){
                                RadarCell firstCell = notSearched.get(0);
                                notSearched.remove(0);
                                spokeIndex = firstCell.spokeIdx;
                                cellIndex = firstCell.cellIdx;
                            }
                        }
                        catch(StackOverflowError error){
                            caught++;
                        }

                        RadarTarget target = new RadarTarget(cells, ++targetId);
                        target.assignAttributes(spokes.get(0).mPC, spokes.get(0).overscanRange, spokes.size());
                        targets.add(target);
                    }
                    iteratingCellIndex++;
                }
                iteratingSpokeIndex++;
            }
            splitIndex++;
        }
        
        rotationTargets = new RadarTargetTable(targets);
        System.out.println("Number of targets: "+targets.size());
        System.out.println("StackOverflows during target searching: "+caught);
    }
    
    
    
    
    
    //writing targets to csv file + calling python file to cluster them
    private void clusterTargets(){
        //write targets to csv
        try{
            String csvName = "targets.csv";
            CSVWriter writer = new CSVWriter(new FileWriter(new File(csvName)));
            String[] header = {"sizeAsCellCount", "areaAsM2", "minRange", "maxRange", "avEchoStrength", "minEchoStrength", "maxEchoStrength"};
            writer.writeNext(header);
            
            for (RadarTarget target : rotationTargets.targets){
                String[] data = {Integer.toString(target.sizeAsCellCount), Double.toString(target.areaAsM2), Double.toString(target.minRange), Double.toString(target.minRange), Double.toString(target.avEchoStrength), Double.toString(target.minEchoStrength), Double.toString(target.maxEchoStrength)};
                writer.writeNext(data);
            }
            writer.close();
            
            ProcessBuilder builder = new ProcessBuilder("python", "clusterTargets.py", csvName);
            Process process = builder.start();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String line;
            
            while((line = errorReader.readLine())!=null){
                System.out.println("PYTHON ERROR: "+line);
            }
            
            while((line = outputReader.readLine())!=null){
                System.out.println(line);
            }
            
            
            //read data from csv file
            BufferedReader csvReader = new BufferedReader(new FileReader(csvName));
            String row = csvReader.readLine();
            int targetIndex = 0;
            
            while((row = csvReader.readLine())!=null){
                String[] data = row.split(",");
                rotationTargets.targets.get(targetIndex).cluster = Integer.parseInt(data[data.length-1]);
                targetIndex++; 
            }
            
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
     
    //method for finding cells of target
    private boolean findCellsOfTarget(ArrayList<RadarCell> target, int spokeIndex, int cellIndex, int recursiveCount, ArrayList<RadarCell> notSearched) throws StackOverflowError{
        if(rotation[spokeIndex][cellIndex] != 0)
            target.add(new RadarCell(spokeIndex-1, cellIndex-1, rotation[spokeIndex][cellIndex]));
        rotation[spokeIndex][cellIndex] = 0;
        
        if(++recursiveCount == 2500)
            return false;
        
        
        //left
        if(rotation[spokeIndex][cellIndex-1] != 0){
            boolean targetFound = findCellsOfTarget(target, spokeIndex, cellIndex-1, recursiveCount, notSearched);
            
            if(!targetFound){
                notSearched.add(new RadarCell(spokeIndex, cellIndex, rotation[spokeIndex][cellIndex]));
                return false;
            }
        }

        //right
        if(rotation[spokeIndex][cellIndex+1] != 0){
            boolean targetFound = findCellsOfTarget(target, spokeIndex, cellIndex+1, recursiveCount, notSearched);
            
             if(!targetFound){
                notSearched.add(new RadarCell(spokeIndex, cellIndex, rotation[spokeIndex][cellIndex]));
                return false;
            }
        }

        //top
        if(rotation[spokeIndex-1][cellIndex] != 0){
            boolean targetFound = findCellsOfTarget(target, spokeIndex-1, cellIndex, recursiveCount, notSearched);
            
             if(!targetFound){
                notSearched.add(new RadarCell(spokeIndex, cellIndex, rotation[spokeIndex][cellIndex]));
                return false;
            }
        }

        //bottom
        if(rotation[spokeIndex+1][cellIndex] != 0){
            boolean targetFound = findCellsOfTarget(target, spokeIndex+1, cellIndex, recursiveCount, notSearched);
            
             if(!targetFound){
                notSearched.add(new RadarCell(spokeIndex, cellIndex, rotation[spokeIndex][cellIndex]));
                return false;
            }
        }

        //top left
        if(rotation[spokeIndex-1][cellIndex-1] != 0){
            boolean targetFound = findCellsOfTarget(target, spokeIndex-1, cellIndex-1, recursiveCount, notSearched);
            
             if(!targetFound){
                notSearched.add(new RadarCell(spokeIndex, cellIndex, rotation[spokeIndex][cellIndex]));
                return false;
            }
        }

        //top right
        if(rotation[spokeIndex-1][cellIndex+1] != 0){
            boolean targetFound = findCellsOfTarget(target, spokeIndex-1, cellIndex+1, recursiveCount, notSearched);
            
             if(!targetFound){
                notSearched.add(new RadarCell(spokeIndex, cellIndex, rotation[spokeIndex][cellIndex]));
                return false;
            }
        }

        //bottom left
        if(rotation[spokeIndex+1][cellIndex-1] != 0){
            boolean targetFound = findCellsOfTarget(target, spokeIndex+1, cellIndex-1, recursiveCount, notSearched);
            
             if(!targetFound){
                notSearched.add(new RadarCell(spokeIndex, cellIndex, rotation[spokeIndex][cellIndex]));
                return false;
            }
        }

        //bottom right
        if(rotation[spokeIndex+1][cellIndex+1] != 0){
            boolean targetFound = findCellsOfTarget(target, spokeIndex+1, cellIndex+1, recursiveCount, notSearched);
            
             if(!targetFound){
                notSearched.add(new RadarCell(spokeIndex, cellIndex, rotation[spokeIndex][cellIndex]));
                return false;
            }
        }
        return true;
    }
    
    
    //method that has the optiuon of splitting the rotation and/or adding a padding of 0's around the outside
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
    
    public String getDate(){
        return rotationDate;
    }
    
    public String getStartTime(){
        return startTime;
    }
    
    public ArrayList<RadarSpoke> getSpokes(){
        return spokes;
    }//getSpokes
    
    
    
    //***************************
    //test methods
    //***************************
    
    //print target to console.
    private static void printTarget(RadarTarget target){
        int[][] r = new int[2048][1024];
        ArrayList<RadarCell> latest = target.latest;
        
        for(RadarCell cell : latest){
            r[cell.spokeIdx][cell.cellIdx] = cell.echo;
        }
        
        int spokeIndex = 0;
        int cellIndex;
        
        while(spokeIndex < r.length){
            cellIndex = 0;
            
            while(cellIndex < r[spokeIndex].length){
                System.out.print(r[spokeIndex][cellIndex]);
                cellIndex++;
            }
            System.out.println();
            spokeIndex++;
        }
    }
    
    
    
    
    
    
    
    //Create excel file of rotationand highlight the given target
    private void printTargetExcel(RadarTarget target){
        ArrayList<int[][]> splits = this.splitRotation(1, false);
        rotation = splits.get(0);
        
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("rotation");
        XSSFRow Xrow;
        int r = 0;
        int c = 0;
        XSSFCell cell;
        
        while(r < rotation.length){
            Xrow = spreadsheet.createRow(r);
            c = 0;
            
            while(c < rotation[r].length){
                cell = Xrow.createCell(c);
                cell.setCellValue(""+rotation[r][c]);
                c++;
            }
            r++;
        }
        
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        style.setFont(font);
        
        CellStyle styleOutline = workbook.createCellStyle();
        XSSFFont fontOutline = workbook.createFont();
        styleOutline.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        fontOutline.setColor(HSSFColor.HSSFColorPredefined.YELLOW.getIndex());
        styleOutline.setFont(fontOutline);
        
        int[][] outline = new int [2048][1024];
        
        for(RadarCell radarCell : target.outline){
            outline[radarCell.spokeIdx][radarCell.cellIdx] = radarCell.echo;
        }
        
        ArrayList<RadarCell> latest = target.latest;
        int latestIndex = 0;
        
        while(latestIndex < latest.size()){
            RadarCell radarCell = latest.get(latestIndex);
            cell = workbook.getSheetAt(0).getRow(radarCell.spokeIdx).getCell(radarCell.cellIdx);
            if(outline[radarCell.spokeIdx][radarCell.cellIdx] != 0)
                cell.setCellStyle(styleOutline);
            else
                cell.setCellStyle(style);
            latestIndex++;
        }
        
        try{
            workbook.write(new FileOutputStream("Target1NoRefMask.xlsx"));
            workbook.close();
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }   
    }
    
    
    
    
    
    private void printAllTargetsExcel(){
        ArrayList<int[][]> splits = this.splitRotation(1, false);
        rotation = splits.get(0);
        
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("rotation");
        XSSFRow Xrow;
        int r = 0;
        int c = 0;
        XSSFCell cell;
        
        while(r < rotation.length){
            Xrow = spreadsheet.createRow(r);
            c = 0;
            
            while(c < rotation[r].length){
                cell = Xrow.createCell(c);
                cell.setCellValue(""+rotation[r][c]);
                c++;
            }
            r++;
        }
        
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        style.setFont(font);
        
        CellStyle styleOutline = workbook.createCellStyle();
        XSSFFont fontOuline = workbook.createFont();
        styleOutline.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        fontOuline.setColor(HSSFColor.HSSFColorPredefined.YELLOW.getIndex());
        styleOutline.setFont(fontOuline);
        
        for (RadarTarget target : rotationTargets.targets){
            ArrayList<RadarCell> latest = target.latest;
            
            int[][] outline = new int[2048][1024];
            
            for(RadarCell radarCell : target.outline){
                outline[radarCell.spokeIdx][radarCell.cellIdx] = radarCell.echo;
            }
            
            int latestIndex = 0;

            while(latestIndex < latest.size()){
                RadarCell radarCell = latest.get(latestIndex);
                cell = workbook.getSheetAt(0).getRow(radarCell.spokeIdx).getCell(radarCell.cellIdx);
                
                if(outline[radarCell.spokeIdx][radarCell.cellIdx] != 0){
                    cell.setCellStyle(styleOutline);
                }
                else{
                    cell.setCellStyle(style);
                }
                latestIndex++;
            }
        }
        
        
        try{
            workbook.write(new FileOutputStream("AllTargetsWithOutlineHighlighted.xlsx"));
            workbook.close();
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }   
    }
    //***************************
    //test methods
    //***************************
    
    
    
}//RadarRotation