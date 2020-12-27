package me.devtec.theapi.utils.listener.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.devtec.theapi.utils.listener.Cancellable;
import me.devtec.theapi.utils.listener.Event;

public class PlayerJumpEvent extends Event implements Cancellable {
	private Player s;
	private double i;
	private boolean cancel;
	private Location t, f;

	public PlayerJumpEvent(Player p, Location from, Location to, double jump) {
		s = p;
		f = from;
		t = to;
		i = jump;
	}

	public double getJump() {
		return i;
	}

	public Location getTo() {
		return t;
	}

	public Location getFrom() {
		return f;
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
