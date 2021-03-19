package me.devtec.theapi.utils.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

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
	private static Constructor<?> pDestroy, pTitle, pOutChat, pTab, pBlock,
			pSpawn, pNSpawn, pLSpawn, score, sbobj, sbdisplayobj, sbteam, pTeleport,nbt=Ref.constructor(Ref.nms("NBTTagCompound")),
			metadata = Ref.constructor(Ref.nms("PacketPlayOutEntityMetadata"), int.class, Ref.nms("DataWatcher"), boolean.class);
	private static Method entityM, livingentity, oldichatser, post, notify,nofifyManual,
	parseNbt = Ref.method(Ref.nms("MojangsonParser"), "parse", String.class),
	getNbt=Ref.method(Ref.nms("ItemStack"), "getOrCreateTag"), setNbt=Ref.method(Ref.nms("ItemStack"), "setTag", Ref.nms("NBTTagCompound")),
	asNms=Ref.method(Ref.craft("inventory.CraftItemStack"), "asNMSCopy", ItemStack.class), 
	asBukkit=Ref.method(Ref.craft("inventory.CraftItemStack"), "asBukkitCopy", Ref.nms("ItemStack"));
	
	private static int old, not;
	private static Field tps,getMap,getProvider;
	private static Object sbremove, sbinteger, sbchange, sbhearts, empty, server;
	private static Field[] scr = new Field[4];
	static {
		if(getNbt==null)
			getNbt=Ref.method(Ref.nms("ItemStack"), "getTag");
		scr[0] = Ref.field(Ref.nms("PacketPlayOutScoreboardScore"), "a");
		scr[1] = Ref.field(Ref.nms("PacketPlayOutScoreboardScore"), "b");
		scr[2] = Ref.field(Ref.nms("PacketPlayOutScoreboardScore"), "c");
		scr[3] = Ref.field(Ref.nms("PacketPlayOutScoreboardScore"), "d");
		pTeleport = Ref.constructor(Ref.nms("PacketPlayOutEntityTeleport"), Ref.nms("Entity"));
		server = Ref.invokeStatic(Ref.nms("MinecraftServer"), "getServer");
		sbteam = Ref.constructor(Ref.nms("PacketPlayOutScoreboardTeam"));
		sbdisplayobj = Ref.constructor(Ref.nms("PacketPlayOutScoreboardDisplayObjective"));
		sbobj = Ref.constructor(Ref.nms("PacketPlayOutScoreboardObjective"));
		score = Ref.constructor(Ref.nms("PacketPlayOutScoreboardScore"));
		sbremove = Ref.getNulled(Ref.field(Ref.nms("PacketPlayOutScoreboardScore$EnumScoreboardAction"), "REMOVE"));
		if (sbremove == null)
			sbremove = Ref.getNulled(Ref.field(Ref.nms("ScoreboardServer$Action"), "REMOVE"));
		sbchange = Ref.getNulled(Ref.field(Ref.nms("PacketPlayOutScoreboardScore$EnumScoreboardAction"), "CHANGE"));
		if (sbchange == null)
			sbchange = Ref.getNulled(Ref.field(Ref.nms("ScoreboardServer$Action"), "CHANGE"));
		sbinteger = Ref.getNulled(Ref.field(Ref.nms("IScoreboardCriteria$EnumScoreboardHealthDisplay"), "INTEGER"));
		sbhearts = Ref.getNulled(Ref.field(Ref.nms("IScoreboardCriteria$EnumScoreboardHealthDisplay"), "HEARTS"));
		if (TheAPI.isNewVersion())
			post = Ref.method(Ref.nms("IAsyncTaskHandler"), "executeSync", Runnable.class);
		if (post == null)
			post = Ref.method(Ref.nms("MinecraftServer"), "executeSync", Runnable.class);
		if (post == null)
			post = Ref.method(Ref.nms("MinecraftServer"), "postToMainThread", Runnable.class);
		if (Ref.nms("PacketPlayOutTitle") != null)
			enumTitle = Ref.nms("PacketPlayOutTitle$EnumTitleAction");
		oldichatser = Ref.method(Ref.nms("IChatBaseComponent$ChatSerializer"), "a", String.class);
		if(oldichatser==null)
			oldichatser = Ref.method(Ref.nms("ChatSerializer"), "a", String.class);
		pDestroy = Ref.constructor(Ref.nms("PacketPlayOutEntityDestroy"), int[].class);
		pSpawn = Ref.constructor(Ref.nms("PacketPlayOutSpawnEntity"), Ref.nms("Entity"), int.class);
		pNSpawn = Ref.constructor(Ref.nms("PacketPlayOutNamedEntitySpawn"), Ref.nms("EntityHuman"));
		pLSpawn = Ref.constructor(Ref.nms("PacketPlayOutSpawnEntityLiving"), Ref.nms("EntityLiving"));
		entityM = Ref.method(Ref.craft("entity.CraftEntity"), "getHandle");
		livingentity = Ref.method(Ref.craft("entity.CraftLivingEntity"), "getHandle");
		pTitle = Ref.constructor(Ref.nms("PacketPlayOutTitle"), enumTitle, Ref.nms("IChatBaseComponent"), int.class,
				int.class, int.class);
		pOutChat = Ref.constructor(Ref.nms("PacketPlayOutChat"), Ref.nms("IChatBaseComponent"),
				Ref.nms("ChatMessageType"));
		if (pOutChat == null) {
			old = 1;
			pOutChat = Ref.constructor(Ref.nms("PacketPlayOutChat"), Ref.nms("IChatBaseComponent"),
					Ref.nms("ChatMessageType"), UUID.class);
		}
		if (pOutChat == null) {
			old = 2;
			pOutChat = Ref.constructor(Ref.nms("PacketPlayOutChat"), Ref.nms("IChatBaseComponent"), byte.class);
		}
		if (TheAPI.isNewerThan(7))
			try {
				pTab = Ref.nms("PacketPlayOutPlayerListHeaderFooter").getDeclaredConstructor();
			} catch (Exception e) {
			}
		pBlock = Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), Ref.nms("IBlockAccess"),
				Ref.nms("BlockPosition"));
		if (pBlock == null)
			pBlock = Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), Ref.nms("World"), Ref.nms("BlockPosition"));
		if (pBlock == null)
			pBlock = Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), int.class, int.class, int.class, Ref.nms("World"));
		tps = Ref.field(Ref.nms("MinecraftServer"), "recentTps");
		
		getMap = Ref.field(Ref.nms("WorldServer"),"manager");
		if(getMap==null) {
			getProvider = Ref.field(Ref.nms("WorldServer"),"chunkProvider");
			getMap = Ref.field(Ref.nms("ChunkProviderServer"),"playerChunkMap");
		}
		
		notify = Ref.method(Ref.nms("PlayerChunkMap"), "flagDirty", int.class, int.class, int.class); //1.7
		if(notify==null) {
		notify = Ref.method(Ref.nms("PlayerChunkMap"), "a", int.class, int.class, int.class); //1.8
		if(notify==null) {
		notify = Ref.method(Ref.nms("PlayerChunkMap"), "flagDirty", Ref.nms("BlockPosition")); //1.9 - 1.12
		if(notify==null) {
		notify = Ref.method(Ref.nms("PlayerChunkMap"), "a", Ref.nms("BlockPosition")); //1.13
		if(notify==null) {
			not=1;
			notify = Ref.method(Ref.nms("PlayerChunkMap"), "getVisibleChunk", long.class); //1.14 - 1.16
			nofifyManual = Ref.method(Ref.nms("PlayerChunk"), "a", int.class, int.class, int.class);
			if(nofifyManual==null) {
				not=0;
				nofifyManual = Ref.method(Ref.nms("PlayerChunk"), "a", Ref.nms("BlockPosition"));
			}
		}}}}
		empty = getIChatBaseComponentJson("{\"text\":\"\"}");
	}

	public static enum Action {
		CHANGE, REMOVE
	}

	public static enum DisplayType {
		INTEGER, HEARTS
	}
	
	//ItemStack utils
	public static Object getNBT(ItemStack stack) {
		Object n = Ref.invoke(asNMSItem(stack), getNbt);
		if(n==null)
			setNBT(stack, n=Ref.newInstance(nbt));
		return n;
	}

	public static Object getNBT(Object stack) {
		Object n = Ref.invoke(stack instanceof ItemStack?asNMSItem((ItemStack)stack):stack, getNbt);
		if(n==null)
			setNBT(stack, n=Ref.newInstance(nbt));
		return n;
	}
	
	public static Object parseNBT(String json) {
		return Ref.invokeNulled(parseNbt, json);
	}
	
	public static ItemStack setNBT(ItemStack stack, String nbt) {
		return setNBT(stack, parseNBT(nbt));
	}
	
	public static ItemStack setNBT(ItemStack stack, NBTEdit nbt) {
		Object nms = asNMSItem(stack);
		setNBT(nms, nbt.getNBT());
		stack.setItemMeta(asBukkitItem(stack).getItemMeta());
		return stack;
	}
	
	public static ItemStack setNBT(ItemStack stack, Object nbt) {
		Object nms = asNMSItem(stack);
		setNBT(nms, nbt instanceof String?parseNBT((String)nbt):nbt);
		stack.setItemMeta(asBukkitItem(stack).getItemMeta());
		return stack;
	}
	
	public static ItemStack setNBT(Object stack, Object nbt) {
		if(stack instanceof ItemStack)return setNBT((ItemStack)stack, nbt);
		Ref.invoke(stack, setNbt, nbt);
		return asBukkitItem(stack);
	}
	
	public static Object asNMSItem(ItemStack stack) {
		return Ref.invokeNulled(asNms, stack);
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

	public static Object getPacketPlayOutScoreboardObjective() {
		return Ref.newInstance(sbobj);
	}

	public static Object getPacketPlayOutScoreboardDisplayObjective() {
		return Ref.newInstance(sbdisplayobj);
	}

	public static Object getPacketPlayOutEntityTeleport(Object entity) {
		return Ref.newInstance(pTeleport, entity);
	}

	public static Object getPacketPlayOutScoreboardScore(Action action, String player, String line, int score) {
		Object o = Ref.newInstance(NMSAPI.score);
		Ref.set(o, scr[0], line);
		Ref.set(o, scr[1], player);
		Ref.set(o, scr[2], score);
		Ref.set(o, scr[3], getScoreboardAction(action));
		return o;
	}

	public static Object getScoreboardAction(Action type) {
		return type == Action.CHANGE ? sbchange : sbremove;
	}

	public static Object getEnumScoreboardHealthDisplay(DisplayType type) {
		return type == DisplayType.HEARTS ? sbhearts : sbinteger;
	}

	public static Object getPacketPlayOutScoreboardTeam() {
		return Ref.newInstance(sbteam);
	}

	public static void postToMainThread(Runnable runnable) {
		if(Thread.currentThread()==thread) {
			runnable.run();
		}else
		CompletableFuture.supplyAsync(() -> {
			runnable.run();
			return null;
		}, (Executor)server).join();
	}

	public static Object getServer() {
		return server;
	}

	public static double[] getServerTPS() {
		return (double[]) Ref.get(getServer(), tps);
	}

	public static enum TitleAction {
		ACTIONBAR, CLEAR, RESET, SUBTITLE, TITLE
	}

	public static enum ChatType {
		CHAT, GAME_INFO, SYSTEM
	}

	public static Object getPacketPlayOutTitle(TitleAction action, Object IChatBaseComponent) {
		return getPacketPlayOutTitle(action, IChatBaseComponent, 10, 20, 10);
	}

	public static Object getPacketPlayOutTitle(TitleAction action, Object IChatBaseComponent, int fadeIn, int stay, int fadeOut) {
		if (action == TitleAction.ACTIONBAR) {
			Object tt=Ref.getNulled(Ref.field(enumTitle, action.name()));
			Object o = Ref.newInstance(pTitle, tt, IChatBaseComponent,
					fadeIn, stay, fadeOut);
			return tt!=null&&o != null ? o : Ref.newInstance(pOutChat, IChatBaseComponent, (byte) 2);
		}
		return Ref.newInstance(pTitle, Ref.getNulled(Ref.field(enumTitle, action.name())), IChatBaseComponent, fadeIn, stay, fadeOut);
	}

	public static Object getPacketPlayOutTitle(TitleAction action, String text, int fadeIn, int stay, int fadeOut) {
		return getPacketPlayOutTitle(action, NMSAPI.getIChatBaseComponentJson(new ChatMessage(text).getJson()), fadeIn, stay, fadeOut);
	}

	public static Object getPacketPlayOutTitle(TitleAction action, String text) {
		return getPacketPlayOutTitle(action, NMSAPI.getIChatBaseComponentJson(new ChatMessage(text).getJson()), 10, 20, 10);
	}

	public static void refleshBlock(Position pos, Object oldBlock) {
		if (TheAPI.isOlderThan(9)) { //1.7 - 1.8
			Ref.invoke(Ref.get(Ref.world(pos.getWorld()), getMap), notify, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
		}else
		if (TheAPI.isOlderThan(14)) { //1.9 - 1.13
			Ref.invoke(Ref.get(Ref.world(pos.getWorld()), getMap), notify, pos.getBlockPosition());
		}else { //1.14+ - manually
			Ref.invoke(Ref.get(Ref.get(Ref.world(pos.getWorld()), getProvider), getMap), notify, not==0?pos.getBlockPosition():pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
		}
	}

	public static Object getPacketPlayOutChat(ChatType type, Object IChatBaseComponent) {
		Object o = old == 2 ? Ref.newInstance(pOutChat, IChatBaseComponent, (byte) 1)
				: (old == 0
						? Ref.newInstance(pOutChat, IChatBaseComponent,
								Ref.getNulled(Ref.field(Ref.nms("ChatMessageType"), type.name())))
						: Ref.newInstance(pOutChat, IChatBaseComponent,
								Ref.getNulled(Ref.field(Ref.nms("ChatMessageType"), type.name())), UUID.randomUUID()));
		return o;
	}

	public static Object getPacketPlayOutChat(ChatType type, String text) {
		return getPacketPlayOutChat(type, NMSAPI.getIChatBaseComponentJson(new ChatMessage(text).getJson()));
	}

	public static Object getPacketPlayOutEntityDestroy(int... id) {
		return Ref.newInstance(pDestroy, id);
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
		aField = Ref.field(Ref.nms("PacketPlayOutPlayerListHeaderFooter"), "a");
		if (aField == null)
			aField = Ref.field(Ref.nms("PacketPlayOutPlayerListHeaderFooter"), "header");
		bField = Ref.field(Ref.nms("PacketPlayOutPlayerListHeaderFooter"), "b");
		if (bField == null)
			bField = Ref.field(Ref.nms("PacketPlayOutPlayerListHeaderFooter"), "footer");
	}
	public static Object getPacketPlayOutPlayerListHeaderFooter(Object headerIChatBaseComponent, Object footerIChatBaseComponent) {
		if(pTab!=null) {
			Object packet = Ref.newInstance(pTab);
			Ref.set(packet, aField, headerIChatBaseComponent==null?empty:headerIChatBaseComponent);
			Ref.set(packet, bField, footerIChatBaseComponent==null?empty:footerIChatBaseComponent);
			return packet;
		}
		return null;
	}

	public static Object getPacketPlayOutPlayerListHeaderFooter(String header, String footer) {
		return getPacketPlayOutPlayerListHeaderFooter(getIChatBaseComponentJson(new ChatMessage(header).getJson()), getIChatBaseComponentJson(new ChatMessage(footer).getJson()));
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

	public static Object getIChatBaseComponentText(String text) {
		if(text==null||text.equals(""))return empty;
		return getIChatBaseComponentJson("{\"text\":\"" + text + "\"}");
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
		return Ref.invokeNulled(oldichatser, json);
	}

	private static Thread thread;
	static {
		Object o = Ref.get(getServer(), Ref.field(Ref.nms("MinecraftServer"), "primaryThread"));
		thread= o != null ? (Thread) o
				: (Thread) Ref.get(getServer(), Ref.field(Ref.nms("MinecraftServer"), "serverThread"));
	}
	
	public static Thread getServerThread() {
		return thread;
	}
}
