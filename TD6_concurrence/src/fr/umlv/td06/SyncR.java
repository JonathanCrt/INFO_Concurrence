package fr.umlv.td06;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class SyncR<V> {
	
	private final ReentrantLock rLock = new ReentrantLock();
	private boolean isExecuting;
	private final Condition condition = rLock.newCondition();
	
	/**
	 * 
	 * @return
	 */
	public boolean inSafe() {
		rLock.unlock();
		try {
			return isExecuting;
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
	public V safe(Supplier<? extends V> supplier) throws InterruptedException { //  une seule lambda s'execute a la fois
		
		rLock.lock();
		try {
			while(isExecuting) {
				condition.await();
			}
			isExecuting = true;
		} finally {
			rLock.unlock();
		}
		
		try {
		    return supplier.get(); // subtilite : il n'y a pas de barriere memoire
		} finally {
		    rLock.lock();
		    try {
		        isExecuting = false;
		        condition.signal();
		    } finally {
		        rLock.unlock();
		    }
		}
	}
	
	
}
