/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.LeaderBoards;


import Client.ClientGui;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Vector;
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
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.GeneralController;
import models.Person;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author Hossam
 */
public class LeaderBoardsController extends GeneralController implements Initializable {

    @FXML
    private Label p1;
    @FXML
    private Label p2;
    @FXML
    private Label p3;
    @FXML
    private Label p4;
    @FXML
    private Label p5;
     @FXML
    private Label p11;
    @FXML
    private Label p21;
    @FXML
    private Label p31;
    @FXML
    private Label p41;
    @FXML
    private Label p51;
    private Vector<Person> players ;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ClientGui.currentLiveCtrl=this;
        sendMsgToServer();
    }
    public void back2MainRoom(ActionEvent event) throws IOException, JSONException
    
    {
        
        JSONObject msg = new JSONObject();
        msg.put("Action", "playerFinishMatch");
        msg.put("Mode","Left");
        msg.put("status", "online");
        ClientGui.printStream.println(msg.toString());
        Parent View = FXMLLoader.load(getClass().getClassLoader().getResource("Client/MainRoom/MainRoom.fxml"));
        Scene ViewScene = new Scene(View);       
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(ViewScene);
        window.show();
    
    } 
    public void sendMsgToServer(){
        JSONObject msg = new JSONObject();
        msg.put("Action", "LeaderBoard");
        msg.put("Mode","Busy");
        ClientGui.printStream.println(msg.toString());
    }
     public void fillleaderboard(JSONObject msg) throws SQLException{
            JSONArray players = msg.getJSONArray("TopPlayers");
            Vector<String> topPlayersNames = new Vector<String>() ;
            Vector<Integer> topPlayersScores = new Vector<Integer>() ;

            for (int i = 0; i < players.length(); i++) {
                topPlayersNames.add(players.getJSONObject(i).getString("name"));
                topPlayersScores.add(players.getJSONObject(i).getInt("score"));
            }
            
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    p1.setText(topPlayersNames.get(0));
                    p2.setText(topPlayersNames.get(1));
                    p3.setText(topPlayersNames.get(2));
                    p4.setText(topPlayersNames.get(3));
                    p5.setText(topPlayersNames.get(4));
                    p11.setText(String.valueOf(topPlayersScores.get(0)));
                    p21.setText(String.valueOf(topPlayersScores.get(1)));
                    p31.setText(String.valueOf(topPlayersScores.get(2)));
                    p41.setText(String.valueOf(topPlayersScores.get(3)));
                    p51.setText(String.valueOf(topPlayersScores.get(4)));
                }
            });
            
     
       }

    @Override
    public void processMessage(JSONObject msg) {
        String Action = msg.getString("Action");
        switch(Action){
            case "LeaderBoard":
        {
            try {
                fillleaderboard(msg);
            } catch (SQLException ex) {
                Logger.getLogger(LeaderBoardsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
                break;
        }
    }
      
    
}
