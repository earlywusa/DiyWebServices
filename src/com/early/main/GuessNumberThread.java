package com.early.main;

import java.util.concurrent.TimeUnit;

public class GuessNumberThread extends Thread{
	   private int number;
	   private Thread parentThread;
	   public GuessNumberThread(int number, Thread calling) {
	      this.number = number;
	      this.parentThread = calling;
	   }
	   
	   public void run() {
	      int counter = 0;
	      int guess = 0;
	      do {
	         guess = (int) (Math.random() * 100 + 1);
	         System.out.println(this.getName() + " guesses " + guess + " calling thread state: " + parentThread.getState().toString());
	         counter++;
	         try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      } while(guess != number);
	      System.out.println("** Correct!" + this.getName() + "in" + counter + "guesses.**");
	   }
}
