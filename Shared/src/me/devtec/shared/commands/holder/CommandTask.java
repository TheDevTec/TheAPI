package me.devtec.shared.commands.holder;

import java.util.Map;

public interface CommandTask<SENDER> {
	public void process(SENDER sender, Map<Integer, String> selectors);
}
