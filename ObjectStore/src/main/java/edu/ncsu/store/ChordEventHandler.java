package edu.ncsu.store;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import edu.ncsu.chord.ChordID;
import edu.ncsu.chord.Event;
import edu.ncsu.chord.UpcallEventHandler;

/**
 * Created by amit on 9/4/17.
 */
public class ChordEventHandler implements UpcallEventHandler {

  /* Keep all loggers transient so that they are not passed over RMI call */
  private final transient static Logger logger = Logger.getLogger(ChordEventHandler.class);

  public void moveKeystoNewPredecessor(ChordID<InetAddress> prevPredecessor,
				       ChordID<InetAddress> newPredecessor) {
    logger.info("Handler called in " + ObjectStoreService.getChordSession().getChordNodeID()
		+ " New predecessor is: " + newPredecessor);

      /* Go through all keys of localStorage and see if we have any keys that needs to be
      moved to this new predecessor.*/
    ObjectStore store = ObjectStoreService.getStore();
    ArrayList<String> allKeys = store.keySet();
    Map<ChordID<String>, DataContainer> misplacedObjects = new HashMap();
    for (String key : allKeys) {
      ChordID<String> chordKey = new ChordID<>(key);
      if (chordKey.inRange(prevPredecessor, newPredecessor, false, true)) {
	// This key ID is less than new predecessor ID.
	// This key needs to be moved to new predecessor.
	try {
	  misplacedObjects.put(chordKey, store.getObject(chordKey));
	} catch (RemoteException e) {
	  e.printStackTrace();
	}
      }
    }
    logger.info("Number of keys that needs to moved: " + misplacedObjects.size());
    // TODO: remove below log statement after debugging is done
    logger.info("About to move below keys: " + new ArrayList<>(misplacedObjects.keySet()));
    // Start key movement. First get remote object for predecessor object store
    ObjectStoreOperations
	predecessorStore =
	StoreRMIUtils.getRemoteObjectStore(newPredecessor.getKey());
    try {
      predecessorStore.putObjects(misplacedObjects);
      // If above operation did not throw an exception
      // only then delete those keys from your storage
      store.deleteKeys(new ArrayList<>(misplacedObjects.keySet()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /* preValue and newValue will contain old and updated values of predecessor/successor depending on the event
  * Currently we can Identify only 4 types of important events with respect to key movements
  * 1.SUCCESSOR_FAILED,
  * 2.NEW_SUCCESSOR,
  * 3.PREDECESSOR_FAILED,
  * 4.NEW_PREDECESSOR
  * */
  @Override
  public void handleEvent(Event updateEvent,
                          ChordID<InetAddress> prevValue, ChordID<InetAddress> newValue) {
    switch (updateEvent) {
      case NEW_PREDECESSOR: {
        moveKeystoNewPredecessor(prevValue, newValue);
        break;
      }
      case NEW_SUCCESSOR: {
        break;
      }
      case PREDECESSOR_FAILED: {
        break;
      }
      case SUCCESSOR_FAILED: {
        break;
      }
      default:
        break;
    }
  }
}
