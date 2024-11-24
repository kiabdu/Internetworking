package core;

/*
 * Abstract configuration class
 * nextLowerProtocol attribute is currently unused (ignore)
 */
public class Configuration {
	protected Protocol nextLowerProtocol;
	
	public Configuration(Protocol proto) {
		this.nextLowerProtocol = proto;
	}

	public Protocol getNextLowerProtocol() {
		return nextLowerProtocol;
	}

	private void setNextLowerProtocol(Protocol nextLowerProtocol) {
		this.nextLowerProtocol = nextLowerProtocol;
	}
}
