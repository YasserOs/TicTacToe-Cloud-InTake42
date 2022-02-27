/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;



/**
 *
 * @author Mostafa
 */
public class Pc 
{
    
    
    static public int randomMove (int max)
    {
        
    int random_int = (int)Math.floor(Math.random()*(max-1));     
 
    return random_int;
            
    }
    


}