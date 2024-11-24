package me.devtec.theapi.velocity.commands.hooker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.velocitypowered.api.command.SimpleCommand;

import me.devtec.shared.commands.holder.CommandHolder;
import me.devtec.shared.commands.manager.CommandsRegister;
import me.devtec.theapi.velocity.VelocityLoader;

public class VelocityCommandManager implements CommandsRegister {

	@Override
	public void register(CommandHolder<?> commandHolder, String command, String[] aliases) {
		SimpleCommand cmd = new SimpleCommand() {

			@Override
			public void execute(Invocation invocation) {
				commandHolder.execute(invocation.source(), invocation.arguments());
			}

			@Override
			public List<String> suggest(Invocation invocation) {
				Collection<String> tablist = commandHolder.tablist(invocation.source(), invocation.arguments());
				if (tablist.isEmpty())
					return Collections.emptyList();
				return tablist instanceof List ? (List<String>) tablist : new ArrayList<>(tablist);
			}

			@Override
			public boolean hasPermission(Invocation invocation) {
				return commandHolder.getStructure().getSenderClass().isAssignableFrom(invocation.source().getClass())
						&& (commandHolder.getStructure().getPermission() == null || invocation.source().hasPermission(commandHolder.getStructure().getPermission()));
			}
		};
		commandHolder.setRegisteredCommand(cmd, command, aliases);
		VelocityLoader.getServer().getCommandManager().register(command, cmd, aliases);
	}

	@Override
	public void unregister(CommandHolder<?> commandHolder) {
		VelocityLoader.getServer().getCommandManager().unregister(commandHolder.getCommandName());
		for (String alias : commandHolder.getCommandAliases())
			VelocityLoader.getServer().getCommandManager().unregister(alias);
	}
}
