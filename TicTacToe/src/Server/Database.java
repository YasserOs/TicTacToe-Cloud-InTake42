package Server;

import models.*;
import java.sql.*;
import java.util.Date;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Vector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Database {

    ResultSet rs;
    Connection conn;
    private final String url = "jdbc:postgresql://localhost/tic-tac-toe";
    private final String user = "postgres";
    private final String password = "admin";

    public Database() throws SQLException {
        connect();
    }
    public void connect() throws SQLException {
        try {
            conn = DriverManager.getConnection(url, user, password);

            System.out.println("Connected to the PostgreSQL server successfully. ");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
     public Vector<Person> Top5Players() throws SQLException {  //--------> add this in db i guess!
        Vector<Person> players = new Vector<Person>();
        conn = DriverManager.getConnection(url, user, password);
        Statement stmt = conn.createStatement();
        String queryString = new String("select * from players order by total_score desc limit 5");
        rs = stmt.executeQuery(queryString);
        while (rs.next()) {
            Person p = createPerson(rs);
            players.add(p);

        }
        return players;
    }
    public Vector<Person> getPlayers() throws SQLException {
        Vector<Person> players = new Vector<Person>();
        conn = DriverManager.getConnection(url, user, password);
        Statement stmt = conn.createStatement();
        String queryString = new String("select * from players");
        rs = stmt.executeQuery(queryString);
        while (rs.next()) {
            Person p = createPerson(rs);
            players.add(p);

        }
        return players;
    }

   public Person getPlayer(String username) throws SQLException {
       
        conn = DriverManager.getConnection(url, user, password);
        Statement stmt = conn.createStatement();
        String queryString = new String("select * from players where username ='"+username+"' ");
        
        rs = stmt.executeQuery(queryString);
        rs.next();
        Person p = createPerson(rs);
        conn.close();
        return p ;
    }
   
    public Person createPerson(ResultSet rs) throws SQLException {

        Person p = new Person(rs.getString(1), rs.getString(4), rs.getInt(3), rs.getString(5), rs.getDate(6), rs.getInt(7), rs.getInt(8), rs.getInt(9), rs.getInt(10), rs.getInt(11));
        return p;
        
    }

    

    //Hashed and Encrypted Password
    public String passwordEnc(String password) {

        String encryptedpassword = null;
        try {
            /* MessageDigest instance for MD5. */
            MessageDigest m = MessageDigest.getInstance("MD5");
            /* Add plain-text password bytes to digest using MD5 update() method. */
            m.update(password.getBytes());
            /* Convert the hash value into bytes */
            byte[] bytes = m.digest();
            /* The bytes array has bytes in decimal form. Converting it into hexadecimal format. */
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            /* Complete hashed password in hexadecimal format */
            encryptedpassword = s.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encryptedpassword;

    }

    // Sign Up Function
    public Person signUp(String username, String pswd, String email) throws SQLException {
            Person p;
        try {
            conn = DriverManager.getConnection(url, user, password);
            String queryString = new String("insert into players"
                    + " (username,password,email,status ,last_seen , total_score, games_played, games_won, games_lost , draws)"
                    + "  values(?,?,?,?,NOW(),?,?,?,?,?)");
            PreparedStatement stmt = conn.prepareStatement(queryString);
            stmt.setString(1, username);
            stmt.setString(2, passwordEnc(pswd));
            stmt.setString(3, email);
            stmt.setString(4,"online");
            stmt.setInt(5, 0);
            stmt.setInt(6, 0);
            stmt.setInt(7, 0);
            stmt.setInt(8, 0);
            stmt.setInt(9, 0);
            stmt.executeUpdate();
            queryString = "select * from players where username=?";
            stmt = conn.prepareStatement(queryString);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            p = createPerson(rs);
            System.out.println("Record successfully Inserted. ");
            stmt.close();
        } catch (SQLException ex) {
            //can use it to print duplicate key error
            System.err.println(ex.getMessage());
            return null;
        }

        conn.close();

        return p;
    }
    
    //Check Register 
     public int checkRegister(String username, String email) throws SQLException 
    {
        ResultSet rs ;
        PreparedStatement ps;
        
            try{
            conn = DriverManager.getConnection(url,user,password);
            ps = conn.prepareStatement("select * from players where username = ? or email = ?");
            ps.setString(1, username);
            ps.setString(2, email);
            rs =  ps.executeQuery();
            if(!rs.next()){
                return 1; //email already used
            }
            System.out.println("Already Signed Up");
            
        }catch(SQLException ex){
            ex.printStackTrace();
            return 3; //error failed to connect to DB
        }
        ps.close();
        conn.close();
        return 2; // Both username and email not used
    }
    
    
    
    
    // login With username
    public int logIn(String username, String pswd) throws SQLException {
            String passwordEncrypted = passwordEnc(pswd);
         try {
              
             conn = DriverManager.getConnection(url, user, password);
            Statement selectStatement = conn.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_SCROLL_INSENSITIVE);
            String query = new String("select username, password,status from players where username='" + username +"'");
            ResultSet rs = selectStatement.executeQuery(query);
            rs.next();
            String retrievedPassword=rs.getString("password");
            if(retrievedPassword.equals(passwordEncrypted) ){
                System.out.println("Logged in successfully");
                String Status=rs.getString("status");
                if(Status.equals("online")){
                  return 3; //the user is already logged/ online
                }
                
                selectStatement.close();
                conn.close();
            }else{
                System.out.println("The password you entered is incorrect");
                selectStatement.close();
                conn.close();
                return 2; // the password is incorrect
            }
            
        } catch (SQLException ex) {
            //can use it to print duplicate key error
            System.err.println(ex.getMessage());
             System.out.println("The username you entered is not correct");
             conn.close();
            return 4; // error occured while connecting to the datebase
        }
         conn.close();
         return 1;// the username and pw are correct success
        // successful retrieved the user from the datebase and the password is entered correctly
    }
    
    //In database
    public JSONObject getLeaderBoard() throws SQLException, JSONException {
        JSONObject leaderboard = new JSONObject();
        JSONArray names = new JSONArray();
        JSONArray scores = new JSONArray();
        try {
            conn = DriverManager.getConnection(url, user, password);
            Statement selectstatement = conn.createStatement();
            String leaderBoardQuery = new String("select username,total_score from players where total_score not in(0) order by total_score desc limit 5");
            ResultSet rs = selectstatement.executeQuery(leaderBoardQuery);
            while (rs.next()) {
                names.put(rs.getString("username"));
                scores.put(rs.getInt("total_score"));
            }
            leaderboard.put("names", names);
            leaderboard.put("scores", scores);

            selectstatement.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            conn.close();
        }
        return leaderboard;
    }
    public ArrayList<String> allPlayers() throws SQLException {
        String allPlayers = new String();
        ArrayList<String> playerList = new ArrayList<String>();
        playerList.add(allPlayers);
        try {
            conn = DriverManager.getConnection(url, user, password);
            Statement selectstatement = conn.createStatement();
            String leaderBoardQuery = new String("select id,username, status, last_seen,total_score, games_played, games_won, games_lost from players where username not in('Computer') order by total_score desc;");
            ResultSet rs = selectstatement.executeQuery(leaderBoardQuery);
            while (rs.next()) {
                Date dateSql = rs.getDate("last_seen");
                String date = dateSql.toString();
                allPlayers =  String.valueOf(rs.getInt("id")) + ":" + rs.getString("username") + ":" + rs.getString("status") + ":" + date + ":" + String.valueOf(rs.getInt("total_score")) + ":" + String.valueOf(rs.getInt("games_played")) + ":" + String.valueOf(rs.getInt("games_won")) + ":" + String.valueOf(rs.getInt("games_lost"));
                playerList.add(allPlayers);
            }
            selectstatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            conn.close();
        }
        conn.close();
        return playerList;

    }
    public boolean createGame(String playerOneName, String playerTwoName){
        String query = new String("insert into games(player_one_name, player_two_name) values(?, ?)");
        try {
            
            PreparedStatement createGameStatement = conn.prepareCall(query);
            createGameStatement.setString(1, playerOneName);
            createGameStatement.setString(2, playerTwoName);
            createGameStatement.executeUpdate();
            createGameStatement.close();
            
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        return true;
    }
    
    
    public ObservableList<DisplayPlayers> displayPlayers()
    {
        ObservableList<DisplayPlayers> list = FXCollections.observableArrayList(); 

        try {
            conn = DriverManager.getConnection(url, user, password);
            Statement select = conn.createStatement();
            String query ="select username, status,total_score from players";
            ResultSet rs = select.executeQuery(query);
            
            while(rs.next()){
            
                list.add(new DisplayPlayers(rs.getString(1), rs.getString(2),rs.getInt(3)));
            
            }
            select.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    
    
        return list;
    }
    
     public boolean saveGame(JSONObject session) throws JSONException{
        int gameId;
        try {
            conn = DriverManager.getConnection(url,user,password);
            
            String query = new String("insert into save_game(player_one_name, player_one_choice, player_two_name, player_two_choice,"
                                 + " square1, square2, square3, square4 ,"
				 +" square5, square6, square7, square8, square9, turn)"
                                 + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement saveStatement = conn.prepareStatement(query);
            
            saveStatement.setString(1, session.getString("playerOne"));
            saveStatement.setString(2, session.getString("playerOnePick"));
            saveStatement.setString(3, session.getString("playerTwo"));
            saveStatement.setString(4, session.getString("playerTwoPick"));
            
            int i =5;
            JSONArray board = session.getJSONArray("board");
            for (int j = 0; j < board.length();  j++) {
                saveStatement.setString(i, (String) board.get(j));
                i++;
            }
            saveStatement.setString(14 ,session.getString("turn") );
            saveStatement.executeUpdate();
            saveStatement.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    public JSONObject getSavedGame(int gameID) throws JSONException{
        JSONObject gameDetails = new JSONObject();

        try {
            conn = DriverManager.getConnection(url,user, password); 
            
            String query= new String("select * from save_game where game_id=?");
            PreparedStatement savedGameStatement = conn.prepareStatement(query);
            savedGameStatement.setInt(1,gameID );
            rs = savedGameStatement.executeQuery();
            rs.next();
            gameDetails.put("playerOne", rs.getString("player_one_name"));
            gameDetails.put("playerOnePick", rs.getString("player_one_choice"));
            gameDetails.put("playerTwo", rs.getString("player_two_name"));
            gameDetails.put("playerTwoPick", rs.getString("player_two_choice") );
            gameDetails.put("turn", rs.getString("turn"));
            JSONArray board = new JSONArray();
            // Board 
            int i=6;
            while (i<15) {
                board.put(rs.getString(i++));  
            }
            gameDetails.put("board", board);
            query = "delete from save_game where game_id=?";
            savedGameStatement = conn.prepareStatement(query);
            savedGameStatement.setInt(1, gameID);
            savedGameStatement.executeUpdate();
            savedGameStatement.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            
        }
        return gameDetails;
    }
    public JSONArray getPlayerSavedGames(String playerName) throws JSONException{
         JSONArray savedGames = new JSONArray();

        try {
            conn = DriverManager.getConnection(url,user, password); 
            String query= new String("select * from save_game where player_one_name=? or player_two_name=?" );
            PreparedStatement  savedGameStatement = conn.prepareStatement(query);
            savedGameStatement.setString(1, playerName);
            savedGameStatement.setString(2, playerName);
            ResultSet rs = savedGameStatement.executeQuery();
            while(rs.next()){
                JSONObject game = new JSONObject();
                game.put("gameID", rs.getInt("game_id"));
                  if (rs.getString("player_one_name").equals(playerName)) {
                      game.put("Opponent", rs.getString("player_two_name"));
                  }else{
                      game.put("Opponent", rs.getString("player_one_name"));
                  }
                savedGames.put(game);
            }
            savedGameStatement.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return savedGames;
    }
    public boolean playerClosing(Person p){
        try {
          conn = DriverManager.getConnection(url, user, password);
          String query = "update players set total_score=?,games_played=?, games_won=?, games_lost=?, draws=?, status='offline' where username=?";  
          PreparedStatement saveDetails = conn.prepareStatement(query);
          saveDetails.setInt(1, p.getTotal_score());
          saveDetails.setInt(2, p.getGames_played());
          saveDetails.setInt(3, p.getGames_won());
          saveDetails.setInt(4, p.getGames_lost());
          saveDetails.setInt(5, p.getDraws());
          saveDetails.setString(6, p.getUsername());
          saveDetails.executeUpdate();
          saveDetails.close();
          conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
         return true;
    }
    
    public boolean updateGame(String winner, String loser, int gameID){
        String query = new String("update games set game_state= ?,game_result=?, winner= ? where game_id =?");
        try {
            conn = DriverManager.getConnection(url, user, password);
            PreparedStatement updateGameStatement = conn.prepareStatement(query);
            updateGameStatement.setString(1, "Finished");
            updateGameStatement.setString(2, "Resolved");
            updateGameStatement.setString(3, winner);
            updateGameStatement.setInt(4, gameID);
            updateGameStatement.executeUpdate();
            updateGameStatement.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
       try{
           // updating the winner
           Statement selectPlayerStatement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
           ResultSet rs =selectPlayerStatement.executeQuery("select * from players where username='" + winner + "'");
           rs.next();
           int gamesWon = rs.getInt("games_won")+1;
           int score = rs.getInt("total_score")+3;
           rs.updateInt("total_score", score);
           rs.updateInt("games_won", gamesWon);
           rs.updateRow();
           // updating the loser
           if((!loser.equals("Computer"))){
           rs = selectPlayerStatement.executeQuery("select * from players where username='" + loser + "'");
           rs.next();
           int gamesLost = rs.getInt("games_Lost")+1;
           rs.updateInt("games_Lost", gamesLost);
           rs.updateRow();
           }
           
           selectPlayerStatement.close();
           conn.close();
       }catch(SQLException ex){
           ex.printStackTrace();
            System.err.println(ex.getMessage());
       }
       
        return true;
    }
    public boolean draw(String playerOne, String PlayerTwo, int gameID){
        try {
            conn = DriverManager.getConnection(url, user, password);
             String query = new String("update games set game_state= ?,game_result=?, winner= ? where game_id =?");
            PreparedStatement drawGameStatement = conn.prepareStatement(query);
            drawGameStatement.setString(1, "Finished");
            drawGameStatement.setString(2, "Draw");
            drawGameStatement.setString(3, "None");
            drawGameStatement.setInt(4, gameID);
            drawGameStatement.executeUpdate();
            drawGameStatement.close();
            query = "select * from players where username='" + playerOne + "'";
            Statement updatePlayersStatement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = updatePlayersStatement.executeQuery(query);
            rs.next();
            int draws=rs.getInt("draws") +1 ;
            int score = rs.getInt("total_score")+1;
            rs.updateInt("total_score", score);
            rs.updateInt("draws", draws);
            rs.updateRow();
            if(!(PlayerTwo.equals("Computer"))){
            rs = updatePlayersStatement.executeQuery("select * from players where username='" + PlayerTwo + "'");
            rs.next();
            int drawsSec = rs.getInt("draws")+1;
            int scoreSec = rs.getInt("total_score")+1;
            rs.updateInt("total_score", scoreSec);
            rs.updateInt("draws", drawsSec);
            rs.updateRow();
            }
            updatePlayersStatement.close();
            conn.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return true;
    }

    
    public boolean updatePlayerStatus(String username, String status){
        
        try {
            conn = DriverManager.getConnection(url, user, password);
            String query = "update players set status=? where username=? ";
            PreparedStatement update = conn.prepareStatement(query);
            update.setString(1, status);
            update.setString(2, username);
            update.executeUpdate();
            update.close();
            conn.close();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    
    
        return true;
    }
    // login With email
    public boolean logInUsingEmail(String email, String pswd) throws SQLException {
            String passwordEncrypted = passwordEnc(pswd);
            ResultSet rs;
         try {
             conn = DriverManager.getConnection(url, user, password);
            Statement selectStatement = conn.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_SCROLL_INSENSITIVE);
            String query = new String("select * from players where email='" + email +"'");
            rs = selectStatement.executeQuery(query);
            rs.next();
            String retrievedPassword=rs.getString("password");
            if(retrievedPassword.equals(passwordEncrypted ) ){
                System.out.println("Logged in successfully");
                String updatingStatus=rs.getString("status");
                updatingStatus = "Online";
                rs.updateString("status", updatingStatus);
                
                selectStatement.close();
                conn.close();
            }else{
                System.out.println("The password you entered is incorrect");
                selectStatement.close();
                conn.close();
                return false;
            }
            
        } catch (SQLException ex) {
            //can use it to print duplicate key error
            System.err.println(ex.getMessage());
             System.out.println("The email you entered is not correct");
             conn.close();
            return false;
        }
         conn.close();
        return true;
    }
    
    //get UserName By Email
    public String getUsername(String email){
        ResultSet rs ; 
        String username;
        PreparedStatement ps;
        try{
            conn = DriverManager.getConnection(url,user,password);
            ps = conn.prepareStatement("select * from players where email = ?");
            ps.setString(1,email);
            rs = ps.executeQuery();
            rs.next();
            username = rs.getString(1);
            return username;
        }catch(SQLException ex){
            System.out.print("Invaild Email");
        }
        return null;
    }
    
    //get Email By Username
    public String getEmail(String username){
        ResultSet rs ; 
        String email;
        PreparedStatement ps;
        try{
            conn = DriverManager.getConnection(url,user,password);
            ps = conn.prepareStatement("select * from players where username = ?");
            ps.setString(1,username);
            rs = ps.executeQuery();
            rs.next();
            email = rs.getString(3);
            return email;
        }catch(SQLException ex){
            System.out.print("Invaild Email");
        }
        return null;
    }
    // Person p = new Person();
    public void removeRecord(int ID) throws SQLException {
        try {
            conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            String queryString = new String("DELETE FROM players WHERE id=" + ID + ";");
            stmt.executeUpdate(queryString);
            System.out.println("Deleted successfully. ");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        conn.close();

    }
}
