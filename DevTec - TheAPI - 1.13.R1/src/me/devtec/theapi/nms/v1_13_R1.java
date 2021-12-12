package me.devtec.theapi.nms;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_13_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_13_R1.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_13_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.mojang.authlib.GameProfile;

import io.netty.channel.Channel;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.guiapi.GUI.ClickType;
import me.devtec.theapi.guiapi.HolderGUI;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.TheMaterial;
import me.devtec.theapi.utils.components.Component;
import me.devtec.theapi.utils.components.ComponentAPI;
import me.devtec.theapi.utils.listener.events.ServerListPingEvent;
import me.devtec.theapi.utils.nms.NmsProvider;
import me.devtec.theapi.utils.nms.nbt.NBTEdit;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.serverlist.PlayerProfile;
import me.devtec.theapi.utils.theapiutils.LoaderClass;
import me.devtec.theapi.utils.theapiutils.LoaderClass.InventoryClickType;
import net.minecraft.server.v1_13_R1.Block;
import net.minecraft.server.v1_13_R1.BlockPosition;
import net.minecraft.server.v1_13_R1.Blocks;
import net.minecraft.server.v1_13_R1.ChatClickable;
import net.minecraft.server.v1_13_R1.ChatClickable.EnumClickAction;
import net.minecraft.server.v1_13_R1.ChatComponentText;
import net.minecraft.server.v1_13_R1.ChatHoverable;
import net.minecraft.server.v1_13_R1.ChatHoverable.EnumHoverAction;
import net.minecraft.server.v1_13_R1.ChatMessageType;
import net.minecraft.server.v1_13_R1.ChatModifier;
import net.minecraft.server.v1_13_R1.ChunkSection;
import net.minecraft.server.v1_13_R1.Container;
import net.minecraft.server.v1_13_R1.ContainerAnvil;
import net.minecraft.server.v1_13_R1.DataBits;
import net.minecraft.server.v1_13_R1.DataPalette;
import net.minecraft.server.v1_13_R1.DataPaletteBlock;
import net.minecraft.server.v1_13_R1.EntityHuman;
import net.minecraft.server.v1_13_R1.EntityLiving;
import net.minecraft.server.v1_13_R1.EntityPlayer;
import net.minecraft.server.v1_13_R1.EnumChatFormat;
import net.minecraft.server.v1_13_R1.IBlockData;
import net.minecraft.server.v1_13_R1.IChatBaseComponent;
import net.minecraft.server.v1_13_R1.ICrafting;
import net.minecraft.server.v1_13_R1.IScoreboardCriteria.EnumScoreboardHealthDisplay;
import net.minecraft.server.v1_13_R1.ITileEntity;
import net.minecraft.server.v1_13_R1.Item;
import net.minecraft.server.v1_13_R1.MinecraftServer;
import net.minecraft.server.v1_13_R1.MojangsonParser;
import net.minecraft.server.v1_13_R1.NBTBase;
import net.minecraft.server.v1_13_R1.NBTTagCompound;
import net.minecraft.server.v1_13_R1.NetworkManager;
import net.minecraft.server.v1_13_R1.NonNullList;
import net.minecraft.server.v1_13_R1.PacketPlayInWindowClick;
import net.minecraft.server.v1_13_R1.PacketPlayOutBlockChange;
import net.minecraft.server.v1_13_R1.PacketPlayOutChat;
import net.minecraft.server.v1_13_R1.PacketPlayOutCloseWindow;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_13_R1.PacketPlayOutExperience;
import net.minecraft.server.v1_13_R1.PacketPlayOutHeldItemSlot;
import net.minecraft.server.v1_13_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_13_R1.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_13_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_13_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_13_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_13_R1.PacketPlayOutPosition;
import net.minecraft.server.v1_13_R1.PacketPlayOutResourcePackSend;
import net.minecraft.server.v1_13_R1.PacketPlayOutRespawn;
import net.minecraft.server.v1_13_R1.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_13_R1.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_13_R1.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_13_R1.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_13_R1.PacketPlayOutSetSlot;
import net.minecraft.server.v1_13_R1.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_13_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_13_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_13_R1.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_13_R1.PacketStatusOutServerInfo;
import net.minecraft.server.v1_13_R1.PlayerConnection;
import net.minecraft.server.v1_13_R1.ScoreboardObjective;
import net.minecraft.server.v1_13_R1.ScoreboardServer;
import net.minecraft.server.v1_13_R1.ServerPing;
import net.minecraft.server.v1_13_R1.ServerPing.ServerData;
import net.minecraft.server.v1_13_R1.ServerPing.ServerPingPlayerSample;
import net.minecraft.server.v1_13_R1.TileEntity;
import net.minecraft.server.v1_13_R1.WorldServer;

public class v1_13_R1 implements NmsProvider {
	private static final MinecraftServer server = MinecraftServer.getServer();
	private static final ChatComponentText empty = new ChatComponentText("");
	private static Field dataPallete = Ref.field(DataPaletteBlock.class, "h"), dataBits = Ref.field(DataPaletteBlock.class, "a");

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
	public int getEntityId(Object entity) {
		return ((net.minecraft.server.v1_13_R1.Entity)entity).getId();
	}

	@Override
	public Object getScoreboardAction(Action type) {
		return ScoreboardServer.Action.valueOf(type.name());
	}

	@Override
	public Object getEnumScoreboardHealthDisplay(DisplayType type) {
		return EnumScoreboardHealthDisplay.valueOf(type.name());
	}

	@Override
	public Object getNBT(ItemStack itemStack) {
		return ((net.minecraft.server.v1_13_R1.ItemStack)asNMSItem(itemStack)).getOrCreateTag();
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
		net.minecraft.server.v1_13_R1.ItemStack i = (net.minecraft.server.v1_13_R1.ItemStack)asNMSItem(stack);
		i.setTag((NBTTagCompound) nbt);
		return asBukkitItem(i);
	}

	@Override
	public Object asNMSItem(ItemStack stack) {
		if(stack==null)return net.minecraft.server.v1_13_R1.ItemStack.a;
		return CraftItemStack.asNMSCopy(stack);
	}

	@Override
	public ItemStack asBukkitItem(Object stack) {
		return CraftItemStack.asBukkitCopy((net.minecraft.server.v1_13_R1.ItemStack) stack);
	}

	@Override
	public Object packetOpenWindow(int id, String legacy, int size, String title) {
		return new PacketPlayOutOpenWindow(id, legacy, (IChatBaseComponent)ComponentAPI.toIChatBaseComponent(title, true), size);
	}
	
	public int getContainerId(Object container) {
		return ((Container)container).windowId;
	}
	
	@Override
	public Object packetResourcePackSend(String url, String hash, boolean requireRP, String prompt) {
		return new PacketPlayOutResourcePackSend(url, hash);
	}

	@Override
	public Object packetSetSlot(int container, int slot, int stateId, Object itemStack) {
		return new PacketPlayOutSetSlot(container, slot, (net.minecraft.server.v1_13_R1.ItemStack)(itemStack==null?asNMSItem(null):itemStack));
	}

	public Object packetSetSlot(int container, int slot, Object itemStack) {
		return packetSetSlot(container,slot,0,itemStack);
	}

	@Override
	public Object packetEntityMetadata(int entityId, Object dataWatcher, boolean bal) {
		return new PacketPlayOutEntityMetadata(entityId, (net.minecraft.server.v1_13_R1.DataWatcher) dataWatcher, bal);
	}

	@Override
	public Object packetEntityDestroy(int... ids) {
		return new PacketPlayOutEntityDestroy(ids);
	}

	@Override
	public Object packetSpawnEntity(Object entity, int id) {
		return new PacketPlayOutSpawnEntity((net.minecraft.server.v1_13_R1.Entity) entity, id);
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
		PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
		packet.a=(IChatBaseComponent)toIChatBaseComponent(ComponentAPI.toComponent(header, true));
		packet.b=(IChatBaseComponent)toIChatBaseComponent(ComponentAPI.toComponent(footer, true));
		return packet;
	}

	@Override
	public Object packetBlockChange(World world, Position position) {
		return new PacketPlayOutBlockChange((net.minecraft.server.v1_13_R1.World)getWorld(world), (BlockPosition)position.getBlockPosition());
	}

	@Override
	public Object packetBlockChange(World world, int x, int y, int z) {
		return new PacketPlayOutBlockChange((net.minecraft.server.v1_13_R1.World)getWorld(world), new BlockPosition(x,y,z));
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
		return new PacketPlayOutScoreboardScore((ScoreboardServer.Action) getScoreboardAction(action), player, line, score);
	}

	@Override
	public Object packetTitle(TitleAction action, String text, int fadeIn, int stay, int fadeOut) {
		return new PacketPlayOutTitle(EnumTitleAction.valueOf(action.name()), (IChatBaseComponent)ComponentAPI.toIChatBaseComponent(ComponentAPI.toComponent(text, true)), fadeIn, stay, fadeOut);
	}

	@Override
	public Object packetChat(ChatType type, Object chatBase, UUID uuid) {
		return new PacketPlayOutChat((IChatBaseComponent)chatBase, ChatMessageType.valueOf(type.name()));
	}

	@Override
	public Object packetChat(ChatType type, String text, UUID uuid) {
		return packetChat(type, ComponentAPI.toIChatBaseComponent(ComponentAPI.toComponent(text, false)), uuid);
	}

	@Override
	public void postToMainThread(Runnable runnable) {
		server.postToMainThread(runnable);
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
				modif=modif.setColor(EnumChatFormat.a(c.getColor().charAt(0)));
			}
			if(c.getClickEvent()!=null)
				modif=modif.setChatClickable(new ChatClickable(EnumClickAction.valueOf(c.getClickEvent().getAction().name()), c.getClickEvent().getValue()));
			if(c.getHoverEvent()!=null)
				modif=modif.setChatHoverable(new ChatHoverable(EnumHoverAction.valueOf(c.getHoverEvent().getAction().name()), (IChatBaseComponent)toIChatBaseComponent(c.getHoverEvent().getValue())));
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
				modif=modif.setColor(EnumChatFormat.a(c.getColor().charAt(0)));
			}
			if(c.getClickEvent()!=null)
				modif=modif.setChatClickable(new ChatClickable(EnumClickAction.valueOf(c.getClickEvent().getAction().name()), c.getClickEvent().getValue()));
			if(c.getHoverEvent()!=null)
				modif=modif.setChatHoverable(new ChatHoverable(EnumHoverAction.valueOf(c.getHoverEvent().getAction().name()), (IChatBaseComponent)toIChatBaseComponent(c.getHoverEvent().getValue())));
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
				modif=modif.setColor(EnumChatFormat.a(c.getColor().charAt(0)));
			}
			if(c.getClickEvent()!=null)
				modif=modif.setChatClickable(new ChatClickable(EnumClickAction.valueOf(c.getClickEvent().getAction().name()), c.getClickEvent().getValue()));
			if(c.getHoverEvent()!=null)
				modif=modif.setChatHoverable(new ChatHoverable(EnumHoverAction.valueOf(c.getHoverEvent().getAction().name()), (IChatBaseComponent)toIChatBaseComponent(c.getHoverEvent().getValue())));
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
				modif=modif.setColor(EnumChatFormat.a(c.getColor().charAt(0)));
			}
			if(c.getClickEvent()!=null)
				modif=modif.setChatClickable(new ChatClickable(EnumClickAction.valueOf(c.getClickEvent().getAction().name()), c.getClickEvent().getValue()));
			if(c.getHoverEvent()!=null)
				modif=modif.setChatHoverable(new ChatHoverable(EnumHoverAction.valueOf(c.getHoverEvent().getAction().name()), (IChatBaseComponent)toIChatBaseComponent(c.getHoverEvent().getValue())));
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
		return IChatBaseComponent.ChatSerializer.a(json);
	}

	@Override
	public String fromIChatBaseComponent(Object component) {
		return CraftChatMessage.fromComponent((IChatBaseComponent)component);
	}

	@Override
	public TheMaterial toMaterial(Object blockOrItemOrIBlockData) {
		if(blockOrItemOrIBlockData instanceof Block) {
			Block b = (Block)blockOrItemOrIBlockData;
			return new TheMaterial((ItemStack)CraftItemStack.asNewCraftStack(b.getItem()));
		}
		if(blockOrItemOrIBlockData instanceof Item) {
			Item b = (Item)blockOrItemOrIBlockData;
			return new TheMaterial((ItemStack)CraftItemStack.asNewCraftStack(b));
		}
		if(blockOrItemOrIBlockData instanceof IBlockData) {
			IBlockData b = (IBlockData)blockOrItemOrIBlockData;
			return new TheMaterial((ItemStack)CraftItemStack.asNewCraftStack(b.getBlock().getItem()));
		}
		return null;
	}

	@Override
	public Object toIBlockData(TheMaterial material) {
		return CraftMagicNumbers.getBlock(material.toItemStack().getData());
	}

	@Override
	public Object toItem(TheMaterial material) {
		return CraftItemStack.asNMSCopy(material.toItemStack()).getItem();
	}

	@Override
	public Object toBlock(TheMaterial material) {
		return CraftMagicNumbers.getBlock(new MaterialData(material.getType(),(byte)material.getData()));
	}

	@Override
	public Object getChunk(World world, int x, int z) {
		return ((CraftChunk)world.getChunkAt(x, z)).getHandle();
	}
	
	@Override
	public void setBlock(Object chunk, int x, int y, int z, Object IblockData, int data) {
		net.minecraft.server.v1_13_R1.Chunk c = (net.minecraft.server.v1_13_R1.Chunk)chunk;
		ChunkSection sc = c.getSections()[y>>4];
		if(sc==null) {
			c.getSections()[y>>4]=sc=new ChunkSection(y >> 4 << 4, true);
		}
		BlockPosition pos = new BlockPosition(x,y,z);
		//REMOVE TILE ENTITY
		c.tileEntities.remove(pos);
		try {
			DataBits bits = (DataBits)dataBits.get(sc.getBlocks());
			@SuppressWarnings("unchecked")
			DataPalette<IBlockData> pallete = (DataPalette<IBlockData>)dataPallete.get(sc.getBlocks());
			bits.a(b(x&15, y&15, z&15), pallete.a((IBlockData)IblockData));
		}catch(Exception er) {}
		
		//ADD TILE ENTITY
		if(IblockData instanceof ITileEntity) {
			TileEntity ent = ((ITileEntity)IblockData).a(c);
			c.tileEntities.put(pos,ent);
			Ref.sendPacket(TheAPI.getOnlinePlayers(), ent.getUpdatePacket());
		}
	}
	
	private static int b(int var0, int var1, int var2) {
		return var1 << 8 | var2 << 4 | var0;
	}

	@Override
	public void updateLightAt(Object chunk, int x, int y, int z) {
		net.minecraft.server.v1_13_R1.Chunk c = (net.minecraft.server.v1_13_R1.Chunk)chunk;
		c.initLighting();
	}

	@Override
	public Object getBlock(Object chunk, int x, int y, int z) {
		net.minecraft.server.v1_13_R1.Chunk c = (net.minecraft.server.v1_13_R1.Chunk)chunk;
		ChunkSection sc = c.getSections()[y>>4];
		if(sc==null)return Blocks.AIR.getBlockData();
		return sc.getBlocks().a(x&15, y&15, z&15);
	}

	@Override
	public int getData(Object chunk, int x, int y, int z) {
		return 0;
	}

	@Override
	public int getCombinedId(Object IblockDataOrBlock) {
		return Block.getCombinedId((IBlockData)IblockDataOrBlock);
	}

	@Override
	public Object blockPosition(int blockX, int blockY, int blockZ) {
		return new BlockPosition(blockX, blockY, blockZ);
	}

	@Override
	public Object toIBlockData(Object data) {
		return ((CraftBlockData)data).getState();
	}

	@Override
	public Object toIBlockData(BlockState state) {
		return CraftMagicNumbers.getBlock(state.getType(),state.getRawData());
	}

	@Override
	public Object toBlock(Material type) {
		return CraftMagicNumbers.getBlock(type);
	}

	@Override
	public Object toItem(Material type, int data) {
		return CraftMagicNumbers.getItem(type, (short)data);
	}

	@Override
	public Object toIBlockData(Material type, int data) {
		return CraftMagicNumbers.getBlock(type, (byte)data);
	}

	@Override
	public Chunk toBukkitChunk(Object nmsChunk) {
		return ((net.minecraft.server.v1_13_R1.Chunk)nmsChunk).bukkitChunk;
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
		return ((NetworkManager)network).channel;
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
		((Container)container).setItem(slot, (net.minecraft.server.v1_13_R1.ItemStack)item);
	}

	@Override
	public void setGUITitle(Player player, Object container, String legacy, int size, String title) {
		EntityPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
		int id = ((Container)container).windowId;
		NonNullList<net.minecraft.server.v1_13_R1.ItemStack> nmsItems = ((Container)container).items;
		Ref.sendPacket(player, packetOpenWindow(id,legacy,size,title));
		int i = 0;
		for(net.minecraft.server.v1_13_R1.ItemStack o : nmsItems) 
			Ref.sendPacket(player, packetSetSlot(id,i++, o));
		nmsPlayer.activeContainer=(Container)container;
		((Container)container).addSlotListener(nmsPlayer);
		((Container)container).checkReachable=false;
	}

	@Override
	public void openGUI(Player player, Object container, String legacy, int size, String title, ItemStack[] items) {
		EntityPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
		int id = ((Container)container).windowId;
		net.minecraft.server.v1_13_R1.ItemStack[] nmsItems = new net.minecraft.server.v1_13_R1.ItemStack[items.length];
		for(int i = 0; i < items.length; ++i) {
			ItemStack is = items[i];
			if(is==null||is.getType()==Material.AIR)continue;
			net.minecraft.server.v1_13_R1.ItemStack item = null;
			((Container)container).setItem(i,item=(net.minecraft.server.v1_13_R1.ItemStack) asNMSItem(is));
			nmsItems[i]=item;
		}
		Ref.sendPacket(player, packetOpenWindow(id,legacy,size,title));
		int i = 0;
		for(net.minecraft.server.v1_13_R1.ItemStack o : nmsItems) 
			Ref.sendPacket(player, packetSetSlot(id,i++, o));
		nmsPlayer.activeContainer=(Container)container;
		((Container)container).addSlotListener((ICrafting) nmsPlayer);
		((Container)container).checkReachable=false;
	}

	@Override
	public void openAnvilGUI(Player player, Object con, String title, ItemStack[] items) {
		ContainerAnvil container = (ContainerAnvil)con;
		EntityPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
		int id = container.windowId;
		net.minecraft.server.v1_13_R1.ItemStack[] nmsItems = new net.minecraft.server.v1_13_R1.ItemStack[items.length];
		for(int i = 0; i < items.length; ++i) {
			ItemStack is = items[i];
			if(is==null||is.getType()==Material.AIR)continue;
			net.minecraft.server.v1_13_R1.ItemStack item = null;
			container.setItem(i,item=(net.minecraft.server.v1_13_R1.ItemStack) asNMSItem(is));
			nmsItems[i]=item;
		}
		Ref.sendPacket(player, packetOpenWindow(id,"minecraft:anvil",0,title));
		int i = 0;
		for(net.minecraft.server.v1_13_R1.ItemStack o : nmsItems) 
			Ref.sendPacket(player, packetSetSlot(id,i++, o));
		nmsPlayer.activeContainer=container;
		container.addSlotListener((ICrafting) nmsPlayer);
		container.checkReachable=false;
	}

	@Override
	public Object createContainer(Inventory inv, Player player) {
		return new CraftContainer(inv, ((CraftPlayer)player).getHandle(), ((CraftPlayer)player).getHandle().nextContainerCounter());
	}

	@Override
	public Object getSlotItem(Object container, int slot) {
		return ((Container)container).getSlot(slot).getItem();
	}

	static BlockPosition zero = new BlockPosition(0,0,0);
	
	@Override
	public Object createAnvilContainer(Player player) {
		int id = ((CraftPlayer)player).getHandle().nextContainerCounter();
		ContainerAnvil anvil = new ContainerAnvil(((CraftPlayer)player).getHandle().inventory,((CraftPlayer)player).getHandle().world, zero,((CraftPlayer)player).getHandle());
		anvil.windowId=id;
		return anvil;
	}

	@Override
	public String getAnvilRenameText(Object anvil) {
		return ((ContainerAnvil)anvil).renameText;
	}
	
	@Override
	public boolean processInvClickPacket(Player player, HolderGUI gui, Object provPacket) {
		PacketPlayInWindowClick packet = (PacketPlayInWindowClick)provPacket;
		int slot = packet.c();
		if(slot==-999)return false;
		
		int id = packet.b();
		int mouseClick = packet.d();
	    net.minecraft.server.v1_13_R1.InventoryClickType nmsType = packet.g();
		InventoryClickType type = InventoryClickType.valueOf(nmsType.name());
		
		Object container = gui.getContainer(player);
		ItemStack item = asBukkitItem(packet.e());
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
		if(!cancel)cancel=gui.onIteractItem(player, item, clickType, slot>gui.size()?slot-gui.size()+27:slot, slot<gui.size());
		if(type==InventoryClickType.QUICK_MOVE && TheAPI.isOlderThan(9))
			cancel=true;
		int position = 0;
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
				for(ItemStack cItem : gui.getInventory().getContents())
					Ref.sendPacket(player,packetSetSlot(id, position++, asNMSItem(cItem)));
				//BUTTON
				player.updateInventory();
				return true;
			default:
				Ref.sendPacket(player,packetSetSlot(id, slot, getSlotItem(container,slot)));
				return true;
			}
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
				((InetSocketAddress) ((Channel)channel).remoteAddress()).getAddress(), ping.getServerData().a(), ping.getServerData().getProtocolVersion());
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
			ping.setServerInfo(new ServerData(event.getVersion(), event.getProtocol()));
		if (event.getFalvicon() != null)
			ping.setFavicon(event.getFalvicon());
		return false;
	}
	
	public Object getNBT(Entity entity) {
		return ((CraftEntity)entity).getHandle().save(new NBTTagCompound());
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

	@Override
	public Set<String> getKeys(Object nbt) {
		return ((NBTTagCompound)nbt).getKeys();
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
		return ((net.minecraft.server.v1_13_R1.Entity)entity).getDataWatcher();
	}

	@Override
	public int incrementStateId(Object container) {
		return 0;
	}

	@Override
	public Object packetEntityHeadRotation(Entity entity) {
		return new PacketPlayOutEntityHeadRotation((net.minecraft.server.v1_13_R1.Entity) getEntity(entity), (byte)(entity.getLocation().getYaw()*256F/360F));
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
		return new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.valueOf(type.name()), (EntityPlayer)getPlayer(player));
	}

	@Override
	public Object packetPosition(double x, double y, double z, float yaw, float pitch) {
		return new PacketPlayOutPosition(x, y, z, yaw, pitch, Collections.emptySet(), 0);
	}

	@Override
	public Object packetRespawn(Player player) {
		EntityPlayer entityPlayer = (EntityPlayer)getPlayer(player);
		WorldServer worldserver = entityPlayer.getWorldServer();
		byte actualDimension = (byte)worldserver.getWorld().getEnvironment().getId();
		return new PacketPlayOutRespawn((byte)((actualDimension >= 0) ? -1 : 0), worldserver.getDifficulty(), worldserver.getWorldData().getType(), entityPlayer.playerInteractManager.getGameMode());
	}
	
}
