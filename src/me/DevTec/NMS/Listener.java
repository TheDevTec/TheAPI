package me.DevTec.NMS;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

public abstract class Listener {
	public Packet call(Player s, Object packet, boolean receive) {
		if(packet==null)return null;
		if(s==null||!s.isOnline())return null;
		if(receive)
		return receive(s, new Packet(packet));
		return read(s, new Packet(packet));
	}
	
	public abstract @Nullable Packet receive(Player player, Packet packet);
	
	public abstract @Nullable Packet read(Player player, Packet packet);
}
