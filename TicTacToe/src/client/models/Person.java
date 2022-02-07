/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.models;

/**
 *
 * @author Mostafa
 */
public class Person extends Player{
    //Person attribute
    private String username;
    private String email;
    private int score;
    private boolean status;
    private String last_seen;
    private int total_score;
    private int games_played;
    private int games_won;
    private int games_lost;
    private int draws;
//Constructor
    public Person(String username, String email, int score, boolean status, String last_seen, int total_score, int games_played, int games_won, int games_lost, int draws) {
        this.username = username;
        this.email = email;
        this.score = score;
        this.status = status;
        this.last_seen = last_seen;
        this.total_score = total_score;
        this.games_played = games_played;
        this.games_won = games_won;
        this.games_lost = games_lost;
        this.draws = draws;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(String last_seen) {
        this.last_seen = last_seen;
    }

    public int getTotal_score() {
        return total_score;
    }

    public void setTotal_score(int total_score) {
        this.total_score = total_score;
    }

    public int getGames_played() {
        return games_played;
    }

    public void setGames_played(int games_played) {
        this.games_played = games_played;
    }

    public int getGames_won() {
        return games_won;
    }

    public void setGames_won(int games_won) {
        this.games_won = games_won;
    }

    public int getGames_lost() {
        return games_lost;
    }

    public void setGames_lost(int games_lost) {
        this.games_lost = games_lost;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }
    
    
    
    
}
