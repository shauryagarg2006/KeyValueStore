package edu.ncsu.store;

import java.net.InetAddress;
import java.util.ArrayList;

import edu.ncsu.chord.ChordDriver;
import edu.ncsu.chord.ConfigParams;
import edu.ncsu.chord.RegistryManager;


/**
 * Created by amit on 24/3/17.
 */
public class ObjectServer {
  public static void main(String args[]) {
    ConfigParams cf = new ConfigParams() {
      @Override
      public int getCHORD_ID_MAX_BITS() {
	return 10;
      }

      @Override
      public int getSUCCESSOR_LIST_MAX_SIZE() {
	return 3;
      }

      @Override
      public ArrayList<InetAddress> getbootstrapNodes() {
	ArrayList<InetAddress> list = new ArrayList<>();
	try {
	  list.add(InetAddress.getByName("10.139.63.115"));
	} catch (Exception e) {
	  e.printStackTrace();
	}
	return list;
      }

      @Override
      public RegistryManager getRegistryManager() {
	return new RegistryManagerImpl();
      }
    };

    try {
      System.out.println("About to initailize chord");
      ChordDriver.initialize(cf);
      System.out.println("Initialization done..");
      ChordDriver.joinChordNetwork();
      System.out.println("Joined network");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
