package fr.upem.concurrence.td05;

import java.util.Arrays;
import java.util.stream.IntStream;

public class PhilosopherDinner {
  private final Object[] forks;  // tab of fork
  private int lastPhilosopher;

  public PhilosopherDinner(int forkCount) {
    Object[] forks = new Object[forkCount];
    Arrays.setAll(forks, i -> new Object());
    this.forks = forks;
  }

  public void eat(int index) {
	Object fork1, fork2;
	if(index != forks.length - 1) {
		fork1 = forks[index];
	    //fork2 = forks[(index + 1) % forks.length];
		fork2 = forks[index + 1];
	}else {
		fork1 = forks[0];
		fork2 = forks[forks.length - 1];
	}
	
    synchronized (fork1) {
      synchronized (fork2) {
    	  if(index !=  lastPhilosopher) {
    		  System.out.println("philosopher " + index + " eat");
          }
    	  lastPhilosopher = index;
      }
    }
  }

  public static void main(String[] args) {
    var dinner = new PhilosopherDinner(5);
    IntStream.range(0, 5).forEach(i -> {
      new Thread(() -> {
        for (;;) {
          dinner.eat(i);
        }
      }).start();
    });
  }
}

