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
import java.util.Optional;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Message;
import models.Person;
import models.Session;
import org.json.JSONException;
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
    ActionEvent e;
    ArrayList<Button> availablePositions = new ArrayList<Button>();
    Person player1; // loggedPlayer
    String player2;
    String currentPlayerPick="";
    String opponentPick="" ;
    Session currentSession;
    Alert alert;
    Thread playerSocketThread;
    boolean playerTurn;
    boolean playerWon =false;
    boolean player1Restart = false;
    boolean player2Restart = false;
    boolean oppTurn;
    boolean invited =false;
    int numberOfPlays = 0 ;
    
    public void initSession(String p2 , boolean isInvited ) throws JSONException{
        player1=ClientGui.loggedPlayer;
        System.out.println("Init Session - Player 1 : " + player1.getUsername() + " , Player 2 : "+p2);
        player2=p2;
        invited = isInvited;
        if(!invited){
            playerTurn = randomTurn();
            oppTurn = !playerTurn;     
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
            System.out.println("Turns - Player 1 : " + playerTurn + " , Player 2 : "+oppTurn);
            
        }
        currentSession = new Session(player1.getUsername(),p2);
        resetGrid();
    }

    
    public void processMessage(JSONObject msg) throws IOException{
        try {
            String Action =msg.getString("Action");
            switch(Action){
                case "Chat" :
                    receiveChat(msg);
                    break;
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
                    gameresult("Loss");
                    break;
                case "Draw":
                    gameresult("Draw");
                    break;
                case "RestartMatch":
                    checkGameRestart(msg);
                    break;
            }} catch (JSONException ex) {
            Logger.getLogger(MultiPlayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private void checkGameRestart(JSONObject msg) throws IOException, JSONException {
        
        String response = msg.getString("Content");
        if(response.equals("true")){
                    player2Restart = true;
                    if(player1Restart&&player2Restart){
                        player1Restart = player2Restart= false;
                        resetGrid();
                    }
        }
        else if(response.equals("false")){
            
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    alert.hide();
                    showAlert("Restart Match", msg.getString("Sender")+" Returned to main room .", 0);
                }
            });
            
            
        }
    }
    public boolean randomTurn()
    {
        Random rand = new Random();
        return rand.nextBoolean();
    }
    public void setTurn(boolean turn) throws IOException, JSONException{
        playerTurn = turn;
        setPicks();    
    }
    public void setPicks() throws IOException, JSONException{
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
    public void setOpponentPick(String pick) throws IOException, JSONException{
        sendMsgToPlayer("Pick",opponentPick);  
    }
     public void resetGrid()
    {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                numberOfPlays=0;
                if(playerWon){
                    try {
                        playerTurn = randomTurn();
                        oppTurn = !playerTurn;  
                        JSONObject msg = new JSONObject();
                        msg.put("Action", "chooseTurn");
                        msg.put("Sender", player1.getUsername());
                        msg.put("Receiver", player2);
                        msg.put("Content", oppTurn);
                        ClientGui.printStream.println(msg.toString());
                        setPicks();
                    } catch (IOException ex) {
                        Logger.getLogger(MultiPlayerController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (JSONException ex) {
                        Logger.getLogger(MultiPlayerController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
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
    private void receiveChat(JSONObject msg) throws JSONException{
        chattxt.appendText(msg.getString("Sender")+": "+msg.getString("Content")+"\n");
    }  
    @FXML 
    private void SendChat(ActionEvent event) throws JSONException{
        chattxt.appendText(player1.getUsername()+": "+chatmsg.getText()+"\n");
        sendMsgToPlayer("Chat",chatmsg.getText());
        chatmsg.clear();

    }  
    @FXML
    private void PlayerMove(ActionEvent event) throws IOException, ClassNotFoundException, InterruptedException, JSONException 
    {   
        e=event;
        Button position = (Button) event.getSource();
        if( isEmpty(position) && playerTurn){
            System.out.println("Player 1 : "+player1.getUsername()+" - Pick : "+currentPlayerPick+" - Position : "+availablePositions.indexOf(position));
            position.setText(currentPlayerPick);
            int buttPosition = currentSession.board.getBoard().indexOf(position);
            availablePositions.remove(position);
            playerTurn=false;
            sendMsgToPlayer("Move",Integer.toString(buttPosition));
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
    public void playerWin() throws IOException, JSONException{
        System.out.println(player1.getUsername()+" Won !");
        playerWon=true;
        sendMsgToPlayer("Won","");
        gameresult("Won");
        
    }
     public void playerdraw() throws IOException, JSONException{
        System.out.println(player1.getUsername()+" draw !");
        sendMsgToPlayer("Draw","");
        gameresult("Draw");
    }
     public void gameresult(String result) throws IOException
    {
        playerTurn=false;
        ClientGui.loggedPlayer.gamesplayed();
        if(result.equals("Won"))
        {
            ClientGui.loggedPlayer.gameswon();
            ClientGui.loggedPlayer.incrementTotal_score(10);        

        }else if(result.equals("Loss")){
            playerWon=false;
            System.out.println(ClientGui.loggedPlayer + " Lost !");
            ClientGui.loggedPlayer.gameslost();
        }
        else
        {
            ClientGui.loggedPlayer.gamesdraws();
            ClientGui.loggedPlayer.incrementTotal_score(2);    
        }
        // then send a messag object with the content= accept or refuse back to the server handler
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                Optional<ButtonType> res = showAlert(result, "Want to Play again ?", 1);
                Button newb = new Button();
                ButtonType button = res.orElse(ButtonType.CANCEL);
                if (button == ButtonType.OK) {
                    try {
                        player1Restart = true;
                        sendMsgToPlayer("RestartMatch","true");
                        if(player2Restart){
                            resetGrid();
                            
                        }else{
                            showAlert("Restart Match", "Waitin Player 2 Response...", 0);
                        }
                    } catch (JSONException ex) {
                        Logger.getLogger(MultiPlayerController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else if(button==ButtonType.CANCEL) {
                    try {
                        sendMsgToPlayer("RestartMatch","false");
                        try {
                        back2MainRoom(e);
                        } catch (IOException ex) {
                            Logger.getLogger(MultiPlayerController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } catch (JSONException ex) {
                        Logger.getLogger(MultiPlayerController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
        
       
    }
    private Optional<ButtonType> showAlert(String title , String header , int flag){
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setResizable(false);
        
        if(flag == 1){
            Optional<ButtonType> res = alert.showAndWait();
             return res;
        }else{
            alert.show();
            return null;
        }
    }
    private void sendMsgToPlayer(String Action , String Content) throws JSONException{
        JSONObject msg = new JSONObject();
        msg.put("Action", Action);
        msg.put("Sender", player1.getUsername());
        msg.put("Receiver", player2);
        if(Action == "Move"){
            int movePos = Integer.parseInt(Content);
            msg.put("Content", movePos);

        }else{
            msg.put("Content", Content);
        }
        ClientGui.printStream.println(msg.toString());
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
    @FXML
    public void back2MainRoom(ActionEvent event) throws IOException, JSONException{
        
        
        JSONObject msg = new JSONObject();
        msg.put ("Action", "playerFinishMatch");
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
