package edu.ncsu.chord;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by amit on 22/2/17.
 */
public class ChordDriver {

//  /* Reference to node object */
//  private static Node node;

  /* Keep all loggers transient so that they are not passed over RMI call */
  private final transient static Logger logger = Logger.getLogger(ChordDriver.class);

  public static class NodeBuilder {
    public NodeBuilder() {}

    public NodeBuilder setBootStrapNodes(ArrayList<InetAddress> bootStrapNodes) {
      ChordConfig.bootstrapNodes = bootStrapNodes;
      return this;
    }

    public NodeBuilder setCHORD_ID_MAX_BITS(int max_bits) {
      ChordConfig.CHORD_ID_MAX_BITS = max_bits;
      return this;
    }

    public NodeBuilder setSUCCESSOR_LIST_MAX_SIZE(int max_size) {
      ChordConfig.SUCCESSOR_LIST_MAX_SIZE = max_size;
      return this;
    }

    public NodeBuilder setRegistryManager(RegistryManager rm) {
      ChordRMI.setRegistryManager(rm);
      ChordRMI.setRegistry(rm.getRegistry());
      return this;
    }

    public Node Build() throws RemoteException {
      InetAddress nodeIPAddress = NetworkUtils.getMyEthernetIP();

      Node node = new Node(nodeIPAddress,
                           ChordConfig.bootstrapNodes.contains(nodeIPAddress));
      return node;
    }
  }

  public static void joinChordNetwork(Node node) throws RemoteException {

    /* Export this object so that it is available for RMI calls */
    ChordRMI.exportNodeObjectRMI(node);

    logger.info("Node:" + node.getChordID() + "Joining network..");
    node.join(ChordConfig.bootstrapNodes);

    /* Fork the stabilize thread */
    Thread stabilizer = new Thread(new Stabilizer(node));
    stabilizer.start();
  }

}
