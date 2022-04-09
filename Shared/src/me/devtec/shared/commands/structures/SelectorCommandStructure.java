package me.devtec.shared.commands.structures;

import java.util.List;

import me.devtec.shared.API;
import me.devtec.shared.commands.holder.CommandExecutor;
import me.devtec.shared.commands.selectors.Selector;

public class SelectorCommandStructure<S> extends CommandStructure<S> {
	Selector selector;
	SelectorCommandStructure(CommandStructure<S> parent, Selector selector, CommandExecutor<S> ex) {
		super(parent, ex);
		this.selector = selector;
	}
	
	public List<String> tabList() {
		return API.selectorUtils.build(selector);
	}
	
	/**
	 * @apiNote Returns selector of this {@link SelectorCommandStructure}
	 */
	public Selector getSelector() {
		return selector;
	}
}
