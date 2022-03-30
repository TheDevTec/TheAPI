package me.devtec.theapi.bungee.commands;

import java.util.Map;

import net.md_5.bungee.api.CommandSender;

public interface CommandTask {
	public void process(CommandSender sender, Map<Integer, String> selectors);
}
