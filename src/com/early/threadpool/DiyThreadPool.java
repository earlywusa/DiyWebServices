package com.early.threadpool;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DiyThreadPool extends Thread{

	private List<Thread> threads = new CopyOnWriteArrayList<>();
	private int maxThreads = 2;
	
	public DiyThreadPool(int maxThread) {
		this.maxThreads = maxThread;
	}
	
	public void addTask(Thread newTask) {
		
	}
	
}
