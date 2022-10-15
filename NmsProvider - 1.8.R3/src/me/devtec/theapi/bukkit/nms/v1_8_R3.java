package me.devtec.theapi.bukkit.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.chunkio.ChunkIOExecutor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import io.netty.channel.Channel;
import me.devtec.shared.Ref;
import me.devtec.shared.components.Component;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.events.EventManager;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.BukkitLoader.InventoryClickType;
import me.devtec.theapi.bukkit.events.ServerListPingEvent;
import me.devtec.theapi.bukkit.game.BlockDataStorage;
import me.devtec.theapi.bukkit.game.Position;
import me.devtec.theapi.bukkit.gui.AnvilGUI;
import me.devtec.theapi.bukkit.gui.GUI.ClickType;
import me.devtec.theapi.bukkit.gui.HolderGUI;
import me.devtec.theapi.bukkit.nms.GameProfileHandler.PropertyHandler;
import me.devtec.theapi.bukkit.nms.utils.InventoryUtils;
import me.devtec.theapi.bukkit.nms.utils.InventoryUtils.DestinationType;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockFalling;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.BlockStateList;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.ChatClickable;
import net.minecraft.server.v1_8_R3.ChatClickable.EnumClickAction;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.ChatHoverable;
import net.minecraft.server.v1_8_R3.ChatHoverable.EnumHoverAction;
import net.minecraft.server.v1_8_R3.ChatModifier;
import net.minecraft.server.v1_8_R3.Chunk.EnumTileEntityState;
import net.minecraft.server.v1_8_R3.ChunkCoordIntPair;
import net.minecraft.server.v1_8_R3.ChunkProviderServer;
import net.minecraft.server.v1_8_R3.ChunkRegionLoader;
import net.minecraft.server.v1_8_R3.ChunkSection;
import net.minecraft.server.v1_8_R3.Container;
import net.minecraft.server.v1_8_R3.ContainerAnvil;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumChatFormat;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.IBlockState;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChunkLoader;
import net.minecraft.server.v1_8_R3.IContainer;
import net.minecraft.server.v1_8_R3.IScoreboardCriteria.EnumScoreboardHealthDisplay;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.MojangsonParser;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.PacketPlayInWindowClick;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutCloseWindow;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutExperience;
import net.minecraft.server.v1_8_R3.PacketPlayOutHeldItemSlot;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutResourcePackSend;
import net.minecraft.server.v1_8_R3.PacketPlayOutRespawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore.EnumScoreboardAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.PacketPlayOutSetSlot;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutWindowItems;
import net.minecraft.server.v1_8_R3.PacketStatusOutServerInfo;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.ScoreboardObjective;
import net.minecraft.server.v1_8_R3.ServerPing;
import net.minecraft.server.v1_8_R3.ServerPing.ServerData;
import net.minecraft.server.v1_8_R3.ServerPing.ServerPingPlayerSample;
import net.minecraft.server.v1_8_R3.TileEntity;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;

public class v1_8_R3 implements NmsProvider {
	private MinecraftServer server = MinecraftServer.getServer();
	private static final ChatComponentText empty = new ChatComponentText("");
	private static Field a = Ref.field(PacketPlayOutPlayerListHeaderFooter.class, "a"), b = Ref.field(PacketPlayOutPlayerListHeaderFooter.class, "b");
	private static Field pos = Ref.field(PacketPlayOutBlockChange.class, "a");
	private static Field score_a = Ref.field(PacketPlayOutScoreboardScore.class, "a"), score_b = Ref.field(PacketPlayOutScoreboardScore.class, "b"),
			score_c = Ref.field(PacketPlayOutScoreboardScore.class, "c"), score_d = Ref.field(PacketPlayOutScoreboardScore.class, "d");

	@Override
	public Collection<? extends Player> getOnlinePlayers() {
		return Bukkit.getOnlinePlayers();
	}

	@Override
	public Object getEntity(Entity entity) {
		return ((CraftEntity) entity).getHandle();
	}

	@Override
	public Object getEntityLiving(LivingEntity entity) {
		return ((CraftLivingEntity) entity).getHandle();
	}

	@Override
	public Object getPlayer(Player player) {
		return ((CraftPlayer) player).getHandle();
	}

	@Override
	public Object getWorld(World world) {
		return ((CraftWorld) world).getHandle();
	}

	@Override
	public Object getChunk(Chunk chunk) {
		return ((CraftChunk) chunk).getHandle();
	}

	@Override
	public int getEntityId(Object entity) {
		return ((net.minecraft.server.v1_8_R3.Entity) entity).getId();
	}

	@Override
	public Object getScoreboardAction(Action type) {
		return EnumScoreboardAction.valueOf(type.name());
	}

	@Override
	public Object getEnumScoreboardHealthDisplay(DisplayType type) {
		return EnumScoreboardHealthDisplay.valueOf(type.name());
	}

	@Override
	public Object getNBT(ItemStack itemStack) {
		return ((net.minecraft.server.v1_8_R3.ItemStack) asNMSItem(itemStack)).getTag();
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
		if (nbt instanceof NBTEdit)
			nbt = ((NBTEdit) nbt).getNBT();
		net.minecraft.server.v1_8_R3.ItemStack i = (net.minecraft.server.v1_8_R3.ItemStack) asNMSItem(stack);
		i.setTag((NBTTagCompound) nbt);
		return asBukkitItem(i);
	}

	private static final net.minecraft.server.v1_8_R3.ItemStack air = CraftItemStack.asNMSCopy(new ItemStack(Material.AIR));

	@Override
	public Object asNMSItem(ItemStack stack) {
		if (stack == null)
			return v1_8_R3.air;
		return CraftItemStack.asNMSCopy(stack);
	}

	@Override
	public ItemStack asBukkitItem(Object stack) {
		return CraftItemStack.asBukkitCopy((net.minecraft.server.v1_8_R3.ItemStack) stack);
	}

	@Override
	public Object packetOpenWindow(int id, String legacy, int size, String title) {
		return new PacketPlayOutOpenWindow(id, legacy, (IChatBaseComponent) this.toIChatBaseComponent(ComponentAPI.fromString(title)), size);
	}

	@Override
	public int getContainerId(Object container) {
		return ((Container) container).windowId;
	}

	@Override
	public Object packetResourcePackSend(String url, String hash, boolean requireRP, String prompt) {
		return new PacketPlayOutResourcePackSend(url, hash);
	}

	@Override
	public Object packetSetSlot(int container, int slot, int stateId, Object itemStack) {
		return new PacketPlayOutSetSlot(container, slot, (net.minecraft.server.v1_8_R3.ItemStack) (itemStack == null ? asNMSItem(null) : itemStack));
	}

	public Object packetSetSlot(int container, int slot, Object itemStack) {
		return this.packetSetSlot(container, slot, 0, itemStack);
	}

	@Override
	public Object packetEntityMetadata(int entityId, Object dataWatcher, boolean bal) {
		return new PacketPlayOutEntityMetadata(entityId, (net.minecraft.server.v1_8_R3.DataWatcher) dataWatcher, bal);
	}

	@Override
	public Object packetEntityDestroy(int... ids) {
		return new PacketPlayOutEntityDestroy(ids);
	}

	@Override
	public Object packetSpawnEntity(Object entity, int id) {
		return new PacketPlayOutSpawnEntity((net.minecraft.server.v1_8_R3.Entity) entity, id);
	}

	@Override
	public Object packetNamedEntitySpawn(Object player) {
		return new PacketPlayOutNamedEntitySpawn((EntityHuman) player);
	}

	@Override
	public Object packetSpawnEntityLiving(Object entityLiving) {
		return new PacketPlayOutSpawnEntityLiving((EntityLiving) entityLiving);
	}

	@Override
	public Object packetPlayerListHeaderFooter(String header, String footer) {
		PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
		try {
			v1_8_R3.a.set(packet, this.toIChatBaseComponent(ComponentAPI.fromString(header)));
			v1_8_R3.b.set(packet, this.toIChatBaseComponent(ComponentAPI.fromString(footer)));
		} catch (Exception err) {
		}
		return packet;
	}

	@Override
	public Object packetBlockChange(World world, Position position, Object iblockdata, int data) {
		PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange();
		packet.block = iblockdata == null ? Blocks.AIR.getBlockData() : (IBlockData) iblockdata;
		try {
			v1_8_R3.pos.set(packet, position.getBlockPosition());
		} catch (Exception e) {
		}
		return packet;
	}

	@Override
	public Object packetBlockChange(World world, int x, int y, int z) {
		PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange();
		packet.block = (IBlockData) getBlock(this.getChunk(world, x >> 4, z >> 4), x, y, z);
		try {
			v1_8_R3.pos.set(packet, new BlockPosition(x, y, z));
		} catch (Exception e) {
		}
		return packet;
	}

	@Override
	public Object packetScoreboardObjective() {
		return new PacketPlayOutScoreboardObjective();
	}

	@Override
	public Object packetScoreboardDisplayObjective(int id, Object scoreboardObjective) {
		return new PacketPlayOutScoreboardDisplayObjective(id, scoreboardObjective == null ? null : (ScoreboardObjective) scoreboardObjective);
	}

	@Override
	public Object packetScoreboardTeam() {
		return new PacketPlayOutScoreboardTeam();
	}

	@Override
	public Object packetScoreboardScore(Action action, String player, String line, int score) {
		if (action == Action.REMOVE)
			return new PacketPlayOutScoreboardScore(line);
		PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();
		try {
			v1_8_R3.score_a.set(packet, line);
			v1_8_R3.score_b.set(packet, player);
			v1_8_R3.score_c.set(packet, score);
			v1_8_R3.score_d.set(packet, EnumScoreboardAction.CHANGE);
		} catch (Exception err) {
		}
		return packet;
	}

	@Override
	public Object packetTitle(TitleAction action, String text, int fadeIn, int stay, int fadeOut) {
		if (action == TitleAction.ACTIONBAR)
			return new PacketPlayOutChat((IChatBaseComponent) this.toIChatBaseComponent(ComponentAPI.fromString(text)), (byte) 2);
		return new PacketPlayOutTitle(EnumTitleAction.valueOf(action.name()), (IChatBaseComponent) this.toIChatBaseComponent(ComponentAPI.fromString(text)), fadeIn, stay, fadeOut);
	}

	@Override
	public Object packetChat(ChatType type, Object chatBase, UUID uuid) {
		return new PacketPlayOutChat((IChatBaseComponent) chatBase, type.toByte());
	}

	@Override
	public Object packetChat(ChatType type, String text, UUID uuid) {
		return this.packetChat(type, this.toIChatBaseComponent(ComponentAPI.fromString(text)), uuid);
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

	private IChatBaseComponent convert(Component c) {
		ChatComponentText current = new ChatComponentText(c.getText());
		ChatModifier modif = current.getChatModifier();
		if (c.getColor() != null)
			modif.setColor(EnumChatFormat.b(c.getColor()));
		if (c.getClickEvent() != null)
			modif.setChatClickable(new ChatClickable(EnumClickAction.valueOf(c.getClickEvent().getAction().name()), c.getClickEvent().getValue()));
		if (c.getHoverEvent() != null)
			modif.setChatHoverable(new ChatHoverable(EnumHoverAction.valueOf(c.getHoverEvent().getAction().name()), (IChatBaseComponent) this.toIChatBaseComponent(c.getHoverEvent().getValue())));
		modif.setBold(c.isBold());
		modif.setItalic(c.isItalic());
		modif.setRandom(c.isObfuscated());
		modif.setUnderline(c.isUnderlined());
		modif.setStrikethrough(c.isStrikethrough());
		return current.setChatModifier(modif);
	}

	@Override
	public Object toIChatBaseComponents(List<Component> components) {
		List<IChatBaseComponent> chat = new ArrayList<>();
		chat.add(new ChatComponentText(""));
		for (Component c : components) {
			if (c.getText() == null || c.getText().isEmpty()) {
				if (c.getExtra() != null)
					addConverted(chat, c.getExtra());
				continue;
			}
			chat.add(convert(c));
			if (c.getExtra() != null)
				addConverted(chat, c.getExtra());
		}
		return chat.toArray(new IChatBaseComponent[0]);
	}

	private void addConverted(List<IChatBaseComponent> chat, List<Component> extra) {
		for (Component c : extra) {
			if (c.getText() == null || c.getText().isEmpty()) {
				if (c.getExtra() != null)
					addConverted(chat, c.getExtra());
				continue;
			}
			chat.add(convert(c));
		}
	}

	@Override
	public Object toIChatBaseComponents(Component co) {
		List<IChatBaseComponent> chat = new ArrayList<>();
		chat.add(new ChatComponentText(""));
		if (co.getText() != null && !co.getText().isEmpty())
			chat.add(convert(co));
		if (co.getExtra() != null)
			for (Component c : co.getExtra()) {
				if (c.getText() == null || c.getText().isEmpty()) {
					if (c.getExtra() != null)
						addConverted(chat, c.getExtra());
					continue;
				}
				chat.add(convert(c));
				if (c.getExtra() != null)
					addConverted(chat, c.getExtra());
			}
		return chat.toArray(new IChatBaseComponent[0]);
	}

	@Override
	public Object toIChatBaseComponent(Component co) {
		ChatComponentText main = new ChatComponentText("");
		List<IChatBaseComponent> chat = new ArrayList<>();
		if (co.getText() != null && !co.getText().isEmpty())
			chat.add(convert(co));
		if (co.getExtra() != null)
			for (Component c : co.getExtra()) {
				chat.add(convert(c));
				if (c.getExtra() != null)
					addConverted(chat, c.getExtra());
			}
		main.a().addAll(chat);
		return main.a().isEmpty() ? v1_8_R3.empty : main;
	}

	@Override
	public Object toIChatBaseComponent(List<Component> cc) {
		ChatComponentText main = new ChatComponentText("");
		for (Component c : cc)
			main.a().add((IChatBaseComponent) this.toIChatBaseComponent(c));
		return main.a().isEmpty() ? v1_8_R3.empty : main;
	}

	@Override
	public Object chatBase(String json) {
		return IChatBaseComponent.ChatSerializer.a(json);
	}

	@Override
	public String fromIChatBaseComponent(Object component) {
		return CraftChatMessage.fromComponent((IChatBaseComponent) component);
	}

	@Override
	public BlockDataStorage toMaterial(Object blockOrIBlockData) {
		if (blockOrIBlockData instanceof Block) {
			IBlockData data = ((Block) blockOrIBlockData).getBlockData();
			return new BlockDataStorage(CraftMagicNumbers.getMaterial(data.getBlock()), (byte) 0, asString(data));
		}
		if (blockOrIBlockData instanceof IBlockData) {
			IBlockData data = (IBlockData) blockOrIBlockData;
			return new BlockDataStorage(CraftMagicNumbers.getMaterial(data.getBlock()), (byte) 0, asString(data));
		}
		return new BlockDataStorage(Material.AIR);
	}

	@SuppressWarnings("rawtypes")
	private static Function<Entry<IBlockState, Comparable>, String> STATE_TO_VALUE = new Function<Entry<IBlockState, Comparable>, String>() {
		@Override
		public String apply(Entry<IBlockState, Comparable> var0) {
			if (var0 == null)
				return "<NULL>";
			IBlockState<?> var1 = var0.getKey();
			return var1.a() + "=" + a(var1, var0.getValue());
		}

		@SuppressWarnings("unchecked")
		private String a(IBlockState var0, Comparable var1) {
			return var0.a(var1);
		}
	};

	private String asString(IBlockData data) {
		StringBuilder stateString = new StringBuilder();
		if (!data.b().isEmpty()) {
			stateString.append('[');
			stateString.append(data.b().entrySet().stream().map(STATE_TO_VALUE).collect(Collectors.joining(",")));
			stateString.append(']');
		}
		return stateString.toString();
	}

	@Override
	public Object toIBlockData(BlockDataStorage material) {
		if (material == null || material.getType() == null || material.getType() == Material.AIR)
			return Blocks.AIR.getBlockData();
		return readArgument(material);
	}

	@Override
	public Object toBlock(BlockDataStorage material) {
		if (material == null || material.getType() == null || material.getType() == Material.AIR)
			return Blocks.AIR;
		return readArgument(material).getBlock();
	}

	private IBlockData readArgument(BlockDataStorage material) {
		IBlockData ib = Block.getByCombinedId(material.getType().getId() + (material.getItemData() << 12));
		return writeData(ib, ib.getBlock().P(), material.getData());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static IBlockData writeData(IBlockData ib, BlockStateList blockStateList, String string) {
		if (string == null || string.trim().isEmpty())
			return ib;

		String key = "";
		String value = "";
		int set = 0;

		for (int i = 1; i < string.length() - 1; ++i) {
			char c = string.charAt(i);
			if (c == ',') {
				IBlockState ibj = getStateByKey(blockStateList, key);
				if (ibj != null) {
					Comparable obj = getComparable(ibj, value);
					if (obj != null)
						ib = ib.set(ibj, obj);
				}
				key = "";
				value = "";
				set = 0;
				continue;
			}
			if (c == '=') {
				set = 1;
				continue;
			}
			if (set == 0)
				key += c;
			else
				value += c;
		}
		if (set == 1) {
			IBlockState ibj = getStateByKey(blockStateList, key);
			if (ibj != null) {
				Comparable obj = getComparable(ibj, value);
				if (obj != null)
					ib = ib.set(ibj, obj);
			}
		}
		return ib;
	}

	@SuppressWarnings("rawtypes")
	private static IBlockState getStateByKey(BlockStateList blockStateList, String key) {
		for (IBlockState state : blockStateList.d())
			if (state.a().equals(key))
				return state;
		return null;
	}

	@SuppressWarnings("rawtypes")
	private static Comparable getComparable(IBlockState ibj, String key) {
		for (Object value : ibj.c())
			if (value.toString().equals(key))
				return (Comparable) value;
		return null;
	}

	@Override
	public ItemStack toItemStack(BlockDataStorage material) {
		Item item = CraftMagicNumbers.getItem(material.getType());
		ItemStack itemStack = CraftItemStack.asBukkitCopy(new net.minecraft.server.v1_8_R3.ItemStack(item));
		itemStack.getData().setData(material.getItemData());
		return itemStack;
	}

	private static Field chunkLoader = Ref.field(ChunkProviderServer.class, "chunkLoader");

	@Override
	public Object getChunk(World world, int x, int z) {
		WorldServer sworld = ((CraftWorld) world).getHandle();
		net.minecraft.server.v1_8_R3.Chunk loaded = ((ChunkProviderServer) sworld.N()).getChunkIfLoaded(x, z);
		if (loaded == null)
			try {
				net.minecraft.server.v1_8_R3.Chunk chunk;
				chunk = ((IChunkLoader) Ref.get(sworld.N(), chunkLoader)).a(sworld, x, z);
				if (chunk != null) {
					chunk.setLastSaved(sworld.getTime());
					if (((ChunkProviderServer) sworld.N()).chunkProvider != null)
						((ChunkProviderServer) sworld.N()).chunkProvider.recreateStructures(chunk, x, z);
				}
				if (chunk != null) {
					((ChunkProviderServer) sworld.N()).chunks.put(ChunkCoordIntPair.a(x, z), chunk);
					postToMainThread(() -> {
						chunk.addEntities();
					});
					loaded = chunk;
				}
			} catch (Exception e) {
			}
		if (loaded == null) { // generate new chunk
			ChunkRegionLoader loader = null;
			if ((IChunkLoader) Ref.get(sworld.N(), chunkLoader) instanceof ChunkRegionLoader)
				loader = (ChunkRegionLoader) Ref.get(sworld.N(), chunkLoader);

			if (loader != null && loader.chunkExists(sworld, x, z))
				loaded = ChunkIOExecutor.syncChunkLoad(sworld, loader, (ChunkProviderServer) sworld.N(), x, z);
			else
				loaded = ((ChunkProviderServer) sworld.N()).originalGetChunkAt(x, z);
			loaded = ((ChunkProviderServer) sworld.N()).chunkProvider.getOrCreateChunk(x, z);
			((ChunkProviderServer) sworld.N()).chunks.put(ChunkCoordIntPair.a(x, z), loaded);
		}
		return loaded;
	}

	@Override
	public void setBlock(Object objChunk, int x, int y, int z, Object IblockData, int data) {
		net.minecraft.server.v1_8_R3.Chunk chunk = (net.minecraft.server.v1_8_R3.Chunk) objChunk;
		if (y < 0)
			return;
		ChunkSection sc = chunk.getSections()[y >> 4];
		if (sc == null)
			return;
		BlockPosition pos = new BlockPosition(x, y, z);

		IBlockData iblock = IblockData == null ? Blocks.AIR.getBlockData() : (IBlockData) IblockData;

		// REMOVE TILE ENTITY
		TileEntity ent = chunk.tileEntities.remove(pos);
		if (ent != null)
			ent.y();
		chunk.world.capturedTileEntities.remove(pos);

		Iterator<BlockState> iterator = chunk.world.capturedBlockStates.iterator();
		while (iterator.hasNext()) {
			BlockState state = iterator.next();
			if (state.getX() == pos.getX() && state.getY() == pos.getY() && state.getZ() == pos.getZ())
				iterator.remove();
		}
		sc.setType(x & 15, y & 15, z & 15, iblock);

		// ADD TILE ENTITY
		if (iblock.getBlock() instanceof IContainer) {
			ent = ((IContainer) iblock.getBlock()).a(chunk.world, 0);
			ent.a(chunk.world);
			ent.a(pos);
			Ref.set(ent, "e", iblock.getBlock());
			Object packet = ent.getUpdatePacket();
			BukkitLoader.getPacketHandler().send(chunk.bukkitChunk.getWorld().getPlayers(), packet);
		}

		// MARK CHUNK TO SAVE
		chunk.mustSave = true;
	}

	@Override
	public void updatePhysics(Object objChunk, int x, int y, int z, Object iblockdata) {
		net.minecraft.server.v1_8_R3.Chunk chunk = (net.minecraft.server.v1_8_R3.Chunk) objChunk;

		BlockPosition blockPos = new BlockPosition(x, y, z);

		doPhysicsAround((WorldServer) chunk.world, blockPos, ((IBlockData) iblockdata).getBlock());
	}

	private void doPhysicsAround(WorldServer world, BlockPosition blockposition, Block block) {
		doPhysics(world, blockposition.west(), block);
		doPhysics(world, blockposition.east(), block);
		doPhysics(world, blockposition.down(), block);
		doPhysics(world, blockposition.up(), block);
		doPhysics(world, blockposition.north(), block);
		doPhysics(world, blockposition.south(), block);
	}

	private void doPhysics(WorldServer world, BlockPosition blockposition, Block block) {
		IBlockData state = world.getType(blockposition);
		state.getBlock().doPhysics(world, blockposition, state, block);
		if (state.getBlock() instanceof BlockFalling)
			((BlockFalling) state.getBlock()).onPlace(world, blockposition, block.getBlockData());
	}

	@Override
	public void updateLightAt(Object chunk, int x, int y, int z) {
		net.minecraft.server.v1_8_R3.Chunk c = (net.minecraft.server.v1_8_R3.Chunk) chunk;
		c.initLighting();
	}

	@Override
	public Object getBlock(Object chunk, int x, int y, int z) {
		net.minecraft.server.v1_8_R3.Chunk c = (net.minecraft.server.v1_8_R3.Chunk) chunk;
		if (y < 0)
			return Blocks.AIR.getBlockData();
		ChunkSection sc = c.getSections()[y >> 4];
		if (sc == null)
			return Blocks.AIR.getBlockData();
		return sc.getType(x & 15, y & 15, z & 15);
	}

	@Override
	public byte getData(Object chunk, int x, int y, int z) {
		return 0;
	}

	@Override
	public String getNBTOfTile(Object objChunk, int x, int y, int z) {
		net.minecraft.server.v1_8_R3.Chunk chunk = (net.minecraft.server.v1_8_R3.Chunk) objChunk;
		NBTTagCompound tag = new NBTTagCompound();
		chunk.a(new BlockPosition(x, y, z), EnumTileEntityState.IMMEDIATE).b(tag);
		return tag.toString();
	}

	@Override
	public void setNBTToTile(Object objChunk, int x, int y, int z, String nbt) {
		net.minecraft.server.v1_8_R3.Chunk chunk = (net.minecraft.server.v1_8_R3.Chunk) objChunk;
		TileEntity ent = chunk.a(new BlockPosition(x, y, z), EnumTileEntityState.IMMEDIATE);
		NBTTagCompound parsedNbt = (NBTTagCompound) parseNBT(nbt);
		parsedNbt.setInt("x", x);
		parsedNbt.setInt("y", y);
		parsedNbt.setInt("z", z);
		ent.a(parsedNbt);
		Object packet = ent.getUpdatePacket();
		BukkitLoader.getPacketHandler().send(chunk.bukkitChunk.getWorld().getPlayers(), packet);
	}

	@Override
	public boolean isTileEntity(Object objChunk, int x, int y, int z) {
		net.minecraft.server.v1_8_R3.Chunk chunk = (net.minecraft.server.v1_8_R3.Chunk) objChunk;
		return chunk.a(new BlockPosition(x, y, z), EnumTileEntityState.IMMEDIATE) != null;
	}

	@Override
	public int getCombinedId(Object IblockDataOrBlock) {
		return Block.getCombinedId((IBlockData) IblockDataOrBlock);
	}

	@Override
	public Object blockPosition(int blockX, int blockY, int blockZ) {
		return new BlockPosition(blockX, blockY, blockZ);
	}

	@Override
	public Object toIBlockData(BlockState state) {
		return Block.getByCombinedId(state.getType().getId() + (state.getRawData() << 12));
	}

	@Override
	public Object toIBlockData(Object data) {
		return null;
	}

	@Override
	public Chunk toBukkitChunk(Object nmsChunk) {
		return ((net.minecraft.server.v1_8_R3.Chunk) nmsChunk).bukkitChunk;
	}

	@Override
	public int getPing(Player player) {
		return ((EntityPlayer) getPlayer(player)).ping;
	}

	@Override
	public Object getPlayerConnection(Player player) {
		return ((EntityPlayer) getPlayer(player)).playerConnection;
	}

	@Override
	public Object getConnectionNetwork(Object playercon) {
		return ((PlayerConnection) playercon).networkManager;
	}

	@Override
	public Object getNetworkChannel(Object network) {
		return ((NetworkManager) network).channel;
	}

	@Override
	public void closeGUI(Player player, Object container, boolean closePacket) {
		if (closePacket)
			BukkitLoader.getPacketHandler().send(player, new PacketPlayOutCloseWindow(BukkitLoader.getNmsProvider().getContainerId(container)));
		EntityPlayer nmsPlayer = (EntityPlayer) getPlayer(player);
		nmsPlayer.activeContainer = nmsPlayer.defaultContainer;
		((Container) container).transferTo(nmsPlayer.activeContainer, (CraftPlayer) player);
	}

	@Override
	public void setSlot(Object container, int slot, Object item) {
		((Container) container).setItem(slot, (net.minecraft.server.v1_8_R3.ItemStack) item);
	}

	@Override
	public void setGUITitle(Player player, Object container, String legacy, int size, String title) {
		int id = ((Container) container).windowId;
		BukkitLoader.getPacketHandler().send(player, packetOpenWindow(id, legacy, size, title));
		BukkitLoader.getPacketHandler().send(player, new PacketPlayOutWindowItems(id, ((Container) container).b));
		((CraftPlayer) player).getHandle().broadcastCarriedItem();
	}

	@Override
	public void openGUI(Player player, Object container, String legacy, int size, String title, ItemStack[] items) {
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		int id = ((Container) container).windowId;
		BukkitLoader.getPacketHandler().send(player, packetOpenWindow(id, legacy, size, title));
		BukkitLoader.getPacketHandler().send(player, new PacketPlayOutWindowItems(id, ((Container) container).b));
		nmsPlayer.activeContainer.transferTo((Container) container, (CraftPlayer) player);
		nmsPlayer.activeContainer = (Container) container;
		((Container) container).addSlotListener(nmsPlayer);
		((Container) container).checkReachable = false;
	}

	@Override
	public void openAnvilGUI(Player player, Object con, String title, ItemStack[] items) {
		ContainerAnvil container = (ContainerAnvil) con;
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		int id = container.windowId;
		BukkitLoader.getPacketHandler().send(player, packetOpenWindow(id, "minecraft:anvil", 0, title));
		BukkitLoader.getPacketHandler().send(player, new PacketPlayOutWindowItems(id, ((Container) container).b));
		nmsPlayer.activeContainer.transferTo((Container) container, (CraftPlayer) player);
		nmsPlayer.activeContainer = container;
		((Container) container).addSlotListener(nmsPlayer);
		((Container) container).checkReachable = false;
	}

	@Override
	public Object createContainer(Inventory inv, Player player) {
		return inv.getType() == InventoryType.ANVIL ? createAnvilContainer(inv, player) : new CraftContainer(inv, player, ((CraftPlayer) player).getHandle().nextContainerCounter());
	}

	@Override
	public Object getSlotItem(Object container, int slot) {
		return slot < 0 ? null : ((Container) container).getSlot(slot).getItem();
	}

	static BlockPosition zero = new BlockPosition(0, 0, 0);

	public Object createAnvilContainer(Inventory inv, Player player) {
		int id = ((CraftPlayer) player).getHandle().nextContainerCounter();
		ContainerAnvil anvil = new ContainerAnvil(((CraftPlayer) player).getHandle().inventory, ((CraftPlayer) player).getHandle().world, v1_8_R3.zero, ((CraftPlayer) player).getHandle());
		anvil.windowId = id;
		for (int i = 0; i < 2; ++i)
			anvil.setItem(i, (net.minecraft.server.v1_8_R3.ItemStack) asNMSItem(inv.getItem(i)));
		return anvil;
	}

	static Field renameText = Ref.field(ContainerAnvil.class, "l");

	@Override
	public String getAnvilRenameText(Object anvil) {
		try {
			return (String) v1_8_R3.renameText.get(anvil);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean processInvClickPacket(Player player, HolderGUI gui, Object provPacket) {
		PacketPlayInWindowClick packet = (PacketPlayInWindowClick) provPacket;
		int slot = packet.b();
		if (slot == -999)
			return false;

		int id = packet.a();
		int mouseClick = packet.c();
		InventoryClickType type = InventoryClickType.values()[packet.f()];

		Object container = gui.getContainer(player);
		if (container == null)
			return false;
		ItemStack item = asBukkitItem(packet.e());
		if ((type == InventoryClickType.QUICK_MOVE || type == InventoryClickType.CLONE || type == InventoryClickType.THROW || item.getType() == Material.AIR) && item.getType() == Material.AIR)
			item = asBukkitItem(getSlotItem(container, slot));
		boolean cancel = false;
		if (InventoryClickType.SWAP == type) {
			item = player.getInventory().getItem(mouseClick);
			mouseClick = 0;
			cancel = true;
		}
		if (item == null)
			item = new ItemStack(Material.AIR);

		ItemStack before = player.getItemOnCursor();
		ClickType clickType = BukkitLoader.buildClick(item, type, slot, mouseClick);
		if (!cancel)
			cancel = BukkitLoader.useItem(player, item, gui, slot, clickType);
		if (!gui.isInsertable())
			cancel = true;

		int gameSlot = slot > gui.size() - 1 ? InventoryUtils.convertToPlayerInvSlot(slot - gui.size()) : slot;
		if (!cancel)
			cancel = gui.onIteractItem(player, item, clickType, gameSlot, slot < gui.size());
		else
			gui.onIteractItem(player, item, clickType, gameSlot, slot < gui.size());

		int position = 0;
		if (!(gui instanceof AnvilGUI) && !cancel && type == InventoryClickType.QUICK_MOVE) {
			ItemStack[] contents = slot < gui.size() ? player.getInventory().getContents() : gui.getInventory().getContents();
			List<Integer> modified = slot < gui.size()
					? InventoryUtils.shift(slot, player, gui, clickType, gui instanceof AnvilGUI ? DestinationType.PLAYER_INV_ANVIL : DestinationType.PLAYER_INV_CUSTOM_INV, null, contents, item)
					: InventoryUtils.shift(slot, player, gui, clickType, DestinationType.CUSTOM_INV, gui.getNotInterableSlots(player), contents, item);
			if (!modified.isEmpty())
				if (slot < gui.size()) {
					boolean canRemove = !modified.contains(-1);
					player.getInventory().setContents(contents);
					if (canRemove)
						gui.remove(gameSlot);
					else
						gui.getInventory().setItem(gameSlot, item);
				} else {
					boolean canRemove = !modified.contains(-1);
					gui.getInventory().setContents(contents);
					if (canRemove)
						player.getInventory().setItem(gameSlot, null);
					else
						player.getInventory().setItem(gameSlot, item);
				}
			return true;
		}
		if (cancel) {
			// MOUSE
			if (!(gui instanceof AnvilGUI) || gui instanceof AnvilGUI && slot != 2)
				BukkitLoader.getPacketHandler().send(player, packetSetSlot(-1, -1, 0, asNMSItem(before)));
			switch (type) {
			case CLONE:
				return true;
			case SWAP:
			case QUICK_MOVE:
			case PICKUP_ALL:
				// TOP
				for (ItemStack cItem : gui.getInventory().getContents())
					BukkitLoader.getPacketHandler().send(player, packetSetSlot(id, position++, 0, asNMSItem(cItem)));
				// BUTTON
				player.updateInventory();
				return true;
			default:
				BukkitLoader.getPacketHandler().send(player, packetSetSlot(id, slot, 0, getSlotItem(container, slot)));
				return true;
			}
		}
		return false;
	}

	static Field field = Ref.field(PacketStatusOutServerInfo.class, "b");

	@Override
	public boolean processServerListPing(String player, Object channel, Object packet) {
		PacketStatusOutServerInfo status = (PacketStatusOutServerInfo) packet;
		ServerPing ping;
		try {
			ping = (ServerPing) v1_8_R3.field.get(status);
		} catch (Exception e) {
			return false;
		}
		List<GameProfileHandler> players = new ArrayList<>();
		for (Player p : getOnlinePlayers())
			players.add(GameProfileHandler.of(p.getName(), p.getUniqueId()));
		ServerListPingEvent event = new ServerListPingEvent(getOnlinePlayers().size(), Bukkit.getMaxPlayers(), players, Bukkit.getMotd(), ping.d(),
				((InetSocketAddress) ((Channel) channel).remoteAddress()).getAddress(), ping.c().a(), ping.c().b());
		EventManager.call(event);
		if (event.isCancelled())
			return true;
		ServerPingPlayerSample playerSample = new ServerPingPlayerSample(event.getMaxPlayers(), event.getOnlinePlayers());
		if (event.getPlayersText() != null) {
			GameProfile[] profiles = new GameProfile[event.getPlayersText().size()];
			int i = -1;
			for (GameProfileHandler s : event.getPlayersText())
				profiles[++i] = new GameProfile(s.getUUID(), s.getUsername());
			playerSample.a(profiles);
		} else
			playerSample.a(new GameProfile[0]);
		ping.setPlayerSample(playerSample);

		if (event.getMotd() != null)
			ping.setMOTD((IChatBaseComponent) this.toIChatBaseComponent(ComponentAPI.fromString(event.getMotd())));
		else
			ping.setMOTD((IChatBaseComponent) BukkitLoader.getNmsProvider().chatBase("{\"text\":\"\"}"));
		if (event.getVersion() != null)
			ping.setServerInfo(new ServerData(event.getVersion(), event.getProtocol()));
		if (event.getFalvicon() != null)
			ping.setFavicon(event.getFalvicon());
		return false;
	}

	@Override
	public Object getNBT(Entity entity) {
		NBTTagCompound nbt = new NBTTagCompound();
		((CraftEntity) entity).getHandle().e(nbt);
		return nbt;
	}

	@Override
	public Object setString(Object nbt, String path, String value) {
		((NBTTagCompound) nbt).setString(path, value);
		return nbt;
	}

	@Override
	public Object setInteger(Object nbt, String path, int value) {
		((NBTTagCompound) nbt).setInt(path, value);
		return nbt;
	}

	@Override
	public Object setDouble(Object nbt, String path, double value) {
		((NBTTagCompound) nbt).setDouble(path, value);
		return nbt;
	}

	@Override
	public Object setLong(Object nbt, String path, long value) {
		((NBTTagCompound) nbt).setLong(path, value);
		return nbt;
	}

	@Override
	public Object setShort(Object nbt, String path, short value) {
		((NBTTagCompound) nbt).setShort(path, value);
		return nbt;
	}

	@Override
	public Object setFloat(Object nbt, String path, float value) {
		((NBTTagCompound) nbt).setFloat(path, value);
		return nbt;
	}

	@Override
	public Object setBoolean(Object nbt, String path, boolean value) {
		((NBTTagCompound) nbt).setBoolean(path, value);
		return nbt;
	}

	@Override
	public Object setIntArray(Object nbt, String path, int[] value) {
		((NBTTagCompound) nbt).setIntArray(path, value);
		return nbt;
	}

	@Override
	public Object setByteArray(Object nbt, String path, byte[] value) {
		((NBTTagCompound) nbt).setByteArray(path, value);
		return nbt;
	}

	@Override
	public Object setNBTBase(Object nbt, String path, Object value) {
		((NBTTagCompound) nbt).set(path, (NBTBase) value);
		return nbt;
	}

	@Override
	public String getString(Object nbt, String path) {
		return ((NBTTagCompound) nbt).getString(path);
	}

	@Override
	public int getInteger(Object nbt, String path) {
		return ((NBTTagCompound) nbt).getInt(path);
	}

	@Override
	public double getDouble(Object nbt, String path) {
		return ((NBTTagCompound) nbt).getDouble(path);
	}

	@Override
	public long getLong(Object nbt, String path) {
		return ((NBTTagCompound) nbt).getLong(path);
	}

	@Override
	public short getShort(Object nbt, String path) {
		return ((NBTTagCompound) nbt).getShort(path);
	}

	@Override
	public float getFloat(Object nbt, String path) {
		return ((NBTTagCompound) nbt).getFloat(path);
	}

	@Override
	public boolean getBoolean(Object nbt, String path) {
		return ((NBTTagCompound) nbt).getBoolean(path);
	}

	@Override
	public int[] getIntArray(Object nbt, String path) {
		return ((NBTTagCompound) nbt).getIntArray(path);
	}

	@Override
	public byte[] getByteArray(Object nbt, String path) {
		return ((NBTTagCompound) nbt).getByteArray(path);
	}

	@Override
	public Object getNBTBase(Object nbt, String path) {
		return ((NBTTagCompound) nbt).get(path);
	}

	@Override
	public Set<String> getKeys(Object nbt) {
		return ((NBTTagCompound) nbt).c();
	}

	@Override
	public boolean hasKey(Object nbt, String path) {
		return ((NBTTagCompound) nbt).hasKey(path);
	}

	@Override
	public void removeKey(Object nbt, String path) {
		((NBTTagCompound) nbt).remove(path);
	}

	@Override
	public Object setByte(Object nbt, String path, byte value) {
		((NBTTagCompound) nbt).setByte(path, value);
		return nbt;
	}

	@Override
	public byte getByte(Object nbt, String path) {
		return ((NBTTagCompound) nbt).getByte(path);
	}

	@Override
	public Object getDataWatcher(Entity entity) {
		return ((CraftEntity) entity).getHandle().getDataWatcher();
	}

	@Override
	public Object getDataWatcher(Object entity) {
		return ((net.minecraft.server.v1_8_R3.Entity) entity).getDataWatcher();
	}

	@Override
	public int incrementStateId(Object container) {
		return 0;
	}

	@Override
	public Object packetEntityHeadRotation(Entity entity) {
		return new PacketPlayOutEntityHeadRotation((net.minecraft.server.v1_8_R3.Entity) getEntity(entity), (byte) (entity.getLocation().getYaw() * 256F / 360F));
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
		return new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.valueOf(type.name()), (EntityPlayer) getPlayer(player));
	}

	private static Field playerInfo = Ref.field(PacketPlayOutPlayerInfo.class, "b");

	private static Constructor<?> infoData = Ref.constructor(Ref.nms("", "PacketPlayOutPlayerInfo$PlayerInfoData"), PacketPlayOutPlayerInfo.class, GameProfile.class, int.class, EnumGamemode.class,
			IChatBaseComponent.class);

	@SuppressWarnings("unchecked")
	@Override
	public Object packetPlayerInfo(PlayerInfoType type, GameProfileHandler gameProfile, int latency, GameMode gameMode, Component playerName) {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.valueOf(type.name()), Collections.emptyList());
		((List<Object>) Ref.get(packet, playerInfo))
				.add(Ref.newInstance(infoData, packet, toGameProfile(gameProfile), latency, EnumGamemode.valueOf(gameMode.name()), toIChatBaseComponent(playerName)));
		return packet;
	}

	@Override
	public Object packetPosition(double x, double y, double z, float yaw, float pitch) {
		return new PacketPlayOutPosition(x, y, z, yaw, pitch, Collections.emptySet());
	}

	@Override
	public Object packetRespawn(Player player) {
		EntityPlayer entityPlayer = (EntityPlayer) getPlayer(player);
		WorldServer worldserver = entityPlayer.u();
		byte actualDimension = (byte) worldserver.getWorld().getEnvironment().getId();
		return new PacketPlayOutRespawn((byte) (actualDimension >= 0 ? -1 : 0), worldserver.getDifficulty(), worldserver.getWorldData().getType(), entityPlayer.playerInteractManager.getGameMode());
	}

	@Override
	public String getProviderName() {
		return "1_8_R3 (1.8.8)";
	}

	@Override
	public int getContainerStateId(Object container) {
		return 0;
	}

	@Override
	public void loadParticles() {
		for (EnumParticle s : EnumParticle.values())
			me.devtec.theapi.bukkit.game.particles.Particle.identifier.put(s.name(), s);
	}

	@Override
	public Object toGameProfile(GameProfileHandler gameProfileHandler) {
		GameProfile profile = new GameProfile(gameProfileHandler.getUUID(), gameProfileHandler.getUsername());
		for (Entry<String, PropertyHandler> entry : gameProfileHandler.getProperties().entrySet())
			profile.getProperties().put(entry.getKey(), new Property(entry.getValue().getName(), entry.getValue().getValues(), entry.getValue().getSignature()));
		return profile;
	}

	@Override
	public GameProfileHandler fromGameProfile(Object gameProfile) {
		GameProfile profile = (GameProfile) gameProfile;
		GameProfileHandler handler = GameProfileHandler.of(profile.getName(), profile.getId());
		for (Entry<String, Property> entry : profile.getProperties().entries())
			handler.getProperties().put(entry.getKey(), PropertyHandler.of(entry.getValue().getName(), entry.getValue().getValue(), entry.getValue().getSignature()));
		return handler;
	}

	@Override
	public Object getGameProfile(Object nmsPlayer) {
		return ((EntityPlayer) nmsPlayer).getProfile();
	}
}