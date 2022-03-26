/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nmci.smartcoast;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import javax.swing.JTable;

/**
 *
 * @author Thomas O Callaghan
 */
public class MouseHandler implements MouseListener{
    PPIPanel ppiPanel;
    RadarTargetTable targetTable;
    JFrame radarWindow;
    JTable table;

    
    MouseHandler(PPIPanel ppiPanel, RadarTargetTable targetTable, JFrame radarWindow, JTable table){
        this.ppiPanel = ppiPanel;
        this.targetTable = targetTable;
        this.radarWindow = radarWindow;
        this.table = table;
        
    }
    
    
    @Override
    public void mouseClicked(MouseEvent e) {
       int x = e.getX();
       int y = e.getY();
       if(targetTable == null || radarWindow == null)
           return;
       ppiPanel.outline(targetTable, x, y, table);
       radarWindow.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
}
