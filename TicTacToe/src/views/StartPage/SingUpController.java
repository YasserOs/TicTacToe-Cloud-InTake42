/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.StartPage;
import controllers.*;
import views.*;
import models.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import controllers.Database;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import views.MainRoom.MainRoomController;

/**
 * FXML Controller class
 *
 * @author Hossam
 */
public class SingUpController  {
    Database db;
    Person p;
    @FXML
    private Button loginbtn;
    @FXML
    private Button singupbtn;
    @FXML
    private TextField txtusername;
    @FXML
    private TextField txtemail;
    @FXML
    private TextField txtpassword;
    @FXML
     private TextField txtpassword1;
    @FXML
    private Label txtalert ;

   
    @FXML
    private void SignUPhandle(ActionEvent event) throws SQLException, IOException {
        //check for a vaild mail
        db = Server.getDatabase();
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(txtemail.getText());
        String userName = txtusername.getText().trim();
        String email    = txtemail.getText().trim();
        String password = txtpassword.getText().trim();
        String cpassword = txtpassword1.getText().trim();
        //System.out.print((userName+" "+ " "+ email+" "+password+" "+cpassword));

        if (userName.isEmpty() || email.isEmpty()
                || password.isEmpty()) {
            Platform.runLater(() -> {
                txtalert.setText("Empty Fields is Required");
            });

        } else if (!matcher.matches()) {
            Platform.runLater(() -> {
                txtalert.setText("Please enter a valid mail");
            });

        } else if(!txtpassword.getText().equals(txtpassword1.getText())){
                Platform.runLater(()->{
                  txtalert.setText("Please check your password");
                }); 
        } else {
                if(db.checkRegister(userName, email)){
                    txtalert.setText("This player already exists");
                } else {
                    db.signUp(userName, password, email) ;
                    Server.db.updatePlayerStatus(userName, "online");
                    p = db.getPlayer(userName);
                    Server.updateAllPlayersVector(p);
                    Server.updateOnlinePlayersVector(p);
                    finshSignUp(event);
            }
         }
      // signed user
      

}
    public void finshSignUp(ActionEvent event) throws IOException{
         
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("views/MainRoom/MainRoom.fxml"));
        Parent View = loader.load();
        ClientGui.startClient(p);
        Scene ViewScene = new Scene(View);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(ViewScene);
        window.show();
    }
    
//      public void logPlayer(Person p)
//      {
//        System.out.println("Printing from main room controller");
//        loggedPlayer=p;
//        System.out.println(p.getUsername()+"\n"+p.getEmail());
//    }
    
    
 public void SwitchtoSignN(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("views/StartPage/SignIn.fxml"));
        Parent signNView = loader.load();
        Scene signNViewScene = new Scene(signNView);        
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();        
        window.setScene(signNViewScene);
        window.show();
    }
}