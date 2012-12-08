package playerSmart;

import java.net.InetAddress;

/**
 * @author Erni
 */
public class Main {
    /**
     * @param INET_ORGANIZER NAME
     */
    public static final int port = 7970;
    public static String name = "Player";
    public static InetAddress organizerAdress = null;

    public static void main(String[] args) {
        // gestisce i parametri a riga di comando
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
            System.out.println("Argomenti invalidi. Usare: ORGANIZER NOME");
            return;
        }
        System.out.println("Player in gioco: " + name);
        Player p = new Player(name, organizerAdress, port);
        p.play();

    }
}
