package edu.ncsu.client;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import edu.ncsu.chord.ChordID;
import edu.ncsu.store.StoreClientAPI;

/**
 * Created by amit on 8/4/17.
 */
public class KeyLocationTest {
  /* Keep all loggers transient so that they are not passed over RMI call */
    private final transient static Logger logger = Logger.getLogger(KeyLocationTest.class);

    /* Sorted list of all active nodes */
    ArrayList<ChordID<InetAddress>> sortedNodeIdList;

    /* list of IP addresses of all nodes */
    ArrayList<InetAddress> ipList;

    @Test
    public void testKeyLocations() {
      /* Ask each node to dump its keySet and then verify that key set against Node list */

      /* Each node loop */
      for (int i = 0; i < sortedNodeIdList.size(); i++) {
        ChordID<InetAddress> prevNodeID = null;
        ChordID<InetAddress> currNodeID = sortedNodeIdList.get(i);
	/* Get Client for this node */
        StoreClientAPI clientAPI = ClientRMIUtils.getRemoteClient(currNodeID.getKey());
        Set<String> keys = null;
        try {
          keys = clientAPI.keySet();
          prevNodeID = (i == 0) ? sortedNodeIdList.get(sortedNodeIdList.size() - 1) : sortedNodeIdList.get(i - 1);
          for (String s : keys) {
            ChordID<String> chordKey = new ChordID<>(s);
            if (!chordKey.inRange(prevNodeID, currNodeID, false, true)) {
              logger.error("Key: " + chordKey + " Should not be present on node " + currNodeID);
            }
          }
        } catch (RemoteException e) {
          e.printStackTrace();
        }
        logger.info("Total keys stored on Node " + currNodeID + " = " + keys.size());
      }
    }

    @Before
    public void initialize() {
      String inputPath = "/root/KeyValueStore/node.list";
      logger.debug("Initializing KeyLocationTest....");
      /* Assuming there is a file at /root/KeyValueStore/node.list which has IP address of all nodes 1 per line */
      ipList = new ArrayList<>();
      sortedNodeIdList = new ArrayList<>();
      try {
        FileReader fr = new FileReader(inputPath);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
	  sortedNodeIdList.add(new ChordID<>(InetAddress.getByName(line)));
	  ipList.add(InetAddress.getByName(line));
        }
	Collections.sort(sortedNodeIdList);
	logger.debug("Active nodes: " + sortedNodeIdList);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

}
