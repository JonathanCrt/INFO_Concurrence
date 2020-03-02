package fr.umlv.conc.exam;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PickNonStopQ7 {
	private final ArrayDeque<Integer> cards = new ArrayDeque<>(10_000);
	private final HashMap<Thread, Integer> players = new HashMap<Thread, Integer>();
	private final HashSet<Thread> stoppedPlayers = new HashSet<Thread>();
	private final ReentrantLock rLock = new ReentrantLock();
	private final Condition emptyDeck = rLock.newCondition();
	private final Condition lastZeroChanged  = rLock.newCondition();
	private final Condition gameFinished = rLock.newCondition();
	private boolean finished;
	
	private Thread lastZero;

	// data -race ? cards et players

	

	private final static int MAX_CARD = 9;

	public void startNewGame(int nbCards) {
		
		rLock.unlock();
		try {
			Random random = new Random();
			random.ints(nbCards, -MAX_CARD, MAX_CARD + 1).forEach(cards::offer);
		} finally {
			rLock.unlock();
		}
		
	}

	public Integer pick() throws InterruptedException {
		
		rLock.lock();
		try {
			var player = Thread.currentThread();
			var card = cards.poll();
			
			if (card == null){ // there is no card left
				lastZero = null;
				this.emptyDeck.signal();
				return null;
			}
			
			if(card == 0) {
				System.out.println(player.getName() + " can't play");
				lastZero= player;
				this.emptyDeck.signal();
				while(lastZero == player) {
					this.emptyDeck.await();
				}
			}
			
			players.merge(player, card, Integer::sum);
			return card;
		} finally {
			rLock.unlock();
		}
		
		
	}

	public Optional<String> winner() throws InterruptedException {
		rLock.lock();
		try {
			
			while(cards.size() > 0 && players.size() != stoppedPlayers.size()) {
				emptyDeck.await();
			}
			finished = true;
			this.gameFinished.signalAll();
			return players.entrySet().stream().max(Entry.comparingByValue())
					.map(e -> e.getKey().getName() + " wins with " + e.getValue());
		
		} finally {
			rLock.unlock();
		}
	}
	
	
	public void stopAndWait() throws InterruptedException{
		rLock.lock();
		try {
			var player = Thread.currentThread();
			System.out.println(player.getName() + " quits");
			if(!players.containsKey(player)) { // Si la HashMap ne contient pas le joueur
				throw new IllegalStateException(); // Le thread a déja joué
			}
			this.stoppedPlayers.add(player);
			var remainingPlayers = players.size() - stoppedPlayers.size();
			if(remainingPlayers == 1) { // Si un jeu Thread est entrain de jouer on applique pas la méthode de la carte 0
				
				this.lastZeroChanged.signal();
				this.lastZero = null;
			}
			
			if(remainingPlayers == 0) {
				emptyDeck.signal();
			}
			while(!finished) {
				gameFinished.await();
			}
			
			
		} finally {
			rLock.unlock();
		}
	}
	
	
	
	
}
