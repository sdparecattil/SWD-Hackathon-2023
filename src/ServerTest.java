// Fig. 28.4: ServerTest.java
// Test the Server application.

import javax.swing.*;

//Runs Server
public class ServerTest
{
   public static void main(String[] args)
   {
      Server application = new Server(); // create server
      application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Terminates on window Closed
      application.runServer(); // run server application
   }
}