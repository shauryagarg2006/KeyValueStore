package edu.ncsu.store;

import edu.ncsu.chord.ChordDriver;
import edu.ncsu.chord.ChordSession;

/**
 * Created by amit on 1/4/17.
 */
public class ObjectStoreService {

  /* Reference to underlying chord node - only if client wants to join as a node */
  private static ChordSession chordSession = null;

  /* Reference to ObjectStore */
  private static ObjectStore store = null;

  public ObjectStoreService() {
    /* First start chord node and join network */
    chordSession = ChordDriver.getSession();
    chordSession.join();

    /* Create object store object and export it for RMI */
    ObjectStore store = new ObjectStore();
    StoreRMIUtils.exportStoreObjectRMI(store);

    /* create client API object and export it for RMI */
    StoreClientAPIImpl storeClientAPI = new StoreClientAPIImpl(chordSession);
    StoreRMIUtils.exportClientAPIRMI(storeClientAPI);
  }

  public static void main(String args[]) {
    new ObjectStoreService();
  }
}
