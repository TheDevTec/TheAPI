package me.devtec.theapi.bukkit.commands;

import java.util.Map;

import org.bukkit.command.CommandSender;

public interface CommandTask {
	public void process(CommandSender sender, Map<Integer, String> selectors);
}
