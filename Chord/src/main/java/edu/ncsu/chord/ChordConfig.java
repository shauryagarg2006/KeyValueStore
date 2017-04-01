package edu.ncsu.chord;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by amit on 23/2/17.
 */
class ChordConfig {

  /* Depending on CHORD_ID_MAX_BITS in each ChordID will be calculated. This is also used in finger table */
  static int CHORD_ID_MAX_BITS = 10;

  /* Number of maximum entries to keep in successor list */
  static int SUCCESSOR_LIST_MAX_SIZE = 3;

  /* ArrayList of IPs of all bootstrap nodes */
  static ArrayList<InetAddress> bootstrapNodes;

  static {
    bootstrapNodes = new ArrayList<>();
    try {
      bootstrapNodes.add(InetAddress.getByName("172.17.0.2"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /* RMI Registry Port */
  static int RMI_REGISTRY_PORT = 1099;

  /* Network interface to be used for communication */
  static String NetworkInterface = "eth0";
}
