package fr.umlv.conc.exam;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.IntStream;

public class Codex {
	
	public static void main(String[] args) {
		 var codedMsgs = new ArrayBlockingQueue<String>(10);
		 var decodedMsgs = new ArrayBlockingQueue<String>(10);
		 
		 var nbThreadsRetrievers = 3;
		 var nbThreadsDecoders = 2;
		 var nbThreadArchiver = 1;
		 
		 for(int i = 0; i < nbThreadsRetrievers; i++) {
			 new Thread ( () -> {
				try {
					while(!Thread.interrupted()){
						codedMsgs.put(CodeAPI.receive());
					}
				} catch (InterruptedException e) {
					throw new AssertionError();
				} 
			 });
		 }
		 
		 /*
		 IntStream.range(0, nbThreadsRetrievers).forEach(i -> {
			 new Thread(() -> {
				 try {
					while(!Thread.interrupted()) {
						codedMsgs.put(CodeAPI.receive());
					}
				} catch (InterruptedException e) {
					throw new AssertionError();
				}
			 });
		 });
		 */
		 
		 
		 for(int i = 0; i < nbThreadsDecoders; i++) {
			 new Thread ( () -> {
					try {
						while(!Thread.interrupted()){
							decodedMsgs.put(CodeAPI.decode(codedMsgs.take()));
						}
					} catch (InterruptedException e) {
						throw new AssertionError(); //ignoring message 
					} 
				 });
		 }
		 
		 for(int i = 0; i < nbThreadArchiver; i++) {
			 new Thread (() ->   {
				 try {
					while(!Thread.interrupted()) {
						CodeAPI.archive(decodedMsgs.take());
					}
				} catch (InterruptedException e) {
					throw new AssertionError(); // ignoring message
				}
			 });
		 }
		 
		 
	}
	
	
	
	
}
