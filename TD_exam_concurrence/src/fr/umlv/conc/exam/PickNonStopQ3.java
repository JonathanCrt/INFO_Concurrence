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

public class PickNonStopQ3 {
	private final ArrayDeque<Integer> cards = new ArrayDeque<>();
	private final HashMap<Thread, Integer> players = new HashMap<Thread, Integer>();
	
	// data -race ? cards et players

	private final static int MAX_CARD = 9;

	public void startNewGame(int nbCards) {
		synchronized (players) {
			Random random = new Random();
			random.ints(nbCards, -MAX_CARD, MAX_CARD + 1).forEach(cards::offer);
		}
		
	}

	public Integer pick() {
		synchronized (players) {
			var player = Thread.currentThread();
			var card = cards.poll();
			//System.out.println("player " + player.getName() + " picks " + card);
			if (card == null){ // there is no card left
				return null;
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
		var game = new PickNonStopQ3();
		var nbThreads = 5;
		game.startNewGame(100);
		
		ExecutorService ex = Executors.newCachedThreadPool(); // initialisation de L'ExecutorsService
		
		try { // try/finally
			var tasks =  new ArrayList<Callable<Thread>>(); // intialisation des t�ches.
			
			IntStream.range(0, nbThreads).forEach( i -> {
				tasks.add( () -> {
					game.pick();
					game.pick();
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
