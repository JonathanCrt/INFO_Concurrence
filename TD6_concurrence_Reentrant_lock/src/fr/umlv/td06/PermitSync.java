package fr.umlv.td06;

import java.util.function.Supplier;

/**
 * TD6 concurrence
 * Exercice 1
 * @author jonathan
 * @param <V>
 */

public class PermitSync<V> {
	
	private boolean isOkToExecute;
	private int nbThreads;
	private int nbPermits;
	private final Object lock = new Object();
	
	public PermitSync(int permits) {
		synchronized (lock) {
			if(permits <= 0) {
				throw new IllegalStateException("error initialization : number of permits must be positive !");
			}
			this.isOkToExecute = true;
			this.nbPermits = permits;
		}
	}
	
	/**
	 * 
	 * @param supplier
	 * @return
	 * @throws InterruptedException
	 */
	public V safe(Supplier<? extends V> supplier) throws InterruptedException {
		synchronized (lock) {
			while(!isOkToExecute) {
				lock.wait();
			}
			if(this.nbPermits <= nbThreads) {
				isOkToExecute = false;
			} 
			else {
				this.nbThreads++;
				this.isOkToExecute = true;
				lock.notify();
			}
			this.nbThreads--;
			return supplier.get(); 
			
		}
		
		
	}
}