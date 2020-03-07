package fr.upem.concurrence.td05;


public class Exchanger<T> {
	
	private  T value1; 
	private  T value2;
	private boolean firstDone; // false by default
	private boolean finished; // false by default
	private final Object lock = new Object();
	private  State state;
	
	
	private static enum State {
	 EMPTY, FIRST, FULL
	}
	
	public Exchanger(){
		synchronized (lock) {
			this.state =  State.EMPTY;
		}
	}
	
	

	/**
	 * two threads can execute this method
	 * @param valparam
	 * @return
	 * @throws InterruptedException
	 */
	
	/*
	public T exchange(T valparam) throws InterruptedException {
		synchronized (lock) {
			// first call
			if(!firstDone) {
				this.value1 = valparam;
				while(!finished) {
					lock.wait();
				}
				return value2;
			}
			// second call 
			else  {
				value2  = valparam;
				finished = true;
				lock.notify();
				return value1;
			}
				
		}
		*/
	
	
	public T exchange(T valparam) throws InterruptedException {
		synchronized (lock) {
			
			
			/*
			 * Dés le premier à renvoyé sa valeur, 
			 * 
			 */
			
			while(state == State.FULL) {
				lock.wait();
			}
			switch (state) {
			case EMPTY:
				this.value1 = valparam;
				state = State.FIRST;
				while(state != State.FULL) {
					lock.wait();
					 
				}
				state = State.EMPTY;
				return this.value2;
				
			case FIRST:
				this.value2 = valparam;
				state = State.FULL;
				//lock.notify();
				lock.notifyAll();
				return this.value1;	
			case FULL:
				throw new AssertionError();
				
			default:
				throw  new AssertionError(); // code qui ne fonctionne que pour 2 thread
		}
			 
		}
				
	}
	
	
	
	
	
	
	
	
	
}
