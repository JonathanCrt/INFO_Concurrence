package fr.umlv.td09.exo1;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import fr.umlv.td09.Answer;
import fr.umlv.td09.Request;

public class FastestPooled{
	
	
	private final String item;
	private final int timeOutMilliPerRequest;
	private final int poolSize;
	
	public FastestPooled(String item, int timeOutMilliPerRequest, int timeoutMilliGlobal,
			int poolSize) {
		super();
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
        Request.ALL_SITES.forEach((site) -> {
        	tasks.add(() -> createRequest(site));
        });

        try {
        	var firstAnswer = ex.invokeAny(tasks);
        	// invokeAny : Renvoi la premiere reponse sans lever d'exception
    		ex.shutdown();
    		
    		if(!firstAnswer.isSuccessful()){
    			return Optional.empty();
    		}
    		else {
    			return Optional.of(firstAnswer);
    		}
    			
    	} catch (ExecutionException e) {
    		return Optional.empty();
      
		} finally {
			ex.shutdownNow(); // referme le Executor service
		}
    	
    }	

	
}
