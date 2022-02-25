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
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import models.Person;
import org.json.JSONException;
import org.json.JSONObject;
import views.GeneralController;
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
    public static GeneralController currentLiveCtrl;
    public static Stage mainStage;
    public static Alert alert;
    @Override
    public void start(Stage primaryStage) throws IOException
    {
        mainStage=primaryStage;
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
        Image icon  = new Image("controllers/icon.png");
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Home");
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
            playerSocket=new Socket("127.0.0.1",12345); 
            printStream = new PrintStream(playerSocket.getOutputStream());
            inputStream = new DataInputStream(playerSocket.getInputStream());
            createPlayerSocketThread();

        }catch (IOException ex){
              System.out.println("Server is not running .");
        }  


    }
 
    public static void createPlayerSocketThread(){
        playerSocketThread = new Thread( new Runnable(){
            @Override
            public void run() {
                while(true){
                    try {
                        String response = inputStream.readLine();
                        if(response==null){
                            System.out.println("Server Close");
                            closeClient();
                            break;
                        }
                        JSONObject msg = new JSONObject(response);
                        currentLiveCtrl.processMessage(msg);                        
                    } catch (IOException ex) {
                        System.out.println("server closed");
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(ClientGui.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (JSONException ex) {
                        Logger.getLogger(ClientGui.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } 
            }
        });
        playerSocketThread.start();
    }

    public static void convertJSONtoPlayer(JSONObject json) throws JSONException{
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
      p.setTotal_score(score);
      p.setGames_played(games);
      p.setGames_won(wins);
      p.setGames_lost(losses);
      p.setDraws(draws);
      loggedPlayer = p ;
    }
    public static void startClient()
    {
        createSocket();
    }

    public static void closeClient(){
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                showAlert("Server Closed", "Closing Client ...");
                mainStage.close();
            }
        });
        
        playerSocketThread.stop();
    }
     private static void showAlert(String title , String header ){
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setResizable(false);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        startClient();
        launch(args);
    }

}