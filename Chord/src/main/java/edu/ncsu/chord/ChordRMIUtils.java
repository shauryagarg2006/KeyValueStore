package edu.ncsu.chord;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by amit on 22/2/17.
 */
class ChordRMIUtils {

  /* Keep all loggers transient so that they are not passed over RMI call */
  private final transient static Logger logger = Logger.getLogger(ChordRMIUtils.class);

  private static Registry getRegistry() {
    Registry registry = null;

    try {
      registry = LocateRegistry.getRegistry();
    } catch (RemoteException e) {
      try {
        registry = LocateRegistry.createRegistry(ChordConfig.RMI_REGISTRY_PORT);
      } catch (RemoteException nestedException) {
        nestedException.printStackTrace();
        logger.error("Unable to get/create RMI registry. Exiting");
      }
    }
    return registry;
  }

  static boolean exportNodeObjectRMI(ChordNode n) {
    Registry registry = getRegistry();

    try {
      ChordOperations nops = (ChordOperations) UnicastRemoteObject.exportObject(n, 0);
      registry.rebind("ChordOperation", nops);
      String entries[] = registry.list();
      logger.debug("Registry entries:");
      for (String binding : entries) {
        logger.debug(binding);
      }

    } catch (Exception e) {
      logger.error("Unable to export object");
      e.printStackTrace();
      return false;
    }

    return true;
  }

  static ChordOperations getRemoteNodeObject(InetAddress ip) {
    /* INetAddress toString adds a '/' at the beginning remove that */
    String serverURL = "rmi://" + ip.toString().substring(1) + "/ChordOperation";
    ChordOperations nops;
    try {
      nops = (ChordOperations) Naming.lookup(serverURL);
      if (nops == null) {
	throw new RemoteException();
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("Unable to get Remote object for " + ip + " Trying one more time...");
      /* Try one more time before giving up */
      try {
	nops = (ChordOperations) Naming.lookup(serverURL);
      } catch (Exception nestedException) {
	nestedException.printStackTrace();
	nops = null;
      }
      e.printStackTrace();
    }
    return nops;
  }

}
