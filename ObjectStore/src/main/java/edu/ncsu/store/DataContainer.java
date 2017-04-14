package edu.ncsu.store;

import java.io.Serializable;

/**
 * Created by amit on 11/4/17.
 */
public class DataContainer implements Serializable {

  public byte[] value;
  public int replicaNumber;

  public DataContainer(byte[] value, int replicaNumber) {
    this.value = value;
    this.replicaNumber = replicaNumber;
  }
}
