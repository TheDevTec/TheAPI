package me.devtec.theapi.bukkit.gui.expansion;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import me.devtec.shared.annotations.Nonnull;
import me.devtec.shared.annotations.Nullable;
import me.devtec.shared.dataholder.Config;
import me.devtec.theapi.bukkit.gui.HolderGUI;
import me.devtec.theapi.bukkit.gui.expansion.actions.Action;
import me.devtec.theapi.bukkit.gui.expansion.actions.EventType;
import me.devtec.theapi.bukkit.gui.expansion.guis.AnvilGuiCreator;
import me.devtec.theapi.bukkit.gui.expansion.guis.ClassicGuiCreator;
import me.devtec.theapi.bukkit.gui.expansion.guis.LoopGuiCreator;

public interface GuiCreator {
	Map<UUID, Config> sharedData = new ConcurrentHashMap<>();

	Map<String, GuiCreator> guis = new HashMap<>();

	static GuiCreator loadFromFile(@Nullable String id, @Nonnull File file) {
		Config config = new Config(file);
		String name = id != null ? id : file.getName().substring(0, file.getName().lastIndexOf('.')==-1?file.getName().length():file.getName().lastIndexOf('.'));
		return loadFromFile(name, config);
	}

	static GuiCreator loadFromFile(@Nonnull String id, @Nonnull Config config) {
		String name = id;
		return "anvil".equalsIgnoreCase(config.getString("type", "NORMAL")) ? new AnvilGuiCreator(name, config)
				: config.exists("loop") ? new LoopGuiCreator(name, config) : new ClassicGuiCreator(name, config);
	}

	@Nonnull
	String getId();

	default GuiCreator register() {
		guis.put(getId(), this);
		return this;
	}

	default GuiCreator unregister() {
		guis.remove(getId());
		return this;
	}

	@Nullable
	Config getConfig();

	void reload();

	HolderGUI open(@Nonnull Player player);

	void updateItem(@Nonnull HolderGUI gui, @Nonnull Player player, char itemId);

	Map<String, List<Action>> getCustomActions();

	Map<EventType, List<Action>> getEventActions();
}
