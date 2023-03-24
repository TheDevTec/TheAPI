package me.devtec.theapi.velocity;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.tools.ToolProvider;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.VelocityServer;
import com.velocitypowered.proxy.config.VelocityConfiguration;
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
import me.devtec.shared.utility.ColorUtils;
import me.devtec.shared.utility.ColorUtils.ColormaticFactory;
import me.devtec.shared.utility.LibraryLoader;
import me.devtec.theapi.velocity.commands.hooker.VelocityCommandManager;
import me.devtec.theapi.velocity.commands.selectors.VelocitySelectorUtils;
import net.kyori.adventure.text.Component;

@Plugin(id = "theapi", name = "TheAPI", version = "10.9", authors = { "DevTec", "StraikerinaCZ" }, url = "https://www.spigotmc.org/resources/72679/")
public class VelocityLoader {

	// Init static APIs
	static {
		VelocityLoader.initTheAPI();
	}

	private final ProxyServer server;
	private static VelocityLoader plugin;

	public static ProxyServer getServer() {
		return VelocityLoader.plugin.server;
	}

	@Inject
	public VelocityLoader(ProxyServer server) {
		VelocityLoader.plugin = this;
		this.server = server;
		broadcastSystemInfo();
	}

	private void broadcastSystemInfo() {
		ConsoleCommandSource console = server.getConsoleCommandSource();
		console.sendMessage((Component) ComponentAPI.adventure().fromString(ColorUtils.colorize("&7>")));
		console.sendMessage((Component) ComponentAPI.adventure().fromString(ColorUtils.colorize("&7> &5TheAPI &dv" + VelocityLoader.class.getAnnotation(Plugin.class).version())));
		console.sendMessage((Component) ComponentAPI.adventure().fromString(ColorUtils.colorize("&7>")));
		console.sendMessage((Component) ComponentAPI.adventure().fromString(ColorUtils.colorize("&7> &5System info&7:")));
		console.sendMessage((Component) ComponentAPI.adventure()
				.fromString(ColorUtils.colorize("&7> &dJava&7: &e" + System.getProperty("java.version") + " &7(" + (ToolProvider.getSystemJavaCompiler() != null ? "&aJDK" : "&aJRE") + "&7)")));
		console.sendMessage((Component) ComponentAPI.adventure().fromString(ColorUtils.colorize("&7> &dServer type&7: &e" + Ref.serverType())));
		console.sendMessage((Component) ComponentAPI.adventure().fromString(ColorUtils.colorize("&7>")));
		console.sendMessage((Component) ComponentAPI.adventure().fromString(ColorUtils.colorize("&7> &dSupport&7: &ehttps://discord.gg/pZsDpKXFDf")));
		console.sendMessage((Component) ComponentAPI.adventure().fromString(ColorUtils.colorize("&7>")));
	}

	@Subscribe
	public void onProxyInitialization(ProxyShutdownEvent event) {
		API.setEnabled(false);

		// OfflineCache support!
		API.offlineCache().saveToConfig().setFile(new File("plugins/TheAPI/Cache.dat")).save("properties");
	}

	@Subscribe
	public void onPreLoginEvent(PreLoginEvent e) {
		if (e.getResult().isAllowed())
			API.offlineCache().setLookup(API.offlineCache().lookupId(e.getUsername()), e.getUsername());
	}

	@Subscribe
	public void onLoginEvent(LoginEvent e) { // fix uuid - premium login?
		if (e.getResult().isAllowed())
			API.offlineCache().setLookup(e.getPlayer().getUniqueId(), e.getPlayer().getUsername());
	}

	@Subscribe
	public void onDisconnect(DisconnectEvent e) {
		API.removeCache(e.getPlayer().getUniqueId());
	}

	public static void initTheAPI() {
		Ref.init(ServerType.VELOCITY, VelocityServer.class.getPackage().getImplementationVersion()); // Server version
		ComponentAPI.registerTransformer("ADVENTURE", new AdventureComponentAPI<>());
		Json.init(new ModernJsonReader(), new ModernJsonWriter()); // Modern version of Guava
		// Commands api
		API.commandsRegister = new VelocityCommandManager();
		API.selectorUtils = new VelocitySelectorUtils();

		// OfflineCache support!
		Path configPath = new File("velocity.toml").toPath();
		try {
			VelocityConfiguration configuration = VelocityConfiguration.read(configPath);
			API.initOfflineCache(configuration.isOnlineMode(), new Config("plugins/TheAPI/Cache.dat"));
		} catch (IOException e1) {
			API.initOfflineCache(false, new Config("plugins/TheAPI/Cache.dat"));
		}

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
		ColorUtils.rainbowSplit = Pattern.compile("(&?#[A-Fa-f0-9]{6}([&§][K-Ok-oRr])*|[&§][Xx]([&§][A-Fa-f0-9]){6}([&§][K-Ok-oRr])*|[&§][A-Fa-f0-9K-ORrk-oUuXx]([&§][K-Ok-oRr])*)");
		ColorUtils.color = new ColormaticFactory() {
			// Defaults
		};
	}
}
