package edu.ncsu.chord;

import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * Created by amit on 10/2/17. Every chord ID must have an input value(which is) associated with it
 * such as InetAddress if its a chord node or String if its a key for an object.
 */
public class ChordID<T> extends SHA256Hash implements Serializable {

  /* Each Chord ID must have an associated input key which is hashed to generated the ChordID
  This key can be an InetAddress in case of a node or a simple String in case of key of an object */
  T key = null;

  /* Depending on CHORD_ID_MAX_BITS in each ChordID will be calculated. This is also used in finger table */
  public static final int MAX_BITS = ChordConfig.CHORD_ID_MAX_BITS;

  /* Keep all loggers transient so that they are not passed over RMI call */
  private final transient static Logger logger = Logger.getLogger(ChordID.class);

  public ChordID(T key) {
    super(key.toString(), MAX_BITS);
    this.key = key;
  }


  public T getKey() {
    return key;
  }

  public String toString() {
    return "[" + key + "," + getValue() + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof SHA256Hash))
      return false;
    ChordID<T> hash = (ChordID<T>) obj;
    return hash.key.equals(this.key) && hash.getValue().equals(this.getValue());
  }
}
