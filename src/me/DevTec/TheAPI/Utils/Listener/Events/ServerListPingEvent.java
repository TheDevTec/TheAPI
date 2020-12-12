package me.DevTec.TheAPI.Utils.Listener.Events;

import java.net.InetAddress;
import java.util.List;

import me.DevTec.TheAPI.Utils.Listener.Cancellable;
import me.DevTec.TheAPI.Utils.Listener.Event;
import me.DevTec.TheAPI.Utils.ServerList.PlayerProfile;

public class ServerListPingEvent extends Event implements Cancellable {
	private boolean b;
	private int online, max;
	private List<PlayerProfile> playersText;
	private String motd, falvicon, version;
	private final InetAddress address;

	public ServerListPingEvent(int online, int max, List<PlayerProfile> playersText, String motd, String falvicon,
			InetAddress inetAddress, String ver) {
		this.online = online;
		this.max = max;
		this.playersText = playersText;
		this.motd = motd;
		this.falvicon = falvicon;
		this.address = inetAddress;
		version=ver;
	}

	public String getVersion() {
		return version;
	}
	
	public void setVersion(String ver) {
		version=ver;
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
		return b;
	}

	@Override
	public void setCancelled(boolean cancel) {
		b = cancel;
	}
}
