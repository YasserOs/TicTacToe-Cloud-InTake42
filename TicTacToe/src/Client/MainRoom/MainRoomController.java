/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.MainRoom;

import Client.ClientGui;
import models.*;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import Client.MultiPlayer.MultiPlayerController;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import models.GeneralController;


/**
 *
 * @author Hossam
 */
public class MainRoomController extends GeneralController implements Initializable {

    boolean PendingInvitation = false;
    @FXML
    BorderPane bp;
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
    @FXML
    Label labelName;
    @FXML
    Label labelScore;
    @FXML
    Label labelWins;
    @FXML
    Label labelGames;
    @FXML
    Label labelLosses;
    @FXML
    Label labelDraws;
    @FXML
    private TableView<DisplayPlayers> tableView;
    @FXML
    private TableColumn<DisplayPlayers, String> name;
    @FXML
    private TableColumn<DisplayPlayers, String> status;
    @FXML
    private Button Games;
    @FXML
    private Label pN;
    @FXML
    private Label pS;
    @FXML
    private Label pW;
    @FXML
    private TableView<savedGames> tableView2;
    @FXML
    private TableColumn<savedGames, Integer> GameID;
    @FXML
    private TableColumn<savedGames, String> Opponent;
    @FXML
    private Button resume;
    @FXML
    BorderPane gamesArea;
    Alert alert;
    String chosenOpponent;
    @FXML
    ImageView MIcon;

    ObservableList<DisplayPlayers> Playerslist = FXCollections.observableArrayList();
    ObservableList<savedGames> savedGamesList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            GameID.setCellValueFactory(new PropertyValueFactory<savedGames, Integer>("gameID"));
            Opponent.setCellValueFactory(new PropertyValueFactory<savedGames, String>("Opponent"));

            name.setCellValueFactory(new PropertyValueFactory<DisplayPlayers, String>("name"));
            status.setCellValueFactory(new PropertyValueFactory<DisplayPlayers, String>("status"));
            ClientGui.currentLiveCtrl = this;
            JSONObject msg = new JSONObject();
            msg.put("Action", "getallplayers");
            ClientGui.printStream.println(msg.toString());
            msg = new JSONObject();
            msg.put("Action", "getSavedGames");
            ClientGui.printStream.println(msg.toString());
            PendingInvitation = false;
            labelName.setText(ClientGui.loggedPlayer.getUsername());
            labelWins.setText(String.valueOf(ClientGui.loggedPlayer.getGames_won()));
            labelScore.setText(String.valueOf(ClientGui.loggedPlayer.getTotal_score()));
            labelGames.setText(String.valueOf(ClientGui.loggedPlayer.getGames_played()));
            labelLosses.setText(String.valueOf(ClientGui.loggedPlayer.getGames_lost()));
            labelDraws.setText(String.valueOf(ClientGui.loggedPlayer.getDraws()));
            fillList();
        } catch (JSONException ex) {
            Logger.getLogger(MainRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }

        MIcon.setImage(ClientGui.SelectedAvatar);

    }

    public void fillList() {
        tableView.setItems(Playerslist);
        tableView2.setItems(savedGamesList);
    }

    // all functions implementation is just for test, feel free to put ur back end implementation   
    public void requestResume(ActionEvent event) throws JSONException {

        ObservableList<savedGames> SelectedRow = tableView2.getSelectionModel().getSelectedItems();
        int gameIDToResume = SelectedRow.get(0).getGameID();
        chosenOpponent = SelectedRow.get(0).getOpponent();
        System.out.println(gameIDToResume + "\t" + chosenOpponent);
        for (DisplayPlayers player : Playerslist) {
            if (player.getName().equals(chosenOpponent)) {
                if (player.getStatus().equals("online")) {
                    sendResumeRequest(chosenOpponent, gameIDToResume);
                }
            }
        }
    }

    public void sendResumeRequest(String opponent, int gameID) throws JSONException {
        JSONObject msg = new JSONObject();
        msg.put("Action", "Invite");
        msg.put("Sender", ClientGui.loggedPlayer.getUsername());
        msg.put("Receiver", opponent);
        msg.put("Content", "Pending");
        msg.put("Avatar", ClientGui.AvatarIndex);
        msg.put("gameID", gameID);
        msg.put("gameState", "Paused");
        ClientGui.printStream.println(msg.toString());
    }

    public void PlayerStartedMatch(String mode) throws JSONException {
        JSONObject msg = new JSONObject();
        msg.put("Action", "playerStartedMatch");
        msg.put("Mode", mode);
        ClientGui.printStream.println(msg.toString());

    }

    public void PlayVsAI(ActionEvent event) throws IOException, JSONException {

        PlayerStartedMatch("Singleplayer");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("Client/SinglePlayer/SinglePlayer.fxml"));
        Parent View = loader.load();
        Scene ViewScene = new Scene(View);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(ViewScene);
        window.show();
    }

    public void PlayVsFriend(ActionEvent event) throws IOException, JSONException {

        DisplayPlayers chosen = tableView.getSelectionModel().getSelectedItems().get(0);

        if (plist.isVisible()) {
            if (chosen != null) {
                if (chosen.getStatus().equals("online") && (PendingInvitation == false)) {
                    ObservableList<DisplayPlayers> SelectedRow = tableView.getSelectionModel().getSelectedItems();
                    chosenOpponent = SelectedRow.get(0).getName();
                    System.out.println(chosenOpponent);
                    JSONObject msg = new JSONObject();
                    msg.put("Action", "Invite");
                    msg.put("Sender", ClientGui.loggedPlayer.getUsername());
                    msg.put("Receiver", chosenOpponent);
                    msg.put("Avatar", ClientGui.AvatarIndex);
                    msg.put("gameState", "New");
                    msg.put("Content", "Pending");
                    ClientGui.printStream.println(msg.toString());
                    PendingInvitation = true;
                }
            }
        }
    }

    public void startMultiPlayerMatch(JSONObject msg, boolean isInvited) throws IOException, JSONException {
        System.out.println(msg);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("Client/MultiPlayer/MultiPlayer.fxml"));
        Parent View = loader.load();
        Scene ViewScene = new Scene(View);
        Stage window = (Stage) multiBTN.getParent().getScene().getWindow();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    window.setScene(ViewScene);
                    MultiPlayerController controller = loader.getController();
                    if (msg.getString("gameState").equals("Paused")) {

                        controller.resumeSession(msg, isInvited);
                    } else {
                        controller.initSession(msg, isInvited);
                    }
                    PlayerStartedMatch("Multiplayer");
                    window.show();
                } catch (JSONException ex) {
                    Logger.getLogger(MainRoomController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    @Override
    public void processMessage(JSONObject msg) throws IOException, ClassNotFoundException {
        try {
            String Action = msg.getString("Action");
            switch (Action) {
                case "BroadcastChat":
                    broadcastChat(msg);
                    break;
                case "Invite":
                    processInvitation(msg);
                    break;
                case "playersignup":
                    addplayer(msg);
                    broadcastChat(msg);
                    break;
                case "playersignin":
                    updateplayerlist(msg);
                    broadcastChat(msg);
                    break;
                case "playersignout":
                    updateplayerlist(msg);
                    broadcastChat(msg);
                    break;
                case "playerStartedMatch":
                    updateplayerlist(msg);
                    break;
                case "Playerslist":
                    initPlayersTable(msg);
                    break;
                case "SaveGamesList":
                    initGamessTable(msg);
                    break;
                case "playerFinishMatch":
                    updateplayerlist(msg);
                    break;
                case "playerBusy":
                    updateplayerlist(msg);
                    break;
                case "ResumeMatch":
                    startMultiPlayerMatch(msg, true);
                    break;
            }
        } catch (JSONException ex) {
            Logger.getLogger(MainRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void broadcastChat(JSONObject msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    globalchat.appendText("\n" + msg.getString("Sender") + ": " + msg.getString("Content"));
                } catch (JSONException ex) {
                    Logger.getLogger(MainRoomController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    @FXML
    public void sendBroadcastMsg(ActionEvent event) throws JSONException {
        if (!msgContent.getText().isEmpty()) {
            String msgcontent = new String(msgContent.getText());
            System.out.println("text ===" + msgContent.getText());
            msgContent.setText("");
            JSONObject msg = new JSONObject();
            msg.put("Action", "BroadcastChat");
            msg.put("Sender", ClientGui.loggedPlayer.getUsername());
            msg.put("Content", msgcontent);
            ClientGui.printStream.println(msg.toString());
        }

    }

    public void processInvitation(JSONObject msg) throws IOException, ClassNotFoundException, JSONException {
        String content = msg.getString("Content");
        switch (content) {
            case "Accept":
                startMultiPlayerMatch(msg, false);
                break;
            case "Refuse":
                openInvitationRefusalScreen();
                break;
            case "Pending":
                checkifPlayerPendingInvitation(msg);
                break;
        }
    }

    public void checkifPlayerPendingInvitation(JSONObject serverMsg) throws JSONException {

        if (PendingInvitation == true) {
            sendInvitaionResponse(serverMsg, "Refuse");
        } else {
            try {
                openInvitationScreen(serverMsg);
            } catch (IOException ex) {
                Logger.getLogger(MainRoomController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MainRoomController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public void sendInvitaionResponse(JSONObject playerResponse, String decision) throws JSONException {

        JSONObject msg = new JSONObject();
        msg.put("Action", "Invite");
        msg.put("Sender", ClientGui.loggedPlayer.getUsername());
        msg.put("Receiver", playerResponse.getString("Sender"));
        msg.put("gameState", "New");
        msg.put("Avatar", ClientGui.AvatarIndex);
        msg.put("Content", decision);
        ClientGui.printStream.println(msg.toString());

    }

    public void openInvitationScreen(JSONObject msg) throws IOException, ClassNotFoundException {
        // open small log with sender name and 2 buttons to accept or refuse the invitation        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    String decision;
                    PendingInvitation = true;
                    String gameState = msg.getString("gameState");
                    Optional<ButtonType> result = null;

                    if (gameState.equals("New")) {
                        result = showAlert("Invitation Recieved!", msg.getString("Sender") + " Invited you to a game");

                    } else {
                        result = showAlert("Resume match", msg.getString("Sender") + " Invite you to resume a game.");
                    }

                    ButtonType button = result.orElse(ButtonType.CANCEL);
                    if (button == ButtonType.OK) {
                        decision = "Accept"; // msg object
                    } else {
                        decision = "Refuse";
                        PendingInvitation = false;
                    }

                    if (decision.equals("Accept") && gameState.equals("New")) {
                        try {
                            sendInvitaionResponse(msg, decision);
                            startMultiPlayerMatch(msg, true);
                        } catch (IOException ex) {
                            Logger.getLogger(MainRoomController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else if (decision.equals("Refuse")) {
                        sendInvitaionResponse(msg, decision);
                    } else {

                        sendResumeAcceptMessage(msg);

                    }
                } catch (JSONException ex) {
                    Logger.getLogger(MainRoomController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }

    public void sendResumeAcceptMessage(JSONObject msg) throws JSONException {
        msg.remove("Action");
        msg.remove("Content");
        msg.put("Action", "ResumeMatch");
        msg.put("Content", "Resume");
        ClientGui.printStream.println(msg.toString());
    }

    public void openInvitationRefusalScreen() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                showAlert("Invitation", "invitation refused .");
                PendingInvitation = false;
            }
        });

    }

    private Optional<ButtonType> showAlert(String title, String header) {

        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle(title);
        dialog.setContentText(header);
        dialog.setResizable(true);
        dialog.getDialogPane().setPrefSize(400, 200);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("../Media/MainRoom.css").toExternalForm());
        Optional<ButtonType> res = dialog.showAndWait();

        return res;
    }

    public void addplayer(JSONObject msg) throws JSONException {
        Playerslist.add(new DisplayPlayers(msg.getString("username"), "online"));
    }

    public void updateplayerlist(JSONObject msg) throws JSONException {
        for (int i = 0; i < Playerslist.size(); i++) {
            if (Playerslist.get(i).getName().equals(msg.getString("username"))) {
                Playerslist.set(i, new DisplayPlayers(msg.getString("username"), msg.getString("status")));
                break;
            }
        }
    }

    public void initPlayersTable(JSONObject msg) throws JSONException {
        JSONArray names = msg.getJSONArray("names");
        JSONArray status = msg.getJSONArray("status");

        for (int i = 0; i < names.length(); i++) {
            Playerslist.add(new DisplayPlayers(names.getString(i), status.getString(i)));

        }

    }

    public void initGamessTable(JSONObject msg) throws JSONException {
        savedGamesList.clear();
        JSONArray games = msg.getJSONArray("gamesArray");
        for (int i = 0; i < games.length(); i++) {
            savedGamesList.add(new savedGames(games.getJSONObject(i).getString("Opponent"), games.getJSONObject(i).getInt("gameID")));
        }
    }

    public void SavedGames(ActionEvent event) throws IOException, JSONException {

        gamesArea.setVisible(true);
    }

    public void LeaderBoard(ActionEvent event) throws IOException, JSONException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("Client/LeaderBoards/LeaderBoards.fxml"));
        Parent View = loader.load();
        Scene ViewScene = new Scene(View);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(ViewScene);
        window.show();
    }

}
