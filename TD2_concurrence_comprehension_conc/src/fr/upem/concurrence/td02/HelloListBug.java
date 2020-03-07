package fr.upem.concurrence.td02;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class HelloListBug {
	 
	/**
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		  var nbThreads = 4;
		  var threads = new Thread[nbThreads];
		  var list = new ArrayList<Integer>();

		  IntStream.range(0, nbThreads).forEach(j -> {
		    Runnable runnable = () -> {
		      
		      for (var i = 0; i < 5000; i++) {
		        list.add(i *nbThreads);
		      }		      
		    };
		    
		    threads[j] = new Thread(runnable);
		    threads[j].start();
		  });

		  for (var thread : threads) {
		    thread.join(); 
		  }

		  System.out.println(list.size());
		}
	
	/**
	 * t1 fait list.add(1)
	 * t2 fait list.add(2)
	 * list.size = 2
	 * 
	 */
}
