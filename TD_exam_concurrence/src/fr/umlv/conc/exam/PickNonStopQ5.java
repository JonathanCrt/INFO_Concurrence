package fr.umlv.conc.exam;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class PickNonStopQ5 {
	private final ArrayDeque<Integer> cards = new ArrayDeque<>(10_000);
	private final HashMap<Thread, Integer> players = new HashMap<Thread, Integer>();
	private final ReentrantLock rLock = new ReentrantLock();
	private final Condition emptyDeck = rLock.newCondition();
	
	private Thread lastZero;

	// data -race ? cards et players

	public PickNonStopQ5() {
		rLock.lock();
		try {
			this.lastZero = null;
		} finally {
			rLock.unlock();
		}
		
	}

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
				while(lastZero.equals(player)) {
					this.emptyDeck.await();
				}
			}
			
			players.merge(player, card, Integer::sum);
			return card;
		} finally {
			rLock.unlock();
		}
		
		
	}

	public Optional<String> winner() {
		rLock.lock();
		try {
			return players.entrySet().stream().max(Entry.comparingByValue())
					.map(e -> e.getKey().getName() + " wins with " + e.getValue());
		} finally {
			rLock.unlock();
		}
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		var game = new PickNonStopQ5();
		var nbThreads = 5;
		game.startNewGame(100);
		
		ExecutorService ex = Executors.newCachedThreadPool();
		
		try {
			var tasks =  new ArrayList<Callable<Thread>>();
			
			IntStream.range(0, nbThreads).forEach( i -> {
				tasks.add( () -> {
					while(game.cards.size() > 0) {
						game.pick();
					}
					return null;
				});
			});
			ex.invokeAll(tasks);
			ex.shutdown();
			System.out.println(game.winner().get());
			
		} finally {
			ex.shutdownNow();
		}
		
		
		
	}
}
