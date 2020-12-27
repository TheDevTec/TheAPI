package me.devtec.theapi.utils.listener.events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.utils.listener.Cancellable;
import me.devtec.theapi.utils.listener.Event;

public class PlayerItemBreakEvent extends Event implements Cancellable {

	private Player s;
	private ItemStack i;
	private boolean cancel;

	public PlayerItemBreakEvent(Player p, ItemStack item) {
		s = p;
		i = item;
	}

	public ItemStack getItem() {
		return i;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	public Player getPlayer() {
		return s;
	}
}
