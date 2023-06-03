package me.devtec.theapi.bungee;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.tools.ToolProvider;

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
import me.devtec.shared.utility.ColorUtils;
import me.devtec.shared.utility.ColorUtils.ColormaticFactory;
import me.devtec.shared.utility.LibraryLoader;
import me.devtec.theapi.bungee.commands.hooker.BungeeCommandManager;
import me.devtec.theapi.bungee.commands.selectors.BungeeSelectorUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.event.EventHandler;

public class BungeeLoader extends Plugin implements Listener {

	// Init static APIs
	static {
		BungeeLoader.initTheAPI();
	}

	public static Plugin plugin;

	@Override
	public void onLoad() {
		plugin = this;
		getProxy().getPluginManager().registerListener(this, this);
		broadcastSystemInfo();
	}

	private void broadcastSystemInfo() {
		CommandSender console = ProxyServer.getInstance().getConsole();
		console.sendMessage((BaseComponent) ComponentAPI.bungee().fromString(ColorUtils.colorize("&7>")));
		console.sendMessage((BaseComponent) ComponentAPI.bungee().fromString(ColorUtils.colorize("&7> &5TheAPI &dv" + getDescription().getVersion())));
		console.sendMessage((BaseComponent) ComponentAPI.bungee().fromString(ColorUtils.colorize("&7>")));
		console.sendMessage((BaseComponent) ComponentAPI.bungee().fromString(ColorUtils.colorize("&7> &5System info&7:")));
		console.sendMessage((BaseComponent) ComponentAPI.bungee()
				.fromString(ColorUtils.colorize("&7> &dJava&7: &e" + System.getProperty("java.version") + " &7(" + (ToolProvider.getSystemJavaCompiler() != null ? "&aJDK" : "&aJRE") + "&7)")));
		console.sendMessage((BaseComponent) ComponentAPI.bungee().fromString(ColorUtils.colorize("&7> &dServer type&7: &e" + Ref.serverType())));
		console.sendMessage((BaseComponent) ComponentAPI.bungee().fromString(ColorUtils.colorize("&7>")));
		console.sendMessage((BaseComponent) ComponentAPI.bungee().fromString(ColorUtils.colorize("&7> &dSupport&7: &ehttps://discord.gg/pZsDpKXFDf")));
		console.sendMessage((BaseComponent) ComponentAPI.bungee().fromString(ColorUtils.colorize("&7>")));
		if (Json.reader().toString().equals("CustomJsonReader") || Json.writer().toString().equals("CustomJsonWriter")) {
			console.sendMessage((BaseComponent) ComponentAPI.bungee().fromString(ColorUtils.colorize("&7>")));
			console.sendMessage((BaseComponent) ComponentAPI.bungee().fromString(ColorUtils.colorize("&7> &cWarning! &eUsing experimental Json reader & writer.")));
		}
	}

	@Override
	public void onDisable() {
		API.setEnabled(false);

		// OfflineCache support!
		API.offlineCache().saveToConfig().setFile(new File("plugins/TheAPI/Cache.dat")).save("properties");
	}

	@EventHandler
	public void onPreLoginEvent(PreLoginEvent e) {
		if (!e.isCancelled())
			API.offlineCache().setLookup(API.offlineCache().lookupId(e.getConnection().getName()), e.getConnection().getName());
	}

	@EventHandler
	public void onLoginEvent(LoginEvent e) { // fix uuid - premium login?
		if (!e.isCancelled())
			API.offlineCache().setLookup(e.getConnection().getUniqueId(), e.getConnection().getName());
	}

	@EventHandler
	public void onDisconnect(PlayerDisconnectEvent e) {
		API.removeCache(e.getPlayer().getUniqueId());
	}

	public static void initTheAPI() {
		Ref.init(ServerType.BUNGEECORD, ProxyServer.getInstance().getVersion()); // Server version
		ComponentAPI.registerTransformer("BUNGEECORD", new BungeeComponentAPI<>());
		if (Ref.getClass("net.kyori.adventure.text.Component") != null)
			ComponentAPI.registerTransformer("ADVENTURE", (ComponentTransformer<?>) Ref.newInstanceByClass(Ref.getClass("me.devtec.shared.components.AdventureComponentAPI")));
		Config config = new Config("plugins/TheAPI/config.yml");
		if (!config.getString("default-json-handler", "Guava").equalsIgnoreCase("TheAPI"))
			Json.init(new ModernJsonReader(), new ModernJsonWriter()); // Modern version of Guava

		// Commands api
		API.commandsRegister = new BungeeCommandManager();
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
		ColorUtils.rainbowSplit = Pattern.compile("(#[A-Fa-f0-9]{6}([&§][K-Ok-oRr])*|[&§][Xx]([&§][A-Fa-f0-9]){6}([&§][K-Ok-oRr])*|[&§][A-Fa-f0-9K-ORrk-oUuXx]([&§][K-Ok-oRr])*)");
		ColorUtils.color = new ColormaticFactory() {
			// Defaults
		};
	}
}
