package com.early.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import com.early.socket.message.HttpMessage;
import com.early.socket.message.Message;
 
public class EchoServer {
	public static String HTTP_VERSION_HEADER = "HTTP/1.1";
    public static void main(String[] args) throws IOException {
         
        int portNumber = 8080;
         
        try (
            ServerSocket serverSocket =
                new ServerSocket(Integer.parseInt("8080"));
            Socket clientSocket = serverSocket.accept();     
            PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);                   
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String inputLine;
            StringBuilder sb = new StringBuilder();
            while ( true) {
            		inputLine = in.readLine();
            		if(inputLine == null) {
            			//System.out.println("input is null");
            			continue;
            		}
            	    System.out.println("inputline: " + inputLine);
            	    sb.append(inputLine).append(System.lineSeparator());
            	    if(inputLine.equals("")) {
            	    		Message msg = new Message(sb.toString(), new Date());
            	    		HttpMessage tmpMessage = new HttpMessage(msg.getText(), new Date());
            	    		String response = generateHandshakeResponse(tmpMessage);
            	    		System.out.println("response: " + response);
            	    		out.println(response);
            	    		out.flush();
            	    }
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
    
	private static String generateHandshakeResponse(HttpMessage message) {
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
}
