package me.devtec.shared.commands.structures;

import java.util.Collections;
import java.util.List;

import me.devtec.shared.commands.holder.CommandExecutor;

public class CallableArgumentCommandStructure<S> extends ArgumentCommandStructure<S> {
	static String[] EMPTY_STRING = {};
	CallableArgument<S> futureArgs;

	CallableArgumentCommandStructure(CommandStructure<S> parent, int length, CommandExecutor<S> ex,
			CallableArgument<S> future) {
		super(parent, null, length, ex, CallableArgumentCommandStructure.EMPTY_STRING);
		this.futureArgs = future;
	}

	@Override
	public List<String> tabList(S sender, CommandStructure<S> structure, String[] arguments) {
		return this.getArgs(sender, structure, arguments);
	}

	/**
	 * @apiNote Returns arguments of this {@link ArgumentCommandStructure}
	 */
	@Override
	public List<String> getArgs(S sender, CommandStructure<S> structure, String[] arguments) {
		try {
			return this.futureArgs.call(sender, structure, arguments);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	public interface CallableArgument<S> {
		public List<String> call(S sender, CommandStructure<S> structure, String[] args);
	}
}
