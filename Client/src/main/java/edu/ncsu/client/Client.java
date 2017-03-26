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
			Scanner in = new Scanner(System.in);
			while (true) {
				System.out.println("Do you want to to proceed?(Y/N)");
				String answer = in.nextLine();
				if ("N".equalsIgnoreCase(answer)) {
					break;
				}
				System.out.println("Press 1 to get and 2 to store");
				answer = in.nextLine();
				if ("1".equalsIgnoreCase(answer)) {
					// Code to get value
					System.out.println("Enter key to retrive");
					String key = in.nextLine();
					System.out.println(driver.get(key));
				} else {
					// Code to set value
					System.out.println("Enter key to store");
					String key = in.nextLine();
					System.out.println("Enter value to store");
					String value = in.nextLine();
					driver.put(key, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
