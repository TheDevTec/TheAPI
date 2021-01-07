package me.devtec.theapi.sockets;

import me.devtec.theapi.utils.datakeeper.Data;

public interface Reader {
	public void read(ServerClient client, Data data);
}
