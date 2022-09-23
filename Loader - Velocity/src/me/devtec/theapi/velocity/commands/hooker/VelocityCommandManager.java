package me.devtec.theapi.velocity.commands.hooker;

import java.util.ArrayList;
import java.util.List;

import com.velocitypowered.api.command.RawCommand;

import me.devtec.shared.commands.holder.CommandHolder;
import me.devtec.shared.commands.manager.CommandsRegister;
import me.devtec.shared.dataholder.StringContainer;
import me.devtec.theapi.velocity.VelocityLoader;

public class VelocityCommandManager implements CommandsRegister {

	@Override
	public void register(CommandHolder<?> commandHolder, String command, String[] aliases) {
		RawCommand cmd = new RawCommand() {

			@Override
			public void execute(Invocation invocation) {
				commandHolder.execute(invocation.source(), invocation.arguments().split(" "));
			}

			@Override
			public List<String> suggest(Invocation invocation) {
				return commandHolder.tablist(invocation.source(), split(invocation.arguments()));
			}

			// Hotfix for velocity tab completer
			private String[] split(String arguments) {
				List<String> strings = new ArrayList<>();
				StringContainer container = new StringContainer(8);
				for (char c : arguments.toCharArray()) {
					if (c == ' ') {
						strings.add(container.length() == 0 ? "" : container.toString());
						container.clear();
						continue;
					}
					container.append(c);
				}
				strings.add("");
				return strings.toArray(new String[0]);
			}

			@Override
			public boolean hasPermission(Invocation invocation) {
				return commandHolder.getStructure().getPermission() == null ? true : invocation.source().hasPermission(commandHolder.getStructure().getPermission());
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
