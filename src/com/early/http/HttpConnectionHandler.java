package com.early.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Date;

public interface HttpConnectionHandler extends Runnable{
	public static String HTTP_VERSION_HEADER = "HTTP/1.1";
	public void receiveRequest();
	public void sendResponse();
	public void closeConnection();
	
	public class DefaultHttpConnectionHandler implements HttpConnectionHandler{
		private Socket connection = null;
		private BufferedReader reader = null;
		private BufferedWriter writer = null;
//		private OutputStreamWriter streamWriter = null;
		private boolean running = true;
		
		public DefaultHttpConnectionHandler(Socket connection) {
			this.connection = connection;
			try {
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
				//streamWriter = new OutputStreamWriter(connection.getOutputStream());
			} catch (IOException e) {
				closeConnection();
			}
		}
		
		@Override
		public void receiveRequest() {
			while(running) {
				try {
					//System.out.println("waiting for message...");
					String message = reader.readLine();
					System.out.println(message);
					if(message == null || message.equals("")) {
						sendResponse();
					}
				} catch (Exception e) {
					e.printStackTrace();
					running = false;
					closeConnection();
				}
			}
		}
		
		public void sendTestResponse() {
				Date today = new Date(); 
				String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + today; 
				try {
					connection.getOutputStream().write(httpResponse.getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
					running=false;
				}

		}

		@Override
		public void sendResponse() {
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					int count = 0;
					String response = HTTP_VERSION_HEADER + " 200 OK \r\n\r\nHELLO!";
					try {
						while(count ++ <1) {
							//TimeUnit.MICROSECONDS.sleep(1);
							if(! connection.isClosed()) {
							System.out.println("sending response...");
							writer.write(response);
							writer.newLine();
							writer.flush();
							//connection.getOutputStream().write(response.getBytes("UTF-8"));
							
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						running = false;
						closeConnection();
					}
				}
				
			});
			t.start();
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}

		@Override
		public void closeConnection() {
			try {
			running = false;
			reader.close();
			reader= null;
			writer.close();
			writer=null;
			connection.close();
			connection = null;
			}catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			receiveRequest();			
		}
		
	}
}
