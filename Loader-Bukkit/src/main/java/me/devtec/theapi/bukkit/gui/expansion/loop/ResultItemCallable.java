package me.devtec.theapi.bukkit.gui.expansion.loop;

import java.util.List;

import org.bukkit.entity.Player;

import me.devtec.theapi.bukkit.gui.expansion.guis.LoopGuiCreator;
import me.devtec.theapi.bukkit.gui.expansion.items.ConditionItem;
import me.devtec.theapi.bukkit.gui.expansion.items.ItemPackage;
import me.devtec.theapi.bukkit.gui.ItemGUI;

public interface ResultItemCallable {

	List<ItemGUI> callLoop(LoopGuiCreator holder, Player player, List<ConditionItem> slotItemWithConditions,
			ItemPackage defaultSlotItem);

}
