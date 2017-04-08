package edu.ncsu.store;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * Created by amit on 1/4/17.
 */
public interface StoreClientAPI extends Remote {

  Object get(String key) throws RemoteException;

  void put(String key, Object value) throws RemoteException;

  void delete(String key) throws RemoteException;

  Set<String> keySet() throws RemoteException;
}