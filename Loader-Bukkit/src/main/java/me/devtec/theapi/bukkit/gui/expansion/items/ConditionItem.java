package me.devtec.theapi.bukkit.gui.expansion.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import me.devtec.theapi.bukkit.gui.expansion.conditions.Condition;

public class ConditionItem {

	private final List<Integer> slots = new ArrayList<>();
	private final List<Condition> conditions;
	private final ItemPackage has;
	private final ItemPackage not;

	public ConditionItem(List<Condition> conditions, int slot, ItemPackage has, ItemPackage not) {
		this.conditions = conditions;
		this.has = has;
		this.not = not;
		slots.add(slot);
	}

	public ItemPackage test(Player player, Map<String, Object> placeholders) {
		for (Condition condition : conditions)
			if (!condition.has(player, placeholders))
				return not;
		return has;
	}

	public List<Integer> getSlots() {
		return slots;
	}

	public void addSlot(int pos) {
		slots.add(pos);
	}

	public ItemPackage getHas() {
		return has;
	}

	public ItemPackage getNot() {
		return not;
	}

}
