package fr.umlv.td07;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TD7 concurrence
 * @author jcrete
 */
public class ThePriceIsRight {
	
	private final int realPrice;
	private final int nbPlayers;
	private String threadWinner;
	private int attemptsToFindPrice;
	private LinkedHashMap<String, Integer> mapThreads;
	private final ReentrantLock rLock = new ReentrantLock();
	private final Condition wakeUp = rLock.newCondition();
	
	/**
	 * Deux threads peut avoir le meme nom ! 
	 */
	
	
	/**
	 * 
	 * @param realPrice
	 * @param nbPlayers
	 */
	public ThePriceIsRight(int realPrice, int nbPlayers) {
		if(realPrice < 0 || nbPlayers < 1) {
			throw new IllegalArgumentException("We need positives values to begin the game !");
		}
		this.realPrice = realPrice;
		this.nbPlayers = nbPlayers;
		this.mapThreads = new LinkedHashMap<String, Integer>();
		rLock.lock();
		try {
			this.threadWinner = new String();
			this.attemptsToFindPrice = 0;
			
		} finally {
			rLock.unlock();
		}
	}
	
	/**
	 * calculate distance between price and just price
	 * @param price
	 * @return  distance
	 */
	private int distance(int price) {
		  return Math.abs(price - this.realPrice);
	}
	
	
	/*
	public Optional<Entry<String, Integer>> findWinner() {
		return  mapThreads.
				entrySet().
				stream()
				.min((k1, k2) -> Integer.compare(this.distance(k1.getValue()), this.distance(k2.getValue())));
	}
	*/

	/***
	 * return true if price is find
	 * @param price
	 * @return
	 */
	public boolean propose (int price) {
		if(price < 0) {
			throw new IllegalArgumentException("need a positive price");
		}
		
		if(this.nbPlayers < this.attemptsToFindPrice + 1) {
			return false;
		}
		rLock.lock();
		try {
			mapThreads.put(Thread.currentThread().getName(), price);
			this.attemptsToFindPrice++;
			
			if(this.attemptsToFindPrice == this.nbPlayers) {
				findWinner();
				wakeUp.signalAll();
			}
			
			while(this.nbPlayers > this.attemptsToFindPrice) {
				try {
					wakeUp.await();
				} catch (InterruptedException ie) {
					mapThreads.remove(Thread.currentThread().getName());
					findWinner();
					wakeUp.signalAll();
					return false; //  return false for all threads
				}
			}
			
		} finally {
			rLock.unlock();
		}
		
		if(this.threadWinner.equals(Thread.currentThread().getName())){
			return true;
		}
		else {
			return false;
		}
		
	}
	
	
	/**
	 * calculate winner if exists
	 * @return 
	 */
	private void calculateWinner() {
		var dist = 0;
        var bigInt = Integer.MAX_VALUE;
        
        for (var potentialWinner : mapThreads.keySet()) {
            dist = Math.abs(distance(mapThreads.get(potentialWinner)));
            if (bigInt> dist) {
                bigInt = dist;
                
                threadWinner = potentialWinner;
            }
        }
        this.attemptsToFindPrice = this.nbPlayers;
	}
	
	/**
	 * public method exposed
	 */
	public void findWinner() {
		this.calculateWinner();
	}
	

	@Override
    public boolean equals(Object o) {
        if (!(o instanceof ThePriceIsRight)) {
        	return false;
        }
        ThePriceIsRight that = (ThePriceIsRight) o;
        return nbPlayers == that.nbPlayers &&
                realPrice == that.realPrice &&
                attemptsToFindPrice == that.attemptsToFindPrice &&
                Objects.equals(mapThreads, that.mapThreads);
    }
	

	public static void main(String[] args) {
		/**
		 *  1 -
		 *  GESTION DE L ALGO 
		 * 	champ et structure de donnee
		 *  LinkedHashMap -> table de hachage qui se souvient de l'ordre 
		 *  stocke les threads dans l'ordre d arrivee
		 *  2-
		 *  GESTION DE LA CONCURRENCE : 
		 * 	Enum pour indiquer les diff etats possible
		 *  3- 
		 *  NE PAS GERER LES INTERUPTIONS DANS LA PREMIER TEMPS -> ASSERTIONERROR
		 *  
		 *  Note : Je n'ai pas r�ussi � impl�menter la map avec comme cl� les Threads, j'ai donc choisi d'utiliser les noms des threads
		 *  comme cl�.
		 */
	}

}
