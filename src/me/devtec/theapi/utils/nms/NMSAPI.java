package me.devtec.theapi.utils.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.ChatMessage;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.nms.datawatcher.DataWatcher;
import me.devtec.theapi.utils.nms.nbt.NBTEdit;
import me.devtec.theapi.utils.reflections.Ref;

public class NMSAPI {

	private static Class<?> enumTitle;
	private static Constructor<?> pDestroy, pTimes, pTitle, pSub, pAction, pReset, pOutChat, pTab, pBlock,
			pSpawn, pNSpawn, pLSpawn, pTeleport,
			metadata = Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutEntityMetadata","PacketPlayOutEntityMetadata"), int.class, Ref.nmsOrOld("network.syncher.DataWatcher","DataWatcher"), boolean.class);
	private static Method entityM, livingentity, post, parseNbt = Ref.method(Ref.nmsOrOld("nbt.MojangsonParser","MojangsonParser"), "parse", String.class),
	setNbt,
	asNms=Ref.method(Ref.craft("inventory.CraftItemStack"), "asNMSCopy", ItemStack.class), getNbt,
	asBukkit=Ref.method(Ref.craft("inventory.CraftItemStack"), "asBukkitCopy", Ref.nmsOrOld("world.item.ItemStack","ItemStack"));
	
	private static int old;
	private static Field tps;
	private static Object sbremove, sbinteger, sbchange, sbhearts, empty, server;
	private static Field[] scr = new Field[4];
	
	static {
		setNbt=Ref.method(Ref.nmsOrOld("world.item.ItemStack","ItemStack"), "setTag", Ref.nmsOrOld("nbt.NBTTagCompound","NBTTagCompound"));
		getNbt=Ref.method(Ref.nmsOrOld("world.item.ItemStack","ItemStack"), "getTag");
		scr[0] = Ref.field(Ref.nmsOrOld("network.protocol.game.PacketPlayOutScoreboardScore","PacketPlayOutScoreboardScore"), "a");
		scr[1] = Ref.field(Ref.nmsOrOld("network.protocol.game.PacketPlayOutScoreboardScore","PacketPlayOutScoreboardScore"), "b");
		scr[2] = Ref.field(Ref.nmsOrOld("network.protocol.game.PacketPlayOutScoreboardScore","PacketPlayOutScoreboardScore"), "c");
		scr[3] = Ref.field(Ref.nmsOrOld("network.protocol.game.PacketPlayOutScoreboardScore","PacketPlayOutScoreboardScore"), "d");
		pTeleport = Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutEntityTeleport","PacketPlayOutEntityTeleport"), Ref.nmsOrOld("world.entity.Entity","Entity"));
		server = Ref.invokeStatic(Ref.nmsOrOld("server.MinecraftServer","MinecraftServer"), "getServer");
		if(server==null)server=Ref.invoke(Ref.cast(Ref.craft("CraftServer"), Bukkit.getServer()),"getServer");
		sbremove = Ref.getNulled(Ref.field(Ref.nmsOrOld("network.protocol.game.PacketPlayOutScoreboardScore$EnumScoreboardAction","PacketPlayOutScoreboardScore$EnumScoreboardAction"), "REMOVE"));
		if (sbremove == null)
			sbremove = Ref.getNulled(Ref.field(Ref.nmsOrOld("server.ScoreboardServer$Action","ScoreboardServer$Action"), TheAPI.isNewerThan(16)?"b":"REMOVE"));
		sbchange = Ref.getNulled(Ref.field(Ref.nmsOrOld("network.protocol.game.PacketPlayOutScoreboardScore$EnumScoreboardAction","PacketPlayOutScoreboardScore$EnumScoreboardAction"), "CHANGE"));
		if (sbchange == null)
			sbchange = Ref.getNulled(Ref.field(Ref.nmsOrOld("server.ScoreboardServer$Action","ScoreboardServer$Action"), TheAPI.isNewerThan(16)?"a":"CHANGE"));
		sbinteger = Ref.getNulled(Ref.field(Ref.nmsOrOld("world.scores.criteria.IScoreboardCriteria$EnumScoreboardHealthDisplay","IScoreboardCriteria$EnumScoreboardHealthDisplay"), TheAPI.isNewerThan(16)?"a":"INTEGER"));
		sbhearts = Ref.getNulled(Ref.field(Ref.nmsOrOld("world.scores.criteria.IScoreboardCriteria$EnumScoreboardHealthDisplay","IScoreboardCriteria$EnumScoreboardHealthDisplay"), TheAPI.isNewerThan(16)?"b":"HEARTS"));
		if (TheAPI.isNewVersion())
			post = Ref.method(Ref.nmsOrOld("util.thread.IAsyncTaskHandler","IAsyncTaskHandler"), "executeSync", Runnable.class);
		if (post == null)
			post = Ref.method(Ref.nms("MinecraftServer"), "executeSync", Runnable.class);
		if (post == null)
			post = Ref.method(Ref.nms("MinecraftServer"), "postToMainThread", Runnable.class);
		enumTitle = Ref.nms("PacketPlayOutTitle$EnumTitleAction");
		if(enumTitle==null && TheAPI.isNewerThan(16)) { //1.17+
			pTimes = Ref.constructor(Ref.nmsOrOld("network.protocol.game.ClientboundSetTitlesAnimationPacket", null), int.class, int.class, int.class);
			pTitle = Ref.constructor(Ref.nmsOrOld("network.protocol.game.ClientboundSetTitleTextPacket", null), Ref.nmsOrOld("network.chat.IChatBaseComponent","IChatBaseComponent"));
			pSub = Ref.constructor(Ref.nmsOrOld("network.protocol.game.ClientboundSetSubtitleTextPacket", null), Ref.nmsOrOld("network.chat.IChatBaseComponent","IChatBaseComponent"));
			pAction = Ref.constructor(Ref.nmsOrOld("network.protocol.game.ClientboundSetActionBarTextPacket", null), Ref.nmsOrOld("network.chat.IChatBaseComponent","IChatBaseComponent"));
			pReset = Ref.constructor(Ref.nmsOrOld("network.protocol.game.ClientboundClearTitlesPacket", null), boolean.class);
		}else {
			pTitle = Ref.constructor(Ref.nms("PacketPlayOutTitle"), enumTitle, Ref.nmsOrOld("network.chat.IChatBaseComponent","IChatBaseComponent"), int.class, int.class, int.class);
		}
		pDestroy = Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutEntityDestroy","PacketPlayOutEntityDestroy"), int[].class);
		if(pDestroy==null)
			pDestroy = Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutEntityDestroy","PacketPlayOutEntityDestroy"), int.class);
		pSpawn = Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutSpawnEntity","PacketPlayOutSpawnEntity"), Ref.nmsOrOld("world.entity.Entity","Entity"), int.class);
		pNSpawn = Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutNamedEntitySpawn","PacketPlayOutNamedEntitySpawn"), Ref.nmsOrOld("world.entity.player.EntityHuman","EntityHuman"));
		pLSpawn = Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutSpawnEntityLiving","PacketPlayOutSpawnEntityLiving"), Ref.nmsOrOld("world.entity.EntityLiving","EntityLiving"));
		entityM = Ref.method(Ref.craft("entity.CraftEntity"), "getHandle");
		livingentity = Ref.method(Ref.craft("entity.CraftLivingEntity"), "getHandle");
		
		pOutChat = Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutChat","PacketPlayOutChat"), Ref.nmsOrOld("network.chat.IChatBaseComponent","IChatBaseComponent"),
				Ref.nmsOrOld("network.chat.ChatMessageType","ChatMessageType"));
		if (pOutChat == null) {
			old = 1;
			pOutChat = Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutChat","PacketPlayOutChat"), Ref.nmsOrOld("network.chat.IChatBaseComponent","IChatBaseComponent"),
					Ref.nmsOrOld("network.chat.ChatMessageType","ChatMessageType"), UUID.class);
		}
		if (pOutChat == null) {
			old = 2;
			pOutChat = Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutChat","PacketPlayOutChat"), Ref.nmsOrOld("network.chat.IChatBaseComponent","IChatBaseComponent"), byte.class);
		}
		if (TheAPI.isNewerThan(7))
			try {
				pTab=Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutPlayerListHeaderFooter","PacketPlayOutPlayerListHeaderFooter"));
				if(pTab==null) { //1.17
					pTab=Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutPlayerListHeaderFooter","PacketPlayOutPlayerListHeaderFooter"),Ref.nmsOrOld("network.chat.IChatBaseComponent","IChatBaseComponent"),Ref.nmsOrOld("network.chat.IChatBaseComponent","IChatBaseComponent"));
				}
			} catch (Exception e) {
			}
		pBlock = Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutBlockChange","PacketPlayOutBlockChange"), Ref.nmsOrOld("world.level.IBlockAccess","IBlockAccess"),
				Ref.nmsOrOld("core.BlockPosition","BlockPosition"));
		if (pBlock == null)
			pBlock = Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutBlockChange","PacketPlayOutBlockChange"), Ref.nmsOrOld("world.level.World","World"), Ref.nmsOrOld("core.BlockPosition","BlockPosition"));
		if (pBlock == null)
			pBlock = Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutBlockChange","PacketPlayOutBlockChange"), int.class, int.class, int.class, Ref.nmsOrOld("world.level.World","World"));
		tps = Ref.field(Ref.nmsOrOld("server.MinecraftServer","MinecraftServer"), "recentTps");
		empty = Ref.IChatBaseComponent("");
	}

	public static enum Action {
		CHANGE, REMOVE
	}

	public static enum DisplayType {
		INTEGER, HEARTS
	}
	
	//ItemStack utils
	public static Object getNBT(ItemStack stack) {
		return Ref.invoke(asNMSItem(stack), getNbt);
	}

	public static Object getNBT(Object stack) {
		return Ref.invoke(stack instanceof ItemStack?asNMSItem((ItemStack)stack):stack, getNbt);
	}
	
	public static Object parseNBT(String json) {
		return Ref.invokeNulled(parseNbt, json);
	}
	
	public static ItemStack setNBT(ItemStack stack, String nbt) {
		return setNBT(stack, parseNBT(nbt));
	}
	
	public static ItemStack setNBT(ItemStack stack, NBTEdit nbt) {
		return setNBT(stack,nbt.getNBT());
	}
	
	public static ItemStack setNBT(ItemStack stack, Object nbt) {
		Object nmsOrOld = asNMSItem(stack);
		Ref.invoke(nmsOrOld, setNbt, nbt instanceof String?parseNBT((String)nbt):(nbt instanceof NBTEdit?((NBTEdit) nbt).getNBT():nbt));
		Ref.set(stack, "handle", nmsOrOld);
		return asBukkitItem(nmsOrOld);
	}
	
	public static Object setNBT(Object stack, Object nbt) {
		if(stack instanceof ItemStack)return setNBT((ItemStack)stack, nbt);
		Ref.invoke(stack, setNbt, nbt);
		return stack;
	}
	
	public static Object asNMSItem(ItemStack stack) {
		Object nmsOrOld = Ref.get(stack,"handle");
		if(nmsOrOld!=null)return nmsOrOld;
		Ref.set(stack, "handle", nmsOrOld=Ref.invokeNulled(asNms, stack));
		return nmsOrOld;
	}
	
	public static ItemStack asBukkitItem(Object stack) {
		return stack instanceof ItemStack ? (ItemStack) stack : (ItemStack) Ref.invokeNulled(asBukkit, stack);
	}
	
	public static Object getPacketPlayOutEntityMetadata(Entity entity) {
		Object o = getEntity(entity);
		return getPacketPlayOutEntityMetadata(entity.getEntityId(), Ref.invoke(o, Ref.method(o.getClass(), "getDataWatcher")), true);
	}

	// Entity
	public static Object getPacketPlayOutEntityMetadata(Object entity) {
		return Ref.newInstance(metadata, (int) Ref.invoke(entity, "getId"), Ref.invoke(entity, "getDataWatcher"), true);
	}

	public static Object getPacketPlayOutEntityMetadata(Entity entity, DataWatcher dataWatcher) {
		return Ref.newInstance(metadata, entity.getEntityId(), dataWatcher.getDataWatcher(), true);
	}

	public static Object getPacketPlayOutEntityMetadata(int entityId, DataWatcher dataWatcher) {
		return Ref.newInstance(metadata, entityId, dataWatcher.getDataWatcher(), true);
	}

	// EntityId, DataWatcher
	public static Object getPacketPlayOutEntityMetadata(int entityId, Object dataWatcher) {
		return Ref.newInstance(metadata, entityId, dataWatcher, true);
	}

	// EntityId, DataWatcher, Boolean
	public static Object getPacketPlayOutEntityMetadata(int entityId, Object dataWatcher, boolean bal) {
		return Ref.newInstance(metadata, entityId, dataWatcher, bal);
	}

	private static sun.misc.Unsafe unsafe = (sun.misc.Unsafe) Ref.getNulled(Ref.field(sun.misc.Unsafe.class, "theUnsafe"));
	
	public static Object getPacketPlayOutScoreboardObjective() {
		try {
			return unsafe.allocateInstance(Ref.nmsOrOld("network.protocol.game.PacketPlayOutScoreboardObjective","PacketPlayOutScoreboardObjective"));
		}catch(Exception err) {
			return null;
		}
	}

	public static Object getPacketPlayOutScoreboardDisplayObjective() {
		try {
			return unsafe.allocateInstance(Ref.nmsOrOld("network.protocol.game.PacketPlayOutScoreboardDisplayObjective","PacketPlayOutScoreboardDisplayObjective"));
		} catch (InstantiationException e) {
		}
		return null;
	}
	
	public static Object getPacketPlayOutEntityTeleport(Object entity) {
		return Ref.newInstance(pTeleport, entity);
	}

	public static Object getPacketPlayOutScoreboardScore(Action action, String player, String line, int score) {
		try {
			Object o = unsafe.allocateInstance(Ref.nmsOrOld("network.protocol.game.PacketPlayOutScoreboardScore","PacketPlayOutScoreboardScore"));
			Ref.set(o, scr[0], line);
			Ref.set(o, scr[1], player);
			Ref.set(o, scr[2], score);
			Ref.set(o, scr[3], getScoreboardAction(action)!=null?getScoreboardAction(action):(action==Action.REMOVE?1:0));
			return o;
		}catch(Exception err) {
			return null;
		}
	}

	public static Object getScoreboardAction(Action type) {
		return type == Action.CHANGE ? sbchange : sbremove;
	}

	public static Object getEnumScoreboardHealthDisplay(DisplayType type) {
		return type == DisplayType.HEARTS ? sbhearts : sbinteger;
	}
	
	public static Object getPacketPlayOutScoreboardTeam() {
		try {
			return unsafe.allocateInstance(Ref.nmsOrOld("network.protocol.game.PacketPlayOutScoreboardTeam","PacketPlayOutScoreboardTeam"));
		} catch (InstantiationException e) {
		}
		return null;
	}

	private static Method poost = Ref.method(Ref.nmsOrOld("server.MinecraftServer","MinecraftServer"), "postToMainThread", Runnable.class);
	
	@SuppressWarnings("unchecked")
	public static void postToMainThread(Runnable runnable) {
		if(Thread.currentThread()==thread) {
			runnable.run();
		}else {
			if(server instanceof Executor) {
				CompletableFuture.supplyAsync(() -> {
					runnable.run();
					return null;
				}, (Executor)server).join();
			}else {
				if(!TheAPI.isOlderThan(8)) {
					Ref.invoke(server, poost, runnable);
				}else {
					((Queue<Runnable>)Ref.get(server, "processQueue")).add(runnable);
				}
			}
		}
	}

	public static Object getServer() {
		return server;
	}

	public static double[] getServerTPS() {
		return (double[]) Ref.get(getServer(), tps);
	}

	public static enum TitleAction {
		ACTIONBAR, CLEAR, RESET, SUBTITLE, TITLE, TIMES
	}

	public static enum ChatType {
		CHAT, GAME_INFO, SYSTEM
	}

	public static Object getPacketPlayOutTitle(TitleAction action, Object IChatBaseComponent) {
		return getPacketPlayOutTitle(action, IChatBaseComponent, 10, 20, 10);
	}

	public static Object getPacketPlayOutTitle(TitleAction action, Object IChatBaseComponent, int fadeIn, int stay, int fadeOut) {
		if(TheAPI.isNewerThan(16)) {
			switch(action) {
			case TIMES:
				return Ref.newInstance(pTimes, fadeIn, stay, fadeOut);
			case ACTIONBAR:
				return Ref.newInstance(pAction, IChatBaseComponent);
			case SUBTITLE:
				return Ref.newInstance(pSub, IChatBaseComponent);
			case TITLE:
				return Ref.newInstance(pTitle, IChatBaseComponent);
			case CLEAR:
			case RESET:
				return Ref.newInstance(pReset, true);
			}
		}
		if(action==TitleAction.TIMES)action=TitleAction.TITLE;
		if (action == TitleAction.ACTIONBAR) {
			Object tt=Ref.getNulled(Ref.field(enumTitle, action.name()));
			Object o = Ref.newInstance(pTitle, tt, IChatBaseComponent,
					fadeIn, stay, fadeOut);
			return tt!=null&&o != null ? o : Ref.newInstance(pOutChat, IChatBaseComponent, (byte) 2);
		}
		return Ref.newInstance(pTitle, Ref.getNulled(Ref.field(enumTitle, action.name())), IChatBaseComponent, fadeIn, stay, fadeOut);
	}

	public static Object getPacketPlayOutTitle(TitleAction action, String text, int fadeIn, int stay, int fadeOut) {
		return getPacketPlayOutTitle(action, NMSAPI.getFixedIChatBaseComponent(text), fadeIn, stay, fadeOut);
	}

	public static Object getPacketPlayOutTitle(TitleAction action, String text) {
		return getPacketPlayOutTitle(action, NMSAPI.getFixedIChatBaseComponent(text), 10, 20, 10);
	}

	public static Object getPacketPlayOutChat(ChatType type, Object IChatBaseComponent) {
		Object o = old == 2 ? Ref.newInstance(pOutChat, IChatBaseComponent, (byte) 1)
				: (old == 0
						? Ref.newInstance(pOutChat, IChatBaseComponent,
								Ref.getNulled(Ref.field(Ref.nmsOrOld("network.chat.ChatMessageType","ChatMessageType"), type.name())))
						: Ref.newInstance(pOutChat, IChatBaseComponent,
								Ref.getNulled(Ref.field(Ref.nmsOrOld("network.chat.ChatMessageType","ChatMessageType"), type.name())), UUID.randomUUID()));
		return o;
	}

	public static Object getPacketPlayOutChat(ChatType type, String text) {
		return getPacketPlayOutChat(type, NMSAPI.getFixedIChatBaseComponent(text));
	}

	public static Object getPacketPlayOutEntityDestroy(int... id) {
		return Ref.newInstance(pDestroy, TheAPI.isNewerThan(16)?id[0]:id);
	}

	public static Object getEntity(Entity entity) {
		return Ref.invoke(Ref.cast(Ref.craft("entity.CraftEntity"), entity), entityM);
	}

	public static Object getEntityLiving(LivingEntity entity) {
		return Ref.invoke(Ref.cast(Ref.craft("entity.CraftLivingEntity"), entity), livingentity);
	}

	public static Object getPacketPlayOutSpawnEntity(Object entity, int id) {
		return Ref.newInstance(pSpawn, entity, id);
	}

	public static Object getPacketPlayOutNamedEntitySpawn(Object Player) {
		return Ref.newInstance(pNSpawn, Player);
	}

	public static Object getPacketPlayOutSpawnEntityLiving(Object entityLiving) {
		return Ref.newInstance(pLSpawn, entityLiving);
	}

	static Field aField, bField;
	static {
		if(TheAPI.isOlderThan(17)) {
			aField = Ref.field(Ref.nmsOrOld("network.protocol.game.PacketPlayOutPlayerListHeaderFooter","PacketPlayOutPlayerListHeaderFooter"), "a");
			if (aField == null)
				aField = Ref.field(Ref.nmsOrOld("network.protocol.game.PacketPlayOutPlayerListHeaderFooter","PacketPlayOutPlayerListHeaderFooter"), "header");
			bField = Ref.field(Ref.nmsOrOld("network.protocol.game.PacketPlayOutPlayerListHeaderFooter","PacketPlayOutPlayerListHeaderFooter"), "b");
			if (bField == null)
				bField = Ref.field(Ref.nmsOrOld("network.protocol.game.PacketPlayOutPlayerListHeaderFooter","PacketPlayOutPlayerListHeaderFooter"), "footer");
		}
	}
	public static Object getPacketPlayOutPlayerListHeaderFooter(Object h, Object f) {
		if(pTab!=null) {
			if(TheAPI.isNewerThan(16)) {
				return Ref.newInstance(pTab, h==null?empty:h, f==null?empty:f);
			}
			Object packet = Ref.newInstance(pTab);
			Ref.set(packet, aField, h==null?empty:h);
			Ref.set(packet, bField, f==null?empty:f);
			return packet;
		}
		return null;
	}

	public static Object getPacketPlayOutPlayerListHeaderFooter(String header, String footer) {
		return getPacketPlayOutPlayerListHeaderFooter(getFixedIChatBaseComponent(header), getFixedIChatBaseComponent(footer));
	}

	public static Object getPacketPlayOutBlockChange(Object World, Position pos) {
		return TheAPI.isOlderThan(8)?Ref.newInstance(pBlock, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), World):getPacketPlayOutBlockChange(World, pos.getBlockPosition());
	}

	public static Object getPacketPlayOutBlockChange(World world, Object BlockPosition) {
		return getPacketPlayOutBlockChange(Ref.world(world), BlockPosition);
	}

	public static Object getPacketPlayOutBlockChange(World world, int x, int y, int z) {
		return TheAPI.isOlderThan(8)?Ref.newInstance(pBlock, x, y, z, Ref.world(world)):getPacketPlayOutBlockChange(Ref.world(world), new Position(world.getName(), x, y, z).getBlockPosition());
	}

	public static Object getPacketPlayOutBlockChange(Object World, Object BlockPosition) {
		return Ref.newInstance(pBlock, World, BlockPosition);
	}
	
	@SuppressWarnings("unchecked")
	public static String fromComponent(Object o) {
		if (o == null)
			return "";
		StringBuilder out = new StringBuilder();
		boolean hadFormat = false;
		for (Object c : (Iterable<Object>)o) {
			Object modi = Ref.invoke(c, "getChatModifier");
			Object color = Ref.invoke(modi, "getColor");
			if (!((String)Ref.invoke(c, "getText")).isEmpty() || color != null) {
				if (color != null) {
					if (Ref.get(color, "format") != null) {
						out.append(""+Ref.get(color, "format"));
						hadFormat = true;
					} else if(TheAPI.isNewerThan(15)){
						out.append("§x");
						char[] arrc = ((String)Ref.invoke(color, "b")).substring(1).toCharArray();
						int n = arrc.length;
						int n2 = 0;
						while (n2 < n) {
							char magic = arrc[n2];
							out.append("§").append(magic);
							++n2;
						}
						hadFormat = true;
					}else
					hadFormat = false;
				} else if (hadFormat) {
					out.append(""+ChatColor.RESET);
					hadFormat = false;
				}
			}
			if ((boolean)Ref.invoke(modi, "isBold")) {
				out.append(""+ChatColor.BOLD);
				hadFormat = true;
			}
			if ((boolean)Ref.invoke(modi, "isItalic")) {
				out.append(""+ChatColor.ITALIC);
				hadFormat = true;
			}
			if ((boolean)Ref.invoke(modi, "isUnderlined")) {
				out.append(""+ChatColor.UNDERLINE);
				hadFormat = true;
			}
			if ((boolean)Ref.invoke(modi, "isStrikethrough")) {
				out.append(""+ChatColor.STRIKETHROUGH);
				hadFormat = true;
			}
			if ((boolean)Ref.invoke(modi, "isRandom")) {
				out.append(""+ChatColor.MAGIC);
				hadFormat = true;
			}
			out.append(""+Ref.invoke(c, "getText"));
		}
		return out.toString();
	}
	
	public static Object getFixedIChatBaseComponent(String text) {
		if(text==null||text.equals(""))return empty;
		return new ChatMessage(text).toNMS();
	}

	public static Object getIChatBaseComponentText(String text) {
		if(text==null||text.equals(""))return empty;
		return Ref.IChatBaseComponent(text);
	}
	private static int oo;
	private static Method mm = Ref.method(Ref.craft("util.CraftChatMessage"), "fromStringOrNull", String.class, boolean.class);
	static {
		if(mm==null) {
			oo=1;
			mm = Ref.method(Ref.craft("util.CraftChatMessage"), "fromString", String.class, boolean.class);
			if(mm==null) {
				oo=2;
				mm = Ref.method(Ref.craft("util.CraftChatMessage"), "fromString", String.class);
			}
		}
	}
	
	public static Object getIChatBaseComponentFromCraftBukkit(String text) {
		if(text==null||text.equals(""))return empty;
		return oo==0?Ref.invokeNulled(mm, text, true):((Object[])(oo==1?Ref.invokeNulled(mm, text, true):Ref.invokeNulled(mm, text)))[0];
	}

	public static Object getIChatBaseComponentJson(String json) {
		return Ref.IChatBaseComponentJson(json);
	}

	private static Thread thread;
	static {
		Object o = Ref.get(getServer(), Ref.field(Ref.nmsOrOld("server.MinecraftServer","MinecraftServer"), "primaryThread"));
		thread= o != null ? (Thread) o
				: (Thread) Ref.get(getServer(), Ref.field(Ref.nmsOrOld("server.MinecraftServer","MinecraftServer"), "serverThread"));
	}
	
	public static Thread getServerThread() {
		return thread;
	}
}
