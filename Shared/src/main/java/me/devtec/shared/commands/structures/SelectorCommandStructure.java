package me.devtec.shared.commands.structures;

import me.devtec.shared.API;
import me.devtec.shared.commands.holder.CommandExecutor;
import me.devtec.shared.commands.selectors.Selector;

import java.util.List;

public class SelectorCommandStructure<S> extends CommandStructure<S> {
	Selector selector;

	SelectorCommandStructure(CommandStructure<S> parent, Selector selector, CommandExecutor<S> ex) {
		super(parent, ex);
		this.selector = selector;
	}

	public List<String> tabList(S sender, CommandStructure<S> structure, String[] arguments) {
		return API.selectorUtils.build(this.selector);
	}

	/**
	 * @apiNote Returns selector of this {@link SelectorCommandStructure}
	 */
	public Selector getSelector() {
		return this.selector;
	}
}
