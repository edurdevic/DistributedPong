/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pongData;

/**
 *
 * @author Erni
 */
public class PlayerRankingData implements Writable, Comparable{
    private String name;
    private double ranking;
    private double oldRanking;

    public PlayerRankingData(String name, double ranking, double oldRanking) {
        this.name = name;
        this.ranking = ranking;
        this.oldRanking = oldRanking;
    }

    public PlayerRankingData(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PlayerRankingData other = (PlayerRankingData) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
    public int compareTo(Object aThat) {
        final int BEFORE = 1;
        final int EQUAL = 0;
        final int AFTER = -1;

        //this optimization is usually worthwhile, and can
        //always be added
        if (aThat == null) {
            throw new NullPointerException();
            //return false;
        }
        if (getClass() != aThat.getClass()) {
            throw new ClassCastException();
        }

        PlayerRankingData that = (PlayerRankingData) aThat;
        
        if ( this == that ) return EQUAL;

        //primitive numbers follow this form
        if (this.equals(aThat)) return EQUAL;               //devo farlo, altrimenti non c'è coerenza con equals
        if (this.name.equals(that.name)) return EQUAL;
        if (this.ranking < that.ranking) return BEFORE;
        if (this.ranking > that.ranking) return AFTER;
        return AFTER;   // se sono pari, uno a caso deve andare sopra o sotto, se do Equal non li inserisce!
    }



    @Override
    public String toString() {
        return name + " " + ranking + " " + oldRanking;
    }

    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getOldRanking() {
        return oldRanking;
    }

    public void setOldRanking(double oldRanking) {
        this.oldRanking = oldRanking;
    }

    public double getRanking() {
        return ranking;
    }

    public void setRanking(double ranking) {
        this.ranking = ranking;
    }

    


}
