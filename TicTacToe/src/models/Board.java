/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

public class Board {
    String[] board = new String[9];
    boolean move(int position , String pick){
        if(board[position]==""){
            board[position]=pick;
        }
    }
    
    boolean checkWin(String pick){
        return ( checkRows(pick) || checkColumns(pick) || checkDiagonal(pick));        
    }
    
    boolean checkRows(String pick){
        if(board[0].equals(pick) && board[1].equals(pick) && board[2].equals(pick)){
            return true;
        }
        if(board[3].equals(pick) && board[4].equals(pick) && board[5].equals(pick)){
            return true;
        }
        
        if(board[6].equals(pick) && board[7].equals(pick) && board[8].equals(pick)){
            return true;
        }
        else{
            return false;
        }
    }
    boolean checkColumns(String pick){
        if(board[0].equals(pick) && board[3].equals(pick) && board[6].equals(pick)){
            return true;
        }
        if(board[1].equals(pick) && board[4].equals(pick) && board[7].equals(pick)){
            return true;
        }
        
        if(board[2].equals(pick) && board[5].equals(pick) && board[8].equals(pick)){
            return true;
        }
        else{
            return false;
        }
    }
    boolean checkDiagonal(String pick){
        if(board[0].equals(pick) && board[4].equals(pick) && board[8].equals(pick)){
            return true;
        }
        if(board[2].equals(pick) && board[4].equals(pick) && board[6].equals(pick)){
            return true;
        }
        else{
            return false;
        }
    }
}
