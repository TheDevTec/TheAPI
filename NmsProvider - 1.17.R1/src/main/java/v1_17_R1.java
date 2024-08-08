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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import me.devtec.theapi.bukkit.nms.GameProfileHandler;
import me.devtec.theapi.bukkit.nms.NBTEdit;
import me.devtec.theapi.bukkit.nms.NmsProvider;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

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
import me.devtec.theapi.bukkit.nms.GameProfileHandler.PropertyHandler;
import me.devtec.theapi.bukkit.nms.utils.InventoryUtils;
import me.devtec.theapi.bukkit.nms.utils.InventoryUtils.DestinationType;
import me.devtec.theapi.bukkit.xseries.XMaterial;
import net.minecraft.EnumChatFormat;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.particles.Particle;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.chat.ChatClickable;
import net.minecraft.network.chat.ChatClickable.EnumClickAction;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatHexColor;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.ChatHoverable.EnumHoverAction;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.PacketPlayInWindowClick;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import net.minecraft.network.protocol.game.PacketPlayOutCloseWindow;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutExperience;
import net.minecraft.network.protocol.game.PacketPlayOutHeldItemSlot;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo.PlayerInfoData;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.network.protocol.game.PacketPlayOutPosition;
import net.minecraft.network.protocol.game.PacketPlayOutResourcePackSend;
import net.minecraft.network.protocol.game.PacketPlayOutRespawn;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.network.protocol.status.PacketStatusOutServerInfo;
import net.minecraft.network.protocol.status.ServerPing;
import net.minecraft.network.protocol.status.ServerPing.ServerData;
import net.minecraft.network.protocol.status.ServerPing.ServerPingPlayerSample;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.inventory.Containers;
import net.minecraft.world.inventory.InventoryClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockFalling;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ITileEntity;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.IBlockDataHolder;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.chunk.Chunk.EnumTileEntityState;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.scores.ScoreboardObjective;
import net.minecraft.world.scores.criteria.IScoreboardCriteria.EnumScoreboardHealthDisplay;

public class v1_17_R1 implements NmsProvider {
	private static final MinecraftServer server = MinecraftServer.getServer();

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
		return ((net.minecraft.world.entity.Entity) entity).getId();
	}

	@Override
	public Object getScoreboardAction(Action type) {
		return type == Action.CHANGE ? ScoreboardServer.Action.a : ScoreboardServer.Action.b;
	}

	@Override
	public Object getEnumScoreboardHealthDisplay(DisplayType type) {
		return type == DisplayType.INTEGER ? EnumScoreboardHealthDisplay.a : EnumScoreboardHealthDisplay.b;
	}

	@Override
	public Object getNBT(ItemStack itemStack) {
		return ((net.minecraft.world.item.ItemStack) asNMSItem(itemStack)).getOrCreateTag();
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
		net.minecraft.world.item.ItemStack i = (net.minecraft.world.item.ItemStack) asNMSItem(stack);
		i.setTag((NBTTagCompound) nbt);
		return asBukkitItem(i);
	}

	@Override
	public Object asNMSItem(ItemStack stack) {
		if (stack == null)
			return net.minecraft.world.item.ItemStack.b;
		return CraftItemStack.asNMSCopy(stack);
	}

	@Override
	public ItemStack asBukkitItem(Object stack) {
		return CraftItemStack.asBukkitCopy(stack == null ? net.minecraft.world.item.ItemStack.b : (net.minecraft.world.item.ItemStack) stack);
	}

	@Override
	public int getContainerId(Object container) {
		return ((Container) container).j;
	}

	@Override
	public Object packetResourcePackSend(String url, String hash, boolean requireRP, Component prompt) {
		return new PacketPlayOutResourcePackSend(url, hash, requireRP, prompt == null ? null : (IChatBaseComponent) this.toIChatBaseComponent(prompt));
	}

	@Override
	public Object packetSetSlot(int container, int slot, int changeId, Object itemStack) {
		return new PacketPlayOutSetSlot(container, changeId, slot, (net.minecraft.world.item.ItemStack) (itemStack == null ? asNMSItem(null) : itemStack));
	}

	@Override
	public Object packetEntityMetadata(int entityId, Object dataWatcher, boolean bal) {
		return new PacketPlayOutEntityMetadata(entityId, (net.minecraft.network.syncher.DataWatcher) dataWatcher, bal);
	}

	@Override
	public Object packetEntityDestroy(int... ids) {
		return new PacketPlayOutEntityDestroy(ids);
	}

	@Override
	public Object packetSpawnEntity(Object entity, int id) {
		return new PacketPlayOutSpawnEntity((net.minecraft.world.entity.Entity) entity, id);
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
		return new PacketPlayOutPlayerListHeaderFooter((IChatBaseComponent) this.toIChatBaseComponent(header), (IChatBaseComponent) this.toIChatBaseComponent(footer));
	}

	@Override
	public Object packetBlockChange(int x, int y, int z, Object iblockdata, int data) {
		return new PacketPlayOutBlockChange(new BlockPosition(x, y, z), iblockdata == null ? Blocks.a.getBlockData() : (IBlockData) iblockdata);
	}

	@Override
	public Object packetScoreboardObjective() {
		return Ref.newUnsafeInstance(PacketPlayOutScoreboardObjective.class);
	}

	@Override
	public Object packetScoreboardDisplayObjective(int id, Object scoreboardObjective) {
		return new PacketPlayOutScoreboardDisplayObjective(id, scoreboardObjective == null ? null : (ScoreboardObjective) scoreboardObjective);
	}

	@Override
	public Object packetScoreboardTeam() {
		return Ref.newUnsafeInstance(PacketPlayOutScoreboardTeam.class);
	}

	@Override
	public Object packetScoreboardScore(Action action, String player, String line, int score) {
		return new PacketPlayOutScoreboardScore((net.minecraft.server.ScoreboardServer.Action) getScoreboardAction(action), player, line, score);
	}

	@Override
	public Object packetTitle(TitleAction action, Component text, int fadeIn, int stay, int fadeOut) {
		switch (action) {
		case ACTIONBAR:
			return new ClientboundSetActionBarTextPacket((IChatBaseComponent) this.toIChatBaseComponent(text));
		case TITLE:
			return new ClientboundSetTitleTextPacket((IChatBaseComponent) this.toIChatBaseComponent(text));
		case SUBTITLE:
			return new ClientboundSetSubtitleTextPacket((IChatBaseComponent) this.toIChatBaseComponent(text));
		case TIMES:
			return new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut);
		case CLEAR:
		case RESET:
			return new ClientboundClearTitlesPacket(true);
		}
		return null;
	}

	@Override
	public Object packetChat(ChatType type, Object chatBase, UUID uuid) {
		switch (type) {
		case CHAT:
			return new PacketPlayOutChat((IChatBaseComponent) chatBase, ChatMessageType.a, uuid);
		case GAME_INFO:
			return new PacketPlayOutChat((IChatBaseComponent) chatBase, ChatMessageType.c, uuid);
		case SYSTEM:
			return new PacketPlayOutChat((IChatBaseComponent) chatBase, ChatMessageType.b, uuid);
		}
		return null;
	}

	@Override
	public Object packetChat(ChatType type, Component text, UUID uuid) {
		return this.packetChat(type, this.toIChatBaseComponent(text), uuid);
	}

	@Override
	public void postToMainThread(Runnable runnable) {
		v1_17_R1.server.executeSync(runnable);
	}

	@Override
	public Object getMinecraftServer() {
		return v1_17_R1.server;
	}

	@Override
	public Thread getServerThread() {
		return v1_17_R1.server.an;
	}

	@Override
	public double[] getServerTPS() {
		return v1_17_R1.server.recentTps;
	}

	private IChatBaseComponent convert(Component c) {
		ChatComponentText current = new ChatComponentText(c.getText());
		ChatModifier modif = current.getChatModifier();
		if (c.getColor() != null && !c.getColor().isEmpty())
			if (c.getColor().startsWith("#"))
				modif = modif.setColor(ChatHexColor.a(c.getColor()));
			else
				modif = modif.setColor(EnumChatFormat.a(c.colorToChar()));
		if (c.getClickEvent() != null)
			modif = modif.setChatClickable(new ChatClickable(EnumClickAction.valueOf(c.getClickEvent().getAction().name()), c.getClickEvent().getValue()));
		if (c.getHoverEvent() != null)
			switch (c.getHoverEvent().getAction()) {
			case SHOW_ENTITY:
				try {
					ComponentEntity compoundTag = (ComponentEntity) c.getHoverEvent().getValue();
					IChatBaseComponent component = compoundTag.getName() == null ? null : (IChatBaseComponent) toIChatBaseComponent(compoundTag.getName());
					EntityTypes<?> entityType = IRegistry.Y.get(new MinecraftKey(compoundTag.getType()));
					modif = modif.setChatHoverable(new ChatHoverable(EnumHoverAction.c, new ChatHoverable.b(entityType, compoundTag.getId(), component)));
				} catch (Exception commandSyntaxException) {
				}
				break;
			case SHOW_ITEM:
				try {
					ComponentItem compoundTag = (ComponentItem) c.getHoverEvent().getValue();
					net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(
							CraftMagicNumbers.getItem(XMaterial.matchXMaterial(compoundTag.getId()).orElse(XMaterial.AIR).parseMaterial()), compoundTag.getCount());
					if (compoundTag.getNbt() != null)
						stack.setTag((NBTTagCompound) parseNBT(compoundTag.getNbt()));
					modif = modif.setChatHoverable(new ChatHoverable(EnumHoverAction.b, new ChatHoverable.c(stack)));
				} catch (Exception commandSyntaxException) {
				}
				break;
			default:
				modif = modif.setChatHoverable(new ChatHoverable(EnumHoverAction.a, (IChatBaseComponent) this.toIChatBaseComponent(c.getHoverEvent().getValue())));
				break;
			}
		modif = modif.setBold(c.isBold());
		modif = modif.setItalic(c.isItalic());
		modif = modif.setRandom(c.isObfuscated());
		modif = modif.setUnderline(c.isUnderlined());
		modif = modif.setStrikethrough(c.isStrikethrough());
		current.setChatModifier(modif);
		return current;
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
			return new IChatBaseComponent[] { ChatComponentText.d };
		if (co instanceof ComponentItem || co instanceof ComponentEntity)
			return new IChatBaseComponent[] { IChatBaseComponent.ChatSerializer.b(Json.writer().simpleWrite(co.toJsonMap())) };
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
			return ChatComponentText.d;
		if (co instanceof ComponentItem || co instanceof ComponentEntity)
			return IChatBaseComponent.ChatSerializer.b(Json.writer().simpleWrite(co.toJsonMap()));
		ChatComponentText main = new ChatComponentText("");
		List<IChatBaseComponent> chat = new ArrayList<>();
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
		for (IChatBaseComponent d : chat)
			main.addSibling(d);
		return main.getSiblings().isEmpty() ? ChatComponentText.d : main;
	}

	@Override
	public Object toIChatBaseComponent(List<Component> cc) {
		ChatComponentText main = new ChatComponentText("");
		for (Component c : cc)
			main.addSibling((IChatBaseComponent) this.toIChatBaseComponent(c));
		return main.getSiblings().isEmpty() ? ChatComponentText.d : main;
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
			if (!component.getSiblings().isEmpty()) {
				List<Component> extra = new ArrayList<>();
				for (IChatBaseComponent base : component.getSiblings())
					extra.add(fromIChatBaseComponent(base));
				comp.setExtra(extra);
			}
			return comp;
		}
		Component comp = new Component(component.getText());
		ChatModifier modif = component.getChatModifier();
		if (modif.getColor() != null)
			comp.setColor(modif.getColor().b());

		if (modif.getClickEvent() != null)
			comp.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(modif.getClickEvent().a().name()), modif.getClickEvent().b()));

		if (modif.getHoverEvent() != null)
			switch (HoverEvent.Action.valueOf(modif.getHoverEvent().a().b().toUpperCase())) {
			case SHOW_ENTITY: {
				net.minecraft.network.chat.ChatHoverable.b hover = modif.getHoverEvent().a(EnumHoverAction.c);
				ComponentEntity compEntity = new ComponentEntity(hover.a.j().getKey(), hover.b);
				if (hover.c != null)
					compEntity.setName(fromIChatBaseComponent(hover.c));
				comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, compEntity));
				break;
			}
			case SHOW_ITEM: {
				net.minecraft.network.chat.ChatHoverable.c hover = modif.getHoverEvent().a(EnumHoverAction.b);
				ComponentItem compEntity = new ComponentItem(CraftMagicNumbers.getMaterial(hover.a().getItem()).name(), hover.a().getCount());
				if (hover.a().getTag() != null)
					compEntity.setNbt(hover.a().getTag().toString());
				comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, compEntity));
				break;
			}
			default:
				IChatBaseComponent hover = modif.getHoverEvent().a(EnumHoverAction.a);
				comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, fromIChatBaseComponent(hover)));
				break;
			}
		comp.setBold(modif.isBold());
		comp.setItalic(modif.isItalic());
		comp.setObfuscated(modif.isRandom());
		comp.setUnderlined(modif.isUnderlined());
		comp.setStrikethrough(modif.isStrikethrough());

		if (!component.getSiblings().isEmpty()) {
			List<Component> extra = new ArrayList<>();
			for (IChatBaseComponent base : component.getSiblings())
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

	private String asString(IBlockData data) {
		StringBuilder stateString = new StringBuilder();
		if (!data.getStateMap().isEmpty()) {
			stateString.append('[');
			stateString.append(data.getStateMap().entrySet().stream().map(IBlockDataHolder.a).collect(Collectors.joining(",")));
			stateString.append(']');
		}
		return stateString.toString();
	}

	@Override
	public Object toIBlockData(BlockDataStorage material) {
		if (material == null || material.getType() == null || material.getType() == Material.AIR)
			return Blocks.a.getBlockData();
		Block block = CraftMagicNumbers.getBlock(material.getType());
		return readArgument(block, material);
	}

	@Override
	public Object toBlock(BlockDataStorage material) {
		if (material == null || material.getType() == null || material.getType() == Material.AIR)
			return Blocks.a;
		Block block = CraftMagicNumbers.getBlock(material.getType());
		return readArgument(block, material).getBlock();
	}

	private IBlockData readArgument(Block block, BlockDataStorage material) {
		IBlockData ib = block.getBlockData();
		return writeData(ib, ib.getBlock().getStates(), material.getData());
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
				IBlockState ibj = blockStateList.a(key);
				if (ibj != null) {
					Optional optional = ibj.b(value);
					if (optional.isPresent())
						ib = ib.set(ibj, (Comparable) optional.get());
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
			IBlockState ibj = blockStateList.a(key);
			if (ibj != null) {
				Optional optional = ibj.b(value);
				if (optional.isPresent())
					ib = ib.set(ibj, (Comparable) optional.get());
			}
		}
		return ib;
	}

	@Override
	public ItemStack toItemStack(BlockDataStorage material) {
		Item item = CraftMagicNumbers.getItem(material.getType(), ParseUtils.getShort(material.getData()));
		return CraftItemStack.asBukkitCopy(item.createItemStack());
	}

	@Override
	public Object getChunk(World world, int x, int z) {
		return ((CraftChunk) world.getChunkAt(x, z)).getHandle();
	}

	static Field blockNbt = Ref.field(net.minecraft.world.level.chunk.Chunk.class, "f");

	@Override
	public void setBlock(Object objChunk, int x, int y, int z, Object IblockData, int data) {
		net.minecraft.world.level.chunk.Chunk chunk = (net.minecraft.world.level.chunk.Chunk) objChunk;
		int highY = chunk.getSectionIndex(y);
		if (highY < 0)
			return;
		ChunkSection sc = chunk.b(highY);
		if (sc == null)
			return;
		BlockPosition pos = new BlockPosition(x, y, z);

		IBlockData iblock = IblockData == null ? Blocks.a.getBlockData() : (IBlockData) IblockData;

		boolean onlyModifyState = iblock.getBlock() instanceof ITileEntity;

		// REMOVE TILE ENTITY IF NOT SAME TYPE
		TileEntity ent = chunk.l.get(pos);
		if (ent != null) {
			boolean shouldSkip = true;
			if (!onlyModifyState)
				shouldSkip = false;
			else if (onlyModifyState && !ent.getBlock().getBlock().getClass().equals(iblock.getBlock().getClass())) {
				shouldSkip = false;
				onlyModifyState = false;
			}
			if (!shouldSkip)
				chunk.removeTileEntity(pos);
		}

		IBlockData old = sc.setType(x & 15, y & 15, z & 15, iblock, false);

		// ADD TILE ENTITY
		if (iblock.getBlock() instanceof ITileEntity && !onlyModifyState) {
			ent = ((ITileEntity) iblock.getBlock()).createTile(pos, iblock);
			chunk.l.put(pos, ent);
			ent.setWorld(chunk.i);
			Object packet = ent.getUpdatePacket();
			BukkitLoader.getPacketHandler().send(chunk.i.getWorld().getPlayers(), packet);
		}

		// MARK CHUNK TO SAVE
		chunk.setNeedsSaving(true);

		// POI
		if (!chunk.i.preventPoiUpdated)
			chunk.i.a(pos, old, iblock);
	}

	@Override
	public void updatePhysics(Object objChunk, int x, int y, int z, Object iblockdata) {
		net.minecraft.world.level.chunk.Chunk chunk = (net.minecraft.world.level.chunk.Chunk) objChunk;

		BlockPosition blockPos = new BlockPosition(x, y, z);

		doPhysicsAround(chunk.i, blockPos, ((IBlockData) iblockdata).getBlock());
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
		world.getType(blockposition).doPhysics(world, blockposition, block, blockposition1, false);
		IBlockData state = world.getType(blockposition);
		state.doPhysics(world, blockposition, block, blockposition1, false);
		if (state.getBlock() instanceof BlockFalling)
			((BlockFalling) state.getBlock()).onPlace(state, world, blockposition, block.getBlockData(), false);
	}

	@Override
	public void updateLightAt(Object chunk, int x, int y, int z) {
		net.minecraft.world.level.chunk.Chunk c = (net.minecraft.world.level.chunk.Chunk) chunk;
		c.i.getChunkProvider().getLightEngine().a(new BlockPosition(x, y, z));
	}

	@Override
	public Object getBlock(Object objChunk, int x, int y, int z) {
		net.minecraft.world.level.chunk.Chunk chunk = (net.minecraft.world.level.chunk.Chunk) objChunk;
		int highY = chunk.getSectionIndex(y);
		if (highY < 0)
			return Blocks.a.getBlockData();
		ChunkSection sc = chunk.b(highY);
		if (sc == null)
			return Blocks.a.getBlockData();
		return sc.getBlocks().a(x & 15, y & 15, z & 15);
	}

	@Override
	public byte getData(Object chunk, int x, int y, int z) {
		return 0;
	}

	@Override
	public String getNBTOfTile(Object objChunk, int x, int y, int z) {
		net.minecraft.world.level.chunk.Chunk chunk = (net.minecraft.world.level.chunk.Chunk) objChunk;
		return chunk.a(new BlockPosition(x, y, z), EnumTileEntityState.a).save(new NBTTagCompound()).toString();
	}

	@Override
	public void setNBTToTile(Object objChunk, int x, int y, int z, String nbt) {
		net.minecraft.world.level.chunk.Chunk chunk = (net.minecraft.world.level.chunk.Chunk) objChunk;
		TileEntity ent = chunk.a(new BlockPosition(x, y, z), EnumTileEntityState.a);
		NBTTagCompound parsedNbt = (NBTTagCompound) parseNBT(nbt);
		parsedNbt.setInt("x", x);
		parsedNbt.setInt("y", y);
		parsedNbt.setInt("z", z);
		ent.load(parsedNbt);
		Object packet = ent.getUpdatePacket();
		BukkitLoader.getPacketHandler().send(chunk.i.getWorld().getPlayers(), packet);
	}

	@Override
	public boolean isTileEntity(Object objChunk, int x, int y, int z) {
		net.minecraft.world.level.chunk.Chunk chunk = (net.minecraft.world.level.chunk.Chunk) objChunk;
		return chunk.a(new BlockPosition(x, y, z), EnumTileEntityState.a) != null;
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
	public Object toIBlockData(Object data) {
		return ((CraftBlockData) data).getState();
	}

	@Override
	public Object toIBlockData(BlockState state) {
		return CraftMagicNumbers.getBlock(state.getType(), state.getRawData());
	}

	@Override
	public Chunk toBukkitChunk(Object nmsChunk) {
		return ((net.minecraft.world.level.chunk.Chunk) nmsChunk).bukkitChunk;
	}

	@Override
	public int getPing(Player player) {
		return ((EntityPlayer) getPlayer(player)).e;
	}

	@Override
	public Object getPlayerConnection(Player player) {
		return ((EntityPlayer) getPlayer(player)).b;
	}

	@Override
	public Object getConnectionNetwork(Object playercon) {
		return ((PlayerConnection) playercon).a;
	}

	@Override
	public Object getNetworkChannel(Object network) {
		return ((NetworkManager) network).k;
	}

	@Override
	public Object packetOpenWindow(int id, String legacy, int size, Component title) {
		Containers<?> windowType = Containers.a;
		switch (size) {
		case 0: {
			windowType = Containers.h;
			break;
		}
		case 18: {
			windowType = Containers.b;
			break;
		}
		case 27: {
			windowType = Containers.c;
			break;
		}
		case 36: {
			windowType = Containers.d;
			break;
		}
		case 45: {
			windowType = Containers.e;
			break;
		}
		case 54: {
			windowType = Containers.f;
			break;
		}
		}
		return new PacketPlayOutOpenWindow(id, windowType, (IChatBaseComponent) this.toIChatBaseComponent(title));
	}

	@Override
	public void closeGUI(Player player, Object container, boolean closePacket) {
		if (closePacket)
			BukkitLoader.getPacketHandler().send(player, new PacketPlayOutCloseWindow(BukkitLoader.getNmsProvider().getContainerId(container)));
		EntityPlayer nmsPlayer = (EntityPlayer) getPlayer(player);
		nmsPlayer.bV = nmsPlayer.bU;
		((Container) container).transferTo(nmsPlayer.bV, (CraftPlayer) player);
	}

	@Override
	public void setSlot(Object container, int slot, Object item) {
		((Container) container).a(slot, (net.minecraft.world.item.ItemStack) item);
	}

	@Override
	public void setGUITitle(Player player, Object container, String legacy, int size, Component title) {
		int id = ((Container) container).j;
		BukkitLoader.getPacketHandler().send(player, packetOpenWindow(id, legacy, size, title));
		net.minecraft.world.item.ItemStack carried = ((Container) container).getCarried();
		if (!carried.isEmpty())
			BukkitLoader.getPacketHandler().send(player, new PacketPlayOutSetSlot(id, getContainerStateId(container), -1, carried));
		int slot = 0;
		for (net.minecraft.world.item.ItemStack item : ((Container) container).c()) {
			if (slot == size)
				break;
			if (!item.isEmpty())
				BukkitLoader.getPacketHandler().send(player, new PacketPlayOutSetSlot(id, getContainerStateId(container), slot, item));
			++slot;
		}
	}

	@Override
	public void openGUI(Player player, Object container, String legacy, int size, Component title) {
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		int id = ((Container) container).j;
		BukkitLoader.getPacketHandler().send(player, packetOpenWindow(id, legacy, size, title));
		nmsPlayer.bV.transferTo((Container) container, (CraftPlayer) player);
		nmsPlayer.bV = (Container) container;
		nmsPlayer.initMenu((Container) container);
		((Container) container).checkReachable = false;
	}

	@Override
	public void openAnvilGUI(Player player, Object container, Component title) {
		openGUI(player, container, "minecraft:anvil", 0, title);
	}

	@Override
	public Object createContainer(Inventory inv, Player player) {
		if (inv.getType() == InventoryType.ANVIL) {
			ContainerAnvil container = new ContainerAnvil(((CraftPlayer) player).getHandle().nextContainerCounter(), ((CraftPlayer) player).getHandle().getInventory(), new ContainerAccess() {

				@Override
				public <T> Optional<T> a(BiFunction<net.minecraft.world.level.World, BlockPosition, T> getter) {
					return Optional.empty();
				}

				@Override
				public Location getLocation() {
					return null;
				}
			});
			postToMainThread(() -> {
				int slot = 0;
				for (ItemStack stack : inv.getContents())
					container.getSlot(slot++).set((net.minecraft.world.item.ItemStack) asNMSItem(stack));
			});
			container.checkReachable = false;
			return container;
		}
		return new CraftContainer(inv, ((CraftPlayer) player).getHandle(), ((CraftPlayer) player).getHandle().nextContainerCounter());
	}

	@Override
	public Object getSlotItem(Object container, int slot) {
		return slot < 0 ? null : ((Container) container).getSlot(slot).getItem();
	}

	@Override
	public String getAnvilRenameText(Object anvil) {
		return ((ContainerAnvil) anvil).v;
	}

	public static int c(final int quickCraftData) {
		return quickCraftData >> 2 & 0x3;
	}

	public static int d(final int quickCraftData) {
		return quickCraftData & 0x3;
	}

	@Override
	public boolean processInvClickPacket(Player player, HolderGUI gui, Object provPacket) {
		PacketPlayInWindowClick packet = (PacketPlayInWindowClick) provPacket;
		int slot = packet.c();

		Object container = gui.getContainer(player);
		if (container == null)
			return false;

		int id = packet.b();
		int mouseClick = packet.d();
		InventoryClickType type = packet.g();

		if (packet.c() < -1 && packet.c() != -999)
			return true;

		Container c = (Container) container;
		EntityHuman nPlayer = ((CraftPlayer) player).getHandle();

		ItemStack newItem;
		ItemStack oldItem;
		switch (type) {
		case a: // PICKUP
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
			} else if (slot > 0 && mouseClick == 0) // drop
				if (oldItem.isSimilar(newItem))
					newItem.setAmount(Math.min(newItem.getAmount() + oldItem.getAmount(), newItem.getMaxStackSize()));
			break;
		case b: // QUICK_MOVE
			newItem = asBukkitItem(c.getCarried());
			oldItem = asBukkitItem(getSlotItem(container, slot));
			break;
		case c:// SWAP
			newItem = asBukkitItem(nPlayer.getInventory().getItem(mouseClick));
			oldItem = asBukkitItem(getSlotItem(container, slot));
			break;
		case d:// CLONE
			newItem = asBukkitItem(getSlotItem(container, slot));
			oldItem = asBukkitItem(getSlotItem(container, slot));
			break;
		case e:// THROW
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
		case f:// QUICK_CRAFT
			newItem = asBukkitItem(c.getCarried());
			oldItem = slot <= -1 ? new ItemStack(Material.AIR) : asBukkitItem(getSlotItem(container, slot));
			break;
		case g:// PICKUP_ALL
			newItem = asBukkitItem(c.getCarried());
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

		ClickType clickType = InventoryUtils.buildClick(type == InventoryClickType.f ? 1 : type == InventoryClickType.b ? 2 : 0, mouseClick);
		if (slot > -1) {
			if (!cancel)
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
				postToMainThread(() -> {
					processEvent(c, type, gui, player, slot, gameSlot, newItemFinal, oldItem, packet, mouseClick, clickType, nPlayer);
				});
			} else
				processEvent(c, type, gui, player, slot, gameSlot, newItem, oldItem, packet, mouseClick, clickType, nPlayer);
			return true;
		}
		// MOUSE
		int statusId = ((Container) container).getStateId();
		BukkitLoader.getPacketHandler().send(player, packetSetSlot(-1, -1, statusId, c.getCarried()));
		switch (type) {
		case d:
			break;
		case c:
		case b:
		case g:
			c.updateInventory();
			break;
		default:
			BukkitLoader.getPacketHandler().send(player, packetSetSlot(id, slot, statusId, c.getSlot(packet.c()).getItem()));
			break;
		}
		return true;
	}

	private void processEvent(Container c, InventoryClickType type, HolderGUI gui, Player player, int slot, int gameSlot, ItemStack newItem, ItemStack oldItem, PacketPlayInWindowClick packet,
			int mouseClick, ClickType clickType, EntityHuman nPlayer) {
		c.h();
		switch (type) {
		case b: {
			ItemStack[] contents = slot < gui.size() ? player.getInventory().getStorageContents() : gui.getInventory().getStorageContents();
			boolean interactWithResultSlot = false;
			if (gui instanceof AnvilGUI && slot < gui.size() && slot == 2)
				if (c.getSlot(2).isAllowed(nPlayer))
					interactWithResultSlot = true;
				else
					return;
			Pair result = slot < gui.size()
					? InventoryUtils.shift(slot, player, gui, clickType, gui instanceof AnvilGUI && slot != 2 ? DestinationType.PLAYER_FROM_ANVIL : DestinationType.PLAYER, null, contents, oldItem)
					: InventoryUtils.shift(slot, player, gui, clickType, DestinationType.GUI, gui.getNotInterableSlots(player), contents, oldItem);
			@SuppressWarnings("unchecked")
			Map<Integer, ItemStack> modified = (Map<Integer, ItemStack>) result.getValue();
			int remaining = (int) result.getKey();

			if (!modified.isEmpty())
				if (slot < gui.size()) {
					for (Entry<Integer, ItemStack> modif : modified.entrySet())
						nPlayer.getInventory().setItem(modif.getKey(), (net.minecraft.world.item.ItemStack) asNMSItem(modif.getValue()));
					if (remaining == 0) {
						c.getSlot(gameSlot).set((net.minecraft.world.item.ItemStack) asNMSItem(null));
						if (interactWithResultSlot) {
							c.getSlot(0).set((net.minecraft.world.item.ItemStack) asNMSItem(null));
							c.getSlot(1).set((net.minecraft.world.item.ItemStack) asNMSItem(null));
						}
					} else {
						newItem.setAmount(remaining);
						c.getSlot(gameSlot).set((net.minecraft.world.item.ItemStack) asNMSItem(newItem));
					}
				} else {
					for (Entry<Integer, ItemStack> modif : modified.entrySet())
						c.getSlot(modif.getKey()).set((net.minecraft.world.item.ItemStack) asNMSItem(modif.getValue())); // Visual & Nms side
					// Plugin & Bukkit side
					gui.getInventory().setStorageContents(contents);
					if (remaining == 0)
						nPlayer.getInventory().setItem(gameSlot, (net.minecraft.world.item.ItemStack) asNMSItem(null));
					else {
						newItem.setAmount(remaining);
						nPlayer.getInventory().setItem(gameSlot, (net.minecraft.world.item.ItemStack) asNMSItem(newItem));
					}
				}
			c.i();
			return;
		}
		default:
			processClick(gui, gui.getNotInterableSlots(player), c, slot, mouseClick, type, nPlayer);
			break;
		}
		postToMainThread(() -> {
			if (type != InventoryClickType.f && (c.getType().equals(Containers.h) || c.getType().equals(Containers.u)))
				c.updateInventory();
			packet.f().forEach((key, value) -> c.b(key, value));
			c.a(packet.e());
			c.i();
			if (packet.h() != c.getStateId())
				c.e();
			else
				c.d();
		});
	}

	private Method addAmount = Ref.method(Slot.class, "b", int.class);
	private Method getSlotAccess = Ref.method(Container.class, "m");

	@SuppressWarnings("unchecked")
	private void processClick(HolderGUI gui, List<Integer> ignoredSlots, Container container, int slotIndex, int button, InventoryClickType actionType, EntityHuman player) {
		if (actionType == InventoryClickType.f)
			processDragMove(gui, container, player, slotIndex, button);
		else {
			int u = (int) Ref.get(container, containerU);
			int j = getContainerStateId(container);
			Set<Slot> mod = (Set<Slot>) Ref.get(container, containerV);
			if (u != 0) {
				Ref.set(container, containerU, u = 0);
				mod.clear();
			} else if (actionType == InventoryClickType.a && (button == 0 || button == 1)) {
				ClickAction clickaction = button == 0 ? ClickAction.a : ClickAction.b;
				if (slotIndex == -999) {
					if (!container.getCarried().isEmpty())
						if (clickaction == ClickAction.a) {
							net.minecraft.world.item.ItemStack carried = container.getCarried();
							container.setCarried(net.minecraft.world.item.ItemStack.b);
							postToMainThread(() -> player.drop(carried, true));
						} else
							postToMainThread(() -> player.drop(container.getCarried().cloneAndSubtract(1), true));
				} else {
					if (slotIndex < 0)
						return;
					Slot slot = container.getSlot(slotIndex);
					net.minecraft.world.item.ItemStack itemstack = slot.getItem();
					net.minecraft.world.item.ItemStack itemstack4 = container.getCarried();
					player.a(itemstack4, slot.getItem(), clickaction);

					if (!itemstack4.a(slot, clickaction, player) && !itemstack.a(itemstack4, slot, clickaction, player, (SlotAccess) Ref.invoke(container, getSlotAccess)))
						if (itemstack.isEmpty()) {
							if (!itemstack4.isEmpty()) {
								int i2 = clickaction == ClickAction.a ? itemstack4.getCount() : 1;
								net.minecraft.world.item.ItemStack stack = slot.b(itemstack4, i2);
								container.setCarried(stack);
							}
						} else if (slot.isAllowed(player))
							if (itemstack4.isEmpty()) {
								int i2 = clickaction == ClickAction.a ? itemstack.getCount() : (itemstack.getCount() + 1) / 2;
								Optional<net.minecraft.world.item.ItemStack> optional = slot.a(i2, Integer.MAX_VALUE, player);
								optional.ifPresent(i -> {
									container.setCarried(i);
									slot.a(player, i);
								});
							} else if (slot.isAllowed(itemstack4)) {
								if (net.minecraft.world.item.ItemStack.c(itemstack, itemstack4)) {
									int i2 = clickaction == ClickAction.a ? itemstack4.getCount() : 1;
									net.minecraft.world.item.ItemStack stack = slot.b(itemstack4, i2);
									container.setCarried(stack);
								} else if (itemstack4.getCount() <= slot.getMaxStackSize(itemstack4)) {
									container.setCarried(itemstack);
									slot.set(itemstack4);
								}
							} else if (net.minecraft.world.item.ItemStack.c(itemstack, itemstack4)) {
								Optional<net.minecraft.world.item.ItemStack> optional2 = slot.a(itemstack.getCount(), itemstack4.getMaxStackSize() - itemstack4.getCount(), player);
								optional2.ifPresent(i -> {
									itemstack.setCount(i.getCount());
									slot.a(player, i);
								});
							}
					slot.d();
					if (player instanceof EntityPlayer && slot.getMaxStackSize() != 64) {
						BukkitLoader.getPacketHandler().send((Player) player.getBukkitEntity(), BukkitLoader.getNmsProvider().packetSetSlot(j, slot.d, container.incrementStateId(), slot.getItem()));
						if (container.getBukkitView().getType() == InventoryType.WORKBENCH || container.getBukkitView().getType() == InventoryType.CRAFTING)
							BukkitLoader.getPacketHandler().send((Player) player.getBukkitEntity(),
									BukkitLoader.getNmsProvider().packetSetSlot(j, 0, container.incrementStateId(), container.getSlot(0).getItem()));
					}
				}
			} else if (actionType == InventoryClickType.c) {
				if (slotIndex < 0)
					return;
				PlayerInventory playerinventory = player.getInventory();
				Slot slot3 = container.getSlot(slotIndex);
				net.minecraft.world.item.ItemStack itemstack2 = playerinventory.getItem(button);
				net.minecraft.world.item.ItemStack itemstack = slot3.getItem();
				if (!itemstack2.isEmpty() || !itemstack.isEmpty())
					if (itemstack2.isEmpty()) {
						if (slot3.isAllowed(player)) {
							playerinventory.setItem(button, itemstack);
							Ref.invoke(slot3, addAmount, itemstack.getCount());
							slot3.set(net.minecraft.world.item.ItemStack.b);
							slot3.a(player, itemstack);
						}
					} else if (itemstack.isEmpty()) {
						if (slot3.isAllowed(itemstack2)) {
							int j2 = slot3.getMaxStackSize(itemstack2);
							if (itemstack2.getCount() > j2)
								slot3.set(itemstack2.cloneAndSubtract(j2));
							else {
								playerinventory.setItem(button, net.minecraft.world.item.ItemStack.b);
								slot3.set(itemstack2);
							}
						}
					} else if (slot3.isAllowed(player) && slot3.isAllowed(itemstack2)) {
						int j2 = slot3.getMaxStackSize(itemstack2);
						if (itemstack2.getCount() > j2) {
							slot3.set(itemstack2.cloneAndSubtract(j2));
							slot3.a(player, itemstack);
							if (!playerinventory.pickup(itemstack))
								postToMainThread(() -> player.drop(itemstack, true));
						} else {
							playerinventory.setItem(button, itemstack);
							slot3.set(itemstack2);
							slot3.a(player, itemstack);
						}
					}
			} else if (actionType == InventoryClickType.d && player.getAbilities().d && container.getCarried().isEmpty() && slotIndex >= 0) {
				Slot slot3 = container.getSlot(slotIndex);
				if (slot3.hasItem()) {
					net.minecraft.world.item.ItemStack itemstack2 = slot3.getItem();

					net.minecraft.world.item.ItemStack stack = itemstack2.cloneItemStack();
					stack.setCount(itemstack2.getCount());
					container.setCarried(stack);
				}
			} else if (actionType == InventoryClickType.e && container.getCarried().isEmpty() && slotIndex >= 0) {
				Slot slot3 = container.getSlot(slotIndex);
				int m = button == 0 ? 1 : slot3.getItem().getCount();
				net.minecraft.world.item.ItemStack itemstack = slot3.b(m, Integer.MAX_VALUE, player);
				postToMainThread(() -> player.drop(itemstack, true));
			} else if (actionType == InventoryClickType.g && slotIndex >= 0) {
				Slot slot2 = container.i.get(slotIndex);
				net.minecraft.world.item.ItemStack itemstack1 = container.getCarried();
				if (!itemstack1.isEmpty() && (!slot2.hasItem() || !slot2.isAllowed(player))) {
					List<Integer> ignoreSlots = ignoredSlots == null ? Collections.emptyList() : ignoredSlots;
					List<Integer> corruptedSlots = ignoredSlots == null ? Collections.emptyList() : new ArrayList<>();
					Map<Integer, ItemStack> modifiedSlots = new HashMap<>();
					Map<Integer, ItemStack> modifiedSlotsPlayerInv = new HashMap<>();

					int l = button == 0 ? 0 : container.i.size() - 1;
					int i2 = button == 0 ? 1 : -1;

					for (int l1 = 0; l1 < 2; ++l1)
						for (int j2 = l; j2 >= 0 && j2 < container.i.size() && itemstack1.getCount() < itemstack1.getMaxStackSize(); j2 += i2) {
							Slot slot3 = container.i.get(j2);
							if (slot3.hasItem() && Container.a(slot3, itemstack1, true) && slot3.isAllowed(player) && container.a(itemstack1, slot3)) {
								net.minecraft.world.item.ItemStack itemstack3 = slot3.getItem();
								if (l1 != 0 || itemstack3.getCount() != itemstack3.getMaxStackSize()) {
									if (j2 < gui.size() && ignoreSlots.contains(j2)) {
										corruptedSlots.add(j2);
										continue;
									}
									net.minecraft.world.item.ItemStack itemstack6 = slot3.b(itemstack3.getCount(), itemstack1.getMaxStackSize() - itemstack1.getCount(), player);
									itemstack1.add(itemstack6.getCount());
									int gameSlot = j2 > gui.size() - 1 ? InventoryUtils.convertToPlayerInvSlot(j2 - gui.size()) : j2;
									if (j2 < gui.size())
										modifiedSlots.put(gameSlot, asBukkitItem(slot3.getItem()));
									else
										modifiedSlotsPlayerInv.put(gameSlot, asBukkitItem(slot3.getItem()));
								}
							}
						}
					if (slotIndex < gui.size())
						modifiedSlots.put(slotIndex, new ItemStack(Material.AIR));
					else
						modifiedSlotsPlayerInv.put(InventoryUtils.convertToPlayerInvSlot(slotIndex - gui.size()), new ItemStack(Material.AIR));
					if (!modifiedSlots.isEmpty() || !modifiedSlotsPlayerInv.isEmpty())
						gui.onMultipleIteract((Player) player.getBukkitEntity(), modifiedSlots, modifiedSlotsPlayerInv);
					for (int s : corruptedSlots)
						BukkitLoader.getPacketHandler().send((Player) player.getBukkitEntity(), BukkitLoader.getNmsProvider().packetSetSlot(BukkitLoader.getNmsProvider().getContainerId(container), s,
								getContainerStateId(container), BukkitLoader.getNmsProvider().getSlotItem(container, s)));
				}
			}
		}
	}

	private Field containerU = Ref.field(Container.class, "s"), containerV = Ref.field(Container.class, "u"), containerT = Ref.field(Container.class, "t");

	@SuppressWarnings("unchecked")
	private void processDragMove(HolderGUI gui, Container container, EntityHuman player, int slot, int mouseClick) {
		int previous = (int) Ref.get(container, containerU);
		int u = d(mouseClick);
		Set<Slot> mod = (Set<Slot>) Ref.get(container, containerV);
		if ((previous != 1 || u != 2) && previous != u || container.getCarried().isEmpty()) {
			mod.clear();
			u = 0;
		} else
			switch (u) {
			case 0: {
				int t = c(mouseClick);
				Ref.set(container, containerT, t);
				if (Container.a(t, player)) {
					u = 1;
					mod.clear();
				} else {
					mod.clear();
					u = 0;
				}
				break;
			}
			case 1: {
				if (slot < 0) {
					Ref.set(container, containerU, u);
					return; // nothing
				}
				int t = (int) Ref.get(container, containerT);
				final Slot bslot = container.getSlot(slot);
				final net.minecraft.world.item.ItemStack itemstack = container.getCarried();
				if (Container.a(bslot, itemstack, true) && bslot.isAllowed(itemstack) && (t == 2 || itemstack.getCount() > mod.size()) && container.b(bslot))
					mod.add(bslot);
				break;
			}
			case 2:
				if (!mod.isEmpty()) {
					final net.minecraft.world.item.ItemStack itemstack2 = container.getCarried().cloneItemStack();
					if (itemstack2.isEmpty()) {
						mod.clear();
						Ref.set(container, containerU, 0);
						return;
					}
					int t = (int) Ref.get(container, containerT);
					int l = container.getCarried().getCount();
					final Map<Integer, net.minecraft.world.item.ItemStack> draggedSlots = new HashMap<>();
					for (Slot slot2 : mod) {
						final net.minecraft.world.item.ItemStack itemstack3 = container.getCarried();
						if (slot2 != null && Container.a(slot2, itemstack3, true) && slot2.isAllowed(itemstack3) && (t == 2 || itemstack3.getCount() >= mod.size()) && container.b(slot2)) {

							final int j1 = slot2.hasItem() ? slot2.getItem().getCount() : 0;
							final int k1 = Math.min(itemstack2.getMaxStackSize(), slot2.getMaxStackSize(itemstack2));
							final int l2 = Math.min(a(mod, t, itemstack2) + j1, k1);
							l -= l2 - j1;
							net.minecraft.world.item.ItemStack stack = itemstack2.cloneItemStack();
							stack.setCount(l2);
							draggedSlots.put(slot2.d, stack);
						}
					}
					final InventoryView view = container.getBukkitView();
					final org.bukkit.inventory.ItemStack newcursor = CraftItemStack.asCraftMirror(itemstack2);
					newcursor.setAmount(l);
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
					container.setCarried(CraftItemStack.asNMSCopy(newcursor));
					if (!guiSlots.isEmpty() || !playerSlots.isEmpty())
						gui.onMultipleIteract((Player) player.getBukkitEntity(), guiSlots, playerSlots);
					for (final Entry<Integer, net.minecraft.world.item.ItemStack> dslot : draggedSlots.entrySet())
						view.setItem(dslot.getKey(), CraftItemStack.asBukkitCopy(dslot.getValue()));
					if (container.getCarried() != null)
						container.updateInventory();
				}
				mod.clear();
				u = 0;
			default:
				mod.clear();
				u = 0;
				break;
			}
		Ref.set(container, containerU, u);
	}

	public static int a(Set<Slot> slots, int mode, net.minecraft.world.item.ItemStack stack) {
		int j;
		switch (mode) {
		case 0:
			j = MathHelper.d((float) stack.getCount() / (float) slots.size());
			break;
		case 1:
			j = 1;
			break;
		case 2:
			j = stack.getItem().getMaxStackSize();
			break;
		default:
			j = stack.getCount();
		}

		return j;
	}

	@Override
	public boolean processServerListPing(String player, Object channel, Object packet) {
		PacketStatusOutServerInfo status = (PacketStatusOutServerInfo) packet;
		ServerPing ping = status.b();

		List<GameProfileHandler> players = new ArrayList<>();
		if (ping.b().c() != null)
			for (GameProfile profile : ping.b().c())
				players.add(fromGameProfile(profile));

		ServerListPingEvent event = new ServerListPingEvent(getOnlinePlayers().size(), Bukkit.getMaxPlayers(), players, Bukkit.getMotd(), ping.d(),
				((InetSocketAddress) ((Channel) channel).remoteAddress()).getAddress(), ping.getServerData().a(), ping.getServerData().getProtocolVersion());
		EventManager.call(event);
		if (event.isCancelled())
			return true;
		ServerPingPlayerSample playerSample = new ServerPingPlayerSample(event.getMaxPlayers(), event.getOnlinePlayers());
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
		return ((NBTTagCompound) nbt).getKeys();
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
		return ((net.minecraft.world.entity.Entity) entity).getDataWatcher();
	}

	@Override
	public int incrementStateId(Object container) {
		return ((Container) container).incrementStateId();
	}

	@Override
	public Object packetEntityHeadRotation(Entity entity) {
		return new PacketPlayOutEntityHeadRotation((net.minecraft.world.entity.Entity) getEntity(entity), (byte) (entity.getLocation().getYaw() * 256F / 360F));
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
		EnumPlayerInfoAction action = null;
		switch (type) {
		case ADD_PLAYER:
			action = EnumPlayerInfoAction.a;
			break;
		case REMOVE_PLAYER:
			action = EnumPlayerInfoAction.e;
			break;
		case UPDATE_DISPLAY_NAME:
			action = EnumPlayerInfoAction.d;
			break;
		case UPDATE_GAME_MODE:
			action = EnumPlayerInfoAction.b;
			break;
		case UPDATE_LATENCY:
			action = EnumPlayerInfoAction.c;
			break;
		}
		return new PacketPlayOutPlayerInfo(action, (EntityPlayer) getPlayer(player));
	}

	@Override
	public Object packetPlayerInfo(PlayerInfoType type, GameProfileHandler gameProfile, int latency, GameMode gameMode, Component playerName) {
		EnumPlayerInfoAction action = null;
		switch (type) {
		case ADD_PLAYER:
			action = EnumPlayerInfoAction.a;
			break;
		case REMOVE_PLAYER:
			action = EnumPlayerInfoAction.e;
			break;
		case UPDATE_DISPLAY_NAME:
			action = EnumPlayerInfoAction.d;
			break;
		case UPDATE_GAME_MODE:
			action = EnumPlayerInfoAction.b;
			break;
		case UPDATE_LATENCY:
			action = EnumPlayerInfoAction.c;
			break;
		}
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(action, Collections.emptyList());
		packet.b().add(new PlayerInfoData((GameProfile) toGameProfile(gameProfile), latency, gameMode == null ? EnumGamemode.a : EnumGamemode.a(gameMode.name().toLowerCase()),
				(IChatBaseComponent) (playerName == null ? toIChatBaseComponent(new Component(gameProfile.getUsername())) : toIChatBaseComponent(playerName))));
		return packet;
	}

	@Override
	public Object packetPosition(double x, double y, double z, float yaw, float pitch) {
		return new PacketPlayOutPosition(x, y, z, yaw, pitch, Collections.emptySet(), 0, false);
	}

	@Override
	public Object packetRespawn(Player player) {
		EntityPlayer entityPlayer = (EntityPlayer) getPlayer(player);
		WorldServer worldserver = entityPlayer.getWorldServer();
		return new PacketPlayOutRespawn(worldserver.getDimensionManager(), worldserver.getDimensionKey(), BiomeManager.a(worldserver.getSeed()), entityPlayer.d.getGameMode(), entityPlayer.d.c(),
				worldserver.isDebugWorld(), worldserver.isFlatWorld(), true);
	}

	@Override
	public String getProviderName() {
		return "1_17_R1 (1.17)";
	}

	@Override
	public int getContainerStateId(Object container) {
		return ((Container) container).getStateId();
	}

	@Override
	public void loadParticles() {
		for (Entry<ResourceKey<Particle<?>>, Particle<?>> s : IRegistry.ab.d())
			me.devtec.theapi.bukkit.game.particles.Particle.identifier.put(s.getKey().a().getKey(), s.getValue());
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
