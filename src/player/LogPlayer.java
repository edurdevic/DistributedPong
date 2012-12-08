/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package player;

import java.net.InetAddress;
import java.util.Date;

/**
 *
 * @author Erni
 */
public class LogPlayer {
    int count = 0;
  synchronized public void srvOpen(InetAddress a, int port)  {
    System.out.println( new Date() + " Connecter to server: (" + a + " : " + port + ")");
    };
  synchronized public void srvClose(InetAddress a, int port) {
    System.out.println(new Date() + " Closed connection to server: (" + a + " : " + port + ")");
    };
  synchronized public void playgroundNotFound(InetAddress a, int port)  {
    System.out.println(new Date() + " Playground not available (" + a + " : " + port + ")");
    };
  synchronized public void clnException(int id, String msg) {
    System.out.println("(" + id +") " + "Exception caught: " + msg);
    };
  synchronized public void srvMessage(String msg) {
    System.out.println(msg);
    };
  synchronized public void debugMessage(String msg) {
    System.out.println("DEBUG:     " + msg);
  };

  public void init() {  };
  public void shutdown()  {  };

}
