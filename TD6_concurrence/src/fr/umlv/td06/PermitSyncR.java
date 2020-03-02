package fr.umlv.td06;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * TD6 concurrence
 * Exercice 2
 * @author jonathan
 * @param <V>
 */

public class PermitSyncR<V> {
	
	private int nbThreads;
	private int nbPermits;
	private final ReentrantLock rLock = new ReentrantLock();
	private Condition isOkToExecute = rLock.newCondition();
	
	public PermitSyncR(int permits) {
		this.rLock.lock();
		try {
			if(permits <= 0) {
				throw new IllegalStateException("error initialization : number of permits must be positive !");
			}
			this.nbPermits = permits;
		} finally {
			rLock.unlock();
		}
	}
	
	/**
	 * 
	 * @param supplier
	 * @return
	 * @throws InterruptedException
	 */
	@SuppressWarnings("finally")
	public V safe(Supplier<? extends V> supplier) throws InterruptedException {
		rLock.lock();
		try {
			while(this.nbPermits <= nbThreads) {
				isOkToExecute.await(); // warning: no wait() method!
			}
			this.nbThreads++;
			isOkToExecute.signal(); // warning: no notify() method!
		} finally {
			nbThreads--;
			rLock.unlock();
			return supplier.get();
		}
		
	}
}
