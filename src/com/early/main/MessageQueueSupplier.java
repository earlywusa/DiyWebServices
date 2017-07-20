package com.early.main;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MessageQueueSupplier implements Runnable {

	private List<Integer> messageQueue = null;
	private boolean running = true;
	public MessageQueueSupplier(List<Integer> messageQueue) {
		this.messageQueue = messageQueue;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int count = 0;
		while(running && ! Thread.currentThread().isInterrupted()) {
			try {
				synchronized(messageQueue) {
					System.out.println("add message: " + count);
					messageQueue.add(count);
					messageQueue.notifyAll();
				}
				count ++;
				TimeUnit.SECONDS.sleep(3);
			}catch(InterruptedException e) {
				e.printStackTrace();
				running = false;
				Thread.currentThread().interrupt();
			}
		}
	}
	
	public void stop() {
		running = false;
	}

}
