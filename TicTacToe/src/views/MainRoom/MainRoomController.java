/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.MainRoom;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 *
 * @author Hossam
 */
public class MainRoomController implements Initializable {
    
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
    
    // all functions implementation is just for test, feel free to put ur back end implementation 
   
    @FXML
    private void SendingMSG(ActionEvent event) {    // assigned to button send (bottom right on GUI)
      
        globalchat.appendText(msg.getText()+"\n"); 
        msg.clear();
    }
    
     @FXML
    private void ShowPlayers(ActionEvent event) { // assigned to button Showlist of players (bottom center on GUI)
        
        playersList.appendText("Players TEST" + "\n");
        
    }
    
    @FXML
    private void PlaySingle(ActionEvent event) { // assigned to button PLAY VS COMPUTER (top center on GUI)
        
        System.out.print("Single test");
        
        // match room scene switch code will be implemented here 
        
    }
    @FXML
    private void PlayMulti(ActionEvent event) {// assigned to button Play with a friend (mid center on GUI)
        
        System.out.print(" Multi test");
        
        // after sending invite and the other player agrees match room scene switch code will be implemented here
        
    }
   
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
