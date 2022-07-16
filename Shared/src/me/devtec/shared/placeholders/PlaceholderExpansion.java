package me.devtec.shared.placeholders;

import java.util.UUID;

public abstract class PlaceholderExpansion {
	private final String name;

	public PlaceholderExpansion(String name) {
		this.name = name;
	}

	public final String getName() {
		return name;
	}

	public PlaceholderExpansion register() {
		PlaceholderAPI.register(this);
		return this;
	}

	public PlaceholderExpansion unregister() {
		PlaceholderAPI.unregister(this);
		return this;
	}

	public boolean isRegistered() {
		return PlaceholderAPI.isRegistered(getName());
	}

	// Nullable return value
	public abstract String apply(String text, UUID player);

	@Override
	public String toString() {
		return "PlaceholderExpansion[" + getName() + "]";
	}
}
