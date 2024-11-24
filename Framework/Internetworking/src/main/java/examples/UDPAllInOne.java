package examples;

import java.net.DatagramSocket;

public class UDPAllInOne {
		
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Provide an UDP port number");
			return;
		}
		int port = Integer.parseInt(args[0]);
		
		// Create socket on the port provided
		DatagramSocket recvSocket = new DatagramSocket(port);
		
		// Start the receiver side of the app; share socket with this thread  
		// Create an object to run as a new activity
		UDPServerRunnable recv = new UDPServerRunnable(recvSocket);
		// Create a new thread object in which to embed the object
		Thread recvT = new Thread(recv);
		// Start the activity -> this will invoke the run-method of the Runnable object
		recvT.start();
		System.out.println("Receiver started on port: " + port);

		// Create sender thread -> the sender will create its own socket for sending
		UDPClientRunnable send = new UDPClientRunnable();
		Thread sendT = new Thread(send);
		sendT.start();
	}

}
