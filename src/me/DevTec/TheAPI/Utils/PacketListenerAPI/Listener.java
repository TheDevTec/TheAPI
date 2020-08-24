package me.DevTec.TheAPI.Utils.PacketListenerAPI;

import org.bukkit.entity.Player;

public abstract class Listener {
	private Priority priority=Priority.NORMAL;
	
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
	
	public Priority getPriority() {
		return priority;
	}
	
	protected final Object call(Player player, Object packet, boolean isPacketOut) {
		if(packet==null)return null;
		if(isPacketOut)PacketPlayOut(player,packet);
		else PacketPlayIn(player,packet);
		return packet;
	}
	
	public abstract void PacketPlayOut(Player player, Object packet);
	
	public abstract void PacketPlayIn(Player player, Object packet);
}
