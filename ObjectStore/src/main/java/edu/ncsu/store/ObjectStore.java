package edu.ncsu.store;

import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import edu.ncsu.chord.ChordID;

/**
 * Created by amit on 24/3/17.
 */
public interface ObjectStore extends Remote {
  String getObject(ChordID<String> key) throws RemoteException;

  boolean putObject(ChordID<String> key, String value) throws RemoteException;

  boolean verifyKeys(ArrayList<ChordID<InetAddress>> nodeIDList) throws RemoteException;
}
