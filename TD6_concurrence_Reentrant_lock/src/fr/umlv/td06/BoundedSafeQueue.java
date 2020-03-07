package fr.umlv.td06;

import java.util.ArrayDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class BoundedSafeQueue<V> {

	  private final ArrayDeque<V> fifo = new ArrayDeque<>();
	  private final ReentrantLock rLock = new ReentrantLock();
	  private final Condition areNotFull = rLock.newCondition();
	  private final Condition areNotEmpty = rLock.newCondition();
	  private final int capacity;
	  
	  public BoundedSafeQueue(int capacity) {
	    if (capacity <= 0) {
	      throw new IllegalArgumentException();
	    }
	    this.capacity = capacity;
	  }

	  /**
	   * 
	   * @param value
	   * @throws InterruptedException
	   */
	  public void put (V value) throws InterruptedException{
		  rLock.lock();
		  try {
			  while(fifo.size() == capacity) {
				  areNotFull.await();
			  }
			  fifo.add(value);
			  areNotEmpty.signal();
		  }finally {
			  rLock.unlock();
		  }
	  }
	  
	  /**
	   * 
	   * @return
	   * @throws InterruptedException
	   */
	  public V take() throws InterruptedException {
		  rLock.lock();
		  try {
			  while(fifo.isEmpty()) {
				  areNotEmpty.await();
			  }
			  areNotEmpty.signal();
			  return fifo.remove();
		  }finally {
			  rLock.unlock();
		  }
	  }
	  
	  
	  /*
	   * --> Il faut reveiller tous les threads bloque par les deux & conditions await()
	   * On utilise deux conditions. On nomme la condition en consequence.
	   * 
	   */
	  
	  public static void main(String[] args) throws InterruptedException{
		
		  var threads = new Thread[50];
		  var queue = new BoundedSafeQueue<>(3);
			
			for (int i = 0; i < threads.length; i++) {
				new Thread (() ->  {
					while(true) {
						try {
							Thread.sleep(2000);
							queue.put(Thread.currentThread().getName());
						} catch (InterruptedException e) {
							System.err.println(e);
						}
					}
				}).start();
			}
			while(true) {
				System.out.println(queue.take());
			}
				  
	}
}