package me.devtec.shared.commands.manager;

import me.devtec.shared.commands.selectors.Selector;

import java.util.List;

public interface SelectorUtils {
	public List<String> build(Selector selector);

	public boolean check(Selector selector, String value);

}
