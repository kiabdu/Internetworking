package examples;

import java.io.IOException;

import phy.PhyMsg;
import phy.PhyProtocol;
import exceptions.IWProtocolException;

/*
 * Server using the phy protocol
 */
public class SimplexPhyServer {
	protected static final int SERVERPORT = 4999;
	
	public static void main(String[] args) throws IWProtocolException {
		// create phy protocol instance
		PhyProtocol phy = new PhyProtocol(SERVERPORT);
		
		boolean eof = false;
		while (!eof) {
			try {
				// read the message received via the phy protocol
				PhyMsg msg = (PhyMsg) phy.receive();
				String sentence = msg.getData().trim();
				// if client sends eof all data was transferred and we can close the server app
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
