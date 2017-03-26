package edu.ncsu.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.ncsu.chord.NodeTest;
import edu.ncsu.store.StoreDriver;

public class ClientTest {

	static final String inputPath = "/root/KeyValueStore/node.list";
	static ArrayList<InetAddress> ipList;
	private final transient static Logger logger = Logger.getLogger(NodeTest.class);

	public static void main(String args[]) {
		try {
			ArrayList<InetAddress> bootstrapNode = new ArrayList<>();
			for (int i = 0; i < args.length; i++) {
				bootstrapNode.add(InetAddress.getByName(args[i]));
			}
			StoreDriver driver = new StoreDriver(bootstrapNode, false);
			Map<String, String> values = new HashMap<String, String>();
			RandomStringGen stringGen = new RandomStringGen();
			Long average_time = 0L;
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
					logger.info("Time Taken to put key : " + (after_time - before_time));
					average_time += after_time - before_time;
				}
			}
			logger.info("Average Time Taken to put key : " + average_time / values.size());
			getInetAddressList();
			driver.verifyKeys(ipList);
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
