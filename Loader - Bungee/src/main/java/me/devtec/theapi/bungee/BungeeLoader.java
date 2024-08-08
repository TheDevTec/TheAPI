package me.devtec.theapi.bungee;

import me.devtec.shared.API;
import me.devtec.shared.Ref;
import me.devtec.shared.Ref.ServerType;
import me.devtec.shared.components.BungeeComponentAPI;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.components.ComponentTransformer;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.mcmetrics.GatheringInfoManager;
import me.devtec.shared.mcmetrics.Metrics;
import me.devtec.shared.utility.ColorUtils;
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

import javax.tools.ToolProvider;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;

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
        console.sendMessage((BaseComponent) ComponentAPI.bungee().fromString(ColorUtils.colorize("&7> &dSupport&7: &ehttps://discord.gg/APwYKQRxby")));
        console.sendMessage((BaseComponent) ComponentAPI.bungee().fromString(ColorUtils.colorize("&7>")));
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

        Metrics.gatheringInfoManager = new GatheringInfoManager() {

            @Override
            public Consumer<String> getInfoLogger() {
                return msg -> plugin.getLogger().log(Level.INFO, msg);
            }

            @Override
            public BiConsumer<String, Throwable> getErrorLogger() {
                return (msg, error) -> plugin.getLogger().log(Level.WARNING, msg, error);
            }

            @Override
            public String getServerVersionVendor() {
                return null;
            }

            @Override
            public int getManagedServers() {
                return ProxyServer.getInstance().getServers().size();
            }

            @Override
            public String getServerVersion() {
                return ProxyServer.getInstance().getVersion();
            }

            @Override
            public String getServerName() {
                return ProxyServer.getInstance().getName();
            }

            @Override
            public int getPlayers() {
                return ProxyServer.getInstance().getOnlineCount();
            }

            @Override
            public int getOnlineMode() {
                return ProxyServer.getInstance().getConfig().isOnlineMode() ? 1 : 0;
            }
        };

        ComponentAPI.registerTransformer("BUNGEECORD", new BungeeComponentAPI<>());
        if (Ref.getClass("net.kyori.adventure.text.Component") != null)
            ComponentAPI.registerTransformer("ADVENTURE", (ComponentTransformer<?>) Ref.newInstanceByClass(Ref.getClass("me.devtec.shared.components.AdventureComponentAPI")));

        // Commands api
        API.commandsRegister = new BungeeCommandManager();
        API.selectorUtils = new BungeeSelectorUtils();

        // OfflineCache support!
        API.initOfflineCache(ProxyServer.getInstance().getConfig().isOnlineMode(), new Config("plugins/TheAPI/Cache.dat"));

        API.library = new LibraryLoader() {
            final List<File> loaded = new ArrayList<>();
            final Constructor<?> constructor = Ref.constructor(Ref.getClass("net.md_5.bungee.api.plugin.PluginClassloader"), ProxyServer.class, PluginDescription.class, URL[].class);

            @Override
            public void load(File file) {
                if (isLoaded(file) || !file.exists())
                    return;
                loaded.add(file);
                try {
                    Ref.newInstance(constructor, null, null, new URL[]{file.toURI().toURL()});
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
    }
}
