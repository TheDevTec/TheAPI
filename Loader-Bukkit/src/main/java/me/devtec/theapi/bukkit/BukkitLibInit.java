package me.devtec.theapi.bukkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.zip.ZipEntry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import me.devtec.shared.API;
import me.devtec.shared.Ref;
import me.devtec.shared.Ref.ServerType;
import me.devtec.shared.annotations.Nullable;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.components.ComponentTransformer;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.dataholder.StringContainer;
import me.devtec.shared.json.Json;
import me.devtec.shared.json.Json.DataReader;
import me.devtec.shared.json.Json.DataWriter;
import me.devtec.shared.mcmetrics.GatheringInfoManager;
import me.devtec.shared.mcmetrics.Metrics;
import me.devtec.shared.utility.ColorUtils;
import me.devtec.shared.utility.ColorUtils.ColormaticFactory;
import me.devtec.shared.utility.LibraryLoader;
import me.devtec.shared.utility.ParseUtils;
import me.devtec.shared.versioning.VersionUtils;
import me.devtec.shared.versioning.VersionUtils.Version;
import me.devtec.theapi.bukkit.commands.hooker.BukkitCommandManager;
import me.devtec.theapi.bukkit.commands.selectors.BukkitSelectorUtils;
import me.devtec.theapi.bukkit.game.BlockDataStorage;
import me.devtec.theapi.bukkit.game.ItemMaker;
import me.devtec.theapi.bukkit.game.Position;

public class BukkitLibInit {
    private static Method addUrl;

    private static class SimpleClassLoader extends URLClassLoader {

        public SimpleClassLoader(URL[] urls) {
            super(urls);
        }

        @Override
        public void addURL(URL url) {
            super.addURL(url);
        }
    }

    private static class ImplementableJar extends JarFile {
        public final List<JarFile> file = new ArrayList<>();

        public ImplementableJar(File file) throws IOException {
            super(file);
        }

        @Override
        public @NotNull Enumeration<JarEntry> entries() {
            List<Enumeration<JarEntry>> totalEntries = new ArrayList<>();
            totalEntries.add(super.entries());
            for (JarFile search : file) {
				totalEntries.add(search.entries());
			}

            return new Enumeration<JarEntry>() {

                int posInList = 0;
                Enumeration<JarEntry> current = totalEntries.get(posInList);

                @Override
                public JarEntry nextElement() {
                    if (current.hasMoreElements()) {
						return current.nextElement();
					}
                    if (++posInList < totalEntries.size()) {
						return (current = totalEntries.get(posInList)).nextElement();
					}
                    return null;
                }

                @Override
                public boolean hasMoreElements() {
                    if (current.hasMoreElements()) {
						return true;
					}
                    if (posInList + 1 < totalEntries.size()) {
						return totalEntries.get(posInList + 1).hasMoreElements();
					}
                    return false;
                }
            };
        }

        @Override
        public ZipEntry getEntry(String name) {
            ZipEntry find = super.getEntry(name);
            if (find == null) {
				for (JarFile search : file) {
                    find = search.getEntry(name);
                    if (find != null) {
						return find;
					}
                }
			}
            return null;
        }

        @Override
        public JarEntry getJarEntry(String name) {
            JarEntry find = super.getJarEntry(name);
            if (find == null) {
				for (JarFile search : file) {
                    find = search.getJarEntry(name);
                    if (find != null) {
						return find;
					}
                }
			}
            return null;
        }

        @Override
        public InputStream getInputStream(ZipEntry name) throws IOException {
            InputStream find = super.getInputStream(name);
            if (find == null) {
				for (JarFile search : file) {
                    find = search.getInputStream(name);
                    if (find != null) {
						return find;
					}
                }
			}
            return null;
        }

        @Override
        public void close() throws IOException {
            super.close();
            for (JarFile f : file) {
				f.close();
			}
            file.clear();
        }

    }

    private static int getJavaVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
			version = version.substring(2, 3);
		} else {
            int dot = version.indexOf(".");
            if (dot != -1) {
				version = version.substring(0, dot);
			}
        }
        return ParseUtils.getInt(version);
    }

    public static void initTheAPI() {
        try {
            Ref.init(Ref.getClass("net.md_5.bungee.api.ChatColor") != null ? Ref.getClass("net.kyori.adventure.Adventure") != null ? ServerType.PAPER : ServerType.SPIGOT : ServerType.BUKKIT,
                    Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]);
        } catch (Exception e) {
            // Paper 1.20.5+
            String version = (String) Ref.invoke(Bukkit.getServer(),"getMinecraftVersion");
            Version ver = VersionUtils.getVersion(version, "1.20.5");
            if (ver == Version.SAME_VERSION || ver == Version.NEWER_VERSION) {
				Ref.init(Ref.getClass("net.md_5.bungee.api.ChatColor") != null ? Ref.getClass("net.kyori.adventure.Adventure") != null ? ServerType.PAPER : ServerType.SPIGOT : ServerType.BUKKIT,
                        version);
			}
        }

        Metrics.gatheringInfoManager = new GatheringInfoManager() {

            @Override
            public String getServerVersionVendor() {
                return null;
            }

            @Override
            public int getManagedServers() {
                return 0;
            }

            @Override
            public String getServerVersion() {
                return Bukkit.getVersion();
            }

            @Override
            public String getServerName() {
                return Bukkit.getName();
            }

            @Override
            public int getPlayers() {
                return BukkitLoader.getOnlinePlayers().size();
            }

            @Override
            public int getOnlineMode() {
                return Bukkit.getOnlineMode() ? 1 : 0;
            }

            @Override
            public Consumer<String> getInfoLogger() {
                return msg -> JavaPlugin.getPlugin(BukkitLoader.class).getLogger().log(Level.INFO, msg);
            }

            @Override
            public BiConsumer<String, Throwable> getErrorLogger() {
                return (msg, error) -> JavaPlugin.getPlugin(BukkitLoader.class).getLogger().log(Level.WARNING, msg, error);
            }
        };

        // Init json parsers
        registerWriterAndReaders();

        // version
        if (Ref.serverType() != ServerType.BUKKIT) {
            ComponentAPI.registerTransformer("BUNGEECORD", (ComponentTransformer<?>) Ref.newInstanceByClass(Ref.getClass("me.devtec.shared.components.BungeeComponentAPI")));
            if (Ref.serverType() == ServerType.PAPER) {
				ComponentAPI.registerTransformer("ADVENTURE", (ComponentTransformer<?>) Ref.newInstanceByClass(Ref.getClass("me.devtec.shared.components.AdventureComponentAPI")));
			}
        }
        // Commands api
        API.commandsRegister = new BukkitCommandManager();
        API.selectorUtils = new BukkitSelectorUtils();

        // OfflineCache support!
        API.initOfflineCache(Bukkit.getOnlineMode(), new Config("plugins/TheAPI/Cache.dat"));

        API.library = new LibraryLoader() {
            final List<File> loaded = new ArrayList<>();
            ImplementableJar jar;
            Field libField;
            SimpleClassLoader libraryLoader;

            @Override
            public void load(File file) {
                if (isLoaded(file) || !file.exists()) {
					return;
				}
                loaded.add(file);
                ClassLoader loader = BukkitLoader.class.getClassLoader();
                if (getJavaVersion() <= 15) {
                    if (addUrl == null) {
						addUrl = Ref.method(URLClassLoader.class, "addURL", URL.class);
					}
                    try {
                        Ref.invoke(loader, addUrl, file.toURI().toURL()); // Simple!
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                } else if (Ref.isNewerThan(16) || Ref.isNewerThan(15) && Ref.serverType() == ServerType.PAPER) {
                    if (libraryLoader == null) {
                        try {
                            libraryLoader = new SimpleClassLoader(new URL[]{file.toURI().toURL()});
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        if (libField == null) {
                            libField = Ref.field(loader.getClass(), "library");
                            if (libField == null) {
								libField = Ref.field(loader.getClass(), "libraryLoader");
							}
                        }
                        Ref.set(loader, libField, libraryLoader);
                    } else {
						try {
                            libraryLoader.addURL(file.toURI().toURL());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
					}
                } else {
					try { // Just small hack for modern Java. - Does not working for files inside jar
                        if (jar == null) {
                            jar = new ImplementableJar((File) Ref.get(loader, "file"));
                            Ref.set(loader, "manifest", jar);
                            Ref.set(loader, "jar", jar);
                        }
                        jar.file.add(new JarFile(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
				}
            }

            @Override
            public boolean isLoaded(File file) {
                return loaded.contains(file);
            }
        };
        API.basics().load();
        if (Ref.isOlderThan(16)) {
			ColorUtils.color = new ColormaticFactory() {
                final String rainbow = "c6ea9b5";
                final char[] chars = rainbow.toCharArray();
                final AtomicInteger position = new AtomicInteger(0);

                final int[][] EMPTY_ARRAY = {};
                final char[] EMPTY_CHAR_ARRAY = {};
                final char[] RESET_CHAR_ARRAY = {'§', 'r'};

                @Override
                public StringContainer gradient(StringContainer container, int start, int end, @Nullable String firstHex, @Nullable String secondHex, @Nullable List<String> protectedStrings) {
                    boolean inRainbow = false;
                    char[] formats = EMPTY_CHAR_ARRAY;

                    // Skip regions
                    int[][] skipRegions = EMPTY_ARRAY;
                    byte allocated = 0;
                    int currentSkipAt = -1;
                    byte skipId = 0;

                    if (protectedStrings != null) {
                        for (String protect : protectedStrings) {
                            int size = protect.length();

                            int num = 0;
                            while (true) {
                                int position = container.indexOf(protect, num);
                                if (position == -1) {
									break;
								}
                                num = position + size;
                                if (allocated == 0 || allocated >= skipRegions.length - 1) {
                                    int[][] copy = new int[(allocated << 1) + 1][];
                                    if (allocated > 0) {
										System.arraycopy(skipRegions, 0, copy, 0, skipRegions.length);
									}
                                    skipRegions = copy;
                                }
                                skipRegions[allocated++] = new int[]{position, size};
                            }
                        }
                        if (allocated > 0) {
							currentSkipAt = skipRegions[0][0];
						}
                    }

                    int i = start - 1;
                    for (int step = 0; step < end - start; ++step) {
                        char c = container.charAt(++i);

                        if (currentSkipAt == i) {
                            int skipForChars = skipRegions[skipId++][1] - 1;
                            currentSkipAt = skipId == allocated ? -1 : skipRegions[skipId][0];
                            i += skipForChars;
                            step += skipForChars;
                            continue;
                        }

                        if (c == '&' && i + 1 < container.length() && container.charAt(i + 1) == 'u') {
                            container.delete(i, i + 2);
                            --i;
                            ++step;
                            inRainbow = true;
                            continue;
                        }

                        if (inRainbow) {
							switch (c) {
                                case ' ':
                                    if (formats.length == 2 && formats[1] == 'r') {
                                        container.insertMultipleChars(i, formats);
                                        formats = EMPTY_CHAR_ARRAY;
                                        i += 2;
                                        container.insert(i, generateColor());
                                        i += 2;
                                    }
                                    continue;
                                case '§':
                                    if (i + 1 < container.length()) {
                                        c = container.charAt(++i);
                                        ++step;
                                        if (isFormat(c)) {
                                            container.delete(i - 1, i + 1);
                                            i -= 2;
                                            if (c == 'r') {
												formats = RESET_CHAR_ARRAY;
											} else if (formats.length == 0) {
												formats = new char[]{'§', c};
											} else {
                                                char[] copy = new char[formats.length + 2];
                                                System.arraycopy(formats, 0, copy, 0, formats.length);
                                                formats = copy;
                                                formats[formats.length - 2] = '§';
                                                formats[formats.length - 1] = c;
                                            }
                                            break;
                                        }
                                        if (isColor(c)) {
											inRainbow = false;
										}
                                        break;
                                    }
                                default:
                                    if (formats.length == 2 && formats[1] == 'r') {
                                        container.insertMultipleChars(i, formats);
                                        formats = EMPTY_CHAR_ARRAY;
                                        i += 2;
                                        container.insert(i, generateColor());
                                        i += 2;
                                    } else {
                                        container.insert(i, generateColor());
                                        i += 2;
                                        if (formats.length != 0) {
                                            container.insertMultipleChars(i, formats);
                                            i += formats.length;
                                        }
                                    }
                                    break;
                            }
						}
                    }
                    return container;
                }

                private boolean isColor(int charAt) {
                    return charAt >= 97 && charAt <= 102 || charAt >= 65 && charAt <= 70 || charAt >= 48 && charAt <= 57;
                }

                private boolean isFormat(int charAt) {
                    return charAt >= 107 && charAt <= 111 || charAt == 114;
                }

                @Override
                public String generateColor() {
                    if (position.get() == chars.length) {
						position.set(0);
					}
                    return new String(new char[]{'§', chars[position.getAndIncrement()]});
                }

                @Override
                public StringContainer replaceHex(StringContainer text) {
                    return text;
                }

                @Override
                public StringContainer rainbow(StringContainer container, int start, int end, @Nullable String firstHex, @Nullable String secondHex, @Nullable List<String> protectedStrings) {
                    gradient(container, start, end, null, null, protectedStrings);
                    return container;
                }
            };
		}
    }

    private static void registerWriterAndReaders() {
        // world
        Json.registerDataWriter(new DataWriter() {

            @Override
            public Map<String, Object> write(Object object) {
                Map<String, Object> map = new HashMap<>();
                World pos = (World) object;
                map.put("classType", "org.bukkit.World");
                map.put("name", pos.getName());
                map.put("uuid", pos.getUID());
                return map;
            }

            @Override
            public boolean isAllowed(Object object) {
                return object instanceof World;
            }
        });
        Json.registerDataReader(new DataReader() {

            @Override
            public boolean isAllowed(Map<String, Object> map) {
                return "org.bukkit.World".equals(map.get("classType"));
            }

            @Override
            public Object read(Map<String, Object> map) {
                return Bukkit.getWorld(UUID.fromString(map.get("uuid").toString()));
            }
        });
        // location
        Json.registerDataWriter(new DataWriter() {

            @Override
            public Map<String, Object> write(Object object) {
                Map<String, Object> map = new HashMap<>();
                Location pos = (Location) object;
                map.put("classType", "org.bukkit.Location");
                map.put("world", pos.getWorld().getName());
                map.put("x", pos.getX());
                map.put("y", pos.getY());
                map.put("z", pos.getZ());
                map.put("yaw", pos.getYaw());
                map.put("pitch", pos.getPitch());
                return map;
            }

            @Override
            public boolean isAllowed(Object object) {
                return object instanceof Location;
            }
        });
        Json.registerDataReader(new DataReader() {

            @Override
            public boolean isAllowed(Map<String, Object> map) {
                return "org.bukkit.Location".equals(map.get("classType"));
            }

            @Override
            public Object read(Map<String, Object> map) {
                Object result = map.get("yaw");
                return new Location(Bukkit.getWorld(map.get("world").toString()), ((Number) map.get("x")).doubleValue(), ((Number) map.get("y")).doubleValue(), ((Number) map.get("z")).doubleValue(),
                        ((Number) (result == null ? 0f : result)).floatValue(), ((Number) ((result = map.get("pitch")) == null ? 0f : result)).floatValue());
            }
        });
        // position
        Json.registerDataWriter(new DataWriter() {

            @Override
            public Map<String, Object> write(Object object) {
                Map<String, Object> map = new HashMap<>();
                Position pos = (Position) object;
                map.put("classType", "Position");
                map.put("world", pos.getWorldName());
                map.put("x", pos.getX());
                map.put("y", pos.getY());
                map.put("z", pos.getZ());
                map.put("yaw", pos.getYaw());
                map.put("pitch", pos.getPitch());
                return map;
            }

            @Override
            public boolean isAllowed(Object object) {
                return object instanceof Position;
            }
        });
        Json.registerDataReader(new DataReader() {

            @Override
            public boolean isAllowed(Map<String, Object> map) {
                return "Position".equals(map.get("classType"));
            }

            @Override
            public Object read(Map<String, Object> map) {
                Object result = map.get("yaw");
                return new Position(map.get("world").toString(), ((Number) map.get("x")).doubleValue(), ((Number) map.get("y")).doubleValue(), ((Number) map.get("z")).doubleValue(),
                        ((Number) (result == null ? 0f : result)).floatValue(), ((Number) ((result = map.get("pitch")) == null ? 0f : result)).floatValue());
            }
        });
        // itemstack
        Json.registerDataWriter(new DataWriter() {

            @Override
            public Map<String, Object> write(Object object) {
                Map<String, Object> map = ItemMaker.of((ItemStack) object).serializeToMap();
                map.put("classType", "ItemStack");
                return map;
            }

            @Override
            public boolean isAllowed(Object object) {
                return object instanceof ItemStack;
            }
        });
        Json.registerDataReader(new DataReader() {

            @Override
            public boolean isAllowed(Map<String, Object> map) {
                return "ItemStack".equals(map.get("classType"));
            }

            @Override
            public Object read(Map<String, Object> map) {
                return ItemMaker.loadFromJson(map, false);
            }
        });

        // itemmaker
        Json.registerDataWriter(new DataWriter() {

            @Override
            public Map<String, Object> write(Object object) {
                Map<String, Object> map = ItemMaker.of((ItemStack) object).serializeToMap();
                map.put("classType", "ItemMaker");
                return map;
            }

            @Override
            public boolean isAllowed(Object object) {
                return object instanceof ItemMaker;
            }
        });
        Json.registerDataReader(new DataReader() {

            @Override
            public boolean isAllowed(Map<String, Object> map) {
                return "ItemMaker".equals(map.get("classType"));
            }

            @Override
            public Object read(Map<String, Object> map) {
                return ItemMaker.loadMakerFromJson(map, false);
            }
        });

        // blockdatastorage
        Json.registerDataReader(new DataReader() {

            @Override
            public Object read(Map<String, Object> json) {
                Object nbt = json.get("nbt");
                return new BlockDataStorage(Material.getMaterial(json.get("material").toString()), ((Number) json.get("itemData")).byteValue(), json.get("data").toString(),
                        nbt == null ? null : nbt.toString());
            }

            @Override
            public boolean isAllowed(Map<String, Object> json) {
                return "BlockDataStorage".equals(json.get("classType"));
            }
        });
        Json.registerDataWriter(new DataWriter() {

            @Override
            public boolean isAllowed(Object obj) {
                return obj instanceof BlockDataStorage;
            }

            @Override
            public Map<String, Object> write(Object obj) {
                BlockDataStorage data = (BlockDataStorage) obj;
                Map<String, Object> map = new HashMap<>();
                map.put("classType", "BlockDataStorage");
                map.put("material", data.getType().name());
                map.put("itemData", data.getItemData());
                map.put("data", data.getData() == null ? "" : data.getData());
                if (data.getNBT() != null) {
					map.put("nbt", data.getNBT());
				}
                return map;
            }
        });
    }
}
