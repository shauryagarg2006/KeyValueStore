package edu.ncsu.chord;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by amit on 13/2/17.
 */
public class FingerTable implements Serializable {

  /*
  Table entries are in fashion of self.ID + 2 ^ (i) where 0 <= i <= m
  Number of entries is CHORD_ID_MAX_BITS in Hash Value
   */
  public final static int MAX_SIZE = ChordConfig.CHORD_ID_MAX_BITS;

  /* Actual finger table. First entry is known as 'successor'
  i'th entry is calculated as ChordID.id + 2^(i);
   */
  private ArrayList<FingerTableEntry> table;

  /* The node of whose finger table is this
  * Keep this value transient since we don't want to pass it in RMI calls. */
  transient private Node self;

  private final static Logger logger = Logger.getLogger(FingerTable.class);

  //  private void initFingerTable(NodeOperations bootstrapNode) {
  //    table = new ArrayList<>();
  //    ChordID<InetAddress> successor = bootstrapNode.getSuccessor(self.getID());
  //    /* Find out the successor node and initialize it in finger table */
  //    table.add(new FingerTableEntry(0, self.getID().next(0), successor));
  //    /* Initialize remaining finger table without NodeID - it will get initialized in stabilize step */
  //    for (int i = 1; i < table.size(); i++)
  //      table.add(new FingerTableEntry(i, self.getID().next(i), null));
  //  }

  public FingerTable(Node self) throws RemoteException {
    this.self = self;
    table = new ArrayList<>(MAX_SIZE);
    for (int i = 0; i < MAX_SIZE; i++) {
      table.add(new FingerTableEntry(i, self.getChordID().next(i), self.getChordID().next(i + 1),
				     self.selfChordID));
    }
  }

  public void insertNewNode(ChordID<InetAddress> nodeChordID) {
    for (int i = 0; i < MAX_SIZE; i++) {
      FingerTableEntry entry = table.get(i);
      if (nodeChordID.inRange(entry.hashRangeStart, entry.hashRangeEnd, true, false)
	  && nodeChordID.compareTo(entry.responsibleNodeID) < 0) {
	table.get(i).responsibleNodeID = nodeChordID;
      }
    }
  }

  public FingerTableEntry getEntry(int index) {
    if (index >= MAX_SIZE) {
      logger.error("Entry " + index + " Does not exist in finger table. (Total entries present: "
		   + table.size() + ")");
    }
    return table.get(index);
  }

  public String toString() {
    StringBuilder tableData = new StringBuilder();
    tableData.append("Finger table for node: " + self.selfChordID + "\n");
    for (int i = 0; i < table.size(); i++) {
      tableData.append(table.get(i).toString() + "\n");
    }
    return tableData.toString();
  }
}
