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
public class PlaygroundData implements Writable{
    private InetAddress ip;
    private int portEnqueuing;
    private int portPlaying;
    private int w;
    private int h;
    private double k;
    private double kR;
    private long lastContactTime = 0;
    private int queueLength = 0;

    private int wR = 100;     //width racchetta
    private int pMax = 10;   //Punteggio max = vittoria
    private double t = 0.01;      //Cost di tempo ( in sec)
    private double aR = 1000;  //accelerazione racchetta        //(default era 100)
    private double viR = 500; //vel iniziale racchetta
    private double vip = 200; //vel iniziale palla

    @Override
    public String toString() {
        return ip.getHostAddress() + " " + portEnqueuing + " " + portPlaying + " " + w + " " + h + " " + k + " " + kR +
                " " + lastContactTime + " " + queueLength;
    }

    public long getLastContactTime() {
        return lastContactTime;
    }

    public void setLastContactTime(long lastContactTime) {
        this.lastContactTime = lastContactTime;
    }

    public int getQueueLength() {
        return queueLength;
    }

    public void setQueueLength(int queueLength) {
        this.queueLength = queueLength;
    }

    public double getaR() {
        return aR;
    }

    public void setaR(double aR) {
        this.aR = aR;
    }

    public int getpMax() {
        return pMax;
    }

    public void setpMax(int pMax) {
        this.pMax = pMax;
    }

    public double getT() {
        return t;
    }

    public void setT(double  t) {
        this.t = t;
    }

    public double getViR() {
        return viR;
    }

    public void setViR(double viR) {
        this.viR = viR;
    }

    public double getVip() {
        return vip;
    }

    public void setVip(double vip) {
        this.vip = vip;
    }

    public int getwR() {
        return wR;
    }

    public void setwR(int wR) {
        this.wR = wR;
    }
    
   // private long lastContactTime;

      public PlaygroundData(InetAddress ip, int portEnqueuing, int portPlaying, int w, int h, double k, double kR) {
        this.ip = ip;
        this.portEnqueuing = portEnqueuing;
        this.portPlaying = portPlaying;
        this.w = w;
        this.h = h;
        this.k = k;
        this.kR = kR;
    }

   // public PlaygroundData(int w, int h, double k, double kR, long lastContactTime) {
    public PlaygroundData(int w, int h, double k, double kR) {
        this.w = w;
        this.h = h;
        this.k = k;
        this.kR = kR;
        //this.lastContactTime = lastContactTime;
    }

    public PlaygroundData() {
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
        if (this.ip != other.ip && (this.ip == null || !this.ip.equals(other.ip))) {
            return false;
        }
        if (this.portEnqueuing != other.portEnqueuing) {
            return false;
        }
        if (this.portPlaying != other.portPlaying) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.ip != null ? this.ip.hashCode() : 0);
        hash = 97 * hash + this.portEnqueuing;
        hash = 97 * hash + this.portPlaying;
        return hash;
    }
    
    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public int getPortEnqueuing() {
        return portEnqueuing;
    }

    public void setPortEnqueuing(int portEnqueuing) {
        this.portEnqueuing = portEnqueuing;
    }

    public int getPortPlaying() {
        return portPlaying;
    }

    public void setPortPlaying(int portPlaying) {
        this.portPlaying = portPlaying;
    }


    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public double getkR() {
        return kR;
    }

    public void setkR(double kR) {
        this.kR = kR;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

//    public int compareTo(Object aThat) {
//        final int BEFORE = 1;
//        final int EQUAL = 0;
//        final int AFTER = -1;
//
//        //this optimization is usually worthwhile, and can
//        //always be added
//        if (aThat == null) {
//            throw new NullPointerException();
//            //return false;
//        }
//        if (getClass() != aThat.getClass()) {
//            throw new ClassCastException();
//        }
//
//        PlaygroundData that = (PlaygroundData) aThat;
//
//        if ( this == that ) return EQUAL;
//
//        //primitive numbers follow this form
//        if (this.queueLength < that.queueLength) return BEFORE;
//        if (this.queueLength > that.queueLength) return AFTER;
//        return EQUAL;
//    }

}
