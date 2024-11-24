package examples;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import core.Protocol;
import exceptions.IWProtocolException;
import phy.PhyConfiguration;
import phy.PhyProtocol;

/*
 * Client using the phy protocol 
 */
public class PhyPingClient {

	private static final String SERVERNAME = "localhost";
	public static final int CLIENTPORT = 4444;

	public static void main(String[] args) {
		// Create a new phy protocol instance with defined UDP port
		PhyProtocol proto = new PhyProtocol(CLIENTPORT);

		PhyConfiguration config;
		try {
			// create a new phy configuration object
			// getByName might throw exception
			config = new PhyConfiguration(InetAddress.getByName(SERVERNAME), PhyPingServer.SERVERPORT, Protocol.proto_id.APP);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			return;
		}

		try {
			proto.ping(config);
		} catch (IWProtocolException | IOException e) {
			e.printStackTrace();
		}

	}

}
