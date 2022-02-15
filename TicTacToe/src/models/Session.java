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
    
    
    public Player p2 ;
    public Person p1;
    String p1Pick,p2Pick;
    int turn ;
    boolean status; // 0 for paused session 1 for running
    public Board board;
    
    
    public Session(Person p1){
        this.p1=p1;
        p2 = new Pc();
    }
    public Session(Person p1 , Player p2){
        this.p1=p1;
        this.p2=p2;
    }
    public boolean play(int position , String pick){
        while(true){
            if(turn == 1)
            {
                //listen from player 1 stream
                //board.move(poistion , pick)
            }else if(turn==2){
                //listen from player 2 stream
                //board.move(position , pick)
            }
        }
    }
    
}
