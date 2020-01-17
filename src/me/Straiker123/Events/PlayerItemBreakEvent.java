package me.Straiker123.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PlayerItemBreakEvent extends Event implements Cancellable {
	public PlayerItemBreakEvent(Player p, ItemStack item) {
		s=p;
		i=item;
	}
	Player s;
	ItemStack i;
	private static final HandlerList handler = new HandlerList();
	
	public ItemStack getItem() {
		return i;
	}
	
	@Override
	public boolean isCancelled() {
		return cancel;
	}
	boolean cancel;
	@Override
	public void setCancelled(boolean cancel) {
		this.cancel=cancel;
	}
	
	public Player getPlayer() {
		return s;
	}

	@Override
	public HandlerList getHandlers() {
		return handler;
	}
	
	public static HandlerList getHandlerList() {
		return handler;
	}

}
