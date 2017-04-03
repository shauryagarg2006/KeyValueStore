package edu.ncsu.store;

import java.util.HashMap;

import org.apache.log4j.Logger;


/**
 * Created by amit on 1/4/17.
 */
public class HashStorage implements LocalStorage {

  HashMap<String, byte[]> storage;

  /* Keep all loggers transient so that they are not passed over RMI call */
  private final transient static Logger logger = Logger.getLogger(HashStorage.class);

  public HashStorage() {
	storage = new HashMap<String, byte[]>();
}
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
  public int size() {
    return storage.size();
  }

  @Override
  public boolean containsKey(String key) {
    return storage.containsKey(key);
  }

}
