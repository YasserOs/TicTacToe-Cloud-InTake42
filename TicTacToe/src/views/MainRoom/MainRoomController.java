/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.MainRoom;

import models.*;
import controllers.*;
import java.io.*;
import java.util.logging.*;
import javafx.fxml.*;
import javafx.scene.*;
import java.util.*;
import java.net.URL;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import views.MultiPlayer.MultiPlayerController;
import views.SinglePlayer.SinglePlayerController;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import views.GeneralController;


/**
 *
 * @author Hossam
 */
public class MainRoomController extends GeneralController implements Initializable {
      boolean PendingInvitation=false;
    @FXML BorderPane bp;
    @FXML
    private TextArea playersList; 
    @FXML
    private TextArea globalchat;
    @FXML
    private TextField msgContent;
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
    @FXML Label labelName; // labelName.setText(person.getName());
    @FXML Label labelScore; // labelScore.setText(person.getScore());
    @FXML Label labelWins; //labelScore.setText(person.getWins());     // mfrood el 3 dool yt7to fel init;
    @FXML private TableView<DisplayPlayers> tableView;
    @FXML private TableColumn<DisplayPlayers, String> name;
    @FXML private TableColumn<DisplayPlayers, String> status; 
    @FXML private Button info;
    @FXML private Label pN;
    @FXML private Label pS;
    @FXML private Label pW;
    
    Alert alert;
    String chosenOpponent;
    ObservableList<DisplayPlayers> Playerslist=FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {     
        
          try {
              name.setCellValueFactory(new PropertyValueFactory<DisplayPlayers, String>("name"));
              status.setCellValueFactory(new PropertyValueFactory<DisplayPlayers, String>("status"));
              ClientGui.currentLiveCtrl=this;
              JSONObject msg = new JSONObject();
              msg.put("Action", "getallplayers");
              ClientGui.printStream.println(msg.toString());
              PendingInvitation=false;
              labelName.setText(ClientGui.loggedPlayer.getUsername());
              labelWins.setText(String.valueOf(ClientGui.loggedPlayer.getGames_won()));
              labelScore.setText(String.valueOf(ClientGui.loggedPlayer.getTotal_score()));
              fillList();
          } catch (JSONException ex) {
              Logger.getLogger(MainRoomController.class.getName()).log(Level.SEVERE, null, ex);
          }
    }
    public void fillList()
    {
       
        tableView.setItems(Playerslist);       
    }
    
    // all functions implementation is just for test, feel free to put ur back end implementation   

    public void PlayerStartedMatch() throws JSONException
    {
        JSONObject msg = new JSONObject();
        msg.put("Action", "playerStartedMatch");
        ClientGui.printStream.println(msg.toString());
    
    }
    public void PlayVsAI(ActionEvent event) throws IOException, JSONException{
      
        PlayerStartedMatch();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("views/SinglePlayer/SinglePlayer.fxml"));
        Parent View = loader.load();
        Scene ViewScene = new Scene(View);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(ViewScene);
        window.show();
    }
    public void PlayVsFriend(ActionEvent event) throws IOException, JSONException
    {
      
        DisplayPlayers chosen = tableView.getSelectionModel().getSelectedItems().get(0);

        if(plist.isVisible()){
            if(chosen != null){
                if( chosen.getStatus().equals("online")&& (PendingInvitation==false))
                {
                    ObservableList<DisplayPlayers> SelectedRow = tableView.getSelectionModel().getSelectedItems();
                    chosenOpponent = SelectedRow.get(0).getName();
                    System.out.println(chosenOpponent);
                    JSONObject msg = new JSONObject();
                    msg.put("Action","Invite");
                    msg.put("Sender",ClientGui.loggedPlayer.getUsername());
                    msg.put("Receiver", chosenOpponent);
                    msg.put("Content","Pending");           
                    ClientGui.printStream.println(msg.toString());
                    PendingInvitation=true;
                }
            }
        }
    }
    public void startMultiPlayerMatch( String opponent , boolean isInvited) throws IOException, JSONException{
               
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("views/MultiPlayer/MultiPlayer.fxml"));
        Parent View = loader.load();
        Scene ViewScene = new Scene(View);
        Stage window = (Stage)multiBTN.getParent().getScene().getWindow();
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                try {
                    window.setScene(ViewScene);
                    MultiPlayerController controller = loader.getController();
                    controller.initSession(opponent,isInvited);
                    PlayerStartedMatch();
                    window.show();   
                } catch (JSONException ex) {
                    Logger.getLogger(MainRoomController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    @Override
    public void processMessage(JSONObject msg) throws IOException, ClassNotFoundException{
          try {
              String Action =msg.getString("Action");
              switch(Action)
              {
                  case "BroadcastChat":
                      broadcastChat(msg);
                      break;
                  case "Invite":
                      processInvitation(msg);
                      break;
                  case "playersignup":
                      addplayer(msg);
                      break;
                  case "playersignin":
                      updateplayerlist(msg, "online");
                      break;
                  case "playersignout":
                      updateplayerlist(msg, "offline");
                      break;
                  case "playerStartedMatch":
                      updateplayerlist(msg,"in-game");
                      break;
                  case "Playerslist":
                      initPlayersTable(msg);
                      break;
                  case "playerFinishMatch":
                      updateplayerlist(msg,"online");
                      
              } } catch (JSONException ex) {
              Logger.getLogger(MainRoomController.class.getName()).log(Level.SEVERE, null, ex);
          }
    }
    
    public void broadcastChat(JSONObject msg)
    {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                try {   
                    globalchat.appendText("\n"+msg.getString("Sender")+": "+msg.getString("Content"));
                } catch (JSONException ex) {
                    Logger.getLogger(MainRoomController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    @FXML
    public void sendBroadcastMsg(ActionEvent event) throws JSONException
    {
        if(!msgContent.getText().isEmpty()){
            String msgcontent= new String(msgContent.getText());     
            System.out.println("text ==="+msgContent.getText());
            msgContent.setText("");
            JSONObject msg= new JSONObject();
            msg.put("Action","BroadcastChat");
            msg.put("Sender",ClientGui.loggedPlayer.getUsername());
            msg.put("Content",msgcontent);
            ClientGui.printStream.println(msg.toString()); 
        }
           
    }
   
    public void processInvitation(JSONObject msg) throws IOException, ClassNotFoundException, JSONException{
        String content = msg.getString("Content");
        switch(content){
            case "Accept":
                startMultiPlayerMatch(msg.getString("Sender") , false);
                break;
            case "Refuse":
                openInvitationRefusalScreen();
                break;
            case "Pending":
                checkifPlayerPendingInvitation(msg);
                break;
        }
    }
    
    public void checkifPlayerPendingInvitation(JSONObject serverMsg) throws JSONException
    {
       
        if(PendingInvitation == true)
        {
            sendInvitaionResponse(serverMsg, "Refuse");
        }
        else
        {
            try {
                openInvitationScreen(serverMsg);
            } catch (IOException ex) {
                Logger.getLogger(MainRoomController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MainRoomController.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        }
    }
    
    public void sendInvitaionResponse(JSONObject serverMsg,String decision) throws JSONException
    {
        JSONObject msg= new JSONObject();
        msg.put("Action","Invite");
        msg.put("Sender",ClientGui.loggedPlayer.getUsername());
        msg.put("Receiver",serverMsg.getString("Sender"));
        msg.put("Content",decision);
        ClientGui.printStream.println(msg.toString());

    }
    
    public void openInvitationScreen(JSONObject msg) throws IOException, ClassNotFoundException{       
        // open small log with sender name and 2 buttons to accept or refuse the invitation        
        Platform.runLater(new Runnable()
        {
            @Override
            public void run() {
                try {
                    String decision ;
                    PendingInvitation=true;
                    Optional<ButtonType> result = showAlert("Invitation Recieved!",msg.getString("Sender")+" Invited you to a game");
                    Button newb = new Button();
                    ButtonType button = result.orElse(ButtonType.CANCEL);
                    if (button == ButtonType.OK) {
                        decision ="Accept"; // msg object
                    } else {
                        decision ="Refuse";
                        PendingInvitation=false;
                    }
                    sendInvitaionResponse(msg, decision);
                    if(decision.equals("Accept")){
                        try {
                            startMultiPlayerMatch( msg.getString("Sender"), true);
                        } catch (IOException ex) {
                            Logger.getLogger(MainRoomController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (JSONException ex) {
                    Logger.getLogger(MainRoomController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
    }
    
    public void openInvitationRefusalScreen()
            
    {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                 showAlert("Invitation","invitation refused .");
                 PendingInvitation=false;
            }
        });
       
    }
    
    private Optional<ButtonType> showAlert(String title , String header ){
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setResizable(false);
        Optional<ButtonType> res = alert.showAndWait();
        return res;
    }
    public void addplayer(JSONObject msg) throws JSONException
    {
         Playerslist.add(new DisplayPlayers(msg.getString("username"),"online"));
    }
    
    public void updateplayerlist(JSONObject msg,String status) throws JSONException
    {
        for(int i=0 ; i <Playerslist.size();i++){
            if(Playerslist.get(i).getName().equals(msg.getString("username"))){
                Playerslist.set(i, new DisplayPlayers(msg.getString("username"),status));
                break;
            }
        }
    }
     public void initPlayersTable(JSONObject msg) throws JSONException
    {
        JSONArray names = msg.getJSONArray("names");
        JSONArray status= msg.getJSONArray("status");
        
        for(int i=0 ; i<names.length(); i++)
        {
            Playerslist.add(new DisplayPlayers(names.getString(i), status.getString(i)));
        
        }
        
    }
    
    
}