package me.devtec.theapi.utils.serverlist;

import java.util.UUID;

public class PlayerProfile {
	public PlayerProfile(String name, UUID u) {
		this.name = name;
		uuid = u;
	}

	public String name = "";
	public UUID uuid = UUID.randomUUID();
}
