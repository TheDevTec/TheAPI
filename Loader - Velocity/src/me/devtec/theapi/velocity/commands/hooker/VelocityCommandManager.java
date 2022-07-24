package me.devtec.theapi.velocity.commands.hooker;

import java.util.List;

import com.velocitypowered.api.command.RawCommand;

import me.devtec.shared.commands.holder.CommandHolder;
import me.devtec.shared.commands.manager.CommandsRegister;
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
				return commandHolder.tablist(invocation.source(), invocation.arguments().split(" "));
			}

			@Override
			public boolean hasPermission(Invocation invocation) {
				return commandHolder.getStructure().getPermission() == null ? true : invocation.source().hasPermission(commandHolder.getStructure().getPermission());
			}
		};
		VelocityLoader.getServer().getCommandManager().register(command, cmd, aliases);
	}
}
