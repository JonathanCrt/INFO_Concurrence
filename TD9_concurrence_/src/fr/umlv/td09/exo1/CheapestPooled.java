package fr.umlv.td09.exo1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import fr.umlv.td09.Answer;
import fr.umlv.td09.Request;

public class CheapestPooled {
	
	private final String item;
	private final int timeOutMilliPerRequest;
	private final int poolSize;

	public CheapestPooled(String item, int timeOutMilliPerRequest, int poolSize) {
		this.item = item;
		this.timeOutMilliPerRequest = timeOutMilliPerRequest;
		this.poolSize = poolSize;
		
	}
	/**
	 * create a request
	 * @param site
	 * @return 
	 * @throws InterruptedException 
	 */
	private Answer createRequest(String site) throws InterruptedException {
		var req = new Request(site, this.item);
		return req.request(timeOutMilliPerRequest);
	}
	
	
	/**
     * @return the cheapest price for item if it is sold
     */
    public Optional<Answer> retrieve() throws InterruptedException {
    	
    	var ex = Executors.newFixedThreadPool(this.poolSize);
    	var tasks =  new ArrayList<Callable<Answer>>();
    	Request.ALL_SITES.forEach((site) -> tasks.add(() -> createRequest(site)));
    	var futures = ex.invokeAll(tasks);
    	
    	var arrayOfAnswer = new ArrayList<Answer>();
    	for(var future: futures) {
    		try {
				var answer = future.get();
				if(answer.isSuccessful()) {
					arrayOfAnswer.add(answer);
				}
			} catch (ExecutionException e) {
				return Optional.empty();
			}
    	}
    	return arrayOfAnswer
    			.stream()
    			.min(Comparator.comparing(Answer::getPrice));
    }	
	
	
	public static void main(String[] args) throws InterruptedException {
		var cheapestPooled = new CheapestPooled("tortank", 2_000, 10);
	    System.out.println(cheapestPooled.retrieve()); // Optional[tortank@laredoute.fr:219]
	}
	
}
