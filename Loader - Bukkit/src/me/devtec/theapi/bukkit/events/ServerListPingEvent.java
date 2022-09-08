package me.devtec.theapi.bukkit.events;

import java.net.InetAddress;
import java.util.List;

import me.devtec.shared.events.Cancellable;
import me.devtec.shared.events.Event;
import me.devtec.theapi.bukkit.nms.GameProfileHandler;

public class ServerListPingEvent extends Event implements Cancellable {
	private boolean cancel;
	private int online;
	private int max;
	private int protocol;
	private List<GameProfileHandler> playersText;
	private String motd;
	private String falvicon;
	private String version;
	private final InetAddress address;

	public ServerListPingEvent(int online, int max, List<GameProfileHandler> playersText, String motd, String falvicon, InetAddress inetAddress, String ver, int protocol) {
		this.online = online;
		this.max = max;
		this.playersText = playersText;
		this.motd = motd;
		this.falvicon = falvicon;
		this.protocol = protocol;
		address = inetAddress;
		version = ver;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String ver) {
		version = ver;
	}

	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	public InetAddress getAddress() {
		return address;
	}

	public int getOnlinePlayers() {
		return online;
	}

	public int getMaxPlayers() {
		return max;
	}

	public void setOnlinePlayers(int online) {
		this.online = online;
	}

	public void setMaxPlayers(int max) {
		this.max = max;
	}

	public List<GameProfileHandler> getPlayersText() {
		return playersText;
	}

	public void setPlayersText(List<GameProfileHandler> playersText) {
		this.playersText = playersText;
	}

	public String getMotd() {
		return motd;
	}

	public void setMotd(String motd) {
		this.motd = motd;
	}

	public String getFalvicon() {
		return falvicon;
	}

	public void setFalvicon(String falvicon) {
		this.falvicon = falvicon;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
