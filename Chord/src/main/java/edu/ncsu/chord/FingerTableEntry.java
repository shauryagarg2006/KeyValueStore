package edu.ncsu.chord;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Created by amit on 10/2/17.
 */

class FingerTableEntry {

  /* Index number of this entry in the table */
  int fingerIndex = -1;

  /* The range which this entry covers hashRangeStart is included and hashRangeEnd is excluded */
  Hash hashRangeStart = null;
  Hash hashRangeEnd = null;

  /* Successor ID for corresponding entry ID */
  ChordID<InetAddress> responsibleNodeID = null;

  public FingerTableEntry(int fingerIndex, Hash hashRangeStart, Hash hashRangeEnd) {
    this.fingerIndex = fingerIndex;
    this.hashRangeStart = hashRangeStart;
    this.hashRangeEnd = hashRangeEnd;
    this.responsibleNodeID = null;
  }

  public FingerTableEntry(int fingerIndex, Hash hashRangeStart, Hash hashRangeEnd,
			  ChordID responsibleNodeID) {
    this(fingerIndex, hashRangeStart, hashRangeEnd);
    this.responsibleNodeID = responsibleNodeID;
  }

  public String toString() {
    return "[" + fingerIndex + ", " + "(" + hashRangeStart + "," + hashRangeEnd + ")" +
	   ", " + responsibleNodeID + "]";
  }
}
