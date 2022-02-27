/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.LeaderBoards;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Person;
import org.json.JSONException;

/**
 * FXML Controller class
 *
 * @author Hossam
 */
public class LeaderBoardsController implements Initializable {

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
        try {
            fillleaderboard();
        } catch (SQLException ex) {
            Logger.getLogger(LeaderBoardsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void back2MainRoom(ActionEvent event) throws IOException, JSONException
    
    {
        Parent View = FXMLLoader.load(getClass().getClassLoader().getResource("Client/MainRoom/MainRoom.fxml"));
        Scene ViewScene = new Scene(View);       
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(ViewScene);
        window.show();
    
    } 
     public void fillleaderboard() throws SQLException{
            //players = Server.db.Top5Players();
            p1.setText(players.get(0).getUsername());
            p2.setText(players.get(1).getUsername());
            p3.setText(players.get(2).getUsername());
            p4.setText(players.get(3).getUsername());
            p5.setText(players.get(4).getUsername());
            p11.setText(String.valueOf(players.get(0).getTotal_score()));
            p21.setText(String.valueOf(players.get(1).getTotal_score()));
            p31.setText(String.valueOf(players.get(2).getTotal_score()));
            p41.setText(String.valueOf(players.get(3).getTotal_score()));
            p51.setText(String.valueOf(players.get(4).getTotal_score()));
     
       }
      
    
}
