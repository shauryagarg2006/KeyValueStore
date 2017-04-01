package edu.ncsu.chord;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by amit on 21/3/17.
 */

/* NodeInfo class should be public in order to accessable via java RMI */
class NodeInfo implements Serializable {
  /* ChordID of this node */
  ChordID<InetAddress> selfChordID;
  /* ChordID of predecessor of this node */
  ChordID<InetAddress> predecessor;
  /* Finger table of this node - We can not directly pass FingerTable object via RMI
  * since FingerTable contains a reference named 'self' for its Node object this gives
  * RMI ClassCast Exception. */
  FingerTable fingerTable;
  /* Successor list of this node */
  ArrayList<ChordID<InetAddress>> successorList;


  /* nodeList index - ONLY TO BE USED BY Testing method */
  int sortedListIndex;

  /* LongValue for ID of this node - ONLY TO BE USED BY Testing method */
  long idLongValue;

  public NodeInfo(ChordID<InetAddress> selfChordID, ChordID<InetAddress> predecessor,
		  FingerTable fingerTable, ArrayList<ChordID<InetAddress>> successorList) {
    this.selfChordID = selfChordID;
    this.predecessor = predecessor;
    this.fingerTable = fingerTable;
    this.successorList = successorList;
    sortedListIndex = -1;
    idLongValue = -1;
  }
}
