/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package playground;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author Erni
 */
public class Playground {
    public static int portOrganizer = 3030;
    public static InetAddress adressOrganizer = null;

    int portEnqueuing = 7950;
    int portPlaying = 7951;

    public Playground(){
        try {
            adressOrganizer = InetAddress.getLocalHost();
        } catch (Exception e) {

        }
    }

    public Playground(int portEnq, int portPly, InetAddress adressOrg){
        try {
            this.portEnqueuing = portEnq;
            this.portPlaying = portPly;
            adressOrganizer = adressOrg;
        } catch (Exception e) {

        }
    }
    
    public void start(){
        int w = 800;
        int h = 600;
        Double k = 1.1;
        Double kR = 0.2;

        //Uso Vector perchè è Synchronized
        List<pongData.PlayerData> players = new Vector<pongData.PlayerData>();
        LogPlayground log = new LogPlayground();

        log.writeMessage("Server partito su porEnq " + portEnqueuing + ", portPly " + portPlaying);

        pongData.PlaygroundData plygroundData = new pongData.PlaygroundData(
                null,
                portEnqueuing,
                portPlaying,
                w, h, k, kR);

        ManageEnqueuing mE = new ManageEnqueuing(portEnqueuing, players, log, plygroundData);
        mE.start();
        
        ManagePlaying mP = new ManagePlaying(portPlaying, players, log, plygroundData);
        mP.start();

        CleanQueueTh clean = new CleanQueueTh(players);
        clean.start();
        
        //> Dire a Organizer: register
        boolean registred = false;
        Socket s = null;
        while (!registred){
            try {
                s = new Socket(adressOrganizer, portOrganizer);
                PrintStream out = new PrintStream(s.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                out.println("REGISTER " +
                        InetAddress.getLocalHost().getHostAddress() + " " +
                        plygroundData.getPortEnqueuing() + " " +
                        plygroundData.getPortPlaying() + " " +
                        plygroundData.getW() + " " +
                        plygroundData.getH() + " " +
                        plygroundData.getK() + " " +
                        plygroundData.getkR()
                        );
                String resp = in.readLine();
                if (resp.equals("OK")){
                    log.writeMessage("Registrato dal Organizer correttamente");
                    registred = true;
                } else {
                    log.writeMessage("NON Registrato correttamente dal Organizer");
                    registred = true;
                    return;
                }
            } catch (Exception e) {
                log.writeMessage("eccezzione nella Registrazione dal Organizer: " + e.getMessage());
            } finally {
                try {
                    Thread.sleep(3000);
                    s.close();
                } catch (Exception e) {
                }
            }
        }
    }
}
