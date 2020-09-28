package me.DevTec.TheAPI.Utils.Listener;

public interface Cancellable {
	public boolean isCancelled();
	
	public void setCancelled(boolean cancel);
}
