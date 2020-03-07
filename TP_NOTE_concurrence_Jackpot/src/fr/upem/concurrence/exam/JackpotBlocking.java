package fr.upem.concurrence.exam;

import java.util.Random;

public class JackpotBlocking {
	private int counter;
	private final int target;
	private final Object lock = new Object();

	public JackpotBlocking() {
		synchronized (lock) {
			this.counter = 0;
			this.target = 1 + new Random().nextInt(50);
		}	
	}
	
	
	/*
	public boolean play() throws InterruptedException{
		synchronized (lock) {
			this.counter++;
			while (this.counter < this.target) {
				return false;
			}
			if (this.target == this.counter) {
				return true;
			}
			while(this.counter != this.target) {
				lock.wait();
			}
			lock.notifyAll();
			return true;
		}
	}
	*/
	public boolean play() throws InterruptedException {
        synchronized (lock) {
            counter++;
            while(counter < target) {
                lock.wait();
            }
            
            lock.notifyAll();
            if(counter == target) {
                return true;
            }
            return false;

        }
    }
	
	public static void main(String[] args) throws InterruptedException {
		
		JackpotBlocking jp = new JackpotBlocking();
		var nbThreads = 50;

		
		
		
		for(int i =0; i< nbThreads; i++) {
			new Thread(() -> {
				try {
					for(;;) {
						if(jp.play()) {
							System.out.println(Thread.currentThread().getName() + " won");
						}
						else {
							System.out.println(Thread.currentThread().getName() + " lost");
						}
						Thread.sleep(1000);
					}
					
				} catch (InterruptedException e) {
			        throw new AssertionError(e);
			     }
			}).start();
		}
		
		    
	}
}
	
	