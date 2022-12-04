package me.devtec.theapi.bukkit.nms;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_7_R4.CraftChunk;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.chunkio.ChunkIOExecutor;
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

import me.devtec.shared.Ref;
import me.devtec.shared.components.ClickEvent;
import me.devtec.shared.components.Component;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.components.HoverEvent;
import me.devtec.shared.events.EventManager;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.events.ServerListPingEvent;
import me.devtec.theapi.bukkit.game.BlockDataStorage;
import me.devtec.theapi.bukkit.gui.AnvilGUI;
import me.devtec.theapi.bukkit.gui.GUI.ClickType;
import me.devtec.theapi.bukkit.gui.HolderGUI;
import me.devtec.theapi.bukkit.nms.GameProfileHandler.PropertyHandler;
import me.devtec.theapi.bukkit.nms.utils.InventoryUtils;
import me.devtec.theapi.bukkit.nms.utils.InventoryUtils.DestinationType;
import me.devtec.theapi.bukkit.tablist.TabEntry;
import me.devtec.theapi.bukkit.tablist.Tablist;
import net.minecraft.server.v1_7_R4.Block;
import net.minecraft.server.v1_7_R4.BlockFalling;
import net.minecraft.server.v1_7_R4.Blocks;
import net.minecraft.server.v1_7_R4.ChatClickable;
import net.minecraft.server.v1_7_R4.ChatComponentText;
import net.minecraft.server.v1_7_R4.ChatHoverable;
import net.minecraft.server.v1_7_R4.ChatModifier;
import net.minecraft.server.v1_7_R4.ChatSerializer;
import net.minecraft.server.v1_7_R4.ChunkCoordIntPair;
import net.minecraft.server.v1_7_R4.ChunkPosition;
import net.minecraft.server.v1_7_R4.ChunkProviderServer;
import net.minecraft.server.v1_7_R4.ChunkRegionLoader;
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
import net.minecraft.server.v1_7_R4.IChunkLoader;
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
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.io.netty.channel.Channel;

public class v1_7_R4 implements NmsProvider {
	private MinecraftServer server = MinecraftServer.getServer();
	private static final ChatComponentText empty = new ChatComponentText("");
	private static Field channel = Ref.field(NetworkManager.class, "m");
	private static Field posX = Ref.field(PacketPlayOutBlockChange.class, "a"), posY = Ref.field(PacketPlayOutBlockChange.class, "b"), posZ = Ref.field(PacketPlayOutBlockChange.class, "c");
	private static Field score_a = Ref.field(PacketPlayOutScoreboardScore.class, "a"), score_b = Ref.field(PacketPlayOutScoreboardScore.class, "b"),
			score_c = Ref.field(PacketPlayOutScoreboardScore.class, "c"), score_d = Ref.field(PacketPlayOutScoreboardScore.class, "d");

	@Override
	public Collection<? extends Player> getOnlinePlayers() {
		return Bukkit.getOnlinePlayers().length == 0 ? Collections.emptyList() : Arrays.asList(Bukkit.getOnlinePlayers());
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
	public Object getScoreboardAction(Action type) {
		return type.getId();
	}

	@Override
	public int getEntityId(Object entity) {
		return ((net.minecraft.server.v1_7_R4.Entity) entity).getId();
	}

	@Override
	public Object getEnumScoreboardHealthDisplay(DisplayType type) {
		return null;
	}

	@Override
	public Object getNBT(ItemStack itemStack) {
		net.minecraft.server.v1_7_R4.ItemStack item = (net.minecraft.server.v1_7_R4.ItemStack) asNMSItem(itemStack);
		NBTTagCompound nbt = item.getTag();
		if (nbt == null)
			item.setTag(nbt = new NBTTagCompound());
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
		if (nbt instanceof NBTEdit)
			nbt = ((NBTEdit) nbt).getNBT();
		net.minecraft.server.v1_7_R4.ItemStack i = (net.minecraft.server.v1_7_R4.ItemStack) asNMSItem(stack);
		i.setTag((NBTTagCompound) nbt);
		return asBukkitItem(i);
	}

	private static final net.minecraft.server.v1_7_R4.ItemStack air = CraftItemStack.asNMSCopy(new ItemStack(Material.AIR));

	@Override
	public Object asNMSItem(ItemStack stack) {
		if (stack == null)
			return v1_7_R4.air;
		return CraftItemStack.asNMSCopy(stack);
	}

	@Override
	public ItemStack asBukkitItem(Object stack) {
		return CraftItemStack.asCraftMirror((net.minecraft.server.v1_7_R4.ItemStack) stack);
	}

	@Override
	public Object packetOpenWindow(int id, String legacy, int size, Component title) {
		return new PacketPlayOutOpenWindow(id, legacy.equals("minecraft:chest") ? 0 : 8, title.toString(), size, false);
	}

	@Override
	public int getContainerId(Object container) {
		return ((Container) container).windowId;
	}

	@Override
	public Object packetResourcePackSend(String url, String hash, boolean requireRP, Component prompt) {
		return null;
	}

	@Override
	public Object packetSetSlot(int container, int slot, int stateId, Object itemStack) {
		return new PacketPlayOutSetSlot(container, slot, (net.minecraft.server.v1_7_R4.ItemStack) (itemStack == null ? asNMSItem(null) : itemStack));
	}

	public Object packetSetSlot(int container, int slot, Object itemStack) {
		return this.packetSetSlot(container, slot, 0, itemStack);
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
		return new PacketPlayOutNamedEntitySpawn((EntityHuman) player);
	}

	@Override
	public Object packetSpawnEntityLiving(Object entityLiving) {
		return new PacketPlayOutSpawnEntityLiving((EntityLiving) entityLiving);
	}

	@Override
	public Object packetPlayerListHeaderFooter(Component header, Component footer) {
		return null;
	}

	@Override
	public Object packetBlockChange(int x, int y, int z, Object iblockdata, int data) {
		PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange();
		packet.data = data;
		packet.block = iblockdata == null ? Blocks.AIR : (Block) iblockdata;
		try {
			v1_7_R4.posX.set(packet, x);
			v1_7_R4.posY.set(packet, y);
			v1_7_R4.posZ.set(packet, z);
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
		PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();
		try {
			v1_7_R4.score_a.set(packet, line);
			v1_7_R4.score_b.set(packet, player);
			v1_7_R4.score_c.set(packet, score);
			v1_7_R4.score_d.set(packet, getScoreboardAction(action));
		} catch (Exception err) {
		}
		return packet;
	}

	@Override
	public Object packetTitle(TitleAction action, Component text, int fadeIn, int stay, int fadeOut) {
		if (action == TitleAction.ACTIONBAR)
			return this.packetChat(ChatType.GAME_INFO, text, null);
		return null;
	}

	@Override
	public Object packetChat(ChatType type, Object chatBase, UUID uuid) {
		return new PacketPlayOutChat((IChatBaseComponent) chatBase, type.toByte());
	}

	@Override
	public Object packetChat(ChatType type, Component text, UUID uuid) {
		return this.packetChat(type, this.toIChatBaseComponent(text), uuid);
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

	private IChatBaseComponent convert(Component c) {
		ChatComponentText current = new ChatComponentText(c.toString()); // legacy
		ChatModifier modif = current.getChatModifier();
		if (c.getColor() != null)
			modif.setColor(EnumChatFormat.valueOf(c.getColor().toUpperCase()));
		if (c.getClickEvent() != null)
			modif.setChatClickable(new ChatClickable(EnumClickAction.valueOf(c.getClickEvent().getAction().name()), c.getClickEvent().getValue()));
		if (c.getHoverEvent() != null)
			modif.a(new ChatHoverable(EnumHoverAction.valueOf(c.getHoverEvent().getAction().name()), (IChatBaseComponent) this.toIChatBaseComponent(c.getHoverEvent().getValue())));
		modif.setBold(c.isBold());
		modif.setItalic(c.isItalic());
		modif.setRandom(c.isObfuscated());
		modif.setUnderline(c.isUnderlined());
		modif.setStrikethrough(c.isStrikethrough());
		return current.setChatModifier(modif);
	}

	@Override
	public Object[] toIChatBaseComponents(List<Component> components) {
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
			if (c.getExtra() != null)
				addConverted(chat, c.getExtra());
		}
	}

	@Override
	public Object[] toIChatBaseComponents(Component co) {
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

	@SuppressWarnings("unchecked")
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
		return main.a().isEmpty() ? v1_7_R4.empty : main;
	}

	@Override
	public Object toIChatBaseComponent(List<Component> cc) {
		ChatComponentText main = new ChatComponentText("");
		for (Component c : cc)
			main.addSibling((IChatBaseComponent) this.toIChatBaseComponent(c));
		return main.a().isEmpty() ? v1_7_R4.empty : main;
	}

	@Override
	public Object chatBase(String json) {
		return ChatSerializer.a(json);
	}

	@Override
	public Component fromIChatBaseComponent(Object componentObject) {
		if (componentObject == null)
			return Component.EMPTY_COMPONENT;
		IChatBaseComponent component = (IChatBaseComponent) componentObject;
		if (component.e().isEmpty()) {
			Component comp = new Component("");
			if (!component.e().isEmpty()) {
				List<Component> extra = new ArrayList<>();
				for (Object base : component.a())
					extra.add(fromIChatBaseComponent(base));
				comp.setExtra(extra);
			}
			return comp;
		}
		Component comp = new Component(component.e().replaceAll("§[A-Fa-f0-9K-Ok-oRr]", ""));
		ChatModifier modif = component.getChatModifier();
		if (modif.a() != null)
			comp.setColor(modif.a().name().toLowerCase());

		if (modif.h() != null)
			comp.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(modif.h().a().name()), modif.h().b()));

		if (modif.i() != null)
			comp.setHoverEvent(new HoverEvent(HoverEvent.Action.valueOf(modif.i().a().b()), fromIChatBaseComponent(modif.i().b())));

		comp.setBold(modif.b());
		comp.setItalic(modif.c());
		comp.setObfuscated(modif.d());
		comp.setUnderlined(modif.e());
		comp.setStrikethrough(modif.f());

		if (!component.e().isEmpty()) {
			List<Component> extra = new ArrayList<>();
			for (Object base : component.a())
				extra.add(fromIChatBaseComponent(base));
			comp.setExtra(extra);
		}
		return comp;
	}

	@Override
	public BlockDataStorage toMaterial(Object block) {
		if (block instanceof Block)
			return new BlockDataStorage(CraftMagicNumbers.getMaterial((Block) block));
		return new BlockDataStorage(Material.AIR);
	}

	@Override
	public Object toIBlockData(BlockDataStorage material) {
		if (material == null || material.getType() == null || material.getType() == Material.AIR)
			return Blocks.AIR;
		return CraftMagicNumbers.getBlock(material.getType());
	}

	@Override
	public Object toBlock(BlockDataStorage material) {
		if (material == null || material.getType() == null || material.getType() == Material.AIR)
			return Blocks.AIR;
		return CraftMagicNumbers.getBlock(material.getType());
	}

	@Override
	public ItemStack toItemStack(BlockDataStorage material) {
		Item item = CraftMagicNumbers.getItem(material.getType());
		ItemStack itemStack = CraftItemStack.asBukkitCopy(new net.minecraft.server.v1_7_R4.ItemStack(item));
		itemStack.getData().setData(material.getItemData());
		return itemStack;
	}

	private static Field chunkLoader = Ref.field(ChunkProviderServer.class, "f");

	@Override
	public Object getChunk(World world, int x, int z) {
		WorldServer sworld = ((CraftWorld) world).getHandle();
		net.minecraft.server.v1_7_R4.Chunk loaded = ((ChunkProviderServer) sworld.L()).getChunkIfLoaded(x, z);
		if (loaded == null)
			try {
				net.minecraft.server.v1_7_R4.Chunk chunk;
				chunk = ((IChunkLoader) Ref.get(sworld.L(), chunkLoader)).a(sworld, x, z);
				if (chunk != null) {
					chunk.lastSaved = sworld.getTime();
					if (((ChunkProviderServer) sworld.L()).chunkProvider != null)
						((ChunkProviderServer) sworld.L()).chunkProvider.recreateStructures(x, z);
				}
				if (chunk != null) {
					((ChunkProviderServer) sworld.L()).chunks.put(ChunkCoordIntPair.a(x, z), chunk);
					postToMainThread(() -> {
						chunk.addEntities();
					});
					loaded = chunk;
				}
			} catch (Exception e) {
			}
		if (loaded == null) { // generate new chunk
			ChunkRegionLoader loader = null;
			if ((IChunkLoader) Ref.get(sworld.L(), chunkLoader) instanceof ChunkRegionLoader)
				loader = (ChunkRegionLoader) Ref.get(sworld.L(), chunkLoader);

			if (loader != null && loader.chunkExists(sworld, x, z))
				loaded = ChunkIOExecutor.syncChunkLoad(sworld, loader, (ChunkProviderServer) sworld.L(), x, z);
			else
				loaded = ((ChunkProviderServer) sworld.L()).originalGetChunkAt(x, z);
			loaded = ((ChunkProviderServer) sworld.L()).chunkProvider.getOrCreateChunk(x, z);
			((ChunkProviderServer) sworld.L()).chunks.put(ChunkCoordIntPair.a(x, z), loaded);
		}
		return loaded;
	}

	private static Field tileEntityBlock = Ref.field(TileEntity.class, "h");
	private static Field isCachedInWorld = Ref.field(net.minecraft.server.v1_7_R4.World.class, "M");
	private static Field tileEntityWorld = Ref.field(net.minecraft.server.v1_7_R4.World.class, "a");

	@SuppressWarnings("unchecked")
	@Override
	public void setBlock(Object objChunk, int x, int y, int z, Object block, int data) {
		net.minecraft.server.v1_7_R4.Chunk chunk = (net.minecraft.server.v1_7_R4.Chunk) objChunk;
		if (y < 0)
			return;
		ChunkSection sc = chunk.getSections()[y >> 4];
		if (sc == null)
			return;

		ChunkPosition pos = new ChunkPosition(x & 15, y & 15, z & 15);
		Block iblock = block == null ? Blocks.AIR : (Block) block;

		boolean onlyModifyState = iblock instanceof IContainer;

		// REMOVE TILE ENTITY IF NOT SAME TYPE
		TileEntity ent = onlyModifyState ? (TileEntity) chunk.tileEntities.get(pos) : (TileEntity) chunk.tileEntities.remove(pos);
		if (ent != null) {
			boolean shouldSkip = true;
			if (!onlyModifyState) {
				shouldSkip = false;
				chunk.tileEntities.remove(pos);
			} else if (onlyModifyState && ent.q().getClass().equals(iblock.getClass())) {
				shouldSkip = false;
				onlyModifyState = false;
			}
			if (!shouldSkip) {
				ent.s();
				if ((boolean) Ref.get(chunk.world, isCachedInWorld)) {
					@SuppressWarnings("rawtypes")
					List list = (List) Ref.get(chunk.world, tileEntityWorld);
					for (int l = 0; l < list.size(); ++l) {
						TileEntity state = (TileEntity) list.get(l);
						if (!state.r() && state.x == x && state.y == y && state.z == z) {
							list.remove(l);
							break;
						}
					}
				}
				Iterator<BlockState> iterator = chunk.world.capturedBlockStates.iterator();
				while (iterator.hasNext()) {
					BlockState state = iterator.next();
					if (state.getX() == x && state.getY() == y && state.getZ() == z)
						iterator.remove();
				}
			}
		}

		sc.setTypeId(x & 15, y & 15, z & 15, iblock);
		sc.setData(x & 15, y & 15, z & 15, data);

		// ADD TILE ENTITY
		if (iblock instanceof IContainer && !onlyModifyState) {
			ent = ((IContainer) iblock).a(chunk.world, 0);
			chunk.tileEntities.put(pos, ent);
			ent.a(chunk.world);
			ent.x = x;
			ent.y = y;
			ent.z = z;
			Ref.set(ent, tileEntityBlock, iblock);
			Object packet = ent.getUpdatePacket();
			BukkitLoader.getPacketHandler().send(chunk.world.getWorld().getPlayers(), packet);
		}

		// MARK CHUNK TO SAVE
		chunk.mustSave = true;
	}

	@Override
	public void updatePhysics(Object objChunk, int x, int y, int z, Object iblockdata) {
		net.minecraft.server.v1_7_R4.Chunk chunk = (net.minecraft.server.v1_7_R4.Chunk) objChunk;

		doPhysicsAround((WorldServer) chunk.world, x, y, z, (Block) iblockdata);
	}

	private void doPhysicsAround(WorldServer world, int x, int y, int z, Block block) {
		doPhysics(world, x + BlockFace.WEST.getModX(), y, z + BlockFace.WEST.getModZ(), block);
		doPhysics(world, x + BlockFace.EAST.getModX(), y, z + BlockFace.EAST.getModZ(), block);
		doPhysics(world, x, y - 1, z, block);
		doPhysics(world, x, y + 1, z, block);
		doPhysics(world, x + BlockFace.NORTH.getModX(), y, z + BlockFace.NORTH.getModZ(), block);
		doPhysics(world, x + BlockFace.SOUTH.getModX(), y, z + BlockFace.SOUTH.getModZ(), block);
	}

	private void doPhysics(WorldServer world, int x, int y, int z, Block block) {
		Block state = world.getType(x, y, z);
		state.doPhysics(world, x, y, z, block);
		if (state instanceof BlockFalling)
			((BlockFalling) state).onPlace(world, x, y, z);
	}

	@Override
	public void updateLightAt(Object chunk, int x, int y, int z) {
		net.minecraft.server.v1_7_R4.Chunk c = (net.minecraft.server.v1_7_R4.Chunk) chunk;
		c.initLighting();
	}

	@Override
	public Object getBlock(Object chunk, int x, int y, int z) {
		net.minecraft.server.v1_7_R4.Chunk c = (net.minecraft.server.v1_7_R4.Chunk) chunk;
		if (y < 0)
			return Blocks.AIR;
		ChunkSection sc = c.getSections()[y >> 4];
		if (sc == null)
			return Blocks.AIR;
		return sc.getTypeId(x & 15, y & 15, z & 15);
	}

	@Override
	public byte getData(Object chunk, int x, int y, int z) {
		net.minecraft.server.v1_7_R4.Chunk c = (net.minecraft.server.v1_7_R4.Chunk) chunk;
		if (y < 0)
			return 0;
		ChunkSection sc = c.getSections()[y >> 4];
		if (sc == null)
			return 0;
		return (byte) sc.getData(x & 15, y & 15, z & 15);
	}

	@Override
	public String getNBTOfTile(Object objChunk, int x, int y, int z) {
		net.minecraft.server.v1_7_R4.Chunk chunk = (net.minecraft.server.v1_7_R4.Chunk) objChunk;
		NBTTagCompound tag = new NBTTagCompound();
		chunk.e(x & 15, y & 15, z & 15).b(tag);
		return tag.toString();
	}

	@Override
	public void setNBTToTile(Object objChunk, int x, int y, int z, String nbt) {
		net.minecraft.server.v1_7_R4.Chunk chunk = (net.minecraft.server.v1_7_R4.Chunk) objChunk;
		TileEntity ent = chunk.e(x & 15, y & 15, z & 15);
		NBTTagCompound parsedNbt = (NBTTagCompound) parseNBT(nbt);
		parsedNbt.setInt("x", x);
		parsedNbt.setInt("y", y);
		parsedNbt.setInt("z", z);
		ent.a(parsedNbt);
		Object packet = ent.getUpdatePacket();
		BukkitLoader.getPacketHandler().send(chunk.world.getWorld().getPlayers(), packet);
	}

	@Override
	public boolean isTileEntity(Object objChunk, int x, int y, int z) {
		net.minecraft.server.v1_7_R4.Chunk chunk = (net.minecraft.server.v1_7_R4.Chunk) objChunk;
		return chunk.e(x & 15, y & 15, z & 15) != null;
	}

	@Override
	public int getCombinedId(Object IblockDataOrBlock) {
		return Block.getId((Block) IblockDataOrBlock);
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
	public Chunk toBukkitChunk(Object nmsChunk) {
		return ((net.minecraft.server.v1_7_R4.Chunk) nmsChunk).bukkitChunk;
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
		try {
			return v1_7_R4.channel.get(network);
		} catch (Exception e) {
			return null;
		}
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
		((Container) container).setItem(slot, (net.minecraft.server.v1_7_R4.ItemStack) item);
	}

	@Override
	public void setGUITitle(Player player, Object container, String legacy, int size, Component title) {
		int id = ((Container) container).windowId;
		BukkitLoader.getPacketHandler().send(player, packetOpenWindow(id, legacy, size, title));
		net.minecraft.server.v1_7_R4.ItemStack carried = ((CraftPlayer) player).getHandle().inventory.getCarried();
		if (!Item.REGISTRY.c(carried.getItem()).equals("air"))
			BukkitLoader.getPacketHandler().send(player, new PacketPlayOutSetSlot(id, -1, carried));
		int slot = 0;
		for (Object objItem : ((Container) container).a()) {
			if (slot == size)
				break;
			net.minecraft.server.v1_7_R4.ItemStack item = (net.minecraft.server.v1_7_R4.ItemStack) objItem;
			if (!Item.REGISTRY.c(item.getItem()).equals("air"))
				BukkitLoader.getPacketHandler().send(player, new PacketPlayOutSetSlot(id, slot, item));
			++slot;
		}
	}

	@Override
	public void openGUI(Player player, Object container, String legacy, int size, Component title) {
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		int id = ((Container) container).windowId;
		BukkitLoader.getPacketHandler().send(player, packetOpenWindow(id, legacy, size, title));
		nmsPlayer.activeContainer.transferTo((Container) container, (CraftPlayer) player);
		nmsPlayer.activeContainer = (Container) container;
		((Container) container).addSlotListener(nmsPlayer);
		((Container) container).checkReachable = false;
	}

	@Override
	public void openAnvilGUI(Player player, Object con, Component title) {
		ContainerAnvil container = (ContainerAnvil) con;
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		int id = container.windowId;
		BukkitLoader.getPacketHandler().send(player, packetOpenWindow(id, "minecraft:anvil", 0, title));
		int slot = 0;
		for (Object objItem : ((Container) container).b) {
			if (slot == 3)
				break;
			net.minecraft.server.v1_7_R4.ItemStack item = (net.minecraft.server.v1_7_R4.ItemStack) objItem;
			if (!Item.REGISTRY.c(item.getItem()).equals("air"))
				BukkitLoader.getPacketHandler().send(player, new PacketPlayOutSetSlot(id, slot, item));
			++slot;
		}
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

	public Object createAnvilContainer(Inventory inv, Player player) {
		int id = ((CraftPlayer) player).getHandle().nextContainerCounter();
		ContainerAnvil anvil = new ContainerAnvil(((CraftPlayer) player).getHandle().inventory, ((CraftPlayer) player).getHandle().world, 0, 0, 0, ((CraftPlayer) player).getHandle());
		anvil.windowId = id;
		for (int i = 0; i < 2; ++i)
			anvil.setItem(i, (net.minecraft.server.v1_7_R4.ItemStack) asNMSItem(inv.getItem(i)));
		return anvil;
	}

	static Field renameText = Ref.field(ContainerAnvil.class, "n");

	@Override
	public String getAnvilRenameText(Object anvil) {
		try {
			return (String) v1_7_R4.renameText.get(anvil);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean processInvClickPacket(Player player, HolderGUI gui, Object provPacket) {
		PacketPlayInWindowClick packet = (PacketPlayInWindowClick) provPacket;
		int slot = packet.slot;
		if (slot == -999)
			return false;

		int id = packet.c();
		int mouseClick = packet.e();
		int type = packet.h();

		Object container = gui.getContainer(player);
		if (container == null)
			return false;
		ItemStack item = asBukkitItem(packet.g());
		if ((type == 1 || type == 3 || type == 4 || item.getType() == Material.AIR) && item.getType() == Material.AIR)
			item = asBukkitItem(getSlotItem(container, slot));
		boolean cancel = false;
		if (type == 3) {
			item = player.getInventory().getItem(mouseClick);
			mouseClick = 0;
			cancel = true;
		}
		if (item == null)
			item = new ItemStack(Material.AIR);

		ItemStack before = player.getItemOnCursor();
		ClickType clickType = InventoryUtils.buildClick(type == 5 ? 1 : type == 1 ? 2 : 0, mouseClick);
		int gameSlot = slot > gui.size() - 1 ? InventoryUtils.convertToPlayerInvSlot(slot - gui.size()) : slot;
		if (!cancel)
			cancel = InventoryUtils.useItem(player, gui, slot, clickType);
		if (!gui.isInsertable())
			cancel = true;

		if (!cancel)
			cancel = gui.onInteractItem(player, item, before, clickType, gameSlot, slot < gui.size());
		else
			gui.onInteractItem(player, item, before, clickType, gameSlot, slot < gui.size());

		int position = 0;
		if (!cancel && type == 1) {
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
			case 4:
				return true;
			case 3:
			case 2:
			case 6:
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
			ping = (ServerPing) v1_7_R4.field.get(status);
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
			ping.setServerInfo(new ServerPingServerData(event.getVersion(), event.getProtocol()));
		if (event.getFalvicon() != null)
			ping.setFavicon(event.getFalvicon());
		return false;
	}

	@Override
	public void processPlayerInfo(Player player, Object channel, Object packet, Tablist tablist) {
		UUID id = ((GameProfile) Ref.get(packet, "d")).getId();
		if (id.equals(player.getUniqueId())) {
			if (tablist.isGameProfileModified())
				Ref.set(packet, "player", toGameProfile(tablist.getGameProfile()));
			if (tablist.getLatency().isPresent())
				Ref.set(packet, "ping", tablist.getLatency().get());
			if (tablist.getGameMode().isPresent())
				Ref.set(packet, "gamemode", tablist.getGameMode().get().ordinal());
			if (tablist.getPlayerListName().isPresent())
				Ref.set(packet, "username", tablist.getPlayerListName().get().toString());
		} else {
			TabEntry entry = tablist.getEntryById(id);
			if (entry == null)
				return;
			if (entry.isGameProfileModified())
				Ref.set(packet, "player", toGameProfile(entry.getGameProfile()));
			if (entry.getLatency().isPresent())
				Ref.set(packet, "ping", entry.getLatency().get());
			if (entry.getGameMode().isPresent())
				Ref.set(packet, "gamemode", entry.getGameMode().get().ordinal());
			if (entry.getPlayerListName().isPresent())
				Ref.set(packet, "username", entry.getPlayerListName().get().toString());
		}
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

	@SuppressWarnings("unchecked")
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
		return ((net.minecraft.server.v1_7_R4.Entity) entity).getDataWatcher();
	}

	@Override
	public int incrementStateId(Object container) {
		return 0;
	}

	@Override
	public Object packetEntityHeadRotation(Entity entity) {
		return new PacketPlayOutEntityHeadRotation((net.minecraft.server.v1_7_R4.Entity) getEntity(entity), (byte) (entity.getLocation().getYaw() * 256F / 360F));
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
		EntityPlayer entityPlayer = (EntityPlayer) getPlayer(player);
		switch (type) {
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

	private static Field username = Ref.field(PacketPlayOutPlayerInfo.class, "username");
	private static Field player = Ref.field(PacketPlayOutPlayerInfo.class, "player");
	private static Field gamemode = Ref.field(PacketPlayOutPlayerInfo.class, "gamemode");
	private static Field ping = Ref.field(PacketPlayOutPlayerInfo.class, "ping");
	private static Field action = Ref.field(PacketPlayOutPlayerInfo.class, "action");

	@Override
	public Object packetPlayerInfo(PlayerInfoType type, GameProfileHandler gameProfile, int latency, GameMode gameMode, Component playerName) {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();

		if (playerName == null)
			playerName = new Component(gameProfile.getUsername());

		if (gameMode == null)
			gameMode = GameMode.SURVIVAL;

		switch (type) {
		case ADD_PLAYER:
			Ref.set(packet, username, playerName.toString());
			Ref.set(packet, player, toGameProfile(gameProfile));
			Ref.set(packet, gamemode, net.minecraft.server.v1_7_R4.EnumGamemode.valueOf(gameMode.name()).getId());
			Ref.set(packet, ping, latency);
		case REMOVE_PLAYER:
			Ref.set(packet, action, 4);
			Ref.set(packet, username, playerName.toString());
			Ref.set(packet, player, toGameProfile(gameProfile));
		case UPDATE_DISPLAY_NAME:
			Ref.set(packet, action, 3);
			Ref.set(packet, username, playerName.toString());
			Ref.set(packet, player, toGameProfile(gameProfile));
		case UPDATE_GAME_MODE:
			Ref.set(packet, action, 3);
			Ref.set(packet, username, playerName.toString());
			Ref.set(packet, player, toGameProfile(gameProfile));
			Ref.set(packet, gamemode, net.minecraft.server.v1_7_R4.EnumGamemode.valueOf(gameMode.name()).getId());
		case UPDATE_LATENCY:
			Ref.set(packet, action, 3);
			Ref.set(packet, username, playerName.toString());
			Ref.set(packet, player, toGameProfile(gameProfile));
			Ref.set(packet, ping, latency);
		}
		return packet;
	}

	@Override
	public Object packetPosition(double x, double y, double z, float yaw, float pitch) {
		return new PacketPlayOutPosition(x, y, z, yaw, pitch, false);
	}

	@Override
	public Object packetRespawn(Player player) {
		EntityPlayer entityPlayer = (EntityPlayer) getPlayer(player);
		WorldServer worldserver = entityPlayer.r();
		byte actualDimension = (byte) worldserver.getWorld().getEnvironment().getId();
		return new PacketPlayOutRespawn((byte) (actualDimension >= 0 ? -1 : 0), worldserver.difficulty, worldserver.getWorldData().getType(), entityPlayer.playerInteractManager.getGameMode());
	}

	@Override
	public String getProviderName() {
		return "1_7_R4 (1.7.10)";
	}

	@Override
	public int getContainerStateId(Object container) {
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadParticles() {
		for (Entry<String, Object> entry : ((Map<String, Object>) Ref.getStatic(Ref.nms("", "PacketPlayOutWorldParticles$Particle"), "particleMap")).entrySet())
			me.devtec.theapi.bukkit.game.particles.Particle.identifier.put(entry.getKey().toUpperCase(), entry.getValue());
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