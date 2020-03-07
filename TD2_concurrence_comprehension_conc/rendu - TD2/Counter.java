package fr.upem.concurrence.td02;

public class Counter {

        private int value;

        public void add10000() {
            for (int i = 0; i < 10_000; i++) {
                value++;
            }
        }

        public static void main(String[] args) throws InterruptedException {
            Counter counter = new Counter();
            Runnable runnable = () -> {counter.add10000();};
            Thread t1 = new Thread(runnable);
            Thread t2 = new Thread(runnable);
            t1.start();
            t2.start();
            t1.join();
            t2.join();
            System.out.println(counter.value);
        }
    }
