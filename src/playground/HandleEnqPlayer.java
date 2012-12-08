/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package playground;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Erni
 */
/**
 *
 * Gestisce un player che si è connesso alla porta Enqueuing
 */
public class HandleEnqPlayer extends Thread {

    Socket playerSocket = null;
    private List<pongData.PlayerData> players = null;
    LogPlayground log;
    InputStream in;
    OutputStream out;
    PrintStream buffOut;
    BufferedReader buffIn;
    private pongData.PlaygroundData playgroundData;

    public static int MAX_Q_LEN = 20;

    public HandleEnqPlayer(Socket s, LogPlayground log, List<pongData.PlayerData> players, pongData.PlaygroundData playgroundData) {
        this.log = log;
        playerSocket = s;
        this.players = players;
        this.playgroundData = playgroundData;
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
                    done = doneWithThisClient(corrente);
                }
                catch(Exception E){
                    // log.writeMessageEnq("Errore nel ciclo principale: " + E.getMessage());
                    done = true;
                }
            }
        } catch (IOException ex) {
            log.writeMessageEnq("Non posso gestire il client, errore Socket");
            return;
        } finally {
            try{
                playerSocket.close();
                in.close();
                out.close();
            } catch (Exception e) {
                //ok, erano già chiusi
            }
        }
    }

    //ritorna true se posso chiudere la connessione al client.
    private boolean doneWithThisClient(String req) throws IOException{  //
        //analizzo la richiesta
      synchronized (players){                           //Questa funzione deve prendere il Lock su Players
        if (req.equals("LIST")){
            // torno al client la lista dei players in coda
            buffOut.println(players.size()+1);
            for (int i=0; i<players.size(); i++){
                buffOut.println(
                        players.get(i).getName() + " " +
                        players.get(i).getLastContactTime());
            }
        } else if(req.startsWith("SUBSCRIBE")){
            // il player vuole iscriversi al playground
            String request[] = req.split(" ") ;
            if (request.length < 2) { buffOut.println("UNKNOWN_REQUEST"); }  //pochi parametri
            else {
                if (! request[1].matches("\\w*")){  // \w sta per lettera o num, * sta per [0-infinite] volte
                    // se li nome non è espressione regolare
                    buffOut.println("INVALID_NAME");
                } else if (players.size()>MAX_Q_LEN) {
                    // se la coda è piena
                    buffOut.println("QUEUE_FULL");
                } else {
                    // se la richiesta è formalmente corretta,
                    // Devo controllare se ci sono nomi duplicati:
                    boolean duplicate = false;
                    for (int i=0; i<players.size(); i++){
                        if (request[1].equalsIgnoreCase(players.get(i).getName())){
                            duplicate = true;
                        }
                    }
                    if (duplicate){
                        buffOut.println("DUPLICATED_NAME");
                        // non deve chiudere la connessione da protocollo
                    } else {
                        int token = new Random().nextInt(100);
                        pongData.PlayerData pl = new pongData.PlayerData(request[1], String.valueOf(token), System.currentTimeMillis());
                        pl.setSocketPlayer(playerSocket);
                        players.add(pl);    //Aggiungi il player in lista
                        buffOut.println("OK " +
                                playgroundData.getW() + " " +
                                playgroundData.getH() + " " +
                                playgroundData.getK() + " " +
                                playgroundData.getkR() + " " +
                                pl.getName() + " " +
                                pl.getToken() + " " +
                                pl.getLastContactTime());
                        log.writeMessageEnq("Aggiunto un Player: " + request[1]);
                    }
                }
            }
        } else if (req.startsWith("UNSUBSCRIBE")){
            String request[] = req.split(" ") ;
            if (request.length < 2) {
                buffOut.println("UNKNOWN_REQUEST");
                return true;
            } else {
                //se ci sono i sufficenti parametri
                if (! request[1].matches("\\w*")){  // \w sta per lettera o num, * sta per [0-infinite] volte
                        // se li nome non è espressione regolare
                        buffOut.println("INVALID_NAME");
                    } else {
                        // Creo un nuovo Player, e controllo se esiste nella coda uno uguale
                        pongData.PlayerData plD = new pongData.PlayerData(request[1], request[2], 10);
                        int currentIndex = players.indexOf(plD);
                        if (currentIndex<0) {
                            //Se la risposta è -1 significa che non esiste in coda
                            buffOut.println("INVALID_NAME_OR_TOKEN");
                        } else {
                            players.remove(currentIndex);
                            buffOut.println("OK");
                            log.writeMessageEnq("Player rimosso dalla lista: " + plD.getName());
                        }
                    }
            }
        } else if (req.startsWith("REFRESH")){
            String request[] = req.split(" ") ;
            if (request.length < 2) {
                buffOut.println("UNKNOWN_REQUEST");
                return true;
            } else {
                //se ci sono i sufficenti parametri
                if (! request[1].matches("\\w*")){  // \w sta per lettera o num, * sta per [0-infinite] volte
                        // se li nome non è espressione regolare
                        buffOut.println("INVALID_NAME");
                    } else {
                        // Creo un nuovo Player, e controllo se esiste nella coda uno uguale
                        long now = System.currentTimeMillis();
                        pongData.PlayerData plD = new pongData.PlayerData(request[1], request[2], now);
                        int currentIndex = players.indexOf(plD);
                        if (currentIndex<0) {
                            //Se la risposta è -1 significa che non esiste in coda
                            buffOut.println("INVALID_NAME_OR_TOKEN");
                        } else {
                            players.get(currentIndex).setLastContactTime(now);
                            buffOut.println("OK");
                            //log.debugMessage("Player rinfrescato: " + plD.getName());
                        }
                    }
            }
        } else {
            buffOut.println("UNKNOWN_REQUEST");
            log.writeMessageEnq("UNKNOWN_REQUEST, chiudo la connessione");
            return true;
        }
        return false;
      }
    }

}
