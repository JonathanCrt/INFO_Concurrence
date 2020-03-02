package fr.umlv.td08.exo1;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

import fr.umlv.td08.Answer;
import fr.umlv.td08.Request;

public class Fastest {
	
	private final int timeOutMilliPerRequest;
    private final String item;
	private final ArrayBlockingQueue<Answer> blockingQueue;
	private static int SIZE = Request.ALL_SITES.size();
	private final ArrayList<Thread> arrayOfThreads;
	
	public Fastest(String item, int timeOut) {
		this.item = item;
		this.timeOutMilliPerRequest = timeOut;
		this.blockingQueue = new ArrayBlockingQueue<>(SIZE);
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
			for(String site : Request.ALL_SITES) {
				Thread t = new Thread(()-> {
					createRequest(site);
				});
				arrayOfThreads.add(t);
				t.start();
			}
			
			int nbAnswer = 0;
			while(nbAnswer < Request.ALL_SITES.size()) { // des que je trouve une reponse ok 
				var answer = blockingQueue.take();
				if(answer.isSuccessful()) {
					return Optional.of(answer);
				}
				nbAnswer++;
			}
			
			return Optional.empty();
			
		} finally {
			this.interruptAllThreads();
		}
    }

	public static void main(String[] args) throws InterruptedException {
		
		var agregator = new Fastest("tortank", 2_000);
		var answer = agregator.retrieve();
		System.out.println(answer); // Optional[tortank@amazon.fr : 427]
	}
}
