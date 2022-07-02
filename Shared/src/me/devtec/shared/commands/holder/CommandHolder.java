package me.devtec.shared.commands.holder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.devtec.shared.API;
import me.devtec.shared.commands.structures.ArgumentCommandStructure;
import me.devtec.shared.commands.structures.CallableArgumentCommandStructure;
import me.devtec.shared.commands.structures.CommandStructure;
import me.devtec.shared.utility.StringUtils;

public class CommandHolder<S> {
	private CommandStructure<S> structure;

	public CommandHolder(CommandStructure<S> structure) {
		this.structure = structure;
	}

	public List<String> tablist(Object obj, String[] args) {
		if (!this.structure.getSenderClass().isAssignableFrom(obj.getClass()))
			return Collections.emptyList();
		@SuppressWarnings("unchecked")
		S s = (S) obj;
		int pos = 0;
		CommandStructure<S> cmd = this.structure;
		int argPos = 0;
		for (String arg : args) {
			++argPos;
			CommandStructure<S> next = cmd.findStructure(s, arg, args, true);
			if (next == null)
				return pos == args.length - 1 || this.maybeArgs(s, cmd, args, args.length - argPos) ? StringUtils
						.copyPartialMatches(args[args.length - 1], this.toList(s, cmd.getNextStructures(s)))
						: Collections.emptyList();
			cmd = next;
			++pos;
		}
		return StringUtils.copyPartialMatches(args[args.length - 1],
				this.toList(s, cmd.getParent().getNextStructures(s)));
	}

	private List<String> toList(S sender, List<CommandStructure<S>> nextStructures) {
		List<String> args = new ArrayList<>();
		for (CommandStructure<S> structure : nextStructures)
			args.addAll(structure.tabList(sender));
		return args;
	}

	public void execute(Object obj, String[] args) {
		if (!this.structure.getSenderClass().isAssignableFrom(obj.getClass()))
			return;
		@SuppressWarnings("unchecked")
		S s = (S) obj;
		CommandStructure<S> cmd = this.structure;
		int pos = 0;
		for (String arg : args) {
			++pos;
			CommandStructure<S> next = cmd.findStructure(s, arg, args, false);
			if (next == null && cmd.getFallback() != null) {
				cmd.getFallback().execute(s, cmd, args);
				return;
			}
			if (next == null && this.maybeArgs(s, cmd, args, args.length - pos))
				break;
			if (next != null)
				cmd = next;
		}
		cmd.getExecutor().execute(s, cmd, args);
	}

	public void register(String command, String... aliases) {
		API.commandsRegister.register(this, command, aliases);
	}

	public CommandStructure<S> getStructure() {
		return this.structure;
	}

	private boolean maybeArgs(S sender, CommandStructure<S> cmd, String[] args, int i) {
		if (cmd instanceof CallableArgumentCommandStructure)
			return !((CallableArgumentCommandStructure<S>) cmd).getArgs(sender, cmd, args).isEmpty() && i == 0;
		if (cmd instanceof ArgumentCommandStructure && !(cmd instanceof CallableArgumentCommandStructure))
			return ((ArgumentCommandStructure<S>) cmd).getArgs(sender, cmd, args).isEmpty()
					&& (((ArgumentCommandStructure<S>) cmd).length() == -1
							|| ((ArgumentCommandStructure<S>) cmd).length() >= i);
		return false;
	}
}
