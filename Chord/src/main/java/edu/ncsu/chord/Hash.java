package edu.ncsu.chord;
/**
 * Created by amit on 16/2/17.
 */
public interface Hash extends Comparable<Hash> {

  public String getValue();

  Hash next(int n);

  boolean inRange(Hash start, Hash end, boolean startInclusive, boolean endInclusive);
}
