package edu.ncsu.store;

import java.rmi.Remote;

/**
 * Created by amit on 24/3/17.
 */
public interface ObjectStore extends Remote {
  String getObject(String key);

  boolean putObject(String key, String value);
}
