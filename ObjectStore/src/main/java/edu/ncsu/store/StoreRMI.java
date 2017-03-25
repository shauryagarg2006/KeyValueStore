package edu.ncsu.store;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import edu.ncsu.chord.RegistryManager;

/**
 * Created by amit on 24/3/17.
 */
public class StoreRMI {

  /* Keep all loggers transient so that they are not passed over RMI call */
  private final transient static Logger logger = Logger.getLogger(StoreRMI.class);

  /* RMI regsitry on each Node */
  private static Registry registry;

  /* A RegistryManager object */
  private static RegistryManager registryManager;

  static void setRegistry(Registry registry) {
    StoreRMI.registry = registry;
  }

  static void setRegistryManager(RegistryManager rm) {
    StoreRMI.registryManager = rm;
  }

  static void exportStoreObjectRMI(ObjectServer server) {
    try {
      ObjectStore store = (ObjectStore) UnicastRemoteObject.exportObject(server, 0);
      registry.rebind("ObjectStore", store);

      String entries[] = registry.list();
      logger.debug("Registry entries:");
      for (String binding : entries)
	logger.debug(binding);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static ObjectStore getRemoteObjectStore(InetAddress ip) {
    /* INetAddress toString adds a '/' at the beginning remove taht */
    String serverURL = "rmi://" + ip.toString().substring(1) + "/ObjectStore";
    ObjectStore store = null;
    try {
      store = (ObjectStore) Naming.lookup(serverURL);
      if (store == null) {
	throw new RemoteException();
      }
    } catch (Exception e) {
      /* TODO: Need to do all RMI exception handling
      Why did RMI execution fail
      Node down? Node failure? Network partitioning? New node arrival?
       */
      e.printStackTrace();
      logger.error("Unable to get Remote object for " + ip + " Trying one more time...");
      /* Try one more time before giving up */
      try {
	store = (ObjectStore) Naming.lookup(serverURL);
      } catch (Exception nestedException) {
	nestedException.printStackTrace();
	store = null;
      }
      e.printStackTrace();
    }
    return store;
  }

}
