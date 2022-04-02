package me.devtec.theapi.bukkit.commands.hooker;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.devtec.shared.Ref;
import me.devtec.shared.commands.holder.CommandHolder;
import me.devtec.shared.commands.manager.CommandsRegister;
import me.devtec.theapi.bukkit.BukkitLoader;

public class BukkitCommandManager implements CommandsRegister {
	protected static CommandMap cmdMap = (CommandMap)Ref.get(Bukkit.getPluginManager(), "commandMap");
	@SuppressWarnings("unchecked")
	protected static Map<String, Command> knownCommands = (Map<String, Command>) Ref.get(cmdMap, "knownCommands");
	
	private static final Constructor<?> constructor = Ref.constructor(PluginCommand.class, String.class, Plugin.class);
	
	public static PluginCommand createCommand(String name, Plugin plugin) {
		return (PluginCommand) Ref.newInstance(constructor, name, plugin);
	}

	public static void registerCommand(PluginCommand command) {
		String label = command.getName().toLowerCase(Locale.ENGLISH).trim();
		String sd = command.getPlugin().getName().toLowerCase(Locale.ENGLISH).trim();
		command.setLabel(sd + ":" + label);
		command.register(cmdMap);
		if (command.getTabCompleter() == null) {
			if (command.getExecutor() instanceof TabCompleter) {
				command.setTabCompleter((TabCompleter) command.getExecutor());
			} else
				command.setTabCompleter(new TabCompleter() {
					public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
						return null;
					}
				});
		}
		if (command.getExecutor() == null) {
			if (command.getTabCompleter() instanceof CommandExecutor) {
				command.setExecutor((CommandExecutor) command.getTabCompleter());
			} else
				return; // exectutor can't be null
		}
		List<String> low = new ArrayList<>();
		for (String s : command.getAliases()) {
			s = s.toLowerCase(Locale.ENGLISH).trim();
			low.add(s);
		}
		command.setAliases(low);
		if(command.getPermission()==null)
			command.setPermission("");
		if (!low.contains(label))
			low.add(label);
		for (String s : low)
			knownCommands.put(s, command);
	}

	@Override
	public void register(CommandHolder<?> commandHolder, String command, String[] aliases) {
		PluginCommand cmd = createCommand(command, JavaPlugin.getPlugin(BukkitLoader.class));
		cmd.setAliases(Arrays.asList(aliases));
		cmd.setExecutor(new CommandExecutor() {
			
			@Override
			public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
				commandHolder.execute(s, args);
				return true;
			}
		});
		cmd.setTabCompleter(new TabCompleter() {
			
			@Override
			public List<String> onTabComplete(CommandSender s, Command arg1, String arg2, String[] args) {
				return commandHolder.tablist(s, args);
			}
		});
		cmd.setPermission(commandHolder.getStructure().getPermission());
		registerCommand(cmd);
	}
}
