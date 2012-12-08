/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package organizer;

/**
 *
 * @author Erni
 */
public class Main {
    public static void main (String [] args) {
        // non ha bisogno di parametri, la porta è fissata a 3030
        Organizer org = new Organizer();
        org.start();
    }
}
