/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pongData;

import playground.Match;

/**
 *
 * @author Erni
 */
public class MatchStatus{
    //Controllo di gioco:
    private boolean matchStarted = false;
    private boolean player1InMatch = false;
    private boolean player2InMatch = false;
    private PlayerData player1 = null;
    private PlayerData player2 = null;
    private boolean matchEnd = false;
    private playground.Match currentMatch = null;

    private int punteggio1 = 0;
    private int punteggio2 = 0;

    //Pallina:
    private int xB = 50;             //Posizione pallina
    private int yB = 50;
    private double vXB = 100;         //Velocità pallina
    private double vYB = 100;

    //racchette:
    private int xR1 = 200;
    private int xR2 = 200;
    private double vXR1 = 0;
    private double vXR2 = 0;

    public synchronized void reset(int w, int h, double vi){
        vXR1 = 0;
        vXR2 = 0;
        vXB = vi;
        vYB = vi;
        yB = h/2;
        xB = xR1= xR2 = w/2;
    }
    public synchronized void pointsReset(int w, int h, double vi){
        vXR1 = 0;
        vXR2 = 0;
        vXB = vi;
        vYB = vi;
        yB = h/2;
        xB = xR1= xR2 = w/2;

        punteggio1 = 0;
        punteggio2 = 0;
    }
    public synchronized void matchEndReset(int w, int h, double vi){
        vXR1 = 0;
        vXR2 = 0;
        vXB = vi;
        vYB = vi;
        yB = h/2;
        xB = xR1= xR2 = w/2;

        matchStarted = false;
        player1InMatch = false;
        player2InMatch = false;
        matchEnd = false;
        punteggio1 = 0;
        punteggio2 = 0;
    }
    public synchronized void totalReset(int w, int h, double vi){
        vXR1 = 0;
        vXR2 = 0;
        vXB = vi;
        vYB = vi;
        yB = h/2;
        xB = xR1= xR2 = w/2;

        matchStarted = false;
        player1InMatch = false;
        player2InMatch = false;
        player1 = null;
        player2 = null;
        matchEnd = false;
        punteggio1 = 0;
        punteggio2 = 0;
    }

    @Override
    public String toString() {
        String s = "\n   Pl1InMatch: " + player1InMatch +
                "\n   Pl2InMatch: " + player2InMatch +
                "\n   started:    " + matchStarted +
                "\n   Punteggio 1: " + punteggio1 +
                "\n   Punteggio 2: " + punteggio2 + "\n";

        return s;
    }

    public Match getCurrentMatch() {
        return currentMatch;
    }

    public void setCurrentMatch(Match currentMatch) {
        this.currentMatch = currentMatch;
    }

    public synchronized boolean isPlayer2InMatch() {
        return player2InMatch;
    }

    public synchronized void setPlayer2InMatch(boolean player2InMatch) {
        this.player2InMatch = player2InMatch;
    }

    public synchronized boolean isMatchEnd() {
        return matchEnd;
    }

    public synchronized void setMatchEnd(boolean matchEnd) {
        this.matchEnd = matchEnd;
    }

    public synchronized boolean isMatchStarted() {
        return matchStarted;
    }

    public synchronized void setMatchStarted(boolean matchStarted) {
        this.matchStarted = matchStarted;
    }

    public synchronized PlayerData getPlayer1() {
        return player1;
    }

    public synchronized void setPlayer1(PlayerData player1) {
        this.player1 = player1;
    }

    public synchronized boolean isPlayer1InMatch() {
        return player1InMatch;
    }

    public synchronized void setPlayer1InMatch(boolean player1InMatch) {
        this.player1InMatch = player1InMatch;
    }

    public synchronized PlayerData getPlayer2() {
        return player2;
    }

    public synchronized void setPlayer2(PlayerData player2) {
        this.player2 = player2;
    }

    public synchronized int getPunteggio1() {
        return punteggio1;
    }

    public synchronized void setPunteggio1(int punteggio1) {
        this.punteggio1 = punteggio1;
    }

    public synchronized int getPunteggio2() {
        return punteggio2;
    }

    public synchronized void setPunteggio2(int punteggio2) {
        this.punteggio2 = punteggio2;
    }

    public synchronized double getvXB() {
        return vXB;
    }

    public synchronized void setvXB(double vXB) {
        this.vXB = vXB;
    }

    public synchronized double getvXR1() {
        return vXR1;
    }

    public synchronized void setvXR1(double vXR1) {
        this.vXR1 = vXR1;
    }

    public synchronized double getvXR2() {
        return vXR2;
    }

    public synchronized void setvXR2(double vXR2) {
        this.vXR2 = vXR2;
    }

    public synchronized double getvYB() {
        return vYB;
    }

    public synchronized void setvYB(double vYB) {
        this.vYB = vYB;
    }

    public synchronized int getxB() {
        return xB;
    }

    public synchronized void setxB(int xB) {
        this.xB = xB;
    }

    public synchronized int getxR1() {
        return xR1;
    }

    public synchronized void setxR1(int xR1) {
        this.xR1 = xR1;
    }

    public synchronized int getxR2() {
        return xR2;
    }

    public synchronized void setxR2(int xR2) {
        this.xR2 = xR2;
    }

    public synchronized int getyB() {
        return yB;
    }

    public synchronized void setyB(int yB) {
        this.yB = yB;
    }

}
