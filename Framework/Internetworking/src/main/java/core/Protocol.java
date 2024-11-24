package core;

import java.io.IOException;

import exceptions.IWProtocolException;

/*
 * Protocol base class (abstract)
 */
public abstract class Protocol {
	public enum proto_id {
		PHY, APP, SLP, CP
	}

	public abstract void send(String s, Configuration config) throws IOException, IWProtocolException;
	public abstract Msg receive() throws IOException, IWProtocolException;

}
