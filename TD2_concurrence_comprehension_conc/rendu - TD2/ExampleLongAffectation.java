package fr.upem.concurrence.td02;

class ExampleLongAffectation {
	  long l = -1L;

	  public static void main(String[] args) {
	    ExampleLongAffectation e = new ExampleLongAffectation();
	    new Thread(() -> {
	      System.out.println("l = " + e.l);
	    }).start();
	    e.l = 0;
	  }
	  
	  /**
	   * data-race sur le champ l
	   * Pas atomique
	   * 4 valeurs
	   * Long potentielemment deux opérations
	   * L'opération peut se stoper à cause du scheduler
	   * FFFF FFFF
	   * 0000 FFFF
	   * FFFF 0000
	   * FFFF FFFF 
	   * 
	   */
	}