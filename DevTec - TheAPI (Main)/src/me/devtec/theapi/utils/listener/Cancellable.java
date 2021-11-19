package me.devtec.theapi.utils.listener;

public interface Cancellable {
	public boolean isCancelled();

	public void setCancelled(boolean cancel);
}
