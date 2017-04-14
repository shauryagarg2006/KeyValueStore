package edu.ncsu.store;

import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;


/**
 * Created by amit on 1/4/17.
 */
class HashStorage implements LocalStorage {

  private HashMap<String, DataContainer> storage;

  /* Having a default size of 1000 for the hashmap improves performance of get/put keys.
  This might be because JVM by default creates HashMap of smaller size and then grows the value as more
  keys come.However growing hashmap is a costly operation.
   */
  private static long HASHMAP_DEFAULT_SIZE = 1024;
  /* Keep all loggers transient so that they are not passed over RMI call */
  private final transient static Logger logger = Logger.getLogger(HashStorage.class);

  public HashStorage() {
	storage = new HashMap<String, DataContainer>();
}
  @Override
  public DataContainer get(String key) {
    return storage.get(key);
  }

  @Override
  public void put(String key, DataContainer value) {
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
