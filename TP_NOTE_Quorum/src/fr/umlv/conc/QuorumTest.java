package fr.umlv.conc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class QuorumTest {
  @Test(timeout = 5000)
  public void simple() throws InterruptedException {
    Quorum quorum = new Quorum();
    Thread thread = new Thread(() -> assertEquals(1, quorum.vote(1)));
    thread.start();
    assertEquals(1, quorum.vote(1));
    thread.join();
  }

  @Test(timeout = 5000)
  public void quorum3Vote1() throws InterruptedException {
    Quorum quorum = new Quorum();
    Thread thread1 = new Thread(() -> assertEquals(1, quorum.vote(1)));
    thread1.start();
    Thread.sleep(1000);
    Thread thread2 = new Thread(() -> assertEquals(1, quorum.vote(2)));
    thread2.start();
    Thread.sleep(1000);
    assertEquals(1, quorum.vote(1));
    thread1.join();
    thread2.join();
  }

  @Test(timeout = 5000)
  public void quorum3Vote2() throws InterruptedException {
    Quorum quorum = new Quorum();
    Thread thread1 = new Thread(() -> assertEquals(2, quorum.vote(1)));
    thread1.start();
    Thread.sleep(1000);
    Thread thread2 = new Thread(() -> assertEquals(2, quorum.vote(2)));
    thread2.start();
    Thread.sleep(1000);
    assertEquals(2, quorum.vote(2));
    thread1.join();
    thread2.join();
  }

  @Test(timeout = 5000)
  public void noQuorum() throws InterruptedException {
    Quorum quorum = new Quorum();
    Thread thread1 = new Thread(() -> {
      try {
        quorum.vote(55);
        fail();
      } catch(IllegalStateException e) {
        // do nothing
      }
    });
    Thread thread2 = new Thread(() -> {
      try {
        quorum.vote(66);
        fail();
      } catch(IllegalStateException e) {
        // do nothing
      }
    });
    Thread thread3 = new Thread(() -> {
      try {
        quorum.vote(44);
        fail();
      } catch(IllegalStateException e) {
        // do nothing
      }
    });
    thread1.start();
    thread2.start();
    thread3.start();
    thread1.join();
    thread2.join();
    thread3.join();
  }
  
  @Test(timeout = 5000)
  public void interrupted() throws InterruptedException {
    Quorum quorum = new Quorum();
    Thread thread = new Thread(() -> {
      try {
        quorum.vote(42);
        fail();
      } catch(IllegalStateException e) {
        // do nothing
      }
    });
    thread.start();
    Thread.sleep(1000);
    thread.interrupt();
    thread.join();
  }
  
  @Test(timeout = 5000)
  public void interrupted2() throws InterruptedException {
    Quorum quorum = new Quorum();
    Thread thread1 = new Thread(() -> {
      try {
        quorum.vote(77);
        fail();
      } catch(IllegalStateException e) {
        // do nothing
      }
    });
    Thread thread2 = new Thread(() -> {
      try {
        quorum.vote(33);
        fail();
      } catch(IllegalStateException e) {
        // do nothing
      }
    });
    thread1.start();
    thread2.start();
    Thread.sleep(1000);
    thread1.interrupt();
    thread1.join();
    thread2.join();
  }

  @Test(timeout = 5000)
  public void tooManyThreads() throws InterruptedException {
    Quorum quorum = new Quorum();
    Runnable runnable = () -> assertEquals(10, quorum.vote(10));
    Thread thread1 = new Thread(runnable);
    Thread thread2 = new Thread(runnable);
    thread1.start();
    thread2.start();
    Thread.sleep(1000);
    assertEquals(10, quorum.vote(45));
    assertEquals(10, quorum.vote(45));
    assertEquals(10, quorum.vote(45));
    thread1.join();
    thread2.join();
  }

  @Test(timeout = 5000)
  public void tooManyThreads2() throws InterruptedException {
    Quorum quorum = new Quorum();
    Runnable runnable = () -> assertEquals(20, quorum.vote(20));
    Thread thread1 = new Thread(runnable);
    Thread thread2 = new Thread(runnable);
    thread1.start();
    thread2.start();
    Thread.sleep(1000);
    assertEquals(20, quorum.vote(45));
    assertEquals(20, quorum.vote(45));
    assertEquals(20, quorum.vote(45));
    thread1.join();
    thread2.join();
  }

  @Test(timeout = 5000, expected=IllegalStateException.class)
  public void noQuorumTooManyThreads() throws InterruptedException {
    Quorum quorum = new Quorum();
    Thread thread1 = new Thread(() -> {
      try {
        quorum.vote(55);
        fail();
      } catch(IllegalStateException e) {
        // do nothing
      }
    });
    Thread thread2 = new Thread(() -> {
      try {
        quorum.vote(66);
        fail();
      } catch(IllegalStateException e) {
        // do nothing
      }
    });
    Thread thread3 = new Thread(() -> {
      try {
        quorum.vote(44);
        fail();
      } catch(IllegalStateException e) {
        // do nothing
      }
    });
    thread1.start();
    thread2.start();
    thread3.start();
    thread1.join();
    thread2.join();
    thread3.join();

    quorum.vote(99);
  }
}