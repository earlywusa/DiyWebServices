package com.early.main;

public class AsynchronizedGuessNumber extends Thread{

	private Thread guessNumber = null;
	
	public AsynchronizedGuessNumber() {
		guessNumber = new GuessNumberThread(99, Thread.currentThread());
		
	}
	
	@Override
	public void run() {
		guessNumber.start();
		try {
			guessNumber.join(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
