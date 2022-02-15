/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.Date;
import java.util.Vector;



/**
 *
 * @author Mostafa
 */
public class Person extends Player{
    //Person attribute
    private String username;
    private String email;
    private String password;
    private int id ;
    private int score;
    private String status;
    private Date last_seen;
    private int total_score;
    private int games_played;
    private int games_won;
    private int games_lost;
    private int draws;
    static Vector<Session> pause_session = new Vector<Session>();
    
//Constructor
    public Person(){
        this.username = "";
        this.email = "";
        this.password="";
        this.score = 0;
        this.status = "offline";
        this.last_seen = new Date(0000, 00, 00);
        this.total_score = 0;
        this.games_played = 0;
        this.games_won = 0;
        this.games_lost = 0;
        this.draws = 0;
    };
    public Person(String username, String email , int id, String status, Date last_seen, int total_score, int games_played, int games_won, int games_lost, int draws) {
        this.username = username;
        this.email = email;
        this.id=id;
        this.status = status;
        this.last_seen = last_seen;
        this.total_score = total_score;
        this.games_played = games_played;
        this.games_won = games_won;
        this.games_lost = games_lost;
        this.draws = draws;
    }
     
    public Person(String username, String email){
        this.username = username;
        this.email = email;
    }

    public Person(String userName) {
        this.username = username;
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
    
    public void setPassword(String password){
        this.password = password;
    }
    public String getPassword (){
        return this.password;
    }
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isOnline() {
        return status == "online" ? true : false ;
    }
    public String getStatus(){
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
   
    public Date getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(Date last_seen) {
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