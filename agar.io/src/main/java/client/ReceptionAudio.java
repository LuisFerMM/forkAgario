package client;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import settings.Port;

public class ReceptionAudio extends Thread{
	
	static AudioInputStream audioInputStream;
	static SourceDataLine sourceDataLine;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		this.setPriority(MAX_PRIORITY);
		initiateAudio();

	}
	
	/*
	*this method allows the client to get the audio throug UDP conection 
	*initializing the transfer of the narration
	*/
	private static void initiateAudio() {
		try {
			MulticastSocket socket = new MulticastSocket(Port.MUSIC.getPort());
			InetAddress group = InetAddress.getByName("229.0.0.0");
			socket.joinGroup(group);
			
			while (true) {
				byte[] audioBuffer = new byte[60000];
				DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length);
				socket.receive(packet);

				try {
					byte audioData[] = packet.getData();
					InputStream byteInputStream = new ByteArrayInputStream(audioData);
					AudioFormat audioFormat = getAudioFormat();
					audioInputStream = new AudioInputStream(byteInputStream, audioFormat,
							audioData.length / audioFormat.getFrameSize());
					play(audioInputStream);
					
				} catch (Exception e) {
					e.printStackTrace();
					// Handle exceptions
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	*this method return the audioFormat 
	*/
	private static AudioFormat getAudioFormat() {
		float sampleRate = 16000F;
		int sampleSizeInBits = 16;
		int channels = 2;
		boolean signed = false;
		boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}
	
    private synchronized static void play(AudioInputStream ais) throws Exception {
    	
        try (Clip clip = AudioSystem.getClip()) {
//        	System.out.println("Playing audio: "+clip);
            clip.open(ais);
            clip.start();
            Thread.sleep(500); // given clip.drain a chance to start
            clip.drain();
        }
        catch(Exception e) {
        	e.printStackTrace();
        	System.out.println(e.getMessage());
        }
    }

}
