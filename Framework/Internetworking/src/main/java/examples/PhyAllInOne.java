package examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

import core.Msg;
import core.Protocol;
import exceptions.IWProtocolException;
import phy.PhyProtocol;
import phy.PhyConfiguration;

public class PhyAllInOne implements Runnable {
	private static final String SERVERNAME = "localhost";
	PhyProtocol phy;
	
	public PhyAllInOne(PhyProtocol p) {
		this.phy = p;
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Provide an UDP port number");
			return;
		}
		int phyPortNumber = Integer.parseInt(args[0]);
		
		// Set up the virtual link protocol
		PhyProtocol phy = new PhyProtocol(phyPortNumber);
		
		// Start the receiver side of the app
		PhyAllInOne recv = new PhyAllInOne(phy);
		Thread recvT = new Thread(recv);
		recvT.start();

		// Read data from user to send to client
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Message Destination: ");
		String sentence;
		try {
			sentence = inFromUser.readLine();
			sentence = sentence.trim();
		} catch (IOException | NullPointerException e1) {
			System.out.println("Incorrect switch port! Aborting ...");
			return;
		}
		int dst = Integer.parseInt(sentence);
		
		boolean eof = false;
		while (!eof) {
			try {
				System.out.println("Message: ");
				sentence = null;
				sentence = inFromUser.readLine();
				if (sentence.equalsIgnoreCase("eof"))
					eof = true;
				PhyConfiguration config = new PhyConfiguration(InetAddress.getByName(SERVERNAME), dst, Protocol.proto_id.APP);
				phy.send(sentence.trim(), config);
			} catch (IWProtocolException | IOException | NullPointerException e) {
				e.printStackTrace();
			}
		}

	}
	
	// Receiver thread
	@Override
	public void run() {
		boolean eof = false;
		
		while (!eof) {
			try {
				Msg msg = this.phy.receive();
				String sentence = msg.getData().trim();
				if (sentence.equalsIgnoreCase("eof"))
					eof = true;
				else
					System.out.println("Received message: " + sentence);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
	}

}
