package edu.ncsu.client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.ncsu.chord.ChordID;
import edu.ncsu.chord.SHA256Hash;
import edu.ncsu.store.DataContainer;
import edu.ncsu.store.StoreClientAPI;
import edu.ncsu.store.StoreConfig;

/**
 * Created by amit on 24/3/17.
 */
public class Client {

  private final transient static Logger logger = Logger.getLogger(Client.class);

  /* All keys-values used for get & put will be taken & verified from below file */
  private String inputDataPath = "/root/KeyValueStore/Resources/tests/input_keys";
  private String ipListPath = "/root/KeyValueStore/Resources/tests/node_ip_list";

  HashMap<String, String> keyValueMap;
  ArrayList<ChordID<InetAddress>> ipList;
  HashMap<ChordID<InetAddress>, HashMap<String, DataContainer>> nodeDataMap;

  public Client(int nKeys) {
    keyValueMap = new HashMap<>();
    try (FileReader fr = new FileReader(inputDataPath);
         BufferedReader br = new BufferedReader(fr);) {
      String line;
      while ((line = br.readLine()) != null && keyValueMap.size() < nKeys) {
        String data[] = line.split("\\$");
        keyValueMap.put(data[1], data[2]);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    ipList = new ArrayList<>();
    try {
      FileReader fr = new FileReader(ipListPath);
      BufferedReader br = new BufferedReader(fr);
      String line;
      while ((line = br.readLine()) != null) {
        ipList.add(new ChordID<>(InetAddress.getByName(line)));
      }
      Collections.sort(ipList);
      logger.debug("Active nodes: " + ipList);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void putKeys() {
    StoreClientAPI handle = ClientRMIUtils.getRemoteClient();
    Double average_time = 0D;
    try {
      for (Map.Entry<String, String> e : keyValueMap.entrySet()) {
        long before_time = System.currentTimeMillis();
        handle.put(e.getKey(), e.getValue());
        long after_time = System.currentTimeMillis();
        //logger.debug("Time Taken to put key : " + (after_time - before_time));
        average_time += after_time - before_time;
        }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    logger.info("Average Time Taken to put key : " + (average_time / keyValueMap.size()) + " ms For "
                + keyValueMap.size() + " keys");
    System.out.println("Average Time Taken to put key : " + (average_time / keyValueMap.size()) + " ms For "
                       + keyValueMap.size() + " keys");

  }

  public void getKeys() {
    StoreClientAPI handle = ClientRMIUtils.getRemoteClient();
    Double average_time = 0D;
    try {
      for (Map.Entry<String, String> e : keyValueMap.entrySet()) {
        long before_time = System.currentTimeMillis();
        String retrievedValue = (String) handle.get(e.getKey());
        long after_time = System.currentTimeMillis();
        if (retrievedValue == null || !retrievedValue.equals(e.getValue())) {
          logger.error("Value for Key: " + e.getKey() + " Could not be found.");
        }
        //logger.debug("Time Taken to get key : " + (after_time - before_time));
        average_time += after_time - before_time;
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    logger.info("Average Time Taken to get key : " + (average_time / keyValueMap.size()) + " ms For "
                + keyValueMap.size() + " keys");
    System.out.println("Average Time Taken to get key : " + (average_time / keyValueMap.size()) + " ms For "
                       + keyValueMap.size() + " keys");
  }

  private ChordID<InetAddress> getResponsibleNode(ChordID<String> key, int replicaNumber) {
    int i = 0;
    while (i < ipList.size() && ipList.get(i).compareTo(key) < 0) i++;
    if (i == ipList.size())
      i = 0;
    i = (i + replicaNumber - 1) % ipList.size();
    return ipList.get(i);
  }

  private boolean verifyKeyLocation(ChordID<String> chordKey) {
    ChordID<InetAddress> responsibleNode = null;
    boolean result = true;
    for (int i = 1; i <= StoreConfig.REPLICATION_COUNT; i++) {
      responsibleNode = getResponsibleNode(chordKey, i);
      if (!nodeDataMap.get(responsibleNode).containsKey(chordKey.getKey())) {
        logger.error("Key: " + chordKey  + " (Replica: "+ i + ")  Not found on " + responsibleNode);
        result = false;
      } else {
        DataContainer c = nodeDataMap.get(responsibleNode).get(chordKey.getKey());
        if (c.replicaNumber != i) {
          logger.error("Key: " + chordKey  + " Replica should be: "+ i + " found: " +
                       c.replicaNumber + " on node " + responsibleNode);
        }
        result = false;
      }
    }
    return result;
  }

  public void testKeys() {
    nodeDataMap = new HashMap<>();
    for (ChordID<InetAddress> nodeID : ipList) {
      StoreClientAPI handle = ClientRMIUtils.getRemoteClient(nodeID.getKey());
      try {
        HashMap<String, DataContainer> nodeStoreDump = handle.dumpStore();
        nodeDataMap.put(nodeID, nodeStoreDump);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    /* Iterate over all keys to verify each key one by one */
    for (Map.Entry<String, String> e : keyValueMap.entrySet()) {
      ChordID<String> chordKey = new ChordID<>(e.getKey());
      verifyKeyLocation(chordKey);
    }
  }

  public static void main(String args[]) {
    System.out.println("Arg1: " + args[0] + " Arg2:" + args[1]);
    if (args.length != 2) {
      System.out.println("Usage: java Client <PUT/GET/TEST> <NKEYS>");
      System.exit(0);
    }
    Client c = new Client(Integer.parseInt(args[1]));
    switch (args[0]) {
      case "PUT":
        c.putKeys();
        break;
      case "GET":
        c.getKeys();
        break;
      case "TEST":
        c.testKeys();
        break;
      default:
        break;
    }
  }
}
