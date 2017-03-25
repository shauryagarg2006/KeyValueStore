package edu.ncsu.chord;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by amit on 22/2/17.
 */
class ChordRMI {

  /* Keep all loggers transient so that they are not passed over RMI call */
  private final transient static Logger logger = Logger.getLogger(ChordRMI.class);

  /* RMI regsitry on each Node */
  private static Registry registry;

  /* A RegistryManager object */
  private static RegistryManager registryManager;

  static void setRegistry(Registry registry) {
    ChordRMI.registry = registry;
  }

  static void setRegistryManager(RegistryManager rm) {
    ChordRMI.registryManager = rm;
  }

  static void exportNodeObjectRMI(Node n) {
    try {
      NodeOperations nops = (NodeOperations) UnicastRemoteObject.exportObject(n, 0);
      registry.rebind("NodeOperation", nops);
      logger.debug("Node " + n + " ready...");

      String entries[] = registry.list();
      logger.debug("Registry entries:");
      for (String binding : entries)
        logger.debug(binding);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static NodeOperations getRemoteNodeObject(InetAddress ip) {
    /* INetAddress toString adds a '/' at the beginning remove taht */
    String serverURL = "rmi://" + ip.toString().substring(1) + "/NodeOperation";
    NodeOperations nops = null;
    try {
      nops = (NodeOperations) Naming.lookup(serverURL);
      if (nops == null) {
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
        nops = (NodeOperations) Naming.lookup(serverURL);
      } catch (Exception nestedException) {
        nestedException.printStackTrace();
        nops = null;
      }
      e.printStackTrace();
    }
    return nops;
  }

}
