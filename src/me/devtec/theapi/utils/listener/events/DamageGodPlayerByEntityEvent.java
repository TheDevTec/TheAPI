package me.devtec.theapi.utils.listener.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.devtec.theapi.utils.listener.Cancellable;
import me.devtec.theapi.utils.listener.Event;

public class DamageGodPlayerByEntityEvent extends Event implements Cancellable {
	private Player s;
	private DamageCause cause;
	private Entity entity;
	private double damage;

	public DamageGodPlayerByEntityEvent(Player p, Entity en, double dam, DamageCause ed) {
		s = p;
		cause = ed;
		entity = en;
		damage = dam;
	}

	public DamageCause getCause() {
		return cause;
	}

	public Entity getEntity() {
		return entity;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	boolean cancel = true;

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double value) {
		damage = value;
	}

	public Player getPlayer() {
		return s;
	}
}
