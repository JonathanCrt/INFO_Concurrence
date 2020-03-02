package fr.umlv.td09;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CheapestPooledWithGlobalTimeout {
	
	
	private final String item;
	private final int timeOutMilliPerRequest;
	private final int timeoutMilliGlobal;
	private final int poolSize;
	
	public CheapestPooledWithGlobalTimeout(String item, int timeOutMilliPerRequest, int timeoutMilliGlobal,
			int poolSize) {
		super();
		this.item = item;
		this.timeOutMilliPerRequest = timeOutMilliPerRequest;
		this.timeoutMilliGlobal = timeoutMilliGlobal;
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
    	try {
    		var tasks =  new ArrayList<Callable<Answer>>();
        	Request.ALL_SITES.forEach((site) -> tasks.add(() -> createRequest(site)));
        	var futures = ex.invokeAll(tasks, this.timeoutMilliGlobal, TimeUnit.MILLISECONDS);
        	// invokeAny : Renvoi la premiere reponse sans lever d'exception
        	
        	
        	var arrayOfAnswer = new ArrayList<Answer>();
        	for(var future: futures) {
        		try {
    				 arrayOfAnswer.add(future.get());
    				 ex.shutdown();
    				/*
    				if(answer.isSuccessful()) {
    					arrayOfAnswer.add(answer);
    				}
    				*/
    			} catch (ExecutionException e) {
    				return Optional.empty();
    			}
        	}
        	
        	return arrayOfAnswer
        			.stream()
        			.filter(Answer::isSuccessful)
        			.min(Answer::compareTo);
        	/*
        	return arrayOfAnswer
        			.stream()
        			.min(Comparator.comparing(Answer::getPrice));
        	*/
		} finally {
			ex.shutdownNow(); // referme le Executor service
		}
    	
    }	
	
	
	public static void main(String[] args) throws InterruptedException {
		var cheapestPooled = new CheapestPooled("tortank", 2_000, 10);
	    System.out.println(cheapestPooled.retrieve()); // Optional[tortank@laredoute.fr:219]
	}
	
	
	
	
}
