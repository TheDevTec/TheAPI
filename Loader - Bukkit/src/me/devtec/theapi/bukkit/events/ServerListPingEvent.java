package me.devtec.theapi.bukkit.events;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

import me.devtec.shared.events.Cancellable;
import me.devtec.shared.events.Event;

public class ServerListPingEvent extends Event implements Cancellable {
	private boolean cancel;
	private int online;
	private int max;
	private int protocol;
	private List<PlayerProfile> playersText;
	private String motd;
	private String falvicon;
	private String version;
	private final InetAddress address;

	public ServerListPingEvent(int online, int max, List<PlayerProfile> playersText, String motd, String falvicon,
			InetAddress inetAddress, String ver, int protocol) {
		this.online = online;
		this.max = max;
		this.playersText = playersText;
		this.motd = motd;
		this.falvicon = falvicon;
		this.protocol = protocol;
		this.address = inetAddress;
		this.version = ver;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String ver) {
		this.version = ver;
	}

	public int getProtocol() {
		return this.protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	public InetAddress getAddress() {
		return this.address;
	}

	public int getOnlinePlayers() {
		return this.online;
	}

	public int getMaxPlayers() {
		return this.max;
	}

	public void setOnlinePlayers(int online) {
		this.online = online;
	}

	public void setMaxPlayers(int max) {
		this.max = max;
	}

	public List<PlayerProfile> getPlayersText() {
		return this.playersText;
	}

	public void setPlayersText(List<PlayerProfile> playersText) {
		this.playersText = playersText;
	}

	public String getMotd() {
		return this.motd;
	}

	public void setMotd(String motd) {
		this.motd = motd;
	}

	public String getFalvicon() {
		return this.falvicon;
	}

	public void setFalvicon(String falvicon) {
		this.falvicon = falvicon;
	}

	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	public static class PlayerProfile {
		private static final UUID defaultUuid = UUID.randomUUID();
		private static final String defaultName = "";

		private String name;
		private UUID uuid;

		public PlayerProfile(String name, UUID u) {
			if (name == null)
				this.name = PlayerProfile.defaultName;
			else
				this.name = name;
			if (u == null)
				this.uuid = PlayerProfile.defaultUuid;
			else
				this.uuid = u;
		}

		public PlayerProfile(String name) {
			if (name == null)
				this.name = PlayerProfile.defaultName;
			else
				this.name = name;
			this.uuid = PlayerProfile.defaultUuid;
		}

		public PlayerProfile(UUID u) {
			this.name = PlayerProfile.defaultName;
			if (u == null)
				this.uuid = PlayerProfile.defaultUuid;
			else
				this.uuid = u;
		}

		public PlayerProfile() {
			this.name = PlayerProfile.defaultName;
			this.uuid = PlayerProfile.defaultUuid;
		}

		public PlayerProfile setName(String name) {
			if (name == null)
				this.name = PlayerProfile.defaultName;
			else
				this.name = name;
			return this;
		}

		public String getName() {
			return this.name;
		}

		public PlayerProfile setUUID(UUID uuid) {
			if (uuid == null)
				this.uuid = PlayerProfile.defaultUuid;
			else
				this.uuid = uuid;
			return this;
		}

		public UUID getUUID() {
			return this.uuid;
		}
	}
}
