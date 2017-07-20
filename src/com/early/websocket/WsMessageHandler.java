package com.early.websocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.early.socket.message.HttpMessage;
import com.early.socket.message.Message;
import com.early.socket.message.MessageHandler;

public class WsMessageHandler implements MessageHandler {
	private Log log = LogFactory.getLog(WsMessageHandler.class);
	protected Socket connection = null;
	protected InputStream is = null;
	private boolean running = true;
	protected Runnable sender = null;
	protected Runnable receiver = null;
	protected Queue<Message> receiveQueue = null;
	protected Queue<Message> sendQueue = null;
	final int MAX_QUEUE_WAIT_SIZE = 10;
	private static boolean useWebSocket = false;
	public static String HTTP_VERSION_HEADER = "HTTP/1.1";
	public WsMessageHandler(Socket connection) {
		this.connection = connection;
		receiveQueue = new LinkedBlockingQueue<>();
		sendQueue = new LinkedBlockingQueue<>();
	}
	
	public void setSender(Runnable sender) {
		this.sender = sender;
	}
	
	public void setReceiver(Runnable receiver) {
		this.receiver = receiver;
	}
	
	public void setReceiveQueue(Queue<Message> q) {
		this.receiveQueue = q;
	}
	
	public void setSendQueue(Queue<Message> q) {
		this.sendQueue = q;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ExecutorService executor = Executors.newCachedThreadPool();
		try {
			log.debug("submit sender and reciver..");
			executor.submit(setupSender());
			executor.submit(setupReceiver());
			executor.submit(()->{
				receiveMessage();
			});
			executor.shutdown();
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);//wait forever unless all process done.
			log.debug("shutdown server..");
		}
		catch(Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void sendMessage(Message message) {
		synchronized(sendQueue) {
			if(sendQueue.size() > MAX_QUEUE_WAIT_SIZE) {
				log.warn("send queue is full, clean up..");
				sendQueue.clear();
			}
			log.debug("offer message: " + message);
			sendQueue.offer(message);
			sendQueue.notifyAll();
		}
	}

	@Override
	public void receiveMessage() {
		Message msg = null;

		synchronized(receiveQueue) {

			if(receiveQueue.isEmpty()) {
				log.debug("waiting for new message...");
				try {
					receiveQueue.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			msg = receiveQueue.poll();	
			log.debug("received message: " + msg);
		}
		try {
		msg = processMessage(msg);
		sendMessage(msg);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public Message processMessage(Message message) {
		// TODO Auto-generated method stub
		HttpMessage tmpMessage = new HttpMessage(message.getText(), new Date());
		String handshakeResponse = generateHandshakeResponse(tmpMessage);
		//log.debug("handshake response: " + handshakeResponse);
		useWebSocket = true;
		return new Message(handshakeResponse, new Date());

	}
	
	private String generateHandshakeResponse(HttpMessage message) {
		String webSocketKey = message.getVal(HttpMessage.SEC_WEBSOCKET_KEY);
		String webSocketAccept = webSocketKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
		byte[] bytes = DigestUtils.sha1(webSocketAccept);
		String secWebSocketAcceptVal = Base64.encodeBase64String(bytes);
		
		StringBuilder sb = new StringBuilder();
		sb.append(HTTP_VERSION_HEADER).append(" 101 ").append("Switching Protocols\r\n");
		sb.append("Upgrade: websocket\r\n");
		sb.append("Connection: Upgrade\r\n");
		sb.append("Sec-WebSocket-Accept: ").append(secWebSocketAcceptVal).append("\r\n");
		//log.debug(sb.toString());
		return sb.toString();
	}

	@Override
	public void validateMessage(Message message) {
		// TODO Auto-generated method stub

	}

	public Runnable setupSender() {
		if(this.sender != null) return sender;
		return ()->{
			BufferedWriter bw = null;
			try {
				while(running) {
					bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())); 
					Message msg = null;
					synchronized(sendQueue) {
						if(sendQueue.isEmpty()) {
							sendQueue.wait();
						}
						msg = sendQueue.poll();
					}
					log.debug("about to write message: " + msg);
					bw.write(msg.getText());
					bw.newLine();
					bw.flush();
				}
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				//connection should be closed outside.
				bw = null;
			}

		};
	}
	
	public Runnable setupReceiver() {
		if(receiver != null) return receiver;
		return ()->{
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				InputStream is = connection.getInputStream();
				
				String line = null;
				StringBuilder sb = new StringBuilder();
				do {
					if(useWebSocket) {
						log.debug("Use websocket, read byte...");
						byte[] bArray = IOUtils.toByteArray(is);
						log.debug("array: " + Arrays.toString(bArray));
						
					}else {
						log.debug("read something..");
						line = br.readLine();
						
						
						log.debug(line);
						if(line != null) {
							if(line.equals("")) {
								
								Message msg = new Message(sb.toString(), new Date());
								synchronized(receiveQueue) {
									if(receiveQueue.size() > MAX_QUEUE_WAIT_SIZE) {
										log.warn("receive queue reach max queue wait size, clean all...");
										receiveQueue.clear();
									}
									//log.debug("send message ....." + msg);
									receiveQueue.add(msg);
									receiveQueue.notifyAll();
									useWebSocket=true;
								}
							}
							else {
								sb.append(line).append(System.lineSeparator());
							}
						}
					}
				}while(true);
				
				//log.debug("receiver exits...");
			
			}
			catch(Exception e) {
				log.error(e.getMessage(), e);
			}
		};
	}
}
