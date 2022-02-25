/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author abdur
 */
public class savedGames {
    private String Opponent;
    private Integer gameID;

    public savedGames(String Opponent, Integer gameID) {
        this.Opponent = Opponent;
        this.gameID = gameID;
    }

    public String getOpponent() {
        return Opponent;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setOpponent(String Opponent) {
        this.Opponent = Opponent;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    
    
}
