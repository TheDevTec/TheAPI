package me.devtec.shared.commands.structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.devtec.shared.commands.holder.CommandExecutor;

public class ArgumentCommandStructure<S> extends CommandStructure<S> {
	List<String> args = new ArrayList<>();
	int length;
	
	ArgumentCommandStructure(CommandStructure<S> parent, String argument, int length, CommandExecutor<S> ex, String[] aliases) {
		super(parent, ex);
		if(argument != null)
			args.add(argument);
		for(String arg : aliases)
			args.add(arg);
		this.length = length;
	}
	
	public List<String> tabList(S sender) {
		return args.isEmpty() ? Arrays.asList("{text}") : args;
	}

	/**
	 * @apiNote Returns arguments of this {@link ArgumentCommandStructure}
	 */
	public List<String> getArgs(S sender) {
		return args;
	}

	/**
	 * @apiNote Returns maximum length of arguments (-1 means unlimited)
	 */
	public int length() {
		return length;
	}
}
