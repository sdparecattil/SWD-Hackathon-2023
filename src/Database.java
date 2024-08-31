import java.sql.*;

//The Database class is used to maintain connection between the database and get and add usernames and passwords from said database
public class Database{
    private Connection connection; //Variable used to represent the connection to the database server
    private String url = "jdbc:mysql://s-l112.engr.uiowa.edu/" + "engr_class009" + "?enabledTLSProtocols=TLSv1.2"; //URL to access the database server

    //method that logs in the database and creates the connection variable
    public void databaseConnection() {
        try {
            String username = "engr_class009";
            String password = "swd2023$";
            this.connection  = DriverManager.getConnection(url, username, password); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    //method that takes a username and gets the corresponding password from the database
    public String getPassword(String username){
        try{
            String query = "SELECT Password FROM Players WHERE Username = '" + username + "'"; //SQL query used to gather password
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query); //Query execution
            if(resultSet.next()) return resultSet.getString("Password"); //return the string of the password if it exists
            else return null; //return null if username is not in database
        }
        catch (Exception e){
            return null;
        }
    }

    //method for creating a new username and password inside the database
    public boolean addPlayer(String username, String password) throws SQLException {
        try{
            String update = "INSERT INTO Players (Username, Password) VALUES ('" + username + "', '" + password + "')"; //SQL command to create new username and password in database
            Statement statement = connection.createStatement();
            statement.executeUpdate(update); //execute command
            return true; //return true if added
        }
        catch (Exception e){
            return false; //return false if unable to add username and password
        }
    }
}
