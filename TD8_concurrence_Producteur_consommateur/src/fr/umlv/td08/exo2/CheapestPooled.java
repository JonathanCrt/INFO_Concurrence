package fr.umlv.td08.exo2;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

import fr.umlv.td08.Answer;
import fr.umlv.td08.Request;

public class CheapestPooled {
	
	private final String item;
	private final int timeOutMilliPerRequest;
	private static int SIZE = Request.ALL_SITES.size();
	private final ArrayBlockingQueue<String> siteQueue;
	private final ArrayBlockingQueue<Answer> answerQueue;
	private final int poolSize;
	private final ArrayList<Thread> workersThreads;
	
	
	public CheapestPooled(String item, int timeOutMilliPerRequest, int poolSize) {
		this.item = item;
		this.timeOutMilliPerRequest = timeOutMilliPerRequest;
		this.poolSize = poolSize;
		this.siteQueue = new ArrayBlockingQueue<String>(SIZE, true, Request.ALL_SITES);
		this.answerQueue = new ArrayBlockingQueue<Answer>(SIZE);
		this.workersThreads = new ArrayList<Thread>();
	}
	
	/**
	 * create a request
	 * @param site
	 */
	private void createRequest(String site) {
		try {
			var req = new Request(site, this.item);
			var answer  = req.request(timeOutMilliPerRequest);
			answerQueue.put(answer);  
		} catch (Exception e) {
			return;
		}
	}
	
	/**
	 * interrupt all threads
	 */
	private void interruptAllThreads() {
		workersThreads.forEach(Thread::interrupt);
	}
	
	/**
	 * 
	 */
	private void supplyWorkersThreads() {
		while(this.siteQueue.size() > 0) {
			for(var i = 0; i < poolSize; i++) {
				Thread t = new Thread(() ->  {
					try {
						var site = siteQueue.take(); // feed a thread with a site of siteQueue
						this.createRequest(site);
					} catch (InterruptedException e) {
						return;
					}
				});
				t.start();
				this.workersThreads.add(t);
			}
		}
	}
	
	
	/**
     * @return the cheapest price for item if it is sold
     */
    public Optional<Answer> retrieve() throws InterruptedException {
    	try {
    		
    		this.supplyWorkersThreads();
    		
    		Answer cheapestPrice = null;
    		var price = Integer.MAX_VALUE;
    		int nbAnswers = 0;
    		
    		while(nbAnswers < Request.ALL_SITES.size()) {
    			var answer = answerQueue.take();
    			if(answer.isSuccessful()) {
    				if(answer.getPrice() < price) {
    					cheapestPrice = answer;
    				}
    			}
    			nbAnswers++;
    		}
    		
    		if(cheapestPrice == null) {
    			Optional.empty();
    		}
    		return Optional.of(cheapestPrice);
		} finally {
			interruptAllThreads();
		}
    }	
	
	
	public static void main(String[] args) throws InterruptedException {
		var cheapestPooled = new CheapestPooled("tortank", 2_000, 10);
	    System.out.println(cheapestPooled.retrieve()); // Optional[tortank@laredoute.fr:219]
	}
	
}
