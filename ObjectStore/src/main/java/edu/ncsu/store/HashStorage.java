package edu.ncsu.store;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Set;


/**
 * Created by amit on 1/4/17.
 */
class HashStorage implements LocalStorage {

  private HashMap<String, byte[]> storage;

  /* Keep all loggers transient so that they are not passed over RMI call */
  private final transient static Logger logger = Logger.getLogger(HashStorage.class);

  @Override
  public byte[] get(String key) {
    return storage.get(key);
  }

  @Override
  public void put(String key, byte[] value) {
    storage.put(key, value);
  }

  @Override
  public void delete(String key) {
    storage.remove(key);
  }

  @Override
  public Set<String> keySet() {
    return storage.keySet();
  }

  @Override
  public int size() {
    return storage.size();
  }

  @Override
  public boolean containsKey(String key) {
    return storage.containsKey(key);
  }

}
