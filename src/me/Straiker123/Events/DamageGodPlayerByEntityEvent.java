package me.Straiker123.Events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class DamageGodPlayerByEntityEvent extends Event implements Cancellable {
	Player s;
	public DamageGodPlayerByEntityEvent(Player p, Entity en,double dam, DamageCause ed) {
		s=p;
		cause=ed;
		entity=en;
		damage=dam;
	}
	DamageCause cause;
	Entity entity;
	public DamageCause getCause() {
		return cause;
	}
	private static final HandlerList handler = new HandlerList();
	public Entity getEntity() {
		return entity;
	}
	@Override
	public boolean isCancelled() {
		return cancel;
	}
	boolean cancel=true;
	@Override
	public void setCancelled(boolean cancel) {
		this.cancel=cancel;
	}
	double damage;
	public double getDamage() {
		return damage;
	}
	
	public void setDamage(double value) {
		damage=value;
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
