package com.early.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
//not used
public class WsConnectionHandler implements Runnable {

	
	protected Socket connection = null;
	public WsConnectionHandler(Socket connection) {
		this.connection = connection;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		InputStreamReader isr;
		try {
			isr = new InputStreamReader(connection.getInputStream());
			BufferedReader reader = new BufferedReader(isr);
			String line = null;
			do {
				//System.out.println("waiting for message...");
				line = reader.readLine();
				if(line != null) {
					System.out.println(line);
				}
			}while(line != null && ! line.equals("\r\n"));
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
