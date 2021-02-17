package me.devtec.theapi.utils.serverlist;

import java.util.UUID;

public class PlayerProfile {
	private static UUID defaultUuid = UUID.randomUUID();
	private static String defaultName = "";

	private String name;
	private UUID uuid;
	
	public PlayerProfile(String name, UUID u) {
		if(name==null)this.name=defaultName;
		else this.name = name;
		if(u==null)uuid = defaultUuid;
		else uuid = u;
	}
	
	public PlayerProfile(String name) {
		if(name==null)this.name=defaultName;
		else this.name = name;
		uuid = defaultUuid;
	}
	
	public PlayerProfile(UUID u) {
		this.name=defaultName;
		if(u==null)uuid = defaultUuid;
		else uuid = u;
	}
	
	public PlayerProfile() {
		this.name=defaultName;
		uuid = defaultUuid;
	}
	
	public PlayerProfile setName(String name) {
		if(name==null)this.name=defaultName;
		else this.name=name;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public PlayerProfile setUUID(UUID uuid) {
		if(uuid==null)this.uuid = defaultUuid;
		else this.uuid = uuid;
		return this;
	}
	
	public UUID getUUID() {
		return uuid;
	}
}
