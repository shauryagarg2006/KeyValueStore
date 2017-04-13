package edu.ncsu.store;

import java.util.Set;

/**
 * Created by amit on 1/4/17.
 */
interface LocalStorage {
  DataContainer get(String key);

  boolean containsKey(String key);

  void put(String key, DataContainer value) throws Exception;

  void delete(String key);

  Set<String> keySet();

  int size();
}
