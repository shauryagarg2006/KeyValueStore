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

import javax.xml.crypto.Data;

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

//  @Override
//  public boolean putObject(ChordID<String> key, DataContainer value) throws RemoteException {
//    try {
//      logger.info("Creating first copy of " + key +
//                  " on Node: " + ObjectStoreService.getChordSession().getChordNodeID());
//      Map<ChordID<String>, DataContainer> replicaData = new HashMap<>();
//      replicaData.put(key, value);
//      makeReplicas(replicaData);
//      localStorage.put(key.getKey(), value);
//    } catch (Exception e) {
//      e.printStackTrace();
//      return false;
//    }
//    return true;
//  }

  @Override
  public boolean putObjects(Map<ChordID<String>, DataContainer> keyValueMap) throws RemoteException {
    boolean result = true;
    makeReplicas(keyValueMap);
//    for (Map.Entry<ChordID<String>, DataContainer> e : keyValueMap.entrySet()) {
//      try {
//        localStorage.put(e.getKey().getKey(), e.getValue());
//      } catch (Exception ex) {
//        ex.printStackTrace();
//      }
//      //result &= putObject(e.getKey(), e.getValue());
//    }
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
  public boolean makeReplicas(Map<ChordID<String>, DataContainer> replicaData) throws RemoteException {
    /* ReplicaData is set of keys that needs to be replicated. However for few of them this node
    might be the last copy node. So separate those keys from the keys that needs to be passed further
     */
    Map<ChordID<String>, DataContainer> furtherPassedKeys = new HashMap<>();
    for (ChordID<String> s : replicaData.keySet()) {
      DataContainer valueContainer = replicaData.get(s);
      if ((valueContainer.replicaNumber + 1) <= StoreConfig.REPLICATION_COUNT) {
        furtherPassedKeys.put(s, new DataContainer(valueContainer.value, valueContainer.replicaNumber + 1));
      }
    }

    if (furtherPassedKeys.size() > 0) {
      ChordID<InetAddress> successorChordID = ObjectStoreService.getChordSession().getSelfSuccessor();
      ObjectStoreOperations successorStore = StoreRMIUtils.getRemoteObjectStore(successorChordID.getKey());
      successorStore.makeReplicas(furtherPassedKeys);
    }

    try {
      for (Map.Entry<ChordID<String>, DataContainer> e : replicaData.entrySet()) {
        logger.info("Putting key :" + e.getKey() + " value: " + e.getValue() +
                     " into " + ObjectStoreService.getChordSession().getChordNodeID());
        localStorage.put(e.getKey().getKey(), e.getValue());
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
    return true;
  }

  @Override
  public boolean removeReplicas(Map<ChordID<String>, DataContainer> replicaData) throws RemoteException {
    return false;
  }

  /* this method is written only for testing purposes */
  public HashMap<String, DataContainer> dumpStore() {
    return localStorage.dumpStorage();
  }

}
