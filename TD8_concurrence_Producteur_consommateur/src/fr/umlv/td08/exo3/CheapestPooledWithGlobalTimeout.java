package fr.umlv.td08.exo3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

import fr.umlv.td08.Answer;
import fr.umlv.td08.Request;

public class CheapestPooledWithGlobalTimeout {
	
	  private final String item;
	 
	  private long duration;
	  private boolean isGlobalMilliTimeout;
	  private final int timeoutMilliPerRequest;
	  private final int timeoutMilliGlobal;
	  private final ArrayBlockingQueue<String> siteQueue = new ArrayBlockingQueue<>(Request.ALL_SITES.size());
	  private final ArrayBlockingQueue<Answer> answerQueue = new ArrayBlockingQueue<>(Request.ALL_SITES.size());
	  private final FixedThreadPool fixedThreadPool;
	  private final static int TIME = 1000000;

	  public CheapestPooledWithGlobalTimeout(String item, int poolSize, int timeoutMilliPerRequest,
	      int timeoutMilliGlobal) {
	    this.item = item;
	    this.duration = 0;
	    this.timeoutMilliPerRequest = timeoutMilliPerRequest;
	    this.timeoutMilliGlobal = timeoutMilliGlobal;
	    this.fixedThreadPool = new FixedThreadPool(poolSize);
	    
	  }
	  
	  /**
	   * 
	   */
	  private void createRequest() {
	    while (!Thread.interrupted()) {
	      long startTimeRequest = System.nanoTime();
	      long endTimeRequest = System.nanoTime();
	      
	      try {
	        var site = siteQueue.take(); // remove
	        var req = new Request(site, item);
	        var answer = req.request(this.timeoutMilliPerRequest);
	        
	        answerQueue.put(answer);
	      } catch (InterruptedException e) {
	        return;
	      }
	      
	      this.duration = (endTimeRequest - startTimeRequest) / TIME;
	    }
	  }

	  /**
	   * @return the cheapest price for item if it is sold
	   */
	  public Optional<Answer> retrieve() throws InterruptedException {
	    this.siteQueue.addAll(Request.ALL_SITES);

	    try {
	      Request.ALL_SITES.forEach((site) ->{
	        try {
	          this.fixedThreadPool.submit(() -> this.createRequest());
	        } catch (InterruptedException e) {
	          return;
	        }
	      });
	      fixedThreadPool.start();

	      var arrayOfAnswers = new ArrayList<Answer>();
	      Request.ALL_SITES.forEach((site) ->{
	        try {
	          var ans = answerQueue.take();
	          if (ans.isSuccessful()) {
	        	  arrayOfAnswers.add(ans);
	          }
	          
	        } catch (InterruptedException e) {
	          return;
	        }
	      });
	      
	      if(isGlobalMilliTimeout) {
	    	  return Optional.empty();
	      }
	      else {
	    	return  
	    			arrayOfAnswers
	    			.stream()
	    	  		.min(Comparator.comparing(Answer::getPrice));
	      }
	     
	    } finally {
	      fixedThreadPool.stop();
	    }
	  }
	
	
	public static void main(String[] args) throws InterruptedException {
		System.out.println(new CheapestPooledWithGlobalTimeout("tortank", 3,2_000,1_000).retrieve()); // Optional.empty
		System.out.println(new CheapestPooledWithGlobalTimeout("tortank", 3,2_000,4_000).retrieve()); // Optional[tortank@amazon.uk : 254]
	}
}
