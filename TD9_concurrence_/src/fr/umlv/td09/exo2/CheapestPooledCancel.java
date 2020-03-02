package fr.umlv.td09.exo2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import fr.umlv.td09.Answer;
import fr.umlv.td09.Request;

public class CheapestPooledCancel {
	private final String item;
	private final int poolSize;
	private ArrayBlockingQueue<RequestWithCancel> tmpArray = new ArrayBlockingQueue<RequestWithCancel>(10);
	
	public CheapestPooledCancel(String item, int poolSize) {
		this.item = item;
		this.poolSize = poolSize;
	}
	
	/**
	 * create a request
	 * @param site
	 * @return 
	 * @throws InterruptedException 
	 */
	private Answer createRequest(String site) throws InterruptedException {
		var req = new RequestWithCancel(site, this.item);
		this.tmpArray.put(req);
		return req.request();
	}
	
	/**
     * @return the cheapest price for item if it is sold
     */
    public Optional<Answer> retrieve() throws InterruptedException {
    	
    	
    	var scheduler = Executors.newScheduledThreadPool(this.poolSize);
    	var tasks = new ArrayList<Callable<Answer>>();
    	var tmpArray = new ArrayBlockingQueue<RequestWithCancel>(10);
    	
    	
    	Request.ALL_SITES.forEach((site) -> {
    		tasks.add(() -> this.createRequest(site));
    	});
    	
    	
    	Runnable canceller  = () -> {
    		while(!tmpArray.isEmpty() &&!Thread.interrupted()) {
				try {
					this.tmpArray.take().cancel();
				} catch (InterruptedException e) {
					return;
				}
			}
    	};
    	scheduler.schedule(canceller, 5, TimeUnit.MILLISECONDS);
    	
    	var futures = scheduler.invokeAll(tasks);
    	
    	var arrayOfAnswer = new ArrayList<Answer>();
    	for(var future: futures) {
    		try {
				var answer = future.get();
				if(answer.isSuccessful()) {
					arrayOfAnswer.add(answer);
				}
			} catch (ExecutionException e) {
				return Optional.empty();
			} finally {
				scheduler.shutdownNow();
			}
    	}
    	return arrayOfAnswer
    			.stream()
    			.min(Comparator.comparing(Answer::getPrice));
    }	
	
	
    public static void main(String[] args) throws InterruptedException {
    	var cheapestPooledCancel = new CheapestPooledCancel("tortank", 10);
	    System.out.println(cheapestPooledCancel.retrieve()); // Optional[tortank@laredoute.fr:219]
	}
    
    
    
	
	
	
	
}
