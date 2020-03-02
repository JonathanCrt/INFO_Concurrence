package fr.umlv.td07;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;


/**
 * TD7 concurrence
 * @author jcrete
 */
public class InputOutput {
	
	
	public static void main(String[] args) throws InterruptedException {
	
		var nbThreads = 4;
        Thread tabOfThreads[] = new Thread[nbThreads];
        
        int cpt[] = new int[nbThreads];
        int i = 0;
        while(i < nbThreads) {
        	var j = i; // for lambda
            cpt[j]++;
            
            var threadRunning = new Thread (() -> {
                while(!Thread.interrupted()){
                    try {
                        Thread.sleep(1000);
                        tabOfThreads[j] = Thread.currentThread();
                        
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        continue;
                    }
                }
            });
            threadRunning.start();
            Thread.sleep(1000);
            threadRunning.interrupt();
            i++;
        }
       

        System.out.println("enter a thread id: ");
        try (InputStreamReader input = new InputStreamReader(System.in); 
        		var reader = new Scanner(System.in)) {
            int nT = reader.nextInt();
            System.out.println(tabOfThreads[nT].getName());
            tabOfThreads[nT].interrupt();
        } catch (IOException io) {
            return;
        }

		/**
		 * "daemon threads are low-priority threads whose only role is to provide services to user threads."
		 */
		
	}
}
