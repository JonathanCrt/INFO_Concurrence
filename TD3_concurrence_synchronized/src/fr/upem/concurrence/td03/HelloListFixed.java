package fr.upem.concurrence.td03;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class HelloListFixed {
	
	/*
	 * on prend comme lock list
	 * 
	 */
	  public static void main(String[] args) throws InterruptedException {
	    var nbThreads = 4;
	    var threads = new Thread[nbThreads]; 
	    var list = new ArrayList<Integer>();

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
	    // pas de besoin du synchronized grâce au join(...)
	    // tous ce qui fait précédemment est vu par le thread
	    //qui a été écrit en mémoire - join -> effets de barrière mémoire
	    // 
	    synchronized (list) {
	    	System.out.println("taille de la liste:" + list.size());
		}
	   
	  }
	}