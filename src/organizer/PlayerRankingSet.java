/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package organizer;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.TreeSet;

/**
 *
 * @author Erni
 */
public class PlayerRankingSet extends TreeSet<pongData.PlayerRankingData>{
    /**
     *
     * @param playerName1 String
     * @param playerName2 String
     * @param p1 int Points player1
     * @param p2 int Points player1
     * @param log logger
     * refreshRankingFromResult aggiorna la classifica dei player dal risultato della partita
     */
    public synchronized void refreshRankingFromResult(String playerName1, String playerName2, int p1, int p2, LogOrganizer log){
        Iterator<pongData.PlayerRankingData> it = this.iterator();
        
        boolean trovatoPl1 = false;     // se esisteva nel ranking prima
        boolean trovatoPl2 = false;
        pongData.PlayerRankingData player1 = null;
        pongData.PlayerRankingData player2 = null;

        while (it.hasNext()){
            // scorre tutti i players
            pongData.PlayerRankingData currentPl = it.next();
            if (currentPl.getName().equals(playerName1)){
                // devo aggiornare il ranking di questo Pl!
                trovatoPl1 = true;
                player1 = currentPl;
                it.remove();
            } else if (currentPl.getName().equals(playerName2)){
                // devo aggiornare il ranking di questo Pl!
                trovatoPl2 = true;
                player2 = currentPl;
                it.remove();
            }
        }
        // sono stati visitati tutti i players, se non erano in lista bisogna inserirli
        if (!trovatoPl1){
            player1 = new pongData.PlayerRankingData(playerName1);
            player1.setOldRanking(0);
            player1.setRanking(0);
            //this.add(player1);
        }
        if (!trovatoPl2){
            player2 = new pongData.PlayerRankingData(playerName2);
            player2.setOldRanking(0);
            player2.setRanking(0);
            //this.add(player2);
        }
        double R1, R2,           //Costante (-1, 0.5, 1)
                D;          //differenza di ranking
        if (p1>p2){
            R1 = 1;
            R2 = -1;
            D = player2.getRanking() - player1.getRanking();        //looser original R - winner original R
        } else if (p1==p2){
            R1 = R2 = 0.5;
            D = Math.abs(player2.getRanking() - player1.getRanking());
        } else {
            R1 = -1;
            R2 = 1;      // se perdo, il punteggio rimane uguale
            D = player1.getRanking() - player2.getRanking();        //looser original R - winner original R
        }
        player1.setOldRanking(player1.getRanking());
        player1.setRanking(player1.getRanking() + R1*(20 + 0.05*D));

        player2.setOldRanking(player2.getRanking());
        player2.setRanking(player2.getRanking() + R2*(20 + 0.05*D));

        this.add(player1);
        this.add(player2);
        
        // ora bisogna salvare le modifiche su file
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(Organizer.FILE_PLAYER_RANKING);
            ReadWriteSet.writePlayersTo(fo, this, log);
        } catch (Exception e){

        } finally {
            try { fo.close(); }
            catch (Exception e) {}
        }
    }
}
