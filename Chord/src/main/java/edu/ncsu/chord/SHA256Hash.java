package edu.ncsu.chord;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Created by amit on 14/2/17. The class for providing function on Hash value.
 */
public class SHA256Hash implements Hash, Serializable {

  /* The actual generated hashValue
  * This value is stored as long for now. can be changed to BigInteger
  * later. So do not use these values directly. Always use class methods */
  private long hashValue;

  /* Depending on CHORD_ID_MAX_BITS in each ChordID will be calculated. This is also used in finger table */
  private int MAX_BITS = -1;

  /* A MOD of every generated dht value is calculated to keep all ChordID in circular topology */
  private long MOD;

  /* Keep all loggers transient so that they are not passed over RMI call */
  private final transient static Logger logger = Logger.getLogger(SHA256Hash.class);

  public SHA256Hash(String input, int MAX_BITS) {
    /* Hash the input string, take first CHORD_ID_MAX_BITS from that */
    this.MAX_BITS = MAX_BITS;
    MOD = (long) Math.pow(2, MAX_BITS);
    hashValue = hash(input) % MOD;
    logger.debug(" Hash input: " + input +
                 " Generated hash: " + hashValue);
  }

  private SHA256Hash(long value, int MAX_BITS) {
    this.MAX_BITS = MAX_BITS;
    MOD = (long) Math.pow(2, MAX_BITS);
    hashValue = value;
  }

  private long hash(String input) {
    BigInteger hash = null;
    try {
      MessageDigest hasher = MessageDigest.getInstance("SHA-256");
      hasher.update(input.getBytes("UTF-8"));
      byte[] hashBytes = hasher.digest();

      /* Get first 'CHORD_ID_MAX_BITS' from generated hash */
      hashBytes = Arrays.copyOfRange(hashBytes, 0, MAX_BITS / 8 + 1);

      hash = new BigInteger(1, hashBytes);
      //return String.format("%064x", new BigInteger(1, digest));
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      /* TODO: If CHORD_ID_MAX_BITS is more than 32/64 then longValue will overflow. Check it later */
      return hash.longValue();
    }
  }

  @Override
  public String getValue() {
    return Long.toString(hashValue);
  }


  /**
   * @return returns the 'n'th next hash value. 0 <= n <= CHORD_ID_MAX_BITS n'th next is calculated as
   * hasValue + 2^(n);
   */
  @Override
  public Hash next(int n) {
    if (n >= 0 && n <= MAX_BITS) {
      return new SHA256Hash((hashValue + (long) Math.pow(2, n)) % MOD, MAX_BITS);
    }
    return new SHA256Hash(hashValue, MAX_BITS);
  }

  public String toString() {
    return getValue();
  }

  @Override
  public int compareTo(Hash h) {
    if (this.hashValue < Long.parseLong(h.getValue())) {
      return -1;
    } else if (this.hashValue > Long.parseLong(h.getValue())) {
      return 1;
    }
    return 0;
  }


  /* Checks range inclusive of start and exclusive of end */
  @Override
  public boolean inRange(Hash rangeStart, Hash rangeEnd, boolean startInclusive,
			 boolean endInclusive) {
    /* Ranges can have bigger start and smaller end value due to mod */
    /* TODO: this casting can give runtime execption find a way out later */
    SHA256Hash start = new SHA256Hash(((SHA256Hash) rangeStart).hashValue, MAX_BITS);
    SHA256Hash end = new SHA256Hash(((SHA256Hash) rangeEnd).hashValue, MAX_BITS);

    /* If start =x and end = x + 1and startinclusive is false & endInclusive is false
     * then nothing can be inRange of this interval */
    if (start.hashValue + 1 == end.hashValue && !startInclusive && !endInclusive) {
      return false;
    }

    if (!startInclusive) {
      start.hashValue = (start.hashValue + 1) % MOD;
    }
    if (!endInclusive) {
      end.hashValue = (end.hashValue - 1) % MOD;
    }

    if (end.compareTo(start) == 0) {
      return end.compareTo(this) == 0;
    } else if (end.compareTo(start) < 0) {
      return !(end.compareTo(this) < 0 && this.compareTo(start) < 0);
    } else {
      return (start.compareTo(this) <= 0 && this.compareTo(end) <= 0);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || !(o instanceof SHA256Hash))
      return false;
    SHA256Hash hash = (SHA256Hash) o;
    return (hash.hashValue == this.hashValue);
  }
}
