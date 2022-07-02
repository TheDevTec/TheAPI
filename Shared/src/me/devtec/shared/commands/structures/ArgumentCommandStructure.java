package me.devtec.shared.commands.structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.devtec.shared.commands.holder.CommandExecutor;

public class ArgumentCommandStructure<S> extends CommandStructure<S> {
	List<String> args = new ArrayList<>();
	int length;

	ArgumentCommandStructure(CommandStructure<S> parent, String argument, int length, CommandExecutor<S> ex,
			String[] aliases) {
		super(parent, ex);
		if (argument != null)
			this.args.add(argument);
		Collections.addAll(this.args, aliases);
		this.length = length;
	}

	public List<String> tabList(S sender, CommandStructure<S> structure, String[] arguments) {
		return this.args.isEmpty() ? Arrays.asList("<args>") : this.args;
	}

	/**
	 * @apiNote Returns arguments of this {@link ArgumentCommandStructure}
	 */
	public List<String> getArgs(S sender, CommandStructure<S> structure, String[] arguments) {
		return this.args;
	}

	/**
	 * @apiNote Returns maximum length of arguments (-1 means unlimited)
	 */
	public int length() {
		return this.length;
	}
}
