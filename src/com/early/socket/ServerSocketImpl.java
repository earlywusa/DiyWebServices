package com.early.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerSocketImpl implements DiyServerSocket  {

	protected int port;
	protected ServerSocket serverSocket = null;
	protected boolean running = true;
	
	public ServerSocketImpl(int port) {
		this.port = port;
	}
	
	@Override
	public void setupServer() throws UnknownHostException, IOException {
		serverSocket = new ServerSocket(port);
	}

	@Override
	public void startServer(){
		ExecutorService executor = Executors.newCachedThreadPool();
		System.out.println("Starting new socket server thread..." + Thread.currentThread().getName());
		while(running) {
			try {
				System.out.println("waiting for connection...");
				Socket connection = serverSocket.accept();
				executor.submit(new MessageProcessor(connection));
				
				
			} catch (IOException e) {
				e.printStackTrace();
				running = false;
			}
		}
		executor.shutdown();
		try {
			executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void stopServer() throws IOException {
		if(serverSocket != null && ! serverSocket.isClosed()) {
			serverSocket.close();
		}
		serverSocket = null;
		
	}

	@Override 
	public void run() {
		this.startServer();
	}
	
	public class MessageProcessor implements Runnable{
		Socket connection = null;
		public MessageProcessor(Socket newConnection) {
			this.connection = newConnection;
		}
		
		@Override
		public void run() {
			InputStreamReader isr;
			try {
				isr = new InputStreamReader(connection.getInputStream());
				BufferedReader reader = new BufferedReader(isr);
				String line = null;
				do {
					System.out.println("waiting for message...");
					line = reader.readLine();
					if(line != null) {
						System.out.println("message: " + line);
					}
				}while(line != null && ! line.equals("END"));
				System.out.println("about to close connection...");
				connection.getInputStream().close();
				isr.close();
				reader.close();
				connection.close();
				connection = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	

}
