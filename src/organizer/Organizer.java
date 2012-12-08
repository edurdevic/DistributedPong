/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package organizer;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Erni
 */
public class Organizer {

    // Costanti:
    public static final int PORT_ORGANIZER = 3030;
    public static final String FILE_PLAYGROUNDS = "PlaygroundSet.txt";
    public static final String FILE_PLAYER_RANKING = "RankingSet.txt";
    public static final String FILE_MATCHES = "MatchList.txt";

    public Organizer() {
    }

    public void start(){
        // ho bisogno di un set di Playgrounds sincronizzato
        Set<pongData.PlaygroundData> playgrounds = Collections.synchronizedSet(new HashSet<pongData.PlaygroundData>());
        PlayerRankingSet players = new PlayerRankingSet();
        LogOrganizer log = new LogOrganizer();

        // Carica i dati dei Set dai file (se puoi)
        ReadWriteSet rw = new ReadWriteSet();
        try {
            rw.readPlayerRankingFromFile(FILE_PLAYER_RANKING, players, log);
        } catch (IOException ex) {
            Logger.getLogger(Organizer.class.getName()).log(Level.SEVERE, null, ex);
            log.ErrorMessage("non riesco a leggere il file di RANKING - " + ex.getMessage());
        }
        try {
            rw.readPlaygroundsFromFile(FILE_PLAYGROUNDS, playgrounds, log);
        } catch (IOException ex) {
            Logger.getLogger(Organizer.class.getName()).log(Level.SEVERE, null, ex);
            log.ErrorMessage("non riesco a leggere il file di PLAYGROUNDS - " + ex.getMessage());
        }

        // inizia a controllare che i Playgrounds siano attivi
        ClearPlaygroundSet cl = new ClearPlaygroundSet(playgrounds, log);
        cl.start();

        //parte anche il server per le richieste
        ManageRequests server = new ManageRequests(playgrounds, players, log);
        server.start();
        
    }

}
