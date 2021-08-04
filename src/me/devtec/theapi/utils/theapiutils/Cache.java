package me.devtec.theapi.utils.theapiutils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.devtec.theapi.utils.StreamUtils;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.json.Reader;

public class Cache {
	private String USER_FORMAT="https://api.ashcon.app/mojang/v2/user/%s";
	protected Map<String, Query> values = new HashMap<>();
	
	public UUID lookupId(String name){
		Query o = values.get(name.toLowerCase());
		if(o==null) {
			UUID uuid = Bukkit.getOnlineMode()?lookupIdFromMojang(name):
				UUID.nameUUIDFromBytes(("OfflinePlayer:"+name).getBytes(StandardCharsets.UTF_8));
			values.put(name.toLowerCase(), new Query(name, uuid));
			return uuid;
		}
		return o.uuid;
	}
	
	@SuppressWarnings("unchecked")
	public String lookupNameFromMojang(String name) {
		try {
			HttpURLConnection conn = (HttpURLConnection)new URL(String.format(USER_FORMAT, name)).openConnection();
			conn.setRequestProperty("User-Agent", "TheAPI-JavaClient");
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(20000);
			conn.connect();
			return (String)((Map<String, Object>) Reader.read(StreamUtils.fromStream(conn.getInputStream()))).get("username");
		}catch(Exception error) {}
		return name;
	}
	
	@SuppressWarnings("unchecked")
	public UUID lookupIdFromMojang(String name) {
		try {
			HttpURLConnection conn = (HttpURLConnection)new URL(String.format(USER_FORMAT, name)).openConnection();
			conn.setRequestProperty("User-Agent", "TheAPI-JavaClient");
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(20000);
			conn.connect();
			return UUID.fromString((String)((Map<String, Object>) Reader.read(StreamUtils.fromStream(conn.getInputStream()))).get("uuid"));
		}catch(Exception error) {}
		return UUID.nameUUIDFromBytes(("OfflinePlayer:"+name).getBytes(StandardCharsets.UTF_8));
	}
	
	public String lookupNameById(UUID id) {
		for(Query i : values.values())
			if(id.equals(i.uuid))return i.name;
		String name = Bukkit.getOfflinePlayer(id).getName();
		if(name==null)return null;
		values.put(name.toLowerCase(), new Query(name, id));
		return name;
	}
	
	public String lookupNameByIdOrNull(UUID id) {
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
		String result = null;
		if(get==null) {
			UUID uuid = Bukkit.getOnlineMode()?lookupIdFromMojang(name):
				UUID.nameUUIDFromBytes(("OfflinePlayer:"+name).getBytes(StandardCharsets.UTF_8));
			result=Bukkit.getOfflinePlayer(uuid).getName();
			if(result==null)result=name;
			else
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
	
	public Data saveToData() {
		Data data = new Data();
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
