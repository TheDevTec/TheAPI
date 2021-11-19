package me.devtec.theapi.utils.listener.events;

import java.net.InetAddress;
import java.util.List;

import me.devtec.theapi.utils.listener.Cancellable;
import me.devtec.theapi.utils.listener.Event;
import me.devtec.theapi.utils.serverlist.PlayerProfile;

public class ServerListPingEvent extends Event implements Cancellable {
	private boolean cancel;
	private int online, max, protocol;
	private List<PlayerProfile> playersText;
	private String motd, falvicon, version;
	private final InetAddress address;

	public ServerListPingEvent(int online, int max, List<PlayerProfile> playersText, String motd, String falvicon,
			InetAddress inetAddress, String ver, int protocol) {
		this.online = online;
		this.max = max;
		this.playersText = playersText;
		this.motd = motd;
		this.falvicon = falvicon;
		this.protocol=protocol;
		this.address = inetAddress;
		version=ver;
	}

	public String getVersion() {
		return version;
	}
	
	public void setVersion(String ver) {
		version=ver;
	}

	public int getProtocol() {
		return protocol;
	}
	
	public void setProtocol(int protocol) {
		this.protocol=protocol;
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

	public List<PlayerProfile> getPlayersText() {
		return playersText;
	}

	public void setPlayersText(List<PlayerProfile> playersText) {
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

	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
