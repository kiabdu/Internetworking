package examples;

import java.io.IOException;

import core.Msg;
import exceptions.IWProtocolException;
import phy.PhyPingMsg;
import phy.PhyProtocol;

public class PhyPingServer {
	protected static final int SERVERPORT = 4455;
	
	public static void main(String[] args) throws IWProtocolException {
		// create phy protocol instance
		PhyProtocol phy = new PhyProtocol(SERVERPORT);
		int count=0;
		
		while (count < 2) {
			try {
				// read the message received via the phy protocol
				Msg msg = phy.receive();
				if(msg instanceof PhyPingMsg) {
					count = ((PhyPingMsg) msg).getCount();
					System.out.println("Received ping message: " + count);
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
