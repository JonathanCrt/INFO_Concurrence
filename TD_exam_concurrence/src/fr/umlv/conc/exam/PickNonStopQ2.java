package fr.umlv.conc.exam;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.IntStream;
import java.util.Optional;
import java.util.Random;

public class PickNonStopQ2 {
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
		var game = new PickNonStopQ2();
		var nbThreads = 5;
		var threads = new Thread[nbThreads];
		game.startNewGame(100);
		
		
		
		for(int i = 0; i < nbThreads; i++) {
			Thread t = new Thread(() -> {
				game.pick();
				game.pick(); // on piocche deux cartes
			});
			threads[i] = t;
			t.setName("player" + i);
			t.start();
			
		}
		
		/*
		IntStream.range(0, nbThreads).forEach(i -> {
			new Thread(() -> {
				game.pick();
				game.pick(); // on piocche deux cartes
			});
		});
		*/
		
		
		for(Thread thread: threads) {
			thread.join();
		}
		
		System.out.println(game.winner().get());
		
	}
	
	
	
	
}