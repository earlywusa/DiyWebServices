package com.early.socket;

import java.io.IOException;
import java.net.UnknownHostException;

public interface DiyClientSocket extends Runnable{
	public boolean connect() throws UnknownHostException, IOException ;
	public void getState();
	public void close();

}
