package me.DevTec.TheAPI.Utils.PacketListenerAPI;

import org.bukkit.entity.Player;

public abstract class Listener {
	public Listener() {
		this(Priority.NORMAL);
	}

	public Listener(Priority prio) {
		priority=prio;
	}
	
	private Priority priority;
	
	public final Listener setPriority(Priority prio) {
		if(prio==null)return this;
		PacketManager.notify(this, priority, prio);
		priority=prio;
		return this;
	}
	
	public final void register() {
		PacketManager.register(this);
	}
	
	public final void unregister() {
		PacketManager.unregister(this);
	}
	
	public final Priority getPriority() {
		return priority;
	}
	
	protected final Object call(Player player, Object packet, Object channel, boolean isPacketOut) {
		if(packet==null || channel == null || (isPacketOut?PacketPlayOut(player,packet, channel):PacketPlayIn(player,packet, channel)))return null;
		return packet;
	}
	
	public abstract boolean PacketPlayOut(Player player, Object packet, Object channel);
	
	public abstract boolean PacketPlayIn(Player player, Object packet, Object channel);
}
