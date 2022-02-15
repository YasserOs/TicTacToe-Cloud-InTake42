/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.SinglePlayer;

import models.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 *
 * @author Hossam
 */
public class SinglePlayerController implements Initializable {
    @FXML AnchorPane ap;
    @FXML GridPane grid;
    
    ArrayList<Button> btns = new ArrayList<Button>();
    
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
    
    Board current_board= new Board();
    int counter=0;
    Session currentSession;
    Boolean check=true;
    String pick ; 
    Person loggedPlayer;
    @FXML
    
    private boolean isEmpty(Button pos)
    {
//        Button btn =(Button) grid.getChildren().get(pos);
  
        if(pos.getText().isEmpty())
        {
            return true;
        }
        else return false;
    
    }
    public void PlayerMove(ActionEvent event) 
    {
       Button btn = (Button) event.getSource(); 
  if(!btns.isEmpty()){
        if(isEmpty(btn))
        {
            btn.setText("X");
            btns.remove(btn);
            counter++;
            if(counter>=5)
            {
                if(current_board.checkWin("X"))
                { 
                    System.out.println("player one win");
                     btns.clear();
                 
                }
                
            
            }
            psMove();
           
        }
        else
            System.out.println("used");
  }
     }
    
    
    public void restart (ActionEvent event)
    {

        
        btns.add(btn1);
        btns.add(btn2);
        btns.add(btn3);
        btns.add(btn4);
        btns.add(btn5);
        btns.add(btn6);
        btns.add(btn7);
        btns.add(btn8);
        btns.add(btn9);
        
        
        for(Button b: btns)
        {
            b.setText("");
        
        }
        
        current_board.board.clear();
        current_board.board.addAll(btns);
        
        System.out.println(current_board.board.size());
    
    }
    public void psMove()
    {
        if(!btns.isEmpty())
        {
            int pos=Pc.randomMove(btns.size());
            btns.get(pos).setText("O");
            btns.remove(btns.get(pos));
            counter++;
            if(counter>=6)
               {
                   if(current_board.checkWin("O"))
                   { 
                       System.out.println("pc win");
                       btns.clear();

                   }


               }
        }
        
    }
    
    
    
    public void back2MainRoom(ActionEvent event) throws IOException
    
    {
        Parent View = FXMLLoader.load(getClass().getClassLoader().getResource("views/MainRoom/MainRoom.fxml"));
        Scene ViewScene = new Scene(View);       
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(ViewScene);
        window.show();
    
    }
    
      public void getsession(Session p)
      {
        System.out.println("Printing from single player");
        currentSession = p;
          System.out.println(currentSession.p1.getUsername());
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        btns.add(btn1);
        btns.add(btn2);
        btns.add(btn3);
        btns.add(btn4);
        btns.add(btn5);
        btns.add(btn6);
        btns.add(btn7);
        btns.add(btn8);
        btns.add(btn9);
        
        current_board.board.addAll(btns);
        
        System.out.println(current_board.board.size());
        
        
        
    }    
    
}
