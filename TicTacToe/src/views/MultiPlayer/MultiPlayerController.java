/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.MultiPlayer;

import controllers.ClientGui;
import static controllers.ClientGui.SelectedAvatar;
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
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import models.Message;
import models.Person;
import models.Session;
import org.json.JSONArray;
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
    @FXML Button pausebtn = new Button();
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
    boolean player1Restart = false;
    boolean player2Restart = false;
    boolean oppTurn;
    boolean invited =false;
    boolean isPaused= false;
    int numberOfPlays = 0 ;
    Image iconX ;
    Image iconO;
    @FXML
    ImageView leftPlayer;
    @FXML
    ImageView rightPlayer;
    @FXML
    Label leftPlayerName;
    @FXML
    Label rightPlayerName;
    @FXML
    TextArea SHOWTRUN;
    @FXML ImageView Micon,Sicon;
    @FXML Label boardLabel;
    MediaPlayer videoForWinner;
    MediaPlayer videoForLoser;
    MediaPlayer VideoForDraw;
   
    
    public void initSession(JSONObject msg , boolean isInvited ) throws JSONException{
        
        player1=ClientGui.loggedPlayer;
        player2= msg.getString("Sender");
        invited = isInvited;
        turnRandomizer();
        System.out.println("Turns - Player 1 : " + playerTurn + " , Player 2 : "+oppTurn);
        currentSession = new Session(player1.getUsername(),player2);
        resetGrid();        
        leftPlayerName.setText(player1.getUsername());
        rightPlayerName.setText(player2);
        Micon.setImage(SelectedAvatar);
        Sicon.setImage(ClientGui.Avatars.get(msg.getInt("Avatar")));
    }
    public void turnRandomizer() throws JSONException{
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
        }
        
    }
    public void resumeSession(JSONObject msg , boolean isInvited) throws JSONException{
        System.out.println(msg);
        JSONArray board = msg.getJSONObject("gameDetails").getJSONArray("board");
        player1 = ClientGui.loggedPlayer;
        JSONObject gameDetails = msg.getJSONObject("gameDetails");
        if( gameDetails.getString("playerOne").equals(player1.getUsername())) {
           player2 =  gameDetails.getString("playerTwo");
           currentPlayerPick =  gameDetails.getString("playerOnePick");       
        }else{
           player2 =  gameDetails.getString("playerOne");
           currentPlayerPick =  gameDetails.getString("playerTwoPick");
        }
        leftPlayerName.setText(player1.getUsername());
        rightPlayerName.setText(player2);
        setMyPick(currentPlayerPick);
        if (gameDetails.getString("turn").equals(player1.getUsername())) {
            oppTurn = false;
            playerTurn = true;
            changeBoardLabel("Your Turn");
            invited =true;
        }else{
            oppTurn = true;
            playerTurn = false;
            changeBoardLabel(player2 +"'s Turn");
            invited =false;
        }
        Micon.setImage(SelectedAvatar);
        Sicon.setImage(ClientGui.Avatars.get(msg.getInt("Avatar")));
        currentSession = new Session(player1.getUsername(), player2);
        resetGrid();
        System.out.println(board);
        System.out.println(board.getString(2));
        for (int i = 0; i < board.length(); i++) {
            if (!board.getString(i).isEmpty()) {
                 updateBoard(i, board.getString(i));
            }
        }
        
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
                    updateBoard(msg.getInt("Content"), opponentPick);
                    playerTurn=true;
                    changeBoardLabel("Your Turn");
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
                case "Pause":
                    checkPauseResponse(msg);
                    break;
                case "Surrender":
                    opponentSurrendered(msg);
                    break;
            }} catch (JSONException ex) {
            Logger.getLogger(MultiPlayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void  opponentSurrendered(JSONObject msg) throws JSONException{
        ClientGui.loggedPlayer.gameswon();
        ClientGui.loggedPlayer.incrementTotal_score(10);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    showAlert("You won",msg.getString("Sender") + " left the game", 0);
                    pausebtn.setDisable(true);
                    changeBoardLabel(player2 + " surrendered !");
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        });
        playerTurn = false;
        isPaused = true;
    }
    
    @FXML 
    private void pauseGameRequest(ActionEvent event) throws JSONException{
            
            JSONObject msg = new JSONObject();
            msg.put("Action", "Pause");
            msg.put("Receiver", player2);
            msg.put("Sender",  ClientGui.loggedPlayer.getUsername());
            msg.put("Content", "Pending");
            ClientGui.printStream.println(msg.toString());
    }
    private void checkPauseResponse(JSONObject msg) {
        
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                try {
                    if (msg.getString("Content").equals("true")) {
                        changeBoardLabel("The Game is saved");
                        showAlert("Pause", msg.getString("Sender") +  " has accepted to save the game", 1);
                        pausebtn.setDisable(true);
                        System.out.println(pausebtn.isDisabled());
                        isPaused = true;
                        playerTurn=false;
                    } else if(msg.getString("Content").equals("Pending")) {
                        Optional<ButtonType> res = showAlert("Pause game", msg.getString("Sender") + " wants to pause the game?", 1);
                        ButtonType button = res.orElse(ButtonType.CANCEL);
                        if (button == ButtonType.OK) {
                            sendMsgToPlayer("Pause", "true");
                            pausebtn.setDisable(true);
                            System.out.println(pausebtn.isDisabled());
                            changeBoardLabel("The Game is saved");
                            isPaused = true;
                            saveSession();
                        } else if (button == ButtonType.CANCEL) {
                            sendMsgToPlayer("Pause", "false");
                        }
                    }else{
                        showAlert("Pause", msg.getString("Sender") +  " has refused saving game", 1);
                    }
                } catch (JSONException ex) {
                    Logger.getLogger(MultiPlayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }
    private void saveSession() throws JSONException{
        JSONObject msg = new JSONObject();
        msg.put("Action", "SaveSession");
        msg.put("playerOne",player1.getUsername() );
        msg.put("playerTwo", player2);
        msg.put("playerOnePick", currentPlayerPick);
        msg.put("playerTwoPick", opponentPick);
        JSONArray board = new JSONArray();
        for (Button button : currentSession.board.board) {
            board.put(button.getText());
        }
        msg.put("board", board);
        if (playerTurn) {
            msg.put("turn", player1.getUsername());
        }else{
             msg.put("turn", player2);
        }
        ClientGui.printStream.println(msg.toString());
        playerTurn=false;
    }
    private void checkGameRestart(JSONObject msg) throws IOException, JSONException {
        
        String response = msg.getString("Content");
        if(response.equals("true")){
                    player2Restart = true;
                    if(player1Restart&&player2Restart){
                        player1Restart = player2Restart= false;
                        turnRandomizer();
                        resetGrid();
                    }
        }
        else if(response.equals("false")){
            
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    try {
                        alert.hide();
                        showAlert("Restart Match", msg.getString("Sender")+" Returned to main room .", 0);
                    } catch (JSONException ex) {
                        Logger.getLogger(MultiPlayerController.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
            changeBoardLabel("Your Turn");
        }else{
            changeBoardLabel(player2 + "'s Turn");
        }
    }
    
    public void changeBoardLabel(String update){
        Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    boardLabel.setText(update);
                }
            });
    }
    public void setMyPick(String pick){
        currentPlayerPick=pick;   
        if(pick.equals("x")){
              opponentPick = "o";
           
          leftPlayer.setImage(iconX);
          rightPlayer.setImage(iconO);
         
        
        }else{
                opponentPick = "x";
             leftPlayer.setImage(iconO);
             rightPlayer.setImage(iconX);
        }
    }
    public void setOpponentPick(String pick) throws IOException, JSONException{
        sendMsgToPlayer("Pick",opponentPick);  
    }
     public void resetGrid()
    {
        numberOfPlays=0;
        isPaused = false;
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                pausebtn.setDisable(false);
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
            position.setText(currentPlayerPick );
            //currentPlayerPick.equals("x");
            changeBoardLabel(player2 + "'s Turn");
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
        sendMsgToPlayer("Won","");
        gameresult("Won");
        
    }
     public void playerdraw() throws IOException, JSONException{
        System.out.println(player1.getUsername()+" draw !");
        sendMsgToPlayer("Draw","");
        gameresult("Draw");
    }
     public void playerSurrender() throws JSONException, IOException {
        sendMsgToPlayer("Surrender", "");
        ClientGui.loggedPlayer.gameslost(); 
     }
     public void gameresult(String result) throws IOException
    {
        isPaused = true;
        playerTurn=false;
        ClientGui.loggedPlayer.gamesplayed();
        if(result.equals("Won"))
        {
            changeBoardLabel("You won");
            ClientGui.loggedPlayer.gameswon();
            ClientGui.loggedPlayer.incrementTotal_score(10); 
            displayVideo(videoForWinner,result);

        }else if(result.equals("Loss")){
            changeBoardLabel("You Lost");
            System.out.println(ClientGui.loggedPlayer + " Lost !");
            ClientGui.loggedPlayer.gameslost();
            displayVideo(videoForLoser,result);
        }
        else
        {
            changeBoardLabel("Draw");
            ClientGui.loggedPlayer.gamesdraws();
            ClientGui.loggedPlayer.incrementTotal_score(2);   
            displayVideo(VideoForDraw,result);
        }
        // then send a messag object with the content= accept or refuse back to the server handler
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                 pausebtn.setDisable(true);
                Optional<ButtonType> res = showAlert(result, "Want to Play again ?", 1);
                ButtonType button = res.orElse(ButtonType.CANCEL);
                if (button == ButtonType.OK) {
                    try {
                        player1Restart = true;
                        sendMsgToPlayer("RestartMatch","true");
                        if(player2Restart){
                            turnRandomizer();
                            resetGrid();
                            
                        }
                        else
                        {
                            showAlert("Restart Match", "Waiting Player 2 Response...", 0);
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
    private Optional<ButtonType> showAlert(String title , String body , int flag){
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(body);
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
    public void updateBoard(int position, String pick)
    {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                currentSession.play(position, pick);        
            }
        });
        
        numberOfPlays+=1;        
    }
    @FXML
    public void back2MainRoom(ActionEvent event) throws IOException, JSONException{
        if(!isPaused){
            Optional<ButtonType>  res = showAlert("Leaving the game", "If you leave the game, you would lose", 1);
            ButtonType button = res.orElse(ButtonType.CANCEL);
                if (button == ButtonType.OK) {
                   playerSurrender();
                   savePlayerBeforeLeavingTheMatch(event);
                } 
        }else{
            savePlayerBeforeLeavingTheMatch(event);
        }
        
    
    }   
    public void savePlayerBeforeLeavingTheMatch(ActionEvent event) throws JSONException, IOException{
        JSONObject msg = new JSONObject();
        msg.put("Action", "playerFinishMatch");
        msg.put("Mode", "Multiplayer");
        msg.put("score", ClientGui.loggedPlayer.getTotal_score());
        msg.put("Wins",  ClientGui.loggedPlayer.getGames_won());
        msg.put("Draws", ClientGui.loggedPlayer.getDraws());
        msg.put("Loses", ClientGui.loggedPlayer.getGames_lost());
        msg.put("Games", ClientGui.loggedPlayer.getGames_played());
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
         iconX = new Image("views/MultiPlayer/x.png");
         iconO = new Image("views/MultiPlayer/o.png");
         videoForWinner = new MediaPlayer(new Media(getClass().getResource("1.mp4").toExternalForm()));
         videoForLoser = new MediaPlayer(new Media(getClass().getResource("2.mp4").toExternalForm()));
         VideoForDraw =new MediaPlayer(new Media(getClass().getResource("3.mp4").toExternalForm()));
         
    }    

    

        public void displayVideo(MediaPlayer video, String result)
    {
            Platform.runLater(new Runnable(){
             @Override
                 public void run() {
                StackPane secondaryLayout2 = new StackPane();
                MediaView mediaView2 = new MediaView(video);
                secondaryLayout2.getChildren().addAll(mediaView2);
                Scene secondScene2 = new Scene(secondaryLayout2, 700, 700);
                Stage secondStage2 = new Stage();
                secondStage2.setResizable(false);
                secondStage2.setScene(secondScene2);
                secondStage2.setX(0);
                secondStage2.setY(50);
                secondStage2.show();
                video.play();
                PauseTransition delay = new PauseTransition(Duration.seconds(7));
                delay.setOnFinished( event -> secondStage2.close());
                secondStage2.setOnHidden(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        later(result);
                    }
                });
                delay.play();
                  }
        });
    }
    
    public void later(String result){
    
    
    
                 pausebtn.setDisable(true);
                Optional<ButtonType> res = showAlert(result, "Want to Play again ?", 1);
                ButtonType button = res.orElse(ButtonType.CANCEL);
                if (button == ButtonType.OK) {
                    try {
                        player1Restart = true;
                        sendMsgToPlayer("RestartMatch","true");
                        if(player2Restart){
                            turnRandomizer();
                            resetGrid();
                            
                        }
                        else
                        {
                            showAlert("Restart Match", "Waiting Player 2 Response...", 0);
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
}
