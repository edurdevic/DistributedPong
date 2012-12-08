/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package playground;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author Erni
 */
public class ManageEnqueuing extends Thread{

    private int portEnqueuing;
    private List<pongData.PlayerData> players = null;
    private LogPlayground log;
    private pongData.PlaygroundData playgroundData;

    public ManageEnqueuing(int portEnqueuing, List<pongData.PlayerData> players, LogPlayground log, pongData.PlaygroundData playgroundData){
        this.portEnqueuing = portEnqueuing;
        this.players = players;
        this.log = log;
        this.playgroundData = playgroundData;
    }

    @Override
    public void run(){
        ServerSocket myServSocket = null;
        try{
            try {
                myServSocket = new ServerSocket(portEnqueuing);
            } catch (Exception e){
                log.writeMessage("La porta di Emqueuing " + portEnqueuing + " è già occupata");
                return;
            }
            while (true){
                Socket clnSocket = null;
                HandleEnqPlayer hndlClient = null;
                try {
                    clnSocket = myServSocket.accept();
                    //System.out.println(clnSocket.getInetAddress().toString());
                    hndlClient = new HandleEnqPlayer(clnSocket, log, players, playgroundData);
                    hndlClient.start();
                } catch (Exception e){
                    log.writeMessageEnq("Errore nella gestione del socket client ENQUEUING");
                }
            }
        } catch(Exception e){
            log.writeMessageEnq("Errore nella creazione del server socket");
            return;
        } finally {
                try{
                    myServSocket.close();
                } catch(Exception e){
                    log.writeMessageEnq("Impossibile chiudere il socket");
                }
        }
    }
    
}
