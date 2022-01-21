package me.devtec.theapi.nms;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_7_R4.CraftChunk;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_7_R4.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.guiapi.AnvilGUI;
import me.devtec.theapi.guiapi.GUI.ClickType;
import me.devtec.theapi.guiapi.HolderGUI;
import me.devtec.theapi.utils.InventoryUtils;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.TheMaterial;
import me.devtec.theapi.utils.InventoryUtils.DestinationType;
import me.devtec.theapi.utils.components.Component;
import me.devtec.theapi.utils.components.ComponentAPI;
import me.devtec.theapi.utils.listener.events.ServerListPingEvent;
import me.devtec.theapi.utils.nms.NmsProvider;
import me.devtec.theapi.utils.nms.nbt.NBTEdit;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.serverlist.PlayerProfile;
import me.devtec.theapi.utils.theapiutils.LoaderClass;
import me.devtec.theapi.utils.theapiutils.LoaderClass.InventoryClickType;
import net.minecraft.server.v1_7_R4.Block;
import net.minecraft.server.v1_7_R4.Blocks;
import net.minecraft.server.v1_7_R4.ChatClickable;
import net.minecraft.server.v1_7_R4.ChatComponentText;
import net.minecraft.server.v1_7_R4.ChatHoverable;
import net.minecraft.server.v1_7_R4.ChatModifier;
import net.minecraft.server.v1_7_R4.ChatSerializer;
import net.minecraft.server.v1_7_R4.ChunkCoordIntPair;
import net.minecraft.server.v1_7_R4.ChunkPosition;
import net.minecraft.server.v1_7_R4.ChunkSection;
import net.minecraft.server.v1_7_R4.Container;
import net.minecraft.server.v1_7_R4.ContainerAnvil;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.EnumChatFormat;
import net.minecraft.server.v1_7_R4.EnumClickAction;
import net.minecraft.server.v1_7_R4.EnumHoverAction;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.server.v1_7_R4.IContainer;
import net.minecraft.server.v1_7_R4.Item;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.MojangsonParser;
import net.minecraft.server.v1_7_R4.NBTBase;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NetworkManager;
import net.minecraft.server.v1_7_R4.PacketPlayInWindowClick;
import net.minecraft.server.v1_7_R4.PacketPlayOutBlockChange;
import net.minecraft.server.v1_7_R4.PacketPlayOutChat;
import net.minecraft.server.v1_7_R4.PacketPlayOutCloseWindow;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R4.PacketPlayOutExperience;
import net.minecraft.server.v1_7_R4.PacketPlayOutHeldItemSlot;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_7_R4.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R4.PacketPlayOutPosition;
import net.minecraft.server.v1_7_R4.PacketPlayOutRespawn;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_7_R4.PacketPlayOutSetSlot;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R4.PacketStatusOutServerInfo;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import net.minecraft.server.v1_7_R4.ScoreboardObjective;
import net.minecraft.server.v1_7_R4.ServerPing;
import net.minecraft.server.v1_7_R4.ServerPingPlayerSample;
import net.minecraft.server.v1_7_R4.ServerPingServerData;
import net.minecraft.server.v1_7_R4.TileEntity;
import net.minecraft.server.v1_7_R4.WorldServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.io.netty.channel.Channel;

public class v1_7_R4 implements NmsProvider {
	private MinecraftServer server = MinecraftServer.getServer();
	private static final ChatComponentText empty = new ChatComponentText("");
	private static Field channel = Ref.field(NetworkManager.class, "m");
	private static Field score_a = Ref.field(PacketPlayOutScoreboardScore.class, "a"), score_b = Ref.field(PacketPlayOutScoreboardScore.class, "b"), score_c = Ref.field(PacketPlayOutScoreboardScore.class, "c"), score_d = Ref.field(PacketPlayOutScoreboardScore.class, "d");

	@Override
	public Object getEntity(Entity entity) {
		return ((CraftEntity)entity).getHandle();
	}

	@Override
	public Object getEntityLiving(LivingEntity entity) {
		return ((CraftLivingEntity)entity).getHandle();
	}

	@Override
	public Object getPlayer(Player player) {
		return ((CraftPlayer)player).getHandle();
	}

	@Override
	public Object getWorld(World world) {
		return ((CraftWorld)world).getHandle();
	}

	@Override
	public Object getChunk(Chunk chunk) {
		return ((CraftChunk)chunk).getHandle();
	}

	@Override
	public Object getScoreboardAction(Action type) {
		return type.getId();
	}

	@Override
	public int getEntityId(Object entity) {
		return ((net.minecraft.server.v1_7_R4.Entity)entity).getId();
	}

	@Override
	public Object getEnumScoreboardHealthDisplay(DisplayType type) {
		return null;
	}

	@Override
	public Object getNBT(ItemStack itemStack) {
		net.minecraft.server.v1_7_R4.ItemStack item = ((net.minecraft.server.v1_7_R4.ItemStack)asNMSItem(itemStack));
		NBTTagCompound nbt = item.getTag();
		if(nbt==null)item.setTag(nbt=new NBTTagCompound());
		return nbt;
	}

	@Override
	public Object parseNBT(String json) {
		try {
			return MojangsonParser.parse(json);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public ItemStack setNBT(ItemStack stack, Object nbt) {
		if(nbt instanceof NBTEdit)nbt=((NBTEdit) nbt).getNBT();
		net.minecraft.server.v1_7_R4.ItemStack i = (net.minecraft.server.v1_7_R4.ItemStack)asNMSItem(stack);
		i.setTag((NBTTagCompound) nbt);
		return asBukkitItem(i);
	}
	
	private static final net.minecraft.server.v1_7_R4.ItemStack air = CraftItemStack.asNMSCopy(new ItemStack(Material.AIR));

	@Override
	public Object asNMSItem(ItemStack stack) {
		if(stack==null)return air;
		return CraftItemStack.asNMSCopy(stack);
	}

	@Override
	public ItemStack asBukkitItem(Object stack) {
		return CraftItemStack.asCraftMirror((net.minecraft.server.v1_7_R4.ItemStack) stack);
	}

	@Override
	public Object packetOpenWindow(int id, String legacy, int size, String title) {
		return new PacketPlayOutOpenWindow(id, legacy.equals("minecraft:chest")?0:8, title, size, false);
	} 
	
	public int getContainerId(Object container) {
		return ((Container)container).windowId;
	}
	
	@Override
	public Object packetResourcePackSend(String url, String hash, boolean requireRP, String prompt) {
		return null;
	}

	@Override
	public Object packetSetSlot(int container, int slot, int stateId, Object itemStack) {
		return new PacketPlayOutSetSlot(container, slot, (net.minecraft.server.v1_7_R4.ItemStack)(itemStack==null?asNMSItem(null):itemStack));
	}

	public Object packetSetSlot(int container, int slot, Object itemStack) {
		return packetSetSlot(container,slot,0,itemStack);
	}

	@Override
	public Object packetEntityMetadata(int entityId, Object dataWatcher, boolean bal) {
		return new PacketPlayOutEntityMetadata(entityId, (net.minecraft.server.v1_7_R4.DataWatcher) dataWatcher, bal);
	}

	@Override
	public Object packetEntityDestroy(int... ids) {
		return new PacketPlayOutEntityDestroy(ids);
	}

	@Override
	public Object packetSpawnEntity(Object entity, int id) {
		return new PacketPlayOutSpawnEntity((net.minecraft.server.v1_7_R4.Entity) entity, id);
	}

	@Override
	public Object packetNamedEntitySpawn(Object player) {
		return new PacketPlayOutNamedEntitySpawn((EntityHuman)player);
	}

	@Override
	public Object packetSpawnEntityLiving(Object entityLiving) {
		return new PacketPlayOutSpawnEntityLiving((EntityLiving)entityLiving);
	}

	@Override
	public Object packetPlayerListHeaderFooter(String header, String footer) {
		return null;
	}

	@Override
	public Object packetBlockChange(World world, Position position) {
		return new PacketPlayOutBlockChange(position.getBlockX(),position.getBlockY(),position.getBlockZ(),(net.minecraft.server.v1_7_R4.World)getWorld(world));
	}

	@Override
	public Object packetBlockChange(World world, int x, int y, int z) {
		return new PacketPlayOutBlockChange(x,y,z,(net.minecraft.server.v1_7_R4.World)getWorld(world));
	}

	@Override
	public Object packetScoreboardObjective() {
		return new PacketPlayOutScoreboardObjective();
	}

	@Override
	public Object packetScoreboardDisplayObjective(int id, Object scoreboardObjective) {
		return new PacketPlayOutScoreboardDisplayObjective(id, scoreboardObjective==null?null:(ScoreboardObjective)scoreboardObjective);
	}

	@Override
	public Object packetScoreboardTeam() {
		return new PacketPlayOutScoreboardTeam();
	}
	
	@Override
	public Object packetScoreboardScore(Action action, String player, String line, int score) {
		PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();
		try {
			score_a.set(packet, line);
			score_b.set(packet, player);
			score_c.set(packet, score);
			score_d.set(packet, getScoreboardAction(action));
		}catch(Exception err) {}
		return packet;
	}

	@Override
	public Object packetTitle(TitleAction action, String text, int fadeIn, int stay, int fadeOut) {
		if(action==TitleAction.ACTIONBAR)return packetChat(ChatType.GAME_INFO, text, null);
		return null;
	}

	@Override
	public Object packetChat(ChatType type, Object chatBase, UUID uuid) {
		return new PacketPlayOutChat((IChatBaseComponent)chatBase, type.toByte());
	}

	@Override
	public Object packetChat(ChatType type, String text, UUID uuid) {
		return packetChat(type, ComponentAPI.toIChatBaseComponent(ComponentAPI.toComponent(text, false)), uuid);
	}

	@Override
	public void postToMainThread(Runnable runnable) {
		server.processQueue.add(runnable);
	}

	@Override
	public Object getMinecraftServer() {
		return server;
	}

	@Override
	public Thread getServerThread() {
		return server.primaryThread;
	}

	@Override
	public double[] getServerTPS() {
		return server.recentTps;
	}

	@Override
	public Object toIChatBaseComponents(List<Component> components) {
		List<IChatBaseComponent> chat = new ArrayList<>();
		chat.add(new ChatComponentText(""));
		for(Component c : components) {
			if(c.getText()==null||c.getText().isEmpty()) {
				c=c.getExtra();
				continue;
			}
			ChatComponentText current = new ChatComponentText(c.getText());
			chat.add(current);
			ChatModifier modif = current.getChatModifier();
			if(c.getColor()!=null && !c.getColor().isEmpty()) {
				modif=modif.setColor(EnumChatFormat.valueOf(ChatColor.getByChar(c.getColor().charAt(0)).name()));
			}
			if(c.getClickEvent()!=null)
				modif=modif.setChatClickable(new ChatClickable(EnumClickAction.valueOf(c.getClickEvent().getAction().name()), c.getClickEvent().getValue()));
			if(c.getHoverEvent()!=null)
				modif=modif.a(new ChatHoverable(EnumHoverAction.valueOf(c.getHoverEvent().getAction().name()), (IChatBaseComponent)toIChatBaseComponent(c.getHoverEvent().getValue())));
			modif=modif.setBold(c.isBold());
			modif=modif.setItalic(c.isItalic());
			modif=modif.setRandom(c.isObfuscated());
			modif=modif.setUnderline(c.isUnderlined());
			modif=modif.setStrikethrough(c.isStrikethrough());
			current.setChatModifier(modif);
		}
		return chat.toArray(new IChatBaseComponent[0]);
	}

	@Override
	public Object toIChatBaseComponents(Component c) {
		List<IChatBaseComponent> chat = new ArrayList<>();
		chat.add(new ChatComponentText(""));
		while(c!=null) {
			if(c.getText()==null||c.getText().isEmpty()) {
				c=c.getExtra();
				continue;
			}
			ChatComponentText current = new ChatComponentText(c.getText());
			chat.add(current);
			ChatModifier modif = current.getChatModifier();
			if(c.getColor()!=null && !c.getColor().isEmpty()) {
				modif=modif.setColor(EnumChatFormat.valueOf(ChatColor.getByChar(c.getColor().charAt(0)).name()));
			}
			if(c.getClickEvent()!=null)
				modif=modif.setChatClickable(new ChatClickable(EnumClickAction.valueOf(c.getClickEvent().getAction().name()), c.getClickEvent().getValue()));
			if(c.getHoverEvent()!=null)
				modif=modif.a(new ChatHoverable(EnumHoverAction.valueOf(c.getHoverEvent().getAction().name()), (IChatBaseComponent)toIChatBaseComponent(c.getHoverEvent().getValue())));
			modif=modif.setBold(c.isBold());
			modif=modif.setItalic(c.isItalic());
			modif=modif.setRandom(c.isObfuscated());
			modif=modif.setUnderline(c.isUnderlined());
			modif=modif.setStrikethrough(c.isStrikethrough());
			current.setChatModifier(modif);
			c=c.getExtra();
		}
		return chat.toArray(new IChatBaseComponent[0]);
	}

	@Override
	public Object toIChatBaseComponent(Component c) {
		ChatComponentText main = new ChatComponentText("");
		while(c!=null) {
			if(c.getText()==null||c.getText().isEmpty()) {
				c=c.getExtra();
				continue;
			}
			ChatComponentText current = new ChatComponentText(c.getText());
			main.addSibling(current);
			ChatModifier modif = current.getChatModifier();
			if(c.getColor()!=null && !c.getColor().isEmpty()) {
				modif=modif.setColor(EnumChatFormat.valueOf(ChatColor.getByChar(c.getColor().charAt(0)).name()));
			}
			if(c.getClickEvent()!=null)
				modif=modif.setChatClickable(new ChatClickable(EnumClickAction.valueOf(c.getClickEvent().getAction().name()), c.getClickEvent().getValue()));
			if(c.getHoverEvent()!=null)
				modif=modif.a(new ChatHoverable(EnumHoverAction.valueOf(c.getHoverEvent().getAction().name()), (IChatBaseComponent)toIChatBaseComponent(c.getHoverEvent().getValue())));
			modif=modif.setBold(c.isBold());
			modif=modif.setItalic(c.isItalic());
			modif=modif.setRandom(c.isObfuscated());
			modif=modif.setUnderline(c.isUnderlined());
			modif=modif.setStrikethrough(c.isStrikethrough());
			current.setChatModifier(modif);
			c=c.getExtra();
		}
		return main.a().isEmpty()?empty:main;
	}

	@Override
	public Object toIChatBaseComponent(List<Component> cc) {
		ChatComponentText main = new ChatComponentText("");
		for(Component c : cc) {
			if(c.getText()==null||c.getText().isEmpty()) {
				c=c.getExtra();
				continue;
			}
			ChatComponentText current = new ChatComponentText(c.getText());
			main.addSibling(current);
			ChatModifier modif = current.getChatModifier();
			if(c.getColor()!=null && !c.getColor().isEmpty()) {
				modif=modif.setColor(EnumChatFormat.valueOf(ChatColor.getByChar(c.getColor().charAt(0)).name()));
			}
			if(c.getClickEvent()!=null)
				modif=modif.setChatClickable(new ChatClickable(EnumClickAction.valueOf(c.getClickEvent().getAction().name()), c.getClickEvent().getValue()));
			if(c.getHoverEvent()!=null)
				modif=modif.a(new ChatHoverable(EnumHoverAction.valueOf(c.getHoverEvent().getAction().name()), (IChatBaseComponent)toIChatBaseComponent(c.getHoverEvent().getValue())));
			modif=modif.setBold(c.isBold());
			modif=modif.setItalic(c.isItalic());
			modif=modif.setRandom(c.isObfuscated());
			modif=modif.setUnderline(c.isUnderlined());
			modif=modif.setStrikethrough(c.isStrikethrough());
			current.setChatModifier(modif);
		}
		return main.a().isEmpty()?empty:main;
	}

	@Override
	public Object chatBase(String json) {
		return ChatSerializer.a(json);
	}

	@Override
	public String fromIChatBaseComponent(Object component) {
		if(component==null)return null;
		if(component instanceof IChatBaseComponent[]) {
			IChatBaseComponent[] cchat = (IChatBaseComponent[])component;
			StringBuilder builder = new StringBuilder();
			for(IChatBaseComponent chat : cchat) {
				builder.append(asString(chat.getChatModifier())).append(chat.e());
				for(Object c : chat.a()) {
					builder.append(asString(((IChatBaseComponent)c).getChatModifier())).append(((IChatBaseComponent)c).e());
				}
			}
			return builder.toString();
		}
		if(component instanceof IChatBaseComponent) {
			IChatBaseComponent chat = (IChatBaseComponent)component;
			StringBuilder builder = new StringBuilder();
			builder.append(asString(chat.getChatModifier())).append(chat.e());
			for(Object c : chat.a()) {
				builder.append(asString(((IChatBaseComponent)c).getChatModifier())).append(((IChatBaseComponent)c).e());
			}
			return builder.toString();
		}
		return component.toString();
	}

	private StringBuilder asString(ChatModifier chatModifier) {
		StringBuilder builder = new StringBuilder();
		if(chatModifier.a()!=null)builder.append('§').append(chatModifier.a().getChar());
		if(chatModifier.b())builder.append('§').append('l');
		if(chatModifier.c())builder.append('§').append('o');
		if(chatModifier.d())builder.append('§').append('m');
		if(chatModifier.e())builder.append('§').append('n');
		if(chatModifier.f())builder.append('§').append('k');
		return builder;
	}

	@Override
	public TheMaterial toMaterial(Object blockOrItemOrIBlockData) {
		if(blockOrItemOrIBlockData instanceof Block) {
			Block b = (Block)blockOrItemOrIBlockData;
			return new TheMaterial((ItemStack)CraftItemStack.asNewCraftStack(Item.getItemOf(b)));
		}
		if(blockOrItemOrIBlockData instanceof Item) {
			Item b = (Item)blockOrItemOrIBlockData;
			return new TheMaterial((ItemStack)CraftItemStack.asNewCraftStack(b));
		}
		return null;
	}

	@Override
	public Object toIBlockData(TheMaterial material) {
		return toItem(material);
	}

	@Override
	public Object toItem(TheMaterial material) {
		return CraftItemStack.asNMSCopy(material.toItemStack()).getItem();
	}

	@Override
	public Object toBlock(TheMaterial material) {
		return CraftMagicNumbers.getBlock(material.getType());
	}

	@Override
	public Object getChunk(World world, int x, int z) {
		WorldServer sworld = ((CraftWorld)world).getHandle();
		net.minecraft.server.v1_7_R4.Chunk loaded = sworld.chunkProviderServer.getChunkIfLoaded(x, z);
		if(loaded==null) { //load chunk
			net.minecraft.server.v1_7_R4.Chunk chunk = sworld.chunkProviderServer.loadChunk(x, z);
			if(chunk!=null) {
				sworld.chunkProviderServer.chunks.put(ChunkCoordIntPair.a(x,z), chunk);
				chunk.addEntities();
				loaded=chunk;
			}
		}
		if(loaded==null) { //generate new chunk
			loaded=sworld.chunkProviderServer.chunkProvider.getOrCreateChunk(x,z);
			sworld.chunkProviderServer.chunks.put(ChunkCoordIntPair.a(x,z), loaded);
		}
		return loaded;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setBlock(Object chunk, int x, int y, int z, Object block, int data) {
		net.minecraft.server.v1_7_R4.Chunk c = (net.minecraft.server.v1_7_R4.Chunk)chunk;
		ChunkSection sc = c.getSections()[y>>4];
		if(sc==null) {
			c.getSections()[y>>4]=sc=new ChunkSection(y >> 4 << 4, true);
		}
		ChunkPosition pos = new ChunkPosition(x&15,y&15,z&15);
		//REMOVE TILE ENTITY
		c.tileEntities.remove(pos);

		sc.setTypeId(x&15, y&15, z&15, (Block)block);
		sc.setData(x&15, y&15, z&15, data);
		
		//ADD TILE ENTITY
		if(block instanceof IContainer) {
			TileEntity ent = ((IContainer)block).a(c.world, 0);
			c.tileEntities.put(pos,ent);
			Ref.sendPacket(c.bukkitChunk.getWorld().getPlayers(), ent.getUpdatePacket());
		}
	}

	@Override
	public void updateLightAt(Object chunk, int x, int y, int z) {
		net.minecraft.server.v1_7_R4.Chunk c = (net.minecraft.server.v1_7_R4.Chunk)chunk;
		c.initLighting();
	}

	@Override
	public Object getBlock(Object chunk, int x, int y, int z) {
		net.minecraft.server.v1_7_R4.Chunk c = (net.minecraft.server.v1_7_R4.Chunk)chunk;
		ChunkSection sc = c.getSections()[y>>4];
		if(sc==null)return Blocks.AIR;
		return sc.getTypeId(x&15, y&15, z&15);
	}

	@Override
	public int getData(Object chunk, int x, int y, int z) {
		net.minecraft.server.v1_7_R4.Chunk c = (net.minecraft.server.v1_7_R4.Chunk)chunk;
		ChunkSection sc = c.getSections()[y>>4];
		if(sc==null)return 0;
		return sc.getData(x&15, y&15, z&15);
	}

	@Override
	public int getCombinedId(Object IblockDataOrBlock) {
		return Block.getId((Block)IblockDataOrBlock);
	}

	@Override
	public Object blockPosition(int blockX, int blockY, int blockZ) {
		return new ChunkPosition(blockX, blockY, blockZ);
	}

	@Override
	public Object toIBlockData(BlockState state) {
		return null;
	}

	@Override
	public Object toIBlockData(Object data) {
		return null;
	}

	@Override
	public Object toBlock(Material type) {
		return CraftMagicNumbers.getBlock(type);
	}

	@Override
	public Object toItem(Material type, int data) {
		return CraftMagicNumbers.getItem(type);
	}

	@Override
	public Object toIBlockData(Material type, int data) {
		return CraftMagicNumbers.getBlock(type);
	}

	@Override
	public Chunk toBukkitChunk(Object nmsChunk) {
		return ((net.minecraft.server.v1_7_R4.Chunk)nmsChunk).bukkitChunk;
	}

	@Override
	public int getPing(Player player) {
		return ((EntityPlayer)getPlayer(player)).ping;
	}

	@Override
	public Object getPlayerConnection(Player player) {
		return ((EntityPlayer)getPlayer(player)).playerConnection;
	}

	@Override
	public Object getConnectionNetwork(Object playercon) {
		return ((PlayerConnection)playercon).networkManager;
	}
	
	@Override
	public Object getNetworkChannel(Object network) {
		try {
			return channel.get(network);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public void closeGUI(Player player, Object container, boolean closePacket) {
		if(closePacket)
		Ref.sendPacket(player, new PacketPlayOutCloseWindow(LoaderClass.nmsProvider.getContainerId(container)));
		EntityPlayer nmsPlayer = (EntityPlayer)getPlayer(player);
		nmsPlayer.activeContainer=nmsPlayer.defaultContainer;
		((Container)container).transferTo(nmsPlayer.activeContainer, (CraftPlayer)player);
	}

	@Override
	public void setSlot(Object container, int slot, Object item) {
		((Container)container).setItem(slot, (net.minecraft.server.v1_7_R4.ItemStack)item);
	}

	@Override
	public void setGUITitle(Player player, Object container, String legacy, int size, String title) {
		EntityPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
		int id = ((Container)container).windowId;
		@SuppressWarnings("unchecked")
		List<net.minecraft.server.v1_7_R4.ItemStack> nmsItems = ((Container)container).b;
		Ref.sendPacket(player, packetOpenWindow(id,legacy,size,title));
		int i = 0;
		for(net.minecraft.server.v1_7_R4.ItemStack o : nmsItems) 
			Ref.sendPacket(player, packetSetSlot(id,i++, o));
		nmsPlayer.activeContainer=(Container)container;
		((Container)container).addSlotListener(nmsPlayer);
		((Container)container).checkReachable=false;
	}

	@Override
	public void openGUI(Player player, Object container, String legacy, int size, String title, ItemStack[] items) {
		EntityPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
		int id = ((Container)container).windowId;
		net.minecraft.server.v1_7_R4.ItemStack[] nmsItems = new net.minecraft.server.v1_7_R4.ItemStack[items.length];
		for(int i = 0; i < items.length; ++i) {
			ItemStack is = items[i];
			if(is==null||is.getType()==Material.AIR)continue;
			net.minecraft.server.v1_7_R4.ItemStack item = null;
			((Container)container).setItem(i,item=(net.minecraft.server.v1_7_R4.ItemStack) asNMSItem(is));
			nmsItems[i]=item;
		}
		Ref.sendPacket(player, packetOpenWindow(id,legacy,size,title));
		int i = 0;
		for(net.minecraft.server.v1_7_R4.ItemStack o : nmsItems) 
			Ref.sendPacket(player, packetSetSlot(id,i++, o));
		nmsPlayer.activeContainer.transferTo((Container)container, (CraftPlayer) player);
		nmsPlayer.activeContainer=(Container)container;
		((Container)container).addSlotListener(nmsPlayer);
		((Container)container).checkReachable=false;
	}
	
	@Override
	public void openAnvilGUI(Player player, Object con, String title, ItemStack[] items) {
		ContainerAnvil container = (ContainerAnvil)con;
		EntityPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
		int id = container.windowId;
		net.minecraft.server.v1_7_R4.ItemStack[] nmsItems = new net.minecraft.server.v1_7_R4.ItemStack[items.length];
		for(int i = 0; i < items.length; ++i) {
			ItemStack is = items[i];
			if(is==null||is.getType()==Material.AIR)continue;
			net.minecraft.server.v1_7_R4.ItemStack item = null;
			container.setItem(i,item=(net.minecraft.server.v1_7_R4.ItemStack) asNMSItem(is));
			nmsItems[i]=item;
		}
		Ref.sendPacket(player, packetOpenWindow(id,"minecraft:anvil",0,title));
		int i = 0;
		for(net.minecraft.server.v1_7_R4.ItemStack o : nmsItems) 
			Ref.sendPacket(player, packetSetSlot(id,i++, o));
		nmsPlayer.activeContainer.transferTo((Container)container, (CraftPlayer) player);
		nmsPlayer.activeContainer=(Container)container;
		((Container)container).addSlotListener(nmsPlayer);
		container.checkReachable=false;
	}

	@Override
	public Object createContainer(Inventory inv, Player player) {
		return inv.getType()==InventoryType.ANVIL?createAnvilContainer(inv, player):new CraftContainer(inv, player, ((CraftPlayer)player).getHandle().nextContainerCounter());
	}

	@Override
	public Object getSlotItem(Object container, int slot) {
		return ((Container)container).getSlot(slot).getItem();
	}
	
	public Object createAnvilContainer(Inventory inv, Player player) {
		int id = ((CraftPlayer)player).getHandle().nextContainerCounter();
		ContainerAnvil anvil = new ContainerAnvil(((CraftPlayer)player).getHandle().inventory,((CraftPlayer)player).getHandle().world, 0,0,0,((CraftPlayer)player).getHandle());
		anvil.windowId=id;
		for(int i = 0; i < 2; ++i)
			anvil.setItem(i, (net.minecraft.server.v1_7_R4.ItemStack) asNMSItem(inv.getItem(i)));
		return anvil;
	}
	
	static Field renameText = Ref.field(ContainerAnvil.class, "n");
	
	@Override
	public String getAnvilRenameText(Object anvil) {
		try {
			return (String) renameText.get((ContainerAnvil)anvil);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public boolean processInvClickPacket(Player player, HolderGUI gui, Object provPacket) {
		PacketPlayInWindowClick packet = (PacketPlayInWindowClick)provPacket;
		int slot = packet.slot;
		if(slot==-999)return false;
		
		int id = packet.c();
		int mouseClick = packet.e();
		InventoryClickType type = InventoryClickType.values()[packet.h()];
		
		Object container = gui.getContainer(player);
		ItemStack item = asBukkitItem(packet.f());
		if((type==InventoryClickType.QUICK_MOVE||type==InventoryClickType.CLONE||type==InventoryClickType.THROW||item.getType()==Material.AIR) && item.getType()==Material.AIR)
			item=asBukkitItem(getSlotItem(container, slot));
		if(InventoryClickType.SWAP==type) {
			item=player.getInventory().getItem(mouseClick);
			mouseClick=0;
		}
		if(item==null)item=new ItemStack(Material.AIR);
		
		ItemStack before = player.getItemOnCursor();
		ClickType clickType = LoaderClass.buildClick(item, type, slot, mouseClick);
		boolean cancel = LoaderClass.useItem(player, item, gui, slot, clickType);
		if(!gui.isInsertable())cancel=true;
		int gameSlot = slot>gui.size()-1?InventoryUtils.convertToPlayerInvSlot(slot-gui.size()):slot;
		if(!cancel)cancel=gui.onIteractItem(player, item, clickType, gameSlot, slot<gui.size());
		else gui.onIteractItem(player, item, clickType, gameSlot, slot<gui.size());
		int position = 0;
		if(!cancel && type==InventoryClickType.QUICK_MOVE) {
			ItemStack[] contents = slot<gui.size()?player.getInventory().getContents():gui.getInventory().getContents();
			List<Integer> modified = slot<gui.size()?InventoryUtils.shift(slot,player,gui,clickType,gui instanceof AnvilGUI?DestinationType.PLAYER_INV_ANVIL:DestinationType.PLAYER_INV_CUSTOM_INV,null, contents, item):InventoryUtils.shift(slot,player,gui,clickType,DestinationType.CUSTOM_INV,gui.getNotInterableSlots(player), contents, item);
			if(!modified.isEmpty()) {
				if(slot<gui.size()) {
					boolean canRemove = !modified.contains(-1);
					player.getInventory().setContents(contents);
					if(canRemove) {
						gui.remove(gameSlot);
					}else {
						gui.getInventory().setItem(gameSlot, item);
					}
				}else {
					boolean canRemove = !modified.contains(-1);
					gui.getInventory().setContents(contents);
					if(canRemove) {
						player.getInventory().setItem(gameSlot, null);
					}else {
						player.getInventory().setItem(gameSlot, item);
					}
				}
			}
			return true;
		}else
			if(type==InventoryClickType.QUICK_MOVE)cancel=true;
		if(cancel) {
			//MOUSE
			Ref.sendPacket(player,packetSetSlot(-1, -1, asNMSItem(before)));
			switch(type) {
			case CLONE:
				return true;
			case SWAP:
			case QUICK_MOVE:
			case PICKUP_ALL:
				//TOP
				for(ItemStack cItem : gui.getInventory().getContents()) {
					Ref.sendPacket(player,packetSetSlot(id, position++, asNMSItem(cItem)));
				}
				//BUTTON
				player.updateInventory();
				return true;
			default:
				Ref.sendPacket(player,packetSetSlot(id, slot, getSlotItem(container,slot)));
				if(gui instanceof AnvilGUI) {
					//TOP
					for(ItemStack cItem : gui.getInventory().getContents()) {
						if(position!=slot)
						Ref.sendPacket(player,packetSetSlot(id, position++, asNMSItem(cItem)));
					}
					//BUTTON
					player.updateInventory();
				}
				return true;
			}
		}else {
			if(gui instanceof AnvilGUI && slot==2)
				postToMainThread(() -> ((ContainerAnvil)container).b((EntityPlayer)getPlayer(player),slot));
		}
		return false;
	}

	static Field field = Ref.field(PacketStatusOutServerInfo.class, "b");
	
	@Override
	public boolean processServerListPing(String player, Object channel, Object packet) {
		PacketStatusOutServerInfo status = (PacketStatusOutServerInfo)packet;
		ServerPing ping;
		try {
			ping = (ServerPing) field.get(status);
		} catch (Exception e) {
			return false;
		}
		List<PlayerProfile> players = new ArrayList<>();
		for (Player p : TheAPI.getOnlinePlayers())
			players.add(new PlayerProfile(p.getName(), p.getUniqueId()));
		ServerListPingEvent event = new ServerListPingEvent(TheAPI.getOnlinePlayers().size(),
				TheAPI.getMaxPlayers(), players, TheAPI.getMotd(), ping.d(),
				((InetSocketAddress) ((Channel)channel).remoteAddress()).getAddress(), ping.c().a(), ping.c().b());
		TheAPI.callEvent(event);
		if (event.isCancelled())
			return true;
		ServerPingPlayerSample playerSample = new ServerPingPlayerSample(event.getMaxPlayers(), event.getOnlinePlayers());
		if (event.getPlayersText() != null) {
			GameProfile[] profiles = new GameProfile[event.getPlayersText().size()];
			int i = -1;
			for (PlayerProfile s : event.getPlayersText())
				profiles[++i] = new GameProfile(s.getUUID(), s.getName());
			playerSample.a(profiles);
		} else
			playerSample.a(new GameProfile[0]);
		ping.setPlayerSample(playerSample);

		if (event.getMotd() != null)
			ping.setMOTD((IChatBaseComponent)toIChatBaseComponent(ComponentAPI.toComponent(event.getMotd(), true)));
		else
			ping.setMOTD((IChatBaseComponent)LoaderClass.nmsProvider.chatBase("{\"text\":\"\"}"));
		if(event.getVersion()!=null)
			ping.setServerInfo(new ServerPingServerData(event.getVersion(), event.getProtocol()));
		if (event.getFalvicon() != null)
			ping.setFavicon(event.getFalvicon());
		return false;
	}
	
	public Object getNBT(Entity entity) {
		NBTTagCompound nbt = new NBTTagCompound();
		((CraftEntity)entity).getHandle().e(nbt);
		return nbt;
	}

	@Override
	public Object setString(Object nbt, String path, String value) {
		((NBTTagCompound)nbt).setString(path, value);
		return nbt;
	}

	@Override
	public Object setInteger(Object nbt, String path, int value) {
		((NBTTagCompound)nbt).setInt(path, value);
		return nbt;
	}

	@Override
	public Object setDouble(Object nbt, String path, double value) {
		((NBTTagCompound)nbt).setDouble(path, value);
		return nbt;
	}

	@Override
	public Object setLong(Object nbt, String path, long value) {
		((NBTTagCompound)nbt).setLong(path, value);
		return nbt;
	}

	@Override
	public Object setShort(Object nbt, String path, short value) {
		((NBTTagCompound)nbt).setShort(path, value);
		return nbt;
	}

	@Override
	public Object setFloat(Object nbt, String path, float value) {
		((NBTTagCompound)nbt).setFloat(path, value);
		return nbt;
	}

	@Override
	public Object setBoolean(Object nbt, String path, boolean value) {
		((NBTTagCompound)nbt).setBoolean(path, value);
		return nbt;
	}

	@Override
	public Object setIntArray(Object nbt, String path, int[] value) {
		((NBTTagCompound)nbt).setIntArray(path, value);
		return nbt;
	}

	@Override
	public Object setByteArray(Object nbt, String path, byte[] value) {
		((NBTTagCompound)nbt).setByteArray(path, value);
		return nbt;
	}

	@Override
	public Object setNBTBase(Object nbt, String path, Object value) {
		((NBTTagCompound)nbt).set(path, (NBTBase)value);
		return nbt;
	}

	@Override
	public String getString(Object nbt, String path) {
		return ((NBTTagCompound)nbt).getString(path);
	}

	@Override
	public int getInteger(Object nbt, String path) {
		return ((NBTTagCompound)nbt).getInt(path);
	}

	@Override
	public double getDouble(Object nbt, String path) {
		return ((NBTTagCompound)nbt).getDouble(path);
	}

	@Override
	public long getLong(Object nbt, String path) {
		return ((NBTTagCompound)nbt).getLong(path);
	}

	@Override
	public short getShort(Object nbt, String path) {
		return ((NBTTagCompound)nbt).getShort(path);
	}

	@Override
	public float getFloat(Object nbt, String path) {
		return ((NBTTagCompound)nbt).getFloat(path);
	}

	@Override
	public boolean getBoolean(Object nbt, String path) {
		return ((NBTTagCompound)nbt).getBoolean(path);
	}

	@Override
	public int[] getIntArray(Object nbt, String path) {
		return ((NBTTagCompound)nbt).getIntArray(path);
	}

	@Override
	public byte[] getByteArray(Object nbt, String path) {
		return ((NBTTagCompound)nbt).getByteArray(path);
	}

	@Override
	public Object getNBTBase(Object nbt, String path) {
		return ((NBTTagCompound)nbt).get(path);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<String> getKeys(Object nbt) {
		return ((NBTTagCompound)nbt).c();
	}

	@Override
	public boolean hasKey(Object nbt, String path) {
		return ((NBTTagCompound)nbt).hasKey(path);
	}

	@Override
	public void removeKey(Object nbt, String path) {
		((NBTTagCompound)nbt).remove(path);
	}

	@Override
	public Object setByte(Object nbt, String path, byte value) {
		((NBTTagCompound)nbt).setByte(path, value);
		return nbt;
	}

	@Override
	public byte getByte(Object nbt, String path) {
		return ((NBTTagCompound)nbt).getByte(path);
	}

	@Override
	public Object getDataWatcher(Entity entity) {
		return ((CraftEntity)entity).getHandle().getDataWatcher();
	}

	@Override
	public Object getDataWatcher(Object entity) {
		return ((net.minecraft.server.v1_7_R4.Entity)entity).getDataWatcher();
	}

	@Override
	public int incrementStateId(Object container) {
		return 0;
	}

	@Override
	public Object packetEntityHeadRotation(Entity entity) {
		return new PacketPlayOutEntityHeadRotation((net.minecraft.server.v1_7_R4.Entity) getEntity(entity), (byte)(entity.getLocation().getYaw()*256F/360F));
	}

	@Override
	public Object packetHeldItemSlot(int slot) {
		return new PacketPlayOutHeldItemSlot(slot);
	}

	@Override
	public Object packetExp(float exp, int total, int toNextLevel) {
		return new PacketPlayOutExperience(exp, total, toNextLevel);
	}

	@Override
	public Object packetPlayerInfo(PlayerInfoType type, Player player) {
		EntityPlayer entityPlayer = (EntityPlayer)getPlayer(player);
		switch(type) {
		case ADD_PLAYER:
			return PacketPlayOutPlayerInfo.addPlayer(entityPlayer);
		case REMOVE_PLAYER:
			return PacketPlayOutPlayerInfo.removePlayer(entityPlayer);
		case UPDATE_DISPLAY_NAME:
			return PacketPlayOutPlayerInfo.updateDisplayName(entityPlayer);
		case UPDATE_GAME_MODE:
			return PacketPlayOutPlayerInfo.updateGamemode(entityPlayer);
		case UPDATE_LATENCY:
			return PacketPlayOutPlayerInfo.updatePing(entityPlayer);
		
		}
		return null;
	}

	@Override
	public Object packetPosition(double x, double y, double z, float yaw, float pitch) {
		return new PacketPlayOutPosition(x, y, z, yaw, pitch, false);
	}

	@Override
	public Object packetRespawn(Player player) {
		EntityPlayer entityPlayer = (EntityPlayer)getPlayer(player);
		WorldServer worldserver = entityPlayer.r();
		byte actualDimension = (byte)worldserver.getWorld().getEnvironment().getId();
		return new PacketPlayOutRespawn((byte)((actualDimension >= 0) ? -1 : 0), worldserver.difficulty, worldserver.getWorldData().getType(), entityPlayer.playerInteractManager.getGameMode());
	}

	@Override
	public String getProviderName() {
		return "1_7_R4 (1.7.10)";
	}

	@Override
	public int getContainerStateId(Object container) {
		return 0;
	}
	
}