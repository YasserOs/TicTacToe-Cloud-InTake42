/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.MultiPlayer;

import controllers.ClientGui;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import views.SinglePlayer.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Message;
import models.Person;
import models.Session;

/**
 *
 * @author Hossam
 */
public class MultiPlayerController implements Initializable {
    @FXML AnchorPane ap;
    @FXML Button backbtn;
    @FXML Button restartbtn;
    @FXML Button btn1;
    @FXML Button btn2;
    @FXML Button btn3;
    @FXML Button btn4;
    @FXML Button btn5;
    @FXML Button btn6;
    @FXML Button btn7;
    @FXML Button btn8;
    @FXML Button btn9;
    @FXML Button sendbtn;
    @FXML TextArea chattxt;
    @FXML TextField chatmsg;
    @FXML Label labelLeft; //player1
    @FXML Label labelRight; // player2
    
    ArrayList<Button> availablePositions = new ArrayList<Button>();
    Person player1; // loggedPlayer
    String player2;
    String currentPlayerPick;
    String opponentPick ;
    Session currentSession;

    Thread playerSocketThread;
    boolean playerTurn; 
    boolean invited =false;
    int numberOfPlays = 0 ;
    
    public void initSession(String p2 , boolean isInvited ) throws IOException, ClassNotFoundException{
        player1=ClientGui.loggedPlayer;
        player2=p2;
        invited = isInvited;
        if(!invited){
            //random boolean to decide which player to start , but only the player who sent the invite is gonna run it
            playerTurn = randomStart();
            Message msg = new Message("BeginMatch",player1.getUsername(),player2,Boolean.toString(!playerTurn));
            ClientGui.objectOutputStream.writeObject(msg);
            if(playerTurn){
                //show pick dialog , and set the other option for opponent
                msg = new Message("Pick",player1.getUsername(),player2,currentPlayerPick);
                ClientGui.objectOutputStream.writeObject(msg);
            }
        }
        
        currentSession = new Session(player1.getUsername(),p2);
    }

    
    public boolean randomStart()
    {
        Random rand = new Random();
        return rand.nextBoolean();
    }
    
    public void processMessage(Message msg) throws IOException{
       String Action =msg.getAction(); 
       switch(Action){
           case "BeginMatch":
               playerTurn = Boolean.parseBoolean(msg.getContent());
               break;
           case "Pick":
               setPick(msg.getContent());
               break;
           case "Move":
               updateBoard(msg.getPosition());

       }
    }
    
    public void setPick(String pick){
        opponentPick = pick;
        if(pick.equals("X")){
            currentPlayerPick = "O";
        }else{
            currentPlayerPick = "X";
        }
    }
    public void updateBoard(int position)
    {
        currentSession.play(position, opponentPick);        
        playerTurn=true;
    }
    private boolean isEmpty(Button pos)
    {
        return pos.getText().isEmpty();  
    }
    @FXML
    private void PlayerMove(ActionEvent event) throws IOException, ClassNotFoundException 
    {
        Button position = (Button) event.getSource(); 
        if( isEmpty(position) && playerTurn){

                  position.setText(currentPlayerPick);
                  int buttPosition = currentSession.board.getBoard().indexOf(position);
                  availablePositions.remove(position);
                  Message msg = new Message("Move",player1.getUsername(),player2,buttPosition);
                  ClientGui.objectOutputStream.writeObject(msg);
                  numberOfPlays++;
                  if(numberOfPlays>=5)
                  {
                      if(currentSession.board.checkWin(currentPlayerPick))
                      { 
                          System.out.println(player1+" Won !");
                           availablePositions.clear();

                      }
                  }
                  playerTurn=false;
              }
    }
    
    
    public void resetGrid()
    {
        availablePositions.add(btn1);
        availablePositions.add(btn2);
        availablePositions.add(btn3);
        availablePositions.add(btn4);
        availablePositions.add(btn5);
        availablePositions.add(btn6);
        availablePositions.add(btn7);
        availablePositions.add(btn8);
        availablePositions.add(btn9);
        for(Button b: availablePositions)
        {
            b.setText("");
        
        }
        currentSession.board.getBoard().clear();
        currentSession.board.getBoard().addAll(availablePositions);

      }
    public void back2MainRoom(ActionEvent event) throws IOException{
     Parent View = FXMLLoader.load(getClass().getClassLoader().getResource("views/MainRoom/MainRoom.fxml"));
        Scene ViewScene = new Scene(View);
        
       
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(ViewScene);
        window.show();
    
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ClientGui.mpc = this;
        resetGrid();
    }    
    
}
