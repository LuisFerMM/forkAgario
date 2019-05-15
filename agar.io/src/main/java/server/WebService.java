package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebService extends Thread{

	Server server;
	
	public WebService (Server server) {
		
		this.server = server;
	}
	
	public void run() {
		
		while(server.isWebOnFire()) {
			System.out.println(":::Web Server Started:::");
			ServerSocket serverSocket = server.getWebSocket();
			try {
				Socket cliente = serverSocket.accept();
				ClientRequest hilo = new ClientRequest(cliente, server);
				hilo.start();	
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
}
