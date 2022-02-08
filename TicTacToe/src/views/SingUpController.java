/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Hossam
 */
public class SingUpController implements Initializable {

    @FXML
    private Button loginbtn;
    @FXML
    private Button singupbtn;
    @FXML
    private TextField username;
    @FXML
    private TextField email;
    @FXML
    private TextField password;
    
    @FXML
    private void SignUPandle(ActionEvent event) {
        System.out.println("You clikced on signiup");
        // sing up functionality should be implemented here
    }
    
    public void SwitchtoSignN(ActionEvent event) throws IOException
    {
        Parent signNView = FXMLLoader.load(getClass().getResource("SignIn.fxml"));
        Scene signNViewScene = new Scene(signNView);
        
       
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(signNViewScene);
        window.show();
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}