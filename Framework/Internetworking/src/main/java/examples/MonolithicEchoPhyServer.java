package examples;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class MonolithicEchoPhyServer {
	public static void main(String[] args) {
		try (DatagramSocket datagramSocket = new DatagramSocket(12000)) {
			DatagramPacket receivePacket;
			while (true) {
				// Receive packet 
				byte[] receiveData = new byte[2048];
				receivePacket = new DatagramPacket(receiveData, receiveData.length);
				datagramSocket.receive(receivePacket);
				
				String message = new String(receivePacket.getData());
				
				// Strip protocol header 
				String sentence = message.substring("phy 3 ".length()).trim();
				System.out.println("Received message: " + sentence);
				
				// Process string
				sentence = sentence.toUpperCase();
				
				// Add protocol header
				sentence = "phy 3 " + sentence;
				byte[] buffer = sentence.getBytes();

				// Send packet
				DatagramPacket packet = new DatagramPacket(
				        buffer, buffer.length, receivePacket.getAddress(), receivePacket.getPort());
				datagramSocket.send(packet);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
