package com.early.socket.message;

import java.util.Date;

import lombok.Data;

@Data
public class Message {
	public Message(String text, Date time) {
		super();
		this.text = text;
		this.time = time;
	}
	private String text;
	private Date time;

	
	
}
