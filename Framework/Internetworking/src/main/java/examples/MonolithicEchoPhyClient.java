package examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MonolithicEchoPhyClient {

	public static void main(String[] args) {
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		try (DatagramSocket datagramSocket = new DatagramSocket()) {
			// Read String from keyboard
			System.out.print("Your message for the server: ");
			String sentence = inFromUser.readLine();
			
			// Add protocol header
			sentence = "phy 3 " + sentence;
			
			// Send packet
			byte[] buffer = sentence.getBytes();
			DatagramPacket packet = new DatagramPacket(
			        buffer, buffer.length, InetAddress.getByName("localhost"), 12000);
			datagramSocket.send(packet);
			
			// Receive a packet
			byte[] receiveData = new byte[2048];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			datagramSocket.receive(receivePacket);
			String message = new String(receivePacket.getData());
			
			// Strip protocol header 
			message = message.substring("phy 3 ".length()).trim();
			System.out.println("Received message: " + message);

			// SocketException and UnknownHostException are subclasses of IOException
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
