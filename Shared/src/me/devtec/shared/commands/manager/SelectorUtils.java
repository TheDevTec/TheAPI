package me.devtec.shared.commands.manager;

import java.util.List;

import me.devtec.shared.commands.selectors.Selector;

public interface SelectorUtils {
	public List<String> build(Selector selector);

	public boolean check(Selector selector, String value);

}
