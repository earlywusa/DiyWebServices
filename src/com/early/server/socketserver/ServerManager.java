package com.early.server.socketserver;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.early.socket.DiyServerSocket;

public class ServerManager {

	private static ServerManager instance = new ServerManager();
	private Map<Integer, DiyServerSocket> servers;
	private ExecutorService service = null;
	
	public static ServerManager getInstance() {
		return instance;
	}
	
	private ServerManager() {
		servers = new ConcurrentHashMap<>();
		service = Executors.newCachedThreadPool();
	}
	
	public void startService(int port, SocketServerFactory.ServerType Type) throws UnknownHostException, IOException {
		if(servers.get(port) == null) {
			DiyServerSocket newServer = SocketServerFactory.getFactory()
					.constructServer(port, Type);
			newServer.setupServer();
			servers.put(port, newServer);
			
		}
		service.submit(servers.get(port));
	}
	
	
	public void stopServices() {
	System.out.println("About to close sockets..");
	for(Integer port : servers.keySet()) {
		
		DiyServerSocket server = servers.get(port);
		try {
			server.stopServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	System.out.println("about to shutdown services...");
	service.shutdown();
	try {
		
		System.out.println("wait 5 seconds to stop all tasks.");
		service.awaitTermination(5, TimeUnit.SECONDS);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	//System.out.println("force to shutdown...");
	//service.shutdownNow();
	}
}
