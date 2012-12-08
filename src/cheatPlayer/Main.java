package cheatPlayer;

import java.net.InetAddress;

/**
 * @author Erni
 */
public class Main {
    /**
     * @param NAME INET_ORGANIZER
     * CheatPlayer è un giocatore bugiardo. Fa finta di giocare, invece contatta solo l'organizer per
     * comunicare i risultati falsi delle partite.
     */
    public static final int port = 7970;
    public static String name = "Player";
    public static InetAddress organizerAdress = null;

    public static void main(String[] args) {
        if (args.length == 2){
            try {
                organizerAdress = InetAddress.getByName(args[1]) ;
                name = args[0];
            } catch (Exception e){
                System.out.println("Argomenti invalidi. Usare: ORGANIZER NOME");
                return;
            }
        } else if (args.length == 1){
            try {
                organizerAdress = InetAddress.getLocalHost();
                name = args[0];
                System.out.println("Non è stato specificato un indirizzo per l'organizer, in uso LOCALHOST.");
            } catch (Exception e){
                System.out.println("Impossibile reperire l'indirizzo di localhost");
                return;
            }
        } else {
            System.out.println("Argomenti invalidi. Usare: NOME ORGANIZER ");
            return;
        }
        Player p = new Player(name, organizerAdress, port);
        p.play();

    }
}
