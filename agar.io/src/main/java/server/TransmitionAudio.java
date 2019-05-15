package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import settings.Port;


public class TransmitionAudio extends Thread{
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		broadcastAudio();

	}
	
	
	/*
	*this method allows the transmission of the audio through the adress 230.0.0.0  creating a adress group
	*/
	private static void broadcastAudio() {
		try {
			File soundFile = new File("src/main/resources/server/audio.wav");
			FileInputStream in = new FileInputStream(soundFile);
			
			MulticastSocket socket = new MulticastSocket();
			InetAddress group = InetAddress.getByName("229.0.0.0");
			int count;
			byte buffer[] = new byte[60000];
			
			while ((count = in.read(buffer, 0, buffer.length))!=-1) {
//					System.out.println("Trasnmitiendo audio: "+buffer);
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, Port.MUSIC.getPort());
					socket.send(packet);
					Thread.sleep(500);
			}

		} catch (Exception ex) {
			// Handle exceptions
			ex.printStackTrace();
			System.out.println(ex.getMessage()+"\n");
		}
	}

}
