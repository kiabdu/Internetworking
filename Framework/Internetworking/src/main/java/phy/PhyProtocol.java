package phy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import core.*;
import exceptions.*;

public class PhyProtocol extends Protocol {
	protected DatagramSocket socket;
	
	/*
	 * Create a new PhyProtocol instance connected to UDP port provided
	 */
	public PhyProtocol(int port) {
		try {
			this.socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Create msg object and send
	 */
	@Override
	public void send(String s, Configuration config) throws IOException, IWProtocolException {
		// Create empty PhyMsg object
		PhyMsg m = new PhyMsg((PhyConfiguration) config);
		//Populate PhyMsg object with data
		m.create(s);
		// Call actual send method
		this.send(m);
	}
	
	public void send(PhyMsg m) throws IOException {
		// Create UDP packet
		DatagramPacket sendPacket = new DatagramPacket(m.getDataBytes(), m.getLength(),
				((PhyConfiguration) m.getConfiguration()).remoteIPAddress,
				((PhyConfiguration) m.getConfiguration()).remotePort);
		// send UDP packet
		socket.send(sendPacket);
	}
	
	/*
	 * receive incoming message from socket and parse -> call blocks on socket until message is received
	 * return Msg object to caller
	 */
	@Override
	public Msg receive() throws IOException {
		// read from UDP socket
		// data and meta-data contained in receivedPacket object
		byte[] receiveData = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		socket.receive(receivePacket);

		// create msg object for parsing
		PhyMsg in = new PhyMsg();
		// get data from packet data
		String sentence = new String(receivePacket.getData()).trim();
		try {
			// parse data to check if message is compliant with protocol specification
			in = (PhyMsg) in.parse(sentence);
		} catch (IllegalMsgException e) {
			e.printStackTrace();
		}
		// create a config object from packet meta-data
		PhyConfiguration config = new PhyConfiguration(receivePacket.getAddress(), receivePacket.getPort(), in.getPid());
		in.setConfiguration(config);

		// if message was parsed correctly object is returned to caller  
		return in;
	}
	
	/*
	 * wrapper method to basic receive method -> call blocks on socket until message is received or 
	 * timeout expires and exception is raised
	 */
	public Msg receive(int timeout) throws IOException {
		socket.setSoTimeout(timeout);
		Msg in;
		in = receive();

		socket.setSoTimeout(0);
		return in;
	}
	
	// Send three ping messages to another system
	public void ping(Configuration config) throws IOException, IWProtocolException {
		for(int i=0; i<3;i++) {
			// Create empty PhyPingMsg object
			PhyPingMsg m = new PhyPingMsg((PhyConfiguration) config);
			//Populate PhyPingMsg object with data
			m.create(Integer.toString(i));
			// Call actual send method
			this.send(m);
		}
	}
	
}
