package me.Straiker123.Events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TNTExplosionEvent extends Event implements Cancellable {
	boolean b,nuclear,sync,e,d,n,col;
	int power;
	Location l;
	public TNTExplosionEvent(Location loc) {
		l=loc;
		power=4;
		d=true;
		e=true;
		sync=true;
		col=true;
	}
	@Override
	public boolean isCancelled() {
		return b;
	}
	
	public void setTNTInLiquidCancelEvent(boolean cancel) {
		col=cancel;
	}
	
	public boolean canTNTInLiquidCancelEvent() {
		return col;
	}
	
	public void setNuclearDestroyLiquid(boolean can) {
		n=can;
	}
	
	public boolean canNuclearDestroyLiquid() {
		return n;
	}

	public int getPower() {
		return power;
	}
	public void setSynchronized(boolean synchronize) {
		sync=synchronize;
	}

	public void setDestroyBlocks(boolean destroy) {
		d=destroy;
	}
	
	public boolean canDestroyBlocks() {
		return d;
	}

	public void setDamageEntities(boolean damage) {
		e=damage;
	}
	
	public boolean canHitEntities() {
		return e;
	}
	
	public boolean isSynchronized() {
		return sync;
	}
	
	public Location getLocation() {
		return l;
	}
	
	public boolean isNuclearBomb() {
		return nuclear;
	}
	
	public void setNuclearBomb(boolean bomb) {
		nuclear=bomb;
	}
	
	public void setPower(int power) {
		if(power < 30) {
			this.power=power;
		}else {
			if(nuclear) {
				this.power=power;
			}else
				Bukkit.getLogger().warning("Error when setting power of tnt explosion higher than 30, Reason: You must enable Nuclear bomb mode");
		}
	}
	
	@Override
	public void setCancelled(boolean cancel) {
		b=cancel;
	}
	static HandlerList c = new HandlerList();
	@Override
	public HandlerList getHandlers() {
		return c;
	}
	
	public static HandlerList getHandlerList() {
		return c;
	}

}
