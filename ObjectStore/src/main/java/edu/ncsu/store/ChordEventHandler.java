package edu.ncsu.store;

import org.apache.log4j.Logger;

import java.net.InetAddress;

import edu.ncsu.chord.ChordID;
import edu.ncsu.chord.UpcallEventHandler;

/**
 * Created by amit on 9/4/17.
 */
public class ChordEventHandler implements UpcallEventHandler {

  /* Keep all loggers transient so that they are not passed over RMI call */
  private final transient static Logger logger = Logger.getLogger(ChordEventHandler.class);

  @Override
  public void handleEvent(ChordID<InetAddress> newPredecessor) {
    logger.debug("Handler called! New predecessor is: " + newPredecessor);
  }
}
