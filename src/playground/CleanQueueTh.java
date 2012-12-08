package playground;

import pongData.PlayerData;
import java.util.List;

/**
 *
 * @author Erni
 */
public class CleanQueueTh extends Thread{
    List<pongData.PlayerData> players;

    public CleanQueueTh(List<PlayerData> players) {
        this.players = players;
    }

    @Override
    public void run(){
        // deve togliere dalla coda tutti i players che non si sono fatti vivi da + di 1 minuto
        long limit = 0;
        while (true) {
            try {
                Thread.sleep(10000);    //controlla ogni 10 sec
                limit = System.currentTimeMillis() - 60000;
                for (int i=0; i<players.size(); i++){
                    if (players.get(i).getLastContactTime() < limit){
                        //se l'ultimo contatto è obsoleto, chiudigli il socket e toglilo dalla coda
                        try {
                            players.get(i).getSocketPlayer().close();
                        } catch (Exception e) {
                            System.out.println("non posso chiudere il socket obsoleto " + e.getMessage());
                        }
                        players.remove(i);
                        System.out.println("cancello il player in posizione " + i + ", ormai obsoleto.");
                        i--;   //adesso ce n'è 1 in meno non devo saltare quello successivo
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
