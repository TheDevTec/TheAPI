package me.devtec.shared.sockets;

import me.devtec.shared.dataholder.Config;

public interface Reader {
	public void read(ServerClient client, Config data);
}
