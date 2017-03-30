package edu.ncsu.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import edu.ncsu.store.StoreDriver;

public class ClientTest {

	static final String inputPath = "/root/KeyValueStore/node.list";
	static ArrayList<InetAddress> ipList;
	private final transient static Logger logger = Logger.getLogger(ClientTest.class);

	@Test
	public void testKeyStore() {
		try {
			ArrayList<InetAddress> bootstrapNode = new ArrayList<>();
			// TODO Hard coded BootStrap IP Address
			bootstrapNode.add(InetAddress.getByName("172.17.0.2"));
			StoreDriver driver = new StoreDriver(bootstrapNode, false);
			//Thread.sleep(1000*80);
			Map<String, String> values = new HashMap<String, String>();
			RandomStringGen stringGen = new RandomStringGen();
			Double average_time = 0D;
			for (int iter = 0; iter <= 1020; iter++) {
				String key = stringGen.nextString();
				String value = stringGen.nextString();
				if (values.containsKey(key)) {
					continue;
				} else {
					values.put(key, value);
					long before_time = System.currentTimeMillis();
					driver.put(key, value);
					long after_time = System.currentTimeMillis();
					logger.debug("Time Taken to put key : " + (after_time
										   - before_time));
					average_time += after_time - before_time;
				}
			}
			logger.info("Average Time Taken to put key : " + (average_time / values.size()) +
				    " For " + values.size() + " keys");
		  System.out.println("Average Time Taken to put key : " + (average_time / values
											      .size())
				     +
				     " For " + values.size() + " keys");
			getInetAddressList();
			driver.verifyKeys(ipList);

		  average_time = 0.0;
		  for (Map.Entry<String, String> e : values.entrySet()) {
		    long before_time = System.currentTimeMillis();
		    String retrievedValue = driver.get(e.getKey());
		    long after_time = System.currentTimeMillis();
		    logger.debug("Time Taken to get key : " + (after_time - before_time));
		    average_time += after_time - before_time;
		    if (retrievedValue == null ||
			!retrievedValue.equals(e.getValue())) {
		      logger.error("Key: " + e.getKey() + " Actual value: " + e.getValue() + ""
				   + " Recieved value " + retrievedValue);
		    }
		  }
		  logger.info("Average Time Taken to get key : " + (average_time / values.size()) +
			      " For " + values.size() + " keys");
		  System.out.println("Average Time Taken to get key : " + (average_time / values.size()) +
			      " For " + values.size() + " keys");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getInetAddressList() {
		logger.debug("Initializing InetAddress List");
		/*
		 * Assuming there is a file at /tmp/node.list which has IP address of
		 * all nodes 1 per line
		 */
		ipList = new ArrayList<>();

		try {
			FileReader fr = new FileReader(inputPath);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				logger.info("Got active node @ " + line);
				ipList.add(InetAddress.getByName(line));
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
