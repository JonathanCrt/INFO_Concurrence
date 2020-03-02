package fr.umlv.td06;


public class Counter {
	private int counter;
	private final SyncR<Integer> synchronizer = new SyncR<>();

	
	/**
	 * 
	 * @param counter
	 * @return
	 * @throws InterruptedException
	 */
	public int count(int counter) throws InterruptedException {
		return synchronizer.safe(() -> this.counter++);
	} 
	
	public static void main(String[] args) {
		var nbThreads = 2;
	    for (var i = 0; i < nbThreads; i++) {
	      var cpt = 0;
	      var counterObj = new Counter();
	      var thread = new Thread(() -> {
	        for (;;) {
	          try {
				System.out.println(Thread.currentThread().getName() + ":" + counterObj.count(cpt));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        }
	      });
	      thread.start();
	    }
	}
	
}
