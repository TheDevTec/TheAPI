package me.devtec.theapi.bukkit.commands.holder;

import java.util.Map;

import org.bukkit.command.CommandSender;

import me.devtec.shared.commands.holder.CommandTask;

public interface BukkitCommandTask extends CommandTask<CommandSender> {
	public void process(CommandSender sender, Map<Integer, String> selectors);
}
