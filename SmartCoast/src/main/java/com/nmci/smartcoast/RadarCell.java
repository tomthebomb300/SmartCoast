/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nmci.smartcoast;

import java.io.Serializable;

/**
 *
 * @author cormac
 */
class RadarCell implements Serializable{

    int spokeIdx;
    int cellIdx;
    int echo;
    
    //IS STORING THE SPOKE SEQUENCE NUMBER HERE, OF ANY USE..?

    RadarCell(int spoke, int cell, int echoStrength) {
        spokeIdx = spoke;
        cellIdx = cell;
        echo = echoStrength;
    }//constructor

    public int getSpokeIdx() {
        return spokeIdx;
    }
    
    public int getCellIdx() {
        return cellIdx;
    }

    public int getCellEcho() {
        return echo;
    }
    
    public void setCellEcho(int echo){
        this.echo = echo;
    }
    
    @Override
    public String toString(){
        return String.valueOf(getCellIdx()); //"s:" + getSpokeIdx() + " c:" + getCellIdx() + " e:" + getCellEcho();
    }

}//RadarCell class