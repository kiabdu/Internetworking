package core;

import exceptions.IWProtocolException;

/* 
 * Msg base class (abstract)
 */
public abstract class Msg {
	protected String data;
	protected byte[] dataBytes;
	protected Configuration config;
	
	public byte[] getDataBytes() {
		return this.dataBytes;
	}
	
	public int getLength() {
		return this.dataBytes.length;
	}
	
	public String getData() {
		return this.data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	public Configuration getConfiguration() {
		return this.config;
	}
	public void setConfiguration(Configuration c) {this.config = c;}
	
	protected abstract void create(String sentence);
	protected abstract Msg parse(String sentence) throws IWProtocolException;
	
	
	public void printDataBytes() {
		String msgString = new String(this.dataBytes);
		System.out.println(msgString);
	}
}
