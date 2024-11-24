package phy;

import core.Msg;
import exceptions.IllegalMsgException;

public class PhyPingMsg extends PhyMsg {
	protected static final String PHY_PING_HEADER = "ping ";
	private int count;

	protected PhyPingMsg(PhyConfiguration config) {
		super(config);
	}
	
	public int getCount() {
		return count;
	}

	/*
	 * Create ping msg
	 */
	@Override
	protected void create(String data) {
		this.data = data;
		String sentence = PHY_PING_HEADER + data;
		super.create(sentence);
	}
	
	/*
	 * Does the message start with the correct header
	 *  -> if not illegal message
	 * Remove header and populate data attribute
	 *  -> try converting data to type Integer
	 */
	@Override
	protected Msg parse(String sentence) throws IllegalMsgException {
		this.dataBytes = sentence.getBytes();
		if (!sentence.startsWith(PHY_PING_HEADER)) {
			System.out.println("Illeagal ping header: " + sentence);
			throw new IllegalMsgException();
		}
		this.data = sentence.substring(PHY_PING_HEADER.length());
		try {
			this.count = Integer.parseInt(this.data.trim());
		} catch (NumberFormatException e) {
			throw new IllegalMsgException();
		}
		
		return this;
	}

}
