package me.devtec.theapi.utils.thapiutils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.devtec.theapi.utils.StreamUtils;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.json.Reader;

public class Cache {
	private String USER_FORMAT="https://api.ashcon.app/mojang/v2/user/%s";
	protected Map<String, String> nameLookup = new HashMap<>();
	protected Map<String, UUID> uuidLookup = new HashMap<>();
	
	public UUID lookupId(String name){
		return uuidLookup.getOrDefault(name.toLowerCase(), 
				Bukkit.getOnlineMode()?lookupIdFromMojang(name):
					UUID.nameUUIDFromBytes(("OfflinePlayer:"+name).getBytes(StandardCharsets.UTF_8)));
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
		return null;
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
		return null;
	}
	
	public String lookupNameById(UUID id){
		for(Entry<String, UUID> i : uuidLookup.entrySet())
			if(i.getValue().equals(id))return i.getKey();
		return Bukkit.getOfflinePlayer(id).getName();
	}
	
	public String lookupName(String name) {
		String get = nameLookup.get(name.toLowerCase());
		if(get==null) {
			get=Bukkit.getOfflinePlayer(lookupId(name)).getName();
			if(get==null)get=name;
		}
		return get;
	}
	
	public void setLookup(UUID uuid, String name) {
		if(!uuidLookup.containsKey(name.toLowerCase())||!lookupNameById(uuid).equals(name)) {
			uuidLookup.put(name.toLowerCase(), uuid);
			nameLookup.put(name.toLowerCase(), name);
		}
	}
	
	public Data saveToData() {
		Data data = new Data();
		for(Entry<String, UUID> i : uuidLookup.entrySet())
			data.set(i.getValue().toString(), nameLookup.get(i.getKey()));	
		return data;
	}
}
