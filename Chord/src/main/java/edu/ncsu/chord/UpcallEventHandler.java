package edu.ncsu.chord;

import java.net.InetAddress;

/**
 * Created by amit on 1/4/17.
 */
public interface UpcallEventHandler {
  // TODO: for now this event handler will only notify arrival of new
  // predecessor. In future support for multiple events can be handled.
  // Node failure event will also be required by KeyStore module.
  void handleEvent(ChordID<InetAddress> newPredecessor);
}
