package edu.ncsu.store;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.ncsu.chord.ChordDriver;
import edu.ncsu.chord.ChordID;
import edu.ncsu.chord.ChordOperations;
import edu.ncsu.chord.ChordSession;

/**
 * Created by amit on 24/3/17.
 */
class ObjectStore implements ObjectStoreOperations {

  private LocalStorage localStorage;


  /* Keep all loggers transient so that they are not passed over RMI call */
  private final transient static Logger logger = Logger.getLogger(ObjectStore.class);

  public ObjectStore() {
    localStorage = new HashStorage();
  }

  @Override
  public DataContainer getObject(ChordID<String> key) throws RemoteException {
    if (!localStorage.containsKey(key.getKey())) {
      return null;
    } else {
      return localStorage.get(key.getKey());
    }
  }

  @Override
  public boolean putObject(ChordID<String> key, DataContainer value) throws RemoteException {
    try {
      logger.info("Creating first copy of " + key +
                  " on Node: " + ObjectStoreService.getChordSession().getChordNodeID());
      makeReplicas(key.getKey(), value);
      localStorage.put(key.getKey(), value);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @Override
  public boolean putObjects(Map<ChordID<String>, DataContainer> keyValueMap) throws RemoteException {
    boolean result = true;
    for (Map.Entry<ChordID<String>, DataContainer> e : keyValueMap.entrySet()) {
      result &= putObject(e.getKey(), e.getValue());
    }
    logger.info("Accepted " + keyValueMap.size() + " new keys");
    return result;
  }

  @Override
  public boolean delete(ChordID<String> key) throws RemoteException {
    localStorage.delete(key.getKey());
    return true;
  }

  @Override
  public boolean deleteKeys(ArrayList<ChordID<String>> key) throws RemoteException {
    for (ChordID<String> k : key) {
      delete(k);
    }
    return true;
  }

  @Override
  public boolean makeReplicas(String key, DataContainer value) throws RemoteException {
    if ((value.replicaNumber + 1) <= StoreConfig.REPLICATION_COUNT) {
      ChordID<InetAddress> successorChordID = ObjectStoreService.getChordSession().getSelfSuccessor();
      ObjectStoreOperations successorStore = StoreRMIUtils.getRemoteObjectStore(successorChordID.getKey());
      successorStore.makeReplicas(key, new DataContainer(value.value, value.replicaNumber + 1));
      logger.info("Creating " + value.replicaNumber + "th copy of " +
                  key + " on Node: " + ObjectStoreService.getChordSession().getChordNodeID());
    }
    try {
      localStorage.put(key, value);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @Override
  public boolean removeReplicas(String key, DataContainer value) throws RemoteException {
    return false;
  }

  /* this method is written only for testing purposes */
  public ArrayList<String> keySet() {
    return new ArrayList<String>(localStorage.keySet());
  }

}
