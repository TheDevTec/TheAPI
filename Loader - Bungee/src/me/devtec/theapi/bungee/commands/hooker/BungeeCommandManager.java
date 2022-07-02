package me.devtec.theapi.bungee.commands.hooker;

import me.devtec.shared.commands.holder.CommandHolder;
import me.devtec.shared.commands.manager.CommandsRegister;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.command.PlayerCommand;

public class BungeeCommandManager implements CommandsRegister {
	private Plugin plugin;

	public BungeeCommandManager(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void register(CommandHolder<?> commandHolder, String command, String[] aliases) {
		PlayerCommand cmd = new PlayerCommand(command, null, aliases) {

			@Override
			public void execute(CommandSender s, String[] args) {
				commandHolder.execute(s, args);
			}

			@Override
			public boolean hasPermission(CommandSender sender) {
				return commandHolder.getStructure().getPermission() == null ? true
						: sender.hasPermission(commandHolder.getStructure().getPermission());
			}

			@Override
			public Iterable<String> onTabComplete(CommandSender s, String[] args) {
				return commandHolder.tablist(s, args);
			}
		};
		ProxyServer.getInstance().getPluginManager().registerCommand(this.plugin, cmd);
	}
}
