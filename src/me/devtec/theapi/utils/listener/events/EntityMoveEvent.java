package me.devtec.theapi.utils.listener.events;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import me.devtec.theapi.utils.listener.Cancellable;
import me.devtec.theapi.utils.listener.Event;
import me.devtec.theapi.utils.thapiutils.LoaderClass;

public class EntityMoveEvent extends Event implements Cancellable {
	private Entity entity;
	private boolean cancel;
	private Location to, from;

	public EntityMoveEvent(Entity entity, Location from, Location to) {
		this.entity = entity;
		this.from = from;
		this.to = to;
	}

	public Location getTo() {
		return to;
	}

	public Location getFrom() {
		return from;
	}

	public static boolean isEventDisabled() {
		return !LoaderClass.config.getBoolean("Options.EntityMoveEvent.Enabled");
	}

	public Entity getEntity() {
		return entity;
	}

	public EntityType getEntityType() {
		return entity.getType();
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}