package me.devtec.theapi.bungee;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import me.devtec.shared.API;
import me.devtec.shared.Ref;
import me.devtec.shared.Ref.ServerType;
import me.devtec.shared.components.BungeeComponentAPI;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.components.ComponentTransformer;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.json.Json;
import me.devtec.shared.json.modern.ModernJsonReader;
import me.devtec.shared.json.modern.ModernJsonWriter;
import me.devtec.shared.scheduler.Scheduler;
import me.devtec.shared.utility.LibraryLoader;
import me.devtec.shared.utility.StringUtils;
import me.devtec.shared.utility.StringUtils.ColormaticFactory;
import me.devtec.theapi.bungee.commands.hooker.BungeeCommandManager;
import me.devtec.theapi.bungee.commands.selectors.BungeeSelectorUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.event.EventHandler;

public class BungeeLoader extends Plugin implements Listener {

	@Override
	public void onLoad() {
		BungeeLoader.initTheAPI(this);
		getProxy().getPluginManager().registerListener(this, this);
	}

	@Override
	public void onDisable() {
		API.setEnabled(false);
		Scheduler.cancelAll();

		// OfflineCache support!
		API.offlineCache().saveToConfig().setFile(new File("plugins/TheAPI/Cache.dat")).save();
	}

	@EventHandler
	public void onPreLoginEvent(PreLoginEvent e) {
		API.offlineCache().setLookup(API.offlineCache().lookupId(e.getConnection().getName()), e.getConnection().getName());
	}

	@EventHandler
	public void onLoginEvent(LoginEvent e) { // fix uuid - premium login?
		API.offlineCache().setLookup(e.getConnection().getUniqueId(), e.getConnection().getName());
	}

	@EventHandler
	public void onDisconnect(PlayerDisconnectEvent e) {
		Config cache = API.removeCache(e.getPlayer().getUniqueId());
		if (cache != null)
			cache.save();
	}

	public static void initTheAPI(Plugin plugin) {

		Ref.init(ServerType.BUNGEECORD, ProxyServer.getInstance().getVersion()); // Server version
		ComponentAPI.registerTransformer("BUNGEECORD", new BungeeComponentAPI<>());
		if (Ref.getClass("net.kyori.adventure.text.Component") != null)
			ComponentAPI.registerTransformer("ADVENTURE", (ComponentTransformer<?>) Ref.newInstanceByClass(Ref.getClass("me.devtec.shared.components.AdventureComponentAPI")));
		Json.init(new ModernJsonReader(), new ModernJsonWriter()); // Modern version of Guava

		// Commands api
		API.commandsRegister = new BungeeCommandManager(plugin);
		API.selectorUtils = new BungeeSelectorUtils();

		// OfflineCache support!
		API.initOfflineCache(ProxyServer.getInstance().getConfig().isOnlineMode(), new Config("plugins/TheAPI/Cache.dat"));

		API.library = new LibraryLoader() {
			List<File> loaded = new ArrayList<>();
			Constructor<?> c = Ref.constructor(Ref.getClass("net.md_5.bungee.api.plugin.PluginClassloader"), ProxyServer.class, PluginDescription.class, URL[].class);

			@Override
			public void load(File file) {
				if (isLoaded(file) || !file.exists())
					return;
				loaded.add(file);
				try {
					Ref.newInstance(c, null, null, new URL[] { file.toURI().toURL() });
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}

			@Override
			public boolean isLoaded(File file) {
				return loaded.contains(file);
			}
		};
		API.basics().load();
		StringUtils.rainbowSplit = Pattern.compile("(#[A-Fa-f0-9]{6}([&§][K-Ok-oRr])*|[&§][Xx]([&§][A-Fa-f0-9]){6}([&§][K-Ok-oRr])*|[&§][A-Fa-f0-9K-ORrk-oUuXx]([&§][K-Ok-oRr])*)");
		StringUtils.color = new ColormaticFactory() {
			// Defaults
		};
	}
}
