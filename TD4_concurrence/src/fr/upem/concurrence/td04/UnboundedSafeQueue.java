package fr.upem.concurrence.td04;

import java.util.ArrayDeque;

public class UnboundedSafeQueue<V> {
	
	private final ArrayDeque<V> table =  new ArrayDeque<>(); 
	private final Object lock = new Object();
	
	/***
	 * add synchronized value to ArrayDeque
	 * @param value
	 */
	public void add(V value) {
		synchronized (lock) {
			table.add(value);
			lock.notifyAll();
		}
	}
	
	/**
	 * remove first element and return it
	 * @return V type of element
	 * @throws InterruptedException 
	 */
	public V take() throws InterruptedException {
		synchronized (lock) {
			while(table.isEmpty()) {
				lock.wait();
			}
			lock.notifyAll();
			return table.removeFirst();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		var threads = new Thread[3];
		var queue = new UnboundedSafeQueue<>();
		
		for (int i = 0; i < threads.length; i++) {
			new Thread (() ->  {
				while(true) {
					try {
						Thread.sleep(2000);
						queue.add(Thread.currentThread().getName());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		while(true) {
			System.out.println(queue.take());
		}
		
	}
}
