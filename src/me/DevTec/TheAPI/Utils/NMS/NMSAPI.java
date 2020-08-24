package me.DevTec.TheAPI.Utils.NMS;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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
import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.NMS.DataWatcher.DataWatcher;
import me.DevTec.TheAPI.Utils.Reflections.Ref;
import me.DevTec.TheAPI.Utils.Reflections.Reflections;
import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;

public class NMSAPI {
	
	private static Class<?> ichat = Reflections.getNMSClass("IChatBaseComponent"), ichatser,
			iblockdata = Reflections.getNMSClass("IBlockData"), pos = Reflections.getNMSClass("BlockPosition"),
			world = Reflections.getNMSClass("World"), enumChat = Reflections.getNMSClass("ChatMessageType"),
			bWorld = Reflections.getBukkitClass("CraftWorld"),
					bPlayer = Reflections.getBukkitClass("entity.CraftPlayer"),entity = Reflections.getNMSClass("Entity"),
			EntityPlayer = Reflections.getNMSClass("EntityPlayer"), server = Reflections.getNMSClass("MinecraftServer"),
			b = Reflections.getNMSClass("Block"), cChunk = Reflections.getBukkitClass("CraftChunk"),
			cs = Reflections.getNMSClass("ChunkSection"), enumTitle, Containers = Reflections.getNMSClass("Containers"),
			particleEnum = Reflections.existNMSClass("EnumParticle") ? Reflections.getNMSClass("EnumParticle")
					: Reflections.getNMSClass("Particles");
	private static Constructor<?> pDestroy, pTitle, pOutChat, pTab, pBlock, blockPos, pChunk, ChunkSection, chunkc, particle,
			pSpawn, pNSpawn, pLSpawn, pWindow,score,sbobj,sbdisplayobj,sbteam,pSign,pTeleport,
			 metadata = Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutEntityMetadata"), int.class, Reflections.getNMSClass("DataWatcher"), boolean.class);
	private static Method getmat, getb, getc, gett, WorldHandle, PlayerHandle, ichatcon, getser, plist, block,
			IBlockData, worldset, Chunk, getblocks, setblock, setblockb, itemstack, entityM, livingentity, oldichatser,
			post;
	private static int old;
	private static Field tps,scoreb,scorec,scored;
	private static Object sbremove, sbinteger, sbchange,sbhearts;
	private static Field[] part= new Field[11];
	static {
		Class<?> c = Reflections.getNMSClass("PacketPlayOutWorldParticles")!=null?Reflections.getNMSClass("PacketPlayOutWorldParticles"):Reflections.getNMSClass("Packet63WorldParticles");
		part[0]=Reflections.getField(c, "a");
		part[1]=Reflections.getField(c, "j");
		part[2]=Reflections.getField(c, "k");
		part[3]=Reflections.getField(c, "b");
		part[4]=Reflections.getField(c, "c");
		part[5]=Reflections.getField(c, "d");
		part[6]=Reflections.getField(c, "e");
		part[7]=Reflections.getField(c, "f");
		part[8]=Reflections.getField(c, "g");
		part[9]=Reflections.getField(c, "h");
		part[10]=Reflections.getField(c, "i");
		scoreb=Reflections.getField(Reflections.getNMSClass("PacketPlayOutScoreboardScore"),"b");
		scorec=Reflections.getField(Reflections.getNMSClass("PacketPlayOutScoreboardScore"),"c");
		scored=Reflections.getField(Reflections.getNMSClass("PacketPlayOutScoreboardScore"),"d");
		pTeleport=Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutEntityTeleport"), entity);
		pSign=Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutOpenSignEditor"), pos);
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
		if(post==null)post=Reflections.getMethod(Reflections.getNMSClass("IAsyncTaskHandler"),"executeSync", Runnable.class);
		particle = Reflections.getConstructor(c);
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
		if(pOutChat==null) {
			old=1;
			pOutChat = Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutChat"),ichat, enumChat, UUID.class);
		}
		if(pOutChat==null) {
			old=2;
			pOutChat = Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutChat"),ichat, byte.class);
		}
		pTab = Ref.findConstructor(Reflections.getNMSClass("PacketPlayOutPlayerListHeaderFooter"));
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
	
	//Entity
	public static Object getPacketPlayOutEntityMetadata(Entity entity) {
		Object o = getEntity(entity);
		return getPacketPlayOutEntityMetadata(entity.getEntityId(), Reflections.invoke(o, Reflections.getMethod(o.getClass(), "getDataWatcher")));
	}
	
	//Entity
	public static Object getPacketPlayOutEntityMetadata(Object entity) {
		return getPacketPlayOutEntityMetadata((int)Reflections.invoke(entity, Reflections.getMethod(entity.getClass(), "getId")), Reflections.invoke(entity, Reflections.getMethod(entity.getClass(), "getDataWatcher")));
	}
	
	public static Object getPacketPlayOutEntityMetadata(Entity entity, DataWatcher dataWatcher) {
		return Reflections.c(metadata, entity.getEntityId(),dataWatcher.getDataWatcher(),true);
	}
	
	public static Object getPacketPlayOutEntityMetadata(int entityId, DataWatcher dataWatcher) {
		return Reflections.c(metadata, entityId,dataWatcher.getDataWatcher(),true);
	}
	
	//EntityId, DataWatcher
	public static Object getPacketPlayOutEntityMetadata(int entityId, Object dataWatcher) {
		return Reflections.c(metadata, entityId,dataWatcher,true);
	}

	public static Object getPacketPlayOutScoreboardObjective() {
		return Reflections.c(sbobj);
	}
	
	public static Object getPacketPlayOutScoreboardDisplayObjective() {
		return Reflections.c(sbdisplayobj);
	}
	
	public static Object getPacketPlayOutOpenSignEditor(Object position) {
		return Reflections.c(pSign, position);
	}
	
	public static Object getPacketPlayOutEntityTeleport(Object entity) {
		return Reflections.c(pTeleport, entity);
	}
	public static Object getPacketPlayOutScoreboardScore(Action action, String player, String line, int score) {
		Object o = Reflections.c(NMSAPI.score,line);
		if(o!=null) {
			Reflections.setField(o, scoreb, player);
			Reflections.setField(o, scorec, score);
			Reflections.setField(o, scored, getScoreboardAction(action));
			return o;
		}
		return Reflections.c(NMSAPI.score,getScoreboardAction(action),player, line, score);
	}
	
	public static Object getScoreboardAction(Action type) {
		return type == Action.CHANGE ? sbchange : sbremove;
	}
	
	public static Object getEnumScoreboardHealthDisplay(DisplayType type) {
		return type == DisplayType.HEARTS ? sbhearts : sbinteger;
	}

	public static Object getPacketPlayOutScoreboardTeam() {
		return Reflections.c(sbteam);
	}
	
	// return DataPaletteBlock<IBlockData>
	public static Object getChunkSectionBlocks(Object ChunkSection) {
		return Reflections.invoke(ChunkSection, getblocks);
	}

	public static void setChunkSectionsBlocks(int chunksection, int x, int y, int z, Object IBlockData) {
		setChunkSectionsBlocks(getChunkSection(chunksection), x, y, z, IBlockData);
	}

	public static void setChunkSectionsBlocks(Object chunksection, int x, int y, int z, Object IBlockData) {
		Reflections.invoke(chunksection, setblock, x, y, z, IBlockData);
	}

	public static void postToMainThread(Runnable runnable) {
		Reflections.invoke(getServer(), post, runnable);
	}

	public static void setChunkSectionsBlocksByMethodB(Object chunksection, int x, int y, int z, Object IBlockData) {
		if(Reflections.invoke(chunksection, setblockb, x, y, z, IBlockData)==null)
			Reflections.invoke(chunksection, setblock, x, y, z, IBlockData);
	}

	public static Object getChunkSection(int y) {
		Object o = Reflections.c(ChunkSection, y);
		return o != null ? o : Reflections.c(ChunkSection, y, true);
	}

	public static NMSPlayer getNMSPlayerAPI(Player p) {
		return new NMSPlayer(getPlayer(p));
	}

	public static NMSPlayer getNMSPlayerAPI(Object entityPlayer) {
		return new NMSPlayer(entityPlayer);
	}

	public static Object getChunk(Chunk chunk) {
		return getChunk(getCraftChunk(chunk));
	}

	public static Object getChunk(Object CraftChunk) {
		return Reflections.invoke(CraftChunk, Chunk);
	}

	public static Object getCraftChunk(Chunk chunk) {
		return Reflections.getBukkitClass("CraftChunk").cast(chunk);
	}

	public static Object getChunkCoordIntPair(int x, int z) {
		return Reflections.c(chunkc, x, z);
	}

	public static Object getChunkCoordIntPair(Chunk chunk) {
		return getChunkCoordIntPair(chunk.getX(),chunk.getZ());
	}

	public static Class<?> getMinecraftServer() {
		return server;
	}

	public static ArrayList<String> getOnlinePlayersNames() {
		ArrayList<String> a = new ArrayList<String>();
		List<?> list = (List<?>) Reflections.get(Reflections.getField(Reflections.getNMSClass("PlayerList"), "players"),getPlayerList());
		for (Object s : list) {
			a.add((String) Reflections.invoke(s, Reflections.getMethod(EntityPlayer, "getName")));
		}
		return a;
	}

	public static ArrayList<Player> getOnlinePlayers() {
		ArrayList<Player> a = new ArrayList<Player>();
		List<?> list = (List<?>) Reflections.get(Reflections.getField(Reflections.getNMSClass("PlayerList"), "players"),getPlayerList());
		for (Object s : list) {
			a.add((Player)Reflections.invoke(s, Reflections.getMethod(EntityPlayer, "getBukkitEntity")));
		}
		return a;
	}

	public static Object getPlayerList() {
		return Reflections.invoke(getServer(), plist);
	}

	public static Object getServer() {
		return Reflections.invoke(server, getser);
	}

	public static double[] getServerTPS() {
		return (double[]) Reflections.get(tps, getServer());
	}

	public static enum TitleAction {
		ACTIONBAR, CLEAR, RESET, SUBTITLE, TITLE
	}

	public static enum ChatType {
		CHAT, GAME_INFO, SYSTEM
	}

	public static Object getMaterial(Object IBlockData) {
		return Reflections.invoke(IBlockData, getmat);
	}

	public static Object getBlock(Object IBlockData) {
		return Reflections.invoke(IBlockData, getb);
	}

	public static Object getItemStack(org.bukkit.inventory.ItemStack stack) {
		return Reflections.invoke(null, itemstack, stack);
	}

	public static Object getPacketPlayOutWorldParticles(Particle effect, Location loc) {
		return create(effect, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 1, 1, null, 0, 0, 0);
	}

	public static Object getPacketPlayOutWorldParticles(Particle effect, Location loc, ParticleData data) {
		return create(effect, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 1, 1, data, 0, 0, 0);
	}

	public static Object getPacketPlayOutWorldParticles(Particle effect, float x, float y, float z, int amount,
			ParticleData data) {
		return create(effect, x, y, z, 1, amount, data, 0, 0, 0);
	}

	public static Object getPacketPlayOutWorldParticles(Particle effect, float x, float y, float z, ParticleData data) {
		return create(effect, x, y, z, 1, 1, data, 0, 0, 0);
	}

	public static Object getPacketPlayOutWorldParticles(Particle effect, float x, float y, float z, float speed, int amount,
			ParticleColor color) {
		if (effect == Particle.REDSTONE && color.getRed() == 0) {
			return create(effect, x, y, z, speed, amount, null, Float.MIN_NORMAL, color.getValueY(), color.getValueZ());
		}
		return create(effect, x, y, z, speed, amount, null, color.getValueX(), color.getValueY(), color.getValueZ());
	}

	public static Object getPacketPlayOutWorldParticles(Particle effect, float x, float y, float z, float speed,
			ParticleColor color) {
		if (effect == Particle.REDSTONE && color.getRed() == 0) {
			return create(effect, x, y, z, speed, 1, null, Float.MIN_NORMAL, color.getValueY(), color.getValueZ());
		}
		return create(effect, x, y, z, speed, 1, null, color.getValueX(), color.getValueY(), color.getValueZ());
	}

	public static Object getPacketPlayOutWorldParticles(Particle effect, float x, float y, float z, ParticleColor color) {
		if (effect == Particle.REDSTONE && color.getRed() == 0) {
			return create(effect, x, y, z, 1, 1, null, Float.MIN_NORMAL, color.getValueY(), color.getValueZ());
		}
		return create(effect, x, y, z, 1, 1, null, color.getValueX(), color.getValueY(), color.getValueZ());
	}

	public static Object getPacketPlayOutWorldParticles(Particle effect, Location loc, ParticleColor color) {
		if (effect == Particle.REDSTONE && color.getRed() == 0) {
			return create(effect, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 1, 1, null,
					Float.MIN_NORMAL, color.getValueY(), color.getValueZ());
		}
		return create(effect, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 1, 1, null, color.getValueX(),
				color.getValueY(), color.getValueZ());
	}

	public static Object getPacketPlayOutWorldParticles(Particle effect, float x, float y, float z, float speed, int amount,
			ParticleData data) {
		if (speed < 0)
			throw new IllegalArgumentException("The speed is lower than 0");
		if (amount < 0)
			throw new IllegalArgumentException("The amount is lower than 0");
		return create(effect, x, y, z, speed, amount, data, 0, 0, 0);
	}
	private static Object create(Particle effect, float x, float y, float z, float speed, int amount, ParticleData data,
			float floatx, float floaty, float floatz) {
			Object packet = Reflections.c(particle);
			if (StringUtils.getInt(TheAPI.getServerVersion().split("_")[1]) < 8) {
				String name = effect.name();
				if (data != null) {
					name += data.getPacketDataString();
				}
				Reflections.setField(packet, part[0], name);
			} else {
				Reflections.setField(packet, part[0], Reflections.get(Reflections.getField(particleEnum, effect.name()),null));
				Reflections.setField(packet, part[1], false);
				if (data != null) {
					int[] packetData = data.getPacketData();
					Reflections.setField(packet, part[2], effect == Particle.ITEM_CRACK ? packetData
							: new int[] { packetData[0] | (packetData[1] << 12) });
				}
			}
			Reflections.setField(packet, part[3], x);
			Reflections.setField(packet, part[4], y);
			Reflections.setField(packet, part[5], z);
			Reflections.setField(packet, part[6], floatx);
			Reflections.setField(packet, part[7], floaty);
			Reflections.setField(packet, part[8], floatz);
			Reflections.setField(packet, part[9], speed);
			Reflections.setField(packet, part[10], amount);
			return packet;
	}

	public static Object getChunk(Object World, int x, int z) {
		return Reflections.invoke(World, getc, x,z);
	}

	public static Object getIBlockData(Object World, Object blockPosition) {
		return Reflections.invoke(World, gett, blockPosition);
	}

	public static Object getIBlockData(Object World, int x, int y, int z) {
		return getIBlockData(World, getBlockPosition(x, y, z));
	}

	public static Object getIBlockData(World world, Object blockPosition) {
		return getIBlockData(getWorld(world), blockPosition);
	}

	public static Object getIBlockData(World world, int x, int y, int z) {
		return getIBlockData(getWorld(world), getBlockPosition(x, y, z));
	}

	public static Object getChunk(World world, int x, int z) {
		return getChunk(getWorld(world), x, z);
	}

	public static Object getPacketPlayOutTitle(TitleAction action, Object IChatBaseComponent) {
		return getPacketPlayOutTitle(action, IChatBaseComponent, 10, 20, 10);
	}

	public static Object getPacketPlayOutOpenWindow(int id, String container, String text) {
		return getPacketPlayOutOpenWindow(id, container, getIChatBaseComponentText(text));
	}

	public static Object getPacketPlayOutOpenWindow(int id, String container, Object IChatBaseComponent) {
		Object o = Reflections.c(pWindow, id, container, IChatBaseComponent);
		return o!=null ? o : Reflections.c(pWindow, id,  Reflections.get(Reflections.getField(Containers, container),null), IChatBaseComponent);
	}

	public static Object getPacketPlayOutTitle(TitleAction action, Object IChatBaseComponent, int fadeIn, int stay, int fadeOut) {
		if(action==TitleAction.ACTIONBAR) {
			Object o =Reflections.c(pTitle, Reflections.get(Reflections.getField(enumTitle, action.name()),null), IChatBaseComponent, fadeIn, stay,fadeOut);
			return o!=null?o:Reflections.c(pOutChat,IChatBaseComponent, (byte)2);
		}
		return Reflections.c(pTitle, Reflections.get(Reflections.getField(enumTitle, action.name()),null), IChatBaseComponent, fadeIn, stay,fadeOut);
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
		Object o = Reflections.c(pChunk,Chunk, workers);
		return o != null ? o : Reflections.c(pChunk,Chunk, value, workers);
	}

	public static void refleshBlock(Object world, Object blockposition, Object oldBlock, Object newBlock) {
		Reflections.invoke(world, Reflections.getMethod(NMSAPI.world, "notify", pos, iblockdata, iblockdata, int.class),blockposition, oldBlock, newBlock, 3);
	}
	
	public static Object getPacketPlayOutChat(ChatType type, Object IChatBaseComponent) {
		Object o = old==2?Reflections.c(pOutChat,IChatBaseComponent, (byte)1):
			(old==0?Reflections.c(pOutChat,IChatBaseComponent, Reflections.get(Reflections.getField(enumChat, type.name()),null)):Reflections.c(pOutChat,IChatBaseComponent, Reflections.get(Reflections.getField(enumChat, type.name()),null), UUID.randomUUID()));
		return o;
	}

	public static Object getPacketPlayOutChat(ChatType type, String text) {
		return getPacketPlayOutChat(type, getIChatBaseComponentText(text));
	}

	public static Object getType(World w, int x, int y, int z) {
		return Reflections.invoke(getWorld(w), Reflections.getMethod(world, "getType", pos), getBlockPosition(x, y, z));
	}

	public static void setBlock(World world, int x, int y, int z, Material material, int data, boolean applyPsychics) {
		world.getBlockAt(x, y, z).getDrops().clear();
		Object old = getIBlockData(world, x,y,z);
		Object newIblock = getIBlockData(material, data), position = getBlockPosition(x, y, z), World = getWorld(world);
		Reflections.invoke(World, worldset, position, newIblock, applyPsychics ? 3 : 2);
		LoaderClass.plugin.refleshing.add(new Object[] {getWorld(world),getBlockPosition(x, y, z),old,newIblock});
	}

	public static void setBlock(World world, int x, int y, int z, Material material, boolean applyPsychics) {
		setBlock(world, x, y, z, material, 0, applyPsychics);
	}

	public static void setBlock(Location loc, Material material, int data, boolean applyPsychics) {
		setBlock(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), material, data,
				applyPsychics);
	}

	public static Object getIBlockData(Material material) {
		return getIBlockData(material, 0);
	}

	@SuppressWarnings("deprecation")
	public static Object getIBlockData(Material material, int data) {
		try {
			// 1.13+ only
			Object o = Reflections.cast(Reflections.getNMSClass("block.data.CraftBlockData"), Bukkit.createBlockData(material));
			return Reflections.invoke(o,Reflections.getMethod(Reflections.getNMSClass("block.data.CraftBlockData"),"getState"));
		} catch (NoSuchMethodError er) {
			try {
			Object o = Reflections.invoke(Reflections.invoke(null, block, material.getId()),IBlockData,data);
			return o != null ? o : Reflections.invoke(null, IBlockData, material.getId() + (data << 12));
			}catch(Exception e) {
				return Reflections.invoke(null, IBlockData, material.getId() + (data << 12));
			}
		}
	}

	public static Object getPacketPlayOutEntityDestroy(int... id) {
		return Reflections.c(pDestroy, id);
	}

	public static Object getEntity(Entity entity) {
		return Reflections.invoke(Reflections.cast(Reflections.getBukkitClass("entity.CraftEntity"), entity),entityM);
	}

	public static Object getEntityLiving(LivingEntity entity) {
		return Reflections.invoke(Reflections.cast(Reflections.getBukkitClass("entity.CraftLivingEntity"), entity),livingentity);
	}

	public static Object getPacketPlayOutSpawnEntity(Object entity, int id) {
		return Reflections.c(pSpawn, entity, id);
	}

	public static Object getPacketPlayOutNamedEntitySpawn(Object Player) {
		return Reflections.c(pNSpawn, Player);
	}

	public static Object getPacketPlayOutSpawnEntityLiving(Object entityLiving) {
		return Reflections.c(pLSpawn, entityLiving);
	}

	public static Object getPacketPlayOutPlayerListHeaderFooter(Object headerIChatBaseComponent, Object footerIChatBaseComponent) {
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

	public static Object getPacketPlayOutPlayerListHeaderFooter(String header, String footer) {
		return getPacketPlayOutPlayerListHeaderFooter(getIChatBaseComponentText(header),
				getIChatBaseComponentText(footer));
	}

	public static Object getCraftWorld(World world) {
		return bWorld.cast(world);
	}

	public static Object getWorld(World world) {
		return getWorld(getCraftWorld(world));
	}

	public static Object getWorld(Object craftWorld) {
		return Reflections.invoke(craftWorld, WorldHandle);
	}

	public static Object getCraftPlayer(Player player) {
		return Reflections.cast(bPlayer, player);
	}

	public static Object getPlayer(Player player) {
		return getPlayer(getCraftPlayer(player));
	}

	public static Object getPlayer(Object CraftPlayer) {
		return Reflections.invoke(CraftPlayer,PlayerHandle);
	}

	public static void sendPacket(Player player, Object packet) {
		getNMSPlayerAPI(player).sendPacket(packet);
	}

	public static Object getPacketPlayOutBlockChange(Object World, int x, int y, int z) {
		return getPacketPlayOutBlockChange(World, getBlockPosition(x, y, z));
	}

	public static Object getPacketPlayOutBlockChange(World world, Object BlockPosition) {
		return getPacketPlayOutBlockChange(getWorld(world), BlockPosition);
	}

	public static Object getPacketPlayOutBlockChange(World world, int x, int y, int z) {
		return getPacketPlayOutBlockChange(getWorld(world), getBlockPosition(x, y, z));
	}

	public static Object getPacketPlayOutBlockChange(Object World, Object BlockPosition) {
		return Reflections.c(pBlock, World, BlockPosition);
	}

	public static Object getBlockPosition(int x, int y, int z) {
		return Reflections.c(blockPos, x, y, z);
	}

	public static Object getIChatBaseComponentText(String text) {
		return getIChatBaseComponentJson("{\"text\":\"" + text + "\"}");
	}
	
	public static Object getIChatBaseComponentJson(String json) {
			if (oldichatser != null)
				return Reflections.invoke(null, oldichatser, json);
			return Reflections.invoke(null, ichatcon, json);
	}

	public static Thread getServerThread() {
		Object o = Reflections.get(Reflections.getField(Reflections.getNMSClass("MinecraftServer"),"primaryThread"),getServer());
		return o != null ? (Thread)o : (Thread)Reflections.get(Reflections.getField(Reflections.getNMSClass("MinecraftServer"),"serverThread"),getServer());
	}
}
