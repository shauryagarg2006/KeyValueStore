package edu.ncsu.store;

import edu.ncsu.chord.ChordDriver;
import edu.ncsu.chord.ChordSession;

/**
 * Created by amit on 1/4/17.
 */
class ObjectStoreService {

  /* Reference to underlying chord node - only if client wants to join as a node */
  private static ChordSession chordSession = null;

  /* Reference to ObjectStore */
  private static ObjectStore store = null;

  static ChordSession getChordSession() {
    return chordSession;
  }

  static ObjectStore getStore() {
    return store;
  }

  private static void start() {
    /* First start chord node and join network */
    chordSession = ChordDriver.getSession();
    chordSession.join();

    /* Create object store object and export it for RMI */
    ObjectStore store = new ObjectStore();
    StoreRMIUtils.exportStoreObjectRMI(store);

    /* create client API object and export it for RMI */
    StoreClientAPIImpl storeClientAPI = new StoreClientAPIImpl();
    StoreRMIUtils.exportClientAPIRMI(storeClientAPI);
  }

  public static void main(String args[]) {
    ObjectStoreService.start();
  }
}
