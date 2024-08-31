// Referenced Modified Fig. 27.5: Multi-threaded Chat Server.java

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends JFrame {
   private JTextArea displayArea; //Display information to user
   private ExecutorService executor; //Will run different players
   private ServerSocket server; //The server socket to accept connections
   private SockServer[] sockServer; //Array of objects to be threaded
   private int counter = 1; //Counter of number of connections
   private int nClientsActive = 0; //Number of client currently active

   private int deceiver; //The random number of the client that will be chosen as the deceiver


   //Setting up the GUI for server and finding a random number for imposter
   public Server() {
      super("Server");

      sockServer = new SockServer[100]; // allocate array for up to 100 server threads, Most likely will only have game working for 4 people
      executor = Executors.newFixedThreadPool(100);

      displayArea = new JTextArea();
      add(new JScrollPane(displayArea), BorderLayout.CENTER);

      setSize(500, 500);
      setLocation(600,600);
      setVisible(true);

      //Creating a random number 1 - 4 to assign as imposter/deceiver
      Random rand = new Random();
      deceiver = rand.nextInt(4) + 1;

   }

   // set up and run server
   public void runServer() {
      try
      {
         server = new ServerSocket(23660, 100); //Creating ServerSocket

         while (true) {
            try {
               sockServer[counter] = new SockServer(counter); //Creating a new runnable object to serve the next client to call in
               sockServer[counter].waitForConnection(); //Making that new object wait for a connection on that new server object
               nClientsActive++; //Incrementing the client active
               executor.execute(sockServer[counter]); //Executing server object into its own new thread

            }
            catch (EOFException eofException) {
               displayMessage("\nSERVER TERMINATED CONNECTION");
            }
            finally {
               ++counter;
            }
         }
      }
      catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   //Displays the messages to the GUI
   private void displayMessage(final String messageToDisplay) {
      SwingUtilities.invokeLater(
              new Runnable() {
                 public void run() // updates displayArea
                 {
                    displayArea.append(messageToDisplay); // append message
                 }
              }
      );
   }


   //This new Inner Class implements Runnable and objects instantiated from this
   //class will become server threads each serving a different client
   private class SockServer implements Runnable {
      private ObjectOutputStream output; //Output stream to client
      private ObjectInputStream input; //Input stream from client
      private Socket connection; //Connection to client
      private int myConID; //Connection ID
      private boolean alive = false; //If the socket is still established
      private static String coordinatesMaster[] = {"680,240","740,240","680,280","740,280"}; //Stores coordinate values of the players
      private String message; //Input message or data from client

      public SockServer(int counterIn) {
         myConID = counterIn;
      }

      public void run() {
         try {
            alive = true;
            try {
               getStreams(); //Getting input & output streams
               processConnection(); //Processing the connecting/ reading data
               nClientsActive--;
            }
            catch (EOFException eofException) {
               displayMessage("\nServer" + myConID + " terminated connection");
            }
         }
         catch (IOException ioException) {
            ioException.printStackTrace();
         }
      }

      //Reads coordinate values of different players when they are moved
      private void coordinateReader() {
         //Symbol for coordinate
         if (message.startsWith("$")) {
            for (int i = 1; i <= counter; i++) {
               if (sockServer[i].alive == true) {
                  int ID = Integer.valueOf(message.substring(1,2));
                  coordinatesMaster[ID - 1] = message.substring(3);
                  String temp = "";
                  for (int j = 0; j < 4; j++) {
                     temp += "|" + coordinatesMaster[j];
                  }
                  sockServer[i].sendDataToAllClients(temp);
               }
            }
         }
      }

      //Sending status to all clients
      private void sendingStatusToAllClients() {
         //Symbol for status
         if (message.startsWith("@@@")) {
            for (int i = 1; i <= counter; i++) {
               if (sockServer[i].alive == true) {
                  sockServer[i].sendDataToAllClients(message);
               }
            }
         }
      }

      //Checks if the report button has been pressed
      private void readingReport() {
         //Symbol for report
         if (message.startsWith("REPORT")) {
            for (int i = 1; i <= counter; i++) {
               if (sockServer[i].alive == true) {
                  sockServer[i].sendDataToAllClients("MEETING --> SOMETHING REALLY BAD MAY HAVE OCCURRED!!!!!!");
               }
            }
         }
      }

      //Waiting for connection to arrive, then display connection info
      private void waitForConnection() throws IOException {
         displayMessage("Waiting for connection" + myConID + "\n");
         connection = server.accept(); //Allowing server to accept connection
         displayMessage("Connection " + myConID + " received from: " + connection.getInetAddress().getHostName());
      }

      //Gets the streams for the connection with client
      private void getStreams() throws IOException {
         output = new ObjectOutputStream(connection.getOutputStream()); //Setting up output stream for objects
         output.flush(); //Flushing output buffer to send header information

         input = new ObjectInputStream(connection.getInputStream()); //Setting up input stream for objects

         displayMessage("\nGot I/O streams\n");
      }

      //Processing connection with client
      private void processConnection() throws IOException {
         message = "Connection " + myConID + " successful";
         sendDataToAllClients(message); // send connection successful message
         sendDataToAllClients(String.valueOf("IMPOSTER" + deceiver)); //Sending to the clients who the imposter is

         do
         {
            try
            {
               message = (String) input.readObject(); // read new message
               //If it is a message enter
               if (!message.startsWith("$") && !message.startsWith("@@@") && !message.startsWith("%%%") && !message.startsWith("REPORT")) {
                  for (int i = 1; i <= counter; i++) {
                     if (sockServer[i].alive == true) {
                        sockServer[i].sendDataToAllClients(message);
                     }
                  }
               }
               //If not message perform these actions
               else {
                  coordinateReader();
                  sendingStatusToAllClients();
                  readingReport();
               }
            }
            catch (ClassNotFoundException classNotFoundException) {
               displayMessage("\nUnknown object type received");
            }

         } while (!message.equals("CLIENT>>> TERMINATE"));
      }
      private void sendDataToAllClients(String message) {
         try
         {
            output.writeObject(message); //Writing object to client
            output.flush(); //Flushing to client
         }
         catch (IOException ioException) {
            displayArea.append("\nError writing object");
         }
      }
   }
}

