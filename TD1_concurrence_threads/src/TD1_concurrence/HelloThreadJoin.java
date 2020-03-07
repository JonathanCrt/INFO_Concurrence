package TD1_concurrence;

public class HelloThreadJoin {
	 
	private static int  NB_THREADS = 4;
	
	public static void main(String[] args) throws InterruptedException {
		
		Thread[] tab_thread = new Thread[NB_THREADS];
		
		for(int i = 0; i < NB_THREADS; i++) {
			int id = i;
			tab_thread[i] = new Thread(() -> {
				for(int j = 0; j < 5000; j++) {
					System.out.println("hello " + id + " "+ j);
				}
			});
			tab_thread[i].start();
		}
		for(int k = 0; k <NB_THREADS; k++) {
			tab_thread[k].join();
		}
        System.out.println("Le thread a fini son Runnable");
	}
}
