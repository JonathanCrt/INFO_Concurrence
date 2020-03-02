package fr.upem.concurrence.td04;
import java.util.Objects;

public class RendezVous<V> {
  private V value;
  private final Object lock = new Object();
  
  public void set(V value) {
    Objects.requireNonNull(value);
    synchronized (lock) {
    	this.value = value;
    	lock.notify();
	}
  }
  
  public V get() throws InterruptedException {
	synchronized (lock) {
		// on peut mettre le synchronized autour car on la méthode wait()
		while(value == null) {
			 lock.wait();
		     //Thread.sleep(1); 
		}
		return value; // Attention : dans le bloc synchronized ! 
	}
    
  }
  
  public static void main(String[] args) throws InterruptedException {
    RendezVous<String> rendezVous = new RendezVous<>();
    new Thread(() -> {
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
      rendezVous.set("hello");
    }).start();
    
    System.out.println(rendezVous.get());
  }
}
