package com.early.main;

import java.util.concurrent.TimeUnit;

public class GuessNumberRunnable implements Runnable{

	private int number = 0;
	private String name = "";
	private Thread parentThread = null;
	private boolean running = true;
	public GuessNumberRunnable(int number, String name, Thread parentThread) {
		this.number = number;
		this.name = name;
		this.parentThread = parentThread;
	}
	
	@Override
	public void run() {
		
		int counter = 0;
	      int guess = 0;
	      try {
	      do {
		         guess = (int) (Math.random() * 100 + 1);
		         System.out.println(this.getName() + " guesses " + guess + " calling thread state: " + parentThread.getState().toString());
		         counter++;
		         TimeUnit.SECONDS.sleep(2);
				
		      } while(guess != number && ! checkForInterruption());
	      	System.out.println("** Correct!" + this.getName() + "in" + counter + "guesses.**");
	      } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//System.out.println("Time is up. exit..");
				//return;
				Thread.currentThread().interrupt();
			}
	     

	}
	
	private boolean checkForInterruption() {
		if(Thread.currentThread().isInterrupted()) {
			System.out.println("Interrupted !");
			return true;
		}
		else {
			System.out.println("not Interrupted !");
			return false;
		}
	}
	
	private String getName() {
		return name;
	}
	
	public void stopGuess() {
		System.out.println("Stop guessing...");
		running = false;
	}
	

}
