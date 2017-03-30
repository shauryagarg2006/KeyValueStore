package edu.ncsu.chord;

import java.util.Random;

/**
 * Created by amit on 4/3/17. This class will keep running stabilize method after every 1 min
 */

public class Stabilizer implements Runnable {

  Node self;
  private static Random rand;

  public Stabilizer(Node self) {
    this.self = self;
    rand = new Random();
  }

  private static long randTime() {
    return ((int) (rand.nextDouble() * 100000) % 5000);
  }

  @Override
  public void run() {
    while (true) {
      try {
	Thread.sleep(1000);
	self.stabilize();
	Thread.sleep(1000);
	self.fixFingers();
      } catch (Exception e) {
	e.printStackTrace();
      }
    }
  }
}
