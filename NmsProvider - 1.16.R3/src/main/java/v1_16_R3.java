import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.Map.Entry;
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
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;
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
import net.minecraft.server.v1_16_R3.BiomeManager;
import net.minecraft.server.v1_16_R3.Block;
import net.minecraft.server.v1_16_R3.BlockFalling;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.BlockStateList;
import net.minecraft.server.v1_16_R3.Blocks;
import net.minecraft.server.v1_16_R3.ChatClickable;
import net.minecraft.server.v1_16_R3.ChatClickable.EnumClickAction;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.ChatHexColor;
import net.minecraft.server.v1_16_R3.ChatHoverable;
import net.minecraft.server.v1_16_R3.ChatHoverable.EnumHoverAction;
import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.ChatModifier;
import net.minecraft.server.v1_16_R3.Chunk.EnumTileEntityState;
import net.minecraft.server.v1_16_R3.ChunkSection;
import net.minecraft.server.v1_16_R3.Container;
import net.minecraft.server.v1_16_R3.ContainerAccess;
import net.minecraft.server.v1_16_R3.ContainerAnvil;
import net.minecraft.server.v1_16_R3.Containers;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EnumChatFormat;
import net.minecraft.server.v1_16_R3.EnumGamemode;
import net.minecraft.server.v1_16_R3.IBlockData;
import net.minecraft.server.v1_16_R3.IBlockDataHolder;
import net.minecraft.server.v1_16_R3.IBlockState;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.IRegistry;
import net.minecraft.server.v1_16_R3.IScoreboardCriteria.EnumScoreboardHealthDisplay;
import net.minecraft.server.v1_16_R3.ITileEntity;
import net.minecraft.server.v1_16_R3.InventoryClickType;
import net.minecraft.server.v1_16_R3.Item;
import net.minecraft.server.v1_16_R3.MathHelper;
import net.minecraft.server.v1_16_R3.MinecraftKey;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.MojangsonParser;
import net.minecraft.server.v1_16_R3.NBTBase;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NetworkManager;
import net.minecraft.server.v1_16_R3.NonNullList;
import net.minecraft.server.v1_16_R3.PacketPlayInWindowClick;
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockChange;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import net.minecraft.server.v1_16_R3.PacketPlayOutCloseWindow;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutExperience;
import net.minecraft.server.v1_16_R3.PacketPlayOutHeldItemSlot;
import net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R3.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_16_R3.PacketPlayOutPosition;
import net.minecraft.server.v1_16_R3.PacketPlayOutResourcePackSend;
import net.minecraft.server.v1_16_R3.PacketPlayOutRespawn;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_16_R3.PacketPlayOutSetSlot;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_16_R3.PacketPlayOutTransaction;
import net.minecraft.server.v1_16_R3.PacketStatusOutServerInfo;
import net.minecraft.server.v1_16_R3.Particle;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.minecraft.server.v1_16_R3.PlayerInventory;
import net.minecraft.server.v1_16_R3.ResourceKey;
import net.minecraft.server.v1_16_R3.ScoreboardObjective;
import net.minecraft.server.v1_16_R3.ScoreboardServer;
import net.minecraft.server.v1_16_R3.ServerPing;
import net.minecraft.server.v1_16_R3.ServerPing.ServerData;
import net.minecraft.server.v1_16_R3.ServerPing.ServerPingPlayerSample;
import net.minecraft.server.v1_16_R3.Slot;
import net.minecraft.server.v1_16_R3.TileEntity;
import net.minecraft.server.v1_16_R3.WorldServer;

public class v1_16_R3 implements NmsProvider {
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
		return ((net.minecraft.server.v1_16_R3.Entity) entity).getId();
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
		return ((net.minecraft.server.v1_16_R3.ItemStack) asNMSItem(itemStack)).getOrCreateTag();
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
		net.minecraft.server.v1_16_R3.ItemStack i = (net.minecraft.server.v1_16_R3.ItemStack) asNMSItem(stack);
		i.setTag((NBTTagCompound) nbt);
		return asBukkitItem(i);
	}

	@Override
	public Object asNMSItem(ItemStack stack) {
		if (stack == null)
			return net.minecraft.server.v1_16_R3.ItemStack.b;
		return CraftItemStack.asNMSCopy(stack);
	}

	@Override
	public ItemStack asBukkitItem(Object stack) {
		return CraftItemStack.asBukkitCopy(stack == null ? net.minecraft.server.v1_16_R3.ItemStack.b : (net.minecraft.server.v1_16_R3.ItemStack) stack);
	}

	@Override
	public Object packetOpenWindow(int id, String legacy, int size, Component title) {
		Containers<?> windowType = Containers.GENERIC_9X1;
		switch (size) {
		case 0: {
			windowType = Containers.ANVIL;
			break;
		}
		case 18: {
			windowType = Containers.GENERIC_9X2;
			break;
		}
		case 27: {
			windowType = Containers.GENERIC_9X3;
			break;
		}
		case 36: {
			windowType = Containers.GENERIC_9X4;
			break;
		}
		case 45: {
			windowType = Containers.GENERIC_9X5;
			break;
		}
		case 54: {
			windowType = Containers.GENERIC_9X6;
			break;
		}
		}
		return new PacketPlayOutOpenWindow(id, windowType, (IChatBaseComponent) this.toIChatBaseComponent(title));
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
	public Object packetSetSlot(int container, int slot, int statusId, Object itemStack) {
		return new PacketPlayOutSetSlot(container, slot, (net.minecraft.server.v1_16_R3.ItemStack) (itemStack == null ? asNMSItem(null) : itemStack));
	}

	public Object packetSetSlot(int container, int slot, Object itemStack) {
		return this.packetSetSlot(container, slot, 0, itemStack);
	}

	@Override
	public Object packetEntityMetadata(int entityId, Object dataWatcher, boolean bal) {
		return new PacketPlayOutEntityMetadata(entityId, (net.minecraft.server.v1_16_R3.DataWatcher) dataWatcher, bal);
	}

	@Override
	public Object packetEntityDestroy(int... ids) {
		return new PacketPlayOutEntityDestroy(ids);
	}

	@Override
	public Object packetSpawnEntity(Object entity, int id) {
		return new PacketPlayOutSpawnEntity((net.minecraft.server.v1_16_R3.Entity) entity, id);
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
		packet.header = (IChatBaseComponent) this.toIChatBaseComponent(header);
		packet.footer = (IChatBaseComponent) this.toIChatBaseComponent(footer);
		return packet;
	}

	@Override
	public Object packetBlockChange(int x, int y, int z, Object iblockdata, int data) {
		return new PacketPlayOutBlockChange(new BlockPosition(x, y, z), iblockdata == null ? Blocks.AIR.getBlockData() : (IBlockData) iblockdata);
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
		return new PacketPlayOutScoreboardScore((ScoreboardServer.Action) getScoreboardAction(action), player, line, score);
	}

	@Override
	public Object packetTitle(TitleAction action, Component text, int fadeIn, int stay, int fadeOut) {
		return new PacketPlayOutTitle(EnumTitleAction.valueOf(action.name()), (IChatBaseComponent) this.toIChatBaseComponent(text), fadeIn, stay, fadeOut);
	}

	@Override
	public Object packetChat(ChatType type, Object chatBase, UUID uuid) {
		return new PacketPlayOutChat((IChatBaseComponent) chatBase, ChatMessageType.valueOf(type.name()), uuid);
	}

	@Override
	public Object packetChat(ChatType type, Component text, UUID uuid) {
		return this.packetChat(type, this.toIChatBaseComponent(text), uuid);
	}

	@Override
	public void postToMainThread(Runnable runnable) {
		v1_16_R3.server.executeSync(runnable);
	}

	@Override
	public Object getMinecraftServer() {
		return v1_16_R3.server;
	}

	@Override
	public Thread getServerThread() {
		return v1_16_R3.server.serverThread;
	}

	@Override
	public double[] getServerTPS() {
		return v1_16_R3.server.recentTps;
	}

	private IChatBaseComponent convert(Component c) {
		ChatComponentText current = new ChatComponentText(c.getText());
		ChatModifier modif = current.getChatModifier();
		if (c.getColor() != null && !c.getColor().isEmpty())
			if (c.getColor().startsWith("#"))
				modif = modif.setColor(ChatHexColor.a(c.getColor()));
			else
				modif = modif.setColor(EnumChatFormat.b(c.getColor()));
		if (c.getClickEvent() != null)
			modif = modif.setChatClickable(new ChatClickable(EnumClickAction.valueOf(c.getClickEvent().getAction().name()), c.getClickEvent().getValue()));
		if (c.getHoverEvent() != null)
			switch (c.getHoverEvent().getAction()) {
			case SHOW_ENTITY:
				try {
					ComponentEntity compoundTag = (ComponentEntity) c.getHoverEvent().getValue();
					IChatBaseComponent component = compoundTag.getName() == null ? null : (IChatBaseComponent) toIChatBaseComponent(compoundTag.getName());
					EntityTypes<?> entityType = IRegistry.ENTITY_TYPE.get(new MinecraftKey(compoundTag.getType()));
					modif = modif.setChatHoverable(new ChatHoverable(EnumHoverAction.SHOW_ENTITY, new ChatHoverable.b(entityType, compoundTag.getId(), component)));
				} catch (Exception ignored) {
				}
				break;
			case SHOW_ITEM:
				try {
					ComponentItem compoundTag = (ComponentItem) c.getHoverEvent().getValue();
					net.minecraft.server.v1_16_R3.ItemStack stack = new net.minecraft.server.v1_16_R3.ItemStack(
							CraftMagicNumbers.getItem(XMaterial.matchXMaterial(compoundTag.getId()).orElse(XMaterial.AIR).parseMaterial()), compoundTag.getCount());
					if (compoundTag.getNbt() != null)
						stack.setTag((NBTTagCompound) parseNBT(compoundTag.getNbt()));
					modif = modif.setChatHoverable(new ChatHoverable(EnumHoverAction.SHOW_ITEM, new ChatHoverable.c(stack)));
				} catch (Exception ignored) {
				}
				break;
			default:
				modif = modif.setChatHoverable(new ChatHoverable(EnumHoverAction.SHOW_TEXT, (IChatBaseComponent) this.toIChatBaseComponent(c.getHoverEvent().getValue())));
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

	private static final Field itemField = Ref.field(ChatHoverable.c.class, "a");
	private static final Field countField = Ref.field(ChatHoverable.c.class, "b");
	private static final Field tagField = Ref.field(ChatHoverable.c.class, "c");

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
				ChatHoverable.b hover = modif.getHoverEvent().a(EnumHoverAction.SHOW_ENTITY);
				ComponentEntity compEntity = new ComponentEntity(hover.a.i().getKey(), hover.b);
				if (hover.c != null)
					compEntity.setName(fromIChatBaseComponent(hover.c));
				comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, compEntity));
				break;
			}
			case SHOW_ITEM: {
				ChatHoverable.c hover = modif.getHoverEvent().a(EnumHoverAction.SHOW_ITEM);
				Ref.get(hover, itemField);
				ComponentItem compEntity = new ComponentItem(CraftMagicNumbers.getMaterial((Item) Ref.get(hover, itemField)).name(), (int) Ref.get(hover, countField));
				Object tag = Ref.get(hover, tagField);
				if (tag != null)
					compEntity.setNbt(tag.toString());
				comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, compEntity));
				break;
			}
			default:
				IChatBaseComponent hover = modif.getHoverEvent().a(EnumHoverAction.SHOW_TEXT);
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
			stateString.append(data.getStateMap().entrySet().stream().map(IBlockDataHolder.STATE_TO_VALUE).collect(Collectors.joining(",")));
			stateString.append(']');
		}
		return stateString.toString();
	}

	@Override
	public Object toIBlockData(BlockDataStorage material) {
		if (material == null || material.getType() == null || material.getType() == Material.AIR)
			return Blocks.AIR.getBlockData();
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

	private IBlockData readArgument(Block block, BlockDataStorage material) {
		IBlockData ib = block.getBlockData();
		return writeData(ib, ib.getBlock().getStates(), material.getData());
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
		Item item = CraftMagicNumbers.getItem(material.getType(), ParseUtils.getShort(material.getData()));
		return CraftItemStack.asBukkitCopy(item.createItemStack());
	}

	@Override
	public Object getChunk(World world, int x, int z) {
		return ((CraftChunk) world.getChunkAt(x, z)).getHandle();
	}

	@Override
	public void setBlock(Object objChunk, int x, int y, int z, Object IblockData, int data) {
		net.minecraft.server.v1_16_R3.Chunk chunk = (net.minecraft.server.v1_16_R3.Chunk) objChunk;
		if (y < 0)
			return;
		ChunkSection sc = chunk.getSections()[y >> 4];
		if (sc == null)
			return;
		BlockPosition pos = new BlockPosition(x, y, z);

		IBlockData iblock = IblockData == null ? Blocks.AIR.getBlockData() : (IBlockData) IblockData;

		boolean onlyModifyState = iblock.getBlock() instanceof ITileEntity;

		// REMOVE TILE ENTITY IF NOT SAME TYPE
		TileEntity ent = chunk.tileEntities.get(pos);
		if (ent != null) {
			boolean shouldSkip = true;
			if (!onlyModifyState)
				shouldSkip = false;
			else if (!ent.getBlock().getBlock().getClass().equals(iblock.getBlock().getClass())) {
				shouldSkip = false;
				onlyModifyState = false;
			}
			if (!shouldSkip)
				chunk.removeTileEntity(pos);
		}

		IBlockData old = sc.setType(x & 15, y & 15, z & 15, iblock, false);

		// ADD TILE ENTITY
		if (iblock.getBlock() instanceof ITileEntity && !onlyModifyState) {
			ent = ((ITileEntity) iblock.getBlock()).createTile(chunk);
			chunk.tileEntities.put(pos, ent);
			ent.setLocation(chunk.world, pos);
			Ref.set(ent, "c", iblock);
			Object packet = ent.getUpdatePacket();
			BukkitLoader.getPacketHandler().send(chunk.world.getWorld().getPlayers(), packet);
		}

		// MARK CHUNK TO SAVE
		chunk.setNeedsSaving(true);

		// POI
		chunk.world.a(pos, old, iblock);
	}

	@Override
	public void updatePhysics(Object objChunk, int x, int y, int z, Object iblockdata) {
		net.minecraft.server.v1_16_R3.Chunk chunk = (net.minecraft.server.v1_16_R3.Chunk) objChunk;

		BlockPosition blockPos = new BlockPosition(x, y, z);

		doPhysicsAround(chunk.world, blockPos, ((IBlockData) iblockdata).getBlock());
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
		net.minecraft.server.v1_16_R3.Chunk c = (net.minecraft.server.v1_16_R3.Chunk) chunk;
		c.world.getChunkProvider().getLightEngine().a(new BlockPosition(x, y, z));
	}

	@Override
	public Object getBlock(Object objChunk, int x, int y, int z) {
		net.minecraft.server.v1_16_R3.Chunk chunk = (net.minecraft.server.v1_16_R3.Chunk) objChunk;
		if (y < 0)
			return Blocks.AIR.getBlockData();
		ChunkSection sc = chunk.getSections()[y >> 4];
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
		net.minecraft.server.v1_16_R3.Chunk chunk = (net.minecraft.server.v1_16_R3.Chunk) objChunk;
		return chunk.a(new BlockPosition(x, y, z), EnumTileEntityState.IMMEDIATE).save(new NBTTagCompound()).toString();
	}

	@Override
	public void setNBTToTile(Object objChunk, int x, int y, int z, String nbt) {
		net.minecraft.server.v1_16_R3.Chunk chunk = (net.minecraft.server.v1_16_R3.Chunk) objChunk;
		TileEntity ent = chunk.a(new BlockPosition(x, y, z), EnumTileEntityState.IMMEDIATE);
		NBTTagCompound parsedNbt = (NBTTagCompound) parseNBT(nbt);
		parsedNbt.setInt("x", x);
		parsedNbt.setInt("y", y);
		parsedNbt.setInt("z", z);
		ent.load(ent.getBlock(), parsedNbt);
		Object packet = ent.getUpdatePacket();
		BukkitLoader.getPacketHandler().send(chunk.world.getWorld().getPlayers(), packet);
	}

	@Override
	public boolean isTileEntity(Object objChunk, int x, int y, int z) {
		net.minecraft.server.v1_16_R3.Chunk chunk = (net.minecraft.server.v1_16_R3.Chunk) objChunk;
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
	public Object toIBlockData(Object data) {
		return ((CraftBlockData) data).getState();
	}

	@Override
	public Object toIBlockData(BlockState state) {
		return CraftMagicNumbers.getBlock(state.getType(), state.getRawData());
	}

	@Override
	public Chunk toBukkitChunk(Object nmsChunk) {
		return ((net.minecraft.server.v1_16_R3.Chunk) nmsChunk).bukkitChunk;
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
		((Container) container).setItem(slot, (net.minecraft.server.v1_16_R3.ItemStack) item);
	}

	@Override
	public void setGUITitle(Player player, Object container, String legacy, int size, Component title) {
		int id = ((Container) container).windowId;
		BukkitLoader.getPacketHandler().send(player, packetOpenWindow(id, legacy, size, title));
		net.minecraft.server.v1_16_R3.ItemStack carried = ((CraftPlayer) player).getHandle().inventory.getCarried();
		if (!carried.isEmpty())
			BukkitLoader.getPacketHandler().send(player, new PacketPlayOutSetSlot(id, -1, carried));
		int slot = 0;
		for (net.minecraft.server.v1_16_R3.ItemStack item : ((Container) container).b()) {
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
			ContainerAnvil container = new ContainerAnvil(((CraftPlayer) player).getHandle().nextContainerCounter(), ((CraftPlayer) player).getHandle().inventory, new ContainerAccess() {

				@Override
				public <T> Optional<T> a(BiFunction<net.minecraft.server.v1_16_R3.World, BlockPosition, T> getter) {
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
					container.getSlot(slot++).set((net.minecraft.server.v1_16_R3.ItemStack) asNMSItem(stack));
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

	static final BlockPosition zero = new BlockPosition(0, 0, 0);

	public Object createAnvilContainer(Inventory inv, Player player) {
		ContainerAnvil container = new ContainerAnvil(((CraftPlayer) player).getHandle().nextContainerCounter(), ((CraftPlayer) player).getHandle().inventory,
				ContainerAccess.at(((CraftPlayer) player).getHandle().world, v1_16_R3.zero));
		for (int i = 0; i < 2; ++i)
			container.setItem(i, (net.minecraft.server.v1_16_R3.ItemStack) asNMSItem(inv.getItem(i)));
		return container;
	}

	@Override
	public String getAnvilRenameText(Object anvil) {
		return ((ContainerAnvil) anvil).renameText;
	}

	public static int c(final int quickCraftData) {
		return quickCraftData >> 2 & 3;
	}

	public static int d(final int quickCraftData) {
		return quickCraftData & 3;
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
		EntityPlayer nPlayer = ((CraftPlayer) player).getHandle();

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
			newItem = slot <= -1 ? new ItemStack(Material.AIR) : asBukkitItem(packet.f());
			oldItem = slot <= -1 ? new ItemStack(Material.AIR) : asBukkitItem(packet.f());
			break;
		}

		if (oldItem.getType() == Material.AIR && newItem.getType() == Material.AIR)
			return true;

		boolean cancel = false;
		int gameSlot = slot > gui.size() - 1 ? InventoryUtils.convertToPlayerInvSlot(slot - gui.size()) : slot;

		ClickType clickType = InventoryUtils.buildClick(type == InventoryClickType.QUICK_CRAFT ? 1 : type == InventoryClickType.QUICK_MOVE ? 2 : 0, mouseClick);
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
				postToMainThread(() -> processEvent(c, type, gui, player, slot, gameSlot, newItemFinal, oldItem, packet, mouseClick, clickType, nPlayer));
			} else
				processEvent(c, type, gui, player, slot, gameSlot, newItem, oldItem, packet, mouseClick, clickType, nPlayer);
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
			BukkitLoader.getPacketHandler().send(player, packetSetSlot(id, slot, 0, c.getSlot(packet.c()).getItem()));
			break;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private void processEvent(Container c, InventoryClickType type, HolderGUI gui, Player player, int slot, int gameSlot, ItemStack newItem, ItemStack oldItem, PacketPlayInWindowClick packet,
			int mouseClick, ClickType clickType, EntityPlayer nPlayer) {
		net.minecraft.server.v1_16_R3.ItemStack result;
        if (type == InventoryClickType.QUICK_MOVE) {
            ItemStack[] contents = slot < gui.size() ? player.getInventory().getStorageContents() : gui.getInventory().getStorageContents();
            boolean interactWithResultSlot = false;
            if (gui instanceof AnvilGUI && slot < gui.size() && slot == 2)
                if (c.getSlot(2).isAllowed(nPlayer))
                    interactWithResultSlot = true;
                else
                    return;
            Pair pairResult = slot < gui.size()
                    ? InventoryUtils.shift(slot, player, gui, clickType, gui instanceof AnvilGUI && slot != 2 ? DestinationType.PLAYER_FROM_ANVIL : DestinationType.PLAYER, null, contents, oldItem)
                    : InventoryUtils.shift(slot, player, gui, clickType, DestinationType.GUI, gui.getNotInterableSlots(player), contents, oldItem);
            Map<Integer, ItemStack> modified = (Map<Integer, ItemStack>) pairResult.getValue();
            int remaining = (int) pairResult.getKey();

            if (!modified.isEmpty())
                if (slot < gui.size()) {
                    for (Entry<Integer, ItemStack> modif : modified.entrySet())
                        nPlayer.inventory.setItem(modif.getKey(), (net.minecraft.server.v1_16_R3.ItemStack) asNMSItem(modif.getValue()));
                    if (remaining == 0) {
                        c.getSlot(gameSlot).set((net.minecraft.server.v1_16_R3.ItemStack) asNMSItem(null));
						if(gui instanceof AnvilGUI)
							gui.getInventory().setItem(gameSlot, null);
                        if (interactWithResultSlot) {
                            c.getSlot(0).set((net.minecraft.server.v1_16_R3.ItemStack) asNMSItem(null));
                            c.getSlot(1).set((net.minecraft.server.v1_16_R3.ItemStack) asNMSItem(null));
							if(gui instanceof AnvilGUI) {
								gui.getInventory().setItem(0, null);
								gui.getInventory().setItem(1, null);
							}
                        }
                    } else {
                        newItem.setAmount(remaining);
                        c.getSlot(gameSlot).set((net.minecraft.server.v1_16_R3.ItemStack) asNMSItem(newItem));
						if(gui instanceof AnvilGUI)
							gui.getInventory().setItem(gameSlot, newItem);
                    }
                } else {
                    for (Entry<Integer, ItemStack> modif : modified.entrySet())
                        c.getSlot(modif.getKey()).set((net.minecraft.server.v1_16_R3.ItemStack) asNMSItem(modif.getValue())); // Visual & Nms side
                    // Plugin & Bukkit side
                    gui.getInventory().setStorageContents(contents);
                    if (remaining == 0)
                        nPlayer.inventory.setItem(gameSlot, (net.minecraft.server.v1_16_R3.ItemStack) asNMSItem(null));
                    else {
                        newItem.setAmount(remaining);
                        nPlayer.inventory.setItem(gameSlot, (net.minecraft.server.v1_16_R3.ItemStack) asNMSItem(newItem));
                    }
                }
            return;
        } else {
            result = processClick(gui, gui.getNotInterableSlots(player), c, slot, mouseClick, type, nPlayer);
        }
		postToMainThread(() -> {
			if (net.minecraft.server.v1_16_R3.ItemStack.matches(packet.f(), result)) {
				nPlayer.playerConnection.sendPacket(new PacketPlayOutTransaction(packet.b(), packet.e(), true));
				nPlayer.e = true;
				c.c();
				nPlayer.broadcastCarriedItem();
				nPlayer.e = false;
			} else {
				((Map<Integer, Short>) Ref.get(nPlayer.playerConnection, "k")).put(c.windowId, packet.e());
				nPlayer.playerConnection.sendPacket(new PacketPlayOutTransaction(packet.b(), packet.e(), false));
				c.a(nPlayer, false);
				NonNullList<net.minecraft.server.v1_16_R3.ItemStack> nonnulllist1 = NonNullList.a();
				for (Slot element : c.slots) {
					net.minecraft.server.v1_16_R3.ItemStack cursor = element.getItem();
					nonnulllist1.add(cursor.isEmpty() ? net.minecraft.server.v1_16_R3.ItemStack.b : cursor);
				}
				nPlayer.a(c, nonnulllist1);
			}
		});
	}

	private final Method addAmount = Ref.method(Slot.class, "b", int.class);

	@SuppressWarnings("unchecked")
	private net.minecraft.server.v1_16_R3.ItemStack processClick(HolderGUI gui, List<Integer> ignoredSlots, Container container, int slotIndex, int button, InventoryClickType actionType,
			EntityPlayer player) {
		net.minecraft.server.v1_16_R3.ItemStack result = net.minecraft.server.v1_16_R3.ItemStack.b;

		if (actionType == InventoryClickType.QUICK_CRAFT)
			processDragMove(gui, container, player, slotIndex, button);
		else {
			int u = (int) Ref.get(container, containerU);
			Set<Slot> mod = (Set<Slot>) Ref.get(container, containerV);
			if (u != 0) {
				Ref.set(container, containerU, 0);
				mod.clear();
			} else if (actionType == InventoryClickType.PICKUP && (button == 0 || button == 1)) {
				if (slotIndex == -999) {
					if (!player.inventory.getCarried().isEmpty())
						if (button == 0) {
							net.minecraft.server.v1_16_R3.ItemStack carried = player.inventory.getCarried();
							player.inventory.setCarried(net.minecraft.server.v1_16_R3.ItemStack.b);
							postToMainThread(() -> player.drop(carried, true));
						} else
							postToMainThread(() -> player.drop(player.inventory.getCarried().cloneAndSubtract(1), true));
				} else {
					if (slotIndex < 0)
						return net.minecraft.server.v1_16_R3.ItemStack.b;

					PlayerInventory playerinventory = player.inventory;

					int k1;
					net.minecraft.server.v1_16_R3.ItemStack itemstack1;
					net.minecraft.server.v1_16_R3.ItemStack itemstack2;
					Slot slot2 = container.slots.get(slotIndex);
					if (slot2 != null) {
						itemstack2 = slot2.getItem();
						itemstack1 = playerinventory.getCarried();
						if (!itemstack2.isEmpty())
							result = itemstack2.cloneItemStack();
						if (itemstack2.isEmpty()) {
							if (!itemstack1.isEmpty() && slot2.isAllowed(itemstack1)) {
								k1 = button == 0 ? itemstack1.getCount() : 1;
								if (k1 > slot2.getMaxStackSize(itemstack1))
									k1 = slot2.getMaxStackSize(itemstack1);

								slot2.set(itemstack1.cloneAndSubtract(k1));
							}
						} else if (slot2.isAllowed(player))
							if (itemstack1.isEmpty()) {
								if (itemstack2.isEmpty()) {
									slot2.set(net.minecraft.server.v1_16_R3.ItemStack.b);
									playerinventory.setCarried(net.minecraft.server.v1_16_R3.ItemStack.b);
								} else {
									k1 = button == 0 ? itemstack2.getCount() : (itemstack2.getCount() + 1) / 2;
									playerinventory.setCarried(slot2.a(k1));
									if (itemstack2.isEmpty())
										slot2.set(net.minecraft.server.v1_16_R3.ItemStack.b);

									slot2.a(player, playerinventory.getCarried());
								}
							} else if (slot2.isAllowed(itemstack1)) {
								if (Container.a(itemstack2, itemstack1)) {
									k1 = button == 0 ? itemstack1.getCount() : 1;
									if (k1 > slot2.getMaxStackSize(itemstack1) - itemstack2.getCount())
										k1 = slot2.getMaxStackSize(itemstack1) - itemstack2.getCount();

									if (k1 > itemstack1.getMaxStackSize() - itemstack2.getCount())
										k1 = itemstack1.getMaxStackSize() - itemstack2.getCount();

									itemstack1.subtract(k1);
									itemstack2.add(k1);
								} else if (itemstack1.getCount() <= slot2.getMaxStackSize(itemstack1)) {
									slot2.set(itemstack1);
									playerinventory.setCarried(itemstack2);
								}
							} else if (itemstack1.getMaxStackSize() > 1 && Container.a(itemstack2, itemstack1) && !itemstack2.isEmpty()) {
								k1 = itemstack2.getCount();
								if (k1 + itemstack1.getCount() <= itemstack1.getMaxStackSize()) {
									itemstack1.add(k1);
									itemstack2 = slot2.a(k1);
									if (itemstack2.isEmpty())
										slot2.set(net.minecraft.server.v1_16_R3.ItemStack.b);

									slot2.a(player, playerinventory.getCarried());
								}
							}

						slot2.d();
						if (slot2.getMaxStackSize() != 64) {
							BukkitLoader.getPacketHandler().send(player.getBukkitEntity(), BukkitLoader.getNmsProvider().packetSetSlot(container.windowId, slot2.rawSlotIndex, 0, slot2.getItem()));
							if (container.getBukkitView().getType() == InventoryType.WORKBENCH || container.getBukkitView().getType() == InventoryType.CRAFTING)
								BukkitLoader.getPacketHandler().send(player.getBukkitEntity(), BukkitLoader.getNmsProvider().packetSetSlot(container.windowId, 0, 0, container.getSlot(0).getItem()));
						}
					}
					if(gui instanceof AnvilGUI && slotIndex < gui.size())
						gui.getInventory().setItem(slotIndex, asBukkitItem(slot2.getItem()));
				}
			} else if (actionType == InventoryClickType.SWAP) {
				if (slotIndex < 0)
					return result;
				PlayerInventory playerinventory = player.inventory;
				Slot slot3 = container.getSlot(slotIndex);
				net.minecraft.server.v1_16_R3.ItemStack itemstack2 = playerinventory.getItem(button);
				net.minecraft.server.v1_16_R3.ItemStack itemstack = slot3.getItem();
				if (!itemstack2.isEmpty() || !itemstack.isEmpty())
					if (itemstack2.isEmpty()) {
						if (slot3.isAllowed(player)) {
							playerinventory.setItem(button, itemstack);
							Ref.invoke(slot3, addAmount, itemstack.getCount());
							slot3.set(net.minecraft.server.v1_16_R3.ItemStack.b);
							slot3.a(player, itemstack);
						}
					} else if (itemstack.isEmpty()) {
						if (slot3.isAllowed(itemstack2)) {
							int j2 = slot3.getMaxStackSize(itemstack2);
							if (itemstack2.getCount() > j2)
								slot3.set(itemstack2.cloneAndSubtract(j2));
							else {
								playerinventory.setItem(button, net.minecraft.server.v1_16_R3.ItemStack.b);
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
				if(gui instanceof AnvilGUI && slotIndex < gui.size())
					gui.getInventory().setItem(slotIndex, asBukkitItem(slot3.getItem()));
			} else if (actionType == InventoryClickType.CLONE && player.abilities.canInstantlyBuild && player.inventory.getCarried().isEmpty() && slotIndex >= 0) {
				Slot slot3 = container.getSlot(slotIndex);
				if (slot3.hasItem()) {
					net.minecraft.server.v1_16_R3.ItemStack itemstack2 = slot3.getItem();

					net.minecraft.server.v1_16_R3.ItemStack stack = itemstack2.cloneItemStack();
					stack.setCount(itemstack2.getCount());
					player.inventory.setCarried(stack);
				}
			} else if (actionType == InventoryClickType.THROW && player.inventory.getCarried().isEmpty() && slotIndex >= 0) {
				Slot slot2 = container.getSlot(slotIndex);
				if (slot2 != null && slot2.hasItem() && slot2.isAllowed(player)) {
					net.minecraft.server.v1_16_R3.ItemStack itemstack2 = slot2.a(button == 0 ? 1 : slot2.getItem().getCount());
					slot2.a(player, itemstack2);
					if(gui instanceof AnvilGUI && slotIndex < gui.size())
						gui.getInventory().setItem(slotIndex, asBukkitItem(itemstack2));
					postToMainThread(() -> player.drop(itemstack2, true));
				}
			} else if (actionType == InventoryClickType.PICKUP_ALL && slotIndex >= 0) {
				Slot slot2 = container.slots.get(slotIndex);
				net.minecraft.server.v1_16_R3.ItemStack itemstack1 = player.inventory.getCarried();
				if (!itemstack1.isEmpty() && (!slot2.hasItem() || !slot2.isAllowed(player))) {
					List<Integer> ignoreSlots = ignoredSlots == null ? Collections.emptyList() : ignoredSlots;
					List<Integer> corruptedSlots = ignoredSlots == null ? Collections.emptyList() : new ArrayList<>();
					Map<Integer, ItemStack> modifiedSlots = new HashMap<>();
					Map<Integer, ItemStack> modifiedSlotsPlayerInv = new HashMap<>();

					int l = button == 0 ? 0 : container.slots.size() - 1;
					int i2 = button == 0 ? 1 : -1;

					for (int l1 = 0; l1 < 2; ++l1)
						for (int j2 = l; j2 >= 0 && j2 < container.slots.size() && itemstack1.getCount() < itemstack1.getMaxStackSize(); j2 += i2) {
							Slot slot3 = container.slots.get(j2);
							if (slot3.hasItem() && Container.a(slot3, itemstack1, true) && slot3.isAllowed(player) && container.a(itemstack1, slot3)) {
								net.minecraft.server.v1_16_R3.ItemStack itemstack3 = slot3.getItem();
								if (l1 != 0 || itemstack3.getCount() != itemstack3.getMaxStackSize()) {
									if (j2 < gui.size() && ignoreSlots.contains(j2)) {
										corruptedSlots.add(j2);
										continue;
									}
									int count = Math.min(itemstack1.getMaxStackSize() - itemstack1.getCount(), itemstack3.getCount());
									net.minecraft.server.v1_16_R3.ItemStack itemstack6 = slot3.a(count);
									itemstack1.add(count);
									if (itemstack6.isEmpty())
										slot3.set(net.minecraft.server.v1_16_R3.ItemStack.b);
									slot3.a(player, itemstack6);
									int gameSlot = j2 > gui.size() - 1 ? InventoryUtils.convertToPlayerInvSlot(j2 - gui.size()) : j2;
									ItemStack bukkitStack = asBukkitItem(slot3.getItem());
									if (j2 < gui.size()) {
										modifiedSlots.put(gameSlot, bukkitStack);
										if(gui instanceof AnvilGUI)
											gui.getInventory().setItem(gameSlot, bukkitStack);
									} else
										modifiedSlotsPlayerInv.put(gameSlot, bukkitStack);
								}
							}
						}
					if (slotIndex < gui.size())
						modifiedSlots.put(slotIndex, new ItemStack(Material.AIR));
					else
						modifiedSlotsPlayerInv.put(InventoryUtils.convertToPlayerInvSlot(slotIndex - gui.size()), new ItemStack(Material.AIR));
                    gui.onMultipleIteract(player.getBukkitEntity(), modifiedSlots, modifiedSlotsPlayerInv);
					for (int s : corruptedSlots)
						BukkitLoader.getPacketHandler().send(player.getBukkitEntity(), BukkitLoader.getNmsProvider().packetSetSlot(BukkitLoader.getNmsProvider().getContainerId(container), s,
								getContainerStateId(container), BukkitLoader.getNmsProvider().getSlotItem(container, s)));
				}
			}
		}
		return result;
	}

	private final Field containerU = Ref.field(Container.class, "h");
    private final Field containerV = Ref.field(Container.class, "i");
    private final Field containerT = Ref.field(Container.class, "dragType");

	@SuppressWarnings("unchecked")
	private void processDragMove(HolderGUI gui, Container container, EntityPlayer player, int slot, int mouseClick) {
		int previous = (int) Ref.get(container, containerU);
		int u = d(mouseClick);
		Set<Slot> mod = (Set<Slot>) Ref.get(container, containerV);
		if ((previous != 1 || u != 2) && previous != u || player.inventory.getCarried().isEmpty()) {
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
				final net.minecraft.server.v1_16_R3.ItemStack itemstack = player.inventory.getCarried();
				if (Container.a(bslot, itemstack, true) && bslot.isAllowed(itemstack) && (t == 2 || itemstack.getCount() > mod.size()) && container.b(bslot))
					mod.add(bslot);
				break;
			}
			case 2:
				if (!mod.isEmpty()) {
					final net.minecraft.server.v1_16_R3.ItemStack itemstack2 = player.inventory.getCarried().cloneItemStack();
					if (itemstack2.isEmpty()) {
						mod.clear();
						Ref.set(container, containerU, 0);
						return;
					}
					int t = (int) Ref.get(container, containerT);
					int l = player.inventory.getCarried().getCount();
					final Map<Integer, net.minecraft.server.v1_16_R3.ItemStack> draggedSlots = new HashMap<>();
					for (Slot slot2 : mod) {
						final net.minecraft.server.v1_16_R3.ItemStack itemstack3 = player.inventory.getCarried();
						if (slot2 != null && Container.a(slot2, itemstack3, true) && slot2.isAllowed(itemstack3) && (t == 2 || itemstack3.getCount() >= mod.size()) && container.b(slot2)) {

							final int j1 = slot2.hasItem() ? slot2.getItem().getCount() : 0;
							final int k1 = Math.min(itemstack2.getMaxStackSize(), slot2.getMaxStackSize(itemstack2));
							final int l2 = Math.min(a(mod, t, itemstack2) + j1, k1);
							l -= l2 - j1;
							net.minecraft.server.v1_16_R3.ItemStack stack = itemstack2.cloneItemStack();
							stack.setCount(l2);
							draggedSlots.put(slot2.rawSlotIndex, stack);
						}
					}
					final InventoryView view = container.getBukkitView();
					final org.bukkit.inventory.ItemStack newcursor = CraftItemStack.asCraftMirror(itemstack2);
					newcursor.setAmount(l);
					final Map<Integer, org.bukkit.inventory.ItemStack> guiSlots = new HashMap<>();
					final Map<Integer, org.bukkit.inventory.ItemStack> playerSlots = new HashMap<>();
					for (final Entry<Integer, net.minecraft.server.v1_16_R3.ItemStack> ditem : draggedSlots.entrySet())
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
					player.inventory.setCarried(CraftItemStack.asNMSCopy(newcursor));
					if (!guiSlots.isEmpty() || !playerSlots.isEmpty())
						gui.onMultipleIteract(player.getBukkitEntity(), guiSlots, playerSlots);
					for (final Entry<Integer, net.minecraft.server.v1_16_R3.ItemStack> dslot : draggedSlots.entrySet())
						view.setItem(dslot.getKey(), CraftItemStack.asBukkitCopy(dslot.getValue()));
					if (player.inventory.getCarried() != null)
						player.updateInventory(container);
				}
				mod.clear();
                default:
				mod.clear();
				u = 0;
				break;
			}
		Ref.set(container, containerU, u);
	}

	public static int a(Set<Slot> slots, int mode, net.minecraft.server.v1_16_R3.ItemStack stack) {
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

	static final Field field = Ref.field(PacketStatusOutServerInfo.class, "b");

	@Override
	public boolean processServerListPing(String player, Object channel, Object packet) {
		PacketStatusOutServerInfo status = (PacketStatusOutServerInfo) packet;
		ServerPing ping;
		try {
			ping = (ServerPing) v1_16_R3.field.get(status);
		} catch (Exception e) {
			return false;
		}

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
		return ((net.minecraft.server.v1_16_R3.Entity) entity).getDataWatcher();
	}

	@Override
	public int incrementStateId(Object container) {
		return 0;
	}

	@Override
	public Object packetEntityHeadRotation(Entity entity) {
		return new PacketPlayOutEntityHeadRotation((net.minecraft.server.v1_16_R3.Entity) getEntity(entity), (byte) (entity.getLocation().getYaw() * 256F / 360F));
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

	private static final Constructor<?> infoData = Ref.constructor(Ref.nms("", "PacketPlayOutPlayerInfo$PlayerInfoData"), PacketPlayOutPlayerInfo.class, GameProfile.class, int.class, EnumGamemode.class,
			IChatBaseComponent.class);

	@SuppressWarnings("unchecked")
	@Override
	public Object packetPlayerInfo(PlayerInfoType type, GameProfileHandler gameProfile, int latency, GameMode gameMode, Component playerName) {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.valueOf(type.name()), Collections.emptyList());
		((List<Object>) Ref.get(packet, playerInfo))
				.add(Ref.newInstance(infoData, packet, toGameProfile(gameProfile), latency, gameMode == null ? EnumGamemode.SURVIVAL : EnumGamemode.valueOf(gameMode.name()),
						playerName == null ? toIChatBaseComponent(new Component(gameProfile.getUsername())) : toIChatBaseComponent(playerName)));
		return packet;
	}

	@Override
	public Object packetPosition(double x, double y, double z, float yaw, float pitch) {
		return new PacketPlayOutPosition(x, y, z, yaw, pitch, Collections.emptySet(), 0);
	}

	@Override
	public Object packetRespawn(Player player) {
		EntityPlayer entityPlayer = (EntityPlayer) getPlayer(player);
		WorldServer worldserver = entityPlayer.getWorldServer();
		return new PacketPlayOutRespawn(worldserver.getDimensionManager(), worldserver.getDimensionKey(), BiomeManager.a(worldserver.getSeed()), entityPlayer.playerInteractManager.getGameMode(),
				entityPlayer.playerInteractManager.c(), worldserver.isDebugWorld(), worldserver.isFlatWorld(), true);
	}

	@Override
	public String getProviderName() {
		return "1_16_R3 (1.16)";
	}

	@Override
	public int getContainerStateId(Object container) {
		return 0;
	}

	@Override
	public void loadParticles() {
		for (Entry<ResourceKey<Particle<?>>, Particle<?>> s : IRegistry.PARTICLE_TYPE.d())
			me.devtec.theapi.bukkit.game.particles.Particle.identifier.put(s.getKey().a().toString(), s.getValue());
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
