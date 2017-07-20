package com.early.socket;

import java.io.IOException;
import java.net.UnknownHostException;

public interface DiyServerSocket extends Runnable {
	public void startServer() throws IOException;
	public void stopServer() throws IOException;
	public void setupServer() throws UnknownHostException, IOException;
}
