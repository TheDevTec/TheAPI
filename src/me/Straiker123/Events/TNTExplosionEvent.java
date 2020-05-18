package me.Straiker123.Events;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.Straiker123.Position;

public class TNTExplosionEvent extends Event implements Cancellable {
	boolean b, nuclear, e, d, n, col, dr;
	int power;
	Position l;

	public TNTExplosionEvent(Position c2) {
		l = c2;
		power = 4;
		d = true;
		e = true;
		col = true;
		dr = true;
	}

	@Override
	public boolean isCancelled() {
		return b;
	}

	public void setDropItems(boolean cancel) {
		dr = cancel;
	}

	public boolean isDropItems() {
		return dr;
	}

	public void setTNTInLiquidCancelEvent(boolean cancel) {
		col = cancel;
	}

	public boolean canTNTInLiquidCancelEvent() {
		return col;
	}

	public void setNuclearDestroyLiquid(boolean can) {
		n = can;
	}

	public boolean canNuclearDestroyLiquid() {
		return n;
	}

	public int getPower() {
		return power;
	}

	public void setDestroyBlocks(boolean destroy) {
		d = destroy;
	}

	public boolean canDestroyBlocks() {
		return d;
	}

	public void setDamageEntities(boolean damage) {
		e = damage;
	}

	public boolean canHitEntities() {
		return e;
	}

	public Position getLocation() {
		return l;
	}

	public boolean isNuclearBomb() {
		return nuclear;
	}

	public void setNuclearBomb(boolean bomb) {
		nuclear = bomb;
	}

	public void setPower(int power) {
		if (power < 30) {
			this.power = power;
		} else {
			if (nuclear) {
				this.power = power;
			} else
				Bukkit.getLogger().warning(
						"Error when setting power of tnt explosion higher than 30, Reason: You must enable Nuclear bomb mode");
		}
	}

	@Override
	public void setCancelled(boolean cancel) {
		b = cancel;
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
