import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.chunkio.ChunkIOExecutor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Optional;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import io.netty.channel.Channel;
import me.devtec.shared.Pair;
import me.devtec.shared.Ref;
import me.devtec.shared.components.ClickEvent;
import me.devtec.shared.components.Component;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.components.ComponentEntity;
import me.devtec.shared.components.ComponentItem;
import me.devtec.shared.components.HoverEvent;
import me.devtec.shared.events.EventManager;
import me.devtec.shared.json.Json;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.events.ServerListPingEvent;
import me.devtec.theapi.bukkit.game.BlockDataStorage;
import me.devtec.theapi.bukkit.game.particles.Particle;
import me.devtec.theapi.bukkit.gui.AnvilGUI;
import me.devtec.theapi.bukkit.gui.GUI.ClickType;
import me.devtec.theapi.bukkit.gui.HolderGUI;
import me.devtec.theapi.bukkit.nms.GameProfileHandler;
import me.devtec.theapi.bukkit.nms.GameProfileHandler.PropertyHandler;
import me.devtec.theapi.bukkit.nms.NBTEdit;
import me.devtec.theapi.bukkit.nms.NmsProvider;
import me.devtec.theapi.bukkit.nms.utils.InventoryUtils;
import me.devtec.theapi.bukkit.nms.utils.InventoryUtils.DestinationType;
import net.minecraft.server.v1_12_R1.*;
import net.minecraft.server.v1_12_R1.ChatClickable.EnumClickAction;
import net.minecraft.server.v1_12_R1.ChatHoverable.EnumHoverAction;
import net.minecraft.server.v1_12_R1.Chunk.EnumTileEntityState;
import net.minecraft.server.v1_12_R1.IScoreboardCriteria.EnumScoreboardHealthDisplay;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_12_R1.PacketPlayOutScoreboardScore.EnumScoreboardAction;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_12_R1.ServerPing.ServerData;
import net.minecraft.server.v1_12_R1.ServerPing.ServerPingPlayerSample;

public class v1_12_R1 implements NmsProvider {
	private final MinecraftServer server = MinecraftServer.getServer();
	private static final Field pos = Ref.field(PacketPlayOutBlockChange.class, "a");
	private static final Field a = Ref.field(PacketPlayOutPlayerListHeaderFooter.class, "a");
	private static final Field b = Ref.field(PacketPlayOutPlayerListHeaderFooter.class, "b");
	private static final Field score_a = Ref.field(PacketPlayOutScoreboardScore.class, "a");
	private static final Field score_b = Ref.field(PacketPlayOutScoreboardScore.class, "b");
	private static final Field score_c = Ref.field(PacketPlayOutScoreboardScore.class, "c");
	private static final Field score_d = Ref.field(PacketPlayOutScoreboardScore.class, "d");
	private static final IChatBaseComponent empty = new ChatComponentText("");

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
		return ((net.minecraft.server.v1_12_R1.Entity) entity).getId();
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
		return ((net.minecraft.server.v1_12_R1.ItemStack) asNMSItem(itemStack)).getTag();
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
		net.minecraft.server.v1_12_R1.ItemStack i = (net.minecraft.server.v1_12_R1.ItemStack) asNMSItem(stack);
		i.setTag((NBTTagCompound) nbt);
		return asBukkitItem(i);
	}

	@Override
	public Object asNMSItem(ItemStack stack) {
		if (stack == null)
			return net.minecraft.server.v1_12_R1.ItemStack.a;
		return CraftItemStack.asNMSCopy(stack);
	}

	@Override
	public ItemStack asBukkitItem(Object stack) {
		return CraftItemStack.asBukkitCopy(stack == null ? net.minecraft.server.v1_12_R1.ItemStack.a
				: (net.minecraft.server.v1_12_R1.ItemStack) stack);
	}

	@Override
	public Object packetOpenWindow(int id, String legacy, int size, Component title) {
		return new PacketPlayOutOpenWindow(id, legacy, (IChatBaseComponent) this.toIChatBaseComponent(title), size);
	}

	@Override
	public int getContainerId(Object container) {
		return ((Container) container).windowId;
	}

	@Override
	public Object packetResourcePackSend(String url, String hash, boolean requireRP, Component prompt) {
		return new PacketPlayOutResourcePackSend(url, hash);
	}

	@Override
	public Object packetSetSlot(int container, int slot, int stateId, Object itemStack) {
		return new PacketPlayOutSetSlot(container, slot,
				(net.minecraft.server.v1_12_R1.ItemStack) (itemStack == null ? asNMSItem(null) : itemStack));
	}

	public Object packetSetSlot(int container, int slot, Object itemStack) {
		return this.packetSetSlot(container, slot, 0, itemStack);
	}

	@Override
	public Object packetEntityMetadata(int entityId, Object dataWatcher, boolean bal) {
		return new PacketPlayOutEntityMetadata(entityId, (net.minecraft.server.v1_12_R1.DataWatcher) dataWatcher, bal);
	}

	@Override
	public Object packetEntityDestroy(int... ids) {
		return new PacketPlayOutEntityDestroy(ids);
	}

	@Override
	public Object packetSpawnEntity(Object entity, int id) {
		return new PacketPlayOutSpawnEntity((net.minecraft.server.v1_12_R1.Entity) entity, id);
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
		PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
		try {
			v1_12_R1.a.set(packet, this.toIChatBaseComponent(header));
			v1_12_R1.b.set(packet, this.toIChatBaseComponent(footer));
		} catch (Exception ignored) {
		}
		return packet;
	}

	@Override
	public Object packetBlockChange(int x, int y, int z, Object iblockdata, int data) {
		PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange();
		packet.block = iblockdata == null ? Blocks.AIR.getBlockData() : (IBlockData) iblockdata;
		try {
			v1_12_R1.pos.set(packet, new BlockPosition(x, y, z));
		} catch (Exception ignored) {
		}
		return packet;
	}

	@Override
	public Object packetScoreboardObjective() {
		return new PacketPlayOutScoreboardObjective();
	}

	@Override
	public Object packetScoreboardDisplayObjective(int id, Object scoreboardObjective) {
		return new PacketPlayOutScoreboardDisplayObjective(id,
				scoreboardObjective == null ? null : (ScoreboardObjective) scoreboardObjective);
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
			v1_12_R1.score_a.set(packet, line);
			v1_12_R1.score_b.set(packet, player);
			v1_12_R1.score_c.set(packet, score);
			v1_12_R1.score_d.set(packet, EnumScoreboardAction.CHANGE);
		} catch (Exception ignored) {
		}
		return packet;
	}

	@Override
	public Object packetTitle(TitleAction action, Component text, int fadeIn, int stay, int fadeOut) {
		return new PacketPlayOutTitle(EnumTitleAction.valueOf(action.name()),
				(IChatBaseComponent) this.toIChatBaseComponent(text), fadeIn, stay, fadeOut);
	}

	@Override
	public Object packetChat(ChatType type, Object chatBase, UUID uuid) {
		return new PacketPlayOutChat((IChatBaseComponent) chatBase, ChatMessageType.valueOf(type.name()));
	}

	@Override
	public Object packetChat(ChatType type, Component text, UUID uuid) {
		return this.packetChat(type, this.toIChatBaseComponent(text), uuid);
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
		ChatComponentText current = new ChatComponentText(c.toString()); // legacy
		ChatModifier modif = current.getChatModifier();
		if (c.getColor() != null)
			modif.setColor(EnumChatFormat.valueOf(c.getColor().toUpperCase()));
		if (c.getClickEvent() != null)
			modif.setChatClickable(new ChatClickable(EnumClickAction.valueOf(c.getClickEvent().getAction().name()),
					c.getClickEvent().getValue()));
		if (c.getHoverEvent() != null)
			switch (c.getHoverEvent().getAction()) {
			case SHOW_ITEM:
			case SHOW_ENTITY:
				modif = modif.setChatHoverable(
						new ChatHoverable(EnumHoverAction.SHOW_ENTITY, IChatBaseComponent.ChatSerializer
								.b(Json.writer().simpleWrite(c.getHoverEvent().getValue().toJsonMap()))));
				break;
			default:
				modif = modif.setChatHoverable(new ChatHoverable(EnumHoverAction.SHOW_TEXT,
						(IChatBaseComponent) this.toIChatBaseComponent(c.getHoverEvent().getValue())));
				break;
			}
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
		if (co == null)
			return new IChatBaseComponent[] { empty };
		if (co instanceof ComponentItem || co instanceof ComponentEntity)
			return new IChatBaseComponent[] {
					IChatBaseComponent.ChatSerializer.b(Json.writer().simpleWrite(co.toJsonMap())) };
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
		if (co == null)
			return empty;
		if (co instanceof ComponentItem || co instanceof ComponentEntity)
			return IChatBaseComponent.ChatSerializer.b(Json.writer().simpleWrite(co.toJsonMap()));
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
		return main.a().isEmpty() ? empty : main;
	}

	@Override
	public Object toIChatBaseComponent(List<Component> cc) {
		ChatComponentText main = new ChatComponentText("");
		for (Component c : cc)
			main.a().add((IChatBaseComponent) this.toIChatBaseComponent(c));
		return main.a().isEmpty() ? empty : main;
	}

	@Override
	public Object chatBase(String json) {
		return IChatBaseComponent.ChatSerializer.a(json);
	}

	@Override
	public Component fromIChatBaseComponent(Object componentObject) {
		if (componentObject == null)
			return Component.EMPTY_COMPONENT;
		IChatBaseComponent component = (IChatBaseComponent) componentObject;
		if (component.getText().isEmpty()) {
			Component comp = new Component("");
			if (!component.a().isEmpty()) {
				List<Component> extra = new ArrayList<>();
				for (IChatBaseComponent base : component.a())
					extra.add(fromIChatBaseComponent(base));
				comp.setExtra(extra);
			}
			return comp;
		}
		Component comp = new Component(component.getText().replaceAll("§[A-Fa-f0-9K-Ok-oRr]", ""));
		ChatModifier modif = component.getChatModifier();
		if (modif.getColor() != null)
			comp.setColor(modif.getColor().name().toLowerCase());

		if (modif.h() != null)
			comp.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(modif.h().a().name()), modif.h().b()));

		if (modif.i() != null)
			switch (HoverEvent.Action.valueOf(modif.i().a().b().toUpperCase())) {
			case SHOW_ENTITY: {
				ComponentEntity compEntity = ComponentEntity.fromJson(modif.i().b().getText());
				if (compEntity != null)
					comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY,
							ComponentItem.fromJson(modif.i().b().getText())));
				break;
			}
			case SHOW_ITEM:
				ComponentItem compEntity = ComponentItem.fromJson(modif.i().b().getText());
				if (compEntity != null)
					comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
							ComponentItem.fromJson(modif.i().b().getText())));
				break;
			default:
				comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, fromIChatBaseComponent(modif.i().b())));
				break;
			}
		comp.setBold(modif.isBold());
		comp.setItalic(modif.isItalic());
		comp.setObfuscated(modif.isRandom());
		comp.setUnderlined(modif.isUnderlined());
		comp.setStrikethrough(modif.isStrikethrough());

		if (!component.a().isEmpty()) {
			List<Component> extra = new ArrayList<>();
			for (IChatBaseComponent base : component.a())
				extra.add(fromIChatBaseComponent(base));
			comp.setExtra(extra);
		}
		return comp;
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

	private static final Function<Entry<IBlockState<?>, Comparable<?>>, String> STATE_TO_VALUE = new Function<Entry<IBlockState<?>, Comparable<?>>, String>() {
		@Override
		public String apply(Entry<IBlockState<?>, Comparable<?>> var0) {
			if (var0 == null)
				return "<NULL>";
			IBlockState<?> var1 = var0.getKey();
			return var1.a() + "=" + a(var1, var0.getValue());
		}

		@SuppressWarnings("unchecked")
		private <T extends Comparable<T>> String a(IBlockState<T> var0, Comparable<?> var1) {
			return var0.a((T) var1);
		}
	};

	private String asString(IBlockData data) {
		StringBuilder stateString = new StringBuilder();
		if (!data.t().isEmpty()) {
			stateString.append('[');
			stateString.append(data.t().entrySet().stream().map(STATE_TO_VALUE).collect(Collectors.joining(",")));
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
		return writeData(ib, ib.getBlock().s(), material.getData());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static IBlockData writeData(IBlockData ib, BlockStateList blockStateList, String string) {
		if (string == null || string.trim().isEmpty())
			return ib;

		StringBuilder key = new StringBuilder();
		StringBuilder value = new StringBuilder();
		int set = 0;

		for (int i = 1; i < string.length() - 1; ++i) {
			char c = string.charAt(i);
			if (c == ',') {
				IBlockState ibj = blockStateList.a(key.toString());
				if (ibj != null) {
					Optional optional = ibj.b(value.toString());
					if (optional.isPresent())
						ib = ib.set(ibj, (Comparable) optional.get());
				}
				key = new StringBuilder();
				value = new StringBuilder();
				set = 0;
				continue;
			}
			if (c == '=') {
				set = 1;
				continue;
			}
			if (set == 0)
				key.append(c);
			else
				value.append(c);
		}
		if (set == 1) {
			IBlockState ibj = blockStateList.a(key.toString());
			if (ibj != null) {
				Optional optional = ibj.b(value.toString());
				if (optional.isPresent())
					ib = ib.set(ibj, (Comparable) optional.get());
			}
		}
		return ib;
	}

	@Override
	public ItemStack toItemStack(BlockDataStorage material) {
		Item item = CraftMagicNumbers.getItem(material.getType());
		ItemStack itemStack = CraftItemStack.asBukkitCopy(new net.minecraft.server.v1_12_R1.ItemStack(item));
		itemStack.getData().setData(material.getItemData());
		return itemStack;
	}

	private static final Field chunkLoader = Ref.field(ChunkProviderServer.class, "chunkLoader");

	@Override
	public Object getChunk(World world, int x, int z) {
		WorldServer sworld = ((CraftWorld) world).getHandle();
		net.minecraft.server.v1_12_R1.Chunk loaded = ((ChunkProviderServer) sworld.getChunkProvider())
				.getChunkIfLoaded(x, z);
		if (loaded == null)
			try {
				net.minecraft.server.v1_12_R1.Chunk chunk;
				chunk = ((IChunkLoader) Ref.get(sworld.getChunkProvider(), chunkLoader)).a(sworld, x, z);
				if (chunk != null) {
					chunk.setLastSaved(sworld.getTime());
					if (((ChunkProviderServer) sworld.getChunkProvider()).chunkGenerator != null)
						((ChunkProviderServer) sworld.getChunkProvider()).chunkGenerator.recreateStructures(chunk, x,
								z);
				}
				if (chunk != null) {
					((ChunkProviderServer) sworld.getChunkProvider()).chunks.put(ChunkCoordIntPair.a(x, z), chunk);
					postToMainThread(chunk::addEntities);
					loaded = chunk;
				}
			} catch (Exception ignored) {
			}
		if (loaded == null) { // generate new chunk
			ChunkRegionLoader loader = null;
			if (Ref.get(sworld.getChunkProvider(), chunkLoader) instanceof ChunkRegionLoader)
				loader = (ChunkRegionLoader) Ref.get(sworld.getChunkProvider(), chunkLoader);

			if (loader != null && loader.chunkExists(x, z))
				loaded = ChunkIOExecutor.syncChunkLoad(sworld, loader, (ChunkProviderServer) sworld.getChunkProvider(),
						x, z);
			else
				loaded = ((ChunkProviderServer) sworld.getChunkProvider()).originalGetChunkAt(x, z);
			if (loaded == null)
				loaded = ((ChunkProviderServer) sworld.getChunkProvider()).chunkGenerator.getOrCreateChunk(x, z);
			((ChunkProviderServer) sworld.getChunkProvider()).chunks.put(ChunkCoordIntPair.a(x, z), loaded);
		}
		return loaded;
	}

	@Override
	public void setBlock(Object objChunk, int x, int y, int z, Object IblockData, int data) {
		net.minecraft.server.v1_12_R1.Chunk chunk = (net.minecraft.server.v1_12_R1.Chunk) objChunk;
		if (y < 0)
			return;
		ChunkSection sc = chunk.getSections()[y >> 4];
		if (sc == null)
			return;
		BlockPosition pos = new BlockPosition(x, y, z);

		IBlockData iblock = IblockData == null ? Blocks.AIR.getBlockData() : (IBlockData) IblockData;

		boolean onlyModifyState = iblock.getBlock() instanceof ITileEntity;

		// REMOVE TILE ENTITY IF NOT SAME TYPE
		TileEntity ent = onlyModifyState ? chunk.tileEntities.get(pos) : chunk.tileEntities.remove(pos);
		if (ent != null) {
			boolean shouldSkip = true;
			if (!onlyModifyState) {
				shouldSkip = false;
				chunk.tileEntities.remove(pos);
			} else if (!ent.getBlock().getClass().equals(iblock.getBlock().getClass())) {
				shouldSkip = false;
				onlyModifyState = false;
			}
			if (!shouldSkip) {
				ent.z();
				chunk.world.capturedTileEntities.remove(pos);
				if (chunk.world.captureBlockStates)
					chunk.world.capturedBlockStates.removeIf(state -> state.getX() == pos.getX()
							&& state.getY() == pos.getY() && state.getZ() == pos.getZ());
			}
		}

		sc.setType(x & 15, y & 15, z & 15, iblock);

		// ADD TILE ENTITY
		if (iblock.getBlock() instanceof ITileEntity && !onlyModifyState) {
			ent = ((ITileEntity) iblock.getBlock()).a(chunk.world, 0);
			chunk.tileEntities.put(pos, ent);
			ent.a(chunk.world);
			ent.setPosition(pos);
			Ref.set(ent, "e", iblock.getBlock());
			Object packet = ent.getUpdatePacket();
			BukkitLoader.getPacketHandler().send(chunk.world.getWorld().getPlayers(), packet);
		}

		// MARK CHUNK TO SAVE
		chunk.mustSave = true;
	}

	@Override
	public void updatePhysics(Object objChunk, int x, int y, int z, Object iblockdata) {
		net.minecraft.server.v1_12_R1.Chunk chunk = (net.minecraft.server.v1_12_R1.Chunk) objChunk;

		BlockPosition blockPos = new BlockPosition(x, y, z);

		doPhysicsAround((WorldServer) chunk.world, blockPos, ((IBlockData) iblockdata).getBlock());
	}

	private void doPhysicsAround(WorldServer world, BlockPosition blockposition, Block block) {
		doPhysics(world, blockposition.west(), block, blockposition);
		doPhysics(world, blockposition.east(), block, blockposition);
		doPhysics(world, blockposition.down(), block, blockposition);
		doPhysics(world, blockposition.up(), block, blockposition);
		doPhysics(world, blockposition.north(), block, blockposition);
		doPhysics(world, blockposition.south(), block, blockposition);
	}

	private void doPhysics(WorldServer world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
		IBlockData state = world.getType(blockposition);
		state.doPhysics(world, blockposition, block, blockposition1);
		if (state.getBlock() instanceof BlockFalling)
			state.getBlock().onPlace(world, blockposition, block.getBlockData());
	}

	@Override
	public void updateLightAt(Object chunk, int x, int y, int z) {
		net.minecraft.server.v1_12_R1.Chunk c = (net.minecraft.server.v1_12_R1.Chunk) chunk;
		c.initLighting();
	}

	@Override
	public Object getBlock(Object chunk, int x, int y, int z) {
		net.minecraft.server.v1_12_R1.Chunk c = (net.minecraft.server.v1_12_R1.Chunk) chunk;
		if (y < 0)
			return Blocks.AIR.getBlockData();
		ChunkSection sc = c.getSections()[y >> 4];
		if (sc == null)
			return Blocks.AIR.getBlockData();
		return sc.getBlocks().a(x & 15, y & 15, z & 15);
	}

	@Override
	public byte getData(Object chunk, int x, int y, int z) {
		return 0;
	}

	@Override
	public String getNBTOfTile(Object objChunk, int x, int y, int z) {
		net.minecraft.server.v1_12_R1.Chunk chunk = (net.minecraft.server.v1_12_R1.Chunk) objChunk;
		return chunk.a(new BlockPosition(x, y, z), EnumTileEntityState.IMMEDIATE).save(new NBTTagCompound()).toString();
	}

	@Override
	public void setNBTToTile(Object objChunk, int x, int y, int z, String nbt) {
		net.minecraft.server.v1_12_R1.Chunk chunk = (net.minecraft.server.v1_12_R1.Chunk) objChunk;
		TileEntity ent = chunk.a(new BlockPosition(x, y, z), EnumTileEntityState.IMMEDIATE);
		NBTTagCompound parsedNbt = (NBTTagCompound) parseNBT(nbt);
		parsedNbt.setInt("x", x);
		parsedNbt.setInt("y", y);
		parsedNbt.setInt("z", z);
		ent.load(parsedNbt);
		Object packet = ent.getUpdatePacket();
		BukkitLoader.getPacketHandler().send(chunk.world.getWorld().getPlayers(), packet);
	}

	@Override
	public boolean isTileEntity(Object objChunk, int x, int y, int z) {
		net.minecraft.server.v1_12_R1.Chunk chunk = (net.minecraft.server.v1_12_R1.Chunk) objChunk;
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
		return ((net.minecraft.server.v1_12_R1.Chunk) nmsChunk).bukkitChunk;
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
			BukkitLoader.getPacketHandler().send(player,
					new PacketPlayOutCloseWindow(BukkitLoader.getNmsProvider().getContainerId(container)));
		EntityPlayer nmsPlayer = (EntityPlayer) getPlayer(player);
		nmsPlayer.activeContainer = nmsPlayer.defaultContainer;
		((Container) container).transferTo(nmsPlayer.activeContainer, (CraftPlayer) player);
	}

	@Override
	public void setSlot(Object container, int slot, Object item) {
		((Container) container).setItem(slot, (net.minecraft.server.v1_12_R1.ItemStack) item);
	}

	@Override
	public void setGUITitle(Player player, Object container, String legacy, int size, Component title) {
		int id = ((Container) container).windowId;
		BukkitLoader.getPacketHandler().send(player, packetOpenWindow(id, legacy, size, title));
		net.minecraft.server.v1_12_R1.ItemStack carried = ((CraftPlayer) player).getHandle().inventory.getCarried();
		if (!carried.isEmpty())
			BukkitLoader.getPacketHandler().send(player, new PacketPlayOutSetSlot(id, -1, carried));
		int slot = 0;
		for (net.minecraft.server.v1_12_R1.ItemStack item : ((Container) container).a()) {
			if (slot == size)
				break;
			if (!item.isEmpty())
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
	public void openAnvilGUI(Player player, Object container, Component title) {
		openGUI(player, container, "minecraft:anvil", 0, title);
	}

	@Override
	public Object createContainer(Inventory inv, Player player) {
		if (inv.getType() == InventoryType.ANVIL) {
			int id = ((CraftPlayer) player).getHandle().nextContainerCounter();
			ContainerAnvil container = new ContainerAnvil(((CraftPlayer) player).getHandle().inventory,
					((CraftWorld) player.getWorld()).getHandle(), zero, ((CraftPlayer) player).getHandle());
			container.windowId = id;
			postToMainThread(() -> {
				int slot = 0;
				for (ItemStack stack : inv.getContents())
					container.getSlot(slot++).set((net.minecraft.server.v1_12_R1.ItemStack) asNMSItem(stack));
			});
			container.checkReachable = false;
			return container;
		}
		return new CraftContainer(inv, ((CraftPlayer) player).getHandle(),
				((CraftPlayer) player).getHandle().nextContainerCounter());
	}

	@Override
	public Object getSlotItem(Object container, int slot) {
		return slot < 0 ? null : ((Container) container).getSlot(slot).getItem();
	}

	static final BlockPosition zero = new BlockPosition(0, 0, 0);

	public Object createAnvilContainer(Inventory inv, Player player) {
		int id = ((CraftPlayer) player).getHandle().nextContainerCounter();
		ContainerAnvil container = new ContainerAnvil(((CraftPlayer) player).getHandle().inventory,
				((CraftPlayer) player).getHandle().world, zero, ((CraftPlayer) player).getHandle());
		container.windowId = id;
		for (int i = 0; i < 2; ++i)
			container.setItem(i, (net.minecraft.server.v1_12_R1.ItemStack) asNMSItem(inv.getItem(i)));
		return container;
	}

	@Override
	public String getAnvilRenameText(Object anvil) {
		return ((ContainerAnvil) anvil).renameText;
	}

	@Override
	public boolean processInvClickPacket(Player player, HolderGUI gui, Object provPacket) {
		PacketPlayInWindowClick packet = (PacketPlayInWindowClick) provPacket;
		int slot = packet.b();

		Object container = gui.getContainer(player);
		if (container == null)
			return false;

		int id = packet.a();
		int mouseClick = packet.c();
		InventoryClickType type = packet.f();
		Container c = (Container) container;

		if (slot < -1 && slot != -999)
			return true;

		net.minecraft.server.v1_12_R1.EntityPlayer nPlayer = ((CraftPlayer) player).getHandle();

		ItemStack newItem;
		ItemStack oldItem;
		switch (type) {
		case PICKUP: // PICKUP
			oldItem = asBukkitItem(getSlotItem(container, slot));
			newItem = asBukkitItem(nPlayer.inventory.getCarried());
			if (slot > 0 && mouseClick != 0) {
				if (nPlayer.inventory.getCarried().isEmpty()) { // pickup half
					newItem = oldItem.clone();
					if (oldItem.getAmount() == 1)
						newItem = new ItemStack(Material.AIR);
					else
						newItem.setAmount(Math.max(1, oldItem.getAmount() / 2));
				} else
				// drop
				if (oldItem.isSimilar(newItem) || oldItem.getType() == Material.AIR)
					newItem.setAmount(oldItem.getType() == Material.AIR ? 1 : oldItem.getAmount() + 1);
			} else if (slot > 0) // drop
				if (oldItem.isSimilar(newItem))
					newItem.setAmount(Math.min(newItem.getAmount() + oldItem.getAmount(), newItem.getMaxStackSize()));
			break;
		case QUICK_MOVE: // QUICK_MOVE
			newItem = asBukkitItem(nPlayer.inventory.getCarried());
			oldItem = asBukkitItem(getSlotItem(container, slot));
			break;
		case SWAP:// SWAP
			newItem = asBukkitItem(nPlayer.inventory.getItem(mouseClick));
			oldItem = asBukkitItem(getSlotItem(container, slot));
			break;
		case CLONE:// CLONE
			newItem = asBukkitItem(getSlotItem(container, slot));
			oldItem = asBukkitItem(getSlotItem(container, slot));
			break;
		case THROW:// THROW
			if (nPlayer.inventory.getCarried().isEmpty() && slot >= 0) {
				Slot slot3 = c.getSlot(slot);
				newItem = asBukkitItem(slot3.getItem());
				if (mouseClick != 0 || newItem.getAmount() - 1 <= 0)
					newItem = new ItemStack(Material.AIR);
				else
					newItem.setAmount(newItem.getAmount() - 1);
			} else
				newItem = asBukkitItem(nPlayer.inventory.getCarried());
			oldItem = asBukkitItem(getSlotItem(container, slot));
			break;
		case QUICK_CRAFT:// QUICK_CRAFT
			newItem = asBukkitItem(nPlayer.inventory.getCarried());
			oldItem = slot <= -1 ? new ItemStack(Material.AIR) : asBukkitItem(getSlotItem(container, slot));
			break;
		case PICKUP_ALL:// PICKUP_ALL
			newItem = asBukkitItem(nPlayer.inventory.getCarried());
			oldItem = asBukkitItem(getSlotItem(container, slot));
			break;
		default:
			newItem = slot <= -1 ? new ItemStack(Material.AIR) : asBukkitItem(packet.e());
			oldItem = slot <= -1 ? new ItemStack(Material.AIR) : asBukkitItem(packet.e());
			break;
		}

		if (oldItem.getType() == Material.AIR && newItem.getType() == Material.AIR)
			return true;

		boolean cancel = false;
		int gameSlot = slot > gui.size() - 1 ? InventoryUtils.convertToPlayerInvSlot(slot - gui.size()) : slot;

		ClickType clickType = InventoryUtils.buildClick(
				type == InventoryClickType.QUICK_CRAFT ? 1 : type == InventoryClickType.QUICK_MOVE ? 2 : 0, mouseClick);
		if (slot > -1) {
			cancel = InventoryUtils.useItem(player, gui, slot, clickType);
			if (!gui.isInsertable())
				cancel = true;

			if (!cancel)
				cancel = gui.onInteractItem(player, newItem, oldItem, clickType, gameSlot, slot < gui.size());
			else
				gui.onInteractItem(player, newItem, oldItem, clickType, gameSlot, slot < gui.size());
		}
		if (!cancel) {
			if (gui instanceof AnvilGUI) { // Event
				final ItemStack newItemFinal = newItem;
				postToMainThread(() -> processEvent(c, type, gui, player, slot, gameSlot, newItemFinal, oldItem, packet,
						mouseClick, clickType, nPlayer, true));
			} else
				processEvent(c, type, gui, player, slot, gameSlot, newItem, oldItem, packet, mouseClick, clickType,
						nPlayer, false);
			return true;
		}
		// MOUSE
		BukkitLoader.getPacketHandler().send(player, packetSetSlot(-1, -1, 0, nPlayer.inventory.getCarried()));
		switch (type) {
		case CLONE:
			break;
		case SWAP:
		case QUICK_MOVE:
		case PICKUP_ALL:
			nPlayer.updateInventory(c);
			break;
		default:
			BukkitLoader.getPacketHandler().send(player, packetSetSlot(id, slot, 0, c.getSlot(slot).getItem()));
			break;
		}
		return true;
	}

	private void processEvent(Container c, InventoryClickType type, HolderGUI gui, Player player, int slot,
			int gameSlot, ItemStack newItem, ItemStack oldItem, PacketPlayInWindowClick packet, int mouseClick,
			ClickType clickType, net.minecraft.server.v1_12_R1.EntityHuman nPlayer, boolean isAnvilGui) {
		if (type == InventoryClickType.QUICK_MOVE) {
			ItemStack[] contents = slot < gui.size() ? player.getInventory().getStorageContents()
					: gui.getInventory().getStorageContents();
			boolean interactWithResultSlot = false;
			if (gui instanceof AnvilGUI && slot < gui.size() && slot == 2)
				if (c.getSlot(2).isAllowed(nPlayer))
					interactWithResultSlot = true;
				else
					return;
			Pair result = slot < gui.size()
					? InventoryUtils.shift(slot, player, gui, clickType,
							gui instanceof AnvilGUI && slot != 2 ? DestinationType.PLAYER_FROM_ANVIL
									: DestinationType.PLAYER,
							null, contents, oldItem)
					: InventoryUtils.shift(slot, player, gui, clickType, DestinationType.GUI,
							gui.getNotInterableSlots(player), contents, oldItem);
			@SuppressWarnings("unchecked")
			Map<Integer, ItemStack> modified = (Map<Integer, ItemStack>) result.getValue();
			int remaining = (int) result.getKey();

			if (!modified.isEmpty())
				if (slot < gui.size()) {
					for (Entry<Integer, ItemStack> modif : modified.entrySet())
						nPlayer.inventory.setItem(modif.getKey(),
								(net.minecraft.server.v1_12_R1.ItemStack) asNMSItem(modif.getValue()));
					if (remaining == 0) {
						c.getSlot(gameSlot).set((net.minecraft.server.v1_12_R1.ItemStack) asNMSItem(null));
						if (isAnvilGui)
							gui.getInventory().setItem(gameSlot, null);
						if (interactWithResultSlot) {
							c.getSlot(0).set((net.minecraft.server.v1_12_R1.ItemStack) asNMSItem(null));
							c.getSlot(1).set((net.minecraft.server.v1_12_R1.ItemStack) asNMSItem(null));
							if (isAnvilGui) {
								gui.getInventory().setItem(0, null);
								gui.getInventory().setItem(1, null);
							}
						}
					} else {
						newItem.setAmount(remaining);
						c.getSlot(gameSlot).set((net.minecraft.server.v1_12_R1.ItemStack) asNMSItem(newItem));
						if (isAnvilGui)
							gui.getInventory().setItem(gameSlot, newItem);
					}
				} else {
					for (Entry<Integer, ItemStack> modif : modified.entrySet())
						c.getSlot(modif.getKey())
								.set((net.minecraft.server.v1_12_R1.ItemStack) asNMSItem(modif.getValue())); // Visual
																												// &
																												// Nms
																												// side
					// Plugin & Bukkit side
					gui.getInventory().setStorageContents(contents);
					if (remaining == 0)
						nPlayer.inventory.setItem(gameSlot, (net.minecraft.server.v1_12_R1.ItemStack) asNMSItem(null));
					else {
						newItem.setAmount(remaining);
						nPlayer.inventory.setItem(gameSlot,
								(net.minecraft.server.v1_12_R1.ItemStack) asNMSItem(newItem));
					}
				}
			if (gui instanceof AnvilGUI) {
				int index = 0;
				for (int i = 0; i < 3; ++i) {
					gui.getInventory().setItem(index++, asBukkitItem(c.getSlot(i).getItem()));
					if (index == 3)
						break;
				}
			}
			nPlayer.inventory.setCarried(nPlayer.inventory.getCarried());
			return;
		}
		processClick(gui, gui.getNotInterableSlots(player), c, slot, mouseClick, type, nPlayer);
		if (gui instanceof AnvilGUI) {
			int index = 0;
			for (int i = 0; i < 3; ++i) {
				gui.getInventory().setItem(index++, asBukkitItem(c.getSlot(i).getItem()));
				if (index == 3)
					break;
			}
			nPlayer.inventory.setCarried(nPlayer.inventory.getCarried());
		}
	}

	private final Method onSwap = Ref.method(Slot.class, "b", int.class);
	private final Field quickcraftStatus = Ref.field(Container.class, "g");
	private final Field quickcraftTypeField = Ref.field(Container.class, "dragType");
	private final Field quickcraftSlotsField = Ref.field(Container.class, "h");
	private final Method resetQuickCraft = Ref.method(Container.class, "d");

	private void processClick(HolderGUI gui, List<Integer> ignoredSlots, Container container, int slotIndex, int button,
			InventoryClickType actionType, EntityHuman player) {
		PlayerInventory playerinventory = player.inventory;
		Slot slot;
		net.minecraft.server.v1_12_R1.ItemStack itemstack;
		if (actionType == InventoryClickType.QUICK_CRAFT)
			processDragMove(gui, ignoredSlots, container, player, slotIndex, button);
		else if ((int) Ref.get(container, quickcraftStatus) != 0)
			Ref.invoke(container, resetQuickCraft);
		else {
			int count;

			if ((actionType == InventoryClickType.PICKUP || actionType == InventoryClickType.QUICK_MOVE)
					&& (button == 0 || button == 1)) {
				boolean isPrimary = button == 0;

				if (slotIndex == -999) {
					if (!playerinventory.getCarried().isEmpty())
						if (isPrimary) {
							// CraftBukkit start
							net.minecraft.server.v1_12_R1.ItemStack carried = playerinventory.getCarried();
							playerinventory.setCarried(net.minecraft.server.v1_12_R1.ItemStack.a);
							postToMainThread(() -> player.drop(carried, true));
							// CraftBukkit start
						} else
							postToMainThread(() -> player.drop(playerinventory.getCarried().cloneAndSubtract(1), true));
				} else if (actionType == InventoryClickType.QUICK_MOVE) {
					if (slotIndex < 0)
						return;

					slot = container.slots.get(slotIndex);
					if (slot != null && slot.isAllowed(player))
						for (itemstack = container.shiftClick(player, slotIndex); !itemstack.isEmpty()
								&& net.minecraft.server.v1_12_R1.ItemStack.c(slot.getItem(),
										itemstack); itemstack = container.shiftClick(player, slotIndex))
							itemstack = itemstack.cloneItemStack();
				} else {
					if (slotIndex < 0)
						return;

					slot = container.slots.get(slotIndex);
					itemstack = slot.getItem();
					net.minecraft.server.v1_12_R1.ItemStack carried = playerinventory.getCarried();

					if (slot.isAllowed(itemstack))
						if (itemstack.isEmpty()) {
							if (!carried.isEmpty()) {
								count = isPrimary ? carried.getCount() : 1;
								slot.set(itemstack.cloneAndSubtract(count));
							}
						} else if (slot.isAllowed(player))
							if (carried.isEmpty()) {
								count = isPrimary ? itemstack.getCount() : (itemstack.getCount() + 1) / 2;
								playerinventory.setCarried(slot.a(count));
								if (itemstack.isEmpty())
									slot.set(net.minecraft.server.v1_12_R1.ItemStack.a);

								slot.a(player, playerinventory.getCarried());
							} else if (slot.isAllowed(carried)) {
								if (areSame(itemstack, carried)) {
									count = isPrimary ? carried.getCount() : 1;
									carried.subtract(count);
									itemstack.add(count);
								} else if (carried.getCount() <= slot.getMaxStackSize(carried)) {
									playerinventory.setCarried(itemstack);
									slot.set(carried);
								}
							} else if (carried.getMaxStackSize() > 1 && areSame(itemstack, carried)
									&& !itemstack.isEmpty()) {
								count = itemstack.getCount();
								if (count + carried.getCount() <= carried.getMaxStackSize()) {
									carried.add(count);
									itemstack = slot.a(count);
									if (itemstack.isEmpty())
										slot.set(net.minecraft.server.v1_12_R1.ItemStack.a);

									slot.a(player, playerinventory.getCarried());
								}
							}

					slot.f();
					// CraftBukkit start - Make sure the client has the right slot contents
					if (player instanceof EntityPlayer && slot.getMaxStackSize() != 64) {
						((EntityPlayer) player).playerConnection
								.sendPacket(new PacketPlayOutSetSlot(container.windowId, slot.index, slot.getItem()));
						// Updating a crafting inventory makes the client reset the result slot, have to
						// send it again
						if (container.getBukkitView().getType() == InventoryType.WORKBENCH
								|| container.getBukkitView().getType() == InventoryType.CRAFTING)
							((EntityPlayer) player).playerConnection.sendPacket(
									new PacketPlayOutSetSlot(container.windowId, 0, container.getSlot(0).getItem()));
					}
					// CraftBukkit end
				}
			} else {
				int maxStackSize;

				if (actionType == InventoryClickType.SWAP && (button >= 0 && button < 9 || button == 40)) {
					if (slotIndex < 0)
						return; // Paper - Add slot sanity checks to container clicks
					net.minecraft.server.v1_12_R1.ItemStack swapItem = playerinventory.getItem(button);

					slot = container.slots.get(slotIndex);
					itemstack = slot.getItem();
					if (!swapItem.isEmpty() || !itemstack.isEmpty())
						if (swapItem.isEmpty()) {
							if (slot.isAllowed(player)) {
								playerinventory.setItem(button, itemstack);
								Ref.invoke(slot, onSwap, itemstack.getCount());
								slot.set(net.minecraft.server.v1_12_R1.ItemStack.a);
								slot.a(player, itemstack);
							}
						} else if (itemstack.isEmpty()) {
							if (slot.isAllowed(swapItem)) {
								maxStackSize = slot.getMaxStackSize(swapItem);
								if (swapItem.getCount() > maxStackSize)
									slot.set(swapItem.cloneAndSubtract(maxStackSize));
								else {
									playerinventory.setItem(button, net.minecraft.server.v1_12_R1.ItemStack.a);
									slot.set(swapItem);
								}
							}
						} else if (slot.isAllowed(player) && slot.isAllowed(swapItem)) {
							maxStackSize = slot.getMaxStackSize(swapItem);
							if (swapItem.getCount() > maxStackSize) {
								slot.set(swapItem.cloneAndSubtract(maxStackSize));
								slot.a(player, itemstack);
								if (!playerinventory.pickup(itemstack)) {
									net.minecraft.server.v1_12_R1.ItemStack finalItemstack = itemstack;
									postToMainThread(() -> player.drop(finalItemstack, true));
								}
							} else {
								playerinventory.setItem(button, itemstack);
								slot.set(swapItem);
								slot.a(player, itemstack);
							}
						}
				} else if (actionType == InventoryClickType.CLONE && player.abilities.canInstantlyBuild
						&& playerinventory.getCarried().isEmpty() && slotIndex >= 0) {
					slot = container.slots.get(slotIndex);
					if (slot.hasItem()) {
						itemstack = slot.getItem().cloneItemStack();
						itemstack.setCount(itemstack.getMaxStackSize());
						playerinventory.setCarried(itemstack);
					}
				} else if (actionType == InventoryClickType.THROW && playerinventory.getCarried().isEmpty()
						&& slotIndex >= 0) {
					slot = container.slots.get(slotIndex);
					if (slot != null && slot.hasItem() && slot.isAllowed(player)) {
						count = button == 0 ? 1 : slot.getItem().getCount();
						itemstack = slot.a(count);
						slot.a(player, itemstack);
						net.minecraft.server.v1_12_R1.ItemStack finalItemstack = itemstack;
						postToMainThread(() -> player.drop(finalItemstack, true));
					}
				} else if (actionType == InventoryClickType.PICKUP_ALL && slotIndex >= 0) {
					slot = container.slots.get(slotIndex);
					itemstack = playerinventory.getCarried();
					if (!itemstack.isEmpty() && (!slot.hasItem() || !slot.isAllowed(player))) {
						int size = button == 0 ? 0 : container.slots.size() - 1;
						maxStackSize = button == 0 ? 1 : -1;
						List<Integer> ignoreSlots = ignoredSlots == null ? Collections.emptyList() : ignoredSlots;
						List<Integer> corruptedSlots = ignoredSlots == null ? Collections.emptyList()
								: new ArrayList<>();
						Map<Integer, ItemStack> modifiedSlots = new HashMap<>();
						Map<Integer, ItemStack> modifiedSlotsPlayerInv = new HashMap<>();

						for (count = 0; count < 2; ++count)
							for (int index = size; index >= 0 && index < container.slots.size()
									&& itemstack.getCount() < itemstack.getMaxStackSize(); index += maxStackSize) {
								Slot targetSlot = container.slots.get(index);

								if (targetSlot.hasItem() && Container.a(targetSlot, itemstack, true)
										&& targetSlot.isAllowed(player) && container.a(itemstack, targetSlot)) {
									net.minecraft.server.v1_12_R1.ItemStack targetItem = targetSlot.getItem();

									if (count != 0 || targetItem.getCount() != targetItem.getMaxStackSize()) {
										if (index < gui.size() && ignoreSlots.contains(index)) {
											corruptedSlots.add(index);
											continue;
										}
										int resultCount = Math.min(itemstack.getMaxStackSize() - itemstack.getCount(),
												targetItem.getCount());
										net.minecraft.server.v1_12_R1.ItemStack resultItem = targetSlot.a(resultCount);
										itemstack.add(resultCount);
										if (resultItem.isEmpty())
											targetSlot.set(net.minecraft.server.v1_12_R1.ItemStack.a);
										targetSlot.a(player, resultItem);
										int gameSlot = index > gui.size() - 1
												? InventoryUtils.convertToPlayerInvSlot(index - gui.size())
												: index;
										if (index < gui.size())
											modifiedSlots.put(gameSlot, asBukkitItem(targetSlot.getItem()));
										else
											modifiedSlotsPlayerInv.put(gameSlot, asBukkitItem(targetSlot.getItem()));
									}
								}
							}
						if (slotIndex < gui.size())
							modifiedSlots.put(slotIndex, new ItemStack(Material.AIR));
						else
							modifiedSlotsPlayerInv.put(InventoryUtils.convertToPlayerInvSlot(slotIndex - gui.size()),
									new ItemStack(Material.AIR));
						gui.onMultipleIteract((Player) player.getBukkitEntity(), modifiedSlots, modifiedSlotsPlayerInv);
						for (int s : corruptedSlots)
							BukkitLoader.getPacketHandler().send((Player) player.getBukkitEntity(),
									BukkitLoader.getNmsProvider().packetSetSlot(0, s, getContainerStateId(container),
											BukkitLoader.getNmsProvider().getSlotItem(container, s)));
					}
				}
			}
		}
	}

	private boolean areSame(net.minecraft.server.v1_12_R1.ItemStack itemstack1,
			net.minecraft.server.v1_12_R1.ItemStack itemstack2) {
		return (!itemstack2.usesData() || itemstack2.getData() == itemstack1.getData())
				&& net.minecraft.server.v1_12_R1.ItemStack.equals(itemstack2, itemstack1);
	}

	@SuppressWarnings("unchecked")
	private void processDragMove(HolderGUI gui, List<Integer> ignoredSlots, Container container, EntityHuman player,
			int slotIndex, int button) {
		PlayerInventory playerinventory = player.inventory;
		Slot slot;
		net.minecraft.server.v1_12_R1.ItemStack itemstack;
		int k;
		int count;
		int quickcraftType = (int) Ref.get(container, quickcraftTypeField);
		int quickcraftStat = (int) Ref.get(container, quickcraftStatus);
		int quickcraftStatNew = Container.c(button);
		Set<Slot> quickcraftSlots = (Set<Slot>) Ref.get(container, quickcraftSlotsField);
		Ref.set(container, quickcraftStatus, quickcraftStatNew);
		if ((quickcraftStat != 1 || quickcraftStatNew != 2) && quickcraftStat != quickcraftStatNew
				|| playerinventory.getCarried().isEmpty())
			Ref.invoke(container, resetQuickCraft);
		else
			switch (quickcraftStatNew) {
			case 0:
				if (Container.a(quickcraftStatNew, player)) {
					Ref.set(container, quickcraftStatus, 1);
					quickcraftSlots.clear();
				} else
					Ref.invoke(container, resetQuickCraft);
				break;
			case 1:
				if (slotIndex < 0)
					return; // Paper - Add slot sanity checks to container clicks
				slot = container.slots.get(slotIndex);
				itemstack = playerinventory.getCarried();
				if (Container.a(slot, itemstack, true) && slot.isAllowed(itemstack)
						&& (quickcraftType == 2 || itemstack.getCount() > quickcraftSlots.size()) && container.b(slot))
					quickcraftSlots.add(slot);
				break;
			case 2:
				if (!quickcraftSlots.isEmpty()) {
					if (quickcraftSlots.size() == 1) { // Paper - Fix CraftBukkit drag system
						k = quickcraftSlots.iterator().next().index;
						Ref.invoke(container, resetQuickCraft);
						processClick(gui, ignoredSlots, container, k, quickcraftType, InventoryClickType.PICKUP,
								player);
						return;
					}

					itemstack = playerinventory.getCarried().cloneItemStack();
					if (itemstack.isEmpty()) {
						Ref.invoke(container, resetQuickCraft);
						return;
					}

					count = playerinventory.getCarried().getCount();
					Map<Integer, net.minecraft.server.v1_12_R1.ItemStack> draggedSlots = new HashMap<>(); // CraftBukkit
																											// -
																											// Store
																											// slots
																											// from drag
																											// in
																											// map (raw
																											// slot
																											// id -> new
																											// stack)
					for (Slot slot1 : quickcraftSlots) {
						net.minecraft.server.v1_12_R1.ItemStack itemstack2 = playerinventory.getCarried();

						if (slot1 != null && Container.a(slot1, itemstack2, true) && slot1.isAllowed(itemstack2)
								&& (quickcraftType == 2 || itemstack2.getCount() >= quickcraftSlots.size())
								&& container.b(slot1)) {
							int j1 = slot1.hasItem() ? slot1.getItem().getCount() : 0;
							int k1 = Math.min(itemstack.getMaxStackSize(), slot1.getMaxStackSize(itemstack));
							int l1 = Math.min(getQuickCraftPlaceCount(quickcraftSlots, quickcraftType, itemstack) + j1,
									k1);

							count -= l1 - j1;
							// slot1.setByPlayer(itemstack1.copyWithCount(l1));
							net.minecraft.server.v1_12_R1.ItemStack clone = itemstack.cloneItemStack();
							clone.setCount(l1);
							draggedSlots.put(slot1.index, clone); // CraftBukkit - Put in map
																	// instead of setting
						}
					}

					// CraftBukkit start - InventoryDragEvent
					InventoryView view = container.getBukkitView();
					org.bukkit.inventory.ItemStack newcursor = CraftItemStack.asCraftMirror(itemstack);
					newcursor.setAmount(count);
					Map<Integer, org.bukkit.inventory.ItemStack> eventmap = new HashMap<>();
					for (Map.Entry<Integer, net.minecraft.server.v1_12_R1.ItemStack> ditem : draggedSlots.entrySet())
						eventmap.put(ditem.getKey(), CraftItemStack.asBukkitCopy(ditem.getValue()));

					// It's essential that we set the cursor to the new value here to prevent item
					// duplication if a plugin closes the inventory.
					playerinventory.setCarried(CraftItemStack.asNMSCopy(newcursor));

					boolean needsUpdate = false;

					final Map<Integer, org.bukkit.inventory.ItemStack> guiSlots = new HashMap<>();
					final Map<Integer, org.bukkit.inventory.ItemStack> playerSlots = new HashMap<>();
					for (final Entry<Integer, net.minecraft.server.v1_12_R1.ItemStack> ditem : draggedSlots.entrySet())
						if (ditem.getKey() < gui.size())
							guiSlots.put(ditem.getKey(), CraftItemStack.asBukkitCopy(ditem.getValue()));
						else {
							int finalSlot = ditem.getKey() - gui.size();
							if (finalSlot >= 27)
								finalSlot -= 27;
							else
								finalSlot += 9;
							playerSlots.put(finalSlot, CraftItemStack.asBukkitCopy(ditem.getValue()));
						}
					if (!guiSlots.isEmpty() || !playerSlots.isEmpty())
						gui.onMultipleIteract((Player) player.getBukkitEntity(), guiSlots, playerSlots);

					for (Map.Entry<Integer, net.minecraft.server.v1_12_R1.ItemStack> dslot : draggedSlots.entrySet())
						view.setItem(dslot.getKey(), CraftItemStack.asBukkitCopy(dslot.getValue()));
					// The only time the carried item will be set to null is if the inventory is
					// closed by the server.
					// If the inventory is closed by the server, then the cursor items are dropped.
					// This is why we change the cursor early.
					if (playerinventory.getCarried() != null) {
						playerinventory.setCarried(CraftItemStack.asNMSCopy(newcursor));
						needsUpdate = true;
					}

					if (needsUpdate && player instanceof EntityPlayer)
						((EntityPlayer) player).updateInventory(container);
				}
				Ref.invoke(container, resetQuickCraft);
				break;
			default:
				Ref.invoke(container, resetQuickCraft);
				break;
			}
	}

	public static int getQuickCraftPlaceCount(Set<Slot> slots, int mode,
			net.minecraft.server.v1_12_R1.ItemStack stack) {
		int j;
		switch (mode) {
		case 0:
			j = MathHelper.floor((float) stack.getCount() / (float) slots.size());
			break;
		case 1:
			j = 1;
			break;
		case 2:
			j = stack.getMaxStackSize();
			break;
		default:
			j = stack.getCount();
		}

		return j;
	}

	static final Field field = Ref.field(PacketStatusOutServerInfo.class, "b");

	@Override
	public boolean processServerListPing(String player, Object channel, Object packet) {
		PacketStatusOutServerInfo status = (PacketStatusOutServerInfo) packet;
		ServerPing ping;
		try {
			ping = (ServerPing) v1_12_R1.field.get(status);
		} catch (Exception e) {
			return false;
		}

		List<GameProfileHandler> players = new ArrayList<>();
		if (ping.b().c() != null)
			for (GameProfile profile : ping.b().c())
				players.add(fromGameProfile(profile));

		ServerListPingEvent event = new ServerListPingEvent(getOnlinePlayers().size(), Bukkit.getMaxPlayers(), players,
				Bukkit.getMotd(), ping.d(), ((InetSocketAddress) ((Channel) channel).remoteAddress()).getAddress(),
				ping.getServerData().a(), ping.getServerData().getProtocolVersion());
		EventManager.call(event);
		if (event.isCancelled())
			return true;
		ServerPingPlayerSample playerSample = new ServerPingPlayerSample(event.getMaxPlayers(),
				event.getOnlinePlayers());
		if (event.getSlots() != null) {
			GameProfile[] profiles = new GameProfile[event.getSlots().size()];
			int i = -1;
			for (GameProfileHandler s : event.getSlots())
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
		if (event.getFavicon() != null)
			ping.setFavicon(event.getFavicon());
		return false;
	}

	@Override
	public Object getNBT(Entity entity) {
		return ((CraftEntity) entity).getHandle().save(new NBTTagCompound());
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
		return ((net.minecraft.server.v1_12_R1.Entity) entity).getDataWatcher();
	}

	@Override
	public int incrementStateId(Object container) {
		return 0;
	}

	@Override
	public Object packetEntityHeadRotation(Entity entity) {
		return new PacketPlayOutEntityHeadRotation((net.minecraft.server.v1_12_R1.Entity) getEntity(entity),
				(byte) (entity.getLocation().getYaw() * 256F / 360F));
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

	private static final Field playerInfo = Ref.field(PacketPlayOutPlayerInfo.class, "b");

	private static final Constructor<?> infoData = Ref.constructor(
			Ref.nms("", "PacketPlayOutPlayerInfo$PlayerInfoData"), PacketPlayOutPlayerInfo.class, GameProfile.class,
			int.class, EnumGamemode.class, IChatBaseComponent.class);

	@SuppressWarnings("unchecked")
	@Override
	public Object packetPlayerInfo(PlayerInfoType type, GameProfileHandler gameProfile, int latency, GameMode gameMode,
			Component playerName) {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.valueOf(type.name()),
				Collections.emptyList());
		((List<Object>) Ref.get(packet, playerInfo)).add(Ref.newInstance(infoData, packet, toGameProfile(gameProfile),
				latency, gameMode == null ? EnumGamemode.SURVIVAL : EnumGamemode.valueOf(gameMode.name()),
				playerName == null ? toIChatBaseComponent(new Component(gameProfile.getUsername()))
						: toIChatBaseComponent(playerName)));
		return packet;
	}

	@Override
	public Object packetPosition(double x, double y, double z, float yaw, float pitch) {
		return new PacketPlayOutPosition(x, y, z, yaw, pitch, Collections.emptySet(), 0);
	}

	@Override
	public Object packetRespawn(Player player) {
		EntityPlayer entityPlayer = (EntityPlayer) getPlayer(player);
		WorldServer worldserver = entityPlayer.x();
		byte actualDimension = (byte) worldserver.getWorld().getEnvironment().getId();
		return new PacketPlayOutRespawn((byte) (actualDimension >= 0 ? -1 : 0), worldserver.getDifficulty(),
				worldserver.getWorldData().getType(), entityPlayer.playerInteractManager.getGameMode());
	}

	@Override
	public String getProviderName() {
		return "1_12_R1 (1.12)";
	}

	@Override
	public int getContainerStateId(Object container) {
		return 0;
	}

	@Override
	public void loadParticles() {
		for (EnumParticle s : EnumParticle.values())
			Particle.identifier.put(s.name(), s);
	}

	@Override
	public Object toGameProfile(GameProfileHandler gameProfileHandler) {
		GameProfile profile = new GameProfile(gameProfileHandler.getUUID(), gameProfileHandler.getUsername());
		for (Entry<String, PropertyHandler> entry : gameProfileHandler.getProperties().entrySet())
			profile.getProperties().put(entry.getKey(), new Property(entry.getValue().getName(),
					entry.getValue().getValues(), entry.getValue().getSignature()));
		return profile;
	}

	@Override
	public GameProfileHandler fromGameProfile(Object gameProfile) {
		GameProfile profile = (GameProfile) gameProfile;
		GameProfileHandler handler = GameProfileHandler.of(profile.getName(), profile.getId());
		for (Entry<String, Property> entry : profile.getProperties().entries())
			handler.getProperties().put(entry.getKey(), PropertyHandler.of(entry.getValue().getName(),
					entry.getValue().getValue(), entry.getValue().getSignature()));
		return handler;
	}

	@Override
	public Object getGameProfile(Object nmsPlayer) {
		return ((EntityPlayer) nmsPlayer).getProfile();
	}
}
