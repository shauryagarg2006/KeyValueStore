package edu.ncsu.client;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
