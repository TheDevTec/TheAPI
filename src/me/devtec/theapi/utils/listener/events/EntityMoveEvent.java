package me.devtec.theapi.utils.listener.events;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import me.devtec.theapi.utils.listener.Cancellable;
import me.devtec.theapi.utils.listener.Event;
import me.devtec.theapi.utils.thapiutils.LoaderClass;

public class EntityMoveEvent extends Event implements Cancellable {
	private Entity s;
	private boolean cancel;
	private Location t, f;

	public EntityMoveEvent(Entity p, Location from, Location to) {
		s = p;
		f = from;
		t = to;
	}

	public Location getTo() {
		return t;
	}

	public Location getFrom() {
		return f;
	}

	public boolean isEventDisabled() {
		return !LoaderClass.config.getBoolean("Options.EntityMoveEvent.Enabled");
	}

	public EntityType getEntityType() {
		return s.getType();
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	public Entity getEntity() {
		return s;
	}
}