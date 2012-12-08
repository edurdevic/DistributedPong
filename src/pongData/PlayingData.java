/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pongData;

/**
 *
 * @author Erni
 *
 * Serve al Player per conoscere i dati della partita
 */
public class PlayingData {

    private int palyerNum;
    private int xR1;    // Posizione racchetta 1
    private int xR2;    // Posizione racchetta 2
    private int xB;     // Posizione pallina x
    private int yB;     // Posizione pallina y
    private int p1;     //Punteggio giocatore 1
    private int p2;     //Punteggio giocatore 2

    public PlayingData(int palyerNum, int xR1, int xR2, int xB, int yB, int p1, int p2) {
        this.palyerNum = palyerNum;
        this.xR1 = xR1;
        this.xR2 = xR2;
        this.xB = xB;
        this.yB = yB;
        this.p1 = p1;
        this.p2 = p2;
    }
    public int getMyPosition(){
        if (palyerNum == 1) {
            return xR1;
        } else {
            return xR2;
        }
    }

    @Override
    public String toString(){
        return "Pallina: (" + xB + "," + yB + ")\n" +
                "Player1: " + xR1 + "\n" +
                "Player2: " + xR2 + "\n" ;
    }
    
    public int getPalyerNum() {
        return palyerNum;
    }

    public void setPalyerNum(int palyerNum) {
        this.palyerNum = palyerNum;
    }

    public int getP1() {
        return p1;
    }

    public void setP1(int p1) {
        this.p1 = p1;
    }

    public int getP2() {
        return p2;
    }

    public void setP2(int p2) {
        this.p2 = p2;
    }

    public int getxB() {
        return xB;
    }

    public void setxB(int xB) {
        this.xB = xB;
    }

    public int getxR1() {
        return xR1;
    }

    public void setxR1(int xR1) {
        this.xR1 = xR1;
    }

    public int getxR2() {
        return xR2;
    }

    public void setxR2(int xR2) {
        this.xR2 = xR2;
    }

    public int getyB() {
        return yB;
    }

    public void setyB(int yB) {
        this.yB = yB;
    }

}
