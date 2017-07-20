package com.early.websocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WsClient {

	private final Log log = LogFactory.getLog(WsClient.class);
	private int port = 8080;
	private BlockingQueue<String> blockingQueue = null;
	private Socket connection = null;
	private BufferedWriter bw = null;
	public WsClient(int port) {
		this.port = port;
		blockingQueue = new LinkedBlockingQueue<>();
	}
	
	public void start() {
//		Thread t = new Thread(()-> {
			try {
				log.debug("start client...");
				connection = new Socket("localhost", port);
				bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
				Thread receiver = startReceiver(connection);
				Thread responder = startResponder();
				TimeUnit.SECONDS.sleep(5);
				log.debug("send handshake..");
				sendMessage(genHandShakeMsg());
				receiver.join();
				responder.join();
			}catch(Exception e) {
				e.printStackTrace();
			}
//		});
//		t.start();
//		try {
//			t.join();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	private String genHandShakeMsg() {
		StringBuilder sb = new StringBuilder();
		sb.append("GET / HTTP/1.1").append(System.lineSeparator());
		sb.append("Host: localhost:" + 8088).append(System.lineSeparator());
		sb.append("Upgrade: websocket").append(System.lineSeparator());
		sb.append("Connection: Upgrade").append(System.lineSeparator());
		sb.append("Sec-WebSocket-Key: E4i4gDQc1XTIQcQxvf+ODA==").append(System.lineSeparator());
		sb.append("Sec-WebSocket-Version: 13").append(System.lineSeparator());
		return sb.toString();
	}
	private void sendMessage(String msg) {
		try {
			bw.write(msg);
			bw.newLine();
			log.debug("about to flush new msg: " + msg);
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Thread startReceiver(Socket connection) {
		Thread t = new Thread(()->{
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line = null;
				StringBuilder sb = new StringBuilder();
				do {
					line = br.readLine();
					log.debug("receive message: " + line);
					if(line.equals("")) {
						blockingQueue.offer(sb.toString());
					}else {
						sb.append(line);
					}
					
				}while(line != null && ! line.equals("\r\n"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		t.start();
		return t;
	}
	
	private Thread startResponder() {
		Thread t = new Thread(()->{
			boolean keepReceiving = true;
			while(keepReceiving) {
				try {
					String msg = blockingQueue.poll(Long.MAX_VALUE, TimeUnit.SECONDS);
					sendMessage(responseGenerator(msg));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					keepReceiving = false;
				}
			}
		});
		t.start();
		return t;
	}
	
	private String responseGenerator(String msg) {
		if(msg.contains("Sec-WebSocket-Accept: d9WHst60HtB4IvjOVevrexl0oLA=")) {
			return "Hello, friend.";
		}
		else {
			return "Hello, Stranger";
		}
	}
}
