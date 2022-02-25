/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.SavedGames;

import controllers.ClientGui;
import controllers.Server;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.DisplayPlayers;
import models.savedGames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import views.GeneralController;

/**
 * FXML Controller class
 *
 * @author Hossam
 */
public class SavedGamesController extends GeneralController implements Initializable {
    @FXML private TableView<savedGames> tableView;
    @FXML private TableColumn<savedGames, Integer> GameID;
    @FXML private TableColumn<savedGames, String> Player1Name;
    @FXML private TableColumn<savedGames, String> Player2Name;// change Display players to what ever class u will use to get games from db
    
    
    
    
     public void back2MainRoom(ActionEvent event) throws IOException, JSONException
    
    {
        Parent View = FXMLLoader.load(getClass().getClassLoader().getResource("views/MainRoom/MainRoom.fxml"));
        Scene ViewScene = new Scene(View);       
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(ViewScene);
        window.show();
    
    } 
     
     public void Resume(ActionEvent event){
         //Assigned to Resume Button
         
     }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        GameID.setCellValueFactory(new PropertyValueFactory<savedGames, Integer>("gameID"));
        Player1Name.setCellValueFactory(new PropertyValueFactory<savedGames, String>("playerOneName"));             
        Player2Name.setCellValueFactory(new PropertyValueFactory<savedGames, String>("playerTwoName"));
        ClientGui.currentLiveCtrl = this;
        
        
    }    

    @Override
    public void processMessage(JSONObject msg) throws IOException, ClassNotFoundException {
    
    }
    
}
