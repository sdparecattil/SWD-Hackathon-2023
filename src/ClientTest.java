// Fig. 27.8: ClientTest.java
// Class that tests the Client.

import javax.swing.*;

public class ClientTest {
   public static void main(String[] args) {
      Client application; // declare client application
      // if no command line args
      if (args.length == 0)
         //application = new Client("128.255.17.132"); // DO NOT DELETE USED FOR IP SERVER CLIENT RELATION
         application = new Client("localhost");// connect to localhost
      else
         application = new Client(args[0]); // Accept command Line Args

      application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Sets to terminate on window close
      application.runClient(); // run client application
   }
}