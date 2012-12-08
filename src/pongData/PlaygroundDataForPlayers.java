/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pongData;

import java.net.InetAddress;

/**
 *
 * @author Erni
 */
public class PlaygroundDataForPlayers extends PlaygroundData implements Comparable{

    public PlaygroundDataForPlayers(InetAddress ip, int portEnqueuing, int portPlaying, int w, int h, double k, double kR) {
        super(ip, portEnqueuing, portPlaying, w, h, k, kR);
    }

   // public PlaygroundData(int w, int h, double k, double kR, long lastContactTime) {
    public PlaygroundDataForPlayers(int w, int h, double k, double kR) {
        super(w, h, k, kR);
        //this.lastContactTime = lastContactTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PlaygroundData other = (PlaygroundData) obj;

        if (super.getQueueLength() != other.getQueueLength()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return super.getQueueLength();
    }

    public int compareTo(Object aThat) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        //this optimization is usually worthwhile, and can
        //always be added
        if (aThat == null) {
            throw new NullPointerException();
            //return false;
        }
        if (getClass() != aThat.getClass()) {
            throw new ClassCastException();
        }

        PlaygroundData that = (PlaygroundData) aThat;

        if ( this == that ) return EQUAL;

        //primitive numbers follow this form
        if (this.getQueueLength() < that.getQueueLength()) return BEFORE;
        if (this.getQueueLength() > that.getQueueLength()) return AFTER;
        return EQUAL;
    }
}
