package fr.upem.concurrence.td03;
import java.util.Objects;

public class RendezVous<V> {
  private V value;
  private final Object lock = new Object();
  
  
  public void set(V value) {
    Objects.requireNonNull(value);
    synchronized (lock) {
    	this.value = value;
	}
  }
  
  public V get() throws InterruptedException {
    while(value == null) {
    	synchronized (lock) {
    		//Thread.sleep(1); 
		}
    }
    return value;
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
