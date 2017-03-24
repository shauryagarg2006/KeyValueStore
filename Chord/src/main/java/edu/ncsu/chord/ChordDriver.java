package edu.ncsu.chord;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.rmi.RemoteException;

/**
 * Created by amit on 22/2/17.
 */
public class ChordDriver {

  /* Reference to node object */
  private static Node node;

  /* Keep all loggers transient so that they are not passed over RMI call */
  private final transient static Logger logger = Logger.getLogger(ChordDriver.class);

  public static void initialize(ConfigParams config) throws RemoteException {

    /* Set configuration parameters passed by the user */
    ChordConfig.bootstrapNodes = config.getbootstrapNodes();
    ChordConfig.CHORD_ID_MAX_BITS = config.getCHORD_ID_MAX_BITS();
    ChordConfig.SUCCESSOR_LIST_MAX_SIZE = config.getSUCCESSOR_LIST_MAX_SIZE();
    RMIUtils.setRegistry(config.getRegistryManager().getRegistry());

    InetAddress selfIPAddress = NetworkUtils.getMyEthernetIP();

    /* Create object for this node */
    node = new Node(selfIPAddress, ChordConfig.bootstrapNodes.contains(selfIPAddress));

    /* Export this object so that it is available for RMI calls */
    RMIUtils.exportNodeObjectRMI(node);

  }


  public static void joinChordNetwork() throws RemoteException {

    node.join(ChordConfig.bootstrapNodes);

    /* Fork the stabilize thread */
    Thread stabilizer = new Thread(new Stabilizer(node));
    stabilizer.start();
  }

}
