package me.devtec.theapi.bukkit.nms;

import java.util.UUID;

import com.google.common.collect.Multimap;

import me.devtec.theapi.bukkit.BukkitLoader;

public class GameProfileHandler {
	private String username;
	private UUID uuid;
	private Multimap<String, PropertyHandler> properties;

	public static GameProfileHandler of(String username, UUID uuid) {
		GameProfileHandler profile = new GameProfileHandler();
		profile.setUsername(username);
		profile.setUUID(uuid);
		return profile;
	}

	public static GameProfileHandler of(String username, UUID uuid, PropertyHandler textures) {
		GameProfileHandler profile = new GameProfileHandler();
		profile.setUsername(username);
		profile.setUUID(uuid);
		profile.properties.put("textures", textures);
		return profile;
	}

	public String getUsername() {
		return username;
	}

	public GameProfileHandler setUsername(String username) {
		this.username = username;
		return this;
	}

	public UUID getUUID() {
		return uuid;
	}

	public GameProfileHandler setUUID(UUID uuid) {
		this.uuid = uuid;
		return this;
	}

	public Multimap<String, PropertyHandler> getProperties() {
		return properties;
	}

	public GameProfileHandler setTextures(PropertyHandler textures) {
		properties.removeAll("textures");
		properties.put("textures", textures);
		return this;
	}

	public Object getGameProfile() {
		return BukkitLoader.getNmsProvider().toGameProfile(this);
	}

	public static class PropertyHandler {
		private String name;
		private String values;
		private String signature;

		public static PropertyHandler of(String name, String values) {
			return of(name, values, null);
		}

		public static PropertyHandler of(String name, String values, String signature) {
			PropertyHandler property = new PropertyHandler();
			property.name = name;
			property.values = values;
			property.signature = signature;
			return property;
		}

		public String getName() {
			return name;
		}

		public String getValues() {
			return values;
		}

		public PropertyHandler setValues(String value) {
			values = value;
			return this;
		}

		public String getSignature() {
			return signature;
		}

		public PropertyHandler setSignature(String value) {
			signature = value;
			return this;
		}
	}
}
