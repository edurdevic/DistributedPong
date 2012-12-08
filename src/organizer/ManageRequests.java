/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package organizer;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import pongData.PlaygroundData;

/**
 *
 * @author Erni
 * Thread che accetta le connessioni in entrata su un ServerSocket
 */
public class ManageRequests extends Thread{
    Set<pongData.PlaygroundData> playgrounds;
    PlayerRankingSet players;
    LogOrganizer log;

    public ManageRequests(Set<PlaygroundData> playgrounds, PlayerRankingSet players, LogOrganizer log) {
        super();
        this.playgrounds = playgrounds;
        this.players = players;
        this.log = log;
    }
    
    @Override
    public void run(){
        ServerSocket myServSocket = null;
        try{
            myServSocket = new ServerSocket(Organizer.PORT_ORGANIZER);
            while (true){
                Socket clnSocket = null;
                HandleClients hndlClient = null;
                try {
                    clnSocket = myServSocket.accept();
                    hndlClient = new HandleClients(clnSocket, playgrounds, players, log);
                    hndlClient.start();             // Handle Client si occuperà di dialogare con il client
                } catch (Exception e){
                    log.ErrorMessage("Errore nella gestione del socket client ENQUEUING");
                }
            }
        } catch(Exception e){
            log.ErrorMessage("Errore nella creazione del server socket");
            return;
        } finally {
                try{
                    myServSocket.close();
                } catch(Exception e){
                    log.ErrorMessage("Impossibile chiudere il socket");
                }
        }
    }

}
