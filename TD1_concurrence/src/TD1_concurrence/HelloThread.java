package TD1_concurrence;

public class HelloThread {
	
	private static int  NB_THREADS = 4;
	
	public static void main(String[] args) {

		/*
		Runnable runnable = () -> {
			
			for(int i = 0; i < 5000; i++) {
				System.out.println("hello " + i);
			}
		};
		
		for(int i = 0; i < 4; i++) {
			Thread t = new Thread(runnable);
			t.start();
		}
		*/
		for(int i = 0; i < NB_THREADS; i++) {
			int id = i;
			new Thread(() -> {
				for(int j = 0; j < 5000; j++) {
					System.out.println("hello " + id + " "+ j);
				}
			}).start();
		}
		
		
		
	}
}
