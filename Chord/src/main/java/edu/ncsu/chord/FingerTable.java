package edu.ncsu.chord;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by amit on 13/2/17.
 */
class FingerTable {

  /*
  Table entries are in fashion of self.ID + 2 ^ (i) where 0 <= i <= m
  Number of entries is CHORD_ID_MAX_BITS in Hash Value
   */
  public final static int MAX_SIZE = ChordConfig.CHORD_ID_MAX_BITS;

  /* Actual finger table. First entry is known as 'successor'
  i'th entry is calculated as ChordID.id + 2^(i); */
  private ArrayList<FingerTableEntry> table;

  private final static Logger logger = Logger.getLogger(FingerTable.class);

  public FingerTable(ChordID<InetAddress> selfChordID) {
    table = new ArrayList<>(MAX_SIZE);
    for (int i = 0; i < MAX_SIZE; i++) {
      table.add(new FingerTableEntry(i, selfChordID.next(i), selfChordID.next(i + 1), selfChordID));
    }
  }

  public FingerTableEntry getEntry(int index) {
    return table.get(index);
  }

  public String toString() {
    StringBuilder tableData = new StringBuilder();
    for (int i = 0; i < table.size(); i++) {
      tableData.append(table.get(i).toString() + "\n");
    }
    return tableData.toString();
  }
}
