/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nmci.smartcoast;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benjaminjakobus
 */
/**
 * A container for storing positions (read from file) that form
 * the polygon. Positions take the form of latitude/longitude
 * coordinates, that are later converted to pixel coordinates.
 *
 * @author Cormac Gebruers
 * @author Benjamin Jakobus
 * @version 1.0
 * @since 1.0
 */
public class PositionContainer {
    /* The positions that define the polygon */
    private List<Position> positions;

    /**
     * Constructor
     * @since 1.0
     */
    public PositionContainer() {
        positions = new ArrayList<Position>(500);
    }

    /**
     * Adds a position to the container
     * @param p the {@code Position{ to add to the container
     * @since 1.0
     */
    public void add(Position p) {
        positions.add(p);
    }

    /**
     * Retrieves all positions stored in the container
     * @return {@code List} object containing all stored positions
     * @since 1.0
     */
    public List<Position> getPositions() {
        return positions;
    }

    /**
     * The number of elements contained inside the container.
     *
     * @return size     The number of elements contained inside the container.
     * @since 1.0
     */
    public int size() {
        return positions.size();
    }
}
