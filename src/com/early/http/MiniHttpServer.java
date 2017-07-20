package com.early.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.early.socket.DiyServerSocket;

public class MiniHttpServer implements DiyServerSocket {

	private ServerSocket serverSocket = null;
	private int port = 8080;
	private boolean running = true;
	private ExecutorService executor = null;;
	
	public MiniHttpServer(int port) throws IOException {
		this.port = port;
		executor = Executors.newCachedThreadPool();
	}
	
	@Override
	public void run() {
		try {
			startServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	@Override
	public void startServer() throws IOException {
		while(running) {
			try {
				Socket connection = serverSocket.accept();
				HttpConnectionHandler handler = new HttpConnectionHandler.DefaultHttpConnectionHandler(connection);
				executor.submit(handler);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}

	@Override
	public void stopServer() throws IOException {
		if(serverSocket != null && ! serverSocket.isClosed()) {
			running = false;
			serverSocket.close();
			serverSocket = null;
		}
		executor.shutdown();
		try {
			executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executor.shutdownNow(); //may have no effect...interruptedexception is not generated 
		//in connection handler
	}

	@Override
	public void setupServer() throws UnknownHostException, IOException {
		serverSocket = new ServerSocket(port);
	}

}
