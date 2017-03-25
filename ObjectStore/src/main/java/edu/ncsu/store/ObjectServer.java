package edu.ncsu.store;

import java.net.InetAddress;
import java.util.ArrayList;

import edu.ncsu.chord.ChordDriver;
import edu.ncsu.chord.Node;



/**
 * Created by amit on 24/3/17.
 */
public class ObjectServer {
  public static void main(String args[]) {
    try {
      ArrayList<InetAddress> bootstrapNodeList = new ArrayList<>();
      bootstrapNodeList.add(InetAddress.getByName(args[0]));
      System.out.println("About to initialize chord");
      Node node = new ChordDriver.NodeBuilder()
                      .setRegistryManager(new RegistryManagerImpl())
                      .setBootStrapNodes(bootstrapNodeList)
                      .setCHORD_ID_MAX_BITS(10)
                      .setSUCCESSOR_LIST_MAX_SIZE(3)
                      .Build();
      ChordDriver.joinChordNetwork(node);
      System.out.println("Joined network");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
