package me.devtec.shared.commands.holder;

import java.util.Map;

public interface CommandTask<S> {
	public void process(S sender, Map<Integer, String> selectors);
}
