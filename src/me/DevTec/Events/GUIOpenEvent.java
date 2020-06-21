package me.DevTec.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

import me.DevTec.GUI.GUIID;
import me.DevTec.Other.LoaderClass;

public class GUIOpenEvent extends Event implements Cancellable {
	public GUIOpenEvent(Player player, Inventory gui, String title) {
		t = title;
		s = player;
		this.gui = gui;
	}

	private boolean c;
	private String t;
	private Player s;
	private Inventory gui;

	/**
	 * @return is GUI open event cancelled
	 */
	@Override
	public boolean isCancelled() {
		return c;
	}

	/**
	 * Cancel GUI open event
	 */
	@Override
	public void setCancelled(boolean cancel) {
		c = cancel;
	}

	/**
	 * 
	 * @return Title of GUI
	 */
	public String getTitle() {
		return t;
	}

	/**
	 * 
	 * @return Player
	 */
	public Player getPlayer() {
		return s;
	}

	/**
	 * 
	 * @return ID of GUI
	 */
	public String getID() {
		String id = null;
	for(GUIID d : LoaderClass.plugin.gui)
		if(d.getPlayers().contains(s)) {
			id=d.getID();
			break;
		}
		return id;
	}

	/**
	 * 
	 * @return Opening GUI
	 */
	public Inventory getGUI() {
		return gui;
	}

	private static final HandlerList cs = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return cs;
	}

	public static HandlerList getHandlerList() {
		return cs;
	}
}
