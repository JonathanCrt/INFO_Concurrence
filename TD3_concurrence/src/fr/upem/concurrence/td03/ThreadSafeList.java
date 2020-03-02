package fr.upem.concurrence.td03;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class ThreadSafeList {
	
	private final ArrayList<Integer> list = new ArrayList<Integer>();
	private  final Object lock = new Object();
	
	/**
	 * @param list
	 * Si champ pas final --> synchronized
	 * Constructeur par défaut
	 * Pas d'ArrayList en paramètre
	 */
	
	/**
	 * add one elt 
	 * @param it
	 */
	public void add(Integer it){
		Objects.requireNonNull(it);
		synchronized (lock) {
			list.add(it);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public int size() {
		synchronized (lock) {
			return list.size();
		}
	}

	@Override
	public String toString() {
		synchronized (lock) {
			return list.stream()
					.map((elt) -> elt.toString())
					.collect(Collectors.joining("-", "(", ")"))
					.toString();
		}
		
	}
	
	
	
	
	
	
	
}
