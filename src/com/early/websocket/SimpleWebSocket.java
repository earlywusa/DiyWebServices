package com.early.websocket;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.early.socket.ServerSocketImpl;

public class SimpleWebSocket extends ServerSocketImpl{

	public SimpleWebSocket(int port) {
		super(port);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void startServer() {
		ExecutorService executor = Executors.newCachedThreadPool();
		System.out.println("Starting new socket server thread..." + Thread.currentThread().getName());
		while(running) {
			try {
				System.out.println("waiting for connection...");
				Socket connection = serverSocket.accept();
				executor.submit(new WsMessageHandler(connection));
				
				
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
}
