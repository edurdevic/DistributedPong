/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package organizer;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.Set;
import pongData.PlaygroundData;

/**
 *
 * @author Erni
 * HandleClients è un Thread che gestisce i client che si connette all'Organizer.
 * Elabora la richiesta e risponde.
 */
public class HandleClients extends Thread {
    Set<pongData.PlaygroundData> playgrounds;
    PlayerRankingSet players;
    Socket client = null;
    LogOrganizer log;

    InputStream in;
    OutputStream out;
    PrintStream buffOut;
    BufferedReader buffIn;

    public HandleClients(Socket s, Set<PlaygroundData> playgrounds, PlayerRankingSet players, LogOrganizer log) {
        super();
        this.playgrounds = playgrounds;
        this.players = players;
        this.log = log;
        client = s;
    }


    @Override
    public void run() {
        try {
            in = client.getInputStream();
            out = client.getOutputStream();
            buffIn = new BufferedReader(new InputStreamReader(in));
            buffOut = new PrintStream(out);
            boolean done = false;

            while (!done){  //finchè la process response mi dice di abbandonare il client
                try{
                    String corrente = buffIn.readLine();
                    done = processResponse(corrente);       //Gestisci la richiesta

                    Thread.sleep(25);                       //Devo aspettare tra 2 risposte consecutive
                } catch (NullPointerException ne) {
                    done = true;                            //Se ho un'eccezzione significa che il Cl
                                                            //se ne è andato
                }
                catch(Exception e){
                    log.ErrorMessage("impossibile gestire il Client: " + e.getMessage());
                    done = true;                            //chiudi pure la connessione
                }
            }
        } catch (IOException ex) {
            log.ErrorMessage("Non posso gestire il client, errore Socket");
            return;
        } finally {
            try{
                client.close();
                in.close();
                out.close();
            } catch (Exception e) {
                //ok, erano già chiusi
            }
        }
    }

    private boolean processResponse(String req) throws IOException{
            ReadWriteSet rw = new ReadWriteSet();
            if (req.startsWith("LIST")){
                synchronized (playgrounds) {                 //Devo prendere il lock sul set di Playgrounds
                    buffOut.println(playgrounds.size()+1);
                    rw.writePlaygrundsTo(out, playgrounds, log);
                }
                return false;               //non ho finito con questo client
            } else if(req.equals("RANKING")){
                synchronized (players) {                    //Devo prendere il lock sul set di players
                    buffOut.println(players.size()+1);
                    ReadWriteSet.writePlayersTo(out, players, log);
                }
                return false;

            } else if (req.startsWith("REGISTER")){
                // REGISTER localhost 7960 7961 800 600 2 1
                String request[] = req.split(" ") ;
                if (request.length < 8) {
                    // Numero di parametri insufficente
                    buffOut.println("INVALID_PARAMETERS");
                    return true;
                }

                String ip;
                int portEn, portPl, w, h;
                double k, kR;
                
                try {
                    ip = request[1];
                    portEn = Integer.parseInt(request[2]);
                    portPl = Integer.parseInt(request[3]);
                    w = Integer.parseInt(request[4]);
                    h = Integer.parseInt(request[5]);
                    k = Double.parseDouble(request[6]);
                    kR = Double.parseDouble(request[7]);
                    // Ok, se tutti i parametri sono validi
                } catch (NumberFormatException numE){
                    // almeno uno dei parametri non era valido
                    buffOut.println("INVALID_PARAMETERS");
                    return true;
                }

                PlaygroundData thisPl = new PlaygroundData(InetAddress.getByName(ip), portEn, portPl, w, h, k, kR);

                Iterator<PlaygroundData> it = playgrounds.iterator();
                while (it.hasNext()){       //Scorro tutta la lista di playgrounds
                    try {
                        PlaygroundData next = it.next();
                        if (thisPl.getIp().equals(next.getIp())){  //Se hanno lo stesso IP, devono avere porte diverse
                            if (
                                    portEn == next.getPortEnqueuing() ||
                                    portEn == next.getPortPlaying() ||
                                    portPl == next.getPortEnqueuing() ||
                                    portPl == next.getPortPlaying()
                                    ){
                                // non va bene, ci sono delle porte sovrapposte con stesso IP!
                                buffOut.println("INVALID_PARAMETERS");
                                log.ClientErrMessage("ci sono delle porte sovrapposte con stesso IP!");
                                return true;
                            }
                        }
                    } catch (Exception e) {
                        buffOut.println("INVALID_PARAMETERS");
                        log.ClientErrMessage("Non riesco a controllare se ci sono delle porte sovrapposte con stesso IP!");
                        return true;
                    }
                    
                }

                // se sono arrivato qui, è andato tutto bene!
                // devo controllare ci sia davvero alla porta comunicata
                Socket s = null;
                int queueLen = 0;
                try{
                    s = new Socket(thisPl.getIp(), portEn);
                    PrintStream ps = new PrintStream(s.getOutputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    ps.println("LIST");
                    String r = br.readLine();
                    queueLen = Integer.parseInt(r) - 1;            
                } catch (Exception e){
                    buffOut.println("IGNORING_YOU");
                    log.ClientErrMessage("Il playground che mi ha contattato non risponde!");
                    return true;
                } finally {
                    s.close();
                }

                // Il playground ha risposto, posso aggiungerlo

                thisPl.setLastContactTime(System.currentTimeMillis());
                thisPl.setQueueLength(queueLen);

                playgrounds.add(thisPl);

                log.RequestMessage("Playground " + thisPl.getIp().getHostAddress() + " registrato correttamente.");
                
                
                // 
                FileOutputStream fo = null;
                try {
                    fo = new FileOutputStream(Organizer.FILE_PLAYGROUNDS);
                    rw.writePlaygrundsTo(fo, playgrounds, log);
                } catch (Exception e) {
                    log.ClientErrMessage("Impossibile salvare il file con i Playgrounds!");
                } finally {
                    try { fo.close(); }
                    catch (Exception e) {}
                }
                buffOut.println("OK");      //gli rispondo "OK"
                return false;
            } else if (req.startsWith("RESULT")){
                String request[] = req.split(" ") ;
                return doneProcessResult(request, req);
            } else {
                buffOut.println("UNKNOWN_REQUEST");
                log.ErrorMessage("UNKNOWN_REQUEST, chiudo la connessione");
                return true;
            }


        //return false;
    }
    
    // rirorna "DoneWithThisClient"
    boolean doneProcessResult(String [] request, String requestStr){
        if (request.length < 9) {
            // Numero di parametri insufficente
            buffOut.println("INVALID_PARAMETERS");
            return true;
        }
        // RESULT 800 600 2 3 ee dd 3 8
        String player1, player2;
        int p1, p2, w, h;
        double k, kR;

        try {
            w = Integer.parseInt(request[1]);
            h = Integer.parseInt(request[2]);
            k = Double.parseDouble(request[3]);
            kR = Double.parseDouble(request[4]);
            player1 = request[5];
            player2 = request[6];
            p1 = Integer.parseInt(request[7]);
            p2 = Integer.parseInt(request[8]);
            // tutti i parametri sono validi
        } catch (NumberFormatException numE){
            // almeno uno dei parametri non era valido
            buffOut.println("INVALID_PARAMETERS");
            return true;
        } catch (Exception e){
            buffOut.println("INVALID_PARAMETERS");
            return true;
        }

        if (! (player1.matches("\\w*") && player2.matches("\\w*") )){  // \w sta per lettera o num, * sta per [0-infinite] volte
            // se 1 dei 2 nomi del player non è regolare non lo posso accettare
            buffOut.println("INVALID_PARAMETERS");
            return true;
        }
        // notifico il termine dalla partita al log
        log.RequestMessage("Partita terminata: " + player1 + ":" + player2 + "  " + p1 + ":" + p2 );
        players.refreshRankingFromResult(player1, player2, p1, p2, log);    //fa tutto la lista di players "PlayerRankingSet"
        saveResult(requestStr);
        log.RequestMessage("Ranking rinfrescato e partita salvata...");
        buffOut.println("OK");
        return false;
    }
    private void saveResult(String result){
        // Salva su file il risultato del match
        PrintStream f = null;
        try {
            f = new PrintStream(new FileOutputStream(Organizer.FILE_MATCHES, true));
            f.println(result);
        } catch (FileNotFoundException ex) {
            log.ErrorMessage("Non posso salvare il risultato della partita.");
        } finally {
            try { f.close(); }
            catch (Exception e) {}
        }
    }
}
