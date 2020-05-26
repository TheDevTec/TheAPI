
package me.Straiker123;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NMSAPI {

	private static Class<?> ichat = Reflections.getNMSClass("IChatBaseComponent"), ichatser,
			iblockdata = Reflections.getNMSClass("IBlockData"), pos = Reflections.getNMSClass("BlockPosition"),
			world = Reflections.getNMSClass("World"), enumChat = Reflections.getNMSClass("ChatMessageType"),
			bWorld = Reflections.getBukkitClass("CraftWorld"),
			bPlayer = Reflections.getBukkitClass("entity.CraftPlayer"),
			EntityPlayer = Reflections.getNMSClass("EntityPlayer"), server = Reflections.getNMSClass("MinecraftServer"),
			b = Reflections.getNMSClass("Block"), cChunk = Reflections.getBukkitClass("CraftChunk"),
			cs = Reflections.getNMSClass("ChunkSection"), enumTitle, Containers = Reflections.getNMSClass("Containers"),
			particleEnum = Reflections.existNMSClass("EnumParticle") ? Reflections.getNMSClass("EnumParticle")
					: Reflections.getNMSClass("Particles");
	private static Constructor<?> pDestroy, pTitle, pOutChat, pTab, pBlock, blockPos, pChunk, ChunkSection, chunkc, particle,
			pSpawn, pNSpawn, pLSpawn, pWindow,score,sbobj,sbdisplayobj,sbteam;
	private static Method getmat, getb, getc, gett, WorldHandle, PlayerHandle, ichatcon, getser, plist, block,
			IBlockData, worldset, Chunk, getblocks, setblock, setblockb, itemstack, entityM, livingentity, oldichatser,
			post;
	private static Field tps;
	private static Object sbremove, sbinteger, sbchange,sbhearts;

	static {
		getser=Reflections.getMethod(server, "getServer");
		sbteam=Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutScoreboardTeam"));
		sbdisplayobj=Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutScoreboardDisplayObjective"));
		sbobj=Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutScoreboardObjective"));
		score=Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutScoreboardScore"),String.class);
		if(score==null)
			score=Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutScoreboardScore"),
					Reflections.getNMSClass("ScoreboardServer$Action"),String.class, String.class, int.class);
		sbremove=Reflections.get(Reflections.getField(Reflections.getNMSClass("PacketPlayOutScoreboardScore$EnumScoreboardAction"),"REMOVE"),null);
		if(sbremove==null)
			sbremove=Reflections.get(Reflections.getField(Reflections.getNMSClass("ScoreboardServer$Action"),"CHANGE"),null);
		sbchange=Reflections.get(Reflections.getField(Reflections.getNMSClass("PacketPlayOutScoreboardScore$EnumScoreboardAction"),"CHANGE"),null);
		if(sbchange==null)
			sbchange=Reflections.get(Reflections.getField(Reflections.getNMSClass("ScoreboardServer$Action"),"REMOVE"),null);
		sbinteger=Reflections.get(Reflections.getField(Reflections.getNMSClass("IScoreboardCriteria$EnumScoreboardHealthDisplay"),"INTEGER"),null);
		sbhearts=Reflections.get(Reflections.getField(Reflections.getNMSClass("IScoreboardCriteria$EnumScoreboardHealthDisplay"),"HEARTS"),null);
		post=Reflections.getMethod(Reflections.getNMSClass("MinecraftServer"),"postToMainThread", Runnable.class);
		if(post==null)post=Reflections.getMethod(Reflections.getNMSClass("MinecraftServer"),"executeSync", Runnable.class);
		if(Reflections.existNMSClass("PacketPlayOutWorldParticles"))
		particle = Reflections.getConstructors(Reflections.getNMSClass("PacketPlayOutWorldParticles"))[1];
		if(particle==null)particle = Reflections.getConstructors(Reflections.getNMSClass("Packet63WorldParticles"))[1];
		if(Reflections.existNMSClass("PacketPlayOutTitle"))
		enumTitle = Reflections.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0];
		try {
			ichatser = ichat.getDeclaredClasses()[0];
		} catch (Exception oldversion) {
			oldichatser = Reflections.getMethod(Reflections.getNMSClass("ChatSerializer"), "a", String.class);
		}
		pDestroy = Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutEntityDestroy"),int[].class);
		pWindow = Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutOpenWindow"),int.class, String.class, ichat);
		if(pWindow==null)
			pWindow = Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutOpenWindow"),int.class, Containers,ichat);
		pSpawn = Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutSpawnEntity"),Reflections.getNMSClass("Entity"), int.class);
		pNSpawn = Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutNamedEntitySpawn"),Reflections.getNMSClass("EntityHuman"));
		pLSpawn = Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutSpawnEntityLiving"),Reflections.getNMSClass("EntityLiving"));
		entityM = Reflections.getMethod(Reflections.getBukkitClass("entity.CraftEntity"),"getHandle");
		livingentity = Reflections.getMethod(Reflections.getBukkitClass("entity.CraftLivingEntity"),"getHandle");
		itemstack = Reflections.getMethod(Reflections.getBukkitClass("inventory.CraftItemStack"),"asNMSCopy",ItemStack.class);
		getblocks = Reflections.getMethod(cs,"getBlocks");
		setblock = Reflections.getMethod(Reflections.getNMSClass("DataPaletteBlock"),"setBlock",int.class, int.class,int.class, Object.class);
		if(setblock==null)
			setblock = Reflections.getMethod(Reflections.getNMSClass("DataPaletteBlock"),"setBlock",int.class, int.class,int.class, iblockdata);
		setblockb = Reflections.getMethod(Reflections.getNMSClass("DataPaletteBlock"),"b",int.class, int.class,int.class, Object.class);
		chunkc = Reflections.getConstructor(Reflections.getNMSClass("ChunkCoordIntPair"),int.class, int.class);
		setblockb = Reflections.getMethod(Reflections.getNMSClass("MinecraftServer"),"getServer");
		ChunkSection = Reflections.getConstructor(Reflections.getNMSClass("ChunkSection"),int.class);
		if(ChunkSection==null)
			ChunkSection = Reflections.getConstructor(Reflections.getNMSClass("ChunkSection"),int.class, boolean.class);
		Chunk = Reflections.getMethod(cChunk,"getHandle");
		getmat = Reflections.getMethod(iblockdata,"getMaterial");
		getb = Reflections.getMethod(iblockdata,"getBlock");
		getc = Reflections.getMethod(world,"getChunkAt", int.class, int.class);
		gett = Reflections.getMethod(world,"getType",pos);
		pTitle = Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutTitle"),enumTitle, ichat, int.class,int.class, int.class);
		pOutChat = Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutChat"),ichat, enumChat);
		if(pOutChat==null)
			pOutChat = Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutChat"),ichat, int.class);
		pTab = Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutPlayerListHeaderFooter"));
		WorldHandle = Reflections.getMethod(bWorld,"getHandle");
		PlayerHandle = Reflections.getMethod(bPlayer,"getHandle");
		pBlock = Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutBlockChange"),Reflections.getNMSClass("IBlockAccess"), pos);
		if(pBlock==null)
			pBlock = Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutBlockChange"),Reflections.getNMSClass("World"), pos);
		blockPos = Reflections.getConstructor(Reflections.getNMSClass("BlockPosition"),int.class, int.class, int.class);
		ichatcon = Reflections.getMethod(ichatser,"a", String.class);
		tps = Reflections.getField(server,"recentTps");
		plist = Reflections.getMethod(server,"getPlayerList");
		block = Reflections.getMethod(b,"getById", int.class);
		IBlockData = Reflections.getMethod(b,"fromLegacyData", int.class);
		if(IBlockData==null)
			IBlockData = Reflections.getMethod(b,"getByCombinedId", int.class);
		worldset = Reflections.getMethod(world,"setTypeAndData", pos, iblockdata, int.class);
		pChunk = Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutMapChunk"),Reflections.getNMSClass("Chunk"),int.class);
		if(pChunk==null)
			pChunk = Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutMapChunk"),Reflections.getNMSClass("Chunk"), boolean.class,int.class);
	}

	public static enum Action {
		CHANGE, REMOVE
	}
	
	public static enum DisplayType {
		INTEGER, HEARTS
	}

	public Object getPacketPlayOutScoreboardObjective() {
		return Reflections.c(sbobj);
	}
	
	public Object getPacketPlayOutScoreboardDisplayObjective() {
		return Reflections.c(sbdisplayobj);
	}
	
	public Object getPacketPlayOutScoreboardScore(Action action, String player, String line, int score) {
		Object o = Reflections.c(NMSAPI.score,line);
		if(o!=null) {
			Reflections.setField(o, "b", player);
			Reflections.setField(o, "c", score);
			Reflections.setField(o, "d", getScoreboardAction(action));
			return o;
		}
		return Reflections.c(NMSAPI.score,getScoreboardAction(action),player, line, score);
	}
	
	public Object getScoreboardAction(Action type) {
		return type == Action.CHANGE ? sbchange : sbremove;
	}
	
	public Object getEnumScoreboardHealthDisplay(DisplayType type) {
		return type == DisplayType.HEARTS ? sbhearts : sbinteger;
	}

	public Object getPacketPlayOutScoreboardTeam() {
		return Reflections.c(sbteam);
	}
	
	// return DataPaletteBlock<IBlockData>
	public Object getChunkSectionBlocks(Object ChunkSection) {
		return Reflections.invoke(ChunkSection, getblocks);
	}

	public void setChunkSectionsBlocks(int chunksection, int x, int y, int z, Object IBlockData) {
		setChunkSectionsBlocks(getChunkSection(chunksection), x, y, z, IBlockData);
	}

	public void setChunkSectionsBlocks(Object chunksection, int x, int y, int z, Object IBlockData) {
		Reflections.invoke(chunksection, setblock, x, y, z, IBlockData);
	}

	public void postToMainThread(Runnable runnable) {
		Reflections.invoke(getServer(), post, runnable);
	}

	public void setChunkSectionsBlocksByMethodB(Object chunksection, int x, int y, int z, Object IBlockData) {
		if(Reflections.invoke(chunksection, setblockb, x, y, z, IBlockData)==null)
			Reflections.invoke(chunksection, setblock, x, y, z, IBlockData);
	}

	public Object getChunkSection(int y) {
		Object o = Reflections.c(ChunkSection, y);
		return o != null ? o : Reflections.c(ChunkSection, y, true);
	}

	public me.Straiker123.Player getNMSPlayerAPI(Player p) {
		return new me.Straiker123.Player(getPlayer(p));
	}

	public me.Straiker123.Player getNMSPlayerAPI(Object entityPlayer) {
		return new me.Straiker123.Player(entityPlayer);
	}

	public Object getChunk(Chunk chunk) {
		return getChunk(getCraftChunk(chunk));
	}

	public Object getChunk(Object CraftChunk) {
		return Reflections.invoke(CraftChunk, Chunk);
	}

	public Object getCraftChunk(Chunk chunk) {
		return Reflections.getBukkitClass("CraftChunk").cast(chunk);
	}

	public Object getChunkCoordIntPair(int x, int z) {
		return Reflections.c(chunkc, x, z);
	}

	public Object getChunkCoordIntPair(Chunk chunk) {
		return getChunkCoordIntPair(chunk.getX(),chunk.getZ());
	}

	public Class<?> getMinecraftServer() {
		return server;
	}

	public ArrayList<String> getOnlinePlayersNames() {
		ArrayList<String> a = new ArrayList<String>();
		List<?> list = (List<?>) Reflections.get(Reflections.getField(Reflections.getNMSClass("PlayerList"), "players"),getPlayerList());
		for (Object s : list) {
			a.add((String) Reflections.invoke(s, Reflections.getMethod(EntityPlayer, "getName")));
		}
		return a;
	}

	public ArrayList<Player> getOnlinePlayers() {
		ArrayList<Player> a = new ArrayList<Player>();
		List<?> list = (List<?>) Reflections.get(Reflections.getField(Reflections.getNMSClass("PlayerList"), "players"),getPlayerList());
		for (Object s : list) {
			a.add((Player)Reflections.invoke(s, Reflections.getMethod(EntityPlayer, "getBukkitEntity")));
		}
		return a;
	}

	public Object getPlayerList() {
		return Reflections.invoke(getServer(), plist);
	}

	public Object getServer() {
		return Reflections.invoke(server, getser);
	}

	public double[] getServerTPS() {
		return (double[]) Reflections.get(tps, getServer());
	}

	public static enum TitleAction {
		ACTIONBAR, CLEAR, RESET, SUBTITLE, TITLE
	}

	public static enum ChatType {
		CHAT, GAME_INFO, SYSTEM
	}

	public Object getMaterial(Object IBlockData) {
		return Reflections.invoke(IBlockData, getmat);
	}

	public Object getBlock(Object IBlockData) {
		return Reflections.invoke(IBlockData, getb);
	}

	public Object getItemStack(org.bukkit.inventory.ItemStack stack) {
		return Reflections.invoke(null, itemstack, stack);
	}

	public Object getPacketPlayOutWorldParticles(Particle effect, Location loc) {
		return create(effect, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 1, 1, null, 0, 0, 0);
	}

	public Object getPacketPlayOutWorldParticles(Particle effect, Location loc, ParticleData data) {
		return create(effect, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 1, 1, data, 0, 0, 0);
	}

	public Object getPacketPlayOutWorldParticles(Particle effect, float x, float y, float z, int amount,
			ParticleData data) {
		return create(effect, x, y, z, 1, amount, data, 0, 0, 0);
	}

	public Object getPacketPlayOutWorldParticles(Particle effect, float x, float y, float z, ParticleData data) {
		return create(effect, x, y, z, 1, 1, data, 0, 0, 0);
	}

	public Object getPacketPlayOutWorldParticles(Particle effect, float x, float y, float z, float speed, int amount,
			ParticleColor color) {
		if (effect == Particle.REDSTONE && color.getRed() == 0) {
			return create(effect, x, y, z, speed, amount, null, Float.MIN_NORMAL, color.getValueY(), color.getValueZ());
		}
		return create(effect, x, y, z, speed, amount, null, color.getValueX(), color.getValueY(), color.getValueZ());
	}

	public Object getPacketPlayOutWorldParticles(Particle effect, float x, float y, float z, float speed,
			ParticleColor color) {
		if (effect == Particle.REDSTONE && color.getRed() == 0) {
			return create(effect, x, y, z, speed, 1, null, Float.MIN_NORMAL, color.getValueY(), color.getValueZ());
		}
		return create(effect, x, y, z, speed, 1, null, color.getValueX(), color.getValueY(), color.getValueZ());
	}

	public Object getPacketPlayOutWorldParticles(Particle effect, float x, float y, float z, ParticleColor color) {
		if (effect == Particle.REDSTONE && color.getRed() == 0) {
			return create(effect, x, y, z, 1, 1, null, Float.MIN_NORMAL, color.getValueY(), color.getValueZ());
		}
		return create(effect, x, y, z, 1, 1, null, color.getValueX(), color.getValueY(), color.getValueZ());
	}

	public Object getPacketPlayOutWorldParticles(Particle effect, Location loc, ParticleColor color) {
		if (effect == Particle.REDSTONE && color.getRed() == 0) {
			return create(effect, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 1, 1, null,
					Float.MIN_NORMAL, color.getValueY(), color.getValueZ());
		}
		return create(effect, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 1, 1, null, color.getValueX(),
				color.getValueY(), color.getValueZ());
	}

	public Object getPacketPlayOutWorldParticles(Particle effect, float x, float y, float z, float speed, int amount,
			ParticleData data) {
		if (speed < 0)
			throw new IllegalArgumentException("The speed is lower than 0");
		if (amount < 0)
			throw new IllegalArgumentException("The amount is lower than 0");
		return create(effect, x, y, z, speed, amount, data, 0, 0, 0);
	}

	private Object create(Particle effect, float x, float y, float z, float speed, int amount, ParticleData data,
			float floatx, float floaty, float floatz) {
			Object packet = Reflections.c(particle, particleEnum);
			if (TheAPI.getStringUtils().getInt(TheAPI.getServerVersion().split("_")[1]) < 8) {
				String name = effect.name();
				if (data != null) {
					name += data.getPacketDataString();
				}
				Reflections.setField(packet, "a", name);
			} else {
				Reflections.setField(packet, "a", Reflections.get(Reflections.getField(particleEnum, effect.name()),null));
				Reflections.setField(packet, "j", false);
				if (data != null) {
					int[] packetData = data.getPacketData();
					Reflections.setField(packet, "k", effect == Particle.ITEM_CRACK ? packetData
							: new int[] { packetData[0] | (packetData[1] << 12) });
				}
			}
			Reflections.setField(packet, "b", x);
			Reflections.setField(packet, "c", y);
			Reflections.setField(packet, "d", z);
			Reflections.setField(packet, "e", floatx);
			Reflections.setField(packet, "f", floaty);
			Reflections.setField(packet, "g", floatz);
			Reflections.setField(packet, "h", speed);
			Reflections.setField(packet, "i", amount);
			return packet;
	}

	public Object getChunk(Object World, int x, int z) {
		return Reflections.invoke(World, getc, x,z);
	}

	public Object getIBlockData(Object World, Object blockPosition) {
		return Reflections.invoke(World, gett, blockPosition);
	}

	public Object getIBlockData(Object World, int x, int y, int z) {
		return getIBlockData(World, getBlockPosition(x, y, z));
	}

	public Object getIBlockData(World world, Object blockPosition) {
		return getIBlockData(getWorld(world), blockPosition);
	}

	public Object getIBlockData(World world, int x, int y, int z) {
		return getIBlockData(getWorld(world), getBlockPosition(x, y, z));
	}

	public Object getChunk(World world, int x, int z) {
		return getChunk(getWorld(world), x, z);
	}

	public Object getPacketPlayOutTitle(TitleAction action, Object IChatBaseComponent) {
		return getPacketPlayOutTitle(action, IChatBaseComponent, 10, 20, 10);
	}

	public Object getPacketPlayOutOpenWindow(int id, String container, String text) {
		return getPacketPlayOutOpenWindow(id, container, getIChatBaseComponentText(text));
	}

	public Object getPacketPlayOutOpenWindow(int id, String container, Object IChatBaseComponent) {
		Object o = Reflections.c(pWindow, id, container, IChatBaseComponent);
		return o!=null ? o : Reflections.c(pWindow, id,  Reflections.get(Reflections.getField(Containers, container),null), IChatBaseComponent);
	}

	public Object getPacketPlayOutTitle(TitleAction action, Object IChatBaseComponent, int fadeIn, int stay, int fadeOut) {
		return Reflections.c(pTitle, Reflections.get(Reflections.getField(enumTitle, action.name()),null), IChatBaseComponent, fadeIn, stay,fadeOut);
	}

	public Object getPacketPlayOutTitle(TitleAction action, String text, int fadeIn, int stay, int fadeOut) {
		return getPacketPlayOutTitle(action, getIChatBaseComponentText(text), fadeIn, stay, fadeOut);
	}

	public Object getPacketPlayOutTitle(TitleAction action, String text) {
		return getPacketPlayOutTitle(action, getIChatBaseComponentText(text), 10, 20, 10);
	}

	public Object getPacketPlayOutMapChunk(Object Chunk, int workers) {
		return getPacketPlayOutMapChunk(Chunk, true, workers);
	}

	public Object getPacketPlayOutMapChunk(World world, int x, int z, int workers) {
		return getPacketPlayOutMapChunk(getChunk(world, x, z), workers);
	}

	public Object getPacketPlayOutMapChunk(World world, int x, int z, boolean value, int workers) {
		return getPacketPlayOutMapChunk(getChunk(world, x, z), value, workers);
	}

	public Object getPacketPlayOutMapChunk(Object World, int x, int z, int workers) {
		return getPacketPlayOutMapChunk(getChunk(World, x, z), workers);
	}

	public Object getPacketPlayOutMapChunk(Object World, int x, int z, boolean value, int workers) {
		return getPacketPlayOutMapChunk(getChunk(World, x, z), value, workers);
	}

	public Object getPacketPlayOutMapChunk(Object Chunk, boolean value, int workers) {
		Object o = Reflections.c(pChunk,Chunk, workers);
		return o != null ? o : Reflections.c(pChunk,Chunk, value, workers);
	}

	public void refleshBlock(Object world, Object blockposition, Object oldBlock, Object newBlock) {
		Reflections.invoke(world, Reflections.getMethod(NMSAPI.world, "notify", pos, iblockdata, iblockdata, int.class),blockposition, oldBlock, newBlock, 3);
	}
	/**
	 * @see see ActionBar if version is 1.7.10
	 */
	public Object getPacketPlayOutChat(ChatType type, Object IChatBaseComponent) {
		Object o = Reflections.c(pOutChat,IChatBaseComponent, Reflections.get(Reflections.getField(enumChat, type.name()),null));
		return o != null ? o : Reflections.c(pOutChat,IChatBaseComponent, 2);
	}

	public Object getPacketPlayOutChat(ChatType type, String text) {
		return getPacketPlayOutChat(type, getIChatBaseComponentText(text));
	}

	public Object getType(World w, int x, int y, int z) {
		return Reflections.invoke(getWorld(w), Reflections.getMethod(world, "getType", pos), getBlockPosition(x, y, z));
	}

	public Object[] setBlock(World world, int x, int y, int z, Material material, int data, boolean applyPsychics, boolean update) {
		Object old = getType(world, x, y, z);
		Object newIblock = getIBlockData(material, data), position = getBlockPosition(x, y, z), World = getWorld(world);
		Reflections.invoke(World, worldset, position, b, applyPsychics ? 3 : 2);
		if(update)
		refleshBlock(World, position, old, newIblock);
		return new Object[] {World, position, old, newIblock};
	}

	public Object[] setBlock(World world, int x, int y, int z, Material material, boolean applyPsychics) {
		return setBlock(world, x, y, z, material, 0, applyPsychics,true);
	}

	public Object[] setBlock(Location loc, Material material, int data, boolean applyPsychics) {
		return setBlock(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), material, data,
				applyPsychics,true);
	}

	public Object[] setBlock(World world, int x, int y, int z, Material material, boolean applyPsychics, boolean update) {
		return setBlock(world, x, y, z, material, 0, applyPsychics,update);
	}

	public Object[] setBlock(Location loc, Material material, int data, boolean applyPsychics, boolean update) {
		return setBlock(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), material, data,
				applyPsychics,update);
	}

	public Object getIBlockData(Material material) {
		return getIBlockData(material, 0);
	}

	@SuppressWarnings("deprecation")
	public Object getIBlockData(Material material, int data) {
		try {
			// 1.13+ only
			Object o = Reflections.cast(Reflections.getNMSClass("block.data.CraftBlockData"), Bukkit.createBlockData(material));
			return Reflections.invoke(o,Reflections.getMethod(Reflections.getNMSClass("block.data.CraftBlockData"),"getState"));
		} catch (NoSuchMethodError er) {
			Object o = Reflections.invoke(Reflections.invoke(null, block, material.getId()),IBlockData,data);
			return o != null ? o : Reflections.invoke(null, IBlockData, material.getId() + (data << 12));
		}
	}

	public Object getPacketPlayOutEntityDestroy(int... id) {
		return Reflections.c(pDestroy, id);
	}

	public Object getEntity(Entity entity) {
		return Reflections.invoke(Reflections.cast(Reflections.getBukkitClass("entity.CraftEntity"), entity),entityM);
	}

	public Object getEntityLiving(LivingEntity entity) {
		return Reflections.invoke(Reflections.cast(Reflections.getBukkitClass("entity.CraftLivingEntity"), entity),livingentity);
	}

	public Object getPacketPlayOutSpawnEntity(Object entity, int id) {
		return Reflections.c(pSpawn, entity, id);
	}

	public Object getPacketPlayOutNamedEntitySpawn(Object Player) {
		return Reflections.c(pNSpawn, Player);
	}

	public Object getPacketPlayOutSpawnEntityLiving(Object entityLiving) {
		return Reflections.c(pLSpawn, entityLiving);
	}

	public Object getPacketPlayOutPlayerListHeaderFooter(Object headerIChatBaseComponent,
			Object footerIChatBaseComponent) {
			Object packet = Reflections.c(pTab);
			Field aField = null;
			Field bField = null;
			aField = Reflections.getField(Reflections.getNMSClass("PacketPlayOutPlayerListHeaderFooter"), "header");
			if(aField==null)
				aField = Reflections.getField(Reflections.getNMSClass("PacketPlayOutPlayerListHeaderFooter"), "a");
			bField = Reflections.getField(Reflections.getNMSClass("PacketPlayOutPlayerListHeaderFooter"), "footer");
			if(bField==null)
				bField = Reflections.getField(Reflections.getNMSClass("PacketPlayOutPlayerListHeaderFooter"), "b");
			Reflections.setField(packet, aField, headerIChatBaseComponent);
			Reflections.setField(packet, bField, footerIChatBaseComponent);
			return packet;
	}

	public Object getPacketPlayOutPlayerListHeaderFooter(String header, String footer) {
		return getPacketPlayOutPlayerListHeaderFooter(getIChatBaseComponentText(header),
				getIChatBaseComponentText(footer));
	}

	public Object getCraftWorld(World world) {
		return bWorld.cast(world);
	}

	public Object getWorld(World world) {
		return getWorld(getCraftWorld(world));
	}

	public Object getWorld(Object craftWorld) {
		return Reflections.invoke(craftWorld, WorldHandle);
	}

	public Object getCraftPlayer(Player player) {
		return Reflections.cast(bPlayer, player);
	}

	public Object getPlayer(Player player) {
		return getPlayer(getCraftPlayer(player));
	}

	public Object getPlayer(Object CraftPlayer) {
		return Reflections.invoke(CraftPlayer,PlayerHandle);
	}

	public void sendPacket(Player player, Object packet) {
		getNMSPlayerAPI(player).sendPacket(packet);
	}

	public Object getPacketPlayOutBlockChange(Object World, int x, int y, int z) {
		return getPacketPlayOutBlockChange(World, getBlockPosition(x, y, z));
	}

	public Object getPacketPlayOutBlockChange(World world, Object BlockPosition) {
		return getPacketPlayOutBlockChange(getWorld(world), BlockPosition);
	}

	public Object getPacketPlayOutBlockChange(World world, int x, int y, int z) {
		return getPacketPlayOutBlockChange(getWorld(world), getBlockPosition(x, y, z));
	}

	public Object getPacketPlayOutBlockChange(Object World, Object BlockPosition) {
		return Reflections.c(pBlock, World, BlockPosition);
	}

	public Object getBlockPosition(int x, int y, int z) {
		return Reflections.c(blockPos, x, y, z);
	}

	public Object getIChatBaseComponentText(String text) {
		return getIChatBaseComponentJson("{\"text\":\"" + text + "\"}");
	}

	public Object getIChatBaseComponentJson(String json) {
			if (oldichatser != null)
				return Reflections.invoke(null, oldichatser, json);
			return Reflections.invoke(null, ichatcon, json);
	}

	public Thread getServerThread() {
		Object o = Reflections.get(Reflections.getField(Reflections.getNMSClass("MinecraftServer"),"primaryThread"),getServer());
		return o != null ? (Thread)o : (Thread)Reflections.get(Reflections.getField(Reflections.getNMSClass("MinecraftServer"),"serverThread"),getServer());
	}
}
