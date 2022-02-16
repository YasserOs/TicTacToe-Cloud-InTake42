/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.MainRoom;
import models.*;
import controllers.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import views.MultiPlayer.MultiPlayerController;
import views.SinglePlayer.SinglePlayerController;

/**
 *
 * @author Hossam
 */
public class MainRoomController implements Initializable {
    
    Thread updatePlayersListThread;
    Thread playerSocketThread;
    Socket playerSocket;
    InputStream inputStream;
    OutputStream outputStream ;
    ObjectOutputStream objectOutputStream ;
    ObjectInputStream objectInputStream;
    
    Person loggedPlayer;
    String chosenOpponent;
    @FXML BorderPane bp;
    @FXML
    private TextArea playersList; 
    @FXML
    private TextArea globalchat;
    @FXML
    private TextField msg;
    // each button has has his own id in case u wanted to use a specific button
    @FXML
    private Button sendmsg;
    @FXML
    private Button signleBTN;
    @FXML
    private Button multiBTN;
    @FXML
    private Button showBTN;
     @FXML
    private BorderPane plist;
    private ActionEvent e;
    @FXML Label labelName; // labelName.setText(person.getName());
    @FXML Label labelScore; // labelScore.setText(person.getScore());
    @FXML Label labelWins; //labelScore.setText(person.getWins());     // mfrood el 3 dool yt7to fel init;
    @FXML Button refresh;
    
    // all functions implementation is just for test, feel free to put ur back end implementation   
    @FXML
    private void SendingMSG(ActionEvent event) {    // assigned to button send (bottom right on GUI)
      
        globalchat.appendText(msg.getText()+"\n"); 
        msg.clear();
    }
    
     @FXML
    private void ShowPlayers(ActionEvent event) { // assigned to button Showlist of players (bottom center on GUI)  
        playersList.appendText(loggedPlayer.getUsername() + "\n");
        plist.setVisible(true);    
    }
   @FXML
   private void refresh(ActionEvent event){}
    public void PlayVsAI(ActionEvent event) throws IOException{
      
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("views/SinglePlayer/SinglePlayer.fxml"));
        Parent View = loader.load();
        Scene ViewScene = new Scene(View);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(ViewScene);
        SinglePlayerController controller = loader.getController();
        window.show();
    
    }
    public void PlayVsFriend(ActionEvent event) throws IOException{
        e = event;
        Message msg = new Message ("Invite",loggedPlayer.getUsername(),chosenOpponent,"Pending");
        objectOutputStream.writeObject(msg);  
    }
    public void startMultiPlayerMatch(ActionEvent event , String opponent , boolean isInvited) throws IOException, ClassNotFoundException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("views/MultiPlayer/MultiPlayer.fxml"));
        Parent View = loader.load();
        
        Scene ViewScene = new Scene(View);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(ViewScene);
        MultiPlayerController controller = loader.getController();
        controller.initSession(objectOutputStream,objectInputStream,loggedPlayer,opponent,isInvited);
        window.show();
    }
    public void logPlayer(Person p){
        loggedPlayer=p;
    }
    
    public void updatePlayersList(Vector <Person> players)
    {
        
    }
    public void createSocket(){
       try {
            playerSocket = new Socket("127.0.0.1",9000);
            outputStream = playerSocket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
            inputStream = playerSocket.getInputStream();
            objectInputStream= new ObjectInputStream(inputStream);
            Message msg = new Message("LoggedIn",loggedPlayer.getUsername(),"","");
            objectOutputStream.writeObject(msg);
        }catch (IOException ex){
            Logger.getLogger(MainRoomController.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    public void createPlayerSocketThread(){
        playerSocketThread = new Thread( new Runnable(){
            @Override
            public void run() {
                while(true){
                    try {
                        Message msg = (Message)objectInputStream.readObject();
                        processMessage(msg);
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
    public void createPlayerListUpdateThread(){
        updatePlayersListThread= new Thread( new Runnable(){
            @Override
            public void run() {
                while(true){
                    // run function every 1 minute
                    updatePlayersList(Server.getPlayers());

                } 
            }
        });
    }
    public void processMessage(Message msg) throws IOException, ClassNotFoundException{
        String Action =msg.getAction(); 
        switch(Action){
            case "Chat":
                globalchat.appendText(msg.getContent());
                break;
            case "Invite":
                processInvitation(msg);

        }
    }
    public void processInvitation(Message msg) throws IOException, ClassNotFoundException{
        String content = msg.getContent();
        switch(content){
            case "Accept":
                startMultiPlayerMatch(e , msg.getSender() , false);
                break;
            case "Refuse":
                openInvitationRefusalScreen();
            case "Pending":
                openInvitationScreen(msg);
        }
    }
    public void openInvitationScreen(Message msg) throws IOException{
        String decision ="" ;
        // open small log with sender name and 2 buttons to accept or refuse the invitation
        // then send a messag object with the content= accept or refuse back to the server handler
        Message response = new Message("Invite",loggedPlayer.getUsername(),msg.getSender(),decision);
        objectOutputStream.writeObject(response);
    }
    public void openInvitationRefusalScreen(){
        //open small log with with refuse statement
    }
    @Override
    public void initialize(URL url, ResourceBundle rb){  
        
    }
    public void initSockets(){
        createSocket();
        createPlayerSocketThread();
        updatePlayersList(Server.getPlayers());
    }
    
}
