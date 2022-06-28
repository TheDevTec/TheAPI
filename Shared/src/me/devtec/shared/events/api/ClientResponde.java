package me.devtec.shared.events.api;

public enum ClientResponde {

	ACCEPTED(1), REJECTED_PASSWORD(2), REJECTED_PLUGIN(3), PROCESS_LOGIN(4), RECEIVE_DATA(10), RECEIVE_NAME(11), RECEIVE_FILE(12), RECEIVE_DATA_AND_FILE(13), UKNOWN(0);

	private int id;
	ClientResponde(int id){
		this.id=id;
	}

	public int getResponde() {
		return id;
	}

	public static ClientResponde fromResponde(int responde) {
		for(ClientResponde clientResponde : ClientResponde.values())
			if(clientResponde.getResponde()==responde)return clientResponde;
		return ClientResponde.UKNOWN;
	}
}
