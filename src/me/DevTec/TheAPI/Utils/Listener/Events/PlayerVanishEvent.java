package me.DevTec.TheAPI.Utils.Listener.Events;

import org.bukkit.entity.Player;

import me.DevTec.TheAPI.Utils.Listener.Cancellable;
import me.DevTec.TheAPI.Utils.Listener.Event;

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
}
