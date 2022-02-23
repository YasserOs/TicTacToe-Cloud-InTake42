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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
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
import org.json.JSONObject;
import views.GeneralController;

/**
 *
 * @author Hossam
 */
public class MultiPlayerController extends GeneralController implements Initializable {
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
    String currentPlayerPick="";
    String opponentPick="" ;
    Session currentSession;

    Thread playerSocketThread;
    boolean playerTurn;
    boolean oppTurn;
    boolean invited =false;
    int numberOfPlays = 0 ;
    
    public void initSession(String p2 , boolean isInvited ){
        player1=ClientGui.loggedPlayer;
        System.out.println("Init Session - Player 1 : " + player1.getUsername() + " , Player 2 : "+p2);
        player2=p2;
        invited = isInvited;
        if(!invited){
            //random boolean to decide which player to start , but only the player who sent the invite is gonna run it
            playerTurn = randomTurn();
            oppTurn = !playerTurn;
            System.out.println("Turns - Player 1 : " + playerTurn + " , Player 2 : "+oppTurn);
            JSONObject msg = new JSONObject();
            msg.put("Action", "chooseTurn");
            msg.put("Sender", player1.getUsername());
            msg.put("Receiver", player2);
            msg.put("Content", oppTurn);
            ClientGui.printStream.println(msg.toString());
            try {
                setPicks();
            } catch (IOException ex) {
                Logger.getLogger(MultiPlayerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        currentSession = new Session(player1.getUsername(),p2);
        resetGrid();
    }

    
    public void processMessage(JSONObject msg) throws IOException{
       String Action =msg.getString("Action"); 
       switch(Action){
           case "chooseTurn":
               setTurn(msg.getBoolean("Content"));
               break;
           case "Pick":
               setMyPick(msg.getString("Content"));
               break;
           case "Move":
               updateBoard(msg.getInt("Content"));
               break;
           case "Won":
               gameresult("lost");
               break;
           case "draw":
               gameresult("draw");
              
              
               break;
       }
    }
    
    public void gameresult(String result)
    {
        
        playerTurn=false;
        ClientGui.loggedPlayer.gamesplayed();
        if(result.equals("lost"))
        {
             ClientGui.loggedPlayer.gameslost();
        }
        else
        {
            ClientGui.loggedPlayer.gamesdraws();
            ClientGui.loggedPlayer.incrementTotal_score(2);    
        }
        
       
    }
    
    public boolean randomTurn()
    {
        Random rand = new Random();
        return rand.nextBoolean();
    }
    public void setTurn(boolean turn) throws IOException{
        playerTurn = turn;
        setPicks();    
    }
    public void setPicks() throws IOException{
        if(playerTurn){
            setMyPick("x");
            setOpponentPick(opponentPick);
        }
    }
    public void setMyPick(String pick){
        currentPlayerPick=pick;   
        if(pick.equals("x")){
              opponentPick = "o";
        }else{
            opponentPick = "x";
        }
    }
    public void setOpponentPick(String pick) throws IOException{
        JSONObject msg = new JSONObject();
        msg.put("Action", "Pick");
        msg.put("Sender", player1.getUsername());
        msg.put("Receiver", player2);
        msg.put("Content", opponentPick);
        
        ClientGui.printStream.println(msg.toString());  
    }
     public void resetGrid()
    {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                availablePositions.clear();
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
        });
        ;        
    }
    
  
    
    @FXML
    private void PlayerMove(ActionEvent event) throws IOException, ClassNotFoundException, InterruptedException 
    {
        
        Button position = (Button) event.getSource();
        System.out.println("Player 1 : "+player1.getUsername()+" - Pick : "+currentPlayerPick+" - Position : "+availablePositions.indexOf(position));

        if( isEmpty(position) && playerTurn){
            System.out.println("Player 1 : "+player1.getUsername()+" - Pick : "+currentPlayerPick+" - Position : "+availablePositions.indexOf(position));
            position.setText(currentPlayerPick);
            int buttPosition = currentSession.board.getBoard().indexOf(position);
            availablePositions.remove(position);
            playerTurn=false;
            JSONObject msg = new JSONObject();
            msg.put("Action", "Move");
            msg.put("Sender", player1.getUsername());
            msg.put("Receiver", player2);
            msg.put("Content",buttPosition);
            ClientGui.printStream.println(msg.toString());
            numberOfPlays++;
            if(numberOfPlays>=5)
            {
                if(currentSession.board.checkWin(currentPlayerPick))
                { 
                    playerWin();
                }
                else if(numberOfPlays==9)
                {
                    playerdraw();
                
                }
            }
           

        }
    }
    public void playerWin() throws InterruptedException, IOException{
        System.out.println(player1.getUsername()+" Won !");
        JSONObject msg = new JSONObject();
        msg.put("Action", "Won");
        msg.put("Sender", player1.getUsername());
        msg.put("Receiver", player2);
        ClientGui.printStream.println(msg.toString());
        ClientGui.loggedPlayer.incrementTotal_score(10);
        ClientGui.loggedPlayer.gameswon();
        ClientGui.loggedPlayer.gamesplayed();
        
        //availablePositions.clear();
        //resetGrid();
    }
    
     public void playerdraw() throws InterruptedException, IOException{
        System.out.println(player1.getUsername()+" draw !");
        JSONObject msg = new JSONObject();
        msg.put("Action", "draw");
        msg.put("Sender", player1.getUsername());
        msg.put("Receiver", player2);
        ClientGui.printStream.println(msg.toString());
        ClientGui.loggedPlayer.gamesplayed();
        ClientGui.loggedPlayer.gamesdraws();
        ClientGui.loggedPlayer.incrementTotal_score(2);
        //availablePositions.clear();
        //resetGrid();
    }
    
    private boolean isEmpty(Button pos)
    {
        return pos.getText().isEmpty();  
    }
    public void updateBoard(int position)
    {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                        currentSession.play(position, opponentPick);        
            }
        });
        numberOfPlays++;
        playerTurn=true;
    }
   
    public void back2MainRoom(ActionEvent event) throws IOException{
        
        
        JSONObject msg = new JSONObject();
        msg.put("Action", "playerFinishMatch");
        ClientGui.printStream.println(msg.toString());
        
        Parent View = FXMLLoader.load(getClass().getClassLoader().getResource("views/MainRoom/MainRoom.fxml"));
        Scene ViewScene = new Scene(View);
        
       
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(ViewScene);
        window.show();
    
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ClientGui.currentLiveCtrl = this;
        
    }    
    
}
