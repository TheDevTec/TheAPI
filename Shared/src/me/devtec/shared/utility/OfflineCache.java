package me.devtec.shared.utility;

import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.UUID;

import me.devtec.shared.dataholder.Config;
import me.devtec.shared.json.Json;

public class OfflineCache {
	private final String USER_FORMAT="https://api.ashcon.app/mojang/v2/user/%s";
	protected final Map<String, Query> values = new ConcurrentHashMap<>();
	
	private boolean onlineMode;
	public OfflineCache(boolean onlineMode) {
		this.onlineMode=onlineMode;
	}
	
	public UUID lookupId(String name){
		Query o = values.get(name.toLowerCase());
		if(o==null) {
			UUID uuid = onlineMode?lookupIdFromMojang(name):
				UUID.nameUUIDFromBytes(("OfflinePlayer:"+name).getBytes());
			values.put(name.toLowerCase(), new Query(name, uuid));
			return uuid;
		}
		return o.uuid;
	}
	
	@SuppressWarnings("unchecked")
	public String lookupNameFromMojang(String name) {
		try {
			return (String)((Map<String, Object>) Json.reader().simpleRead(StreamUtils.fromStream(new URL(String.format(USER_FORMAT, name)).openStream()))).get("username");
		}catch(Exception error) {}
		return name;
	}
	
	@SuppressWarnings("unchecked")
	public UUID lookupIdFromMojang(String name) {
		try {
			return UUID.fromString((String)((Map<String, Object>) Json.reader().simpleRead(StreamUtils.fromStream(new URL(String.format(USER_FORMAT, name)).openStream()))).get("uuid"));
		}catch(Exception error) {}
		return UUID.nameUUIDFromBytes(("OfflinePlayer:"+name).getBytes());
	}
	
	public String lookupNameById(UUID id) {
		for(Query i : values.values())
			if(id.equals(i.uuid))return i.name;
		return null;
	}
	
	public Query lookupQuery(String name) {
		return values.get(name.toLowerCase());
	}
	
	public Query lookupQuery(UUID id) {
		for(Query i : values.values())
			if(id.equals(i.uuid))return i;
		return null;
	}
	
	public String lookupName(String name) {
		Query get = values.get(name.toLowerCase());
		String result = name;
		if(get==null) {
			UUID uuid = onlineMode?lookupIdFromMojang(name):
				UUID.nameUUIDFromBytes(("OfflinePlayer:"+name).getBytes());
			values.put(result.toLowerCase(), new Query(result, uuid));
		}else
			result=get.name;
		return result;
	}
	
	public void setLookup(UUID uuid, String name) {
		if(uuid==null||name==null)return;
		Query get = values.get(name.toLowerCase());
		if(get==null || get.uuid==null) {
			values.put(name.toLowerCase(), new Query(name,uuid));
			return;
		}
		if(!get.uuid.equals(uuid) || !get.name.equals(name)) {
			get.name=name;
			get.uuid=uuid;
		}
	}
	
	public Config saveToConfig() {
		Config data = new Config();
		for(Query i : values.values())
			data.set(i.uuid.toString(), i.name);	
		return data;
	}
	
	public static class Query {
		public String name;
		public UUID uuid;
		public Query(String name, UUID uuid) {
			this.name=name;
			this.uuid=uuid;
		}
		
		public String getName() {
			return name;
		}
		
		public UUID getUUID() {
			return uuid;
		}
		
		public void setName(String name) {
			this.name=name;
		}
		
		public void setUUID(UUID uuid) {
			this.uuid=uuid;
		}
	}
}
