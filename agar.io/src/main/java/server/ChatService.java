package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import client.StreamingViewer;
import settings.ServerMessage;

public class ChatService extends Thread{

	Socket client;
	BufferedReader readerC;
	PrintWriter writerC;

	public ChatService(Socket c) {
		client = c;
		try {
			writerC = new PrintWriter(c.getOutputStream(), true);
			readerC = new BufferedReader(new InputStreamReader(c.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			
			while (true) {
				final String clientText = readerC.readLine();
				System.out.println(clientText);
				Server.sendToChat(clientText);
			}

//			readerC.get(i).close();
		} catch (SocketException e1) {
			System.out.println("One user has left");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void sendToClient(String newMessage) {
		writerC.println(newMessage);
	}
}
