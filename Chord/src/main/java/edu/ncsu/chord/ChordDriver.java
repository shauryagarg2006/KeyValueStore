package edu.ncsu.chord;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by amit on 22/2/17.
 */
public class ChordDriver {

 /* Reference to singleton Session object */
 private static ChordSession session = null;

  public synchronized static ChordSession getSession() {
    if (session == null) {
      session = new ChordSession();
    }
    return session;
  }


}
