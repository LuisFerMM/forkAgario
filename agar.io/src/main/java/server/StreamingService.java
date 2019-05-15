package server;

import java.net.*;

import settings.Port;

import java.io.*;

public class StreamingService extends Thread {
	private DatagramSocket socket;
	private InetAddress group;
	private byte[] buf;

	public void run() {
		try {
			socket = new DatagramSocket();
			group = InetAddress.getByName("230.0.0.0");
			while (true) {
				buf = Server.getStateFromGame().getBytes();
				DatagramPacket packet = new DatagramPacket(buf, buf.length, group, Port.STREAM.getPort());
				socket.send(packet);
				Thread.sleep(36);
			}
		} catch (IOException | InterruptedException e) {
			socket.close();
			e.printStackTrace();
		}
	}
}
