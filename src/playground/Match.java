/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package playground;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 *
 * @author Erni
 */
public class Match extends Thread {

    private LogPlayground log;
    private pongData.PlaygroundData playgroundData;
    private pongData.MatchStatus status;
    private static int count = 0;
    private int id = 0;
    private long TLastStepMillis = 0;
    private double T = 0;

    public boolean terminateMatch = false;

    public Match (
            LogPlayground log,
            pongData.PlaygroundData playgroundData,
            pongData.MatchStatus status){
        this.log = log;
        this.playgroundData = playgroundData;
        this.status = status;
        count ++;
        id = count;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Match other = (Match) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    
    
    

    @Override
    public void run(){

        status.pointsReset(playgroundData.getW(), playgroundData.getH(), playgroundData.getVip());
        log.writeMessage("Match:     Partita " + id + " iniziata!");
        TLastStepMillis = System.currentTimeMillis();
        
        while ((status.getPunteggio1()<playgroundData.getpMax() &&
                status.getPunteggio2()<playgroundData.getpMax()) &&
                //!status.isMatchEnd()){
                !terminateMatch){
            try{
                Thread.sleep((int)(playgroundData.getT()*1000));
                synchronized (status){
                    long temp = System.currentTimeMillis();
                    long diffMillis = temp - TLastStepMillis;
                    T = ((double)diffMillis/1000);
                    TLastStepMillis = temp;

                    aggirnaRacchetta1();
                    aggirnaRacchetta2();
                    aggiornaPalla();
                }
                //System.out.println("Status: Xb=" + status.getxB() + " XR1=" + status.getxR1() + " XR2=" + status.getxR2());
            } catch (Exception e){
                log.writeMessagePly("Spiacente, errore imprevisto nel Match. " + e.getMessage());
                //status.setMatchEnd(true);
                terminateMatch = true;
            }

        }

        String PlayerName1 = status.getPlayer1().getName();
        String PlayerName2 = status.getPlayer2().getName();
        int punti1 = status.getPunteggio1();
        int punti2 = status.getPunteggio2();

        synchronized (status){
            //status.pointsReset(playgroundData.getW(), playgroundData.getH(), playgroundData.getVip());
            try {
                status.getPlayer1().getSocketPlayer().close();
            } catch (Exception e){
                //non fa niente, era gia chiuso
                log.writeMessage("MATCH: non posso chiudere i socket dei client! " + e.getMessage());
            }
            try {
                status.getPlayer2().getSocketPlayer().close();
            } catch (Exception e){
                //non fa niente, era gia chiuso
                log.writeMessage("MATCH: non posso chiudere i socket dei client! " + e.getMessage());
            }
            log.writeMessage("PARTITA TERMINATA " + status.getPunteggio1() + " : " + status.getPunteggio2());
            status.totalReset(playgroundData.getW(), playgroundData.getH(), playgroundData.getVip());

        }
        // ora bisogna contattare l'organizer per il risultato!
        Socket s = null;
        try {
            s = new Socket(Playground.adressOrganizer, Playground.portOrganizer);
            PrintStream buffOut = new PrintStream(s.getOutputStream());
            BufferedReader buffIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
            buffOut.println("RESULT " +
                    playgroundData.getW() + " " +
                    playgroundData.getH() + " " +
                    playgroundData.getK() + " " +
                    playgroundData.getkR() + " " +
                    PlayerName1 + " " +
                    PlayerName2 + " " +
                    punti1 + " " +
                    punti2
                    );
            if (!(buffIn.readLine().equalsIgnoreCase("OK"))){
                log.writeMessage("Match non riesce a comunicare RESULT! risposta non OK");
            }
        } catch (Exception e) {
            log.writeMessage("Match non riesce a comunicare RESULT all' Organizer!");
        }finally {
            try {
                s.close();
            } catch (Exception e) {

            }
        }
    }

    private void aggirnaRacchetta1(){
        if (   ( status.getxR1() +
                (T*status.getvXR1()) +
                (playgroundData.getwR()/2)
                ) > playgroundData.getW()
                ){
            // se sta sbattendo a destra:
            status.setxR1(playgroundData.getW() - (playgroundData.getwR()/2));
            status.setvXR1(0);
        } else if (   ( status.getxR1() +
                (T*status.getvXR1()) -
                (playgroundData.getwR()/2)
                ) < 0
                ){
            // se sta sbattendo a sinistra
            status.setxR1(playgroundData.getwR()/2);
            status.setvXR1(0);
        } else {
            //se non sta sbattendo:
            status.setxR1((int)(status.getxR1()+(T*status.getvXR1())));
            //la racchetta deve anche decelerare:
            if (status.getvXR1()>0){
                status.setvXR1(status.getvXR1()-T*playgroundData.getaR());
            } else if (status.getvXR1()<0){
                status.setvXR1(status.getvXR1()+T*playgroundData.getaR());
            }
        }
    }

    private void aggirnaRacchetta2(){
        if (   ( status.getxR2() +
                (T*status.getvXR2()) +
                (playgroundData.getwR()/2)
                ) > playgroundData.getW()
                ){
            // se sta sbattendo a destra:
            status.setxR2(playgroundData.getW() - (playgroundData.getwR()/2));
            status.setvXR2(0);
        } else if (   ( status.getxR2() +
                (T*status.getvXR2()) -
                (playgroundData.getwR()/2)
                ) < 0
                ){
            // se sta sbattendo a sinistra
            status.setxR2(playgroundData.getwR()/2);
            status.setvXR2(0);
        } else {
            //se non sta sbattendo:
            status.setxR2((int)(status.getxR2()+(T*status.getvXR2())));
            //la racchetta deve anche decelerare:
            if (status.getvXR2()>0){
                status.setvXR2(status.getvXR2()-T*playgroundData.getaR());
            } else if (status.getvXR2()<0){
                status.setvXR2(status.getvXR2()+T*playgroundData.getaR());
            }
        }
    }
    private void aggiornaPalla(){
        //System.out.println("---P:(" + status.getxB() + "," + status.getyB() + ")  P1: "
         //       + status.getxR1() + "  P2: " + status.getxR2());
        int xB = status.getxB();
        double vXB = status.getvXB();

        int yB = status.getyB();
        double vYB = status.getvYB();
        
        int w = playgroundData.getW();
        int h = playgroundData.getH();
        int wR = playgroundData.getwR();

        // gestisce le coordinate x della pallina
        if ((xB+T*vXB) >= w){
            status.setxB((int)(2*w - xB - T*vXB));
            status.setvXB(-vXB);
        } else if ((xB+T*vXB) <= 0) {
            status.setxB((int)( -xB - T*vXB));
            status.setvXB(-vXB);
        } else {
            status.setxB((int)(xB + T*vXB));
        }

        xB = status.getxB();

        //gestisce le coordinate y della pallina e il punteggio.
        if ( (yB + T*vYB) > h){
            if ((status.getxR2() - wR/2 <= xB) && (status.getxR2() + wR/2 >= xB)){
                status.setyB((int)(2*h - yB - T*vYB));
                status.setvYB(-playgroundData.getK()*vYB);
                status.setvXB(playgroundData.getK()*vXB + playgroundData.getkR()*status.getvXR2());
                //log.writeMessagePly("Player 2 ha parato");
                System.out.println("Player 2 ha parato  ---P:(" + status.getxB() + "," + status.getyB() + ")  P1: "
                    + status.getxR1() + "  P2: " + status.getxR2() + "      " + (xB-status.getxR2()));
            } else {
                log.writeMessagePly("Pallina: (" + xB + "," + yB + ")\n" +
                    "Player1: " + status.getxR1() + "\n" +
                    "Player2: " + status.getxR2() + "\n" );
                status.setPunteggio1(status.getPunteggio1()+1);
                status.reset(w, h, playgroundData.getVip());
                log.writeMessagePly("Player 1 (" + status.getPlayer1().getName() +") ha segnato! " + status.getPunteggio1() + " : " + status.getPunteggio2());
                
            }
        } else if ( (yB + T*vYB) < 0){
            if ((status.getxR1() - wR/2 <= xB) && (status.getxR1() + wR/2 >= xB)){
                status.setyB((int)(- yB - T*vYB));
                status.setvYB(-playgroundData.getK()*vYB);
                status.setvXB(playgroundData.getK()*vXB + playgroundData.getkR()*status.getvXR1());
                //log.writeMessagePly("Player 1 ha parato");
                System.out.println("Player 1 ha parato  ---P:(" + status.getxB() + "," + status.getyB() + ")  P1: "
                    + status.getxR1() + "  P2: " + status.getxR2() + "      " + (xB-status.getxR1()));
            } else {
                log.writeMessagePly("Pallina: (" + xB + "," + yB + ")\n" +
                    "Player1: " + status.getxR1() + "\n" +
                    "Player2: " + status.getxR2() + "\n" );
                status.setPunteggio2(status.getPunteggio2()+1);
                status.reset(w, h, playgroundData.getVip());
                log.writeMessagePly("Player 2 (" + status.getPlayer2().getName() +") ha segnato! " + status.getPunteggio1() + " : " + status.getPunteggio2());
                
            }
        } else {
            status.setyB((int)(yB + T*vYB));
        }
    }
}
