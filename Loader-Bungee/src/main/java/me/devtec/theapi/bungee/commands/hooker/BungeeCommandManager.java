package me.devtec.theapi.bungee.commands.hooker;

import me.devtec.shared.commands.holder.CommandHolder;
import me.devtec.shared.commands.manager.CommandsRegister;
import me.devtec.theapi.bungee.BungeeLoader;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.command.PlayerCommand;

public class BungeeCommandManager implements CommandsRegister {

	@Override
	public void register(CommandHolder<?> commandHolder, String command, String[] aliases) {
		PlayerCommand cmd = new PlayerCommand(command, null, aliases) {

			@Override
			public void execute(CommandSender s, String[] args) {
				commandHolder.execute(s, args);
			}

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public boolean hasPermission(CommandSender sender) {
				return commandHolder.getStructure().getSenderClass().isAssignableFrom(sender.getClass())
						
						&& (commandHolder.getStructure().getPermission() == null || ((CommandHolder)commandHolder).getStructure().getPermissionChecker().has(sender, commandHolder.getStructure().getPermission(), false));
			}
			
			@Override
			public Iterable<String> onTabComplete(CommandSender s, String[] args) {
				return commandHolder.tablist(s, args);
			}
		};
		commandHolder.setRegisteredCommand(cmd, command, aliases);
		ProxyServer.getInstance().getPluginManager().registerCommand(BungeeLoader.plugin, cmd);
	}

	@Override
	public void unregister(CommandHolder<?> commandHolder) {
		ProxyServer.getInstance().getPluginManager().unregisterCommand((PlayerCommand) commandHolder.getRegisteredCommand());
	}
}
