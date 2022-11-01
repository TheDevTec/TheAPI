package me.devtec.theapi.bukkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.devtec.shared.API;
import me.devtec.shared.Ref;
import me.devtec.shared.Ref.ServerType;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.components.ComponentTransformer;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.dataholder.StringContainer;
import me.devtec.shared.json.JReader;
import me.devtec.shared.json.JWriter;
import me.devtec.shared.json.Json;
import me.devtec.shared.json.Json.DataReader;
import me.devtec.shared.json.Json.DataWriter;
import me.devtec.shared.json.modern.ModernJsonReader;
import me.devtec.shared.json.modern.ModernJsonWriter;
import me.devtec.shared.utility.LibraryLoader;
import me.devtec.shared.utility.StringUtils;
import me.devtec.shared.utility.StringUtils.ColormaticFactory;
import me.devtec.theapi.bukkit.commands.hooker.BukkitCommandManager;
import me.devtec.theapi.bukkit.commands.selectors.BukkitSelectorUtils;
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
		public List<JarFile> file = new ArrayList<>();

		public ImplementableJar(File file) throws IOException {
			super(file);
		}

		@Override
		public JarEntry getJarEntry(String name) {
			JarEntry find = super.getJarEntry(name);
			if (find == null)
				for (JarFile search : file) {
					find = search.getJarEntry(name);
					if (find != null)
						return find;
				}
			return null;
		}

		@Override
		public InputStream getInputStream(ZipEntry name) throws IOException {
			InputStream find = super.getInputStream(name);
			if (find == null)
				for (JarFile search : file) {
					find = search.getInputStream(name);
					if (find != null)
						return find;
				}
			return null;
		}

		@Override
		public void close() throws IOException {
			super.close();
			for (JarFile f : file)
				f.close();
			file.clear();
		}

	}

	private static int getJavaVersion() {
		String version = System.getProperty("java.version");
		if (version.startsWith("1."))
			version = version.substring(2, 3);
		else {
			int dot = version.indexOf(".");
			if (dot != -1)
				version = version.substring(0, dot);
		}
		return StringUtils.getInt(version);
	}

	public static void initTheAPI(JavaPlugin plugin) {
		Ref.init(Ref.getClass("net.md_5.bungee.api.ChatColor") != null ? Ref.getClass("net.kyori.adventure.Adventure") != null ? ServerType.PAPER : ServerType.SPIGOT : ServerType.BUKKIT,
				Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]); // Server
																						// version

		// Init json parsers
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
				return map.get("classType") != null && map.get("classType").equals("Position");
			}

			@Override
			public Object read(Map<String, Object> map) {
				return new Position(map.get("world").toString(), (double) map.get("x"), (double) map.get("y"), (double) map.get("z"), (float) (double) map.get("yaw"),
						(float) (double) map.get("pitch"));
			}
		});

		// version
		if (Ref.serverType() != ServerType.BUKKIT) {
			ComponentAPI.registerTransformer("BUNGEECORD", (ComponentTransformer<?>) Ref.newInstanceByClass(Ref.getClass("me.devtec.shared.components.BungeeComponentAPI")));
			if (Ref.serverType() == ServerType.PAPER)
				ComponentAPI.registerTransformer("ADVENTURE", (ComponentTransformer<?>) Ref.newInstanceByClass(Ref.getClass("me.devtec.shared.components.AdventureComponentAPI")));
		}
		if (Ref.isNewerThan(7))
			Json.init(new ModernJsonReader(), new ModernJsonWriter()); // Modern version of Guava
		else
			Json.init((JReader) Ref.newInstanceByClass(Ref.getClass("me.devtec.shared.json.legacy.LegacyJsonReader")),
					(JWriter) Ref.newInstanceByClass(Ref.getClass("me.devtec.shared.json.legacy.LegacyJsonWriter"))); // 1.7.10

		// Commands api
		API.commandsRegister = new BukkitCommandManager();
		API.selectorUtils = new BukkitSelectorUtils();

		// OfflineCache support!
		API.initOfflineCache(Bukkit.getOnlineMode(), new Config("plugins/TheAPI/Cache.dat"));

		API.library = new LibraryLoader() {
			List<File> loaded = new ArrayList<>();
			ImplementableJar jar;
			SimpleClassLoader lloader;

			@Override
			public void load(File file) {
				if (isLoaded(file) || !file.exists())
					return;
				loaded.add(file);
				ClassLoader loader = plugin.getClass().getClassLoader();
				if (getJavaVersion() <= 15) {
					if (addUrl == null)
						addUrl = Ref.method(URLClassLoader.class, "addURL", URL.class);
					try {
						Ref.invoke(loader, addUrl, file.toURI().toURL()); // Simple!
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				} else {
					if (Ref.isNewerThan(16) || Ref.isNewerThan(15) && Ref.serverType() == ServerType.PAPER)
						if (lloader == null) {
							try {
								lloader = new SimpleClassLoader(new URL[] { file.toURI().toURL() });
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
							Ref.set(loader, "library", lloader);
						} else
							try {
								lloader.addURL(file.toURI().toURL());
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
					try { // Just small hack for modern Java.. - Does not working for files inside jar
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
		StringUtils.rainbowSplit = Pattern.compile("(#[A-Fa-f0-9]{6}([&§][K-Ok-oRr])*|[&§][Xx]([&§][A-Fa-f0-9]){6}([&§][K-Ok-oRr])*|[&§][A-Fa-f0-9K-ORrk-oUuXx]([&§][K-Ok-oRr])*)");
		StringUtils.color = new ColormaticFactory() {
			String rainbow = "c6ea9b5";
			char[] chars = rainbow.toCharArray();
			AtomicInteger position = new AtomicInteger(0);

			int[][] EMPTY_ARRAY = {};

			@Override
			public String gradient(String msg, String fromHex, String toHex, List<String> protectedStrings) {
				if (Ref.isNewerThan(15)) // Hex
					return API.basics().gradient(msg, fromHex, toHex, protectedStrings);

				boolean inRainbow = false;
				char prev = 0;
				String formats = "";

				int[][] skipRegions = EMPTY_ARRAY;
				int allocated = 0;

				int currentSkipAt = -1;
				int skipId = 0;

				int fixedSize = msg.length() * 2;
				if (protectedStrings != null) {
					for (String protect : protectedStrings) {
						int size = protect.length();

						int num = 0;
						while (true) {
							int position = msg.indexOf(protect, num);
							if (position == -1)
								break;
							num = position + size;
							if (allocated == 0 || allocated >= skipRegions.length - 1) {
								int[][] copy = new int[(allocated << 1) + 1][];
								if (allocated > 0)
									System.arraycopy(skipRegions, 0, copy, 0, skipRegions.length);
								skipRegions = copy;
							}
							fixedSize -= size * 2;
							skipRegions[allocated++] = new int[] { position, size };
						}
					}
					if (allocated > 0)
						currentSkipAt = skipRegions[0][0];
				}

				StringContainer builder = new StringContainer(fixedSize);

				int skipForChars = 0;
				for (int i = 0; i < msg.length(); ++i) {
					char c = msg.charAt(i);
					if (c == 0)
						continue;

					if (skipForChars > 0) {
						builder.append(c);
						--skipForChars;
						continue;
					}

					if (currentSkipAt == i) {
						skipForChars = skipRegions[skipId++][1] - 1;
						currentSkipAt = skipId == allocated ? -1 : skipRegions[skipId][0];
						builder.append(c);
						continue;
					}

					if (prev == '&' || prev == '§') {
						if (prev == '&' && c == 'u') {
							builder.deleteCharAt(builder.length() - 1); // remove & char
							inRainbow = true;
							prev = c;
							continue;
						}
						if (inRainbow && prev == '§' && (isColor(c) || isFormat(c))) {
							if (isFormat(c)) {
								if (c == 'r')
									formats = "§r";
								else
									formats += "§" + c;
								prev = c;
								continue;
							}
							builder.delete(builder.length() - 14, builder.length()); // remove &<random color> string
							inRainbow = false;
						}
					}
					if (c != ' ' && inRainbow)
						if (formats.equals("§r")) {
							builder.append(formats); // add formats
							builder.append(generateColor()); // add random color
							formats = "";
						} else {
							builder.append(generateColor()); // add random color
							builder.append(formats); // add formats
						}
					builder.append(c);
					prev = c;
				}
				return builder.toString();
			}

			private boolean isColor(int charAt) {
				return charAt >= 97 && charAt <= 102 || charAt >= 65 && charAt <= 70 || charAt >= 48 && charAt <= 57;
			}

			private boolean isFormat(int charAt) {
				return charAt >= 107 && charAt <= 111 || charAt == 114;
			}

			@Override
			public String generateColor() {
				if (!Ref.isNewerThan(15)) {
					if (position.get() == chars.length)
						position.set(0);
					return "§" + chars[position.getAndIncrement()];
				}
				StringContainer b = new StringContainer(7).append("#");
				for (int i = 0; i < 6; ++i)
					b.append(characters[StringUtils.random.nextInt(16)]);
				return b.toString();
			}

			@Override
			public String replaceHex(String text) {
				if (!Ref.isNewerThan(15))
					return text;
				String msg = text;
				Matcher match = hex.matcher(msg);
				while (match.find()) {
					String color = match.group();
					StringContainer hex = new StringContainer(14).append("§x");
					for (int i = 1; i < color.length(); ++i)
						hex.append('§').append(Character.toLowerCase(color.charAt(i)));
					msg = msg.replace(color, hex.toString());
				}
				return msg;
			}

			@Override
			public String rainbow(String msg, String fromHex, String toHex, List<String> protectedStrings) {
				if (Ref.isNewerThan(15)) // Hex
					return API.basics().rainbow(msg, fromHex, toHex, protectedStrings);
				return gradient(msg, null, null, protectedStrings);
			}
		};
	}
}
