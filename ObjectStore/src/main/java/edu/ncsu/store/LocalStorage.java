package edu.ncsu.store;

import java.util.Set;

/**
 * Created by amit on 1/4/17.
 */
interface LocalStorage {
  byte[] get(String key);

  boolean containsKey(String key);

  void put(String key, byte[] value) throws Exception;

  void delete(String key);

  Set<String> keySet();

  int size();
}
