package edu.ncsu.client;

import java.net.InetAddress;
import java.util.ArrayList;

import edu.ncsu.store.ObjectServer;
import edu.ncsu.store.StoreDriver;

/**
 * Created by amit on 24/3/17.
 */
public class Client {
  public static void main(String args[]) {
    try {
      ArrayList<InetAddress> bootstrapNode = new ArrayList<>();
      for (int i = 0; i < args.length; i++) {
        bootstrapNode.add(InetAddress.getByName(args[i]));
      }
      StoreDriver driver = new StoreDriver(bootstrapNode, true);
      driver.put("amit", "hello,world!");
      System.out.println(driver.get("amit"));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
