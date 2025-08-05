package me.devtec.theapi.bukkit.gui.expansion.actions;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import me.devtec.shared.scheduler.Tasker;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.gui.HolderGUI;

public interface Action {

	default boolean shouldSync() {
		return false;
	}

	void run(HolderGUI gui, Player player, Map<String, Object> placeholders);

	default void runSync(int pos, List<Action> actions, HolderGUI gui, Player player,
			Map<String, Object> placeholders) {
		BukkitLoader.getNmsProvider().postToMainThread(() -> {
			run(gui, player, placeholders);
			new Tasker() {

				@Override
				public void run() {
					for (int i = pos; i < actions.size(); ++i) {
						Action action = actions.get(i);
						if (action.shouldSync()) {
							actions.get(i).runSync(i + 1, actions, gui, player, placeholders);
							break;
						}
						actions.get(i).run(gui, player, placeholders);
					}
				}
			}.runTask();
		});
	}

}
