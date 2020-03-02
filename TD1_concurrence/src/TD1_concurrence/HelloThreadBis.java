package TD1_concurrence;

public class HelloThreadBis {
	
	private static int  NB_THREADS = 4;
	
	public static void println(String s){
		  for(var i = 0; i < s.length(); i++){
		    System.out.print(s.charAt(i));
		  }
		  System.out.print("\n");
	}
	
	
	public static void main(String[] args) {
		
		for(int i = 0; i < NB_THREADS; i++) {
			new Thread(() -> {
				for(int j = 0; j < 5000; j++) {
					println("hello " + j);
				}
			}).start();
		}
		
		
	}
	
}

