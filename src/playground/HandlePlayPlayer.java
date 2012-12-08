/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package playground;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author Erni
 */
/**
 *
 * Gestisce un player che si è connesso alla porta Enqueuing
 */
public class HandlePlayPlayer extends Thread {

    Socket playerSocket = null;
    private List<pongData.PlayerData> players = null;
    private pongData.PlaygroundData playgroundData;
    LogPlayground log;
    InputStream in;
    OutputStream out;
    PrintStream buffOut;
    BufferedReader buffIn;
 //   private PongData.PlaygroundData playgroundData;
    private pongData.MatchStatus status;
    private Match match;
    private boolean firstRequest = true;
    private boolean iAmPlayer1 = false;
    private boolean iAmPlayer2 = false;
    private pongData.PlayerData thisPlayer = null;

    private static final int DEFAULT_ViR = 500;     //Velocità iniziale racchetta in Px/sec

    public HandlePlayPlayer(
            Socket s,
            LogPlayground log,
            List<pongData.PlayerData> players,
            pongData.PlaygroundData playgroundData,
            pongData.MatchStatus status,
            Match match) {
        this.log = log;
        playerSocket = s;
        this.players = players;
        this.playgroundData = playgroundData;
        this.status = status;
        this.match = match;
    }
    
    public HandlePlayPlayer(
            Socket s,
            LogPlayground log,
            List<pongData.PlayerData> players,
            pongData.PlaygroundData playgroundData,
            pongData.MatchStatus status) {
        this.log = log;
        playerSocket = s;
        this.players = players;
        this.playgroundData = playgroundData;
        this.status = status;
    }

    @Override
    public void run() {
        try {
            in = playerSocket.getInputStream();
            out = playerSocket.getOutputStream();
            buffIn = new BufferedReader(new InputStreamReader(in));
            buffOut = new PrintStream(out);
            boolean done = false;

            while (!done){
                try{
                    String corrente = buffIn.readLine();
                    
                    done = processResponse(corrente);
                    //String p = "";

                    Thread.sleep(25);                       //Devo aspettare tra 2 risposte consecutive
                }
                catch(Exception e){
                    synchronized (status){
                        if (iAmPlayer1){
                            log.writeMessagePly("Errore client 1: " + e.getMessage());
                            if (status.isMatchStarted()){
                                // se la partita ATTUALE è già iniziata la devo fermare
                                try {
                                  if ( thisPlayer.getMyMatch().equals(status.getCurrentMatch())){
                                      //se il giocatore fa parte della partita attuale, non di una vecchia
                                    //status.setMatchEnd(true);    //se 1 dei giocatori muore, devo chiudere la partita
                                    thisPlayer.getMyMatch().terminateMatch = true;
                                  }
                                } catch (Exception exc) {
                                    log.debugMessage("Accidenti 1! status.getcurrentMatch è null! " + exc.getMessage());
                                }
                            } else {
                                // altrimenti aspetto un altro giocatore
                                //status.setPlayer1InMatch(false);
                                log.debugMessage("Player 1 uscito dal gioco! - status refresh");
                            }
                        }
                        if (iAmPlayer2){
                            log.writeMessagePly("Errore client 2: " + e.getMessage());
                            if (status.isMatchStarted()){
                                // se la partita ATTUALE è già iniziata la devo fermare
                                try {
                                  if ( thisPlayer.getMyMatch().equals(status.getCurrentMatch())){
                                      //se il giocatore fa parte della partita attuale, non di una vecchia
                                    //status.setMatchEnd(true);    //se 1 dei giocatori muore, devo chiudere la partita
                                    thisPlayer.getMyMatch().terminateMatch = true;
                                  }
                                } catch (Exception exc) {
                                    log.debugMessage("Accidenti 2! status.getcurrentMatch è null! " + exc.getMessage());
                                }
                            } else {
                                // altrimenti aspetto un altro giocatore
                                //status.setPlayer2InMatch(false);
                                log.debugMessage("Player 2 uscito dal gioco! - status refresh");
                            }
                        }
                        done = true;

                        try{
                            playerSocket.close();
                        } catch (Exception E){

                        }
                    }
                    
                }
            }
        } catch (IOException ex) {
            log.writeMessagePly("Non posso gestire il client, errore Socket");
            return;
        } finally {
            try{
                in.close();
                out.close();
                playerSocket.close();
            } catch (Exception e) {
                //ok, erano già chiusi
            }
        }
    }

    /**
     *
     * @param request
     * @return Done with tihis client?
     */
    private boolean processResponse(String req) throws IOException{
        if (firstRequest){
            synchronized (status){
                    //se è la prima richiesta:
                firstRequest = false;
                if (status.isMatchStarted()){
                    buffOut.println("IGNORING_YOU");
                    return true;
                }
                if (!req.startsWith("READY")){
                    buffOut.println("INVALID_REQUEST");
                    return true;
                }
                //altrimenti devo accettare aualche giocatore:
                String request[] = req.split(" ") ;
                if (request.length < 3) {
                    //se ci sono pochi argomenti
                    buffOut.println("UNKNOWN_REQUEST");
                    return true;                        // finito con questo client incomprensibile
                } else {
                    //se ci sono i sufficenti parametri

                    if (! request[1].matches("\\w*")){  // \w sta per lettera o num, * sta per [0-infinite] volte
                        // se li nome non è espressione regolare
                        buffOut.println("IGNORING_YOU");
                        return true;
                    } else {
                        // Creo un nuovo Player, e controllo se esiste nella coda uno uguale

                        pongData.PlayerData plD = new pongData.PlayerData(request[1], request[2], 10);
                        int currentIndex = players.indexOf(plD);

                        //log.debugMessage("RICHIESTA di gioco da " + request[1] + " in posizione " + currentIndex);
   
                        if (currentIndex==0) {
                            //Se è il primo in lista:
                            addPlayerToMatch(currentIndex, plD.getName());
                            return false;
                        } else if ((currentIndex == 1) && !status.isPlayer1InMatch() && !status.isPlayer2InMatch()) {
                            //Se è il secondo in lista e il playground è libero
                            addPlayerToMatch(currentIndex, plD.getName());
                            return false;
                        } else {
                            buffOut.println("IGNORING_YOU");
                            return true;
                        }
                    }
                }
            }
        } else {
            //se non è la prima richiesta:
            if (!(iAmPlayer1 || iAmPlayer2)){
                //se non sono ne pl1 ne pl2 non dovrei essere qui!
                buffOut.println("IGNORING_YOU");
                return true;
            }
            if (! status.isMatchStarted()){
                // se sono uno dei 2 player in gioco, manca ancora l'altro
                buffOut.println("WAITING_PARTNER");
                return false;
            }
            if (req.startsWith("READY")){
            // se la partita è iniziata, a ready devo rispondere OK!
                buffOut.println("OK");
                return false;               //non ho finito con questo client
            } else if(req.equals("LEFT")){
                if (iAmPlayer1){
                    status.setvXR1(-DEFAULT_ViR);
                    buffOut.println(
                            "OK 1 " +
                            status.getxR1() + " " +
                            status.getxR2() + " " +
                            status.getxB() + " " +
                            status.getyB() + " " +
                            status.getPunteggio1() + " " +
                            status.getPunteggio2() );
                    return false;
                } else if (iAmPlayer2){
                    status.setvXR2(-DEFAULT_ViR);
                    buffOut.println(
                            "OK 2 " +
                            status.getxR1() + " " +
                            status.getxR2() + " " +
                            status.getxB() + " " +
                            status.getyB() + " " +
                            status.getPunteggio1() + " " +
                            status.getPunteggio2() );
                    return false;
                }

            } else if (req.startsWith("RIGHT")){
                if (iAmPlayer1){
                    status.setvXR1(DEFAULT_ViR);
                    buffOut.println(
                            "OK 1 " +
                            status.getxR1() + " " +
                            status.getxR2() + " " +
                            status.getxB() + " " +
                            status.getyB() + " " +
                            status.getPunteggio1() + " " +
                            status.getPunteggio2() );
                    return false;
                } else if (iAmPlayer2){
                    status.setvXR2(DEFAULT_ViR);
                    buffOut.println(
                            "OK 2 " +
                            status.getxR1() + " " +
                            status.getxR2() + " " +
                            status.getxB() + " " +
                            status.getyB() + " " +
                            status.getPunteggio1() + " " +
                            status.getPunteggio2() );
                    return false;
                }
            } else if (req.startsWith("FORFAIT")){
                forfait();
                buffOut.println("OK FORFAIT accettato.");
                log.writeMessagePly("Player ritirato");
                return true;
            } else {
                buffOut.println("UNKNOWN_REQUEST");
                log.writeMessagePly("UNKNOWN_REQUEST, chiudo la connessione");
                //status.setMatchEnd(true);
                thisPlayer.getMyMatch().terminateMatch = true;
                return true;
            }
            
        }
        return false;
    }

    private void addPlayerToMatch(int currentIndex, String name){
        synchronized (status){
            if (!status.isPlayer1InMatch()){
                //log.debugMessage(status.toString());
                // sono io il player1!
                status.setPlayer1InMatch(true);
                try{
                    status.setPlayer1(players.get(currentIndex));
                    status.getPlayer1().setSocketPlayer(playerSocket);
                    thisPlayer = status.getPlayer1();

                    players.remove(currentIndex);
                    buffOut.println(
                            "OK 1 " +
                            status.getxR1() + " " +
                            status.getxR2() + " " +
                            status.getxB() + " " +
                            status.getyB() + " " +
                            status.getPunteggio1() + " " +
                            status.getPunteggio2() );

                    log.writeMessagePly("Player 1 in gioco: " + name);
                    //log.debugMessage(status.toString());
                    
                    iAmPlayer1 = true;
                } catch (Exception e){
                    log.writeMessagePly("Non posso aggiungere Player1 al match! " + e.getMessage());
                    //status.setPlayer1InMatch(false);
                   // throw new Exception ("Player 2 errore");
                }
            } else if (!status.isPlayer2InMatch()){
                // sono io il player2!
                //log.debugMessage(status.toString());
                status.setPlayer2InMatch(true);
                try{
                    status.setPlayer2(players.get(currentIndex));
                    status.getPlayer2().setSocketPlayer(playerSocket);
                    thisPlayer = status.getPlayer2();

                    players.remove(currentIndex);
                    buffOut.println(
                            "OK 2 " +
                            status.getxR1() + " " +
                            status.getxR2() + " " +
                            status.getxB() + " " +
                            status.getyB() + " " +
                            status.getPunteggio1() + " " +
                            status.getPunteggio2() );
                    log.writeMessagePly("Player 2 in gioco: " + name);
                    //log.debugMessage(status.toString());
                    iAmPlayer2 = true;
                } catch (Exception e){
                    log.writeMessagePly("Non posso aggiungere Player2 al match! " + e.getMessage());
                    //status.setPlayer2InMatch(false);
                   // throw new Exception ("Player 2 errore");
                }

            }

            //Se ci sono tutti inizia la partita!
            if (status.isPlayer1InMatch() && status.isPlayer2InMatch()){
                //status.setMatchEnd(false);

                    status.setMatchStarted(true);
                    match = new Match(log, playgroundData, status);
                    status.setCurrentMatch(match);
                    status.getPlayer1().setMyMatch(match);
                    status.getPlayer2().setMyMatch(match);
                    match.start();

            }
        }
    }

    private void forfait(){
        if(iAmPlayer1){
            status.setPunteggio2(10);
            //status.setMatchEnd(true);
            match.terminateMatch = true;
        } else if(iAmPlayer2){
            status.setPunteggio1(10);
            //status.setMatchEnd(true);
            match.terminateMatch = true;
        }
    }

}
