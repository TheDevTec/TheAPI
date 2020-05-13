package me.Straiker123.Events;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.Straiker123.LoaderClass;

public class EntityMoveEvent extends Event implements Cancellable {
	Entity s;
	public EntityMoveEvent(Entity p,Location from, Location to) {
		s=p;
		f=from;
		t=to;
	}
	Location t;
	Location f;
	
	public Location getTo() {
		return t;
	}
	public Location getFrom() {
		return f;
	}
	
	public boolean isEventDisabled() {
		return !LoaderClass.config.getConfig().getBoolean("Options.EntityMoveEvent.Enabled");
	}
	
	public EntityType getEntityType() {
		return s.getType();
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

	private static final HandlerList cs = new HandlerList();
	@Override
	public HandlerList getHandlers() {
		return cs;
	}
	
	public static HandlerList getHandlerList() {
		return cs;
	}}