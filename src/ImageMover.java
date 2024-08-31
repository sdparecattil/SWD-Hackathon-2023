import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ImageMover extends JFrame implements Runnable, KeyListener {
    private int frameWidth; //frame width
    private int frameHeight; // frame Height
    private JLabel backgroundLabel; // holds label added for background
    private ImageIcon mapBackground; // hold image for background
    private JLabel[] playerLabels = new JLabel[4]; // Array fo players (movable)
    private JPanel gameBoard; // Game Panel for GUI
    private JPanel mainBoard; // Main Board that hold GUI
    private static String[] coordinates = {"","","",""}; // Constant Coordinate array Static across all instances
    private String[] allPlayersStatus = {"TRUE","TRUE","TRUE","TRUE"}; //PLayer Status Dead or Alive
    private String[] deadIconsUsed = {"FALSE","FALSE","FALSE","FALSE"}; // Dead Icon Enabled
    private String[] playersVisible =  {"FALSE","FALSE","FALSE","FALSE"}; // Player is Visible to instance
    private JLabel[] deadCharacters = new JLabel[4]; // deadCharacter Labels hold Images
    private int[] playerLabelsUsed = {1,2,3,4}; // Labels Used to Create Players
    private int playerID; // Player ID
    private int playerIDMaster; // PLayer ID Master
    private boolean iAmImposter = false; // Imposter - Enables Kill Feature
    private String deadCase = ""; // Dead Case sent to client relationship
    private ImageIcon redDead; // Image red dead player
    private Image redDeadResized; // Resize red dead player
    private ImageIcon orangeDead; // Image orange dead Player
    private Image orangeDeadResized; // Resize orange dead PLayer
    private ImageIcon pinkDead; // Image pink dead player
    private Image pinkDeadResized; // Resize pink dead PLayer
    private ImageIcon blueDead; // Image blue dead player
    private Image blueDeadResized; // Resize blue dead PLayer
    private String deadXCoordinate; // X Coordinate for dead image to update to
    private String deadYCoordinate; // Y Coordinate for dead image to update to
    private boolean report = false; // Report boolean allows for report feature


    // Constructor Creates and Sets GUI, Configures all Labels, Configures all Icons.
    public ImageMover(int playerID) {

        // Sets playerID
        this.playerID = playerID;
        // Sets MasterID
        this.playerIDMaster = this.playerID;

        // Sets Title of each Window
        setTitle("DECEIT IN THE COSMOS - WANDERER");
        //Adds Game board to mainBoard
        mainBoard = new JPanel(new BorderLayout());
        gameBoard = new JPanel(new BorderLayout());

        // Closes automatically when window closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame width
        frameWidth = 1360;
        // frame height
        frameHeight = 798;
        // set frame size of JFrame
        setSize(frameWidth,frameHeight);

        // Add keyListener to JFrame
        addKeyListener(this);

        // Sets Layout to nll in order set bounds of JLabels
        gameBoard.setLayout(null);

        // Creates Player Labels
        playerLabels[0] = new JLabel();
        playerLabels[1] = new JLabel();
        playerLabels[2] = new JLabel();
        playerLabels[3] = new JLabel();

        // Creates dead Player Labels
        deadCharacters[0] = new JLabel();
        deadCharacters[1] = new JLabel();
        deadCharacters[2] = new JLabel();
        deadCharacters[3] = new JLabel();

        // Images all resized and set so transparent image is compatible
        // Does this for all dead and Alive Player Images
        // All images fround from Google Images
        ImageIcon blueDude = new ImageIcon(this.getClass().getResource("BlueDude.png"));
        Image blueDudeImage = blueDude.getImage();
        Image blueDudeResized = blueDudeImage.getScaledInstance(30,30, BufferedImage.SCALE_SMOOTH);

        ImageIcon pinkGirl = new ImageIcon(this.getClass().getResource("PinkGirl.png"));
        Image pinkGirlImage = pinkGirl.getImage();
        Image pinkGirlResized = pinkGirlImage.getScaledInstance(30,30, BufferedImage.SCALE_SMOOTH);

        ImageIcon orangeDude = new ImageIcon(this.getClass().getResource("OrangeDude.png"));
        Image orangeDudeImage = orangeDude.getImage();
        Image orangeDudeResized = orangeDudeImage.getScaledInstance(30,30, BufferedImage.SCALE_SMOOTH);

        ImageIcon redDude = new ImageIcon(this.getClass().getResource("RedDude.png"));
        Image redDudeImage = redDude.getImage();
        Image redDudeResized = redDudeImage.getScaledInstance(30,30, BufferedImage.SCALE_SMOOTH);

        this.redDead = new ImageIcon(this.getClass().getResource("redDead.png"));
        Image redDudeDead = redDead.getImage();
        this.redDeadResized = redDudeDead.getScaledInstance(30,30, BufferedImage.SCALE_SMOOTH);

        this.orangeDead = new ImageIcon(this.getClass().getResource("orangeDead.png"));
        Image orangeDudeDead = orangeDead.getImage();
        this.orangeDeadResized = orangeDudeDead.getScaledInstance(30,30, BufferedImage.SCALE_SMOOTH);

        this.pinkDead = new ImageIcon(this.getClass().getResource("pinkDead.png"));
        Image pinkDudeDead = pinkDead.getImage();
        this.pinkDeadResized = pinkDudeDead.getScaledInstance(30,30, BufferedImage.SCALE_SMOOTH);

        this.blueDead = new ImageIcon(this.getClass().getResource("blueDead.png"));
        Image blueDudeDead = blueDead.getImage();
        this.blueDeadResized = blueDudeDead.getScaledInstance(30,30, BufferedImage.SCALE_SMOOTH);

        // If player ID matches then set labels for that instance of the ImageMover. Sets bounds of labels and starting coordinates
        if (playerID == 1) {
            //iAmImposter = true;
            playersVisible[playerID - 1] = "TRUE";
            playerLabels[0].setBounds(680,240,30,30);
            playerLabels[0].setIcon(new ImageIcon(orangeDudeResized));
            playerLabels[1].setBounds(740,240,30,30);
            playerLabels[1].setIcon(new ImageIcon(blueDudeResized));
            playerLabels[2].setBounds(680,280,30,30);
            playerLabels[2].setIcon(new ImageIcon(redDudeResized));
            playerLabels[3].setBounds(740,280,30,30);
            playerLabels[3].setIcon(new ImageIcon(pinkGirlResized));
        }
        else if (playerID == 2) {
            playersVisible[playerID - 1] = "TRUE";
            playerLabels[1].setBounds(740,240,30,30);
            playerLabels[1].setIcon(new ImageIcon(blueDudeResized));
            playerLabels[0].setBounds(680,240,30,30);
            playerLabels[0].setIcon(new ImageIcon(orangeDudeResized));
            playerLabels[2].setBounds(680,280,30,30);
            playerLabels[2].setIcon(new ImageIcon(redDudeResized));
            playerLabels[3].setBounds(740,280,30,30);
            playerLabels[3].setIcon(new ImageIcon(pinkGirlResized));
        }
        else if (playerID == 3) {
            playersVisible[playerID - 1] = "TRUE";
            playerLabels[2].setBounds(680,280,30,30);
            playerLabels[2].setIcon(new ImageIcon(redDudeResized));
            playerLabels[1].setBounds(740,240,30,30);
            playerLabels[1].setIcon(new ImageIcon(blueDudeResized));
            playerLabels[0].setBounds(680,240,30,30);
            playerLabels[0].setIcon(new ImageIcon(orangeDudeResized));
            playerLabels[3].setBounds(740,280,30,30);
            playerLabels[3].setIcon(new ImageIcon(pinkGirlResized));
        }
        else if (playerID == 4){
            playersVisible[playerID - 1] = "TRUE";
            playerLabels[3].setBounds(740,280,30,30);
            playerLabels[3].setIcon(new ImageIcon(pinkGirlResized));
            playerLabels[1].setBounds(740,240,30,30);
            playerLabels[1].setIcon(new ImageIcon(blueDudeResized));
            playerLabels[2].setBounds(680,280,30,30);
            playerLabels[2].setIcon(new ImageIcon(redDudeResized));
            playerLabels[0].setBounds(680,240,30,30);
            playerLabels[0].setIcon(new ImageIcon(orangeDudeResized));
        }

        // Added labels to teh gameBoard
        for (int i = 0; i < playerLabels.length; i++){
            gameBoard.add(playerLabels[i]);
        }

        // Creates the Background and sets the PNG to the JLabel, Sets opacity, so it is visible.
        mapBackground = new ImageIcon(this.getClass().getResource("Map.jpg"));
        backgroundLabel = new JLabel(mapBackground);
        backgroundLabel.setSize(1368,768);
        backgroundLabel.setOpaque(true);

        // Adds Background to gameBoard Panel
        gameBoard.add(backgroundLabel);

        // Adds gameBoard to mainBoard
        mainBoard.add(gameBoard, BorderLayout.CENTER);

        // Adds MainBoard to JFRAME
        add(mainBoard);
        setLocationRelativeTo(null);// Sets to Center of screen
        setResizable(false); // Sets resizable window false
        setVisible(true); // Sets visibility of JFrame to true
    }

    //Method needed to implement keyListener
    @Override
    public void keyTyped(KeyEvent e) {    }

    @Override
    public void keyPressed(KeyEvent e) {
        String coordinatePlayer = "";
        //Left Key Press set static array index of ID to match teh coordinates of teh label when pressed
        if (e.getKeyCode() == 37 && checkBoundsOfFrame(37)) {
            playerLabels[playerID-1].setLocation(playerLabels[playerID-1].getX() - 10, playerLabels[playerID-1].getY());
            coordinatePlayer = playerLabels[playerID-1].getX() + "," + playerLabels[playerID-1].getY();
            coordinates[playerID - 1] = coordinatePlayer;
        }
        //Up Key Press set static array index of ID to match teh coordinates of teh label when pressed
        else if (e.getKeyCode() == 38 && checkBoundsOfFrame(38)) {
            playerLabels[playerID-1].setLocation(playerLabels[playerID-1].getX(), playerLabels[playerID-1].getY() - 10);
            coordinatePlayer = playerLabels[playerID-1].getX() + "," + playerLabels[playerID-1].getY();
            coordinates[playerID - 1] = coordinatePlayer;
        }
        //Right Key Press set static array index of ID to match teh coordinates of teh label when pressed
        else if (e.getKeyCode() == 39 && checkBoundsOfFrame(39)) {
            playerLabels[playerID-1].setLocation(playerLabels[playerID-1].getX() + 10, playerLabels[playerID-1].getY());
            coordinatePlayer = playerLabels[playerID-1].getX() + "," + playerLabels[playerID-1].getY();
            coordinates[playerID - 1] = coordinatePlayer;
        }
        //Down Key Press set static array index of ID to match teh coordinates of teh label when pressed
        else if (e.getKeyCode() == 40 && checkBoundsOfFrame(40)) {
            playerLabels[playerID-1].setLocation(playerLabels[playerID-1].getX(), playerLabels[playerID-1].getY() + 10);
            coordinatePlayer = playerLabels[playerID-1].getX() + "," + playerLabels[playerID-1].getY();
            coordinates[playerID - 1] = coordinatePlayer;
        }

        // if imposter presses enter on any player near 10 pixels then kill and updates all arrays stating that player is dead and  icon dead has been uploaded.
        if (iAmImposter == true && e.getKeyCode() == 10) {
            for (int i = 0; i < playerLabels.length; i ++) {
                if (Math.abs(playerLabels[playerID-1].getX() - playerLabels[i].getX()) <= 20 &&  i + 1 != playerID &&  Math.abs(playerLabels[playerID-1].getY() - playerLabels[i].getY()) <=20) {
                    allPlayersStatus[i] = "FALSE";
                    deadIconsUsed[i] = "TRUE";
                    deadXCoordinate = String.valueOf(playerLabels[i].getX());
                    deadYCoordinate = String.valueOf(playerLabels[i].getY());
                }
            }
        }
        else if (iAmImposter == false && e.getKeyCode() == 82) {
            report = true;
        }
    }
    // Gets coordinates of JLabel moving on screen
    public String getTheCoordinateOfSquare() {
        return playerLabels[playerID-1].getX() + "," + playerLabels[playerID-1].getY();
    }

    // Check is coordinate for player moving on screen is greater than bounds of the window, if so don't let the JLabel move.
    private boolean checkBoundsOfFrame(int keyValue) {
        // Checks Down Arrow Key Collision
        if (keyValue == 40) {
            if (playerLabels[playerID-1].getY() + 10 > 740) {
                return false;
            }
            return true;
        }
        // Checks Right Arrow Key Collision
        else if (keyValue == 39) {
            if (playerLabels[playerID-1].getX() + 10 > 1340) {
                return false;
            }
            return true;
        }
        // Checks Up Arrow Key Collision
        else if (keyValue == 38) {
            if (playerLabels[playerID-1].getY() + 10 < 20) {
                return false;
            }
            return true;
        }
        // Checks Left Arrow Key Collision
        else if (keyValue == 37) {
            if (playerLabels[playerID-1].getX() - 10 < 0 ) {
                return false;
            }
            return true;
        }
        else {
            return true;
        }
    }

    // Needed to implement Key Listner
    @Override
    public void keyReleased(KeyEvent e) {

    }

    // Main Method - ONLY FOR TESTING PURPOSES
//    public static void main (String[] args) {
//        ExecutorService executor = Executors.newCachedThreadPool();
//        for (int i = 0; i < 4; i ++) {
//            executor.execute(new ImageMover(i+1));
//        }
//
//    }
    // Run method for executor service. Constantly updates PLayer Label coordinates and check to see if labels are ou of bounds.
    @Override
    public void run() {
        while (true) {
            for (int i = 0; i < playerLabels.length; i++) {
                if (i+1 != playerIDMaster && !(coordinates[i].equals("")))  {
                    int x = Integer.valueOf(coordinates[i].substring(0,coordinates[i].indexOf(",")));
                    int y = Integer.valueOf(coordinates[i].substring(coordinates[i].indexOf(",") + 1));;
                    playerLabels[i].setLocation(x,y);
                }
            }
            getPlayersVisible();
            checkDead();
        }
    }
    // Receives Message from Client from Server for what coordinate all players will be at.
    // Parses through returned string and changes static coordinate array so all instances pull from
    // coordinates.
    public void sendCoordinates(String message) {
        int loopCount = 0;
        String temp = "";
        for (int i = 0; i < message.length(); i++) {
            if (('|') == (message.charAt(i))) {
                loopCount++;
            }
        }
        message = message.substring(1);
        for (int i = 0; i < loopCount; i++){
            if (loopCount - 1 == i) {
                temp = message;
                coordinates[i] = temp;
            }
            else {
                temp = message.substring(0, message.indexOf('|'));
                message = message.substring(message.indexOf('|') + 1);
                coordinates[i] = temp;
            }
        }
    }
    // Client sends down message from server that saying who is deceiver,
    // if player ID matches then set that player as Imposter - enabling kill function
    // SetsTitle of Window to show the user they are imposter and not wanderer.
    public void sendImposter(String message){
        int imposter =  Integer.valueOf(message.substring(message.length() - 1));
        if (playerID == imposter) {
            iAmImposter = true;
            setTitle("DECEIT IN THE COSMOS - DECEIVER");
        }
    }
    // Sends a concatenated string to client for user info, called continuously in Client.
    public String getPlayerStatus() {
        for (int i = 0; i < playerLabelsUsed.length; i++) {
            deadCase = "@@@"; // Identifier  for Client
            if (allPlayersStatus[i].equals("FALSE") && deadIconsUsed[i].equals("TRUE") && playerLabelsUsed[i] != 0) {
                playerLabelsUsed[i] = 0;
                deadCase += i+1 + ",";
                deadCase += allPlayersStatus[i] + ",";
                deadCase += deadIconsUsed[i] + ",";
                deadCase += deadXCoordinate + ",";
                deadCase += deadYCoordinate;
                break;
            }
        }
        return deadCase;
    }
    // Enables ghost feature, and accounts for if kill is processed on JLabel
    public void setPlayerStatus(String message) {
        message = message.substring(3);
        // Get ID
        int ID = Integer.valueOf(message.substring(0,1));
        message = message.substring(13);
        // Get lat known player X coordinate
        deadXCoordinate = message.substring(0,message.indexOf(','));
        // Get last known player Y coordinate;
        deadYCoordinate = message.substring(message.indexOf(',') + 1);
        // Set status to Dead now that player has been killed
        allPlayersStatus[ID - 1] = "FALSE";
        // Set dead Icon to used;
        deadIconsUsed[ID - 1] = "TRUE";
        // SetIcon Function called, sets images
        setIcon(ID);
        if (ID == playerID) {
            playerLabels[ID-1].setEnabled(false);
        }
        else{
            playerLabels[ID-1].setVisible(false);
            playerLabels[ID-1].setEnabled(false);
        }
    }
    // Sets the Icon of the dead Image when someone is killed. (Buggy)...
    // For the ID value set the dead character the to the specific color image.
    private void setIcon(int ID){
        if (ID == 1) {
            deadCharacters[ID-1].setIcon(new ImageIcon(orangeDeadResized));
        }
        else if (ID == 2) {
            deadCharacters[ID-1].setIcon(new ImageIcon(blueDeadResized));
        }
        else if (ID == 3) {
            deadCharacters[ID-1].setIcon(new ImageIcon(redDeadResized));
        }
        else if (ID == 4) {
            deadCharacters[ID-1].setIcon(new ImageIcon(pinkDeadResized));
        }
        deadCharacters[ID - 1].setBounds(Integer.valueOf(deadXCoordinate),Integer.valueOf(deadYCoordinate),30,30);
        backgroundLabel.add(deadCharacters[ID-1]);

    }
    // Check is labels of players are within the proximity.
    public void getPlayersVisible() {
        // Loops through entire player label array
        for (int i = 0; i < playerLabels.length; i++) {
            // Creates a radius, if the label is in the radius then set array value to TRUE, else FALSE
            if (Math.sqrt(Math.pow(playerLabels[playerID - 1].getX() - playerLabels[i].getX(),2) + Math.pow(playerLabels[playerID - 1].getY() - playerLabels[i].getY(),2)) < 125) {
                playersVisible[i] = "TRUE";
            }
            else{
                playersVisible[i] = "FALSE";
            }
        }
    }
    // Checks if Labels are within the proximity set, if not then set labels to not visible, vice versa
    private void checkDead() {
        for (int i = 0; i < playerLabels.length; i++) {
            if (playersVisible[i].equals("FALSE") || allPlayersStatus[i].equals("FALSE")) {
                playerLabels[i].setVisible(false);
            }
            else {
                playerLabels[i].setVisible(true);
            }
        }
    }

    // Report called by client and sends message that Report has been pressed, make meeting...
    public boolean getReport(){
        return report;
    }
    // set value of report variable
    public void setReport(boolean report){
        this.report = report;
    }
}
