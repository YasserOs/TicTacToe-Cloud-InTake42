/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;


public class Session {
    Player p1 ,p2 ;
    String p1Pick,p2Pick,winner,mode;
    boolean status; // 0 for paused session 1 for running
    Board board;
}
