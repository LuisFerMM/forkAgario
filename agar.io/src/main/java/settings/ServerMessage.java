package settings;

public enum ServerMessage {

	SESSION_FAILED("Password or email wrong."),
	REGISTER_SUCCESS("User created successfully."),
	WAITING_MATCH("¡Wait for your friends!"),
	JOIN_SPECTATOR("Joining as spectator"),
	STARTING_MATCH("Starting match. 3, 2, 1...");
	
	private String message;
	
	private ServerMessage(String port) {
		this.message = port;
	}

	public String getMessage() {
		return message;
	}

}
