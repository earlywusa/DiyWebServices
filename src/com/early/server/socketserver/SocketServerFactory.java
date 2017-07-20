package com.early.server.socketserver;

import java.io.IOException;

import com.early.http.MiniHttpServer;
import com.early.socket.DiyServerSocket;
import com.early.socket.ServerSocketImpl;
import com.early.websocket.SimpleWebSocket;

public class SocketServerFactory {
	public static enum ServerType {HTTP, SOCKET, WEBSOCKET};
	public static volatile SocketServerFactory factory = null;

	private SocketServerFactory() {
	}
	
	public static SocketServerFactory getFactory() {
		if (factory == null) {
			synchronized(SocketServerFactory.class) {
				if(factory == null) {
					factory = new SocketServerFactory();
				}
			}
		}
		return factory;
	}
	
	public DiyServerSocket constructServer(int port, ServerType type) {
		try {
			switch(type) {
			case HTTP:
				return new MiniHttpServer(port);
			case SOCKET:
				return new ServerSocketImpl(port);
			case WEBSOCKET:
				return new SimpleWebSocket(port);
			default:
				return new ServerSocketImpl(port);
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
