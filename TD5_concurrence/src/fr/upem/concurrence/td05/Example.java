package fr.upem.concurrence.td05;

import java.util.stream.IntStream;

public class Example {
	  public static void main(String[] args) throws InterruptedException {
	    var exchanger = new Exchanger<String>();
	    IntStream.range(0, 10).forEach(i -> {
	      new Thread(() -> {
	        try {
	          System.out.println("thread " + i + " received from " + exchanger.exchange("thread " + i));
	        } catch (InterruptedException e) {
	          throw new AssertionError(e);
	        }
	      }).start();
	    });
	  }
	}  