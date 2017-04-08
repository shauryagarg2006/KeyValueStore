package edu.ncsu.client;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.ncsu.store.StoreClientAPI;

/**
 * Created by amit on 24/3/17.
 */
public class Client {

  private final transient static Logger logger = Logger.getLogger(Client.class);

  public void verifyKeys() {
    StoreClientAPI storeHandle = ClientRMIUtils.getRemoteClient();
  }

  public static void main(String args[]) {
    StoreClientAPI handle = ClientRMIUtils.getRemoteClient();
    Map<String, String> values = new HashMap<String, String>();
    RandomStringGen stringGen = new RandomStringGen();
    Double average_time = 0D;
    try {
      for (int iter = 0; iter <= 10; iter++) {
	String key = stringGen.nextString();
	String value = stringGen.nextString();
	if (values.containsKey(key)) {
	  continue;
	} else {
	  values.put(key, value);
	  long before_time = System.currentTimeMillis();
	  handle.put(key, value);
	  long after_time = System.currentTimeMillis();
	  logger.debug("Time Taken to put key : " + (after_time - before_time));
	  average_time += after_time - before_time;
	}
      }
      logger.info("Average Time Taken to put key : " + (average_time / values.size()) + " For "
		  + values.size() + " keys");
      System.out
	  .println("Average Time Taken to put key : " + (average_time / values.size()) + " For "
		   + values.size() + " keys");
      average_time = 0.0;
      for (Map.Entry<String, String> e : values.entrySet()) {
	long before_time = System.currentTimeMillis();
	String retrievedValue = (String) handle.get(e.getKey());
	long after_time = System.currentTimeMillis();
	logger.debug("Time Taken to get key : " + (after_time - before_time));
	average_time += after_time - before_time;
	if (retrievedValue == null || !retrievedValue.equals(e.getValue())) {
	  logger.error("Key: " + e.getKey() + " Actual value: " + e.getValue() + ""
		       + " Recieved value " + retrievedValue);
	}
      }
      logger.info("Average Time Taken to get key : " + (average_time / values.size()) + " For "
		  + values.size() + " keys");
      System.out
	  .println("Average Time Taken to get key : " + (average_time / values.size()) + " For "
		   + values.size() + " keys");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
