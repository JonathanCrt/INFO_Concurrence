package fr.umlv.td08.exo3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

import fr.umlv.td08.Answer;
import fr.umlv.td08.Request;

public class CheapestPooledWithFixedThreadPool {
	
	  private final String item;
	  private final int timeoutMilliPerRequest;
	  private static final int SIZE = Request.ALL_SITES.size();
	  private final FixedThreadPool fixedThreadPool;
	  private final ArrayBlockingQueue<String> siteQueue = new ArrayBlockingQueue<>(SIZE);
	  private final ArrayBlockingQueue<Answer> answerQueue = new ArrayBlockingQueue<>(SIZE);
	  

	  public CheapestPooledWithFixedThreadPool(String item, int poolSize, int timeoutMilliPerRequest) {
	    this.item = item;
	    this.timeoutMilliPerRequest = timeoutMilliPerRequest;
	    this.fixedThreadPool = new FixedThreadPool(poolSize);
	  }

	  private void createRequest() {
	    while (!Thread.interrupted()) {
	      try {
	        var site = siteQueue.take();
	        var request = new Request(site, item);
	        var answer = request.request(timeoutMilliPerRequest);
	        this.answerQueue.put(answer);
	        
	      } catch (InterruptedException e) {
	        break;
	      }
	    }
	  }

	  /**
	   * @return the cheapest price for item if it is sold
	   */
	  public Optional<Answer> retrieve() throws InterruptedException {
	    siteQueue.addAll(Request.ALL_SITES);

	    try {
	      Request.ALL_SITES.forEach((site) ->{
	        try {
	          this.fixedThreadPool.submit(this::createRequest);
	          
	        } catch (InterruptedException e) {
	          return;
	        }
	      });

	      this.fixedThreadPool.start();

	      var arrayOfAnswers = new ArrayList<Answer>();
	      
	      Request.ALL_SITES.forEach((site) -> {
	        try {
	          var answer = answerQueue.take();
	          if (answer.isSuccessful()) {
	        	  arrayOfAnswers.add(answer);
	          }
	        } catch (InterruptedException e) {
	          return;
	        }
	      });

	      return arrayOfAnswers.stream()
	    		  	  .min(Comparator.comparing(Answer::getPrice));
	    } finally {
	      this.fixedThreadPool.stop();
	    }
	  }

	  public static void main(String[] args) throws InterruptedException {
	    var cheapestPooledWithFixedThreadPool = new CheapestPooledWithFixedThreadPool("tortank", 2, 2_000);
	    System.out.println(cheapestPooledWithFixedThreadPool.retrieve()); // Optional[tortank@laredoute.fr:219]
	  }

	}