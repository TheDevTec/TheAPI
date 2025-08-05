package me.devtec.theapi.bukkit.gui.expansion.actions;

import me.devtec.theapi.bukkit.gui.expansion.GuiCreator;

public interface ActionConstructor {
	Action create(GuiCreator holder, String values);
}
