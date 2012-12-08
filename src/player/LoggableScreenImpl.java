package player;


import java.net.*;
import java.util.Date;

/** Implementazione dell'interfaccia su standard output.
  * I metodi sono synchronized per permetterne l'uso da parte di un server multi-threaded.
  */
public class LoggableScreenImpl implements Loggable {
  int count = 0;
  synchronized public int clnOpen(InetAddress a, int b, Date c)  {
    count++;
    System.out.println("(" + count + ") " + c + " Open from: (" + a + ", " + b + ")");
    return count;
    };
  synchronized public void clnClose(int id, Date d) {
    System.out.println("(" + id +") " + d + " Closed");
    };
  synchronized public void srvStart(String m, int b, Date c)  {
    System.out.println(m + "STARTED at " + c + " (port " + b + ")" );
    };
  synchronized public void clnException(int id, String msg) {
    System.out.println("(" + id +") " + "Exception caught: " + msg);
    };
  synchronized public void srvMessage(String msg) {
    System.out.println(msg);
    };
    
  public void init() {  };
  public void shutdown()  {  };

  }
