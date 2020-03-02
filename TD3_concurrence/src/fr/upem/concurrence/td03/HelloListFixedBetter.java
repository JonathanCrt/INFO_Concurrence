package fr.upem.concurrence.td03;

import java.util.stream.IntStream;

public class HelloListFixedBetter {
	
	  public static void main(String[] args) throws InterruptedException {
	    var nbThreads = 4;
	    var threads = new Thread[nbThreads]; 
	    var list = new ThreadSafeList();
	    
	    IntStream.range(0, nbThreads).forEach(j -> {
	      Runnable runnable = () -> {
	    		for (var i = 0; i < 5000; i++) {
	    		  synchronized (list) {
	    			  list.add(i);
	    		  }
	  	          
	  	        }
	      };
	      threads[j] = new Thread(runnable);
	      threads[j].start();
	    });

	    for (Thread thread : threads) {
	      thread.join();
	    }
	   
	    synchronized (list) {
	    	System.out.println("contenu de la liste:" + list.toString());
		}
	   
	  }
}
