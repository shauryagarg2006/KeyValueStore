package edu.ncsu.store;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;

import edu.ncsu.chord.ChordID;

/**
 * Created by amit on 24/3/17.
 */
public class StoreDriver {

  private ObjectServer objectServer;

  /* Keep all loggers transient so that they are not passed over RMI call */
  private final transient static Logger logger = Logger.getLogger(StoreDriver.class);

  public StoreDriver(ArrayList<InetAddress> bootstrapNodes,
		     boolean joinNetwork) {
    objectServer = new ObjectServer(bootstrapNodes, joinNetwork);
    /* Initialize RMI object */
    StoreRMI.setRegistryManager(new RegistryManagerImpl());
    StoreRMI.setRegistry(new RegistryManagerImpl().getRegistry());
    /* Export ObjectStore to be available via RMI */
    StoreRMI.exportStoreObjectRMI(objectServer);

  }

  public void put(String key, String value) {
    objectServer.put(key, value);
  }

  public String get(String key) {
    return objectServer.get(key);
  }

  public void verifyKeys(ArrayList<InetAddress> allNodes) {
    logger.debug("Verify keys method initiated..");
    // convert all IP addresses to Chord IDs
    ArrayList<ChordID<InetAddress>> nodeIDList = new ArrayList<>();
    for (InetAddress ip : allNodes) {
      nodeIDList.add(new ChordID<InetAddress>(ip));
    }
    Collections.sort(nodeIDList);
    try {
      // call verify on each node
      for (InetAddress ip : allNodes) {
        ObjectStore s = StoreRMI.getRemoteObjectStore(ip);
        if (s.verifyKeys(nodeIDList)) {
          logger.info("All keys are stored on correct nodes!");
        } else {
          logger.error("Node: " + new ChordID<InetAddress>(ip) + " has some errors ");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
