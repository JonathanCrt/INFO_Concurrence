package fr.upem.concurrence.td05;

import java.util.concurrent.Exchanger;

public class ExchangerExample {
  public static void main(String[] args) throws InterruptedException {
    var exchanger = new Exchanger<String>();
    new Thread(() -> {
      try {
        System.out.println("thread 1 " + exchanger.exchange("foo1"));
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    }).start();
    System.out.println("main " + exchanger.exchange(null));
  }
  /**
   *  Si la valeur stockée est nulle il s agit du premier appel, sinon il s agit du second
   * 
   * deux booleans  qui symbolisent l'etat de la fonction
   * Créer un éum pour préciser l'état du programme ! (mieux)
   * 
   * 3 elements
   * 
   */
}
