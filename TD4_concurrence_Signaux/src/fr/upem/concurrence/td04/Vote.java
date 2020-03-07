package fr.upem.concurrence.td04;

import java.util.HashMap;
import java.util.Objects;

public class Vote {
	private final HashMap<String, Integer> map;
	private final Object lock = new Object();
	private int size;
	private  int nbVotes;
	
	/**
	 * @param map
	 * @param nbVotes
	 */
	public Vote(int nbVotes) {
		this.map = new HashMap<>();
		this.nbVotes = Objects.requireNonNull(nbVotes);
		this.size = 0;
	}

	/**
	 * proposed a vote and blocks until the No votes arrived. Then it returns the winner
	 * @param st
	 * @return
	 * @throws InterruptedException 
	 */
	public String vote(String bullet) throws InterruptedException {
		Objects.requireNonNull(bullet);
		
		synchronized (lock) {
			// key -> voter / Value -> nbVotes 
			if(nbVotes > 0) {
				map.compute(bullet, (voter, nbVotes) -> {
					if(nbVotes == null) {
						return 1;
					}
					else {
						return (nbVotes + 1);
					}
				});
				this.nbVotes--;
			}
			while(nbVotes > 0) {
				lock.wait();
			}
			lock.notifyAll();
		}
		return this.computeWinner();
	}
	
	
	/**
	 * compute winner
	 * @return
	 */
	private String computeWinner() {
	    int currentWinnerScore = -1;
	    String currentWinner = null;
	    for (var e : map.entrySet()) {
	      if (e.getValue() > currentWinnerScore
	          || (e.getValue() == currentWinnerScore && e.getKey().compareTo(currentWinner) < 0)) {
	        currentWinner = e.getKey();
	        currentWinnerScore = e.getValue();
	      }
	    }
	    return currentWinner;
	  }	
	
	public static void main(String[] args) throws InterruptedException {
		Vote vote = new Vote(3);
	    new Thread(
	            () -> {
	              try {
	                Thread.sleep(10_000);
	                System.out.println("The winner is " + vote.vote("1"));
	              } catch (InterruptedException e) {
	                throw new AssertionError(e);
	              }
	            })
	        .start();
	    new Thread(
	            () -> {
	              try {
	                Thread.sleep(5_000);
	                System.out.println("The winner is " + vote.vote("0"));
	              } catch (InterruptedException e) {
	                throw new AssertionError(e);
	              }
	            })
	        .start();
	    System.out.println("The winner is " + vote.vote("0"));
	}

	
	
}
