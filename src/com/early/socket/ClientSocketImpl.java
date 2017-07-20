package com.early.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ClientSocketImpl implements DiyClientSocket {
	Socket clientSocket = null;
	private String targetIp = null;
	private int port = 0;
	
	public ClientSocketImpl(String targetIp, int port) {
		this.targetIp = targetIp;
		this.port = port;
	}
	
	public boolean connect() throws UnknownHostException, IOException {
		boolean connected = false;
		clientSocket = new Socket(targetIp, port);
		connected = true;
		return connected;
	}
	
	public void close() {
		if(clientSocket != null) {
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				clientSocket=null;
			}
		}
	}

	@Override
	public void getState() {
		if(clientSocket == null) {
			System.err.println("NOT Valid client.");
			return;
		}
		System.out.println("Bound? " + clientSocket.isBound());
		System.out.println("Connected? " + clientSocket.isConnected());
		
	}

	@Override
	public void run() {
		try {
			connect();
			Random r = new Random();
			System.out.println("connection made, about to send message.." );
			TimeUnit.SECONDS.sleep(r.nextInt(10));
			String message = "Hello from: " + clientSocket.getLocalPort() + "\n";
			System.out.println("send message: ****" + message + "****");
			
			clientSocket.getOutputStream().write(message.getBytes());
			clientSocket.getOutputStream().flush();
			
			TimeUnit.SECONDS.sleep(r.nextInt(10));
			
			message = "second message: " + clientSocket.getLocalPort() + "\n";
			System.out.println("send message: ****" + message + "****");
			clientSocket.getOutputStream().write(message.getBytes());
			clientSocket.getOutputStream().flush();
			
			TimeUnit.SECONDS.sleep(r.nextInt(10));
			clientSocket.getOutputStream().write("END\n".getBytes());
			clientSocket.getOutputStream().flush();
			
			clientSocket.getOutputStream().close();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			close();
		}
	}

}
