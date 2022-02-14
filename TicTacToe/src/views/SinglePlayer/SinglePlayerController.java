/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.SinglePlayer;

import models.*;
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author Hossam
 */
public class SinglePlayerController implements Initializable {
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
    Session currentSession;
    String pick ; 
    @FXML
    private void PlayerMove(ActionEvent event) {
        currentSession
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
        // TODO
    }    
    
}
