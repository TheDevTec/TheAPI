package me.devtec.theapi.bukkit.gui.expansion.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import me.devtec.shared.dataholder.Config;
import me.devtec.theapi.bukkit.game.ItemMaker;
import me.devtec.theapi.bukkit.gui.HolderGUI;
import me.devtec.theapi.bukkit.gui.expansion.actions.Action;

public class ItemPackage {
	private final String typePlaceholder;
	private final ItemMaker item;
	private final List<Integer> slots = new ArrayList<>();
	private final List<Action> actions;

	public ItemPackage(String typePlaceholder, ItemMaker item, int slot, List<Action> actions) {
		this.typePlaceholder = typePlaceholder;
		this.item = item;
		this.actions = actions;
		slots.add(slot);
	}

	public String getTypePlaceholder() {
		return typePlaceholder;
	}

	public ItemMaker getItem() {
		return item == null ? null : item.clone();
	}

	public List<Integer> getSlots() {
		return slots;
	}

	public void addSlot(int slot) {
		slots.add(slot);
	}

	public List<Action> getActions() {
		return actions;
	}

	public void runActions(HolderGUI gui, Player player, Config sharedData, Map<String, Object> placeholders) {
		int pos = 0;
		for (Action action : getActions()) {
			if (action.shouldSync()) {
				action.runSync(++pos, getActions(), gui, player, sharedData, placeholders);
				break;
			}
			action.run(gui, player, sharedData, placeholders);
			++pos;
		}
	}
}
