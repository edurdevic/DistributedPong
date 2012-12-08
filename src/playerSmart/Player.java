package playerSmart;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;
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

    private PlaygroundDataForPlayers playgroundD;
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
        // Si deve trovare il Playground su cui si vuole giocare
      while (true){
          log.debugMessage("Inizio ciclo");
        try{
            try {
                playgroundD = FindPlayground();
                //playgroundD = FindPlaygroundManually();
                log.debugMessage("Trovato playground");
            }catch (Exception e){
                log.srvMessage("Errore: non riesco a trovare un Playground - " + e.getMessage());
                return;
            }
            // ora bisogna iscriversi al Playground e giocare
            try{
                playgroundSocket = getPlaygroundEnqueing();
                playgIn = new BufferedReader(new InputStreamReader(playgroundSocket.getInputStream()));
                playgOut = new PrintStream(playgroundSocket.getOutputStream());
                log.srvOpen(playgroundD.getIp(), playgroundD.getPortEnqueuing());
                log.debugMessage("Connesso al playground");
            } catch (Exception e){
                log.playgroundNotFound(playgroundD.getIp(), playgroundD.getPortEnqueuing());
                return;
            }
            // Prenotati:
            try {
                subscribe();
                log.srvMessage("Subscribed to playground");
            } catch (Exception e){
                log.srvMessage("Impossibile iscriversi al Playground - " + e.getMessage());
                return;
            }

            // Connettiti alla porta di Playing:
            try {
                connectToPlaygroundPlaying();
                log.srvOpen(playgroundD.getIp(), playgroundD.getPortPlaying());
            } catch (Exception e){
                log.srvMessage("Impossibile connettersi al Playground - port Playing");
                return;
            }

            // Prova a giocare
            try {
                while (!(readyToPlay())){  // Finchè la partita non inizia
                    // Aspetta
                    Thread.sleep(3000);
                    // Refresh
                    refreshSubscription();
                    connectToPlaygroundPlaying();
                    log.srvMessage("Sottoscrizione rinfrescata.");
                }
                log.srvMessage("Sottoscrizione completata.");
            } catch (Exception e){
                // errore di connessione o risposta invalida
                log.srvMessage("Impossibile connettersi al Playground-" + e.getMessage());
                return;
            } finally {
                try {
                    playgroundSocket.close();
                    log.debugMessage("chiuso il socket Enq");
                } catch (Exception e){
                    log.debugMessage("non riesco a chiudere il playground socket Enq");
                }
            }

            //>> Ora devo giocare
            try {
                while (waitingPartner()){
                    Thread.sleep(500);
                    log.srvMessage("Aspetto un partner...");
                    // aspetta un po
                }
                log.srvMessage("Inizia la partita");
                int oldxB = playingD.getxB();
                int oldyB = playingD.getyB();
                while (true){
                    int expectedXPos = 0;           //dove credo finirà la pallina
                    int deltaX = playingD.getxB() - oldxB;
                    int deltaY = playingD.getyB() - oldyB;
                    if (deltaY > 0){        // la pallina sta andando giu
                        if (playingD.getPalyerNum() == 1){      // sono di sopra, devo  stare al centro
                            expectedXPos = playgroundD.getW()/2;
                        } else {                                // sono di sotto, devo prendere la pallina
                            expectedXPos = (int)(playingD.getxB() + deltaX*((playgroundD.getH() - playingD.getyB())/Math.abs(deltaY)));
                        }
                    } else if (deltaY < 0){ // la pallina sta andando su
                        if (playingD.getPalyerNum() == 2){      // sono di sotto, devo  stare al centro
                            expectedXPos = playgroundD.getW()/2;
                        } else {                                // sono di sopra, devo prendere la pallina
                            expectedXPos = (int)(playingD.getxB() + deltaX*(playingD.getyB()/Math.abs(deltaY)));
                        }
                    }
                    if (expectedXPos < 0) {
                        // significa che andrà a sbattere a sx, devo fare il valore assoluto
                        expectedXPos = Math.abs(expectedXPos);
                    } else if (expectedXPos > playgroundD.getW()){
                        // significa che andrà a sbattere a dx,
                        expectedXPos = 2*playgroundD.getW() - expectedXPos;
                    }
                    oldxB = playingD.getxB();
                    oldyB = playingD.getyB();

                    //vai verso la posizione di contatto prevista
                    if (expectedXPos > playingD.getMyPosition()){
                        goRight();
                    } else {
                        goLeft();
                    }
                    
                }
            } catch (Exception e){
                //>>errore di connessione o risposta invalida
                log.srvMessage("Partita terminata......");
            }

        } catch (Exception e) {
            // non serve, ho bisogno del finally
            log.srvMessage("Player:   " + e.getMessage());
        } finally {
            try {
//                playgIn.close();
//                playgOut.close();
//                playingIn.close();
//                playingOut.close();
                playgroundSocketPlaying.close();
            } catch (Exception ex) {
                // non fa niente
            }
        }
      }
    }
    public PlaygroundDataForPlayers FindPlayground() throws Exception{
        // Ritorna i dati del Playground scelto per giocare
        Socket organizerSocket = new Socket(organizerAdress, portOrganizer);
        BufferedReader orgIn = new BufferedReader(new InputStreamReader(organizerSocket.getInputStream()));
        PrintStream orgOut = new PrintStream(organizerSocket.getOutputStream());
        //Chiedo all'organizer la lista dei playground
        orgOut.println("LIST");         
        int rowN = Integer.parseInt(orgIn.readLine());  
        if (rowN == 1) { throw new Exception("Non ci sono Playground disponibili."); }
        Set playgrounds = new TreeSet<PlaygroundDataForPlayers>();
        for (int i=1; i<rowN; i++){                     //Scorro tutte le righe della risposta
            String response = orgIn.readLine();
            String [] dataResponse = response.split(" ");
            if (dataResponse.length >= 7){
                try {
                    PlaygroundDataForPlayers p = new PlaygroundDataForPlayers(
                        InetAddress.getByName(dataResponse[0]),
                        Integer.parseInt(dataResponse[1]),
                        Integer.parseInt(dataResponse[2]),
                        Integer.parseInt(dataResponse[3]),
                        Integer.parseInt(dataResponse[4]),
                        Double.parseDouble(dataResponse[5]),
                        Double.parseDouble(dataResponse[6])
                        );
                    p.setQueueLength(Integer.parseInt(dataResponse[8]));
                    playgrounds.add(p);     //aggiungo la risposta alla lista ordinata per giocatori in coda
                } catch (Exception e){
                    log.srvMessage("Risposta invalida al comando LIST - " + e.getMessage());
                }
            } else {
                log.srvMessage("Risposta invalida al comando LIST");
            }
        }
        Iterator<PlaygroundDataForPlayers> it = playgrounds.iterator();
        if (it.hasNext()) {
            return it.next();       //Prendo il primo in lista = quello con meno coda
        } else { throw new Exception("L'Iteratore non trova elementi nella lista di Playground."); }
    }

    public PlaygroundDataForPlayers FindPlaygroundManually() throws Exception{
        return new PlaygroundDataForPlayers(InetAddress.getLocalHost(), 7960, 7961, 800, 600, 1.1, 0.2);
    }

    public void subscribe() throws Exception{
        playgOut.println("SUBSCRIBE " + name);
        log.debugMessage("SUBSCRIBEd");
        String response = playgIn.readLine();
        log.debugMessage("Response: " + response);
        // OK w h k kR nome token last-contact-timestamo ---> Salva i dati del Playground w,h,k,kR,...
        if (response.startsWith("OK ")){
            String [] dataResponse = response.split(" ");
            if (dataResponse.length < 8){
                throw new Exception("UNKNOWN_RESPONSE");
            } else {
                long prova = Long.parseLong(dataResponse[7]);
                playgroundD.setW(Integer.parseInt(dataResponse[1]));
                playgroundD.setH(Integer.parseInt(dataResponse[2]));
                playgroundD.setK(Double.parseDouble(dataResponse[3]));
                playgroundD.setkR(Double.parseDouble(dataResponse[4]));
                token = dataResponse[6];
            }
        }
        // UNKNOWN_REQUEST ?? Ex
        else if (response.startsWith("UNKNOWN_REQUEST")){
            throw new Exception("UNKNOWN_REQUEST");
        }
        // INVALID_NAME ?? Ex
        else if (response.startsWith("INVALID_NAME")){
            throw new Exception("INVALID_NAME");
        }
        // DUPLICATED_NAME ?? Ex
        else if (response.startsWith("DUPLICATED_NAME")){
            throw new Exception("DUPLICATED_NAME");
        }
        // QUEUE_FULL ?? Ex
        else if (response.startsWith("QUEUE_FULL")){
            throw new Exception("QUEUE_FULL");
        }

    }

    public void refreshSubscription() throws Exception{
        playgOut.println("REFRESH " + name + " " + token);
        String resp = playgIn.readLine();
        if(!resp.startsWith("OK")) {
            throw new Exception("Refresh al playground non riuscito");
        }
    }

    public Socket getPlaygroundEnqueing() throws Exception{
        return new Socket(playgroundD.getIp(), playgroundD.getPortEnqueuing());
    }

    public Socket getPlaygroundPlaying() throws Exception{
        return new Socket(playgroundD.getIp(), playgroundD.getPortPlaying());
    }

    public void connectToPlaygroundPlaying() throws Exception{
        playgroundSocketPlaying = getPlaygroundPlaying();
        playingIn = new BufferedReader(new InputStreamReader(playgroundSocketPlaying.getInputStream()));
        playingOut = new PrintStream(playgroundSocketPlaying.getOutputStream());
    }

    public boolean readyToPlay() throws Exception{
        playingOut.println("READY " + name + " " + token);
        String response = playingIn.readLine();
        if (response == null) {
            return false;
        }

        if (response.startsWith("OK")){
            String [] dataResponse = response.split(" ");
            if (dataResponse.length == 1) {
                //Se risponde "OK" significa che glie lo ho gia chiesto...
                return true;
            }
            else if (dataResponse.length < 8){
                //Se è tra 1 e 8 non so che ha detto
                throw new Exception("UNKNOWN_RESPONSE");
            } else {
                //Se è la prima risposta del Playground setta i parametri di playing
                try {
                    playingD = new pongData.PlayingData(
                        Integer.parseInt(dataResponse[1]),
                        Integer.parseInt(dataResponse[2]),
                        Integer.parseInt(dataResponse[3]),
                        Integer.parseInt(dataResponse[4]),
                        Integer.parseInt(dataResponse[5]),
                        Integer.parseInt(dataResponse[6]),
                        Integer.parseInt(dataResponse[7]));
                    log.debugMessage("posso istanziare PlayingData ");
                } catch (Exception e) {
                    log.debugMessage("Non posso istanziare PlayingData " + e.getMessage());
                }
            }
            return true;
        } else if (response.equalsIgnoreCase("IGNORING_YOU")){
            return false;
        } else {
            throw new Exception("Error: unknown response");
        }
    }


    public boolean waitingPartner() throws Exception{
        playingOut.println("LEFT");
        String response = playingIn.readLine();
        if (response.equalsIgnoreCase("WAITING_PARTNER")){
            return true;
        } else if (response.startsWith("OK ")){
            return false;
        } else {
            throw new Exception("Error: unknown response");
        }
    }

    public void goLeft() throws Exception {
        //>> Vai a sin e aggiorna i parametri in risposta
        playingOut.println("LEFT");
        String response = playingIn.readLine();
        if (response.startsWith("OK ")){
            String [] dataResponse = response.split(" ");
            if (dataResponse.length < 8){
                throw new Exception("UNKNOWN_RESPONSE");
            } else {
                playingD.setxR1(Integer.parseInt(dataResponse[2]));
                playingD.setxR2(Integer.parseInt(dataResponse[3]));
                playingD.setxB(Integer.parseInt(dataResponse[4]));
                playingD.setyB(Integer.parseInt(dataResponse[5]));
                playingD.setP1(Integer.parseInt(dataResponse[6]));
                playingD.setP2(Integer.parseInt(dataResponse[7]));
            }
        }
        // mi sta ignorando
        else {
            throw new Exception("IGNORING_ME");
        }
    }
    public void goRight() throws Exception {
        //>> Vai a dx e aggiorna i parametri in risposta
        playingOut.println("RIGHT");
        String response = playingIn.readLine();
        if (response.startsWith("OK ")){
            String [] dataResponse = response.split(" ");
            if (dataResponse.length < 8){
                throw new Exception("UNKNOWN_RESPONSE");
            } else {
                playingD.setxR1(Integer.parseInt(dataResponse[2]));
                playingD.setxR2(Integer.parseInt(dataResponse[3]));
                playingD.setxB(Integer.parseInt(dataResponse[4]));
                playingD.setyB(Integer.parseInt(dataResponse[5]));
                playingD.setP1(Integer.parseInt(dataResponse[6]));
                playingD.setP2(Integer.parseInt(dataResponse[7]));
            }
        }
        // mi sta ignorando
        else {
            throw new Exception("IGNORING_ME");
        }
    }
}
