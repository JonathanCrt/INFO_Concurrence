package fr.upem.concurrence.td02;

class ExempleReordering {
	  int a = 0;
	  int b = 0;

	  public static void main(String[] args) {
	    ExempleReordering e = new ExempleReordering();
	    new Thread(() -> { System.out.println("a = " + e.a + "  b = " + e.b); }).start();
	    e.a = 1;
	    e.b = 2;
	  }
	  /**
	   * 1)
	   * a= 1 et b = 2 
	   * Le main s'est éxécuté en entier 
	   * le main puis le thread
	   * 
	   * a = 0 et b = 0 
	   * thread puis main
	   * 
	   * a = 1 et b = 0
	   * main jusqu'a après a = 1 puis thread 
	   * 
	   * a= 0 b = 2 
	   * Le JIT  à changé l'odre des affectation 
	   * main jusqu'a b = 2 puis thread
	   */
	}