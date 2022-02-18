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
import java.net.Socket;
import java.net.URL;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import views.MultiPlayer.MultiPlayerController;
import views.SinglePlayer.SinglePlayerController;

/**
 *
 * @author Hossam
 */
public class MainRoomController implements Initializable {
    
    String chosenOpponent;
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
    private ActionEvent e;
    @FXML Label labelName; // labelName.setText(person.getName());
    @FXML Label labelScore; // labelScore.setText(person.getScore());
    @FXML Label labelWins; //labelScore.setText(person.getWins());     // mfrood el 3 dool yt7to fel init;
    @FXML Button refresh;
    @FXML private TableView<DisplayPlayers> tableView;
    @FXML private TableColumn<DisplayPlayers, String> name;
    @FXML private TableColumn<DisplayPlayers, String> status; 
    @FXML private Button info;
    @FXML private Label pN;
    @FXML private Label pS;
    @FXML private Label pW;
    
    @FXML private void showinfo(ActionEvent event){
        pN.setVisible(true);
        pS.setVisible(true);
        pW.setVisible(true);
        labelName.setVisible(true);
        labelScore.setVisible(true);
        labelWins.setVisible(true);
        labelName.setText(ClientGui.loggedPlayer.getUsername());
        labelWins.setText(String.valueOf(ClientGui.loggedPlayer.getGames_won()));
        labelScore.setText(String.valueOf(ClientGui.loggedPlayer.getTotal_score()));
    }
    // all functions implementation is just for test, feel free to put ur back end implementation   
    @FXML
    private void SendingMSG(ActionEvent event) {    // assigned to button send (bottom right on GUI)
      
        globalchat.appendText(msgContent.getText()+"\n"); 
        msgContent.clear();
    }
    
     @FXML
    private void ShowPlayers(ActionEvent event) { // assigned to button Showlist of players (bottom center on GUI) 
         System.out.println(ClientGui.loggedPlayer.getUsername());
        fillList();
        if(plist.isVisible()){
            plist.setVisible(false); 
            multiBTN.setDisable(true);
        }else{
            plist.setVisible(true);
           multiBTN.setDisable(false);
        }   
    }
   @FXML
   private void refresh(ActionEvent event){
   
       fillList();
   }
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
        fillList();
        e = event;

        DisplayPlayers chosen = tableView.getSelectionModel().getSelectedItems().get(0);
        System.out.println(chosen);

        if(plist.isVisible()){
            if(chosen != null){
                if( chosen.getStatus().equals("online")){

                    ObservableList<DisplayPlayers> SelectedRow = tableView.getSelectionModel().getSelectedItems();
                    chosenOpponent = SelectedRow.get(0).getName();
                    System.out.println(chosenOpponent);
                    Message msg = new Message("Invite", ClientGui.loggedPlayer.getUsername(), chosenOpponent, "Pending");
                    ClientGui.objectOutputStream.writeObject(msg);

                }
            }
        }
    }
    public void startMultiPlayerMatch(ActionEvent event , String opponent , boolean isInvited) throws IOException, ClassNotFoundException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("views/MultiPlayer/MultiPlayer.fxml"));
        Parent View = loader.load();
        
        Scene ViewScene = new Scene(View);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(ViewScene);
        MultiPlayerController controller = loader.getController();
        controller.initSession(opponent,isInvited);
        window.show();
    }

    
    public void updatePlayersList(Vector <Person> players)
    {
        
    }
    
    public void createSocket()
    {
       try {
            Socket playerSocket = new Socket("127.0.0.1",9000);
            ClientGui.playerSocket=playerSocket;
            OutputStream outputStream = playerSocket.getOutputStream();
            ClientGui.objectOutputStream = new ObjectOutputStream(outputStream);
            InputStream inputStream = playerSocket.getInputStream();
            ClientGui.objectInputStream= new ObjectInputStream(inputStream);
            Message msg = new Message("LoggedIn",ClientGui.loggedPlayer.getUsername(),"","");
            ClientGui.objectOutputStream.writeObject(msg);
        }catch (IOException ex){
            Logger.getLogger(MainRoomController.class.getName()).log(Level.SEVERE, null, ex);
        } 
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
        Message response = new Message("Invite",ClientGui.loggedPlayer.getUsername(),msg.getSender(),decision);
        ClientGui.objectOutputStream.writeObject(response);
    }
    
    public void openInvitationRefusalScreen()
    {
        //open small log with with refuse statement
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){  
        name.setCellValueFactory(new PropertyValueFactory<DisplayPlayers, String>("name"));
        status.setCellValueFactory(new PropertyValueFactory<DisplayPlayers, String>("status")); 
        initSockets();
        ClientGui.mrc=this;
        ClientGui.createPlayerSocketThread();
    }
    
    public void fillList()
    {
        tableView.setItems(Server.db.displayPlayers( ClientGui.loggedPlayer.getUsername()));
    }

    public void initSockets(){
        createSocket();
        updatePlayersList(Server.getPlayers());
    }
    
}