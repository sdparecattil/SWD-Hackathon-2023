import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client extends JFrame implements Runnable
{
   private JTextField enterField; // enters information from user
   private JTextArea displayArea; // display information to user
   private ObjectOutputStream output; // output stream to server
   private ObjectInputStream input; // input stream from server
   private String message = ""; // message from server
   private String beforeString = "710,240";
   private String server; // host server for application
   private Socket client; // socket to communicate with server
   private String username; // player's name that will be seen by other players
   private ExecutorService clientExecutor; //Executes the client on a thread
   private ExecutorService amongUsExecutor; //Executes the game
   private ImageMover game; //Stores the current instance of the game
   private static String[] coordinate; //Holds the coordinates of the players
   private int clientID; //The clients ID
   int readID; //Used to handle first loop through process connection
   private Login myLogin; //Used to pull up login GUI



   // initialize chatServer and set up GUI
   public Client(String host)
   {
      super("Player");

      //Calls the login GUI
      myLogin = new Login();
      //Checking if the client is logged in
      while (!myLogin.getLoggedIn()) {
         try {
            Thread.sleep(1000); //Sleep to prevent GUI from crashing by looping too many times
         } catch (InterruptedException e) {
            throw new RuntimeException(e);
         }
      }

      //Setting the username
      username = myLogin.getUsername();

      readID = 0;

      server = host; // set server to which this client connects


      coordinate = new String[4];

      enterField = new JTextField(); // create enterField
      enterField.setEditable(false);
      enterField.addActionListener(
              new ActionListener()
              {
                 // send message to server
                 public void actionPerformed(ActionEvent event)
                 {
                    sendData(event.getActionCommand());
                    enterField.setText("");
                 }
              }
      );

      add(enterField, BorderLayout.SOUTH);

      displayArea = new JTextArea();
      displayArea.setEditable(false);
      add(new JScrollPane(displayArea), BorderLayout.CENTER);

      setSize(600, 600);
      setLocation(200,200);
      setVisible(true);
   }

   // connect to server and process messages from server
   public void runClient()
   {
      try
      {
         connectToServer();
         System.out.println("Works");
         getStreams();
         System.out.println("WorksTwo");

         processConnection();
      }
      catch (EOFException eofException)
      {
         displayMessage("Notice: Client terminated connection");
      }
      catch (IOException ioException)
      {
         ioException.printStackTrace();
      }
   }

   public void run() {
      while (true) {
         coordinateSender();
         sendPlayerStatusFromClient();
         grabReport();
      }
   }

   //Checks if a client has pressed the report button
   private void grabReport() {
      if (game.getReport()) {
         try {
            output.writeObject("REPORT"); //Identifier for game coordinate
            output.flush(); //Flushes output to server
            game.setReport(false); //Setting the report back to false, so it you can report again
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }
   }

   //Sends player status to client
   private void sendPlayerStatusFromClient() {
      try {
         String status = game.getPlayerStatus(); //Checks for player's status
         String statusCheck = "";
         if (status.length() > 3) {
            System.out.println(status);
            int x = status.indexOf(',');
            statusCheck = status.substring(x+1);
            statusCheck = statusCheck.substring(0,statusCheck.indexOf(','));
            //Checking client's status
            if (statusCheck.equals("FALSE")) {
               output.writeObject(status); //Identifier for game coordinate
               output.flush();
            }
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   //Sends the coordinates to the server
   private void coordinateSender() {
      try {
         coordinate[clientID - 1] = game.getTheCoordinateOfSquare(); //Grabbing new coordinate
         if (!beforeString.equals(coordinate[clientID - 1])) {
            output.writeObject("$" + clientID + "," + coordinate[clientID - 1]); //Identifier for game coordinate
            output.flush();
            beforeString = coordinate[clientID - 1]; //Used to check if the position has changed
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   //connect to server
   private void connectToServer() throws IOException
   {
      displayMessage("Notice: Attempting connection");

      // create Socket to make connection to server
      client = new Socket(InetAddress.getByName(server), 23660);
      //client = new Socket(server, 23660);

      displayMessage("Notice: Connected to: " + client.getInetAddress().getHostName());
   }

   // get I/O streams to send and receive data
   private void getStreams() throws IOException
   {
      // set up output stream for objects
      output = new ObjectOutputStream(client.getOutputStream());
      output.flush();

      // set up input stream for objects
      input = new ObjectInputStream(client.getInputStream());

      displayMessage("Notice: Got I/O streams");
   }

   // process connection with server
   private void processConnection() throws IOException
   {
      setTextFieldEditable(true);

      do // process messages sent from server
      {
         try // read message and display it
         {
            message = (String) input.readObject(); // read new message
            //First time GUI is read
            if (readID == 0) {
               clientID = Integer.valueOf(message.substring(11,12));
               amongUsExecutor = Executors.newFixedThreadPool(1);
               game = new ImageMover(clientID); //Passing ID
               amongUsExecutor.execute(game); //Executing the game
               clientExecutor = Executors.newFixedThreadPool(1);
               clientExecutor.execute(this); //Executing the client
               readID++;
            }
            //For coordinate of a player
            else if (message.startsWith("|")) {
               game.sendCoordinates(message); //Sends coordinates to game
            }
            //Assigning imposter
            else if (message.contains("IMPOSTER")) {
               game.sendImposter(message); //Sends who the imposter/deceiver is in the game
            }
            //Checking for player status
            else if (message.startsWith("@@@")) {
               game.setPlayerStatus(message); //Sends the player status to the game, marked with ID
            }
            //For messages
            else if(!message.startsWith(" Notice:") || message.startsWith(username + " Notice:")){
               displayMessage("\n" + message); // display message
            }
         }
         catch (ClassNotFoundException classNotFoundException)
         {
            displayMessage("Notice: Unknown object type received");
         }

      } while (!message.equals("SERVER>>> TERMINATE"));
   }

   // send message to server
   private void sendData(String message)
   {
      try // send message to server
      {
         message = username + ": " + message;
         output.writeObject("~" + message); //Identifier for user message
         output.flush();
      }
      catch (IOException ioException)
      {
         displayArea.append("Notice: Error writing object");
      }
   }

   // manipulates displayArea in the event-dispatch thread
   private void displayMessage(final String messageToDisplay)
   {
      SwingUtilities.invokeLater(
              new Runnable()
              {
                 public void run() // updates displayArea
                 {
                    displayArea.append("\n" + messageToDisplay);
                 }
              }
      );
   }

   // manipulates enterField in the event-dispatch thread
   private void setTextFieldEditable(final boolean editable)
   {
      SwingUtilities.invokeLater(
              new Runnable()
              {
                 public void run() // sets enterField's editable
                 {
                    enterField.setEditable(editable);
                 }
              }
      );
   }
}

