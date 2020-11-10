package me.DevTec.TheAPI.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerVanishEvent extends Event implements Cancellable {
	private Player p;
	private boolean v, cancel;
	private String s;

	public PlayerVanishEvent(Player player, String perm, boolean b) {
		p = player;
		v = b;
		s = perm;
	}

	public String getPermission() {
		return s;
	}

	public void setPermission(String perm) {
		s = perm;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	public Player getPlayer() {
		return p;
	}

	public boolean vanish() {
		return v;
	}

	public void setVanish(boolean vanish) {
		v = vanish;
	}

	@Override
	public void setCancelled(boolean c) {
		cancel = c;
	}

	private static final HandlerList c = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return c;
	}

	public static HandlerList getHandlerList() {
		return c;
	}

}
