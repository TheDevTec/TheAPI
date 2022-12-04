package me.devtec.theapi.velocity;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.plugin.PluginClassLoader;

import me.devtec.shared.API;
import me.devtec.shared.Ref;
import me.devtec.shared.Ref.ServerType;
import me.devtec.shared.components.AdventureComponentAPI;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.json.Json;
import me.devtec.shared.json.modern.ModernJsonReader;
import me.devtec.shared.json.modern.ModernJsonWriter;
import me.devtec.shared.scheduler.Scheduler;
import me.devtec.shared.utility.LibraryLoader;
import me.devtec.shared.utility.StringUtils;
import me.devtec.shared.utility.StringUtils.ColormaticFactory;
import me.devtec.theapi.velocity.commands.hooker.VelocityCommandManager;
import me.devtec.theapi.velocity.commands.selectors.VelocitySelectorUtils;

@Plugin(id = "theapi", name = "TheAPI", version = "10.6", authors = { "DevTec", "StraikerinaCZ" }, url = "https://www.spigotmc.org/resources/72679/")
public class VelocityLoader {

	private final ProxyServer server;
	private static VelocityLoader plugin;

	public static ProxyServer getServer() {
		return VelocityLoader.plugin.server;
	}

	@Inject
	public VelocityLoader(ProxyServer server) {
		VelocityLoader.plugin = this;
		this.server = server;
		VelocityLoader.initTheAPI(server);
	}

	@Subscribe
	public void onProxyInitialization(ProxyShutdownEvent event) {
		API.setEnabled(false);
		Scheduler.cancelAll();

		// OfflineCache support!
		API.offlineCache().saveToConfig().setFile(new File("plugins/TheAPI/Cache.dat")).save();
	}

	@Subscribe
	public void onPreLoginEvent(PreLoginEvent e) {
		API.offlineCache().setLookup(API.offlineCache().lookupId(e.getUsername()), e.getUsername());
	}

	@Subscribe
	public void onLoginEvent(LoginEvent e) { // fix uuid - premium login?
		API.offlineCache().setLookup(e.getPlayer().getUniqueId(), e.getPlayer().getUsername());
	}

	@Subscribe
	public void onDisconnect(DisconnectEvent e) {
		Config cache = API.removeCache(e.getPlayer().getUniqueId());
		if (cache != null)
			cache.save();
	}

	public static void initTheAPI(ProxyServer server) {

		Ref.init(ServerType.VELOCITY, server.getVersion().getVersion()); // Server version
		ComponentAPI.registerTransformer("ADVENTURE", new AdventureComponentAPI<>());
		Json.init(new ModernJsonReader(), new ModernJsonWriter()); // Modern version of Guava
		// Commands api
		API.commandsRegister = new VelocityCommandManager();
		API.selectorUtils = new VelocitySelectorUtils();

		// OfflineCache support!
		API.initOfflineCache(server.getConfiguration().isOnlineMode(), new Config("plugins/TheAPI/Cache.dat"));

		API.library = new LibraryLoader() {
			List<File> loaded = new ArrayList<>();

			@SuppressWarnings("resource")
			@Override
			public void load(File file) {
				if (isLoaded(file) || !file.exists())
					return;
				loaded.add(file);
				try {
					new PluginClassLoader(new URL[] { file.toURI().toURL() }).addToClassloaders();
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
		StringUtils.rainbowSplit = Pattern.compile("(&?#[A-Fa-f0-9]{6}([&§][K-Ok-oRr])*|[&§][Xx]([&§][A-Fa-f0-9]){6}([&§][K-Ok-oRr])*|[&§][A-Fa-f0-9K-ORrk-oUuXx]([&§][K-Ok-oRr])*)");
		StringUtils.color = new ColormaticFactory() {
			// Defaults
		};
	}
}
