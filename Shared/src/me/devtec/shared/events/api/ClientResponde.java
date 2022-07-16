package me.devtec.shared.events.api;

public enum ClientResponde {

	// INIT
	LOGIN(0), // password
	REQUEST_NAME(1),
	// LOGIN
	ACCEPTED_LOGIN(2), REJECTED_LOGIN_PASSWORD(3), REJECTED_LOGIN_PLUGIN(4),
	// DATA
	RECEIVE_DATA(10),
	// FILE
	RECEIVE_FILE(11), RECEIVE_DATA_AND_FILE(12),
	// FILE RECEIVE STATUS
	ACCEPTED_FILE(14), REJECTED_FILE(15), SUCCESSFULLY_DOWNLOADED_FILE(16), FAILED_DOWNLOAD_FILE(17),

	// OTHER
	PING(127), PONG(-127), UKNOWN(-1);

	private int id;

	ClientResponde(int id) {
		this.id = id;
	}

	public int getResponde() {
		return id;
	}

	public static ClientResponde fromResponde(int responde) {
		for (ClientResponde clientResponde : ClientResponde.values())
			if (clientResponde.getResponde() == responde)
				return clientResponde;
		return ClientResponde.UKNOWN;
	}
}
