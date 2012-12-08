/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package organizer;

import java.io.*;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import pongData.Writable;

/**
 *
 * @author Erni
 */
public class ReadWriteSet {
    public synchronized void writeSetTo (OutputStream out, Set<Writable> set, LogOrganizer log) throws IOException{
        synchronized (set){
            try{
                PrintStream buffOut = new PrintStream(out);
                Iterator<Writable> it = set.iterator();
                while (it.hasNext()){
                    //scorre tutti i Playground nella lista
                    Writable item = it.next();
                    buffOut.println(item.toString());
                }
            } catch (Exception e) {
                log.writeMessage("Errore, impossibile scrivere i dati sullo stream " + out.toString() + " Err: " + e.getMessage());
            } finally {
                // non devo chiudere, ho ricevuto aperto
                // out.close();
            }
        }
    }
    public synchronized void writePlaygrundsTo (OutputStream out, Set<pongData.PlaygroundData> set, LogOrganizer log) throws IOException{
        synchronized (set){
            try{
                PrintStream buffOut = new PrintStream(out);
                Iterator<pongData.PlaygroundData> it = set.iterator();
                while (it.hasNext()){
                    //scorre tutti i Playground nella lista
                    Writable item = it.next();
                    buffOut.println(item.toString());
                }
            } catch (Exception e) {
                log.writeMessage("Errore, impossibile scrivere i dati sullo stream " + out.toString() + " Err: " + e.getMessage());
            } finally {
                // non devo chiudere, ho ricevuto aperto
                // out.close();
            }
        }
    }
    public static synchronized void writePlayersTo (OutputStream out, PlayerRankingSet set, LogOrganizer log) throws IOException{
        synchronized (set){
            try{
                PrintStream buffOut = new PrintStream(out);
                Iterator<pongData.PlayerRankingData> it = set.iterator();
                while (it.hasNext()){
                    //scorre tutti i Playground nella lista
                    Writable item = it.next();
                    buffOut.println(item.toString());
                }
            } catch (Exception e) {
                log.writeMessage("Errore, impossibile scrivere i dati sullo stream " + out.toString() + " Err: " + e.getMessage());
            } finally {
                // non devo chiudere, ho ricevuto aperto
                // out.close();
            }
        }
    }
    public synchronized void readPlayerRankingFromFile (String fileName, PlayerRankingSet set, LogOrganizer log) throws IOException{
        try{
            InputStream in = new FileInputStream(fileName);
            BufferedReader buffIn = new BufferedReader(new InputStreamReader(in));
           // PlayerRankingSet newSet = new PlayerRankingSet();

            String line = null;

            while (( line = buffIn.readLine()) != null){
                String [] dataResponse = line.split(" ");
                pongData.PlayerRankingData player = new pongData.PlayerRankingData(
                        dataResponse[0],
                        Double.parseDouble(dataResponse[1]),
                        Double.parseDouble(dataResponse[2]) );
                try {
                set.add(player);
                } catch (Exception e ){
                    log.ErrorMessage("impossibile aggiungere il Player dal file al Set");
                }
            }

            //set = newSet;

        } catch (IOException exIO) {
            throw exIO;
        } catch (Exception e) {
            log.writeMessage("Errore, impossibile leggere i dati dal file " + fileName + " Err: " + e.getMessage());
        }
    }

    public synchronized void readPlaygroundsFromFile (String fileName, Set<pongData.PlaygroundData> set, LogOrganizer log) throws IOException{
        try{
            InputStream in = new FileInputStream(fileName);
            BufferedReader buffIn = new BufferedReader(new InputStreamReader(in));
            Set<pongData.PlaygroundData> newSet = new HashSet<pongData.PlaygroundData>();

            String line = null;

            while (( line = buffIn.readLine()) != null){
                String [] dataResponse = line.split(" ");
                pongData.PlaygroundData playground = new pongData.PlaygroundData(
                        InetAddress.getByName(dataResponse[0]),     // ip
                        Integer.parseInt(dataResponse[1]),          // portEnq
                        Integer.parseInt(dataResponse[2]),          // portPlay
                        Integer.parseInt(dataResponse[3]),          // w
                        Integer.parseInt(dataResponse[4]),          // h
                        Double.parseDouble(dataResponse[5]),        // k
                        Double.parseDouble(dataResponse[6])         // kR
                        );
                playground.setLastContactTime(Long.parseLong(dataResponse[7]));     // lastContactTime
                playground.setQueueLength(Integer.parseInt(dataResponse[8]));       // QueueLen

                set.add(playground);

            }

        } catch (IOException exIO) {
            throw exIO;
        } catch (Exception e) {
            log.writeMessage("Errore, impossibile leggere i dati dal file " + fileName + " Err: " + e.getMessage());
        }
    }
}
