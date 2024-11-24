package examples;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServerRunnable implements Runnable {
	private final DatagramSocket serverSocket;

	public UDPServerRunnable(DatagramSocket sock) {
		this.serverSocket = sock;
	}

	@Override
	public void run() {
		// The server should run indefinitely
		while (true) {
			// Socket expects byte arrays for sending and receiving data
			byte[] receiveData = new byte[1024];

			// object that will contain the incoming message
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

			try {
				// server process blocks on receive method until a message comes in
				this.serverSocket.receive(receivePacket);

				// convert byte array to String object for easier manipulation
				String sentence = new String(receivePacket.getData()).trim();
				System.out.println("RECEIVED: " + sentence);

			} catch (Exception e) {
				System.out.println("An error occurred ... aborting");
				serverSocket.close();
				return;
			}
		}
	}
}
