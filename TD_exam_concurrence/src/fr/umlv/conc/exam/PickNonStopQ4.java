package fr.umlv.conc.exam;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.Map.Entry;

public class PickNonStopQ4 {
	private final ArrayDeque<Integer> cards = new ArrayDeque<>(10_000);
	private final HashMap<Thread, Integer> players = new HashMap<Thread, Integer>();
	private Thread lastZero;
	
	
	
	
	// data -race ? cards et players

	public PickNonStopQ4() {
		synchronized (players) {
			this.lastZero = null;
		}
		
	}

	private final static int MAX_CARD = 9;

	public void startNewGame(int nbCards) {
		synchronized (players) {
			Random random = new Random();
			random.ints(nbCards, -MAX_CARD, MAX_CARD + 1).forEach(cards::offer);
		}
		
	}

	public Integer pick() throws InterruptedException {
		synchronized (players) {
			var player = Thread.currentThread();
			var card = cards.poll();
			
			if (card == null){ // there is no card left
				lastZero = null;
				players.notify(); 
				return null;
			}
			
			if(card == 0) {
				System.out.println(player.getName() + " can't play");
				lastZero= player;
				players.notify();
				while(lastZero == player) {
					players.wait();
				}
			}
			
			players.merge(player, card, Integer::sum);
			return card;
		}
		
	}

	public Optional<String> winner() {
		synchronized (players) {
			return players.entrySet().stream().max(Entry.comparingByValue())
					.map(e -> e.getKey().getName() + " wins with " + e.getValue());
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		var game = new PickNonStopQ4();
		var nbThreads = 5;
		game.startNewGame(100);
		
		ExecutorService ex = Executors.newCachedThreadPool();
		
		try {
			var tasks =  new ArrayList<Callable<Object>>();
			
			IntStream.range(0, nbThreads).forEach( i -> {
				tasks.add( () -> {
					while(game.cards.size() > 0) {
						game.pick();
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
