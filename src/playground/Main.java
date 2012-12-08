/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package playground;

import java.net.InetAddress;

/**
 *
 * @author Erni
 */
public class Main {

     public static void main(String[] args) {
         int portEnq, portPly;
         InetAddress organizerAdress;
         if (args.length < 2) {
            System.out.println("Parametri insufficenti, in uso parametri di default");
            try {
                organizerAdress = InetAddress.getLocalHost();
            } catch (Exception e){
                System.out.println("Impossibile reperire l'indirizzo di localhost");
                return;
            }
            portEnq = 7950;
            portPly = 7951;
        } else {
            if (args.length == 3){
                try {
                    organizerAdress = InetAddress.getByName(args[2]) ;
                } catch (Exception e){
                    System.out.println("Argomenti invalidi. Usare: [PORT_ENQUEUING PORT_PLAYING] [ORGANIZER_ADRESS]");
                    return;
                }
            } else {
                try {
                    organizerAdress = InetAddress.getLocalHost();
                    System.out.println("Non è stato specificato un indirizzo per l'organizer, in uso LOCALHOST.");
                } catch (Exception e){
                    System.out.println("Impossibile reperire l'indirizzo di localhost");
                    return;
                }
            }
            try {
                portEnq = Integer.parseInt(args[0]);
                portPly = Integer.parseInt(args[1]);
            } catch (Exception e){
                System.out.println("Argomenti invalidi. Usare: [PORT_ENQUEUING PORT_PLAYING] [ORGANIZER_ADRESS]");
                return;
            }
        }

        Playground p = new Playground(portEnq, portPly, organizerAdress);
        p.start();
     }

}
