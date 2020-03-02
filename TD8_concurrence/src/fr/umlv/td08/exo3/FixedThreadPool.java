package fr.umlv.td08.exo3;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import fr.umlv.td08.Request;
import fr.umlv.td08.exo3.Task;

public class FixedThreadPool {

    final private int poolsize;
    final private LinkedBlockingQueue<Task> tasksQueue;
    final private Thread[] threads;

    public FixedThreadPool(int poolsize) {
        this.poolsize = poolsize;
        this.tasksQueue = new LinkedBlockingQueue<Task>(Request.ALL_SITES.size());
        this.threads = new Thread[poolsize];
    }

    public void start(){
    	var i = 0;
        while(i < this.poolsize) {
        	 threads[i] = new Thread(() ->{
               try {
                 var oneTask = tasksQueue.take(); // take -> remove(...)
                 oneTask.run();
               } catch (InterruptedException e) {
                 return;
               }
             });
             threads[i].start();
             i++;
        }
    }

    public void submit(Task r) throws InterruptedException{
        tasksQueue.put(r);
    }

    public void stop(){
    	for (var thread : threads) {
    	    thread.interrupt();
    	}
    }

    


}