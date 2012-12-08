/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package playground;

import pongData.MatchStatus;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author Erni
 */
public class ManagePlaying extends Thread{

    private int portPlaying;
    private List<pongData.PlayerData> players = null;
    private LogPlayground log;
    private pongData.PlaygroundData playgroundData;
    private pongData.MatchStatus status;
    private Match match;

    public ManagePlaying (int portPlaying, List players, LogPlayground log, pongData.PlaygroundData playgroundData) {
        //super();
        this.portPlaying = portPlaying;
        this.players = players;
        this.log = log;
        this.playgroundData = playgroundData;
    }


    @Override
    public void run(){
        ServerSocket myServSocket = null;
        status = new MatchStatus();
        //match = new Match(log, playgroundData, status);
        try{
            try {
                 myServSocket = new ServerSocket(portPlaying);
            } catch (Exception e){
                log.writeMessage("La porta di Playing " + portPlaying + " è già occupata");
                return;
            }
           
            while (true){
                Socket clnSocket = null;
                HandlePlayPlayer hndlClient = null;
                try {
                    clnSocket = myServSocket.accept();
                    //System.out.println(clnSocket.getInetAddress().toString());
                    hndlClient = new HandlePlayPlayer(clnSocket, log, players, playgroundData, status, match);
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
