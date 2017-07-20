package com.early.socket.message;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HttpMessage extends Message {

	public static String SEC_WEBSOCKET_KEY="Sec-WebSocket-Key";
	
	Map<String, String> keyValueMap;
	public HttpMessage(String text, Date time) {
		super(text, time);
		keyValueMap = genMap(text);
	}
	
	public String getVal(String key) {
		return keyValueMap.get(key);
	}
	
	private Map<String, String> genMap(String text){
		HashMap<String,String> hm = new HashMap<>();
		String[] arrayList = text.split(System.lineSeparator());
		
		for(String entry : arrayList) {
			int index = entry.indexOf(":");
			if(index > 0) {
				hm.put(entry.substring(0,index).trim(), entry.substring(index+1).trim());
			}
		}
		return hm;
	}

}
