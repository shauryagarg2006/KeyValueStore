package edu.ncsu.store;

/**
 * Created by amit on 1/4/17.
 */
public class StoreConfig {

  /* RMI Registry Port */
  static int RMI_REGISTRY_PORT = 1099;

  /* Number of replicas to maintain */
  public static int REPLICATION_COUNT = 2;

  /* RMI Call timeout - Seconds to wait before call is considered as failed */
  static int RMI_TIMEOUT = 1;
}
