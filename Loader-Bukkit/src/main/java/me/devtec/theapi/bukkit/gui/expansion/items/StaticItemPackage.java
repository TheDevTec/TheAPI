package me.devtec.theapi.bukkit.gui.expansion.items;

import java.util.List;

import me.devtec.theapi.bukkit.gui.expansion.actions.Action;
import me.devtec.theapi.bukkit.game.ItemMaker;
import me.devtec.theapi.bukkit.gui.ItemGUI;

public class StaticItemPackage extends ItemPackage {
	private final ItemGUI itemGui;

	public StaticItemPackage(String typePlaceholder, ItemGUI itemGui, ItemMaker item, int slot, List<Action> actions) {
		super(typePlaceholder, item, slot, actions);
		this.itemGui = itemGui;
	}

	public ItemGUI getItemGui() {
		return itemGui;
	}
}
