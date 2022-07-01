package me.devtec.shared.events;

public interface Cancellable {
	public boolean isCancelled();

	public void setCancelled(boolean cancel);
}
