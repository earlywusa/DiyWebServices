package com.early.socket.message;

public interface MessageHandler extends Runnable{
	
	public void sendMessage(Message message);
	public void receiveMessage();
	public Message processMessage(Message message);
	public void validateMessage(Message message);
	

}
