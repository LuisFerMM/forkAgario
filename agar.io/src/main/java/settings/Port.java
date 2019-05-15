package settings;

public enum Port {

	LOGIN(8030),
	GAME(8040),
	MUSIC(8050),
	STREAM(8060),
	CHAT(8070),
	WEB(8081),
	DB(8090);
	
	private int port;
	
	private Port(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

}
