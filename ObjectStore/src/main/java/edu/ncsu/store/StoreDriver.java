package edu.ncsu.store;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by amit on 24/3/17.
 */
public class StoreDriver {

  private ObjectServer objectServer;

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

}
