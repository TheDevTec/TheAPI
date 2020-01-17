package me.Straiker123.Events;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EntityBreedEvent extends Event implements Cancellable {
	Entity s;
	Location loc;
	Player p;
	public EntityBreedEvent(Player p, Location location, Entity entity) {
		s=entity;
		loc=location;
		this.p=p;
	}
	private static final HandlerList handler = new HandlerList();
	public EntityType getEntityType() {
		return s.getType();
	}
	public Player getBreeder() {
		return p;
	}
	public Location getLocation() {
		return loc;
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
	
	public Entity getEntity() {
		return s;
	}

	@Override
	public HandlerList getHandlers() {
		return handler;
	}
	
	public static HandlerList getHandlerList() {
		return handler;
	}}
