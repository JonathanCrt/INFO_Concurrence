package TD1_concurrence;

public class TurtleRace {
	public static void main(String[] args) throws InterruptedException {
		 System.out.println("On your mark!");
		 Thread.sleep(30_000);
		 System.out.println("Go!");
		 int[] times = {25_000, 10_000, 20_000, 5_000, 50_000, 60_000};
		 
		 /*
		 Runnable runnable = () -> {
			 System.out.println(Thread.currentThread().getName() + "has finished");
		 };
		  
		 */
		 Thread[] tab_threads = new Thread[times.length];
		 
		 for(int i = 0; i < times.length; i++) {
			 int id = i;
			 
			 tab_threads[i] = new Thread(() ->  {
				 try {
					Thread.sleep(times[id]);
				 } catch(InterruptedException e) {
					 throw new AssertionError(e);
				 } 
				 System.out.println("Turtle " + Thread.currentThread().getName() + " has finished");
			 });
			
			 tab_threads[i].setName("" + i);
			 tab_threads[i].start();
			
		 }
				  
	}

}
