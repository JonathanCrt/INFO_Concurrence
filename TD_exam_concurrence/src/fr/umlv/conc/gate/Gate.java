package fr.umlv.conc.gate;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class Gate {
	
	private final int nbThreads;
	private ReentrantLock rLock  = new ReentrantLock();
	private Condition cond = rLock.newCondition();
	private int count; // def
	private boolean isInterrupted; // def
	

	public Gate(int nbThreads) {
		this.nbThreads = nbThreads;
	}

	public void waitAt() throws InterruptedException {
		
		rLock.lock();
		try {
			this.count++;
			if(this.count == this.nbThreads) {
				cond.signalAll();
			}
			
			while(this.count < this.nbThreads && !Thread.interrupted()) {
				try {
					cond.await();
				} catch (InterruptedException e) {
					System.out.println(Thread.currentThread().getName());
					this.isInterrupted = true;
					this.count = this.nbThreads;
					this.cond.signalAll();
					throw new InterruptedException();
				}
			}
			if(this.isInterrupted) {
				throw new InterruptedException();
			}
		
		} finally {
			rLock.unlock();
		}		
		
		
	}


	public static void main(String[] args) throws InterruptedException {
		var nbThreads = 100;
	    var tab = new Thread[nbThreads];

	    var barrier = new Gate(nbThreads);

	    IntStream.range(0, nbThreads).forEach(i -> {
	      tab[i] = new Thread(() -> {
	        try {
	          barrier.waitAt();
	          System.out.print(i + " ");

	        } catch (InterruptedException e) {
	          return;
	        }
	      });
	    });
	    for (var i = 0; i < nbThreads; i++) {
	      tab[i].start();
	    }

	    Thread.sleep(100);
	    tab[50].interrupt();
	}
}
