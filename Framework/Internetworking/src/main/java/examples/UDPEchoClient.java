package examples;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPEchoClient  {
	private final static String host = "localhost";
	
	public static void main(String[] args) throws Exception {
		// convert URI to IP address -> exception if DNS lookup fails
		InetAddress IPAddress = InetAddress.getByName(host);
		// Create socket object for sending and receiving data
		DatagramSocket clientSocket = new DatagramSocket();
		
		// Read from keyboard
		java.io.BufferedReader inFromUser =
				new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
		System.out.println("Message to server: ");
		String sentence = "";
		try {
			sentence = inFromUser.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Convert String to byte array for sending
		byte[] sendData;
		sendData = sentence.getBytes();
		System.out.println("Send to server: " + sentence);
		// Create datagram object
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, UDPEchoServer.ECHOSERVERPORT);
		try {
			clientSocket.send(sendPacket);
			// object that will contain the incoming message
			byte[] receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			// server process blocks on receive method until a message comes in
			clientSocket.receive(receivePacket);
			// convert byte array to String object for easier manipulation
			String reply = new String(receivePacket.getData()).trim();
			System.out.println("From Server: " + reply);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			clientSocket.close();
		}
	}

}
