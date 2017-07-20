package com.early.main;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.early.server.socketserver.ServerManager;
import com.early.server.socketserver.SocketServerFactory;
import com.early.websocket.WsClient;

public class Driver {
	private static final Log log = LogFactory.getLog(Driver.class);
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Driver driver = new Driver();
		driver.testWsServer();
		log.debug("Server Started...");
//		try {
//			TimeUnit.SECONDS.sleep(5);
//			log.debug("start client...");
//			WsClient wsc = new WsClient(8080);
//			wsc.start();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		

		
	}
	
	public void testWsServer() {
		try {
			ServerManager.getInstance().startService(8080, SocketServerFactory.ServerType.WEBSOCKET);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testHttpServer() {
		try {
			ServerManager.getInstance().startService(8080, SocketServerFactory.ServerType.HTTP);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void testAsynchGuessNumberThread() {
		Thread asynchThread = new AsynchronizedGuessNumber();
		asynchThread.start();
		try {
			asynchThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("get here");
	}
	
	public void testGuessNumberThread() {
		
		
		Thread testThread = new GuessNumberThread(99, Thread.currentThread());
		testThread.start();
//		try {
//			testThread.join();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Thread testThread1 = new GuessNumberThread(88, Thread.currentThread());
		testThread1.start();
//		try {
//			testThread1.join();
//		}catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		int count = 0;
		while(count < 10) {
			count ++;
			log.debug(Thread.currentThread().getName() + " waiting..");
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("main thread exiting..." + Thread.currentThread().getState().toString());
		
	}
	
	
	public void testExecutorService() {
		ExecutorService executor = Executors.newCachedThreadPool();
		
		List<Runnable> allTasks = new ArrayList<>();
		allTasks.add(new GuessNumberRunnable(88, "Target-88", Thread.currentThread()));
		allTasks.add(new GuessNumberRunnable(99, "Target-99", Thread.currentThread()));
		
		allTasks.forEach(executor::submit);
		
//		try {
//			TimeUnit.SECONDS.sleep(5);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		System.out.println("call to shutdown!....");
		executor.shutdown();
//		
		try {
			System.out.println("call to await.....");
			executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("call to shutdown now....");
		//List<Runnable> liveTasks = executor.shutdownNow();
//		System.out.println("number of live tasks: " + liveTasks.size());
//		liveTasks.forEach(x->{
//			System.out.println("try to stop live tasks...");
//			GuessNumberRunnable guessTask = (GuessNumberRunnable)x;
//			guessTask.stopGuess();
//		});
		
//		while(true) {
//			try {
//				TimeUnit.SECONDS.sleep(1);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}
	
	public void testMessageQueue() {
		List<Integer> safeQueue = new ArrayList<>();
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.submit(new MessageQueueReceiver(safeQueue));
		executor.submit(new MessageQueueSupplier(safeQueue));
		
		executor.shutdown();
		try {
			executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executor.shutdownNow();
	}
}
