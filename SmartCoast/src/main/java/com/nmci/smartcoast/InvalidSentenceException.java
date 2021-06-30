/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nmci.smartcoast;

/**
 *
 * @author Cormac Gebruers
 * @version 0.1
 * @since 0.1
 */
public class InvalidSentenceException extends Exception {

    /* the bad message causing the error */
    private String badMsg;

    /* the source of the bad message */
    private String rxID;

    public InvalidSentenceException(String badMessage, String rxID){
        badMsg = badMessage;
	this.rxID = rxID;
    }

    public String badMsg(){
        return badMsg;
    }

    public String rxID(){
	return rxID;
    }
}

