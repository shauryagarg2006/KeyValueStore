package edu.ncsu.client;

/**
 * Created by amit on 24/3/17.
 */
public class Client {
    public static void main(String args[]) {
      StoreClientAPI handle = ClientRMIUtils.getRemoteClient();
      try {
	handle.put("1", "abcdefg");
      } catch (Exception e) {
	e.printStackTrace();
      }
    }
}
