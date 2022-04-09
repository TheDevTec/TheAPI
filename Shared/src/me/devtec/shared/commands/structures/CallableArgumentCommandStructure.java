package me.devtec.shared.commands.structures;

import java.util.Collections;
import java.util.List;

import me.devtec.shared.commands.holder.CommandExecutor;

public class CallableArgumentCommandStructure<S> extends ArgumentCommandStructure<S> {
	static String[] EMPTY_STRING = {};
	CallableArgument<S> futureArgs;
	
	CallableArgumentCommandStructure(CommandStructure<S> parent, int length, CommandExecutor<S> ex, CallableArgument<S> future) {
		super(parent, null, length, ex, EMPTY_STRING);
		futureArgs = future;
	}
	
	public List<String> tabList(S sender) {
		return getArgs(sender);
	}

	/**
	 * @apiNote Returns arguments of this {@link ArgumentCommandStructure}
	 */
	public List<String> getArgs(S sender) {
		try {
			return futureArgs.call(sender);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
	
	public interface CallableArgument<S> {
		public List<String> call(S sender);
	}
}
