package edu.ncsu.chord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by amit on 21/3/17.
 */


public class NodeTest {

  /* Input file path */
  String inputPath = "/root/KeyValueStore/node.list";

  /* Keep all loggers transient so that they are not passed over RMI call */
  private final transient static Logger logger = Logger.getLogger(NodeTest.class);

  /* Sorted list of all active nodes */
  ArrayList<NodeInfo> sortedNodeInfoList;

  /* list of IP addresses of all nodes */
  ArrayList<InetAddress> ipList;

  /* ChordID of this tester node */
  ChordID<InetAddress> testerChordID;


  //TODO: In all these tests I am deliberately assuming all ID values are of long data type.
  //If SHA256Hash is modified to use BigInteger then this code will break. Assuming long data type
  //allows to verify fingerTable without calling methods written in Node class.



  private void verify(String message, boolean condition) {
    /* Print message in logs as well */
    logger.info(message);
    assertTrue(message, condition);
  }

  private ChordID<InetAddress> getClosestNodeIndex(long value) {
    int i;
    for (i = 0; i < sortedNodeInfoList.size(); i++) {
      ChordID<InetAddress> node = sortedNodeInfoList.get(i).selfChordID;
      long nodeID = Long.parseLong(node.getValue());
      if (nodeID >= value)
        break;
    }
    /* Wrap around for mode */
    return getListNode(i);
  }

  private ChordID<InetAddress> getListNode(int index) {
    if (index < 0)
      index += sortedNodeInfoList.size();
    return sortedNodeInfoList.get(index % sortedNodeInfoList.size()).selfChordID;
  }


  private void verifyFingerTable(NodeInfo ninfo) {
    logger.info("Verification for node " + ninfo.selfChordID);

    /* verify predecessor of node */
    verify("Predecessor: " + ninfo.predecessor +
               " Actual predecessor: " + getListNode(ninfo.sortedListIndex - 1),
               getListNode(ninfo.sortedListIndex - 1).equals(ninfo.predecessor));

    /* verify finger table of node */
    for (int i = 0; i < FingerTable.MAX_SIZE; i++) {
      FingerTableEntry entry = ninfo.fingerTable.getEntry(i);
      long rangeStart = Long.parseLong(entry.hashRangeStart.getValue());
      verify("Responsible node: " + entry.responsibleNodeID +
                   " Actual node: " + getClosestNodeIndex(rangeStart),
                   getClosestNodeIndex(rangeStart).equals(entry.responsibleNodeID));
    }

    /* Verify successor list */
    for (int i = 0; i < ninfo.successorList.size(); i++) {
      verify("Successor node at " + i + " " + ninfo.successorList.get(i) +
                   " Actual nth successor : " + getListNode(ninfo.sortedListIndex + i + 1),
                   getListNode(ninfo.sortedListIndex + i + 1).equals(ninfo.successorList.get(i)));
    }
  }


  /* Given an arraylist of currently active nodes in the network this method will
  do RMI calls on each node to get the finger table of that node and verify it. */
  @Test
  public void testTopology()  {
    /* Gather nodeInfo objects */
    try {
      for (int i = 0; i < ipList.size(); i++) {
        NodeOperations nodeROR = ChordRMI.getRemoteNodeObject(ipList.get(i));
        NodeInfo ninfo = nodeROR.getNodeInfo(testerChordID);
        sortedNodeInfoList.add(ninfo);
      }

      Collections.sort(sortedNodeInfoList, new Comparator<NodeInfo>() {
        @Override
        public int compare(NodeInfo o1, NodeInfo o2) {
          return o1.selfChordID.compareTo(o2.selfChordID);
        }
      });

      logger.info("Tester node is " + testerChordID);
      logger.info("Starting Tests...");

    /* Get finger table for each node */
      for (int i = 0; i < sortedNodeInfoList.size(); i++) {
        sortedNodeInfoList.get(i).sortedListIndex = i;
        sortedNodeInfoList.get(i).idLongValue = Long.parseLong(sortedNodeInfoList.get(i).selfChordID.getValue());
        verifyFingerTable(sortedNodeInfoList.get(i));
        System.out.println("Finger table for node " + sortedNodeInfoList.get(i).idLongValue + " verified!");
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }


  @Before
  public void initialize() {
    logger.debug("Initializing tests..");
    /* Assuming there is a file at /tmp/node.list which has IP address of all nodes 1 per line */
    ipList = new ArrayList<>();

    try {
      FileReader fr = new FileReader(inputPath);
      BufferedReader br = new BufferedReader(fr);
      String line;
      while ((line = br.readLine()) != null) {
        logger.info("Got active node @ " + line);
        ipList.add(InetAddress.getByName(line));
      }

      sortedNodeInfoList = new ArrayList<>();
      testerChordID = new ChordID<InetAddress>(NetworkUtils.getMyEthernetIP());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}