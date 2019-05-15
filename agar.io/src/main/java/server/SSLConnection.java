package server;

import java.awt.SystemColor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketException;

import javax.net.ssl.SSLSocket;

public class SSLConnection extends Thread {

	SSLSocket client;
	BufferedReader readerHS;
	PrintWriter writerHS;

	public SSLConnection(SSLSocket request) {
		super();

		client = request;
		try {
			writerHS = new PrintWriter(client.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		try {
			readerHS = new BufferedReader(new InputStreamReader(client.getInputStream()));
		while (!client.isClosed()) {
			String line = "";
				String[] supported = client.getSupportedCipherSuites();
				client.setEnabledCipherSuites(supported);
				line = readerHS.readLine();
				System.out.println(line);
				executeByClientToServer(line);
		}
		readerHS.close();
		} catch (SocketException e1) {
			System.out.println("One user has left");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void executeByClientToServer(String line) {
		String[] arregloS = line.split(" ");
		int hash = client.hashCode();
		if (arregloS.length == 3)
			Server.registerNewUser(hash, arregloS);
		else if (arregloS.length == 2)
			Server.playGameOf(hash, arregloS);
	}

	public void writeSessionStatus(String line) {
		writerHS.println(line);
	}

}
