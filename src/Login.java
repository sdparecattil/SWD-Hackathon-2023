import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.SQLException;

//The Login class creates a swing GUI that makes a login field with a username and password, as well as buttons to log in or create a new player

public class Login extends JFrame{
    private JTextField usernameField; //Text-field to enter username
    private JTextArea usernameLabel; //label for username text-field
    private JTextField passwordField; //Text-field to enter password
    private JTextArea passwordLabel; //label for password text-field
    private JButton loginButton; //button to log in using entered username and password
    private JButton newPlayerButton; //button to create a new player with entered username and password
    private String username; //String to hold username after successfully logged in

    private Boolean loggedIn = false; //boolean representing log in status, true = logged in
    Database players = new Database(); //Database representing database containing usernames and passswords

    //The constructor for Login creates the GUI for the login screen
    public Login(){
        super("Login");
        players.databaseConnection(); //connects to database

        //creates panel for username text-field and label
        JPanel usernamePanel = new JPanel();
        usernameLabel = new JTextArea("Username: ");
        usernameLabel.setEditable(false);
        usernamePanel.add(usernameLabel, BorderLayout.WEST);
        usernameField = new JTextField("username");
        usernameField.addFocusListener(new FocusListener() { //If text-field is focused remove username prompt
            @Override
            public void focusGained(FocusEvent e) {
                if(usernameField.getText().equals("username")){
                    usernameField.setText("");
                }
            }
            @Override
            public void focusLost(FocusEvent e) { }
        });
        usernamePanel.add(usernameField, BorderLayout.EAST);
        add(usernamePanel, BorderLayout.NORTH);

        //creates pannel for password text-field and label
        JPanel passwordPanel = new JPanel();
        passwordLabel = new JTextArea("Password: ");
        passwordLabel.setEditable(false);
        passwordPanel.add(passwordLabel, BorderLayout.WEST);
        passwordField = new JTextField("password");
        passwordField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { //If text-field is focused remove password prompt
                if(passwordField.getText().equals("password")){
                    passwordField.setText("");
                }
            }
            @Override
            public void focusLost(FocusEvent e) { }
        });
        passwordField.addActionListener(new ActionListener() { //If user presses enter while in text field attempt to login
            @Override
            public void actionPerformed(ActionEvent e) {
                loggedIn = tryLogin(usernameField.getText(), passwordField.getText());//if successful set loggedIn to true
                if(loggedIn){ username = usernameField.getText(); } //if successful set username to entered text
            }
        });
        passwordPanel.add(passwordField, BorderLayout.EAST);
        add(passwordPanel,BorderLayout.CENTER);

        //Create panel to house login and new player
        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() { //if click try to log in
            @Override
            public void actionPerformed(ActionEvent e) {
                loggedIn = tryLogin(usernameField.getText(), passwordField.getText()); //if successful set loggedIn to true
                if(loggedIn){ username = usernameField.getText(); } //if successful set username to entered text
            }
        });
        buttonPanel.add(loginButton, BorderLayout.WEST);
        newPlayerButton = new JButton("New Player");
        newPlayerButton.addActionListener(new ActionListener() { //if click try to create new player
            @Override
            public void actionPerformed(ActionEvent e) {
                loggedIn = tryNewPlayer(usernameField.getText(), passwordField.getText()); //if successful set loggedIn to true
                if(loggedIn){ username = usernameField.getText(); } //if successful set username to entered text
            }
        });
        buttonPanel.add(newPlayerButton, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);
        setSize(300,140);
        setLocationRelativeTo(null); //Center GUI
        setVisible(true);
    }

    //try to log in to game with username and password. If password matches given username from database return true. If username DNE or password doesn't match return fasle
    private boolean tryLogin(String username, String password){
        try{
            return players.getPassword(username).equals(password);
        }
        catch (Exception e){
            return false;
        }
    }

    //try to create new player. If username is unique in database return true and put new username and password into database. If username is not return false
    private boolean tryNewPlayer(String username, String password){
        try{
            return players.addPlayer(username, password);
        }
        catch (Exception e){
            return false;
        }
    }

    //return the boolean value of the loggedIn variable
    public Boolean getLoggedIn() {
        return loggedIn;
    }

    //return the string value of the username variable
    public String getUsername(){
        this.dispose(); //close GUI window
        return username;
    }
}
