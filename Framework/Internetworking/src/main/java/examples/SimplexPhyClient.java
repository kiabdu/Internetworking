package examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import core.Protocol;
import phy.PhyConfiguration;
import phy.PhyProtocol;
import exceptions.IWProtocolException;

/*
 * Client using the phy protocol 
 */
public class SimplexPhyClient {
	private static final String SERVERNAME = "localhost";
	public static final int CLIENTPORT = 6789;
	
	public static void main(String[] args) {
		// Create a new phy protocol instance with defined UDP port
		PhyProtocol proto = new PhyProtocol(CLIENTPORT);

		PhyConfiguration config;
		try {
			// create a new phy configuration object
			// getByName might throw exception
			config = new PhyConfiguration(InetAddress.getByName(SERVERNAME), SimplexPhyServer.SERVERPORT, Protocol.proto_id.APP);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			return;
		}
		
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		boolean eof = false;
		while (!eof) {
			System.out.print("Your message for the server: ");
			String sentence;
			try {
				// Read data from user to send to client -> wait for answer
				sentence = inFromUser.readLine();
				if (sentence.equalsIgnoreCase("eof"))
					eof = true;
				// Call phy protocol to send the message to peer
				proto.send(sentence, config);
			} catch (IWProtocolException | IOException e) {
				e.printStackTrace();
			}
		}

	}

}
