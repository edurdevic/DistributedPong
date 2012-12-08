/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package playground;

/**
 *
 * @author Erni
 */
public class LogPlayground {
  /**
   * Writes a message to video output
   */
  synchronized public void writeMessageEnq(String msg) {
    System.out.println("Enqueuing: " + msg);
  }
  synchronized public void writeMessagePly(String msg) {
    System.out.println("Playing:   " + msg);
  }
  synchronized public void writeMessage(String msg) {
    System.out.println(msg);
  }
  synchronized public void debugMessage(String msg) {
    System.out.println("-----DEBUG:     " + msg);
  }
}
