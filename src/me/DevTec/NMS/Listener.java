package me.DevTec.NMS;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

public abstract class Listener {
	public Object call(Player s, Object packet, boolean receive) {
		if(packet==null)return null;
		if(s==null||!s.isOnline())return null;
		if(receive)
		return receive(s, packet);
		return read(s, packet);
	}
	
	public abstract @Nullable Object receive(Player player, Object packet);
	
	public abstract @Nullable Object read(Player player, Object packet);
}
