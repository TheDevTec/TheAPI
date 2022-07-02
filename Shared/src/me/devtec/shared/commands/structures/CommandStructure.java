package me.devtec.shared.commands.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.devtec.shared.API;
import me.devtec.shared.commands.holder.CommandExecutor;
import me.devtec.shared.commands.holder.CommandHolder;
import me.devtec.shared.commands.manager.PermissionChecker;
import me.devtec.shared.commands.selectors.Selector;
import me.devtec.shared.commands.structures.CallableArgumentCommandStructure.CallableArgument;

public class CommandStructure<S> {
	private CommandExecutor<S> executor;
	String permission;
	int priority;

	PermissionChecker<S> permissionChecker;
	CommandStructure<S> parent;

	Map<Selector, SelectorCommandStructure<S>> selectors = new ConcurrentHashMap<>();
	List<ArgumentCommandStructure<S>> arguments = new ArrayList<>();
	CommandExecutor<S> fallback;
	Class<S> senderClass;

	CommandStructure(CommandStructure<S> parent, CommandExecutor<S> executor) {
		this.setExecutor(executor);
		this.parent = parent;
	}

	/**
	 * @apiNote Creates new {@link CommandStructure}
	 *
	 */
	public static <T> CommandStructure<T> create(Class<T> executorClass, PermissionChecker<T> perm,
			CommandExecutor<T> executor) {
		CommandStructure<T> structure = new CommandStructure<>(null, executor);
		structure.permissionChecker = perm;
		structure.senderClass = executorClass;
		return structure;
	}

	/**
	 * @apiNote Add selector argument to current {@link CommandStructure}
	 *
	 */
	public SelectorCommandStructure<S> selector(Selector selector, CommandExecutor<S> ex) {
		SelectorCommandStructure<S> sub = new SelectorCommandStructure<>(this, selector, ex);
		this.selectors.put(sub.selector, sub);
		return sub;
	}

	/**
	 * @apiNote Fallback executor when every try to find selector / argument
	 *          structure fail
	 *
	 */
	public CommandStructure<S> fallback(CommandExecutor<S> ex) { // Everything failed? Don't worry! This will be
																	// executed
		this.fallback = ex;
		return this;
	}

	/**
	 * @apiNote Returns fallback executor
	 *
	 */
	public CommandExecutor<S> getFallback() {
		return this.fallback;
	}

	/**
	 * @apiNote Returns command executor
	 *
	 */
	public CommandExecutor<S> getExecutor() {
		return this.executor;
	}

	/**
	 * @apiNote Override current command executor
	 *
	 */
	public CommandStructure<S> setExecutor(CommandExecutor<S> executor) {
		this.executor = executor;
		return this;
	}

	/**
	 * @apiNote Add string/s argument to current {@link CommandStructure}
	 *
	 */
	public ArgumentCommandStructure<S> argument(String argument, CommandExecutor<S> ex, String... aliases) {
		return this.argument(argument, 0, ex, aliases);
	}

	/**
	 * @apiNote Add string/s argument to current {@link CommandStructure}
	 *
	 */
	public ArgumentCommandStructure<S> argument(String argument, int length, CommandExecutor<S> ex, String... aliases) {
		ArgumentCommandStructure<S> sub = new ArgumentCommandStructure<>(this, argument, length, ex, aliases);
		this.arguments.add(sub);
		return sub;
	}

	/**
	 * @apiNote Add string/s argument to current {@link CommandStructure}
	 *
	 */
	public CallableArgumentCommandStructure<S> callableArgument(CallableArgument<S> future, CommandExecutor<S> ex) {
		return this.callableArgument(future, 0, ex);
	}

	/**
	 * @apiNote Add string/s argument to current {@link CommandStructure}
	 *
	 */
	public CallableArgumentCommandStructure<S> callableArgument(CallableArgument<S> future, int length,
			CommandExecutor<S> ex) {
		CallableArgumentCommandStructure<S> sub = new CallableArgumentCommandStructure<>(this, length, ex, future);
		this.arguments.add(sub);
		return sub;
	}

	/**
	 * @apiNote Higher number means higher priority in lookup
	 */
	public CommandStructure<S> priority(int level) {
		this.priority = level;
		return this;
	}

	/**
	 * @apiNote Returns priority
	 */
	public int getPriority() {
		return this.priority;
	}

	/**
	 * @apiNote Permission to use this and other sub-commands
	 */
	public CommandStructure<S> permission(String permission) {
		this.permission = permission;
		return this;
	}

	/**
	 * @apiNote Returns permission
	 */
	public String getPermission() {
		return this.permission;
	}

	/**
	 * @apiNote Returns original {@link CommandStructure}
	 */
	public CommandStructure<S> first() {
		return this.getParent() == null ? this : this.getParent().first();
	}

	/**
	 * @apiNote @Nullable Returns parent of this {@link CommandStructure}
	 *
	 */
	public CommandStructure<S> getParent() {
		return this.parent;
	}

	/**
	 * @apiNote @Nullable Returns parent of this
	 *          {@link CommandStructure#getParent()}
	 *
	 */
	public CommandStructure<S> parent() {
		return this.getParent();
	}

	/**
	 * @apiNote Returns tab completer values of this {@link CommandStructure}
	 *
	 */
	public List<String> tabList(S sender) {
		return Collections.emptyList();
	}

	/**
	 * @apiNote Returns executor's class
	 *
	 */
	public Class<S> getSenderClass() {
		return this.first().senderClass;
	}

	/**
	 * @apiNote Build and convert to {@link CommandHolder}
	 *
	 */
	public CommandHolder<S> build() {
		return new CommandHolder<>(this.first());
	}

	@Override
	public String toString() {
		return this.getClass().getCanonicalName() + ":" + this.tabList(null);
	}

	// Special utils to make this structure working!

	public final CommandStructure<S> findStructure(S s, String arg, String[] args, boolean tablist) {
		CommandStructure<S> result = null;
		for (ArgumentCommandStructure<S> sub : this.arguments)
			if (CommandStructure.contains(sub, sub.getArgs(s, sub, args), arg)
					&& (sub.permission == null ? true : sub.first().permissionChecker.has(s, sub.permission, tablist))
					&& (result == null || result != null && result.priority <= sub.priority))
				result = sub;
		for (SelectorCommandStructure<S> sub : this.selectors.values())
			if (API.selectorUtils.check(sub.getSelector(), arg)
					&& (sub.permission == null ? true : sub.first().permissionChecker.has(s, sub.permission, tablist))
					&& (result == null || result != null && result.priority <= sub.priority))
				result = sub;
		return result == null ? null : result;
	}

	public final List<CommandStructure<S>> getNextStructures(S s) {
		List<CommandStructure<S>> structures = new ArrayList<>();
		for (ArgumentCommandStructure<S> sub : this.arguments)
			if (sub.permission == null ? true : sub.first().permissionChecker.has(s, sub.permission, true))
				structures.add(sub);
		for (SelectorCommandStructure<S> sub : this.selectors.values())
			if (sub.permission == null ? true : sub.first().permissionChecker.has(s, sub.permission, true))
				structures.add(sub);
		return structures;
	}

	private static boolean contains(ArgumentCommandStructure<?> sub, List<String> list, String arg) {
		if (!(sub instanceof CallableArgumentCommandStructure) && list.isEmpty())
			return true;
		for (String value : list)
			if (value.equalsIgnoreCase(arg))
				return true;
		return false;
	}
}
