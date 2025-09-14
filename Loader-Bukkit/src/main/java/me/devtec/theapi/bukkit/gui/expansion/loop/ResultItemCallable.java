package me.devtec.theapi.bukkit.gui.expansion.loop;

import java.util.List;

import org.bukkit.entity.Player;

import me.devtec.shared.dataholder.Config;
import me.devtec.theapi.bukkit.gui.ItemGUI;
import me.devtec.theapi.bukkit.gui.expansion.guis.LoopGuiCreator;
import me.devtec.theapi.bukkit.gui.expansion.items.ConditionItem;
import me.devtec.theapi.bukkit.gui.expansion.items.ItemPackage;

public interface ResultItemCallable {

	List<ItemGUI> callLoop(LoopGuiCreator holder, Player player, Config sharedData,
			List<ConditionItem> slotItemWithConditions, ItemPackage defaultSlotItem);

}
