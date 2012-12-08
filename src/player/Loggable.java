package player;


import java.net.*;
import java.util.Date;

/** Interfaccia per operazioni di log effettuate da un server.
  */
public interface Loggable {

/** Partenza del server sulla port specificata, all'istante specificato.
  */
  public void srvStart(String message, int port, Date c);

/** Apertura connessione di un client, all'indirizzo, port, istante specificato.
  *  Torna un identificatore di connessione per l'utilizzo nella clnClose() e clnMessage().
  */
  public int clnOpen(InetAddress a, int port, Date c);

/** Chiusura connessione specificata, all'istante specificato.
  */
  public void clnClose(int id, Date d);

/** Eccezione sollevata durante la connessione specificata.
  */
  public void clnException(int id, String msg);

/** Messaggio generico da parte del server.
  */
  public void srvMessage(String msg);

  // I metodi init() e shutdown() non sarebbero necessari.
  // Ma nella pratica conviene metterceli. Se una implementazione non ne
  //  ha bisogno, avr� metodi che non fanno nulla.
  public void init();
  public void shutdown();

  }
