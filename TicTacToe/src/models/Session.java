/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class Session {
    InputStream dis1 ;
    OutputStream os1;
    ObjectInputStream p1InputStream;
    ObjectOutputStream p1OutPutStream;
    
    InputStream dis2 ;
    OutputStream os2;
    ObjectInputStream p2InputStream;
    ObjectOutputStream p2OutPutStream;
    
    
    Player p1 ,p2 ;
    String p1Pick,p2Pick,winner,mode;
    int turn ;
    boolean status; // 0 for paused session 1 for running
    Board board;
    
    
}
