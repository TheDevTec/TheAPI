package me.devtec.theapi.utils.thapiutils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.FormattedCommandAlias;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.HelpCommand;
import org.bukkit.command.defaults.PluginsCommand;
import org.bukkit.command.defaults.ReloadCommand;
import org.bukkit.command.defaults.TimingsCommand;
import org.bukkit.command.defaults.VersionCommand;
import org.bukkit.util.Java15Compat;
import org.bukkit.util.StringUtil;

import me.devtec.theapi.TheAPI;

public class Old1_8SimpleCommandMap extends SimpleCommandMap {
	  private static final Pattern PATTERN_ON_SPACE = Pattern.compile(" ", 16);
	  
	  private final Server server;
	  
	  public Old1_8SimpleCommandMap(Server server) {
		super(server);
	    this.server = server;
	    setDefaultCommands();
	    TheAPI.cmdMap=this;
	    TheAPI.knownCommands=knownCommands;
	  }
	  
	  private void setDefaultCommands() {
	    register("bukkit", new VersionCommand("version"));
	    register("bukkit", new ReloadCommand("reload"));
	    register("bukkit", new PluginsCommand("plugins"));
	    register("bukkit", new TimingsCommand("timings"));
	  }
	  
	  public void setFallbackCommands() {
	    register("bukkit", new HelpCommand());
	  }
	  
	  public void registerAll(String fallbackPrefix, List<Command> commands) {
	    if (commands != null)
	      for (Command c : commands)
	        register(fallbackPrefix, c);  
	  }
	  
	  public boolean register(String fallbackPrefix, Command command) {
	    return register(command.getName(), fallbackPrefix, command);
	  }
	  
	  public boolean register(String label, String fallbackPrefix, Command command) {
	    label = label.toLowerCase().trim();
	    fallbackPrefix = fallbackPrefix.toLowerCase().trim();
	    boolean registered = register(label, command, false, fallbackPrefix);
	    Iterator<String> iterator = command.getAliases().iterator();
	    while (iterator.hasNext()) {
	      if (!register(iterator.next(), command, true, fallbackPrefix))
	        iterator.remove(); 
	    } 
	    if (!registered)
	      command.setLabel(fallbackPrefix + ":" + label); 
	    command.register(this);
	    return registered;
	  }
	  
	  private synchronized boolean register(String label, Command command, boolean isAlias, String fallbackPrefix) {
	    this.knownCommands.put(fallbackPrefix + ":" + label, command);
	    if ((command instanceof org.bukkit.command.defaults.VanillaCommand || isAlias) && this.knownCommands.containsKey(label))
	      return false; 
	    boolean registered = true;
	    Command conflict = this.knownCommands.get(label);
	    if (conflict != null && conflict.getLabel().equals(label))
	      return false;
	    if (!isAlias)
	      command.setLabel(label); 
	    this.knownCommands.put(label, command);
	    return registered;
	  }
	  
	  @Override
	  public boolean dispatch(CommandSender sender, String commandLine) throws CommandException {
	    String[] args = PATTERN_ON_SPACE.split(commandLine);
	    if (args.length == 0)
	      return false; 
	    String sentCommandLabel = args[0].toLowerCase();
	    Command target = getCommand(sentCommandLabel);
	    if (target == null)
	      return false; 
	    try {
	      target.execute(sender, sentCommandLabel, Java15Compat.Arrays_copyOfRange(args, 1, args.length));
	    } catch (CommandException ex) {
	      throw ex;
	    } catch (Throwable ex) {
	      throw new CommandException("Unhandled exception executing '" + commandLine + "' in " + target, ex);
	    } 
	    return true;
	  }
	  
	  public synchronized void clearCommands() {
	    for (Map.Entry<String, Command> entry : this.knownCommands.entrySet())
	      ((Command)entry.getValue()).unregister(this); 
	    this.knownCommands.clear();
	    setDefaultCommands();
	  }
	  
	  public Command getCommand(String name) {
	    Command target = this.knownCommands.get(name.toLowerCase());
	    return target;
	  }
	  
	  public List<String> tabComplete(CommandSender sender, String cmdLine) {
	    Validate.notNull(sender, "Sender cannot be null");
	    Validate.notNull(cmdLine, "Command line cannot null");
	    int spaceIndex = cmdLine.indexOf(' ');
	    if (spaceIndex == -1) {
	      ArrayList<String> completions = new ArrayList<String>();
	      Map<String, Command> knownCommands = this.knownCommands;
	      String prefix = (sender instanceof org.bukkit.entity.Player) ? "/" : "";
	      for (Map.Entry<String, Command> commandEntry : knownCommands.entrySet()) {
	        Command command = commandEntry.getValue();
	        if (!command.testPermissionSilent(sender))
	          continue; 
	        String name = commandEntry.getKey();
	        if (StringUtil.startsWithIgnoreCase(name, cmdLine))
	          completions.add(String.valueOf(prefix) + name); 
	      } 
	      Collections.sort(completions, String.CASE_INSENSITIVE_ORDER);
	      return completions;
	    } 
	    String commandName = cmdLine.substring(0, spaceIndex);
	    Command target = getCommand(commandName);
	    if (target == null)
	      return null; 
	    if (!target.testPermissionSilent(sender))
	      return null; 
	    String argLine = cmdLine.substring(spaceIndex + 1, cmdLine.length());
	    String[] args = PATTERN_ON_SPACE.split(argLine, -1);
	    try {
	      return target.tabComplete(sender, commandName, args);
	    } catch (CommandException ex) {
	      throw ex;
	    } catch (Throwable ex) {
	      throw new CommandException("Unhandled exception executing tab-completer for '" + cmdLine + "' in " + target, ex);
	    } 
	  }
	  
	  public Collection<Command> getCommands() {
	    return Collections.unmodifiableCollection(this.knownCommands.values());
	  }
	  
	  public void registerServerAliases() {
	    Map<String, String[]> values = this.server.getCommandAliases();
	    for (String alias : values.keySet()) {
	      if (alias.contains(":") || alias.contains(" ")) {
	        this.server.getLogger().warning("Could not register alias " + alias + " because it contains illegal characters");
	        continue;
	      } 
	      String[] commandStrings = values.get(alias);
	      List<String> targets = new ArrayList<String>();
	      StringBuilder bad = new StringBuilder();
	      String[] arrayOfString1;
	      int i;
	      byte b;
	      for (i = (arrayOfString1 = commandStrings).length, b = 0; b < i; ) {
	        String commandString = arrayOfString1[b];
	        String[] commandArgs = commandString.split(" ");
	        Command command = getCommand(commandArgs[0]);
	        if (command == null) {
	          if (bad.length() > 0)
	            bad.append(", "); 
	          bad.append(commandString);
	        } else {
	          targets.add(commandString);
	        } 
	        b++;
	      } 
	      if (bad.length() > 0) {
	        this.server.getLogger().warning("Could not register alias " + alias + " because it contains commands that do not exist: " + bad);
	        continue;
	      } 
	      if (targets.size() > 0) {
	        this.knownCommands.put(alias.toLowerCase(), new FormattedCommandAlias(alias.toLowerCase(), targets.<String>toArray(new String[targets.size()])));
	        continue;
	      } 
	      this.knownCommands.remove(alias.toLowerCase());
	    } 
	  }

	@Override
	public List<String> tabComplete(CommandSender sender, String cmdLine, Location paramLocation)
			throws IllegalArgumentException {
		return tabComplete(sender, cmdLine);
	}
	}