import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_20_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R2.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R2.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

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
import me.devtec.shared.utility.ParseUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.events.ServerListPingEvent;
import me.devtec.theapi.bukkit.game.BlockDataStorage;
import me.devtec.theapi.bukkit.gui.AnvilGUI;
import me.devtec.theapi.bukkit.gui.GUI.ClickType;
import me.devtec.theapi.bukkit.gui.HolderGUI;
import me.devtec.theapi.bukkit.nms.GameProfileHandler;
import me.devtec.theapi.bukkit.nms.GameProfileHandler.PropertyHandler;
import me.devtec.theapi.bukkit.nms.NBTEdit;
import me.devtec.theapi.bukkit.nms.NmsProvider;
import me.devtec.theapi.bukkit.nms.utils.InventoryUtils;
import me.devtec.theapi.bukkit.nms.utils.InventoryUtils.DestinationType;
import me.devtec.theapi.bukkit.packetlistener.PacketContainer;
import me.devtec.theapi.bukkit.xseries.XMaterial;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.HoverEvent.EntityTooltipInfo;
import net.minecraft.network.chat.HoverEvent.ItemStackInfo;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.common.ClientboundResourcePackPacket;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ServerStatus.Favicon;
import net.minecraft.network.protocol.status.ServerStatus.Players;
import net.minecraft.network.protocol.status.ServerStatus.Version;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunk.EntityCreationType;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class v1_20_2 implements NmsProvider {
	private static final MinecraftServer server = MinecraftServer.getServer();
	private static final net.minecraft.network.chat.Component empty = net.minecraft.network.chat.Component.literal("");

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
		return ((CraftChunk) chunk).getHandle(ChunkStatus.FULL);
	}

	@Override
	public int getEntityId(Object entity) {
		return ((net.minecraft.world.entity.Entity) entity).getId();
	}

	@Override
	public Object getScoreboardAction(Action type) {
		return type == Action.CHANGE ? ClientboundSetPlayerTeamPacket.Action.ADD
				: ClientboundSetPlayerTeamPacket.Action.REMOVE;
	}

	@Override
	public Object getEnumScoreboardHealthDisplay(DisplayType type) {
		return type == DisplayType.INTEGER ? ObjectiveCriteria.RenderType.INTEGER : ObjectiveCriteria.RenderType.HEARTS;
	}

	@Override
	public Object getNBT(ItemStack itemStack) {
		net.minecraft.world.item.ItemStack item = (net.minecraft.world.item.ItemStack) asNMSItem(itemStack);
		if (item.isEmpty())
			return new CompoundTag();
		CompoundTag nbt = item.getTag();
		if (nbt != null)
			return nbt.copy();
		return null;
	}

	@Override
	public Object parseNBT(String json) {
		if (json == null)
			return new CompoundTag();
		try {
			return TagParser.parseTag(json);
		} catch (Exception e) {
			return new CompoundTag();
		}
	}

	@Override
	public ItemStack setNBT(ItemStack stack, Object nbt) {
		if (nbt instanceof NBTEdit)
			nbt = ((NBTEdit) nbt).getNBT();
		net.minecraft.world.item.ItemStack item = (net.minecraft.world.item.ItemStack) asNMSItem(stack);
		item.setTag((CompoundTag) nbt);
		return asBukkitItem(item);
	}

	@Override
	public Object asNMSItem(ItemStack stack) {
		if (stack == null)
			return net.minecraft.world.item.ItemStack.EMPTY;
		return CraftItemStack.asNMSCopy(stack);
	}

	@Override
	public ItemStack asBukkitItem(Object stack) {
		return CraftItemStack.asBukkitCopy(
				stack == null ? net.minecraft.world.item.ItemStack.EMPTY : (net.minecraft.world.item.ItemStack) stack);
	}

	@Override
	public int getContainerId(Object container) {
		return ((AbstractContainerMenu) container).containerId;
	}

	@Override
	public Object packetResourcePackSend(String url, String hash, boolean requireRP, Component prompt) {
		return new ClientboundResourcePackPacket(url, hash, requireRP,
				(net.minecraft.network.chat.Component) this.toIChatBaseComponent(prompt));
	}

	@Override
	public Object packetSetSlot(int container, int slot, int changeId, Object itemStack) {
		return new ClientboundContainerSetSlotPacket(container, changeId, slot,
				(net.minecraft.world.item.ItemStack) (itemStack == null ? asNMSItem(null) : itemStack));
	}

	private Method packAll = Ref.method(SynchedEntityData.class, "packAll");

	@SuppressWarnings("unchecked")
	@Override
	public Object packetEntityMetadata(int entityId, Object dataWatcher, boolean bal) {
		return new ClientboundSetEntityDataPacket(entityId, (List<DataValue<?>>) Ref.invoke(dataWatcher, packAll));
	}

	@Override
	public Object packetEntityDestroy(int... ids) {
		return new ClientboundRemoveEntitiesPacket(ids);
	}

	@Override
	public Object packetSpawnEntity(Object entity, int id) {
		return new ClientboundAddEntityPacket((net.minecraft.world.entity.Entity) entity, id,
				((net.minecraft.world.entity.Entity) entity).blockPosition());
	}

	@Override
	public Object packetNamedEntitySpawn(Object player) {
		return new ClientboundAddEntityPacket((net.minecraft.world.entity.player.Player) player, 0,
				((net.minecraft.world.entity.Entity) player).blockPosition());
	}

	@Override
	public Object packetSpawnEntityLiving(Object entityLiving) {
		return new ClientboundAddEntityPacket((net.minecraft.world.entity.LivingEntity) entityLiving, 0,
				((net.minecraft.world.entity.LivingEntity) entityLiving).blockPosition());
	}

	@Override
	public Object packetPlayerListHeaderFooter(Component header, Component footer) {
		return new ClientboundTabListPacket((net.minecraft.network.chat.Component) toIChatBaseComponent(header),
				(net.minecraft.network.chat.Component) this.toIChatBaseComponent(footer));
	}

	@Override
	public Object packetBlockChange(int x, int y, int z, Object iblockdata, int data) {
		return new ClientboundBlockUpdatePacket(new BlockPos(x, y, z),
				iblockdata == null ? Blocks.AIR.defaultBlockState()
						: (net.minecraft.world.level.block.state.BlockState) iblockdata);
	}

	@Override
	public Object packetScoreboardObjective() {
		return Ref.newUnsafeInstance(ClientboundSetObjectivePacket.class);
	}

	@Override
	public Object packetScoreboardDisplayObjective(int id, Object scoreboardObjective) {
		return new ClientboundSetDisplayObjectivePacket(DisplaySlot.values()[id],
				scoreboardObjective == null ? null : (Objective) scoreboardObjective);
	}

	@Override
	public Object packetScoreboardTeam() {
		return Ref.newUnsafeInstance(ClientboundSetPlayerTeamPacket.class);
	}

	@Override
	public Object packetScoreboardScore(Action action, String player, String line, int score) {
		switch (action) {
		case CHANGE:
			return new ClientboundSetScorePacket(net.minecraft.server.ServerScoreboard.Method.CHANGE, line, player,
					score);
		case REMOVE:
			return new ClientboundSetScorePacket(net.minecraft.server.ServerScoreboard.Method.REMOVE, line, player,
					score);
		}
		return null;
	}

	@Override
	public Object packetTitle(TitleAction action, Component text, int fadeIn, int stay, int fadeOut) {
		return switch (action) {
		case ACTIONBAR -> new ClientboundSetActionBarTextPacket(
				(net.minecraft.network.chat.Component) this.toIChatBaseComponent(text));
		case TITLE ->
			new ClientboundSetTitleTextPacket((net.minecraft.network.chat.Component) this.toIChatBaseComponent(text));
		case SUBTITLE -> new ClientboundSetSubtitleTextPacket(
				(net.minecraft.network.chat.Component) this.toIChatBaseComponent(text));
		case TIMES -> new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut);
		case CLEAR, RESET -> new ClientboundClearTitlesPacket(true);
		};
	}

	@Override
	public Object packetChat(ChatType type, Object chatBase, UUID uuid) {
		return new ClientboundSystemChatPacket((net.minecraft.network.chat.Component) chatBase, false);
	}

	@Override
	public Object packetChat(ChatType type, Component text, UUID uuid) {
		return new ClientboundSystemChatPacket((net.minecraft.network.chat.Component) this.toIChatBaseComponent(text),
				false);
	}

	@Override
	public void postToMainThread(Runnable runnable) {
		v1_20_2.server.execute(runnable);
	}

	@Override
	public Object getMinecraftServer() {
		return v1_20_2.server;
	}

	@Override
	public Thread getServerThread() {
		return v1_20_2.server.serverThread;
	}

	@Override
	public double[] getServerTPS() {
		return v1_20_2.server.recentTps;
	}

	private net.minecraft.network.chat.Component convert(Component c) {
		if (c instanceof ComponentItem || c instanceof ComponentEntity)
			return net.minecraft.network.chat.Component.Serializer.fromJson(Json.writer().simpleWrite(c.toJsonMap()));
		MutableComponent current = net.minecraft.network.chat.Component.literal(c.getText());
		Style modif = current.getStyle();
		if (c.getColor() != null && !c.getColor().isEmpty())
			if (c.getColor().startsWith("#"))
				modif = modif.withColor(TextColor.fromRgb(Integer.decode(c.getColor())));
			else
				modif = modif.withColor(ChatFormatting.getByCode(c.colorToChar()));
		if (c.getClickEvent() != null)
			modif = modif.withClickEvent(new net.minecraft.network.chat.ClickEvent(
					net.minecraft.network.chat.ClickEvent.Action.valueOf(c.getClickEvent().getAction().name()),
					c.getClickEvent().getValue()));
		if (c.getHoverEvent() != null)
			switch (c.getHoverEvent().getAction()) {
			case SHOW_ENTITY:
				try {
					ComponentEntity compoundTag = (ComponentEntity) c.getHoverEvent().getValue();
					net.minecraft.network.chat.Component component = compoundTag.getName() == null ? null
							: (net.minecraft.network.chat.Component) toIChatBaseComponent(compoundTag.getName());
					Object type = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.tryParse(compoundTag.getType()));
					@SuppressWarnings("unchecked")
					EntityType<?> entityType = type instanceof EntityType ? (EntityType<?>) type
							: ((Optional<Reference<EntityType<?>>>) type).get().value();
					modif = modif.withHoverEvent(new net.minecraft.network.chat.HoverEvent(
							net.minecraft.network.chat.HoverEvent.Action.SHOW_ENTITY,
							new net.minecraft.network.chat.HoverEvent.EntityTooltipInfo(entityType, compoundTag.getId(),
									component)));
				} catch (Exception ignored) {
				}
				break;
			case SHOW_ITEM:
				try {

					ComponentItem compoundTag = (ComponentItem) c.getHoverEvent().getValue();
					net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(
							CraftMagicNumbers.getItem(XMaterial.matchXMaterial(compoundTag.getId())
									.orElse(XMaterial.AIR).parseMaterial()),
							compoundTag.getCount());
					if (compoundTag.getNbt() != null) {
						CompoundTag nbt = (CompoundTag) parseNBT(compoundTag.getNbt());
						if (!nbt.contains("id"))
							nbt.putString("id",
									BuiltInRegistries.ITEM.getKey(CraftMagicNumbers.getItem(XMaterial
											.matchXMaterial(compoundTag.getId()).orElse(XMaterial.AIR).parseMaterial()))
											.toString());
						if (!nbt.contains("count"))
							nbt.putInt("count", compoundTag.getCount());
						stack = net.minecraft.world.item.ItemStack.of(nbt);
					}
					modif = modif.withHoverEvent(new net.minecraft.network.chat.HoverEvent(
							net.minecraft.network.chat.HoverEvent.Action.SHOW_ITEM,
							new net.minecraft.network.chat.HoverEvent.ItemStackInfo(stack)));
				} catch (Exception ignored) {
				}
				break;
			default:
				modif = modif.withHoverEvent(new net.minecraft.network.chat.HoverEvent(
						net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
						(net.minecraft.network.chat.Component) this
								.toIChatBaseComponent(c.getHoverEvent().getValue())));
				break;
			}
		modif = modif.withBold(c.isBold());
		modif = modif.withItalic(c.isItalic());
		modif = modif.withObfuscated(c.isObfuscated());
		modif = modif.withUnderlined(c.isUnderlined());
		modif = modif.withStrikethrough(c.isStrikethrough());
		current.setStyle(modif);
		return current;
	}

	@Override
	public Object[] toIChatBaseComponents(List<Component> components) {
		List<net.minecraft.network.chat.Component> chat = new ArrayList<>();
		chat.add(net.minecraft.network.chat.Component.literal(""));
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
		return chat.toArray(new net.minecraft.network.chat.Component[0]);
	}

	private void addConverted(List<net.minecraft.network.chat.Component> chat, List<Component> extra) {
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
			return new net.minecraft.network.chat.Component[] { empty };
		if (co instanceof ComponentItem || co instanceof ComponentEntity)
			return new net.minecraft.network.chat.Component[] { net.minecraft.network.chat.Component.Serializer
					.fromJson(Json.writer().simpleWrite(co.toJsonMap())) };
		List<net.minecraft.network.chat.Component> chat = new ArrayList<>();
		chat.add(net.minecraft.network.chat.Component.literal(""));
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
		return chat.toArray(new net.minecraft.network.chat.Component[0]);
	}

	@Override
	public Object toIChatBaseComponent(Component co) {
		if (co == null)
			return empty;
		if (co instanceof ComponentItem || co instanceof ComponentEntity)
			return net.minecraft.network.chat.Component.Serializer.fromJson(Json.writer().simpleWrite(co.toJsonMap()));
		MutableComponent main = net.minecraft.network.chat.Component.literal("");
		List<net.minecraft.network.chat.Component> chat = new ArrayList<>();
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
		main.getSiblings().addAll(chat);
		return main.getSiblings().isEmpty() ? empty : main;
	}

	@Override
	public Object toIChatBaseComponent(List<Component> cc) {
		MutableComponent main = net.minecraft.network.chat.Component.literal("");
		for (Component c : cc)
			main.getSiblings().add((net.minecraft.network.chat.Component) this.toIChatBaseComponent(c));
		return main.getSiblings().isEmpty() ? empty : main;
	}

	@Override
	public Object chatBase(String json) {
		return net.minecraft.network.chat.Component.Serializer.fromJson(json);
	}

	@Override
	public Component fromIChatBaseComponent(Object componentObject) {
		if (!(componentObject instanceof net.minecraft.network.chat.Component component))
			return Component.EMPTY_COMPONENT;
		Object result = Ref.invoke(component.getContents(), "text");
		Component comp = new Component(result == null ? "" : (String) result);
		Style modif = component.getStyle();
		if (modif.getColor() != null)
			comp.setColor(modif.getColor().serialize());

		if (modif.getClickEvent() != null)
			comp.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(modif.getClickEvent().getAction().name()),
					modif.getClickEvent().getValue()));

		if (modif.getHoverEvent() != null)
			switch (HoverEvent.Action.valueOf(modif.getHoverEvent().getAction().getName().toUpperCase())) {
			case SHOW_ENTITY: {
				EntityTooltipInfo hover = modif.getHoverEvent()
						.getValue(net.minecraft.network.chat.HoverEvent.Action.SHOW_ENTITY);
				ComponentEntity compEntity = new ComponentEntity(hover.type.toString(), hover.id);
				if (hover.name != null)
					compEntity.setName(fromIChatBaseComponent(hover.name));
				comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, compEntity));
				break;
			}
			case SHOW_ITEM: {
				ItemStackInfo hover = modif.getHoverEvent()
						.getValue(net.minecraft.network.chat.HoverEvent.Action.SHOW_ITEM);
				ComponentItem compEntity = new ComponentItem(
						CraftMagicNumbers.getMaterial(hover.getItemStack().getItem()).name(),
						hover.getItemStack().getCount());
				compEntity.setNbt(hover.getItemStack().save(new CompoundTag()).toString());
				comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, compEntity));
				break;
			}
			default:
				net.minecraft.network.chat.Component hover = modif.getHoverEvent()
						.getValue(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT);
				comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, fromIChatBaseComponent(hover)));
				break;
			}
		comp.setBold(modif.isBold());
		comp.setItalic(modif.isItalic());
		comp.setObfuscated(modif.isObfuscated());
		comp.setUnderlined(modif.isUnderlined());
		comp.setStrikethrough(modif.isStrikethrough());

		if (!component.getSiblings().isEmpty()) {
			List<Component> extra = new ArrayList<>();
			for (net.minecraft.network.chat.Component base : component.getSiblings())
				extra.add(fromIChatBaseComponent(base));
			comp.setExtra(extra);
		}
		return comp;
	}

	@Override
	public BlockDataStorage toMaterial(Object blockOrIBlockData) {
		if (blockOrIBlockData instanceof Block block) {
			net.minecraft.world.level.block.state.BlockState data = block.defaultBlockState();
			return new BlockDataStorage(CraftMagicNumbers.getMaterial(data.getBlock()), (byte) 0, asString(data));
		}
		if (blockOrIBlockData instanceof net.minecraft.world.level.block.state.BlockState data)
			return new BlockDataStorage(CraftMagicNumbers.getMaterial(data.getBlock()), (byte) 0, asString(data));
		return new BlockDataStorage(Material.AIR);
	}

	private String asString(net.minecraft.world.level.block.state.BlockState data) {
		StringBuilder stateString = new StringBuilder();
		if (!data.getProperties().isEmpty()) {
			stateString.append('[');
			stateString.append(data.getValues().entrySet().stream().map(StateHolder.PROPERTY_ENTRY_TO_STRING_FUNCTION)
					.collect(Collectors.joining(",")));
			stateString.append(']');
		}
		return stateString.toString();
	}

	@Override
	public Object toIBlockData(BlockDataStorage material) {
		if (material == null || material.getType() == null || material.getType() == Material.AIR)
			return Blocks.AIR.defaultBlockState();
		Block block = CraftMagicNumbers.getBlock(material.getType());
		return readArgument(block, material);
	}

	@Override
	public Object toBlock(BlockDataStorage material) {
		if (material == null || material.getType() == null || material.getType() == Material.AIR)
			return Blocks.AIR;
		Block block = CraftMagicNumbers.getBlock(material.getType());
		return readArgument(block, material).getBlock();
	}

	private net.minecraft.world.level.block.state.BlockState readArgument(Block block, BlockDataStorage material) {
		net.minecraft.world.level.block.state.BlockState ib = block.defaultBlockState();
		return writeData(ib, ib.getBlock().getStateDefinition(), material.getData());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static net.minecraft.world.level.block.state.BlockState writeData(
			net.minecraft.world.level.block.state.BlockState ib, StateDefinition blockStateList, String string) {
		if (string == null || string.trim().isEmpty())
			return ib;

		StringBuilder key = new StringBuilder();
		StringBuilder value = new StringBuilder();
		int set = 0;

		for (int i = 1; i < string.length() - 1; ++i) {
			char c = string.charAt(i);
			if (c == ',') {
				net.minecraft.world.level.block.state.properties.Property ibj = blockStateList
						.getProperty(key.toString());
				if (ibj != null) {
					Optional optional = ibj.getValue(value.toString());
					if (optional.isPresent())
						ib = ib.setValue(ibj, (Comparable) optional.get());
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
			net.minecraft.world.level.block.state.properties.Property ibj = blockStateList.getProperty(key.toString());
			if (ibj != null) {
				Optional optional = ibj.getValue(value.toString());
				if (optional.isPresent())
					ib = ib.setValue(ibj, (Comparable) optional.get());
			}
		}
		return ib;
	}

	@Override
	public ItemStack toItemStack(BlockDataStorage material) {
		Item item = CraftMagicNumbers.getItem(material.getType(), ParseUtils.getShort(material.getData()));
		return CraftItemStack.asBukkitCopy(item.getDefaultInstance());
	}

	@Override
	public Object getChunk(World world, int x, int z) {
		return ((CraftChunk) world.getChunkAt(x, z)).getHandle(ChunkStatus.FULL);
	}

	static Method markUnsaved = Ref.method(ChunkAccess.class, "setUnsaved", boolean.class);
	static Method getMaxSection = Ref.method(LevelHeightAccessor.class, "getMaxSection");
	static boolean modernPaper = false, maxY = false;
	static {
		if (markUnsaved == null) {
			modernPaper = true;
			markUnsaved = Ref.method(ChunkAccess.class, "markUnsaved");
		}
		if (getMaxSection == null) {
			maxY = true;
			getMaxSection = Ref.method(LevelHeightAccessor.class, "getMaxY");
		}
	}

	@Override
	public void setBlock(Object objChunk, int x, int y, int z, Object IblockData, int data) {
		LevelChunk chunk = (LevelChunk) objChunk;
		ServerLevel world = chunk.level;
		int highY = chunk.getSectionIndex(y);
		if (highY < 0 || highY > (maxY ? (int) Ref.invoke(chunk, getMaxSection) >> 4
				: (int) Ref.invoke(chunk, getMaxSection)))
			return;
		LevelChunkSection sc = chunk.getSection(highY);
		BlockPos pos = new BlockPos(x, y, z);

		net.minecraft.world.level.block.state.BlockState iblock = IblockData == null ? Blocks.AIR.defaultBlockState()
				: (net.minecraft.world.level.block.state.BlockState) IblockData;
		if (sc.hasOnlyAir() && iblock.isAir())
			return;

		boolean onlyModifyState = iblock.getBlock() instanceof EntityBlock;

		// REMOVE TILE ENTITY IF NOT SAME TYPE
		BlockEntity ent = chunk.blockEntities.get(pos);
		if (ent != null) {
			boolean shouldSkip = true;
			if (!onlyModifyState)
				shouldSkip = false;
			else if (!ent.getType().isValid(iblock)) {
				shouldSkip = false;
				onlyModifyState = false;
			}
			if (!shouldSkip)
				chunk.removeBlockEntity(pos);
		}

		net.minecraft.world.level.block.state.BlockState old = sc.setBlockState(x & 15, y & 15, z & 15, iblock, false);

		if (!old.equals(iblock)) {
			// ADD TILE ENTITY
			if (iblock.getBlock() instanceof EntityBlock && !onlyModifyState) {
				ent = ((EntityBlock) iblock.getBlock()).newBlockEntity(pos, iblock);
				chunk.blockEntities.put(pos, ent);
				ent.setLevel(world);
				Object packet = ent.getUpdatePacket();
				BukkitLoader.getPacketHandler().send(chunk.level.getWorld().getPlayers(), packet);
			}

			// MARK CHUNK TO SAVE
			if (modernPaper)
				Ref.invoke(chunk, markUnsaved);
			else
				Ref.invoke(chunk, markUnsaved, true);

			// POI
			if (!world.preventPoiUpdated)
				world.onBlockStateChange(pos, old, iblock);
		}
	}

	@Override
	public void updatePhysics(Object objChunk, int x, int y, int z, Object iblockdata) {
		LevelChunk chunk = (LevelChunk) objChunk;
		BlockPos blockPos = new BlockPos(x, y, z);
		doPhysicsAround(chunk.level, blockPos,
				((net.minecraft.world.level.block.state.BlockState) iblockdata).getBlock());
	}

	private void doPhysicsAround(ServerLevel world, BlockPos BlockPos, Block block) {
		doPhysics(world, BlockPos.west(), block, BlockPos); // west
		doPhysics(world, BlockPos.east(), block, BlockPos); // east
		doPhysics(world, BlockPos.below(), block, BlockPos); // down
		doPhysics(world, BlockPos.above(), block, BlockPos); // up
		doPhysics(world, BlockPos.north(), block, BlockPos); // north
		doPhysics(world, BlockPos.south(), block, BlockPos); // south
	}

	private static final Method callPhysics = Ref.method(FallingBlock.class, "onPlace",
			net.minecraft.world.level.block.state.BlockState.class, Level.class, BlockPos.class,
			net.minecraft.world.level.block.state.BlockState.class, boolean.class);

	private void doPhysics(ServerLevel world, BlockPos BlockPos, Block block, BlockPos BlockPos1) {

		net.minecraft.world.level.block.state.BlockState state = world.getBlockState(BlockPos);
		state.neighborChanged(world, BlockPos, block, null, false);
		if (state.getBlock() instanceof FallingBlock)
			Ref.invoke(state.getBlock(), callPhysics, state, world, BlockPos, block.defaultBlockState(), false);
	}

	@Override
	public void updateLightAt(Object objChunk, int x, int y, int z) {
		LevelChunk chunk = (LevelChunk) objChunk;
		chunk.level.getChunkSource().getLightEngine().checkBlock(new BlockPos(x, y, z));
	}

	@Override
	public Object getBlock(Object objChunk, int x, int y, int z) {
		LevelChunk chunk = (LevelChunk) objChunk;
		return chunk.getBlockState(x, y, z);
	}

	@Override
	public byte getData(Object chunk, int x, int y, int z) {
		return 0;
	}

	@Override
	public String getNBTOfTile(Object objChunk, int x, int y, int z) {
		LevelChunk chunk = (LevelChunk) objChunk;
		return chunk.getBlockEntity(new BlockPos(x, y, z), EntityCreationType.IMMEDIATE).saveWithFullMetadata()
				.toString();
	}

	@Override
	public void setNBTToTile(Object objChunk, int x, int y, int z, String nbt) {
		LevelChunk chunk = (LevelChunk) objChunk;
		BlockEntity ent = chunk.getBlockEntity(new BlockPos(x, y, z), EntityCreationType.IMMEDIATE);
		CompoundTag parsedNbt = (CompoundTag) parseNBT(nbt);
		parsedNbt.putInt("x", x);
		parsedNbt.putInt("y", y);
		parsedNbt.putInt("z", z);
		ent.load(parsedNbt);
		Object packet = ent.getUpdatePacket();
		BukkitLoader.getPacketHandler().send(chunk.level.getWorld().getPlayers(), packet);
	}

	@Override
	public boolean isTileEntity(Object objChunk, int x, int y, int z) {
		LevelChunk chunk = (LevelChunk) objChunk;
		return chunk.getBlockEntity(new BlockPos(x, y, z), EntityCreationType.IMMEDIATE) != null;
	}

	@Override
	public int getCombinedId(Object IblockDataOrBlock) {
		return Block.getId((net.minecraft.world.level.block.state.BlockState) IblockDataOrBlock);
	}

	@Override
	public Object blockPosition(int blockX, int blockY, int blockZ) {
		return new BlockPos(blockX, blockY, blockZ);
	}

	@Override
	public Object toIBlockData(Object data) {
		return ((CraftBlockData) data).getState();
	}

	@Override
	public Object toIBlockData(BlockState state) {
		return CraftMagicNumbers.getBlock(state.getType(), state.getRawData());
	}

	@Override
	public Chunk toBukkitChunk(Object nmsChunk) {
		return new CraftChunk((LevelChunk) nmsChunk);
	}

	@Override
	public int getPing(Player player) {
		return ((ServerGamePacketListenerImpl) getPlayerConnection(player)).latency();
	}

	@Override
	public Object getPlayerConnection(Player player) {
		return ((ServerPlayer) getPlayer(player)).connection;
	}

	private static final Field networkManagerField = Ref.field(ServerGamePacketListenerImpl.class, Connection.class);

	@Override
	public Object getConnectionNetwork(Object playercon) {
		return Ref.get(playercon, networkManagerField);
	}

	@Override
	public Object getNetworkChannel(Object network) {
		return ((Connection) network).channel;
	}

	@Override
	public Object packetOpenWindow(int id, String legacy, int size, Component title) {

		MenuType<?> windowType = MenuType.GENERIC_9x1;
		switch (size) {
		case 0: {
			windowType = MenuType.ANVIL;
			break;
		}
		case 18: {
			windowType = MenuType.GENERIC_9x2;
			break;
		}
		case 27: {
			windowType = MenuType.GENERIC_9x3;
			break;
		}
		case 36: {
			windowType = MenuType.GENERIC_9x4;
			break;
		}
		case 45: {
			windowType = MenuType.GENERIC_9x5;
			break;
		}
		case 54: {
			windowType = MenuType.GENERIC_9x6;
			break;
		}
		}
		return new ClientboundOpenScreenPacket(id, windowType,
				(net.minecraft.network.chat.Component) this.toIChatBaseComponent(title));
	}

	@Override
	public void closeGUI(Player player, Object container, boolean closePacket) {
		if (closePacket)
			BukkitLoader.getPacketHandler().send(player,
					new ClientboundContainerClosePacket(((AbstractContainerMenu) container).containerId));
		net.minecraft.world.entity.player.Player nmsPlayer = (net.minecraft.world.entity.player.Player) getPlayer(
				player);
		nmsPlayer.containerMenu = nmsPlayer.inventoryMenu;
		((AbstractContainerMenu) container).transferTo(nmsPlayer.containerMenu, (CraftPlayer) player);
	}

	@Override
	public void setSlot(Object container, int slot, Object item) {
		((AbstractContainerMenu) container).setItem(slot, ((AbstractContainerMenu) container).getStateId(),
				(net.minecraft.world.item.ItemStack) item);
	}

	@Override
	public void setGUITitle(Player player, Object container, String legacy, int size, Component title) {
		int id = ((AbstractContainerMenu) container).containerId;
		BukkitLoader.getPacketHandler().send(player, packetOpenWindow(id, legacy, size, title));
		net.minecraft.world.item.ItemStack carried = ((AbstractContainerMenu) container).getCarried();
		if (!carried.isEmpty())
			BukkitLoader.getPacketHandler().send(player,
					new ClientboundContainerSetSlotPacket(id, getContainerStateId(container), -1, carried));
		int slot = 0;
		for (net.minecraft.world.item.ItemStack item : ((AbstractContainerMenu) container).getItems()) {
			if (slot == size)
				break;
			if (!item.isEmpty())
				BukkitLoader.getPacketHandler().send(player,
						new ClientboundContainerSetSlotPacket(id, getContainerStateId(container), slot, item));
			++slot;
		}
		BukkitLoader.getPacketHandler().send(player,
				new ClientboundContainerSetContentPacket(id, getContainerStateId(container),
						((AbstractContainerMenu) container).remoteSlots,
						((AbstractContainerMenu) container).getCarried()));
	}

	@Override
	public void openGUI(Player player, Object container, String legacy, int size, Component title) {
		ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		int id = ((AbstractContainerMenu) container).containerId;
		BukkitLoader.getPacketHandler().send(player, packetOpenWindow(id, legacy, size, title));
		nmsPlayer.containerMenu.transferTo((AbstractContainerMenu) container, (CraftPlayer) player);
		nmsPlayer.containerMenu = (AbstractContainerMenu) container;
		postToMainThread(() -> nmsPlayer.initMenu((AbstractContainerMenu) container));
		((AbstractContainerMenu) container).checkReachable = false;
	}

	@Override
	public void openAnvilGUI(Player player, Object container, Component title) {
		openGUI(player, container, "minecraft:anvil", 0, title);
	}

	@Override
	public Object createContainer(Inventory inv, Player player) {
		if (inv.getType() == InventoryType.ANVIL) {
			AnvilMenu container = new AnvilMenu(((CraftPlayer) player).getHandle().nextContainerCounter(),
					((CraftPlayer) player).getHandle().getInventory(), ContainerLevelAccess.NULL);
			postToMainThread(() -> {
				int slot = 0;
				for (ItemStack stack : inv.getContents())
					container.getSlot(slot++).set((net.minecraft.world.item.ItemStack) asNMSItem(stack));
			});
			container.checkReachable = false;
			return container;
		}
		return new CraftContainer(inv, ((CraftPlayer) player).getHandle(),
				((CraftPlayer) player).getHandle().nextContainerCounter());
	}

	@Override
	public Object getSlotItem(Object container, int slot) {
		return slot < 0 ? null : ((AbstractContainerMenu) container).getSlot(slot).getItem();
	}

	@Override
	public String getAnvilRenameText(Object anvil) {
		return ((AnvilMenu) anvil).itemName;
	}

	@Override
	public boolean processInvClickPacket(Player player, HolderGUI gui, Object provPacket) {
		ServerboundContainerClickPacket packet = (ServerboundContainerClickPacket) provPacket;
		int slot = packet.getSlotNum();

		Object container = gui.getContainer(player);
		if (container == null)
			return false;

		int id = packet.getContainerId();
		int mouseClick = packet.getButtonNum();
		net.minecraft.world.inventory.ClickType type = packet.getClickType();
		AbstractContainerMenu c = (AbstractContainerMenu) container;

		if (slot < -1 && slot != -999)
			return true;

		net.minecraft.world.entity.player.Player nPlayer = ((CraftPlayer) player).getHandle();

		ItemStack newItem;
		ItemStack oldItem;
		switch (type) {
		case PICKUP: // PICKUP
			oldItem = asBukkitItem(getSlotItem(container, slot));
			newItem = asBukkitItem(c.getCarried());
			if (slot > 0 && mouseClick != 0) {
				if (c.getCarried().isEmpty()) { // pickup half
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
			newItem = asBukkitItem(c.getCarried());
			oldItem = asBukkitItem(getSlotItem(container, slot));
			break;
		case SWAP:// SWAP
			newItem = asBukkitItem(nPlayer.getInventory().getItem(mouseClick));
			oldItem = asBukkitItem(getSlotItem(container, slot));
			break;
		case CLONE:// CLONE
			newItem = asBukkitItem(getSlotItem(container, slot));
			oldItem = asBukkitItem(getSlotItem(container, slot));
			break;
		case THROW:// THROW
			if (c.getCarried().isEmpty() && slot >= 0) {
				Slot slot3 = c.getSlot(slot);
				newItem = asBukkitItem(slot3.getItem());
				if (mouseClick != 0 || newItem.getAmount() - 1 <= 0)
					newItem = new ItemStack(Material.AIR);
				else
					newItem.setAmount(newItem.getAmount() - 1);
			} else
				newItem = asBukkitItem(c.getCarried());
			oldItem = asBukkitItem(getSlotItem(container, slot));
			break;
		case QUICK_CRAFT:// QUICK_CRAFT
			newItem = asBukkitItem(c.getCarried());
			oldItem = slot <= -1 ? new ItemStack(Material.AIR) : asBukkitItem(getSlotItem(container, slot));
			break;
		case PICKUP_ALL:// PICKUP_ALL
			newItem = asBukkitItem(c.getCarried());
			oldItem = asBukkitItem(getSlotItem(container, slot));
			break;
		default:
			newItem = slot <= -1 ? new ItemStack(Material.AIR) : asBukkitItem(packet.getCarriedItem());
			oldItem = slot <= -1 ? new ItemStack(Material.AIR) : asBukkitItem(packet.getCarriedItem());
			break;
		}

		if (oldItem.getType() == Material.AIR && newItem.getType() == Material.AIR)
			return true;

		boolean cancel = false;
		int gameSlot = slot > gui.size() - 1 ? InventoryUtils.convertToPlayerInvSlot(slot - gui.size()) : slot;

		ClickType clickType = InventoryUtils.buildClick(type == net.minecraft.world.inventory.ClickType.QUICK_CRAFT ? 1
				: type == net.minecraft.world.inventory.ClickType.QUICK_MOVE ? 2 : 0, mouseClick);
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
		int statusId = c.getStateId();
		BukkitLoader.getPacketHandler().send(player, packetSetSlot(-1, -1, statusId, c.getCarried()));
		BukkitLoader.getPacketHandler().send(player,
				new ClientboundContainerSetContentPacket(id, statusId, c.remoteSlots, c.getCarried()));
		switch (type) {
		case CLONE:
			break;
		case SWAP:
		case QUICK_MOVE:
		case PICKUP_ALL:
			c.sendAllDataToRemote();
			break;
		default:
			BukkitLoader.getPacketHandler().send(player, packetSetSlot(id, slot, statusId, c.getSlot(slot).getItem()));
			break;
		}
		return true;
	}

	private void processEvent(AbstractContainerMenu c, net.minecraft.world.inventory.ClickType type, HolderGUI gui,
			Player player, int slot, int gameSlot, ItemStack newItem, ItemStack oldItem,
			ServerboundContainerClickPacket packet, int mouseClick, ClickType clickType,
			net.minecraft.world.entity.player.Player nPlayer, boolean isAnvilGui) {
		c.suppressRemoteUpdates();
		if (type == net.minecraft.world.inventory.ClickType.QUICK_MOVE) {
			ItemStack[] contents = slot < gui.size() ? player.getInventory().getStorageContents()
					: gui.getInventory().getStorageContents();
			boolean interactWithResultSlot = false;
			if (gui instanceof AnvilGUI && slot < gui.size() && slot == 2)
				if (c.getSlot(2).allowModification(nPlayer))
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
						nPlayer.getInventory().setItem(modif.getKey(),
								(net.minecraft.world.item.ItemStack) asNMSItem(modif.getValue()));
					if (remaining == 0) {
						c.getSlot(gameSlot).set((net.minecraft.world.item.ItemStack) asNMSItem(null));
						if (isAnvilGui)
							gui.getInventory().setItem(gameSlot, null);
						if (interactWithResultSlot) {
							c.getSlot(0).set((net.minecraft.world.item.ItemStack) asNMSItem(null));
							c.getSlot(1).set((net.minecraft.world.item.ItemStack) asNMSItem(null));
							if (isAnvilGui) {
								gui.getInventory().setItem(0, null);
								gui.getInventory().setItem(1, null);
							}
						}
					} else {
						newItem.setAmount(remaining);
						c.getSlot(gameSlot).set((net.minecraft.world.item.ItemStack) asNMSItem(newItem));
						if (isAnvilGui)
							gui.getInventory().setItem(gameSlot, newItem);
					}
				} else {
					for (Entry<Integer, ItemStack> modif : modified.entrySet())
						c.getSlot(modif.getKey()).set((net.minecraft.world.item.ItemStack) asNMSItem(modif.getValue())); // Visual
																															// &
																															// Nms
																															// side
					// Plugin & Bukkit side
					gui.getInventory().setStorageContents(contents);
					if (remaining == 0)
						nPlayer.getInventory().setItem(gameSlot, (net.minecraft.world.item.ItemStack) asNMSItem(null));
					else {
						newItem.setAmount(remaining);
						nPlayer.getInventory().setItem(gameSlot,
								(net.minecraft.world.item.ItemStack) asNMSItem(newItem));
					}
				}
			c.resumeRemoteUpdates();
			if (gui instanceof AnvilGUI) {
				int index = 0;
				for (int i = 0; i < 3; ++i) {
					gui.getInventory().setItem(index++, asBukkitItem(c.getSlot(i).getItem()));
					if (index == 3)
						break;
				}
			}
			nPlayer.inventoryMenu.setCarried(c.getCarried());
			return;
		}
		processClick(gui, gui.getNotInterableSlots(player), c, slot, mouseClick, type, nPlayer);
		c.resumeRemoteUpdates();
		if (gui instanceof AnvilGUI) {
			int index = 0;
			for (int i = 0; i < 3; ++i) {
				gui.getInventory().setItem(index++, asBukkitItem(c.getSlot(i).getItem()));
				if (index == 3)
					break;
			}
			nPlayer.inventoryMenu.setCarried(c.getCarried());
		}
	}

	private final Method onSwap = Ref.method(Slot.class, "onSwapCraft", int.class);
	private final Field quickcraftStatus = Ref.field(AbstractContainerMenu.class, "quickcraftStatus");
	private final Field quickcraftTypeField = Ref.field(AbstractContainerMenu.class, "quickcraftType");
	private final Field quickcraftSlotsField = Ref.field(AbstractContainerMenu.class, "quickcraftSlots");
	private final Method resetQuickCraft = Ref.method(AbstractContainerMenu.class, "resetQuickCraft");
	private final Method checkItem = Ref.method(AbstractContainerMenu.class, "tryItemClickBehaviourOverride",
			net.minecraft.world.entity.player.Player.class, ClickAction.class, Slot.class,
			net.minecraft.world.item.ItemStack.class, net.minecraft.world.item.ItemStack.class);

	private void processClick(HolderGUI gui, List<Integer> ignoredSlots, AbstractContainerMenu container, int slotIndex,
			int button, net.minecraft.world.inventory.ClickType actionType,
			net.minecraft.world.entity.player.Player player) {
		net.minecraft.world.entity.player.Inventory playerinventory = player.getInventory();
		Slot slot;
		net.minecraft.world.item.ItemStack itemstack;
		if (actionType == net.minecraft.world.inventory.ClickType.QUICK_CRAFT)
			processDragMove(gui, ignoredSlots, container, player, slotIndex, button);
		else if ((int) Ref.get(container, quickcraftStatus) != 0)
			Ref.invoke(container, resetQuickCraft);
		else {
			int count;

			if ((actionType == net.minecraft.world.inventory.ClickType.PICKUP
					|| actionType == net.minecraft.world.inventory.ClickType.QUICK_MOVE)
					&& (button == 0 || button == 1)) {
				ClickAction clickaction = button == 0 ? ClickAction.PRIMARY : ClickAction.SECONDARY;

				if (slotIndex == -999) {
					if (!container.getCarried().isEmpty())
						if (clickaction == ClickAction.PRIMARY) {
							// CraftBukkit start
							net.minecraft.world.item.ItemStack carried = container.getCarried();
							container.setCarried(net.minecraft.world.item.ItemStack.EMPTY);
							postToMainThread(() -> player.drop(carried, true));
							// CraftBukkit start
						} else
							postToMainThread(() -> player.drop(container.getCarried().split(1), true));
				} else if (actionType == net.minecraft.world.inventory.ClickType.QUICK_MOVE) {
					if (slotIndex < 0)
						return;

					slot = container.slots.get(slotIndex);
					if (!slot.mayPickup(player))
						return;

					for (itemstack = container.quickMoveStack(player, slotIndex); !itemstack.isEmpty()
							&& net.minecraft.world.item.ItemStack.isSameItem(slot.getItem(),
									itemstack); itemstack = container.quickMoveStack(player, slotIndex))
						;
				} else {
					if (slotIndex < 0)
						return;

					slot = container.slots.get(slotIndex);
					itemstack = slot.getItem();
					net.minecraft.world.item.ItemStack carried = container.getCarried();

					player.updateTutorialInventoryAction(carried, slot.getItem(), clickaction);
					if (!(boolean) Ref.invoke(container, checkItem, player, clickaction, slot, itemstack, carried))
						if (itemstack.isEmpty()) {
							if (!carried.isEmpty()) {
								count = clickaction == ClickAction.PRIMARY ? carried.getCount() : 1;
								container.setCarried(slot.safeInsert(carried, count));
							}
						} else if (slot.mayPickup(player))
							if (carried.isEmpty()) {
								count = clickaction == ClickAction.PRIMARY ? itemstack.getCount()
										: (itemstack.getCount() + 1) / 2;
								slot.tryRemove(count, Integer.MAX_VALUE, player).ifPresent(taken -> {
									container.setCarried(taken);
									slot.onTake(player, taken);
								});
							} else if (slot.mayPlace(carried)) {
								if (net.minecraft.world.item.ItemStack.isSameItemSameTags(itemstack, carried)) {
									count = clickaction == ClickAction.PRIMARY ? carried.getCount() : 1;
									container.setCarried(slot.safeInsert(carried, count));
								} else if (carried.getCount() <= slot.getMaxStackSize(carried)) {
									container.setCarried(itemstack);
									slot.setByPlayer(carried);
								}
							} else if (net.minecraft.world.item.ItemStack.isSameItemSameTags(itemstack, carried))
								slot.tryRemove(itemstack.getCount(), carried.getMaxStackSize() - carried.getCount(),
										player).ifPresent(taken -> {
											carried.grow(taken.getCount());
											slot.onTake(player, taken);
										});

					slot.setChanged();
					// CraftBukkit start - Make sure the client has the right slot contents
					if (player instanceof ServerPlayer && slot.getMaxStackSize() != 64) {
						((ServerPlayer) player).connection.send(new ClientboundContainerSetSlotPacket(
								container.containerId, container.incrementStateId(), slot.index, slot.getItem()));
						// Updating a crafting inventory makes the client reset the result slot, have to
						// send it again
						if (container.getBukkitView().getType() == InventoryType.WORKBENCH
								|| container.getBukkitView().getType() == InventoryType.CRAFTING)
							((ServerPlayer) player).connection
									.send(new ClientboundContainerSetSlotPacket(container.containerId,
											container.incrementStateId(), 0, container.getSlot(0).getItem()));
					}
					// CraftBukkit end
				}
			} else {
				int maxStackSize;

				if (actionType == net.minecraft.world.inventory.ClickType.SWAP
						&& (button >= 0 && button < 9 || button == 40)) {
					if (slotIndex < 0)
						return; // Paper - Add slot sanity checks to container clicks
					net.minecraft.world.item.ItemStack swapItem = playerinventory.getItem(button);

					slot = container.slots.get(slotIndex);
					itemstack = slot.getItem();
					if (!swapItem.isEmpty() || !itemstack.isEmpty())
						if (swapItem.isEmpty()) {
							if (slot.mayPickup(player)) {
								playerinventory.setItem(button, itemstack);
								Ref.invoke(slot, onSwap, itemstack.getCount());
								slot.setByPlayer(net.minecraft.world.item.ItemStack.EMPTY);
								slot.onTake(player, itemstack);
							}
						} else if (itemstack.isEmpty()) {
							if (slot.mayPlace(swapItem)) {
								maxStackSize = slot.getMaxStackSize(swapItem);
								if (swapItem.getCount() > maxStackSize)
									slot.setByPlayer(swapItem.split(maxStackSize));
								else {
									playerinventory.setItem(button, net.minecraft.world.item.ItemStack.EMPTY);
									slot.setByPlayer(swapItem);
								}
							}
						} else if (slot.mayPickup(player) && slot.mayPlace(swapItem)) {
							maxStackSize = slot.getMaxStackSize(swapItem);
							if (swapItem.getCount() > maxStackSize) {
								slot.setByPlayer(swapItem.split(maxStackSize));
								slot.onTake(player, itemstack);
								if (!playerinventory.add(itemstack)) {
									net.minecraft.world.item.ItemStack finalItemstack = itemstack;
									postToMainThread(() -> player.drop(finalItemstack, true));
								}
							} else {
								playerinventory.setItem(button, itemstack);
								slot.setByPlayer(swapItem);
								slot.onTake(player, itemstack);
							}
						}
				} else if (actionType == net.minecraft.world.inventory.ClickType.CLONE && player.isCreative()
						&& container.getCarried().isEmpty() && slotIndex >= 0) {
					slot = container.slots.get(slotIndex);
					if (slot.hasItem()) {
						itemstack = slot.getItem();
						container.setCarried(itemstack.copyWithCount(itemstack.getMaxStackSize()));
					}
				} else if (actionType == net.minecraft.world.inventory.ClickType.THROW
						&& container.getCarried().isEmpty() && slotIndex >= 0) {
					slot = container.slots.get(slotIndex);
					count = button == 0 ? 1 : slot.getItem().getCount();
					itemstack = slot.safeTake(count, Integer.MAX_VALUE, player);
					net.minecraft.world.item.ItemStack finalItemstack = itemstack;
					postToMainThread(() -> player.drop(finalItemstack, true));
				} else if (actionType == net.minecraft.world.inventory.ClickType.PICKUP_ALL && slotIndex >= 0) {
					slot = container.slots.get(slotIndex);
					itemstack = container.getCarried();
					if (!itemstack.isEmpty() && (!slot.hasItem() || !slot.mayPickup(player))) {
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

								if (targetSlot.hasItem()
										&& AbstractContainerMenu.canItemQuickReplace(targetSlot, itemstack, true)
										&& targetSlot.mayPickup(player)
										&& container.canTakeItemForPickAll(itemstack, targetSlot)) {
									net.minecraft.world.item.ItemStack targetItem = targetSlot.getItem();

									if (count != 0 || targetItem.getCount() != targetItem.getMaxStackSize()) {
										if (index < gui.size() && ignoreSlots.contains(index)) {
											corruptedSlots.add(index);
											continue;
										}

										net.minecraft.world.item.ItemStack resultItem = targetSlot.safeTake(
												targetItem.getCount(),
												itemstack.getMaxStackSize() - itemstack.getCount(), player);

										itemstack.grow(resultItem.getCount());
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
									BukkitLoader.getNmsProvider().packetSetSlot(container.incrementStateId(), s,
											getContainerStateId(container),
											BukkitLoader.getNmsProvider().getSlotItem(container, s)));
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void processDragMove(HolderGUI gui, List<Integer> ignoredSlots, AbstractContainerMenu container,
			net.minecraft.world.entity.player.Player player, int slotIndex, int button) {
		Slot slot;
		net.minecraft.world.item.ItemStack itemstack;
		int k;
		int count;
		int quickcraftType = (int) Ref.get(container, quickcraftTypeField);
		int quickcraftStat = (int) Ref.get(container, quickcraftStatus);
		int quickcraftStatNew = AbstractContainerMenu.getQuickcraftHeader(button);
		Set<Slot> quickcraftSlots = (Set<Slot>) Ref.get(container, quickcraftSlotsField);
		Ref.set(container, quickcraftStatus, quickcraftStatNew);
		if ((quickcraftStat != 1 || quickcraftStatNew != 2) && quickcraftStat != quickcraftStatNew
				|| container.getCarried().isEmpty())
			Ref.invoke(container, resetQuickCraft);
		else
			switch (quickcraftStatNew) {
			case 0:
				if (AbstractContainerMenu.isValidQuickcraftType(quickcraftStatNew, player)) {
					Ref.set(container, quickcraftStatus, 1);
					quickcraftSlots.clear();
				} else
					Ref.invoke(container, resetQuickCraft);
				break;
			case 1:
				if (slotIndex < 0)
					return; // Paper - Add slot sanity checks to container clicks
				slot = container.slots.get(slotIndex);
				itemstack = container.getCarried();
				if (AbstractContainerMenu.canItemQuickReplace(slot, itemstack, true) && slot.mayPlace(itemstack)
						&& (quickcraftType == 2 || itemstack.getCount() > quickcraftSlots.size())
						&& container.canDragTo(slot))
					quickcraftSlots.add(slot);
				break;
			case 2:
				if (!quickcraftSlots.isEmpty()) {
					if (quickcraftSlots.size() == 1) { // Paper - Fix CraftBukkit drag system
						k = quickcraftSlots.iterator().next().index;
						Ref.invoke(container, resetQuickCraft);
						processClick(gui, ignoredSlots, container, k, quickcraftType,
								net.minecraft.world.inventory.ClickType.PICKUP, player);
						return;
					}

					itemstack = container.getCarried().copy();
					if (itemstack.isEmpty()) {
						Ref.invoke(container, resetQuickCraft);
						return;
					}

					count = container.getCarried().getCount();
					Map<Integer, net.minecraft.world.item.ItemStack> draggedSlots = new HashMap<>(); // CraftBukkit -
																										// Store slots
																										// from drag in
																										// map (raw slot
																										// id -> new
																										// stack)
					for (Slot slot1 : quickcraftSlots) {
						net.minecraft.world.item.ItemStack itemstack2 = container.getCarried();

						if (slot1 != null && AbstractContainerMenu.canItemQuickReplace(slot1, itemstack2, true)
								&& slot1.mayPlace(itemstack2)
								&& (quickcraftType == 2 || itemstack2.getCount() >= quickcraftSlots.size())
								&& container.canDragTo(slot1)) {
							int j1 = slot1.hasItem() ? slot1.getItem().getCount() : 0;
							int k1 = Math.min(itemstack.getMaxStackSize(), slot1.getMaxStackSize(itemstack));
							int l1 = Math.min(AbstractContainerMenu.getQuickCraftPlaceCount(quickcraftSlots,
									quickcraftType, itemstack) + j1, k1);

							count -= l1 - j1;
							// slot1.setByPlayer(itemstack1.copyWithCount(l1));
							draggedSlots.put(slot1.index, itemstack.copyWithCount(l1)); // CraftBukkit - Put in map
																						// instead of setting
						}
					}

					// CraftBukkit start - InventoryDragEvent
					InventoryView view = container.getBukkitView();
					org.bukkit.inventory.ItemStack newcursor = CraftItemStack.asCraftMirror(itemstack);
					newcursor.setAmount(count);
					Map<Integer, org.bukkit.inventory.ItemStack> eventmap = new HashMap<>();
					for (Map.Entry<Integer, net.minecraft.world.item.ItemStack> ditem : draggedSlots.entrySet())
						eventmap.put(ditem.getKey(), CraftItemStack.asBukkitCopy(ditem.getValue()));

					// It's essential that we set the cursor to the new value here to prevent item
					// duplication if a plugin closes the inventory.
					container.setCarried(CraftItemStack.asNMSCopy(newcursor));

					boolean needsUpdate = false;

					final Map<Integer, org.bukkit.inventory.ItemStack> guiSlots = new HashMap<>();
					final Map<Integer, org.bukkit.inventory.ItemStack> playerSlots = new HashMap<>();
					for (final Entry<Integer, net.minecraft.world.item.ItemStack> ditem : draggedSlots.entrySet())
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

					for (Map.Entry<Integer, net.minecraft.world.item.ItemStack> dslot : draggedSlots.entrySet())
						view.setItem(dslot.getKey(), CraftItemStack.asBukkitCopy(dslot.getValue()));
					// The only time the carried item will be set to null is if the inventory is
					// closed by the server.
					// If the inventory is closed by the server, then the cursor items are dropped.
					// This is why we change the cursor early.
					if (container.getCarried() != null) {
						container.setCarried(CraftItemStack.asNMSCopy(newcursor));
						needsUpdate = true;
					}

					if (needsUpdate && player instanceof ServerPlayer)
						container.sendAllDataToRemote();
				}
				Ref.invoke(container, resetQuickCraft);
				break;
			default:
				Ref.invoke(container, resetQuickCraft);
				break;
			}
	}

	@Override
	public boolean processServerListPing(String player, Object channel, Object packet) {
		if (packet instanceof PacketContainer container) {
			ClientboundStatusResponsePacket status = (ClientboundStatusResponsePacket) container.getPacket();
			ServerStatus ping = status.status();
			List<GameProfileHandler> gameProfiles = new ArrayList<>();
			if (ping.players().isPresent())
				for (GameProfile profile : ping.players().get().sample())
					gameProfiles.add(fromGameProfile(profile));

			net.minecraft.network.chat.Component motd = net.minecraft.network.chat.Component.literal("");
			Optional<Players> players;
			Optional<Favicon> serverIcon = Optional.empty();
			Optional<Version> version = ping.version();
			boolean enforceSecureProfile = ping.enforcesSecureChat();

			String favicon = "server-icon.png";
			ServerListPingEvent event = new ServerListPingEvent(getOnlinePlayers().size(), Bukkit.getMaxPlayers(),
					gameProfiles, Bukkit.getMotd(), favicon,
					((InetSocketAddress) ((Channel) channel).remoteAddress()).getAddress(), ping.version().get().name(),
					ping.version().get().protocol());
			EventManager.call(event);
			if (event.isCancelled()) {
				container.setCancelled(true);
				return true;
			}
			Players playerSample = new Players(event.getMaxPlayers(), event.getOnlinePlayers(), new ArrayList<>());
			if (event.getSlots() != null)
				for (GameProfileHandler s : event.getSlots())
					playerSample.sample().add(new GameProfile(s.getUUID(), s.getUsername()));
			players = Optional.of(playerSample);

			if (event.getMotd() != null)
				motd = (net.minecraft.network.chat.Component) this
						.toIChatBaseComponent(ComponentAPI.fromString(event.getMotd()));
			if (event.getVersion() != null)
				version = Optional.of(new Version(event.getVersion(), event.getProtocol()));
			if (event.getFavicon() != null)
				if (!"server-icon.png".equals(event.getFavicon()) && new File(event.getFavicon()).exists()) {
					BufferedImage var1;
					try {
						var1 = ImageIO.read(new File(event.getFavicon()));
						if (var1.getWidth() != 64)
							throw new IOException("Must be 64 pixels wide");
						if (var1.getHeight() != 64)
							throw new IOException("Must be 64 pixels high");
						ByteArrayOutputStream var2 = new ByteArrayOutputStream();
						ImageIO.write(var1, "PNG", var2);
						serverIcon = Optional.of(new Favicon(var2.toByteArray()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else
					serverIcon = ping.favicon();
			container.setPacket(new ClientboundStatusResponsePacket(
					new ServerStatus(motd, players, version, serverIcon, enforceSecureProfile)));
			return false;
		}
		JavaPlugin.getPlugin(BukkitLoader.class).getLogger()
				.warning("You are using outdated version of TheAPI, please update TheAPI to the latest version!");
		return false;
	}

	@Override
	public Object getNBT(Entity entity) {
		return ((CraftEntity) entity).getHandle().saveWithoutId(new CompoundTag());
	}

	@Override
	public Object setString(Object nbt, String path, String value) {
		((CompoundTag) nbt).putString(path, value);
		return nbt;
	}

	@Override
	public Object setInteger(Object nbt, String path, int value) {
		((CompoundTag) nbt).putInt(path, value);
		return nbt;
	}

	@Override
	public Object setDouble(Object nbt, String path, double value) {
		((CompoundTag) nbt).putDouble(path, value);
		return nbt;
	}

	@Override
	public Object setLong(Object nbt, String path, long value) {
		((CompoundTag) nbt).putLong(path, value);
		return nbt;
	}

	@Override
	public Object setShort(Object nbt, String path, short value) {
		((CompoundTag) nbt).putShort(path, value);
		return nbt;
	}

	@Override
	public Object setFloat(Object nbt, String path, float value) {
		((CompoundTag) nbt).putFloat(path, value);
		return nbt;
	}

	@Override
	public Object setBoolean(Object nbt, String path, boolean value) {
		((CompoundTag) nbt).putBoolean(path, value);
		return nbt;
	}

	@Override
	public Object setIntArray(Object nbt, String path, int[] value) {
		((CompoundTag) nbt).putIntArray(path, value);
		return nbt;
	}

	@Override
	public Object setByteArray(Object nbt, String path, byte[] value) {
		((CompoundTag) nbt).putByteArray(path, value);
		return nbt;
	}

	@Override
	public Object setNBTBase(Object nbt, String path, Object value) {
		((CompoundTag) nbt).put(path, (Tag) value);
		return nbt;
	}

	@Override
	public String getString(Object nbt, String path) {
		return ((CompoundTag) nbt).getString(path);
	}

	@Override
	public int getInteger(Object nbt, String path) {
		return ((CompoundTag) nbt).getInt(path);
	}

	@Override
	public double getDouble(Object nbt, String path) {
		return ((CompoundTag) nbt).getDouble(path);
	}

	@Override
	public long getLong(Object nbt, String path) {
		return ((CompoundTag) nbt).getLong(path);
	}

	@Override
	public short getShort(Object nbt, String path) {
		return ((CompoundTag) nbt).getShort(path);
	}

	@Override
	public float getFloat(Object nbt, String path) {
		return ((CompoundTag) nbt).getFloat(path);
	}

	@Override
	public boolean getBoolean(Object nbt, String path) {
		return ((CompoundTag) nbt).getBoolean(path);
	}

	@Override
	public int[] getIntArray(Object nbt, String path) {
		return ((CompoundTag) nbt).getIntArray(path);
	}

	@Override
	public byte[] getByteArray(Object nbt, String path) {
		return ((CompoundTag) nbt).getByteArray(path);
	}

	@Override
	public Object getNBTBase(Object nbt, String path) {
		return ((CompoundTag) nbt).get(path);
	}

	@Override
	public Set<String> getKeys(Object nbt) {
		return ((CompoundTag) nbt).getAllKeys();
	}

	@Override
	public boolean hasKey(Object nbt, String path) {
		return ((CompoundTag) nbt).contains(path);
	}

	@Override
	public void removeKey(Object nbt, String path) {
		((CompoundTag) nbt).remove(path);
	}

	@Override
	public Object setByte(Object nbt, String path, byte value) {
		((CompoundTag) nbt).putByte(path, value);
		return nbt;
	}

	@Override
	public byte getByte(Object nbt, String path) {
		return ((CompoundTag) nbt).getByte(path);
	}

	@Override
	public Object getDataWatcher(Entity entity) {
		return ((CraftEntity) entity).getHandle().getEntityData();
	}

	@Override
	public Object getDataWatcher(Object entity) {
		return ((net.minecraft.world.entity.Entity) entity).getEntityData();
	}

	@Override
	public int incrementStateId(Object container) {
		return ((AbstractContainerMenu) container).incrementStateId();
	}

	@Override
	public Object packetEntityHeadRotation(Entity entity) {
		return new ClientboundRotateHeadPacket((net.minecraft.world.entity.Entity) getEntity(entity),
				(byte) (entity.getLocation().getYaw() * 256F / 360F));
	}

	@Override
	public Object packetHeldItemSlot(int slot) {
		return new ClientboundSetCarriedItemPacket(slot);
	}

	@Override
	public Object packetExp(float exp, int total, int toNextLevel) {
		return new ClientboundSetExperiencePacket(exp, total, toNextLevel);
	}

	private ClientboundPlayerInfoUpdatePacket.Action fromBukkit(PlayerInfoType type) {
		return switch (type) {
		case ADD_PLAYER -> ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER;
		case UPDATE_DISPLAY_NAME -> ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME;
		case UPDATE_GAME_MODE -> ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE;
		case UPDATE_LATENCY -> ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY;
		default -> null;
		};
	}

	@Override
	public Object packetPlayerInfo(PlayerInfoType type, Player player) {
		if (type == PlayerInfoType.REMOVE_PLAYER)
			return new ClientboundPlayerInfoRemovePacket(List.of(player.getUniqueId()));
		return new ClientboundPlayerInfoUpdatePacket(fromBukkit(type), (ServerPlayer) getPlayer(player));
	}

	@Override
	public Object packetPlayerInfo(PlayerInfoType type, GameProfileHandler gameProfile, int latency, GameMode gameMode,
			Component playerName) {
		if (type == PlayerInfoType.REMOVE_PLAYER)
			return new ClientboundPlayerInfoRemovePacket(List.of(gameProfile.getUUID()));
		EnumSet<ClientboundPlayerInfoUpdatePacket.Action> set = EnumSet.of(fromBukkit(type));
		List<ClientboundPlayerInfoUpdatePacket.Entry> list = Collections
				.singletonList(new ClientboundPlayerInfoUpdatePacket.Entry(gameProfile.getUUID(),
						(GameProfile) toGameProfile(gameProfile), true, latency,
						gameMode == null ? GameType.SURVIVAL : GameType.byName(gameMode.name().toLowerCase()),
						(net.minecraft.network.chat.Component) (playerName == null
								? toIChatBaseComponent(new Component(gameProfile.getUsername()))
								: toIChatBaseComponent(playerName)),
						null));
		Object packet = Ref.newUnsafeInstance(ClientboundPlayerInfoUpdatePacket.class);
		Ref.set(packet, "actions", set);
		Ref.set(packet, "entries", list);
		return packet;
	}

	@Override
	public Object packetPosition(double x, double y, double z, float yaw, float pitch) {
		return new ServerboundMovePlayerPacket.PosRot(x, y, z, yaw, pitch, true);
	}

	@Override
	public Object packetRespawn(Player player) {
		ServerPlayer entityPlayer = (ServerPlayer) getPlayer(player);
		ServerLevel worldserver = entityPlayer.serverLevel();
		return new ClientboundRespawnPacket(entityPlayer.createCommonSpawnInfo(worldserver), (byte) 3);
	}

	@Override
	public String getProviderName() {
		return "PaperMC (1.20.2) " + Bukkit.getServer().getMinecraftVersion();
	}

	@Override
	public int getContainerStateId(Object container) {
		return ((AbstractContainerMenu) container).getStateId();
	}

	@Override
	public void loadParticles() {
		for (Entry<ResourceKey<ParticleType<?>>, ParticleType<?>> s : BuiltInRegistries.PARTICLE_TYPE.entrySet())
			me.devtec.theapi.bukkit.game.particles.Particle.identifier.put(s.getKey().location().getPath(),
					s.getValue());
	}

	@Override
	public Object toGameProfile(GameProfileHandler gameProfileHandler) {
		GameProfile profile = new GameProfile(gameProfileHandler.getUUID(), gameProfileHandler.getUsername());
		for (Entry<String, PropertyHandler> entry : gameProfileHandler.getProperties().entrySet())
			profile.getProperties().put(entry.getKey(), new Property(entry.getValue().getName(),
					entry.getValue().getValues(), entry.getValue().getSignature()));
		return profile;
	}

	private final Field name = Ref.field(Property.class, "name");
	private final Field value = Ref.field(Property.class, "value");
	private final Field signature = Ref.field(Property.class, "signature");

	@Override
	public GameProfileHandler fromGameProfile(Object gameProfile) {
		GameProfile profile = (GameProfile) gameProfile;
		GameProfileHandler handler = GameProfileHandler.of(profile.getName(), profile.getId());
		for (Entry<String, Property> entry : profile.getProperties().entries())
			handler.getProperties().put(entry.getKey(), PropertyHandler.of((String) Ref.get(entry.getValue(), name),
					(String) Ref.get(entry.getValue(), value), (String) Ref.get(entry.getValue(), signature)));
		return handler;
	}

	@Override
	public Object getGameProfile(Object nmsPlayer) {
		return ((net.minecraft.world.entity.player.Player) nmsPlayer).getGameProfile();
	}

}
