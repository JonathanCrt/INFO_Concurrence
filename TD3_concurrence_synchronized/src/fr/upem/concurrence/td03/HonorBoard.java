package fr.upem.concurrence.td03;

public class HonorBoard {
	  private String firstName;
	  private String lastName;
	  
	  private  final Object lock = new Object();
	  
	  
	  public void set(String firstName, String lastName) {
		synchronized (lock) {
			this.firstName = firstName;
		    this.lastName = lastName;
		}
	    
	  }
	  
	  @Override
	  public String toString() {
		synchronized (lock) {
			return firstName + ' ' + lastName;
		}
	    
	  }
	  
	  public String getFirstName() {
		  return firstName;
	  }
	  public String getLastName() {
		    return lastName;
	  }
	  
	  public static void main(String[] args) {
		  
	    HonorBoard board = new HonorBoard();
	    new Thread(() -> {
	      for(;;) {
	        board.set("John", "Doe");
	      }
	    }).start();
	    
	    new Thread(() -> {
	      for(;;) {
	        board.set("Jane", "Odd");
	      }
	    }).start();
	    
	    new Thread(() -> {
	      for(;;) {
	        //System.out.println(board);
	    	  /*
	    	   * Pas de synchronisation
	    	   * prenom d'un moment - laché le lock -- affichage du mauvaus prénom
	    	   * Ne fonctionne pas avec un lock car on laché le lock
	    	   */
	    	  System.out.println(board.getFirstName() + ' ' + board.getLastName());
	      }
	    }).start();
	  }
	}