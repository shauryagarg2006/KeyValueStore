package edu.ncsu.store;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
        logger.info("Key : "+keyChordID+" Responsible Node : "+responsibleNode);
        return StoreRMI.getRemoteObjectStore(responsibleNode.getKey());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  String get(String key) {
    try {
      /* Convert this string value into chord interpretable ChordID */
      return getObjectStore(key).getObject(new ChordID<String>(key));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  boolean put(String key, String value) {
    try {
      ObjectStore obStore=  getObjectStore(key);
      return obStore.putObject(new ChordID<String>(key), value);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public String getObject(ChordID<String> key) {
    if (localStorage.containsKey(key)) {
      return localStorage.get(key);
    } else {
      return null;
    }
  }

  @Override
  public boolean putObject(ChordID<String> key, String value) {
    localStorage.put(key.getKey(), value);
    	try {
			logger.info("Key Stored Successfully at Node -- " + node.getChordID() + "--" + key + "--" + value);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    return true;
  }

  @Override
  public boolean verifyKeys(ArrayList<ChordID<InetAddress>> nodeIDList) {
    boolean result = true;
    if (node == null) {
      logger.error("verify Keys is a test method. Must be run with join network option value set to true!");
      return false;
    }
    for (Map.Entry<String, String> e : localStorage.entrySet()) {
      ChordID<String> key = new ChordID<>(e.getKey());
      int i;
      for (i = 0; i < nodeIDList.size(); i++) {
        if (nodeIDList.get(i).compareTo(key) >= 0)
          break;
      }
      try {
        if (i == nodeIDList.size())
          i = 0;
        if (!nodeIDList.get(i).equals(node.getChordID())) {
          logger.error("Key: " + e.getKey() + " has chordID " + key +
                       " It is stored on " + node.getChordID() +
                       " But should be stored on " + nodeIDList.get(i));
          result = result & false;
        }
        logger.info("Total keys stored on " + node.getChordID() + " : " + localStorage.size());
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }

    return result;
  }
}
