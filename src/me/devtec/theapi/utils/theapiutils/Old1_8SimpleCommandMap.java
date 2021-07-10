package me.devtec.theapi.utils.theapiutils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.reflections.Ref;

public class Old1_8SimpleCommandMap extends SimpleCommandMap {
	  public Old1_8SimpleCommandMap(Server server, Map<String, Command> map) {
		super(server);
	    TheAPI.cmdMap=this;
	    Ref.set(this, "knownCommands", map);
	    TheAPI.knownCommands=knownCommands;
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
	   knownCommands.put(fallbackPrefix + ":" + label, command);
	    if (isAlias && knownCommands.containsKey(label))
	      return false; 
	    boolean registered = true;
	    Command conflict = knownCommands.get(label);
	    if (conflict != null && conflict.getLabel().equals(label))
	      return false;
	    if (!isAlias)
	      command.setLabel(label); 
	    knownCommands.put(label, command);
	    return registered;
	  }
	  
	  @Override
	  public boolean dispatch(CommandSender sender, String commandLine) throws CommandException {
	    String[] args = commandLine.replace("  ", " ").split(" ");
	    if (args.length == 0)
	      return false; 
	    String sentCommandLabel = args[0].toLowerCase();
	    Command target = getCommand(sentCommandLabel);
	    if (target == null)
	      return false; 
	    try {
	    	target.execute(sender, sentCommandLabel, Arrays.copyOfRange(args, 1, args.length));
	    } catch (CommandException ex) {
	    	throw ex;
	    } catch (Throwable ex) {
	    	throw new CommandException("Unhandled exception executing '" + commandLine + "' in " + target, ex);
	    }
	    return true;
	  }
}