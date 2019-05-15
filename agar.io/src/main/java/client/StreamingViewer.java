package client;

import java.net.*;

import settings.Port;

import java.io.*;

public class StreamingViewer extends Thread {
	
	 protected MulticastSocket socket = null;
	 protected String gameState;
	 
	    protected byte[] buf = new byte[16000];
		
	  

		public void run() {
	        try {
				socket = new MulticastSocket(Port.STREAM.getPort());
	        InetAddress group = InetAddress.getByName("230.0.0.0");
	        socket.joinGroup(group);
	        Thread.sleep(1000);
	        while (true) {
//	    			System.out.println(infoGame);
	    				Thread.sleep(36);
	        	
	            DatagramPacket packet = new DatagramPacket(buf, buf.length);
	            socket.receive(packet);
	            String received = new String(
	              packet.getData(), 0, packet.getLength());
	            if ("end".equals(received)) {
	                break;
	            } else {
	            	gameState = received;
	            }
	            Thread.sleep(36);
	        }
	        socket.leaveGroup(group);
	        socket.close();
	        } catch (IOException | InterruptedException e) {
	        	e.printStackTrace();
	        }
	    }
}