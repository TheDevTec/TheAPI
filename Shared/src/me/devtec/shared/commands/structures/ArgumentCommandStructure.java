package me.devtec.shared.commands.structures;

import java.util.ArrayList;
import java.util.List;

import me.devtec.shared.commands.holder.CommandExecutor;

public class ArgumentCommandStructure<S> extends CommandStructure<S> {
	List<String> args = new ArrayList<>();
	ArgumentCommandStructure(CommandStructure<S> parent, String argument, CommandExecutor<S> ex, String[] aliases) {
		super(parent, ex);
		args.add(argument);
		for(String arg : aliases)
			args.add(arg);
	}
	
	public List<String> tabList() {
		return args;
	}
}
