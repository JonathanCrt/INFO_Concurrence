package fr.umlv.td07;

/**
 * 
 * @author jcrete
 *
 */

public class Exercice1 {
	/**
	 * 1 - JAVA ne donne aucune fonctionnalite pour forcer un thread de s arreter.
	 * 2 - Operation bloquante =  operation qui qui bloque le thread en cours en  attendant que la ressource demandée soit disponible
	 * 3- La methode interrupt() de la classe Thread permet d envoyer un signal d interruption en demandant au thread de s arreter
	 * 4- On traite l'interruptedException car la methode bloquante sleep() va checker le statut d interruption avant de s executer
	 * 5- Si on peut modifier le code et la signature de la methode, on leve une exception -> throw new InterruptedException et 
	 * la signature de la méthode slow() à Optional<Integer>.
	 * 6 - Thread.interrupted et thread.isInterrupted (method static remet le statut d interruption a 0)   et (method d instance ne remet pas le statut d interruption a 0!)
	 * @throws InterruptedException 
	 */
	
	
	private static int slow() {
	    var result = 1;
	    for (var i = 0; i < 1_000_000; i++) {
	    	if(Thread.interrupted()) { // tres mal: on devrai lever une interruptedException pour rendre la methode interruptible
	    		// 5 -  
	    		// 
	    		Thread.currentThread().interrupt();
	    		break;
	    	}
	        result += (result * 7) % 513;
	    }
	    return result;
	}
	

	public static void main(String[] args) throws InterruptedException {
		/*
		var t = new Thread(() -> {
			while(true) {
				try {
					Thread.sleep(1_000);
				} catch (InterruptedException e) {
					System.out.println("end");
					break;
				}
			}
		});
		t.start();
		Thread.sleep(1_000);
		t.interrupt();
		*/
		
		var t = new Thread(() -> {
	        var forNothing = 0;
	        while (Thread.interrupted()) { // Tant que on pas a demander l interruption
	          forNothing += slow();
	        }
	        System.out.println("end " + forNothing);
	    });
	    t.start();
	    Thread.sleep(1_000);
	    t.interrupt();
		
		
		
	}
}

