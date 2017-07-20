package test.socket;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.early.socket.ClientSocketImpl;
import com.early.socket.DiyClientSocket;
import com.early.socket.DiyServerSocket;
import com.early.socket.ServerSocketImpl;

public class SocketTest {
	
	@Before
	public void setup() {
		DiyServerSocket server = new ServerSocketImpl(8088);
		try {
			server.setupServer();
			System.out.println("starting up server");
			server.startServer();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void runServerTest() {

	}
}
