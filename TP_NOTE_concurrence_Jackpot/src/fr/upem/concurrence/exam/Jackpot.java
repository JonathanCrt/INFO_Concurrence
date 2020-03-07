package fr.upem.concurrence.exam;

import java.util.Random;
import java.util.stream.IntStream;

public class Jackpot {
	private int counter;
	private final int target;
	private final Object lock = new Object();

	public Jackpot() {
		synchronized (lock) {
			this.counter = 0;
			this.target = 1 + new Random().nextInt(50);
		}	
	}
	
	public boolean play() {
		synchronized (lock) {
			this.counter++;
			//System.out.println(target);
			//System.out.println(counter);
			while (this.counter < this.target) {
				return false;
			}
			if (this.target == this.counter) {
				return true;
			}
			else {
				throw new IllegalStateException();
			}
		}
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		
		Jackpot jp = new Jackpot();
		var nbThreads = 5;
		
		/*
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
		*/
		
		IntStream.range(0, nbThreads).forEach(i -> {
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
		});
	}
	
	
}
