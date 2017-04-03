package edu.ncsu.client;

import org.apache.log4j.Logger;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


/**
 * Created by amit on 24/3/17.
 */
public class ClientRMIUtils {

  /* Keep all loggers transient so that they are not passed over RMI call */
  private final transient static Logger logger = Logger.getLogger(ClientRMIUtils.class);

  private static Registry getRegistry() {
    Registry registry = null;

    try {
      registry = LocateRegistry.getRegistry();
      registry.list();
    } catch (RemoteException e) {
      try {
        registry = LocateRegistry.createRegistry(ClientConfig.RMI_REGISTRY_PORT);
      } catch (RemoteException nestedException) {
        nestedException.printStackTrace();
        logger.error("Unable to get/create RMI registry. Exiting");
      }
    }
    return registry;
  }



  static StoreClientAPI getRemoteClient() {
    /* INetAddress toString adds a '/' at the beginning remove that */
    String serverURL = "rmi://" + ClientConfig.bootStrapIP + "/Client";
    StoreClientAPI api = null;
    try {
      api = (StoreClientAPI) Naming.lookup(serverURL);
      if (api == null) {
	throw new RemoteException();
      }
    } catch (Exception e) {
      /* TODO: Need to do all RMI exception handling
      Why did RMI execution fail
      Node down? Node failure? Network partitioning? New node arrival?
       */
      e.printStackTrace();
      logger.error("Unable to get Remote object for " + ClientConfig.bootStrapIP + " Trying one more time...");
      /* Try one more time before giving up */
      try {
	api = (StoreClientAPI) Naming.lookup(serverURL);
      } catch (Exception nestedException) {
	nestedException.printStackTrace();
	api = null;
      }
      e.printStackTrace();
    }
    return api;
  }

}
