/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import models.Message;
import models.Person;
import views.MainRoom.MainRoomController;
import views.MultiPlayer.MultiPlayerController;

/**
 *
 * @author Mostafa
 */
public class ClientGui extends Application {
    private double xOffset = 0;
    private double yOffset = 0;
    
    public static Person loggedPlayer;
    public static Thread playerSocketThread = null;
    public static Socket playerSocket;
    public static ObjectOutputStream objectOutputStream ;
    public static ObjectInputStream objectInputStream;
    public static MainRoomController mrc;
    public static MultiPlayerController mpc;
    @Override
    public void start(Stage primaryStage) throws IOException
    {
         Parent root = FXMLLoader.load(getClass().getResource("/views/StartPage/SignIn.fxml"));
       
        //grab your root here
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        //move around here
        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });
        primaryStage.setTitle("Home");
        primaryStage.setMaxWidth(1200);
        primaryStage.setMinHeight(650);
       

        Scene scene = new Scene(root);
        //set transparent
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest((event) -> {
            System.exit(1);
        });       
        
    }

    public static void createPlayerSocketThread(){
        if(playerSocketThread!=null){
            playerSocketThread.stop();
        } 
        playerSocketThread = new Thread( new Runnable(){
            @Override
            public void run() {
                while(true){
                    try {
                        Message msg = (Message)ClientGui.objectInputStream.readObject();
                        if(msg!=null)
                        {
                        if(mrc!=null)
                        {
                            mrc.processMessage(msg);
                        }
                        if(mpc!=null)
                        {
                            mpc.processMessage(msg);
                        }
                        }
                    } catch (IOException ex) {
                        System.out.println("IO");
                    } catch (ClassNotFoundException ex) {
                        System.out.println("");;
                    }
                } 
            }
        });
        playerSocketThread.start();
    }
    public static void main(String[] args) {
        launch(args);
    }
    
}
