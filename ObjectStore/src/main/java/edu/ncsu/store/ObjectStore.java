package edu.ncsu.store;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by amit on 24/3/17.
 */
public interface ObjectStore extends Remote {
  String getObject(String key) throws RemoteException;

  boolean putObject(String key, String value) throws RemoteException;
}
