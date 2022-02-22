/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import models.Person;
import org.json.JSONObject;
import views.MainRoom.MainRoomController;
import views.MultiPlayer.MultiPlayerController;
import views.StartPage.SignInController;
import views.StartPage.SignUpController;

/**
 *
 * @author Mostafa
 */
public class ClientGui extends Application {
    private double xOffset = 0;
    private double yOffset = 0;
    
    public static Person loggedPlayer;
    public static Thread playerSocketThread;
    public static Socket playerSocket ;
    public static DataInputStream inputStream;
    public static PrintStream printStream;
    public static MainRoomController mrc;
    public static MultiPlayerController mpc;
    public static SignUpController signUpctrl;
    public static SignInController signInctrl;
    
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
    public static void createSocket()
    {
          try { 
            playerSocket=new Socket("127.0.0.1",9000);
            printStream = new PrintStream(playerSocket.getOutputStream());
            inputStream = new DataInputStream(playerSocket.getInputStream());
            JSONObject msg = new JSONObject();
            //msg.
            /*Message msg = new Message("LoggedIn",ClientGui.loggedPlayer.getUsername(),"","");
            ClientGui.objectOutputStream.writeObject(msg);*/
        }catch (IOException ex){
            Logger.getLogger(MainRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }  
       
       
    }

    public static void createPlayerSocketThread(){
        playerSocketThread = new Thread( new Runnable(){
            @Override
            public void run() {
                while(true){
                    try {
                        JSONObject msg = new JSONObject(inputStream.readLine());
                        if(mrc!=null)
                        {
                            try {
                                mrc.processMessage(msg);
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(ClientGui.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        else if(mpc!=null)
                        {
                            mpc.processMessage(msg);
                        }
                        else if(signUpctrl!=null){
                            signUpctrl.processMessage(msg);
                        }
                        else if(signInctrl!=null){
                            signInctrl.processMessage(msg);
                        }
                        
                    } catch (IOException ex) {
                        Logger.getLogger(ClientGui.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } 
            }
        });
        playerSocketThread.start();
    }
    
    public static void convertJSONtoPlayer(JSONObject json){
      String userName = json.getString("username");
      int score = json.getInt("score");
      String status = json.getString("status");
      int wins = json.getInt("wins");
      int games = json.getInt("games");
      int draws = json.getInt("draws");
      int losses = json.getInt("losses");
      Person p = new Person();
      p.setUsername(userName);
      p.setStatus(status);
      p.setScore(score);
      p.setGames_played(games);
      p.setGames_won(wins);
      p.setGames_lost(losses);
      p.setDraws(draws);
      loggedPlayer = p ;
    }
    public static void startClient()
    {
        createSocket();
        createPlayerSocketThread();
    }
    
    
    public static void main(String[] args) {
        startClient();
        launch(args);
    }
    
}
