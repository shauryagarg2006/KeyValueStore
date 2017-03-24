package edu.ncsu.store;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import edu.ncsu.chord.RegistryManager;

/**
 * Created by amit on 24/3/17.
 */
public class RegistryManagerImpl implements RegistryManager {

  static Registry registry = null;
  private int RMI_DEFAULT_PORT = 1099;

  @Override
  public Registry getRegistry() {
    if (registry == null) {
      try {
	registry = LocateRegistry.createRegistry(RMI_DEFAULT_PORT);
      } catch (Exception e) {
	e.printStackTrace();
      }
    }
    return registry;
  }

}
