package me.DevTec.TheAPI.Utils.NMS;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.Position;
import me.DevTec.TheAPI.Utils.NMS.DataWatcher.DataWatcher;
import me.DevTec.TheAPI.Utils.Reflections.Ref;

public class NMSAPI {

	private static Class<?> enumTitle;
	private static Constructor<?> pDestroy, pTitle, pOutChat, pTab, pBlock, blockPos, pChunk, ChunkSection, chunkc,
			pSpawn, pNSpawn, pLSpawn, score, sbobj, sbdisplayobj, sbteam, pSign, pTeleport,
			metadata = Ref.constructor(Ref.nms("PacketPlayOutEntityMetadata"), int.class,
					Ref.nms("DataWatcher"), boolean.class);
	private static Method getmat, getb, getc, gett, getser, block, IBlockData, worldset, Chunk,
			getblocks, setblock, setblockb, itemstack, entityM, livingentity, oldichatser, post, notify;
	private static int old;
	private static Field tps;
	private static Object sbremove, sbinteger, sbchange, sbhearts;
	private static Field[] scr = new Field[4];
	static {
		scr[0] = Ref.field(Ref.nms("PacketPlayOutScoreboardScore"), "a");
		scr[1] = Ref.field(Ref.nms("PacketPlayOutScoreboardScore"), "b");
		scr[2] = Ref.field(Ref.nms("PacketPlayOutScoreboardScore"), "c");
		scr[3] = Ref.field(Ref.nms("PacketPlayOutScoreboardScore"), "d");
		pTeleport = Ref.constructor(Ref.nms("PacketPlayOutEntityTeleport"), Ref.nms("Entity"));
		pSign = Ref.constructor(Ref.nms("PacketPlayOutOpenSignEditor"), Ref.nms("BlockPosition"));
		getser = Ref.method(Ref.nms("MinecraftServer"), "getServer");
		sbteam = Ref.constructor(Ref.nms("PacketPlayOutScoreboardTeam"));
		sbdisplayobj = Ref.constructor(Ref.nms("PacketPlayOutScoreboardDisplayObjective"));
		sbobj = Ref.constructor(Ref.nms("PacketPlayOutScoreboardObjective"));
		score = Ref.constructor(Ref.nms("PacketPlayOutScoreboardScore"));
		sbremove = Ref.getNulled(Ref.field(
				Ref.nms("PacketPlayOutScoreboardScore$EnumScoreboardAction"), "REMOVE"));
		if (sbremove == null)
			sbremove = Ref
					.getNulled(Ref.field(Ref.nms("ScoreboardServer$Action"), "REMOVE"));
		sbchange = Ref.getNulled(Ref.field(
				Ref.nms("PacketPlayOutScoreboardScore$EnumScoreboardAction"), "CHANGE"));
		if (sbchange == null)
			sbchange = Ref
					.getNulled(Ref.field(Ref.nms("ScoreboardServer$Action"), "CHANGE"));
		sbinteger = Ref.getNulled(Ref
				.field(Ref.nms("IScoreboardCriteria$EnumScoreboardHealthDisplay"), "INTEGER"));
		sbhearts = Ref.getNulled(Ref
				.field(Ref.nms("IScoreboardCriteria$EnumScoreboardHealthDisplay"), "HEARTS"));
		if (TheAPI.isNewVersion())
			post = Ref.method(Ref.nms("IAsyncTaskHandler"), "executeSync", Runnable.class);
		if (post == null)
			post = Ref.method(Ref.nms("MinecraftServer"), "executeSync", Runnable.class);
		if (post == null)
			post = Ref.method(Ref.nms("MinecraftServer"), "postToMainThread",
					Runnable.class);
		if (Ref.nms("PacketPlayOutTitle") != null)
			enumTitle = Ref.nms("PacketPlayOutTitle$EnumTitleAction");
		try {
			oldichatser = Ref.method(Ref.nms("IChatBaseComponent$ChatSerializer"), "a", String.class);
		} catch (Exception oldversion) {
			oldichatser = Ref.method(Ref.nms("ChatSerializer"), "a", String.class);
		}
		pDestroy = Ref.constructor(Ref.nms("PacketPlayOutEntityDestroy"), int[].class);
		pSpawn = Ref.constructor(Ref.nms("PacketPlayOutSpawnEntity"),
				Ref.nms("Entity"), int.class);
		pNSpawn = Ref.constructor(Ref.nms("PacketPlayOutNamedEntitySpawn"),
				Ref.nms("EntityHuman"));
		pLSpawn = Ref.constructor(Ref.nms("PacketPlayOutSpawnEntityLiving"),
				Ref.nms("EntityLiving"));
		entityM = Ref.method(Ref.craft("entity.CraftEntity"), "getHandle");
		livingentity = Ref.method(Ref.craft("entity.CraftLivingEntity"), "getHandle");
		itemstack = Ref.method(Ref.craft("inventory.CraftItemStack"), "asNMSCopy",
				ItemStack.class);
		getblocks = Ref.method(Ref.nms("ChunkSection"), "getBlocks");
		setblock = Ref.method(Ref.nms("DataPaletteBlock"), "setBlock", int.class, int.class,
				int.class, Object.class);
		if (setblock == null)
			setblock = Ref.method(Ref.nms("DataPaletteBlock"), "setBlock", int.class,
					int.class, int.class, Ref.nms("IBlockData"));
		setblockb = Ref.method(Ref.nms("DataPaletteBlock"), "b", int.class, int.class,
				int.class, Object.class);
		chunkc = Ref.constructor(Ref.nms("ChunkCoordIntPair"), int.class, int.class);
		setblockb = Ref.method(Ref.nms("MinecraftServer"), "getServer");
		ChunkSection = Ref.constructor(Ref.nms("ChunkSection"), int.class);
		if (ChunkSection == null)
			ChunkSection = Ref.constructor(Ref.nms("ChunkSection"), int.class,
					boolean.class);
		Chunk = Ref.method(Ref.craft("CraftChunk"), "getHandle");
		getmat = Ref.method(Ref.nms("IBlockData"), "getMaterial");
		getb = Ref.method(Ref.nms("IBlockData"), "getBlock");
		getc = Ref.method(Ref.nms("World"), "getChunkAt", int.class, int.class);
		gett = Ref.method(Ref.nms("World"), "getType", Ref.nms("BlockPosition"));
		pTitle = Ref.constructor(Ref.nms("PacketPlayOutTitle"), enumTitle,
				Ref.nms("IChatBaseComponent"), int.class, int.class, int.class);
		pOutChat = Ref.constructor(Ref.nms("PacketPlayOutChat"),
				Ref.nms("IChatBaseComponent"), Ref.nms("ChatMessageType"));
		if (pOutChat == null) {
			old = 1;
			pOutChat = Ref.constructor(Ref.nms("PacketPlayOutChat"),
					Ref.nms("IChatBaseComponent"), Ref.nms("ChatMessageType"), UUID.class);
		}
		if (pOutChat == null) {
			old = 2;
			pOutChat = Ref.constructor(Ref.nms("PacketPlayOutChat"),
					Ref.nms("IChatBaseComponent"), byte.class);
		}
		if (TheAPI.isNewerThan(7))
			pTab = Ref.findConstructor(Ref.nms("PacketPlayOutPlayerListHeaderFooter"));
		pBlock = Ref.constructor(Ref.nms("PacketPlayOutBlockChange"),
				Ref.nms("IBlockAccess"), Ref.nms("BlockPosition"));
		if (pBlock == null)
			pBlock = Ref.constructor(Ref.nms("PacketPlayOutBlockChange"),
					Ref.nms("World"), Ref.nms("BlockPosition"));
		blockPos = Ref.constructor(Ref.nms("BlockPosition"), int.class, int.class,
				int.class);
		tps = Ref.field(Ref.nms("MinecraftServer"), "recentTps");
		block = Ref.method(Ref.nms("Block"), "getById", int.class);
		IBlockData = Ref.method(Ref.nms("Block"), "fromLegacyData", int.class);
		if (IBlockData == null)
			IBlockData = Ref.method(Ref.nms("Block"), "getByCombinedId", int.class);
		worldset = Ref.method(Ref.nms("World"), "setTypeAndData", Ref.nms("BlockPosition"),
				Ref.nms("IBlockData"), int.class);
		pChunk = Ref.constructor(Ref.nms("PacketPlayOutMapChunk"),
				Ref.nms("Chunk"), int.class);
		if (pChunk == null)
			pChunk = Ref.constructor(Ref.nms("PacketPlayOutMapChunk"),
					Ref.nms("Chunk"), boolean.class, int.class);
		notify = Ref.method(Ref.nms("World"), "notify", Ref.nms("BlockPosition"), Ref.nms("IBlockData"),
				Ref.nms("IBlockData"), int.class);
	}

	public static enum Action {
		CHANGE, REMOVE
	}

	public static enum DisplayType {
		INTEGER, HEARTS
	}

	// Entity
	public static Object getPacketPlayOutEntityMetadata(Entity entity) {
		Object o = getEntity(entity);
		return getPacketPlayOutEntityMetadata(entity.getEntityId(),
				Ref.invoke(o, Ref.method(o.getClass(), "getDataWatcher")));
	}

	// Entity
	public static Object getPacketPlayOutEntityMetadata(Object entity) {
		return getPacketPlayOutEntityMetadata((int) Ref.invoke(entity, "getId"),Ref.invoke(entity, "getDataWatcher"));
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

	public static Object getPacketPlayOutScoreboardObjective() {
		return Ref.newInstance(sbobj);
	}

	public static Object getPacketPlayOutScoreboardDisplayObjective() {
		return Ref.newInstance(sbdisplayobj);
	}

	public static Object getPacketPlayOutOpenSignEditor(Object position) {
		return Ref.newInstance(pSign, position);
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

	// return DataPaletteBlock<IBlockData>
	public static Object getChunkSectionBlocks(Object ChunkSection) {
		return Ref.invoke(ChunkSection, getblocks);
	}

	public static void setChunkSectionsBlocks(int chunksection, int x, int y, int z, Object IBlockData) {
		setChunkSectionsBlocks(getChunkSection(chunksection), x, y, z, IBlockData);
	}

	public static void setChunkSectionsBlocks(Object chunksection, int x, int y, int z, Object IBlockData) {
		Ref.invoke(chunksection, setblock, x, y, z, IBlockData);
	}

	public static void postToMainThread(Runnable runnable) {
		Ref.invoke(getServer(), post, runnable);
	}

	public static void setChunkSectionsBlocksByMethodB(Object chunksection, int x, int y, int z, Object IBlockData) {
		if (Ref.invoke(chunksection, setblockb, x, y, z, IBlockData) == null)
			Ref.invoke(chunksection, setblock, x, y, z, IBlockData);
	}

	public static Object getChunkSection(int y) {
		Object o = Ref.newInstance(ChunkSection, y);
		return o != null ? o : Ref.newInstance(ChunkSection, y, true);
	}

	public static Object getChunk(Chunk chunk) {
		return getChunk(getCraftChunk(chunk));
	}

	public static Object getChunk(Object CraftChunk) {
		return Ref.invoke(CraftChunk, Chunk);
	}

	public static Object getCraftChunk(Chunk chunk) {
		return Ref.craft("CraftChunk").cast(chunk);
	}

	public static Object getChunkCoordIntPair(int x, int z) {
		return Ref.newInstance(chunkc, x, z);
	}

	public static Object getChunkCoordIntPair(Chunk chunk) {
		return getChunkCoordIntPair(chunk.getX(), chunk.getZ());
	}

	public static Object getServer() {
		return Ref.invoke(Ref.nms("MinecraftServer"), getser);
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

	public static Object getMaterial(Object IBlockData) {
		return Ref.invoke(IBlockData, getmat);
	}

	public static Object getBlock(Object IBlockData) {
		return Ref.invoke(IBlockData, getb);
	}

	public static Object getItemStack(org.bukkit.inventory.ItemStack stack) {
		return Ref.invoke(null, itemstack, stack);
	}

	public static Object getChunk(Object World, int x, int z) {
		return Ref.invoke(World, getc, x, z);
	}

	public static Object getIBlockData(Object World, Object blockPosition) {
		return Ref.invoke(World, gett, blockPosition);
	}

	public static Object getIBlockData(Object World, int x, int y, int z) {
		return getIBlockData(World, getBlockPosition(x, y, z));
	}

	public static Object getIBlockData(World world, Object blockPosition) {
		return getIBlockData(Ref.world(world), blockPosition);
	}

	public static Object getIBlockData(World world, int x, int y, int z) {
		return getIBlockData(Ref.world(world), getBlockPosition(x, y, z));
	}

	public static Object getChunk(World world, int x, int z) {
		return getChunk(Ref.world(world), x, z);
	}

	public static Object getPacketPlayOutTitle(TitleAction action, Object IChatBaseComponent) {
		return getPacketPlayOutTitle(action, IChatBaseComponent, 10, 20, 10);
	}

	public static Object getPacketPlayOutTitle(TitleAction action, Object IChatBaseComponent, int fadeIn, int stay,
			int fadeOut) {
		if (action == TitleAction.ACTIONBAR) {
			Object o = Ref.newInstance(pTitle, Ref.getNulled(Ref.field(enumTitle, action.name())),
					IChatBaseComponent, fadeIn, stay, fadeOut);
			return o != null ? o : Ref.newInstance(pOutChat, IChatBaseComponent, (byte) 2);
		}
		return Ref.newInstance(pTitle, Ref.getNulled(Ref.field(enumTitle, action.name())),
				IChatBaseComponent, fadeIn, stay, fadeOut);
	}

	public static Object getPacketPlayOutTitle(TitleAction action, String text, int fadeIn, int stay, int fadeOut) {
		return getPacketPlayOutTitle(action, getIChatBaseComponentText(text), fadeIn, stay, fadeOut);
	}

	public static Object getPacketPlayOutTitle(TitleAction action, String text) {
		return getPacketPlayOutTitle(action, getIChatBaseComponentText(text), 10, 20, 10);
	}

	public static Object getPacketPlayOutMapChunk(Object Chunk, int workers) {
		return getPacketPlayOutMapChunk(Chunk, true, workers);
	}

	public static Object getPacketPlayOutMapChunk(World world, int x, int z, int workers) {
		return getPacketPlayOutMapChunk(getChunk(world, x, z), workers);
	}

	public static Object getPacketPlayOutMapChunk(World world, int x, int z, boolean value, int workers) {
		return getPacketPlayOutMapChunk(getChunk(world, x, z), value, workers);
	}

	public static Object getPacketPlayOutMapChunk(Object World, int x, int z, int workers) {
		return getPacketPlayOutMapChunk(getChunk(World, x, z), workers);
	}

	public static Object getPacketPlayOutMapChunk(Object World, int x, int z, boolean value, int workers) {
		return getPacketPlayOutMapChunk(getChunk(World, x, z), value, workers);
	}

	public static Object getPacketPlayOutMapChunk(Object Chunk, boolean value, int workers) {
		Object o = Ref.newInstance(pChunk, Chunk, workers);
		return o != null ? o : Ref.newInstance(pChunk, Chunk, value, workers);
	}

	public static void refleshBlock(Position pos, Object oldBlock) {
		if (TheAPI.isOlderThan(8))
			Ref.invoke(Ref.world(pos.getWorld()), notify, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
		else
			Ref.invoke(Ref.world(pos.getWorld()), notify, pos.getBlockPosition(), oldBlock,
					pos.getType().getIBlockData(), 3);
	}

	public static Object getPacketPlayOutChat(ChatType type, Object IChatBaseComponent) {
		Object o = old == 2 ? Ref.newInstance(pOutChat, IChatBaseComponent, (byte) 1)
				: (old == 0
						? Ref.newInstance(pOutChat, IChatBaseComponent,
								Ref.getNulled(Ref.field(Ref.nms("ChatMessageType"), type.name())))
						: Ref.newInstance(pOutChat, IChatBaseComponent,
								Ref.getNulled(Ref.field(Ref.nms("ChatMessageType"), type.name())),
								UUID.randomUUID()));
		return o;
	}

	public static Object getPacketPlayOutChat(ChatType type, String text) {
		return getPacketPlayOutChat(type, getIChatBaseComponentText(text));
	}

	public static Object getType(World w, int x, int y, int z) {
		return Ref.invoke(Ref.world(w),
				Ref.method(Ref.nms("World"), "getType", Ref.nms("BlockPosition")),
				getBlockPosition(x, y, z));
	}

	public static void setBlock(World world, int x, int y, int z, Material material, int data, boolean applyPsychics) {
		world.getBlockAt(x, y, z).getDrops().clear();
		Object old = getIBlockData(world, x, y, z);
		Object newIblock = getIBlockData(material, data), position = getBlockPosition(x, y, z),
				World = Ref.world(world);
		Ref.invoke(World, worldset, position, newIblock, applyPsychics ? 3 : 2);
		NMSAPI.refleshBlock(new Position(world, x, y, z), old);
	}

	public static void setBlock(World world, int x, int y, int z, Material material, boolean applyPsychics) {
		setBlock(world, x, y, z, material, 0, applyPsychics);
	}

	public static void setBlock(Location loc, Material material, int data, boolean applyPsychics) {
		setBlock(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), material, data, applyPsychics);
	}

	public static Object getIBlockData(Material material) {
		return getIBlockData(material, 0);
	}

	public static Object getIBlockData(Material material, int data) {
		try {
			// 1.13+ only
			Object o = Ref.cast(Ref.nms("block.data.CraftBlockData"),
					Bukkit.createBlockData(material));
			return Ref.invoke(o,
					Ref.method(Ref.nms("block.data.CraftBlockData"), "getState"));
		} catch (NoSuchMethodError er) {
			try {
				Object o = Ref.invoke(Ref.invoke(null, block, material.getId()), IBlockData, data);
				return o != null ? o : Ref.invoke(null, IBlockData, material.getId() + (data << 12));
			} catch (Exception e) {
				return Ref.invoke(null, IBlockData, material.getId() + (data << 12));
			}
		}
	}

	public static Object getPacketPlayOutEntityDestroy(int... id) {
		return Ref.newInstance(pDestroy, id);
	}

	public static Object getEntity(Entity entity) {
		return Ref.invoke(Ref.cast(Ref.craft("entity.CraftEntity"), entity), entityM);
	}

	public static Object getEntityLiving(LivingEntity entity) {
		return Ref.invoke(Ref.cast(Ref.craft("entity.CraftLivingEntity"), entity),
				livingentity);
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

	public static Object getPacketPlayOutPlayerListHeaderFooter(Object headerIChatBaseComponent,
			Object footerIChatBaseComponent) {
		if (pTab != null) {
			Object packet = Ref.newInstance(pTab);
			Field aField = null;
			Field bField = null;
			aField = Ref.field(Ref.nms("PacketPlayOutPlayerListHeaderFooter"), "header");
			if (aField == null)
				aField = Ref.field(Ref.nms("PacketPlayOutPlayerListHeaderFooter"), "a");
			bField = Ref.field(Ref.nms("PacketPlayOutPlayerListHeaderFooter"), "footer");
			if (bField == null)
				bField = Ref.field(Ref.nms("PacketPlayOutPlayerListHeaderFooter"), "b");
			Ref.set(packet, aField, headerIChatBaseComponent);
			Ref.set(packet, bField, footerIChatBaseComponent);
			return packet;
		}
		return null;
	}

	public static Object getPacketPlayOutPlayerListHeaderFooter(String header, String footer) {
		return getPacketPlayOutPlayerListHeaderFooter(getIChatBaseComponentText(header),
				getIChatBaseComponentText(footer));
	}

	public static Object getPacketPlayOutBlockChange(Object World, int x, int y, int z) {
		return getPacketPlayOutBlockChange(World, getBlockPosition(x, y, z));
	}

	public static Object getPacketPlayOutBlockChange(World world, Object BlockPosition) {
		return getPacketPlayOutBlockChange(Ref.world(world), BlockPosition);
	}

	public static Object getPacketPlayOutBlockChange(World world, int x, int y, int z) {
		return getPacketPlayOutBlockChange(Ref.world(world), getBlockPosition(x, y, z));
	}

	public static Object getPacketPlayOutBlockChange(Object World, Object BlockPosition) {
		return Ref.newInstance(pBlock, World, BlockPosition);
	}

	public static Object getBlockPosition(int x, int y, int z) {
		return Ref.newInstance(blockPos, x, y, z);
	}

	public static Object getIChatBaseComponentText(String text) {
		return getIChatBaseComponentJson("{\"text\":\"" + text + "\"}");
	}

	public static Object getIChatBaseComponentFromCraftBukkit(String text) {
		return Ref.invokeNulled(Ref.method(Ref.craft("util.CraftChatMessage"), "fromStringOrNull", String.class),text);
	}

	public static Object getIChatBaseComponentJson(String json) {
		return Ref.invokeNulled(oldichatser, json);
	}

	public static Thread getServerThread() {
		Object o = Ref.get(getServer(),Ref.field(Ref.nms("MinecraftServer"), "primaryThread"));
		return o != null ? (Thread) o
				: (Thread) Ref.get(getServer(),
						Ref.field(Ref.nms("MinecraftServer"), "serverThread"));
	}

	public static void sendPacket(Player to, Object packet) {
		Ref.sendPacket(to, packet);
	}
}
