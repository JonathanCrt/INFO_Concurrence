package fr.umlv.td06;

import java.util.ArrayDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class UnboundedSafeQueue<V> {

	  private final ArrayDeque<V> fifo = new ArrayDeque<>();
	  private final ReentrantLock rLock = new ReentrantLock();
	  private final Condition condition = rLock.newCondition();
	  
	  public void add(V value) {
		  rLock.lock();
		  try {
			  fifo.add(value);
			  condition.signal();
		  }finally {
			  rLock.unlock();
		  }
	  }
	  
	  public V take() throws InterruptedException {
		  rLock.lock();
		  try {
			while(fifo.isEmpty()) {
				condition.await();
			}
			return fifo.remove();
		  } finally {
			rLock.unlock();
		}
	  }
}