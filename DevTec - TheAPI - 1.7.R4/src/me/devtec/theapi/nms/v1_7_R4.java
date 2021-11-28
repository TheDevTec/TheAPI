package me.devtec.theapi.nms;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
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
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.TheMaterial;
import me.devtec.theapi.utils.components.Component;
import me.devtec.theapi.utils.components.ComponentAPI;
import me.devtec.theapi.utils.nms.NmsProvider;
import me.devtec.theapi.utils.nms.datawatcher.DataWatcher;
import me.devtec.theapi.utils.reflections.Ref;
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
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NetworkManager;
import net.minecraft.server.v1_7_R4.PacketPlayOutBlockChange;
import net.minecraft.server.v1_7_R4.PacketPlayOutChat;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_7_R4.PacketPlayOutSetSlot;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import net.minecraft.server.v1_7_R4.ScoreboardObjective;
import net.minecraft.server.v1_7_R4.TileEntity;
import net.minecraft.server.v1_7_R4.WorldServer;

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
		return CraftItemStack.asBukkitCopy((net.minecraft.server.v1_7_R4.ItemStack) stack);
	}

	@Override
	public Object packetSetSlot(Object container, int slot, Object itemStack) {
		return new PacketPlayOutSetSlot(((CraftContainer)container).windowId, slot, (net.minecraft.server.v1_7_R4.ItemStack)itemStack);
	}

	@Override
	public Object packetEntityMetadata(int entityId, DataWatcher dataWatcher, boolean bal) {
		return new PacketPlayOutEntityMetadata(entityId, (net.minecraft.server.v1_7_R4.DataWatcher) dataWatcher.getDataWatcher(), bal);
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
	
}