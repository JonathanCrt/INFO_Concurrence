package fr.upem.concurrence.td02;

public class StopThreadBug implements Runnable {
    private boolean stop = false;

    public void stop() {
        stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            //System.out.println("Up");
        }
        System.out.print("Done");
    }

    public static void main(String[] args) throws InterruptedException {
        StopThreadBug st = new StopThreadBug();
        new Thread(st::run).start();
        Thread.sleep(5_000);
        System.out.println("Trying to tell the thread to stop");
        st.stop();
    }
}