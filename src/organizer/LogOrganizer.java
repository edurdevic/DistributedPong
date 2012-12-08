
package organizer;

/**
 *
 * @author Erni
 */
public class LogOrganizer {
    synchronized public void writeMessage(String msg){
        System.out.println(msg);
    }
    synchronized public void CleanerMessage(String msg){
        System.out.println("Cleaner:     " + msg);
    }
    synchronized public void RequestMessage(String msg){
        System.out.println("Request:     " + msg);
    }
    synchronized public void ErrorMessage(String msg){
        System.out.println("ERROR:      " + msg);
    }
    synchronized public void ClientErrMessage(String msg){
        System.out.println("Client err: " + msg);
    }

}
