package me.devtec.theapi.bukkit.commands.hooker;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import lombok.Getter;
import me.devtec.shared.commands.holder.CommandHolder;

public class CustomPluginCommand extends Command implements PluginIdentifiableCommand {

	private final Plugin owningPlugin;
	@Getter
	private CommandExecutor executor;
	private TabCompleter completer;
	@Getter
	private final CommandHolder<?> commandHolder;

	protected CustomPluginCommand(String name, Plugin owner, CommandHolder<?> commandHolder) {
		super(name);
		executor = owner;
		owningPlugin = owner;
		usageMessage = "";
		this.commandHolder = commandHolder;
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!owningPlugin.isEnabled())
			throw new CommandException("Cannot execute command '" + commandLabel + "' in plugin "
					+ owningPlugin.getDescription().getFullName() + " - plugin is disabled.");

		if (!testPermission(sender))
			return true;

		boolean success;
		try {
			success = executor.onCommand(sender, this, commandLabel, args);
		} catch (Throwable error) {
			throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in plugin "
					+ owningPlugin.getDescription().getFullName(), error);
		}

		if (!success && !usageMessage.isEmpty())
			for (String line : usageMessage.replace("<command>", commandLabel).split("\n"))
				sender.sendMessage(line);

		return success;
	}

	public void setExecutor(CommandExecutor executor) {
		this.executor = executor == null ? owningPlugin : executor;
	}

	public void setTabCompleter(TabCompleter completer) {
		this.completer = completer;
	}

	public TabCompleter getTabCompleter() {
		return completer;
	}

	@Override
	public Plugin getPlugin() {
		return owningPlugin;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean testPermissionSilent(CommandSender sender) {
		return commandHolder!=null && commandHolder.getStructure().getSenderClass().isAssignableFrom(sender.getClass())
				
				&& (commandHolder.getStructure().getPermission() == null || ((CommandHolder)commandHolder).getStructure().getPermissionChecker().has(sender, commandHolder.getStructure().getPermission(), false));
	}

	private static void checkArgument(boolean expression, Object errorMessage) {
		if (!expression)
			throw new IllegalArgumentException(String.valueOf(errorMessage));
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args)
			throws CommandException, IllegalArgumentException {
		checkArgument(sender != null, "Sender cannot be null");
		checkArgument(args != null, "Arguments cannot be null");
		checkArgument(alias != null, "Alias cannot be null");
		List<String> completions = null;

		try {
			if (completer != null)
				completions = completer.onTabComplete(sender, this, alias, args);
			if (completions == null && executor instanceof TabCompleter)
				completions = ((TabCompleter) executor).onTabComplete(sender, this, alias, args);
		} catch (Throwable error) {
			StringBuilder message = new StringBuilder();
			message.append("Unhandled exception during tab completion for command '/").append(alias).append(' ');
			for (String arg : args)
				message.append(arg).append(' ');
			message.deleteCharAt(message.length() - 1).append("' in plugin ")
					.append(owningPlugin.getDescription().getFullName());
			throw new CommandException(message.toString(), error);
		}
		return completions == null ? super.tabComplete(sender, alias, args) : completions;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(super.toString());
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		stringBuilder.append(", ").append(owningPlugin.getDescription().getFullName()).append(')');
		return stringBuilder.toString();
	}
}