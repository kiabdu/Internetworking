package phy;

import core.Msg;
import core.Protocol;
import exceptions.IllegalMsgException;

/*
 * The only message class for the phy layer
 * This layer prepends a "phy" header when sending a message
 * This layer removes the "phy" header when receiving a message
 */
public class PhyMsg extends Msg {
	protected static final String PHY_HEADER = "phy";
	protected Protocol.proto_id pid;

	protected PhyMsg() {}
	protected PhyMsg(PhyConfiguration config) {
		super();
		this.config = config;
	}

	protected Protocol.proto_id getPid() {return this.pid;}

	/*
	 * Prepend header for sending
	 */
	@Override
	protected void create(String data) {
		this.data = data;
		int id;
		PhyConfiguration conf = (PhyConfiguration) this.config;
		id = switch (conf.getPid()) {
			case PHY -> 1;
			case APP -> 3;
			case SLP -> 5;
			case CP -> 7;
		};
		data = PHY_HEADER + " " + id + " " +data;
		this.dataBytes = data.getBytes();
	}
	
	/*
	 * Does the message start with the correct header
	 *  -> if not illegal message
	 * Remove header and populate data attribute
	 */
	@Override
	protected Msg parse(String sentence) throws IllegalMsgException {
		this.dataBytes = sentence.getBytes();
		if (!sentence.startsWith(PHY_HEADER)) {
			System.out.println("Illeagal data header: " + sentence);
			throw new IllegalMsgException();
		}
		String[] parts = sentence.split("\\s+", 3);
		PhyMsg pdu;
		// Parse the protocol id
		int id;
		try {
			id = Integer.parseInt(parts[1]);
		} catch (NumberFormatException e) {
			throw new IllegalMsgException();
		}
		//Check protocol id
		switch (id) {
			case 1 -> pid = Protocol.proto_id.PHY;
			case 3 -> pid = Protocol.proto_id.APP;
			case 5 -> pid = Protocol.proto_id.SLP;
			case 7 -> pid = Protocol.proto_id.CP;
			default -> throw new IllegalMsgException();
		}
		// If the second token is "1", call the PhyPingMsg parser
		if(pid == Protocol.proto_id.PHY && parts[2].startsWith(PhyPingMsg.PHY_PING_HEADER)) {
			pdu = new PhyPingMsg((PhyConfiguration) this.config);
			pdu.parse(parts[2]);
		} else {
			this.data = parts[2];
			pdu = this;
		}
		return pdu;
	}
	
}
