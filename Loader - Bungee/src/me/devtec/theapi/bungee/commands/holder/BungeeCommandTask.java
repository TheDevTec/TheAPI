package me.devtec.theapi.bungee.commands.holder;

import java.util.Map;

import me.devtec.shared.commands.holder.CommandTask;
import net.md_5.bungee.api.CommandSender;

public interface BungeeCommandTask extends CommandTask<CommandSender> {
	public void process(CommandSender sender, Map<Integer, String> selectors);
}
