/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nmci.smartcoast;

//for graphics handling
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.BorderLayout;
import javax.imageio.ImageIO; //needed for writing image to file only
import javax.swing.event.MouseInputAdapter;

//for radar dat file handing
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.util.ArrayList;

//for processing radar data
import javax.swing.SwingWorker;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cormac
 */
public class RadarDisplay {

    private static JFrame radarWindow;
    private static final String BASE_DIR = "C:\\Users\\Thomas O Callaghan\\NMCI Placement\\Radar data\\";
    private static RadarSession currRadarSession;
    private static File directoryPath, filesList[];
    private static int fileSkipStep;
    private static JLabel lMPerCell, lRangeIR, lSpokeSeqNums,
                          lActiveCells, lNumRotFiles, lRot, lDate, lTime, lWarning, lNum, 
                          lNumRotForRefRot, lthreshold, lNumRotForWetRot, lWetThreshold;
    private static JButton bLoad, bStepFwd, bStepBck, bRun, bStop, bReset, bClear;
    private static JComboBox cbDirectoryList, cbRotationFileList, cbFileSkipStep;
    private static JCheckBox ckBxDrawGraphics, ckBxApplyRefMask;
    private static JTextField txtBxNumRotForRefRot, txtBxthreshold, txtBxNumRotForWetRot, txtBxWetThreshold;
    private static PPIPanel ppiPanel;
    private static SwingWorker swPMR;
    
    
    public static void main(String[] argS) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAppWindow();  
            }
        });
    }//main
    
    private static void createAppWindow() {
        
        radarWindow = new JFrame("SmartCoast...");
        
        radarWindow.setResizable(false);
        radarWindow.setLayout(new BorderLayout());
        radarWindow.setBackground(Color.black);
        radarWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        radarWindow.setSize(1400, 1200);

        Font font = new Font("Courier", Font.PLAIN, 12);
        lRangeIR = new JLabel("       Range (I.R.):");
        lMPerCell = new JLabel("    Metres per Cell:");
        lSpokeSeqNums = new JLabel("     Spoke (seq) # : ");
        lActiveCells = new JLabel("       Active Cells:");
        lNumRotFiles = new JLabel("# Rotation Files:");
        lRot = new JLabel("Rotation:");
        lDate = new JLabel("   Data Record Date: ");
        lTime = new JLabel("Rotation start time: ");
        lNumRotForRefRot = new JLabel("Num rots for refrence rot?");
        lthreshold = new JLabel("Threshold?");
        lNumRotForWetRot = new JLabel("Num rots for wet?");
        lWetThreshold = new JLabel("Wet Threshold?");

        cbDirectoryList = new JComboBox();
        cbDirectoryList.setBounds(5, 10, 230, 30);
        lNumRotFiles.setBounds(245, 10, 200, 30);
        lNumRotFiles.setBackground(Color.BLACK);
        lNumRotFiles.setForeground(Color.YELLOW);
        lNumRotFiles.setFont(font);

        cbRotationFileList = new JComboBox();
        cbRotationFileList.setBounds(5, 50, 170, 30);
        lRot.setBounds(185, 50, 100, 30);
        lRot.setBackground(Color.BLACK);
        lRot.setForeground(Color.YELLOW);
        lRot.setFont(font);

        lDate.setBounds(5, 80, 400, 30);
        lDate.setBackground(Color.BLACK);
        lDate.setForeground(Color.YELLOW);
        lDate.setFont(font);
        
        lTime.setBounds(5, 95, 400, 30);
        lTime.setBackground(Color.BLACK);
        lTime.setForeground(Color.YELLOW);
        lTime.setFont(font);
        
        lRangeIR.setBounds(5, 110, 300, 30);
        lRangeIR.setBackground(Color.BLACK);
        lRangeIR.setForeground(Color.YELLOW);
        lRangeIR.setFont(font);
        
        lMPerCell.setBounds(5, 125, 200, 30);
        lMPerCell.setBackground(Color.BLACK);
        lMPerCell.setForeground(Color.YELLOW);
        lMPerCell.setFont(font);
        
        lSpokeSeqNums.setBounds(5, 140, 250, 30);
        lSpokeSeqNums.setBackground(Color.BLACK);
        lSpokeSeqNums.setForeground(Color.YELLOW);
        lSpokeSeqNums.setFont(font);
        
        lActiveCells.setBounds(5, 155, 200, 30);
        lActiveCells.setBackground(Color.BLACK);
        lActiveCells.setForeground(Color.YELLOW);
        lActiveCells.setFont(font);
        
        lNumRotForRefRot.setBounds(1250, 40, 200, 30);
        lNumRotForRefRot.setBackground(Color.BLACK);
        lNumRotForRefRot.setForeground(Color.YELLOW);
        lNumRotForRefRot.setFont(font);
        
        lthreshold.setBounds(1250, 70, 200, 30);
        lthreshold.setBackground(Color.BLACK);
        lthreshold.setForeground(Color.YELLOW);
        lthreshold.setFont(font);
        
        lNumRotForWetRot.setBounds(1250, 100, 200, 30);
        lNumRotForWetRot.setBackground(Color.BLACK);
        lNumRotForWetRot.setForeground(Color.YELLOW);
        lNumRotForWetRot.setFont(font);
        
        lWetThreshold.setBounds(1250, 130, 200, 30);
        lWetThreshold.setBackground(Color.BLACK);
        lWetThreshold.setForeground(Color.YELLOW);
        lWetThreshold.setFont(font);
        
        font = new Font("Courier", Font.BOLD, 14);
        lWarning = new JLabel("Warnings: Nil");
        lWarning.setBounds(900, 10, 500, 30);
        lWarning.setBackground(Color.BLACK);
        lWarning.setForeground(Color.RED);
        lWarning.setFont(font);
        
        bLoad = new JButton("Load");
        bLoad.setBounds(25, 185, 100, 30);
        
        cbFileSkipStep = new JComboBox();
        cbFileSkipStep.setBounds(25,225,100,30);
        cbFileSkipStep.addItem("++All++");
        cbFileSkipStep.addItem(" 1 min ");
        cbFileSkipStep.addItem(" 2 mins");
        cbFileSkipStep.addItem(" 3 mins");
        cbFileSkipStep.addItem(" 5 mins");
        cbFileSkipStep.addItem("10 mins");
        cbFileSkipStep.addItem("15 mins");
        cbFileSkipStep.addItem("30 mins");
        cbFileSkipStep.addItem("60 mins");

        bStepBck = new JButton("<");
        bStepBck.setBounds(25, 265, 45, 30);

        bStepFwd = new JButton(">");
        bStepFwd.setBounds(80, 265, 45, 30);
        
        font = new Font("Courier", Font.PLAIN, 12);
        ckBxDrawGraphics = new JCheckBox("Draw Graphics?");
        ckBxDrawGraphics.setBounds(25, 305, 140, 30);
        ckBxDrawGraphics.setSelected(true);
        ckBxDrawGraphics.setBackground(Color.BLACK);
        ckBxDrawGraphics.setForeground(Color.YELLOW);
        ckBxDrawGraphics.setFont(font);
        
        ckBxApplyRefMask = new JCheckBox("Apply Refrence Mask?");
        ckBxApplyRefMask.setBounds(25, 500, 200, 30);
        ckBxApplyRefMask.setSelected(false);
        ckBxApplyRefMask.setBackground(Color.BLACK);
        ckBxApplyRefMask.setForeground(Color.YELLOW);
        ckBxApplyRefMask.setFont(font);

        bRun = new JButton("Run >>");
        bRun.setBounds(25, 345, 100, 30);
        
        bStop = new JButton("Stop ||");
        bStop.setBounds(25, 385, 100, 30);
        
        bReset = new JButton("Reset");
        bReset.setBounds(25, 425, 100, 30);
        
        bClear = new JButton("Clear Display");
        bClear.setBounds(25, 465, 120, 30);
        
        txtBxNumRotForRefRot = new JTextField("20");
        txtBxNumRotForRefRot.setBounds(1190, 40, 50, 30);
        
        txtBxthreshold = new JTextField("0.5");
        txtBxthreshold.setBounds(1190, 70, 50, 30);
        
        txtBxNumRotForWetRot = new JTextField("20");
        txtBxNumRotForWetRot.setBounds(1190, 100, 50, 30);
        
        txtBxWetThreshold = new JTextField("0.5");
        txtBxWetThreshold.setBounds(1190, 130, 50, 30);
        
        directoryPath = new File (BASE_DIR);
        
        //List of all directories
        filesList = directoryPath.listFiles();
        //Sort by Timestamp...
        Arrays.sort(filesList, (f1, f2) -> f1.compareTo(f2));
        //add directories to the dropdown list
        for (File file : filesList) {
            cbDirectoryList.addItem(file.getName());
        }

        radarWindow.add(cbDirectoryList);
        radarWindow.add(cbRotationFileList);
        radarWindow.add(lNumRotFiles);
        radarWindow.add(lDate);
        radarWindow.add(lTime);
        radarWindow.add(lRangeIR);
        radarWindow.add(lMPerCell);
        radarWindow.add(lSpokeSeqNums);
        radarWindow.add(lActiveCells);
        radarWindow.add(lRot);
        radarWindow.add(lWarning);
        radarWindow.add(lNumRotForRefRot);
        radarWindow.add(lthreshold);
        radarWindow.add(lNumRotForWetRot);
        radarWindow.add(lWetThreshold);
        radarWindow.add(txtBxNumRotForRefRot);
        radarWindow.add(txtBxthreshold);
        radarWindow.add(txtBxNumRotForWetRot);
        radarWindow.add(txtBxWetThreshold);

        bLoad.addActionListener((ActionEvent e) -> {
            CountDownLatch latch = new CountDownLatch(0);
            processASingleRotation(filesList[cbRotationFileList.getSelectedIndex()], latch);
            try {
                latch.await(); // Wait for countdown
            } catch (InterruptedException e1) {
                //graceful recovery...
            }
        });

        bStepFwd.addActionListener((ActionEvent e) -> {
            stepForwardNRotations();
        });

        bStepBck.addActionListener((ActionEvent e) -> {
            stepBackNRotations();
        });

        bRun.addActionListener((ActionEvent e) -> {
            processMultipleRotations();
        });

        bStop.addActionListener((ActionEvent e) -> {
            stopProcessMultipleRotations();
        });
        
        bReset.addActionListener((ActionEvent e) -> {
            currRadarSession = null;
            filesList = null;
            lRangeIR.setText("       Range (I.R.):");
            lMPerCell.setText("    Metres per Cell:");
            lSpokeSeqNums.setText("     Spoke (seq) # : ");
            lActiveCells.setText("       Active Cells:");
            lNumRotFiles.setText("# Rotation Files:");
            lDate.setText("   Data Record Date: ");
            lTime.setText("Rotation start time: ");
            lWarning.setText("Warnings: Nil");
            cbRotationFileList.removeAllItems();
            lRot.setText("Rotation: ");
            ckBxDrawGraphics.setSelected(true);
            ckBxApplyRefMask.setSelected(false);
            txtBxNumRotForRefRot.setText("10");
            txtBxthreshold.setText("0.3");
            txtBxNumRotForWetRot.setText("10");
            txtBxWetThreshold.setText("0.3");
            radarWindow.setTitle("SmartC0ast...");
        });
        
        bClear.addActionListener((ActionEvent e) -> {
            clearDisplay();
        });
        
        cbRotationFileList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lRot.setText("Rotation: " + (cbRotationFileList.getSelectedIndex() +1));
            }
        });        
       
        cbDirectoryList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                directoryPath = new File(BASE_DIR + 
                        cbDirectoryList.getSelectedItem().toString());
                System.out.println(directoryPath.toString());
                //Create a Radar session object from station.txt file
                currRadarSession = new RadarSession(directoryPath);
                //Get all files for this session
                filesList = currRadarSession.getRotFiles();
                lNumRotFiles.setText("# Rotation Files: " + currRadarSession.getNumRotations());

               //populate the rotation selection combobox
                cbRotationFileList.removeAllItems();
                for (File file : filesList) {
                    cbRotationFileList.addItem(file.getName());
                }
//                radarWindow.setTitle(currRadarSession.getSiteName() + " (" 
//                        + currRadarSession.getSiteLat()
//                        + "  " + currRadarSession.getSiteLng() + ",  " + 
//                        currRadarSession.getElevation() + "m)");
//                ppiPanel.initialisePPIImages();
            }
        });

        //add buttons to the App window
        radarWindow.add(bLoad);
        radarWindow.add(cbFileSkipStep);
        radarWindow.add(bStepFwd);
        radarWindow.add(bStepBck);
        radarWindow.add(bRun);
        radarWindow.add(ckBxDrawGraphics);
        radarWindow.add(bStop);
        radarWindow.add(bReset);
        radarWindow.add(bClear);
        radarWindow.add(ckBxApplyRefMask);

        //Add the PPIPanel to the App window
        ppiPanel = new PPIPanel();
        radarWindow.add(ppiPanel, BorderLayout.CENTER);
        radarWindow.pack();
        radarWindow.setVisible(true);
    }//createPPI
    
    private static void clearDisplay(){
        ppiPanel.clearDisplay();
        radarWindow.repaint();
    }

    private static void processASingleRotation(File rotationFile, CountDownLatch latch) {
        
//        RadarRotation r = new RadarRotation(rotationFile);
//        r.findTargets();
        
        SwingWorker sw1 = new SwingWorker() {
            
            @Override
            protected String doInBackground() throws Exception {

                //Process the rotation
                RadarRotation rotation = new RadarRotation(rotationFile);
                lDate.setText("   Data Record Date: " + rotation.getDate());
                lTime.setText("Rotation start time: " + rotation.getStartTime());
                lWarning.setText("Warnings: Nil");

                //check for spoke count errors and log them 
                if(rotation.getSpokes().size() < 2048){
                    lWarning.setText("Warnings: Spokes: " + rotation.getSpokes().size() + " (" + rotation.getStartTime() + ")");
                     BufferedWriter writer = new BufferedWriter(new FileWriter(BASE_DIR + "error.log", true));
                    writer.append("Warnings: Spokes: " + rotation.getSpokes().size() + " (" + rotation.getStartTime() + ")\n");
                    writer.close();
                }
                
                RadarRotation refrenceMask = null;
                if(ckBxApplyRefMask.isSelected()){
                    int numRotations = Integer.parseInt(txtBxNumRotForRefRot.getText());
                    
                    //creating refrenceMask using dry rotations.
                    RadarRotation dryMask = createRefrenceMask(new File("C:\\Users\\Thomas O Callaghan\\NMCI Placement\\Radar data\\35000m"),
                                                            (Double.parseDouble(txtBxthreshold.getText()))*numRotations,
                                                            numRotations);
                    
                    numRotations = Integer.parseInt(txtBxNumRotForWetRot.getText());
                    
                    //creating refrenceMask using wet rotations.
                    RadarRotation wetMask = createRefrenceMask(new File("C:\\Users\\Thomas O Callaghan\\NMCI Placement\\Radar data\\rain_35000m"),
                                                            (Double.parseDouble(txtBxWetThreshold.getText()))*numRotations,
                                                            numRotations);
                    
                    //combine the two masks adding the edges and higher echo values to the dry mask. 
                    refrenceMask = addEdgesAndMaxValues(dryMask, wetMask);
                    //apply the refrenceMask to the rotation
                    applyRefMask(refrenceMask, rotation);
                }
                
                rotation.findTargets();
                
                for (RadarSpoke rs : rotation.getSpokes()) {
                    //List l = new <RadarSpoke>ArrayList();
                    //l.add(rs);
                    //process(l);
                    //update the PPI Images if drawing graphics is selected
                    if (ckBxDrawGraphics.isSelected()){
                        if(rs.getCells().size() != 0)
                            ppiPanel.updatePPIImages(rs);
                        lSpokeSeqNums.setText("     Spoke (seq) # : " + rs.getSpokeNum()
                            + " (" + rs.getSeqNum() + ")");
                    lActiveCells.setText("       Active Cells: " + rs.getActiveCellCount());
                    }
                    lMPerCell.setText("    Metres per Cell: " + String.format("%.3f", rs.getMPC()) + "m");
                    lRangeIR.setText("       Range (I.R.): " + String.format("%.1f", rs.getOverscanRange())
                            + "km (" + String.format("%.1f", rs.getInstrumentedRange()) + "km)");
                    
                    radarWindow.repaint();
                }
                
                lSpokeSeqNums.setText("     Spoke (seq) # :");
                lActiveCells.setText("       Active Cells: ");

                //advise this thread is comlete
                latch.countDown();
                return "";
            }//doInBackground

            @Override
            protected void done() {
                // this method is called when the background 
                // thread finishes execution
                //System.out.println("Done");
            }//done
        };
        // executes the swingworker on worker thread
        sw1.execute();

    }//processASingleRotation   


    private static void stepForwardNRotations() {
        setFileSkipStep();
        CountDownLatch latch = new CountDownLatch(0);
        processASingleRotation(filesList[cbRotationFileList.getSelectedIndex() + fileSkipStep], latch);
        cbRotationFileList.setSelectedIndex(cbRotationFileList.getSelectedIndex() + fileSkipStep);
        try {
            latch.await(); // Wait for countdown
        } catch (InterruptedException e) {
            //graceful recovery...
        }
    }//stepForwardARotation

    private static void stepBackNRotations() {
        setFileSkipStep();
        CountDownLatch latch = new CountDownLatch(0);
        processASingleRotation(filesList[cbRotationFileList.getSelectedIndex() - fileSkipStep], latch);
        cbRotationFileList.setSelectedIndex(cbRotationFileList.getSelectedIndex() - fileSkipStep);
        try {
            latch.await(); // Wait for countdown
        } catch (InterruptedException e) {
            //graceful recovery...
        }
    }
    
    private static void setFileSkipStep() {
        switch (cbFileSkipStep.getSelectedItem().toString()) {
            case "++All++":
                fileSkipStep = 1;
                break;

            case " 1 min ":
                fileSkipStep = 16;
                break;

            case " 2 mins":
                fileSkipStep = 32;
                break;

            case " 3 mins":
                fileSkipStep = 48;
                break;

            case " 5 mins":
                fileSkipStep = 80;
                break;

            case "10 mins":
                fileSkipStep = 160;
                break;

            case "15 mins":
                fileSkipStep = 240;
                break;

            case "30 mins":
                fileSkipStep = 480;
                break;

            case "60 mins":
                fileSkipStep = 960;
                break;

        }
    }//setFileSkipStep

    private static void processMultipleRotations() {

        swPMR = new SwingWorker() {

            @Override
            protected String doInBackground() throws Exception {

                try {
                    int startFileIdx = cbRotationFileList.getSelectedIndex();
                    setFileSkipStep();
                    //from selected file index, process files
                    for (int i = startFileIdx; i < filesList.length;
                            i += fileSkipStep) {
                        CountDownLatch latch = new CountDownLatch(1);
                        cbRotationFileList.setSelectedIndex(i);
                        processASingleRotation(filesList[i], latch);
                        try {
                            latch.await(); // Wait for countdown
                        } catch (InterruptedException e) {
                            //graceful recovery...
                        }
                        //if the user has clicked the Stop button, abort processing
                        if (swPMR.isCancelled()) {
                            return "Abort at user request";
                        }
                    }
                }//try//try
                catch (Exception e) {

                }
                return "Completed processing all rotations";
            }//doInBackground

            @Override
            protected void done() {
                // this method is called when the background 
                // thread finishes execution
                //System.out.println("Completed processing all rotations");
            }//done
        };
        // executes the swingworker on worker thread
        swPMR.execute();

    }//processMultipleRotations

    private static void stopProcessMultipleRotations() {
        swPMR.cancel(true);
    }//stopProcessMultipleRotations
    
    private static RadarRotation createRefrenceMask(File file, Double THRESHOLD, int NUM_ROTATIONS){
        //This method will create all refrence masks
        
        List<File> fileList = Arrays.asList(file.listFiles());
        RadarRotation comparisonRotation;
        RadarRotation refrenceMask = new RadarRotation(fileList.get(0));
        ArrayList<RadarSpoke> spokes = refrenceMask.getSpokes();
        
        int fileIndex = 1;
        int[][] count = new int[2048][1024];
        int[][] maxValues = new int[2048][1024];
        
        //set all cells maxValue to there current echo value + increment active cells into 2d count
        for(RadarSpoke spoke : spokes){
            ArrayList<RadarCell> cells = spoke.getCells();

            for(RadarCell cell : cells){
                maxValues[cell.spokeIdx][cell.cellIdx] = cell.echo;
                count[cell.spokeIdx][cell.cellIdx] = 1;
            }
        }
        
        //loop to craete mask.
        while(fileIndex < NUM_ROTATIONS && fileIndex < fileList.size()){
            comparisonRotation = new RadarRotation(fileList.get(fileIndex));
            ArrayList<RadarSpoke> comparisonSpokes = comparisonRotation.getSpokes();

            //loop to cycle through spokes;
            for(int spokeIndex = 0; spokeIndex < spokes.size(); spokeIndex++){
                RadarSpoke spoke = spokes.get(spokeIndex);

                //loop to find the same spoke as refrenceMask spoke and compare;
                for(RadarSpoke comparisonSpoke : comparisonSpokes){

                    //jump in if spokeNum matches.
                    if(comparisonSpoke.spokeNum == spoke.spokeNum){
                        ArrayList<RadarCell> cells = spoke.getCells();
                        ArrayList<RadarCell> comparisonCells = comparisonSpoke.getCells();

                        //loop through comparisonCells and count activity and record new cells and maxValues
                        for(RadarCell ComparisonCell : comparisonCells){

                            if(count[ComparisonCell.spokeIdx][ComparisonCell.cellIdx] == 0){    //if cell is new
                                maxValues[ComparisonCell.spokeIdx][ComparisonCell.cellIdx] = ComparisonCell.echo;
                                insertCell(cells, ComparisonCell);
                            }
                            else if(ComparisonCell.echo > maxValues[ComparisonCell.spokeIdx][ComparisonCell.cellIdx]){    //if ComparisonCell has value greater then maxValue
                                maxValues[ComparisonCell.spokeIdx][ComparisonCell.cellIdx] = ComparisonCell.echo;

                                //find cell in refrenceMask and change echo value
                                for(int i = 0; i < cells.size(); i++){
                                    if(cells.get(i).cellIdx == ComparisonCell.cellIdx){
                                        cells.get(i).echo = ComparisonCell.echo; 
                                        break;
                                    }
                                }
                            }
                            count[ComparisonCell.spokeIdx][ComparisonCell.cellIdx]++;
                        }
                        break;
                    }
                }
            }
            fileIndex++;
        }


        //if count below THRESHOLD remove from mask
        for(RadarSpoke spoke : spokes){
            ArrayList<RadarCell> cells = spoke.getCells();
            ArrayList<RadarCell> cellsRemove = new ArrayList<RadarCell>();

            for(RadarCell cell : cells){
                if(count[cell.spokeIdx][cell.cellIdx] < THRESHOLD){
                     cellsRemove.add(cell);
                }
            }
            cells.removeAll(cellsRemove);
        }
        return refrenceMask;
    }//createRefrenceMask
    
    private static RadarRotation addEdgesAndMaxValues(RadarRotation dryRefrenceMask, RadarRotation wetRefrenceMask){
        ArrayList<RadarSpoke> spokes = wetRefrenceMask.getSpokes();
        int[][] wetRefrenceMask2d = new int[2048][1024];

        for(RadarSpoke spoke : spokes){
            ArrayList<RadarCell> cells = spoke.getCells();

            for(RadarCell cell : cells){
                wetRefrenceMask2d[cell.spokeIdx][cell.cellIdx] = cell.echo;
            }
        }
        wetRefrenceMask = null;
        spokes = dryRefrenceMask.getSpokes();

        for(RadarSpoke spoke : spokes){
            ArrayList<RadarCell> cells = spoke.getCells();
            ArrayList<RadarCell> cellsAdd = new ArrayList<RadarCell>();
            int spokeIndex = spoke.spokeNum;

            for(int cellIndex = 0; cellIndex < cells.size()-1; cellIndex++){
                RadarCell cellA = cells.get(cellIndex);
                RadarCell cellB = cells.get(cellIndex+1);
                int wetEcho = wetRefrenceMask2d[cellA.spokeIdx][cellA.cellIdx];

                //if dryRefrenceMask has lower echo value then wetRefrenceMask
                if(cellA.echo < wetEcho){
                    cellA.echo = wetEcho;
                }

                //jump in if edge found
                if(cellA.cellIdx+1 != cellB.cellIdx){
                    int distance = cellB.cellIdx - cellA.cellIdx;
                    int index = cellA.cellIdx;
                    
                    if(distance > 15)
                        distance = 15;
                    
                    distance =  distance + index;
                    
                    while(++index < distance){
                        wetEcho = wetRefrenceMask2d[spokeIndex][index];
                        
                        if(wetEcho != 0){
                            cellsAdd.add(new RadarCell(spokeIndex, index, wetEcho));
                        }
                    }
                }
            }

            for(RadarCell cell : cellsAdd){
                insertCell(cells, cell);
            }
        }
        return dryRefrenceMask;
    }//addEdgesAndMaxValues
    
    private static void insertCell(ArrayList<RadarCell> cells, RadarCell cell){
        if(cells.get(0).cellIdx > cell.cellIdx){//put element at start of list
            cells.add(0, cell);
        }
        else if(cells.get(cells.size()-1).cellIdx < cell.cellIdx){//put element at end of list
            cells.add(cell);
        }
        else{
            for(int i = 0; i < cells.size()-1; i++){//find correct pos for comparisonCell
                RadarCell c1 = cells.get(i);
                RadarCell c2 = cells.get(i+1);

                if(c1.cellIdx < cell.cellIdx && c2.cellIdx > cell.cellIdx){
                    cells.add(i+1, cell);
                    break;
                }
            }
        }
    }//insertCell
    
    private static void applyRefMask(RadarRotation refrenceMask, RadarRotation rotation){
        //The next two loops are part of applying the mask to rotation
        //modify rotation using refrenceMask
        int[][] refrenceMask2D = new int[2048][1024];
        ArrayList<RadarSpoke> spokes = refrenceMask.getSpokes();
        
        for(RadarSpoke spoke : spokes){
            ArrayList<RadarCell> cells = spoke.getCells();
            
            for(RadarCell cell : cells){
                refrenceMask2D[cell.spokeIdx][cell.cellIdx] = cell.echo;
            }
        }
        
        spokes = rotation.getSpokes();
        //loop to go through rotation and apply the mask
        for(int spokeIndex = 0; spokeIndex < spokes.size(); spokeIndex++){
            ArrayList<RadarCell> cells = spokes.get(spokeIndex).getCells();
            ArrayList<RadarCell> cellsRemove = new ArrayList<RadarCell>();
            
            for(int cellIndex = 0; cellIndex < cells.size(); cellIndex++){
                RadarCell cell = cells.get(cellIndex);
                
                //Leave any cells that have higher value then refrenceMask maxValue
                //Remove cells with value less then refrenceMask maxValue
                if(cell.echo <= refrenceMask2D[cell.spokeIdx][cell.cellIdx])
                    cellsRemove.add(cell);
            }
            cells.removeAll(cellsRemove);
            spokes.get(spokeIndex).activeCellCount = cells.size();
        }
    }//applyRefMask
    
    private static void printRefrenceMask(RadarRotation mask, String name) throws Exception{
        FileWriter writer = new FileWriter("C:\\Users\\Thomas O Callaghan\\NMCI Placement\\"+name);
        int[][] mask2d = new int[2048][1024];
        
        ArrayList<RadarSpoke> spokes = mask.getSpokes();
        
        for(RadarSpoke spoke : spokes){
           ArrayList<RadarCell> cells = spoke.getCells();

           for(RadarCell cell : cells){
              mask2d[cell.spokeIdx][cell.cellIdx] = 1; 
           }
        }
        
        for(int row = 0; row < 2048; row++){
               
               for(int col = 0; col < 1024; col++){
                   writer.write(mask2d[row][col]+", ");
               }
               writer.write("\n");
           }
    }
}//HalpinRadar


class PPIPanel extends JPanel {

    private final int ppiDiameter = 1200;
    private int ppiCentreX = ppiDiameter / 2;
    private int ppiCentreY = ppiDiameter / 2;
    private static BufferedImage ppiFullSizeImage;
    private static BufferedImage ppiDisplayImage;

    @Override
    public Dimension getPreferredSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //return new Dimension(screenSize.height - 200, screenSize.height - 200);
        return new Dimension(1500, 800);
    }//getPreferredSize

    public PPIPanel() {
        initPanel();
    }//MyPanel

    private void initPanel() {
        setBorder(BorderFactory.createLineBorder(Color.white));
        setBackground(Color.black);
        //setLayout(new FlowLayout());
        initialisePPIImages();
    }//initPanel

    public void initialisePPIImages() {
        ppiFullSizeImage = new BufferedImage(2048, 2048, BufferedImage.TYPE_INT_RGB);
        ppiDisplayImage = new BufferedImage(1100, 1100, BufferedImage.TYPE_INT_RGB);

        // Create a graphics which can be used to draw into the buffered image
        Graphics2D ppiImageg = ppiFullSizeImage.createGraphics();
        //set anit-aliasing
//        ppiImageg.setRenderingHint(
//                RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);

        // fill all the image with black to start
        ppiImageg.setColor(Color.black);
        ppiImageg.fillRect(0, 0, 2048, 2048);

        //Draw the PPI outline circle
        ppiImageg.setColor(Color.yellow);
        ppiImageg.drawOval(0, 0, 2048, 2048);
        //Draw the PPI centre (radar location) crosshair
        ppiImageg.drawLine((2048 / 2) - 10,
                2048 / 2,
                (2048 / 2) + 10,
                2048 / 2);
        ppiImageg.drawLine(2048 / 2,
                (2048 / 2) - 10,
                2048 / 2,
                (2048 / 2) + 10);
        Font font = new Font("Courier", Font.BOLD, 32);
        ppiImageg.setFont(font);
        
        ppiImageg.drawString("STANDBY...", (2048 / 2) - 70, (2048 / 2) - 20);

        resizePPIImageForDisplay();
        ppiImageg.dispose();
        repaint();
    }//initialisePPIImages

    public void updatePPIImages(RadarSpoke rs) {
        //update the Bufferedimage for the latest spoke
        
// Create a graphics which can be used to draw into the buffered image
        Graphics2D ppiImageg = (Graphics2D) ppiFullSizeImage.getGraphics();
        
        //set anti-aliasing
//        ppiImageg.setRenderingHint(
//                RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);

        //Process cells:
        //convert polar coordinates (bearing / range) to x,y for drawing
        Color echoColour = new Color(0,0,0);
        int[] scrCoOrds;
        int spokeCellIterator = 0;
        
        for (int cIdx = 0; cIdx < 1024; cIdx++) {
            
            if(cIdx == rs.getCells().get(spokeCellIterator).getCellIdx()){
                //System.out.println("cIdx: " + cIdx + " sCI: " + spokeCellIterator + " cellIdx: " + rs.getCells().get(spokeCellIterator).getCellIdx());
                echoColour = getColours(rs.getCells().get(spokeCellIterator).getCellEcho());
                spokeCellIterator++;
            }
            else{
                echoColour = new Color(0,0,0); //black
                 
            }
            //System.out.println("  Echo: " + rs.getCells().get(spokeCellIterator).getCellEcho() + " Colour: " + echoColour.toString());
            ppiImageg.setColor(echoColour);
            scrCoOrds = getScreenCoordinates(rs.getSpokeNum(), cIdx);
            ppiImageg.drawOval(scrCoOrds[0], scrCoOrds[1], 3, 3);
            if(spokeCellIterator == rs.getCells().size()){
                //no more echoes to process this spoke
                //System.out.println(rs.getCells().get(rs.getCells().size()-1).getCellIdx() + "; breaking as no more active cells this spoke");
                break;
            }
        }
        //overpaint all remaining cells with black
        echoColour = new Color(0,0,0);
        ppiImageg.setColor(echoColour);
        //get the index of the last active cell + 1 i.e. the first blank cell thereafter
        
        for(int i = rs.getCells().get(rs.getCells().size()-1).getCellIdx()+1; i<1024; i++){
            //System.out.println("Painting empty cell: " + i);
            scrCoOrds = getScreenCoordinates(rs.getSpokeNum(), i);
            ppiImageg.drawOval(scrCoOrds[0], scrCoOrds[1], 3, 3);
        }
          
        //reDraw the PPI outline circle
        ppiImageg.setColor(Color.yellow);
        ppiImageg.drawOval(0, 0, 2048, 2048);
        //reDraw the PPI centre (radar location) crosshair
        ppiImageg.drawLine((2048 / 2) - 10,
                2048 / 2,
                (2048 / 2) + 10,
                2048 / 2);
        ppiImageg.drawLine(2048 / 2,
                (2048 / 2) - 10,
                2048 / 2,
                (2048 / 2) + 10);

        ppiImageg.dispose();

        //only update image every 2 degrees or so
        if (rs.getSpokeNum() % 10 == 0) {
            resizePPIImageForDisplay();
        }
          //resizePPIImageForDisplay();

        //write out an image file -debugging/checking
        if (rs.spokeNum == 2047) {
            try {
                File outputfile = new File("C:\\Users\\Thomas O Callaghan\\NMCI Placement\\SmartCoast\\saved.png");
                ImageIO.write(ppiFullSizeImage, "png", outputfile);
            } catch (Exception e) {
                //...
            }
        }                //...
    //System.out.println("Finished UpdatePPIImages");
    }//updatePPIImage
    
    private static Color getColours(int echo) {
        Color rgb = new Color(0, 0, 0);
        //System.out.println(echo);
        switch (echo) {
            case 0 -> {
                rgb = new Color(0, 0, 0); //"black"
                break;
            }

            case 1 -> {
                rgb = new Color(0, 19, 51);
                break;
            }
            case 2 -> {
                rgb = new Color(0, 37, 102);
                break;
            }
            case 3 -> {
                rgb = new Color(1, 56, 153);
                break;
            }

            case 4 -> {
                rgb = new Color(1, 74, 204);
                break;
            }

            case 5 -> {
                rgb = new Color(1, 93, 255);
                break;
            }

            case 6 -> {
                rgb = new Color(0, 51, 25);
                break;
            }
            case 7 -> {
                rgb = new Color(0, 102, 50);
                break;
            }

            case 8 -> {
                rgb = new Color(1, 153, 76);
                break;
            }
            case 9 -> {
                rgb = new Color(1, 204, 101);
                break;
            }
            case 10 -> {
                rgb = new Color(1, 255, 126);
                break;
            }
            case 11 -> {
                rgb = new Color(51, 0, 0);
                break;
            }
            case 12 -> {
                rgb = new Color(102, 0, 0);
                break;
            }
            case 13 -> {
                rgb = new Color(153, 1, 1);
                break;
            }
            case 14 -> {
                rgb = new Color(204, 1, 1);
                break;
            }
            case 15 -> {
                rgb = new Color(255, 1, 1);
            }
        }
        //System.out.println("c=" + rgb.toString());
        return rgb;
    }//getColours

    private void resizePPIImageForDisplay() {
        //ppiDisplayImage = Scalr.resize(ppiFullSizeImage, Scalr.Method.BALANCED, ppiDisplayImage.getWidth(), ppiDisplayImage.getHeight());

        int targetWidth = 900;
        int targetHeight = 900;
        float ratio = ((float) ppiFullSizeImage.getHeight() / (float) ppiFullSizeImage.getWidth());
        if (ratio <= 1) { //square or landscape-oriented image
            targetHeight = (int) Math.ceil((float) targetWidth * ratio);
        } else { //portrait image
            targetWidth = Math.round((float) targetHeight / ratio);
        }
        //BufferedImage bi = new BufferedImage(targetWidth, targetHeight, src.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = ppiDisplayImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); //produces a balanced resizing (fast and decent quality)
        g2d.drawImage(ppiFullSizeImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
    }//resizePPIImageForDisplay()

    private int[] getScreenCoordinates(int spokeNum, int cellIdx) {
        int[] scrCoOrds = new int[2];
        double degreesPerSpoke = 0.17578125; //360 degrees / 2048 spokes per rotation
        double angle = 0;
        //System.out.println(screenSize.height);

        switch ((int) spokeNum / 513) {
            case 0 -> {
                //upper right quadrant
                angle = (spokeNum * degreesPerSpoke) - 0;
                scrCoOrds[0] = 2048 / 2 + (int) Math.round(cellIdx * Math.sin(Math.toRadians(angle)));
                scrCoOrds[1] = 2048 / 2 - (int) Math.round(cellIdx * Math.cos(Math.toRadians(angle)));
                //System.out.println("TRQ:" + scrCoOrds[0] + ", " + scrCoOrds[1]);
                break;
            }
            case 1 -> {
                //lower right quadrant
                angle = (spokeNum * degreesPerSpoke) - 90;
                scrCoOrds[1] = 2048 / 2 + (int) Math.round(cellIdx * Math.sin(Math.toRadians(angle)));
                scrCoOrds[0] = 2048 / 2 + (int) Math.round(cellIdx * Math.cos(Math.toRadians(angle)));
                //System.out.println("BRQ:" + scrCoOrds[0] + ", " + scrCoOrds[1]);
                break;
            }
            case 2 -> {
                //lower left quadrant
                angle = (spokeNum * degreesPerSpoke) - 180;
                scrCoOrds[0] = 2048 / 2 - (int) Math.round(cellIdx * Math.sin(Math.toRadians(angle)));
                scrCoOrds[1] = 2048 / 2 + (int) Math.round(cellIdx * Math.cos(Math.toRadians(angle)));
                //System.out.println("BLQ:" + scrCoOrds[0] + ", " + scrCoOrds[1]);
                break;
            }
            case 3 -> {
                //upper left quadrant
                angle = (spokeNum * degreesPerSpoke) - 270;
                scrCoOrds[1] = 2048 / 2 - (int) Math.round(cellIdx * Math.sin(Math.toRadians(angle)));
                scrCoOrds[0] = 2048 / 2 - (int) Math.round(cellIdx * Math.cos(Math.toRadians(angle)));
                //System.out.println("TLQ:" + scrCoOrds[0] + ", " + scrCoOrds[1]);
            }

        }
        return scrCoOrds;
    }

    
    
    public void toggleMapOverlay(){
        //enbable or disable display of a map overlay
    }
    
    public void togglePoIs(){
        //enable or disable display of points of interest
    }
    
    public void toggleTargetOutlines(){
        //enable or disables display of outlines around identified targets
    }
    
    public void toggleTargetsInfo(){
        //enables or disables display of information alongside targets
    }
    
    public void toggleTargetsByType(){
        //enables or disables display of targets of differen types
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(ppiDisplayImage,220, 0, null);
        //Toolkit.getDefaultToolkit().sync();
    }//paintComponent
    
    //Test method;
    public void clearDisplay(){
        Graphics2D ppiImageg = (Graphics2D) ppiFullSizeImage.getGraphics();
        ppiImageg.setColor(new Color(0,0,0));
        for(int spokeIndex = 0; spokeIndex < 2048; spokeIndex++){
            for(int cellIndex = 0; cellIndex < 1024; cellIndex++){
                int[] coor = getScreenCoordinates(spokeIndex, cellIndex);
                ppiImageg.drawOval(coor[0], coor[1], 3, 3);
            }
        }
        ppiImageg.setColor(Color.yellow);
        ppiImageg.drawOval(0, 0, 2048, 2048);
        //reDraw the PPI centre (radar location) crosshair
        ppiImageg.drawLine((2048 / 2) - 10,
                2048 / 2,
                (2048 / 2) + 10,
                2048 / 2);
        ppiImageg.drawLine(2048 / 2,
                (2048 / 2) - 10,
                2048 / 2,
                (2048 / 2) + 10);
        
        
        ppiImageg.dispose();
        resizePPIImageForDisplay();

    }
    

}//MyPanel


