package me.DevTec.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketReadEvent extends Event implements Cancellable {
	private Player s;
	private Object o;
	private boolean c;

	public PacketReadEvent(Player player, Object packet) {
		s = player;
		o = packet;
	}

	public Object getPacket() {
		return o;
	}

	public void setPacket(Object o) {
		if (o != null)
			this.o = o;
	}

	public Player getPlayer() {
		return s;
	}

	// Packet name
	public String getName() {
		return o.toString();
	}

	@Override
	public boolean isCancelled() {
		return c;
	}

	@Override
	public void setCancelled(boolean cancel) {
		c = cancel;
	}

	private static final HandlerList cs = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return cs;
	}

	public static HandlerList getHandlerList() {
		return cs;
	}

}
