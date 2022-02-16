/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.ArrayList;
import javafx.scene.control.Button;

public class Board 
{
    public ArrayList<Button> board = new ArrayList<Button>();
    
    /*boolean move(int position , String pick){
        
        if(board[position]=="")
        {
            board[position]=pick;
            return true;
        }
        
        return false;
    }*/
   
  public boolean checkWin(String pick){
        return ( checkRows(pick) || checkColumns(pick) || checkDiagonal(pick));        
    }
    
    boolean checkRows(String pick){
        if(board.get(0).getText().equals(pick) && board.get(1).getText().equals(pick) && board.get(2).getText().equals(pick)){
            return true;
        }
         if(board.get(3).getText().equals(pick) && board.get(4).getText().equals(pick) && board.get(5).getText().equals(pick)){
            return true;
        }
        
         if(board.get(6).getText().equals(pick) && board.get(7).getText().equals(pick) && board.get(8).getText().equals(pick)){
            return true;
        }
        else{
            return false;
        }
    }
    boolean checkColumns(String pick){
        if(board.get(0).getText().equals(pick) && board.get(3).getText().equals(pick) && board.get(6).getText().equals(pick))
        {
            return true;
        }
         if(board.get(1).getText().equals(pick) && board.get(4).getText().equals(pick) && board.get(7).getText().equals(pick)){
            return true;
        }
        
      if(board.get(2).getText().equals(pick) && board.get(5).getText().equals(pick) && board.get(8).getText().equals(pick)){
            return true;
        }
        else{
            return false;
        }
    }
    boolean checkDiagonal(String pick){
      if(board.get(0).getText().equals(pick) && board.get(4).getText().equals(pick) && board.get(8).getText().equals(pick)){
            return true;
        }
      if(board.get(2).getText().equals(pick) && board.get(4).getText().equals(pick) && board.get(6).getText().equals(pick)){
            return true;
        }
        else{
            return false;
        }
    }
}
