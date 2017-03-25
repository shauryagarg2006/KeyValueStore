package edu.ncsu.store;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.ncsu.chord.ChordDriver;
import edu.ncsu.chord.ChordID;
import edu.ncsu.chord.Node;
import edu.ncsu.chord.NodeOperations;


/**
 * Created by amit on 24/3/17.
 */
public class ObjectServer implements ObjectStore {

  Map<String, String> localStorage;

  /* Reference to underlying chord node - only if client wants to join as a node */
  NodeOperations node = null;

  /* Reference to remote chord nodes Object store - only if client refuses to join as a node */
  ObjectStore remoteObjectStore = null;

  /* Keep all loggers transient so that they are not passed over RMI call */
  private final transient static Logger logger = Logger.getLogger(ObjectServer.class);

  public ObjectServer(ArrayList<InetAddress> bootstrapNodes,
                      boolean joinNetwork) {
    try {
      localStorage = new HashMap<>();
      if (joinNetwork) {
        node = joinNetwork(bootstrapNodes);
      } else {
        for (int i = 0; i < bootstrapNodes.size() && node == null; i++) {
          remoteObjectStore = StoreRMI.getRemoteObjectStore(bootstrapNodes.get(i));
        }
        if (remoteObjectStore == null) {
          logger.error("Unable to connect to bootstrap Object Store. Exiting ");
          System.exit(0);
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private NodeOperations joinNetwork(ArrayList<InetAddress> bootstrapNodeList) throws RemoteException {
    Node n = new ChordDriver.NodeBuilder()
               .setRegistryManager(new RegistryManagerImpl())
               .setBootStrapNodes(bootstrapNodeList)
               .setCHORD_ID_MAX_BITS(10)
               .setSUCCESSOR_LIST_MAX_SIZE(3)
               .Build();

    ChordDriver.joinChordNetwork(n);

    return n;
  }

  private ObjectStore getObjectStore(String key) {
    if (node == null) {
      return remoteObjectStore;
    } else {
      try {
        ChordID<String> keyChordID = new ChordID<>(key);
        ChordID<InetAddress> responsibleNode = node.getSuccessor(node.getChordID(), keyChordID);
        return StoreRMI.getRemoteObjectStore(responsibleNode.getKey());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  String get(String key) {
    return getObjectStore(key).getObject(key);
  }

  boolean put(String key, String value) {
    return getObjectStore(key).putObject(key, value);
  }

  @Override
  public String getObject(String key) {
    if (localStorage.containsKey(key)) {
      return localStorage.get(key);
    } else {
      return null;
    }
  }

  @Override
  public boolean putObject(String key, String value) {
    localStorage.put(key, value);
    return true;
  }
}
