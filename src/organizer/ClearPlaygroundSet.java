/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package organizer;

import java.io.*;
import java.net.Socket;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Erni
 *
 * ClearPlaygroundSet è un Thread che scorre l'insieme di playground,
 * li contatta per controllare che siano ancora attivi. In caso contrario li
 * toglie dalla lista.
 */
public class ClearPlaygroundSet extends Thread{

    Set<pongData.PlaygroundData> playgrounds;
    LogOrganizer log;

    public ClearPlaygroundSet(Set<pongData.PlaygroundData> playgrounds,
            LogOrganizer log) {
        this.playgrounds = playgrounds;
        this.log = log;
    }

    @Override
    // Deve aggiornare la lista dei Playgrounds togliendo quelli inattivi ogni 10 sec
    public void run() {
        boolean more = true;            // finchè non ho qualche eccezione grave
        boolean modificato = false;
        while (more) {
            modificato = false;
            try {
                Iterator<pongData.PlaygroundData> iterator = playgrounds.iterator();
                while (iterator.hasNext()){
                    //scorre tutti i Playground nella lista
                    pongData.PlaygroundData playground = iterator.next();
                    Socket s = null;
                    try {
                        // cerca di richiedere la lista dei Players sul Playground
                        s = new Socket(playground.getIp(),playground.getPortEnqueuing());
                        BufferedReader playgIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        PrintStream playgOut = new PrintStream(s.getOutputStream());
                        playgOut.println("LIST");
                        String resp = playgIn.readLine();
                        int queueLen = Integer.parseInt(resp) - 1;
                        
                        if (!(queueLen == playground.getQueueLength())){
                            // se è cambiata la lung della fila, salva il nuovo val
                            playground.setQueueLength(queueLen);
                            modificato = true;
                        }
                        
                    } catch (Exception e){
                        // se non è riuscito a contattare un pl, lo toglie dalla lista
                        log.CleanerMessage("Non posso contattare " + playground.getIp() + " ; " + e.getMessage());
                        iterator.remove();
                        modificato = true;
                    } finally {
                        // alla fine chiudi il sochet
                        try {
                            s.close();
                        } catch (Exception ex){
                            // non fa niente
                        }
                    }
                }
            }catch (Exception ex){
                more = false;
                log.CleanerMessage("Errore inaspettato nel Cleaner, non posso continuare! " + ex.getMessage());
            }
            
            // ora devo salvare le modifiche al set di playgrounds
            if (modificato){
                // modifico solo se è cambiato qualcosa.
                try {
                    ReadWriteSet rw = new ReadWriteSet();
                    rw.writePlaygrundsTo(new FileOutputStream(Organizer.FILE_PLAYGROUNDS), playgrounds, log);
                } catch (Exception e) {
                    log.ClientErrMessage("Impossibile salvare il file con i Playgrounds!");
                }
            }
            try {
                // ripeti il tutto ogni 10 secondi
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ClearPlaygroundSet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }


}
