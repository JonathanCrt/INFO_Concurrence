package fr.upem.concurrence.td04;

import java.util.ArrayDeque;

public class BoundedSafeQueue <V> {
	private final ArrayDeque<V> table =  new ArrayDeque<>(); 
	private final Object lock = new Object();
	private final int MAXCAPACITY = 10;
	

	/***
	 * put synchronized value to ArrayDeque
	 * @param value
	 * @throws InterruptedException 
	 */
	public void put (V value) throws InterruptedException {
		synchronized (lock) {
			while(table.size() == MAXCAPACITY) {
				lock.wait();
			}
			table.add(value);
			lock.notifyAll();
		}
	}
	
	/**
	 * 
	 * @return
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
		var threads = new Thread[50];
		var queue = new BoundedSafeQueue<>();
		
		for (int i = 0; i < threads.length; i++) {
			new Thread (() ->  {
				while(true) {
					try {
						Thread.sleep(2000);
						queue.put(Thread.currentThread().getName());
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
