package edu.ncsu.store;

/**
 * Created by amit on 1/4/17.
 */
public interface LocalStorage {
  byte[] get(String key);

  boolean containsKey(String key);

  void put(String key, byte[] value) throws Exception;

  void delete(String key);

  int size();
}
