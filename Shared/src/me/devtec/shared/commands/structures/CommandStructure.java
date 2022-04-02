package me.devtec.shared.commands.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.devtec.shared.API;
import me.devtec.shared.commands.holder.CommandExecutor;
import me.devtec.shared.commands.holder.CommandHolder;
import me.devtec.shared.commands.manager.PermissionChecker;
import me.devtec.shared.commands.selectors.Selector;

public class CommandStructure<S> {
	private CommandExecutor<S> executor;
	String permission;
	int priority;
	
	PermissionChecker<S> permissionChecker;
	CommandStructure<S> parent;
	
	Map<Selector, SelectorCommandStructure<S>> selectors = new HashMap<>();
	List<ArgumentCommandStructure<S>> arguments = new ArrayList<>();
	CommandExecutor<S> fallback;
	Class<S> senderClass;
	
	CommandStructure(CommandStructure<S> parent, CommandExecutor<S> executor){
		this.setExecutor(executor);
		this.parent=parent;
	}

	/**
	 * @apiNote Creates new {@link CommandStructure}
	 *
	 */
	public static <T> CommandStructure<T> create(Class<T> executorClass, PermissionChecker<T> perm, CommandExecutor<T> executor) {
		CommandStructure<T> structure = new CommandStructure<T>(null, executor);
		structure.permissionChecker=perm;
		structure.senderClass=executorClass;
		return structure;
	}

	/**
	 * @apiNote Add selector argument to current {@link CommandStructure}
	 *
	 */
	public SelectorCommandStructure<S> selector(Selector selector, CommandExecutor<S> ex) {
		SelectorCommandStructure<S> sub = new SelectorCommandStructure<S>(this, selector, ex);
		selectors.put(sub.selector, sub);
		return sub;
	}

	/**
	 * @apiNote Fallback executor when every try to find selector / argument structure fail
	 *
	 */
	public CommandStructure<S> fallback(CommandExecutor<S> ex) { //Everything failed? Don't worry! This will be executed
		this.fallback=ex;
		return this;
	}

	/**
	 * @apiNote Returns fallback executor
	 *
	 */
	public CommandExecutor<S> getFallback() {
		return fallback;
	}

	/**
	 * @apiNote Returns command executor
	 *
	 */
	public CommandExecutor<S> getExecutor() {
		return executor;
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
		ArgumentCommandStructure<S> sub = new ArgumentCommandStructure<S>(this, argument, ex, aliases);
		arguments.add(sub);
		return sub;
	}
	
	/**
	 * @apiNote Higher number means higher priority in lookup
	 */
	public CommandStructure<S> priority(int level) {
		this.priority=level;
		return this;
	}

	/**
	 * @apiNote Returns priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @apiNote Permission to use this and other sub-commands
	 */
	public CommandStructure<S> permission(String permission) {
		this.permission=permission;
		return this;
	}

	/**
	 * @apiNote Returns permission
	 */
	public String getPermission() {
		return permission;
	}

	/**
	 * @apiNote Returns original {@link CommandStructure}
	 */
	public CommandStructure<S> first() {
		return getParent() == null ? this : getParent().first();
	}

	/**
	 * @apiNote @Nullable Returns parent of this {@link CommandStructure}
	 *
	 */
	public CommandStructure<S> getParent() {
		return parent;
	}

	/**
	 * @apiNote Returns tab completer values of this {@link CommandStructure}
	 *
	 */
	public List<String> tabList() {
		return Collections.emptyList();
	}

	/**
	 * @apiNote Returns executor's class
	 *
	 */
	public Class<S> getSenderClass() {
		return first().senderClass;
	}

	/**
	 * @apiNote Build and convert to {@link CommandHolder}
	 *
	 */
	public CommandHolder<S> build() {
		return new CommandHolder<>(first());
	}
	
	public String toString() {
		return getClass().getCanonicalName()+":"+tabList();
	}
	
	//Special utils to make this structure working!
	
	protected final boolean findAny(Selector key, String arg) {
		return API.selectorUtils.check(key, arg);
	}
	
	public final CommandStructure<S> findStructure(S s, String arg, boolean tablist) {
		CommandStructure<S> result = null;
		for(ArgumentCommandStructure<S> sub : arguments) {
			if(contains(sub.args, arg) && (sub.permission==null ? true : sub.first().permissionChecker.has(s, sub.permission, tablist))) {
				if(result == null || result.priority <= sub.priority)
					result = sub;
			}
		}
		for(Entry<Selector, SelectorCommandStructure<S>> sub : selectors.entrySet()) {
			if(findAny(sub.getKey(), arg) && (sub.getValue().permission==null ? true : sub.getValue().first().permissionChecker.has(s, sub.getValue().permission, tablist))) {
				if(result == null || result.priority <= sub.getValue().priority)
					result = sub.getValue();
			}
		}
		return result == null ? null : result;
	}
	
	public final List<CommandStructure<S>> getNextStructures(S s, boolean tablist) {
		List<CommandStructure<S>> structures = new ArrayList<>();
		for(ArgumentCommandStructure<S> sub : arguments) {
			if(sub.permission==null ? true : sub.first().permissionChecker.has(s, sub.permission, tablist)) {
				structures.add(sub);
			}
		}
		for(SelectorCommandStructure<S> sub : selectors.values()) {
			if(sub.permission==null ? true : sub.first().permissionChecker.has(s, sub.permission, tablist)) {
				structures.add(sub);
			}
		}
		return structures;
	}
	
	protected final boolean contains(List<String> list, String arg) {
		for(String value : list)
			if(value.equalsIgnoreCase(arg))return true;
		return false;
	}
}
