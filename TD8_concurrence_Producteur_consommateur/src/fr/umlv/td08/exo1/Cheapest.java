package fr.umlv.td08.exo1;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

import fr.umlv.td08.Answer;
import fr.umlv.td08.Request;

public class Cheapest {
    private final String item;
	private final int timeOutMilliPerRequest;
	private final ArrayBlockingQueue<Answer> blockingQueue;
	private static int SIZE = Request.ALL_SITES.size();
	private final ArrayList<Thread> arrayOfThreads;
	
	public Cheapest(String item, int timeOutMilliPerRequest) {
		this.timeOutMilliPerRequest = timeOutMilliPerRequest;
		this.item = item;
		this.blockingQueue = new ArrayBlockingQueue<Answer>(SIZE);
		this.arrayOfThreads = new ArrayList<Thread>();
	}
	
	/**
	 * create a request
	 * @param site
	 */
	private void createRequest(String site) {
		try {
			var req = new Request(site, this.item);
			var answer  = req.request(timeOutMilliPerRequest);
			blockingQueue.put(answer);  // potentials methods :  put take & poll
		} catch (Exception e) {
			return;
		}
	}
	
	/**
	 * interrupt all threads
	 */
	private void interruptAllThreads() {
		arrayOfThreads.forEach((th)-> th.interrupt());
	}
	
	/**
     * @return the cheapest price for item if it is sold
     */
    public Optional<Answer> retrieve() throws InterruptedException {
    	try {
			Request.ALL_SITES.forEach((site)-> {
				var t = new Thread(() -> {
					createRequest(site);
				});
				arrayOfThreads.add(t);
				t.start();
			});
			
			Answer cheapestPrice = null;
			var price = Integer.MAX_VALUE;
			int nbAnswer = 0;
			
			while(nbAnswer < Request.ALL_SITES.size()) {
				var answer = blockingQueue.take();
				if(answer.isSuccessful()) {
					if(answer.getPrice() < price) {
						cheapestPrice = answer;
					}
				}
				if(cheapestPrice == null) {
					return Optional.empty();
				}
				nbAnswer++;
			}
			return Optional.of(cheapestPrice);
			
		} finally {
			interruptAllThreads();
		}
    }
	
	public static void main(String[] args) throws InterruptedException {
		System.out.println(new Cheapest("tortank",2_000).retrieve()); // Optional[tortank@laredoute.fr : 219]
		
		
		/**
		 * On pourrai supposer de lancer 1 thread par site soit 1000 threads. Mais ce n'est clairement pas raisonnable. 
		 * L'idée serai d'implémenter plusieurs files bornés  (une pour les requête et une pour les réponses par exemple)
		 * avec un nombre fixe de threads qui utilsent ses files pour "produire et consommer"
		 */
	}
	
	
}
