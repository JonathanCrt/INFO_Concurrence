package fr.umlv.thepriceisright;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThePriceIsRight {
	
	public enum STATE_GAME{
		WAITVOTER, ENDVOTE
	};
	
	private final int realPrice;
	private final int nbPlayers;
	private int attempts;
	private Optional<Entry<Thread, Integer>> winner;
	private final LinkedHashMap<Thread, Integer> map;
	private final ReentrantLock rLock= new ReentrantLock();
	private final Condition wakeUp = rLock.newCondition();
	private STATE_GAME state;
	
	
	public ThePriceIsRight(int realPrice, int nbPlayers) {
		if(nbPlayers <= 0 || realPrice <= 0) {
			throw new IllegalArgumentException("Arguments must be positive");
		}
		this.realPrice = realPrice;
		this.nbPlayers = nbPlayers;
		this.map = new LinkedHashMap<Thread, Integer>();
	}
	

	private int distance(int price) {
		 return Math.abs(price - realPrice);
	}
	
	private Optional<Entry<Thread, Integer>> computeWinner() {
	    return map
	      .entrySet()
	      .stream()
	      .min((e1, e2) -> Integer.compare(distance(e1.getValue()), distance(e2.getValue())));
	}
	
	
	public boolean propose(int proposedPrice) {
		if(proposedPrice <= 0) { // vérification que le prix est correct
			throw new IllegalArgumentException("Price must be positive");
		}
		
		if(state == STATE_GAME.ENDVOTE) { // Si plus de threads que le nombre de participants indiqué à la construction
			return false;
		}
		
		rLock.lock();
		try {

			this.map.put(Thread.currentThread(), proposedPrice);
			this.attempts++;	// Si un thread soumet un prix
			
			if(this.attempts == this.nbPlayers) { // Si le nombre de tentatives est égal au nombre de joueurs
				this.state = STATE_GAME.ENDVOTE; // Fin des votes
				this.winner = this.computeWinner(); //on calcule le gagnant
				this.wakeUp.signalAll();
			}
			
			while(this.state  != STATE_GAME.ENDVOTE) {
				try {
					wakeUp.await();
				} catch (InterruptedException e) {
					this.map.remove(Thread.currentThread()); // Si un thread qui a déjà proposé un prix est interrompu, son prix est retiré des prix pris en compte
					this.winner = this.computeWinner(); //Le gagnant est calculé avec les threads restants
					this.state = STATE_GAME.ENDVOTE;
					this.wakeUp.signalAll(); // Les Threads sont débloqués
					return false;
				}
			}
			
			if(this.winner.isEmpty()) {
				return false;
			}
			return this.winner.get().getKey().equals(Thread.currentThread());
			
		} finally {
			rLock.unlock();
		}
		
		
		
		
	}
	
	
	
	
}
