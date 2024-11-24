package phy;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

import core.Configuration;
import core.Protocol;

public class PhyConfiguration extends Configuration{
	protected int remotePort;
	protected InetAddress remoteIPAddress;
	protected Protocol.proto_id pid;
	protected boolean isClient;
	
	public PhyConfiguration(InetAddress rip, int rp, Protocol.proto_id pid) throws UnknownHostException {
		super(null);
		this.remotePort = rp;
		this.remoteIPAddress = rip;
		this.pid = pid;
		this.isClient = true;
	}

	public int getRemotePort() {
		return this.remotePort;
	}
	
	public InetAddress getRemoteIPAddress () {
		return this.remoteIPAddress;
	}
	public Protocol.proto_id getPid() {return this.pid;}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		PhyConfiguration phyConf = (PhyConfiguration) o;
		return ((this.remoteIPAddress.equals(phyConf.getRemoteIPAddress())) &&
				(this.remotePort == phyConf.getRemotePort()));
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.remoteIPAddress, this.remotePort);
	}
}
