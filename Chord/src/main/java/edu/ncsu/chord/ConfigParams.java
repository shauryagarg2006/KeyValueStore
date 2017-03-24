package edu.ncsu.chord;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by amit on 24/3/17.
 */
public interface ConfigParams {
  /* Depending on CHORD_ID_MAX_BITS in each ChordID will be calculated. This is also used in finger table */
  int getCHORD_ID_MAX_BITS();


  int getSUCCESSOR_LIST_MAX_SIZE();

  /* ArrayList of IPs of all bootstrap nodes */
  ArrayList<InetAddress> getbootstrapNodes();

  /* Get registry manager */
  RegistryManager getRegistryManager();
}
