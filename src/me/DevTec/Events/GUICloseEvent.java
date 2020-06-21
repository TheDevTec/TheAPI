package me.DevTec.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

import me.DevTec.GUI.GUIID;
import me.DevTec.Other.LoaderClass;

public class GUICloseEvent extends Event {
	public GUICloseEvent(Player player, Inventory gui, String title) {
		s = player;
		this.gui = gui;
		t = title;
	}

	private Inventory gui;
	private Player s;
	private String t;

	private static final HandlerList cs = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return cs;
	}

	public static HandlerList getHandlerList() {
		return cs;
	}

	/**
	 * 
	 * @return Closing GUI
	 */
	public Inventory getGUI() {
		return gui;
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

}
