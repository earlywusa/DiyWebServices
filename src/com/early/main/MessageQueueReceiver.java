package com.early.main;

import java.util.List;
import java.util.concurrent.Callable;

public class MessageQueueReceiver implements Callable<Integer>{
	
	List<Integer> messageQueue = null;
	boolean running = true;
	public MessageQueueReceiver(List<Integer> messageQueue) {
		this.messageQueue = messageQueue;
	}

	@Override
	public Integer call()  {
		Integer item = null;
		while( running) {
			try {
				synchronized(messageQueue) {
					if(messageQueue.isEmpty()) {
						System.out.println("List is empty...");
						System.out.println("waiting...");
						messageQueue.wait();
					}
					
					item = messageQueue.remove(0);
					System.out.println("take item..." + item);

				}
			}catch(InterruptedException e) {
				e.printStackTrace();
				running = false;
			}
		}
		
		
		
		
		
		return item;
	}
}
