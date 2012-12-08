package cheatPlayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import pongData.*;

/**
 *
 * @author Erni
 */
public class Player {

    public int port = 7970;
    public InetAddress organizerAdress = null;
    public int portOrganizer = 3030;

    private LogPlayer log;
    private Socket playgroundSocket;
    private Socket playgroundSocketPlaying;
    private BufferedReader playgIn;
    private PrintStream playgOut;
    private BufferedReader playingIn;
    private PrintStream playingOut;

    private PlaygroundData playgroundD;
    private PlayingData playingD;

    public String name = "Erni";
    public String token = "";


    public Player (String name, InetAddress organizerAdress, int port) {
        this.port = port;
        this.organizerAdress = organizerAdress;
        this.name = name;
        log = new LogPlayer();

    }

    public void play(){
        // In realtà non gioca, contatta soltanto l'organizer per comunicare falsi risultati


      while (true){
          log.debugMessage("Inizio la scalata");
        try{
            while (true) {                          //è sempre attivo
                Socket organizerSocket = null;
                try {
                    organizerSocket = new Socket(organizerAdress, portOrganizer);   // contatta l'organizer
                }catch (Exception e){
                    log.srvMessage("Errore: non riesco a trovare un Organizer - " + e.getMessage());
                    return;
                }
                PrintStream out = new PrintStream(organizerSocket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(organizerSocket.getInputStream()));
                out.println("RANKING");                         //controlla chi è il primo classificato
                int quanti = Integer.parseInt(in.readLine());
                String looser = "";
                for (int i=1; i<quanti; i++){
                    String [] r = in.readLine().split(" ");
                    if (i==1){
                        looser = r[0];

                    }
                }
                if (!looser.equals(name)){
                    // se non sono io il primo, lo sfido! Sfidando il migliore ottengo più punti.
                    log.debugMessage("Campione in carica: " + looser + ". Ora lo sfido");
                    try {
                        Thread.sleep(5000);
                        // fa finta di giocare
                    } catch (Exception e) {

                    }
                    int rnd = new Random().nextInt(9);
                    log.debugMessage("Haha, ho vinto 10 a " + rnd + ".");
                    //comunica il risultato fasullo
                    out.println("RESULT 800 600 1.2 0.2 " + name + " " + looser + " 10 " + rnd);
                } else {
                    //se sono io il campione... basta così
                    log.debugMessage("Ora sono io il campione!");
                    try {
                        Thread.sleep(30000);
                        // fa finta di giocare
                    } catch (Exception e) {

                    }
                }

                
                try {
                    organizerSocket.close();
                } catch (Exception e) {

                }
            }



            // ora basterebbe controllare se siamo primi

        } catch (Exception e) {
            // non serve, ho bisogno del finally
            log.srvMessage("Player:   " + e.getMessage());
        } finally {
            try {
                playgroundSocketPlaying.close();
            } catch (Exception ex) {
                // non fa niente
            }
        }
      }
    }
    
}
