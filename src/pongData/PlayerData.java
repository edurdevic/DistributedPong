/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pongData;

import playground.Match;
import java.net.Socket;

/**
 *
 * @author Erni
 */
public class PlayerData {
    private String name;
    private String token;
    private long lastContactTime;
    private Socket socketPlayer = null;
    private playground.Match myMatch= null;

    public PlayerData (String name, String token, long lastContactTime) {
        this.name = name;
        this.token = token;
        this.lastContactTime = lastContactTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PlayerData other = (PlayerData) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.token == null) ? (other.token != null) : !this.token.equals(other.token)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 73 * hash + (this.token != null ? this.token.hashCode() : 0);
        return hash;
    }

    public Match getMyMatch() {
        return myMatch;
    }

    public void setMyMatch(Match myMatch) {
        this.myMatch = myMatch;
    }

    public long getLastContactTime() {
        return lastContactTime;
    }

    public void setLastContactTime(long lastContactTime) {
        this.lastContactTime = lastContactTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Socket getSocketPlayer() {
        return socketPlayer;
    }

    public void setSocketPlayer(Socket socketPlayer) {
        this.socketPlayer = socketPlayer;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
}
