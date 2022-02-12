/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.io.Serializable;

/**
 *
 * @author YasserOsama
 */
public class Message implements Serializable{
    private  String action;
    private String sender;
    private String receiver;
    private  String content;
    public Message(){}
    public Message(String action , String sender , String receiver, String content) {
        this.action = action;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }
    public void setSender(String sender){
        this.sender = sender;
    }
    public String getSender(){
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getAction(){
        return action;
    }
    public void setAction(String action){
        this.action =action;
    }
}
