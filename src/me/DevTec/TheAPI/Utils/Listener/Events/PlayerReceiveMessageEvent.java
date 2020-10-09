package me.DevTec.TheAPI.Utils.Listener.Events;

import org.bukkit.entity.Player;

import me.DevTec.TheAPI.Utils.Listener.Cancellable;
import me.DevTec.TheAPI.Utils.Listener.Event;

public class PlayerReceiveMessageEvent extends Event implements Cancellable {
	private final Player a;
	private String b;
	private boolean c;
	public PlayerReceiveMessageEvent(Player player, String message) {
		a=player;
		b=message;
	}
	
	public Player getPlayer() {
		return a;
	}
	
	public String getMessage() {
		return b;
	}
	
	public void setMessage(String newValue) {
		b=newValue;
	}

	public boolean isCancelled() {
		return c;
	}

	@Override
	public void setCancelled(boolean cancel) {
		c=cancel;
	}
}
