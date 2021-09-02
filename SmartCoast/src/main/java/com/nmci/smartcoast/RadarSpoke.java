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
class RadarSpoke {

    int spokeNum;
    int seqNum;
    float mPC;//M
    float overscanRange;//KM
    float instrumentedRange;//KM
    int activeCellCount;
    ArrayList<RadarCell> cells;

    RadarSpoke(String spokeDataStr) {
         String[] spokeDataArray = spokeDataStr.split(" ");
        spokeNum = Integer.valueOf(spokeDataArray[0]);
        //System.out.println(this.summarise());
        seqNum = Integer.valueOf(spokeDataArray[1]);
        //System.out.println(this.summarise());
        mPC = Float.valueOf(spokeDataArray[2]) / 1000;
        //System.out.println(this.summarise());
        overscanRange = mPC * 1024 / 1000;
        //System.out.println(this.summarise());
        instrumentedRange = overscanRange / (float) 1.8;
        //System.out.println(this.summarise());
        activeCellCount = spokeDataArray.length - 3;
        //System.out.println(this.summarise());
        
        cells = new <RadarCell>ArrayList(spokeDataArray.length-3);
        for (int sdaIdx = 3; sdaIdx < spokeDataArray.length; sdaIdx++){
            RadarCell rc = new RadarCell(
                    spokeNum,
                    Integer.valueOf(spokeDataArray[sdaIdx].substring(0, spokeDataArray[sdaIdx].length() - 1)),
                    Integer.parseInt(spokeDataArray[sdaIdx].substring(spokeDataArray[sdaIdx].length() - 1), 16));
            cells.add(rc);
            //System.out.println(rc.toString());
        }

    }//constructor

    public int getSpokeNum() {
        return spokeNum;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public double getMPC() {
        return mPC;
    }

    public float getOverscanRange() {
        return overscanRange;
    }

    public float getInstrumentedRange() {
        return instrumentedRange;
    }

    public ArrayList<RadarCell> getCells() {
        return cells;
    }

    public int getActiveCellCount() {
        return activeCellCount;
    }

    @Override
    public String toString() {
        String summary;
        summary = "SpokeNum=" + spokeNum + ", ";
        summary += "SeqNum =" + seqNum + ", ";
        summary += "mPC=" + mPC + ", ";
        summary += "OSR=" + overscanRange + ", ";
        summary += "IR=" + instrumentedRange + ", ";
        summary += "ACC=" + activeCellCount;
        return summary;
    }
    
    public ArrayList getTargetParts(){
        
        ArrayList targetParts = new ArrayList();
        
        for(int i=0; i<this.getCells().size();i++){
            //System.out.println("i:" + i);
            ArrayList oneTargetPart = new <RadarCell>ArrayList();
            //Every cell recorded in a radar spoke contains a non-zero echo and
            //therefore is part of a target (or is noise/clutter. We'll deal with
            //noise as part of target classification that comes later
            
            //cycle through cells to find all contiguous sequences of one or 
            //more cells
            oneTargetPart.add(this.getCells().get(i));
            
            //to handle the case where there's a single cell left at the end
            if(i == this.getCells().size()-1){
                for(Object o: oneTargetPart){
                    RadarCell rc = (RadarCell)o;
                    System.out.print(rc.toString() + " ");
                }
                targetParts.add(oneTargetPart);
                break;
            }
            else{
                //see how many subsequent cells are contiguous and add them to
                //the current targetPart
                int cellIdx = this.getCells().get(i).cellIdx;
                int nextCellIdx = this.getCells().get(i+1).cellIdx;
                int seqCounter=i;
                while(nextCellIdx == cellIdx+1){
                    oneTargetPart.add(this.getCells().get(seqCounter+1));
                    seqCounter++;
                    cellIdx = nextCellIdx;
                    nextCellIdx = this.getCells().get(seqCounter+1).cellIdx;
                }
//                for(Object o: oneTargetPart){
//                    RadarCell rc = (RadarCell)o;
//                    System.out.print(rc.toString() + " ");
//                }
//                System.out.println();
            targetParts.add(oneTargetPart);
            i=seqCounter;
            }
        }
        return targetParts;
    }//getTargetParts
    
public static void main(String[] argS) {
       //create a test spoke
       //1790 61523 0d 1f 26 3f 103 12a 13a 149 15a 164 177 181 192 22e 231 24f 25f 26f 27f 283 296 304 327 339 341 353 421 441 455 682 70f 71c 72f 73f 74f 75f 76f 77f 78f 79f 80f 81f 82f 83f 84f 85f 86f 87f 882 897 1751 2002 2025 2036 2044 2052 206b 2077 208f 209f 210f 211f 212c 213f 2148 2158 2164 2179 2181 2209 2216 222b 223b 2241 225a 9883 
       //10 1757 61523 0d 1f 2f 3f 5f 112 141 166 175 184 196 206 213 224 23a 24f 26f 27f 28f 29f 301 312 322 343 354 362 371 394 46b 48f 49f 50c 51f 531 687 70f 71f 72f 73f 74f 75f 76f 77f 78f 79f 80f 81f 82f 83f 844 85c 2065 2071 208b 209a 210e 211c 212f 213f 214f 215f 216f 217f 2189 219f 2201 2228 2238 2243 2255 2743 2768 2777 2785 2798 2843 2867 2877 2884 2896 2902 2911 2923 2935 2972 10121
       //1068 2815 61523 0d 1f 2f 3f 5f 531 2549 256f 257f 258f 259f 260d 261f 2631 3801 3812 10121 
       //1353 3100 61523 0f 1f 2f 3f 59 142 173 20d 22f 23f 24f 25f 26f 27f 28f 29f 30f 31f 32f 33f 342 357 489 50f 51f 52f 53f 54c 55f 562 577 602 623 635 64b 651 66f 67f 68f 69f 70f 71f 72f 73f 74f 75f 76b 77f 793 848 867 87f 104d 106f 107f 108e 109f 1208 1212 122f 123f 1249 125e 1274 3851 5751 8521 9161 10181 
       RadarSpoke trs = new RadarSpoke("1353 3100 61523 0f 1f 2f 3f 59 142 173 20d 22f 23f 24f 25f 26f 27f 28f 29f 30f 31f 32f 33f 342 357 489 50f 51f 52f 53f 54c 55f 562 577 602 623 635 64b 651 66f 67f 68f 69f 70f 71f 72f 73f 74f 75f 76b 77f 793 848 867 87f 104d 106f 107f 108e 109f 1208 1212 122f 123f 1249 125e 1274 3851 5751 8521 9161 10181");
       System.out.println(trs.toString());
       ArrayList <ArrayList>targetParts = trs.getTargetParts();
       for(ArrayList targetPart: targetParts){
           for(Object o: targetPart){
               RadarCell rc = (RadarCell)o;
               System.out.print(rc.toString() + " ");
           }
           System.out.println();
       }
       
    }//main
    
}//RadarSpoke class