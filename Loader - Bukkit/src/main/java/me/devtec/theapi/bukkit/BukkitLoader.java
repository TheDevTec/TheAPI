package me.devtec.theapi.bukkit;

import com.mojang.authlib.GameProfile;
import me.devtec.shared.API;
import me.devtec.shared.Ref;
import me.devtec.shared.Ref.ServerType;
import me.devtec.shared.commands.structures.CommandStructure;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.dataholder.DataType;
import me.devtec.shared.dataholder.StringContainer;
import me.devtec.shared.mcmetrics.Metrics;
import me.devtec.shared.placeholders.PlaceholderAPI;
import me.devtec.shared.placeholders.PlaceholderExpansion;
import me.devtec.shared.utility.ColorUtils;
import me.devtec.shared.utility.MemoryCompiler;
import me.devtec.shared.utility.StreamUtils;
import me.devtec.shared.versioning.VersionUtils;
import me.devtec.shared.versioning.VersionUtils.Version;
import me.devtec.theapi.bukkit.bossbar.BossBar;
import me.devtec.theapi.bukkit.commands.hooker.BukkitCommandManager;
import me.devtec.theapi.bukkit.commands.hooker.LegacySimpleCommandMap;
import me.devtec.theapi.bukkit.events.ServerListPingEvent;
import me.devtec.theapi.bukkit.game.resourcepack.ResourcePackHandler;
import me.devtec.theapi.bukkit.game.resourcepack.ResourcePackResult;
import me.devtec.theapi.bukkit.game.worldgens.VoidGeneratorHelper;
import me.devtec.theapi.bukkit.gui.AnvilGUI;
import me.devtec.theapi.bukkit.gui.HolderGUI;
import me.devtec.theapi.bukkit.nms.GameProfileHandler;
import me.devtec.theapi.bukkit.nms.NmsProvider;
import me.devtec.theapi.bukkit.packetlistener.*;
import me.devtec.theapi.bukkit.scoreboard.ScoreboardAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.tools.ToolProvider;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.security.CodeSource;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class BukkitLoader extends JavaPlugin implements Listener {

    // Init static APIs
    public static final boolean NO_OBFUSCATED_NMS_MODE;

    static {
        BukkitLibInit.initTheAPI();
        NO_OBFUSCATED_NMS_MODE = Ref.isNewerThan(20) && Ref.serverType() == ServerType.PAPER || Ref.serverVersionInt() == 20 && Ref.serverVersionRelease() >= 5 && Ref.serverType() == ServerType.PAPER;
    }

    // public APIs
    public static NmsProvider nmsProvider;
    public static PacketHandler<?> handler;

    // private fields
    private static double release;
    private Metrics metrics;

    // public plugin fields
    public final Map<UUID, HolderGUI> gui = new ConcurrentHashMap<>();
    public List<BossBar> bossbars = new ArrayList<>();
    public final Map<UUID, ResourcePackHandler> resourcePackHandler = new ConcurrentHashMap<>();

    /**
     * @apiNote Get online players on the server
     * {@link NmsProvider#getOnlinePlayers()}
     */
    public static Collection<? extends Player> getOnlinePlayers() {
        return BukkitLoader.nmsProvider.getOnlinePlayers();
    }

    /**
     * @apiNote Get NmsProvider @see {@link BukkitLoader#nmsProvider} - Can be null
     * if NmsProvider failed load or isn't loaded yet
     */
    public static NmsProvider getNmsProvider() {
        return BukkitLoader.nmsProvider;
    }

    /**
     * @apiNote Get PacketHandler @see {@link BukkitLoader#handler} - Can be null if
     * PacketHandler isn't loaded yet
     */
    public static PacketHandler<?> getPacketHandler() {
        return BukkitLoader.handler;
    }

    /**
     * @apiNote Get Api release version
     */
    public static double getApiRelease() {
        return release;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        if (id == null || id.equalsIgnoreCase("void") || id.equalsIgnoreCase("voidgen"))
            return VoidGeneratorHelper.get();
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onLoad() {
        release = Config.loadFromInput(getResource("release.yml")).getDouble("release");

        Config config = new Config("plugins/TheAPI/config.yml");

        try {
            loadProvider(config.getBoolean("nmsProvider-use-directly-jar"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Ref.isNewerThan(12))
            ScoreboardAPI.SPLIT_MODERN_LINES = config.getBoolean("fallback-scoreboard-support");

        broadcastSystemInfo();

        if (Ref.field(Command.class, "timings") != null && Ref.isOlderThan(9)) {
            LegacySimpleCommandMap simpleCommandMap;
            Map<String, Command> map;
            Ref.set(Bukkit.getServer(), "commandMap",
                    simpleCommandMap = new LegacySimpleCommandMap(Bukkit.getServer(), map = (Map<String, Command>) Ref.get(Ref.get(Bukkit.getPluginManager(), "commandMap"), "knownCommands")));

            BukkitCommandManager.knownCommands = map;
            BukkitCommandManager.cmdMap = simpleCommandMap;
        }

        if (nmsProvider != null)
            if (new File("spigot.yml").exists() && Config.loadFromString(StreamUtils.fromStream(new File("spigot.yml"))).getBoolean("settings.late-bind"))
                new Thread(() -> { // ASYNC
                    if (Ref.isNewerThan(7))
                        handler = new PacketHandlerModern(true);
                    else
                        handler = (PacketHandler<?>) Ref.newInstance(Ref.constructor(Ref.getClass("me.devtec.theapi.bukkit.packetlistener.PacketHandlerLegacy"), boolean.class), true);
                }).start();

            else if (Ref.isNewerThan(7))
                handler = new PacketHandlerModern(false);
            else
                handler = (PacketHandler<?>) Ref.newInstance(Ref.constructor(Ref.getClass("me.devtec.theapi.bukkit.packetlistener.PacketHandlerLegacy"), boolean.class), false);
        if (handler == null) {
            Bukkit.getConsoleSender().sendMessage(ColorUtils.colorize("&7>"));
            Bukkit.getConsoleSender().sendMessage(ColorUtils.colorize("&7> &4Error! &eFailed to load PacketHandler."));
        }

        Class<?> serverPing;
        Class<?> resource;
        Class<?> close;
        Class<?> click;
        Class<?> itemname;
        Field anvilText;
        Field rpStatusField;
        if (NO_OBFUSCATED_NMS_MODE) {
            resource = Ref.nms("network.protocol.game", "ServerboundResourcePackPacket");
            close = Ref.nms("network.protocol.game", "ServerboundContainerClosePacket");
            serverPing = Ref.nms("network.protocol.status", "ClientboundStatusResponsePacket");
            click = Ref.nms("network.protocol.game", "ServerboundContainerClickPacket");
            itemname = Ref.nms("network.protocol.game", "ServerboundRenameItemPacket");
            rpStatusField = Ref.field(resource, "action");
            anvilText = Ref.field(itemname, "name");
        } else {
            resource = Ref.nms("network.protocol.game", "PacketPlayInResourcePackStatus");
            close = Ref.nms("network.protocol.game", "PacketPlayInCloseWindow");
            serverPing = Ref.nms("network.protocol.status", "PacketStatusOutServerInfo");
            click = Ref.nms("network.protocol.game", "PacketPlayInWindowClick");
            itemname = Ref.nms("network.protocol.game", "PacketPlayInItemName");
            rpStatusField = Ref.field(resource, Ref.isNewerThan(16) ? "a" : "status");
            anvilText = Ref.field(itemname, "a");
        }

        // BOSSBAR API: 1.7.10 - 1.8.8
        if (!Ref.isOlderThan(9))
            bossbars = null;

        new PacketListener() {

            @Override
            public void playOut(String nick, PacketContainer packetContainer, ChannelContainer channel) {
                if (packetContainer.isCancelled())
                    return;

                Object packet = packetContainer.getPacket();
                if (packet.getClass() == serverPing) {
                    if (ServerListPingEvent.getHandlerList().isEmpty())
                        return; // Do not process if event isn't used by any plugin
                    if (nmsProvider.processServerListPing(nick, channel.getChannel(),
                            Ref.isNewerThan(19) || Ref.serverVersionInt() == 19 && Ref.serverVersionRelease() == 3 ? packetContainer : packetContainer.getPacket()))
                        packetContainer.setCancelled(true);
                }
            }

            public boolean isAllowedChatCharacter(char var0) {
                return var0 != 167 && var0 >= ' ' && var0 != 127;
            }

            public String buildText(String text) {
                StringContainer builder = new StringContainer(text.length());
                for (int i = 0; i < text.length(); ++i) {
                    char c = text.charAt(i);
                    if (isAllowedChatCharacter(c))
                        builder.append(c);
                }
                return builder.toString();
            }

            @Override
            public void playIn(String nick, PacketContainer packetContainer, ChannelContainer channel) {
                if (nick == null || packetContainer.isCancelled())
                    return;

                Object packet = packetContainer.getPacket();
                // ResourcePackAPI
                if (packet.getClass() == resource) {
                    Player player = Bukkit.getPlayer(nick);
                    ResourcePackHandler handler;
                    if (player == null || (handler = resourcePackHandler.remove(player.getUniqueId())) == null)
                        return;
                    handler.call(player, ResourcePackResult.valueOf(getLegacyNameOf(rpStatusField.toString())));
                    return;
                }
                // GUIS
                if (packet.getClass() == itemname) {
                    Player player = Bukkit.getPlayer(nick);
                    if (player == null)
                        return;
                    HolderGUI gui = BukkitLoader.this.gui.get(player.getUniqueId());
                    if (gui instanceof AnvilGUI) {
                        String text = (String) Ref.get(packet, anvilText);
                        BukkitLoader.nmsProvider.postToMainThread(() -> ((AnvilGUI) gui).setRepairText(buildText(text)));
                        packetContainer.setCancelled(true);
                    }
                    return;
                }
                if (packet.getClass() == close) {
                    Player player = Bukkit.getPlayer(nick);
                    if (player == null)
                        return;
                    HolderGUI gui = BukkitLoader.this.gui.remove(player.getUniqueId());
                    if (gui == null)
                        return;
                    gui.closeWithoutPacket(player);
                    packetContainer.setCancelled(true);
                    return;
                }
                if (packet.getClass() == click) {
                    Player player = Bukkit.getPlayer(nick);
                    if (player == null)
                        return;
                    HolderGUI gui = BukkitLoader.this.gui.get(player.getUniqueId());
                    packetContainer.setCancelled(gui != null && BukkitLoader.nmsProvider.processInvClickPacket(player, gui, packet));
                }
            }

            private String getLegacyNameOf(String string) {
                switch (string.charAt(0)) {
                    case 'a':
                        return "SUCCESSFULLY_LOADED";
                    case 'b':
                        return "DECLINED";
                    case 'c':
                        return "FAILED_DOWNLOAD";
                    case 'd':
                        return "ACCEPTED";
                }
                return string;
            }
        }.register();

        metrics = new Metrics(getDescription().getVersion(), 20203);
    }

    private void broadcastSystemInfo() {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(ColorUtils.colorize("&7>"));
        console.sendMessage(ColorUtils.colorize("&7> &5TheAPI &dv" + getDescription().getVersion()));
        console.sendMessage(ColorUtils.colorize("&7>"));
        console.sendMessage(ColorUtils.colorize("&7> &5System info&7:"));
        console.sendMessage(ColorUtils.colorize("&7> &dJava&7: &e" + System.getProperty("java.version") + " &7(" + (ToolProvider.getSystemJavaCompiler() != null ? "&aJDK" : "&aJRE") + "&7)"));
        console.sendMessage(ColorUtils.colorize("&7> &dNmsProvider&7: " + (nmsProvider == null ? "&cNot provided &7(&e" + Ref.serverVersion() + "&7)" : "&e" + nmsProvider.getProviderName())));
        console.sendMessage(ColorUtils.colorize("&7> &dServer type&7: &e" + Ref.serverType()));
        console.sendMessage(ColorUtils.colorize("&7>"));
        console.sendMessage(ColorUtils.colorize("&7> &dSupport&7: &ehttps://discord.gg/APwYKQRxby"));
        console.sendMessage(ColorUtils.colorize("&7>"));
        if (nmsProvider == null) {
            console.sendMessage(ColorUtils.colorize("&7>"));
            console.sendMessage(ColorUtils.colorize("&7> &cWarning! Because NmsProvider isn't provided, PacketHandler is disabled."));
        }
    }

    private void loadProvider(boolean canUseJavaFile) throws Exception {
        String serverVersion = Ref.serverVersion().replace('.', '_');
        if (!serverVersion.startsWith("v")) {
            if(Ref.serverType()==ServerType.PAPER && (Ref.isNewerThan(20)||Ref.isNewerThan(19) && Ref.serverVersionRelease()>=6)) {
                try {
                    Config mappings = Config.loadFromInput(new URL("https://raw.githubusercontent.com/TheDevTec/TheAPI/main/paper-mappings.yml").openStream());
                    serverVersion = mappings.getString(serverVersion);
                }catch(Exception ignored){

                }
            }
            serverVersion = 'v' + serverVersion;
        }
        if (ToolProvider.getSystemJavaCompiler() != null && !canUseJavaFile)
            try {
                getAllJarFiles();
                checkForUpdateAndDownload(serverVersion);
                if (new File("plugins/TheAPI/NmsProviders/" + serverVersion + ".java").exists()) {
                    nmsProvider = (NmsProvider) new MemoryCompiler(NO_OBFUSCATED_NMS_MODE ? getClassLoader() : Bukkit.getServer().getClass().getClassLoader(), serverVersion, new File("plugins/TheAPI/NmsProviders/" + serverVersion + ".java")).buildClass().newInstance();
                    nmsProvider.loadParticles();
                }
            } catch (Exception err) {
                err.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(ColorUtils.colorize("&7> &4Error! Failed to load NmsProvider from .java file, loading from .jar."));
                checkForUpdateAndDownloadCompiled(serverVersion);
                if (new File("plugins/TheAPI/NmsProviders/" + serverVersion + ".jar").exists()){
                    URLClassLoader cl = new URLClassLoader(new URL[]{new URL("jar:file:" + "plugins/TheAPI/NmsProviders/" + serverVersion + ".jar" + "!/")}, getClassLoader());
                    Class<?> c = cl.loadClass(serverVersion);
                    nmsProvider = (NmsProvider) c.newInstance();
                    nmsProvider.loadParticles();
                }
            }
        else { // JRE
            checkForUpdateAndDownloadCompiled(serverVersion);
            if (new File("plugins/TheAPI/NmsProviders/" + serverVersion + ".jar").exists()) {
                URLClassLoader cl = new URLClassLoader(new URL[]{new URL("jar:file:" + "plugins/TheAPI/NmsProviders/" + serverVersion + ".jar" + "!/")}, getClassLoader());
                Class<?> c = cl.loadClass(serverVersion);
                nmsProvider = (NmsProvider) c.newInstance();
                nmsProvider.loadParticles();
            }
        }
    }

    private void getAllJarFiles() throws URISyntaxException {
        StringContainer args = new StringContainer(1024);
        CodeSource source = Bukkit.getServer().getClass().getProtectionDomain().getCodeSource();
        if (source != null) {
            File file = new File(source.getLocation().toURI());
            StringContainer fixedPath = new StringContainer(file.getName());
            while (file.getParentFile() != null && !isInsidePath(file.getParentFile().toPath(), new File(System.getProperty("java.class.path")).toPath())) {
                fixedPath.insert(0, '/').insert(0, file.getParentFile().getName());
                file = file.getParentFile();
            }
            MemoryCompiler.allJars += (System.getProperty("os.name").toLowerCase().contains("win") ? ";" : ":") + (fixedPath.charAt(0) == '/' ? fixedPath : "./" + fixedPath);
        }
        addAllJarFiles(args, new File("plugins"), false); // Plugins
        if (Ref.serverType() == ServerType.PAPER)
            addAllJarFiles(args, new File("libraries"), true); // Libraries
        else
            addAllJarFiles(args, new File("bundler/libraries"), true); // Libraries
        MemoryCompiler.allJars += args.toString();
    }

    private boolean isInsidePath(Path current, Path file) {
        return current.equals(file.toAbsolutePath().getParent());
    }

    private void addAllJarFiles(StringContainer args, File folder, boolean sub) {
        if (!folder.exists())
            return;
        File[] files = folder.listFiles();

        char splitChar = System.getProperty("os.name").toLowerCase().contains("win") ? ';' : ':';

        if (files != null)
            for (File file : files)
                if (file.isDirectory() && sub)
                    addAllJarFiles(args, file, true);
                else if (file.getName().endsWith(".jar"))
                    if (file.getPath().charAt(0) == '/')
                        args.append(splitChar).append(file.getPath());
                    else
                        args.append(splitChar).append('.').append('/').append(file.getPath());
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PlaceholderAPI.PAPI_BRIDGE = new PlaceholderExpansion("PAPI Support") {
                @Override
                public String apply(String text, UUID player) {
                    return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player == null ? null : Bukkit.getOfflinePlayer(player), "%" + text + "%");
                }
            };
            PlaceholderAPI.unregisterConsumer = exp -> ((me.clip.placeholderapi.expansion.PlaceholderExpansion) exp.getPapiInstance()).unregister();
            PlaceholderAPI.registerConsumer = exp -> ((me.clip.placeholderapi.expansion.PlaceholderExpansion) exp.setPapiInstance(new me.clip.placeholderapi.expansion.PlaceholderExpansion() {
                @Override
                public String onRequest(OfflinePlayer player, @NotNull String params) {
                    return exp.apply(params, player == null ? null : player.getUniqueId());
                }

                @Override
                public @NotNull String getName() {
                    return exp.getName();
                }

                @Override
                public @NotNull String getIdentifier() {
                    return exp.getName().toLowerCase();
                }

                @Override
                public @NotNull String getAuthor() {
                    return "(Unknown) TheAPI Provided Placeholder";
                }

                @Override
                public @NotNull String getVersion() {
                    return BukkitLoader.this.getDescription().getVersion();
                }
            }).getPapiInstance()).register();
        }

        // Command to reload NmsProvider
        CommandStructure.create(ConsoleCommandSender.class, (sender, perm, isTablist) -> sender.hasPermission(perm), (sender, structure, args) -> {
            try {
                loadProvider(new Config("plugins/TheAPI/config.yml").getBoolean("nmsProvider-use-directly-jar"));
                sender.sendMessage(ColorUtils.colorize("&5TheAPI &8» &7NmsProvider &asuccesfully &7reloaded."));
            } catch (Exception e) {
                sender.sendMessage(ColorUtils.colorize("&5TheAPI &8» &7An &cerror &7occurred when reloading NmsProvider."));
                sender.sendMessage(ColorUtils.colorize("&5TheAPI &8» &7&nDO NOT MODIFY THIS FILE IF YOU DON'T KNOW WHAT ARE YOU DOING!"));
            }
        }).permission("theapireload.command").build().register("theapireload");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPreLoginEvent(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() == org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.ALLOWED)
            API.offlineCache().setLookup(e.getUniqueId(), e.getName());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLoginEvent(PlayerLoginEvent e) { // fix uuid - premium login?
        if (e.getResult() == Result.ALLOWED) {
            API.offlineCache().setLookup(e.getPlayer().getUniqueId(), e.getPlayer().getName());
            if (handler != null)
                handler.add(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLoginEvent(PlayerJoinEvent e) {
        if (handler != null)
            handler.add(e.getPlayer()); // Move to the first position
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        API.removeCache(e.getPlayer().getUniqueId());
    }

    @Override
    public void onDisable() {
        metrics.shutdown();
        API.setEnabled(false);
        for (HolderGUI gui : new ArrayList<>(this.gui.values()))
            gui.close();
        if (handler != null) {
            BukkitLoader.handler.close();
            if (bossbars != null)
                for (BossBar bar : new ArrayList<>(bossbars))
                    bar.remove();
        }
        PlaceholderAPI.PAPI_BRIDGE = null;
        // OfflineCache support!
        API.offlineCache().saveToConfig().setFile(new File("plugins/TheAPI/Cache.dat")).save("properties");
    }

    private void checkForUpdateAndDownloadCompiled(String serverVersion) {
        try {
            Config gitVersion = Config.loadFromInput(new URL("https://raw.githubusercontent.com/TheDevTec/TheAPI/main/version.yml").openStream());
            Config localVersion = new Config("plugins/TheAPI/version.yml");
            Version ver = getGitVersion(localVersion, gitVersion);

            if (ver != Version.OLDER_VERSION && ver != Version.SAME_VERSION && new File("plugins/TheAPI/NmsProviders/" + serverVersion + ".jar").exists()) {
                Bukkit.getConsoleSender().sendMessage("[TheAPI NmsProvider Updater] §cERROR! Can't download new NmsProvider, please update TheAPI.");
                localVersion.save(DataType.YAML);
                return;
            }
            if (localVersion.getInt("build") < gitVersion.getInt("build") || !new File("plugins/TheAPI/NmsProviders/" + serverVersion + ".jar").exists()
                    || new File("plugins/TheAPI/NmsProviders/" + serverVersion + ".jar").length() == 0) {
                localVersion.set("build", gitVersion.getInt("build"));
                localVersion.save(DataType.YAML);

                URL url = new URL("https://raw.githubusercontent.com/TheDevTec/TheAPI/main/NmsProvider%20-%20" + serverVersion.substring(1).replace('_', '.') + "/build/NmsProvider.jar");
                Bukkit.getConsoleSender().sendMessage("[TheAPI NmsProvider Updater] §aDownloading update!");
                API.library.downloadFileFromUrl(url, new File("plugins/TheAPI/NmsProviders/" + serverVersion + ".jar"));
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("[TheAPI NmsProvider Updater] §eNot found NmsProvider for your server version, do you have your own?");
        }
    }

    private Version getGitVersion(Config localVersion, Config gitVersion) {
        localVersion.setIfAbsent("build", 1);
        localVersion.setComments("build", Collections.singletonList("# DO NOT MODIFY THIS VALUE"));
        return VersionUtils.getVersion(gitVersion.getString("release"), "" + release);
    }

    private void checkForUpdateAndDownload(String serverVersion) {
        try {
            Config gitVersion = Config.loadFromInput(new URL("https://raw.githubusercontent.com/TheDevTec/TheAPI/main/version.yml").openStream());
            Config localVersion = new Config("plugins/TheAPI/version.yml");
            Version ver = getGitVersion(localVersion, gitVersion);

            if (ver != Version.OLDER_VERSION && ver != Version.SAME_VERSION && new File("plugins/TheAPI/NmsProviders/" + serverVersion + ".java").exists()) {
                Bukkit.getConsoleSender().sendMessage("[TheAPI NmsProvider Updater] §cERROR! Can't download new NmsProvider, please update TheAPI.");
                Bukkit.getConsoleSender().sendMessage("[TheAPI NmsProvider Updater] §cERROR! Current release: " + release);
                Bukkit.getConsoleSender().sendMessage("[TheAPI NmsProvider Updater] §cERROR! Required release: " + gitVersion.getString("release"));
                localVersion.save(DataType.YAML);
                return;
            }
            if (localVersion.getInt("build") < gitVersion.getInt("build") || !new File("plugins/TheAPI/NmsProviders/" + serverVersion + ".java").exists()) {
                localVersion.set("build", gitVersion.getInt("build"));
                localVersion.save(DataType.YAML);

                URL url = new URL("https://raw.githubusercontent.com/TheDevTec/TheAPI/main/NmsProvider%20-%20" + serverVersion.substring(1).replace('_', '.') + "/src/main/java/"
                        + serverVersion + ".java");
                Bukkit.getConsoleSender().sendMessage("[TheAPI NmsProvider Updater] §aDownloading update!");
                API.library.downloadFileFromUrl(url, new File("plugins/TheAPI/NmsProviders/" + serverVersion + ".java"));
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("[TheAPI NmsProvider Updater] §eNot found NmsProvider for your server version, do you have your own?");
        }
    }
}
