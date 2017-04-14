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
      try {
        DataContainer valueContainer = store.getObject(chordKey);
        if (valueContainer.replicaNumber != 1 ||
            chordKey.inRange(prevPredecessor, newPredecessor, false, true)) {
          // This key ID is either a replica or belongs to new predecessor
          // This key needs to be moved to new predecessor.
          misplacedObjects.put(chordKey, valueContainer);
        }
      } catch (RemoteException e) {
        e.printStackTrace();
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

  public void moveKeystoNewSuccessor(ChordID<InetAddress> prevSuccessor,
                                       ChordID<InetAddress> newSuccessor) {
    // move all the keys with replicaNumber != StoreConfig.REPLICATION_COUNT to this new successor
    logger.info("Handler called in " + ObjectStoreService.getChordSession().getChordNodeID()
                + " New successor is: " + newSuccessor);

    /* Go through all keys of localStorage and see if we have any keys that needs to bb
      further replicated.*/
    ObjectStore store = ObjectStoreService.getStore();
    ArrayList<String> allKeys = store.keySet();
    Map<ChordID<String>, DataContainer> replicableKeys = new HashMap();
    for (String key : allKeys) {
      ChordID<String> chordKey = new ChordID<>(key);
      try {
        DataContainer valueContainer = store.getObject(chordKey);
        if (valueContainer.replicaNumber != StoreConfig.REPLICATION_COUNT ) {
          // This key ID can be further replicated
          replicableKeys.put(chordKey, valueContainer);
        }
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }
    logger.info("Number of keys that can be replicated: " + replicableKeys.size());
    // TODO: remove below log statement after debugging is done
    logger.info("About to replicate below keys: " + new ArrayList<>(replicableKeys.keySet()));
    // Start key movement. First get remote object for predecessor object store
    ObjectStoreOperations
        predecessorStore =
        StoreRMIUtils.getRemoteObjectStore(newSuccessor.getKey());
    try {
      predecessorStore.makeReplicas(replicableKeys);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void handleSuccessorFailure(ChordID<InetAddress> prevPredecessor,
                                     ChordID<InetAddress> newPredecessor) {
    /**
     * When successor fails, you need to call replicateKeys on all the keys with
     * replicaNumber < StoreConfig.REPLICA_COUNT (Keys with replicaNumber == REPLICA_COUNT
     * have no relation with successor)
     */

  }

  public void handlePredecessorFailure(ChordID<InetAddress> prevPredecessor,
                                     ChordID<InetAddress> newPredecessor) {
    /**
     *  When predecessor fails, you need to call replicate Keys on all the
     *  keys with replicaNumber > 1. (keys with replicaNumber == 1 are the keys
     *  that belong to you and have no relation with predecessor)
     *  */


  }

  /* preValue and newValue will contain old and updated values of predecessor/successor depending on the event
  * Currently we can Identify only 4 types of important events with respect to key movements
  * 1.SUCCESSOR_FAILED,
  * 2.NEW_SUCCESSOR,
  * 3.PREDECESSOR_FAILED,
  * 4.NEW_PREDECESSOR
  *
  * Note a SUCCESSOR FAILED or PREDECSSOR_FAILED even will most probably be always followed by
  * NEW_SUCCESSOR or NEW_PREDECESSOR event so if key movement is being done on these events then you can
  * avoid doing any keymovement on SUCCSSOR_FAILED or PREDECESSOR_FAILED events (just do all key movement on
  * NEW_* events.
  * Ideally on getting NEW_PREDECSSOR you move all the keys that now belong to that predecessor and on getting
  * NEW_SUCCESSOR you call replicate on all the kesy that have replciaNumber < REPLICA_COUNT
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
        moveKeystoNewSuccessor(prevValue, newValue);
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
