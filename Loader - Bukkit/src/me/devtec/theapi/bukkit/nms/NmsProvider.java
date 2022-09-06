package me.devtec.theapi.bukkit.nms;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.devtec.shared.components.Component;
import me.devtec.theapi.bukkit.game.BlockDataStorage;
import me.devtec.theapi.bukkit.game.Position;
import me.devtec.theapi.bukkit.gui.HolderGUI;

public interface NmsProvider {
	static final UUID serverUuid = UUID.randomUUID();

	public enum Action {
		CHANGE(0), REMOVE(1);

		int id;

		Action(int i) {
			id = i;
		}

		public int getId() {
			return id;
		}
	}

	public enum DisplayType {
		INTEGER, HEARTS
	}

	public enum TitleAction {
		ACTIONBAR, CLEAR, RESET, SUBTITLE, TITLE, TIMES
	}

	public enum PlayerInfoType {
		ADD_PLAYER, REMOVE_PLAYER, UPDATE_GAME_MODE, UPDATE_LATENCY, UPDATE_DISPLAY_NAME
	}

	public enum ChatType {
		CHAT(0), SYSTEM(1), GAME_INFO(2);

		byte id;

		ChatType(int i) {
			id = (byte) i;
		}

		public byte toByte() {
			return id;
		}
	}

	public String getProviderName();

	public Object getEntity(Entity entity);

	public Object getEntityLiving(LivingEntity entity);

	public Object getPlayer(Player player);

	public Object getWorld(World world);

	public Object getChunk(Chunk chunk);

	public Object getScoreboardAction(Action type);

	public Object getEnumScoreboardHealthDisplay(DisplayType type);

	public Object getNBT(Entity entity);

	public Object getNBT(ItemStack itemStack);

	public Object parseNBT(String json);

	public default ItemStack setNBT(ItemStack stack, String nbt) {
		return this.setNBT(stack, parseNBT(nbt));
	}

	public default ItemStack setNBT(ItemStack stack, NBTEdit nbt) {
		return this.setNBT(stack, nbt.getNBT());
	}

	public ItemStack setNBT(ItemStack stack, Object nbt);

	public Object asNMSItem(ItemStack stack);

	public ItemStack asBukkitItem(Object stack);

	public Object getDataWatcher(Entity entity);

	public Object getDataWatcher(Object entity);

	public default Object packetEntityMetadata(Entity entity) {
		return this.packetEntityMetadata(entity.getEntityId(), this.getDataWatcher(entity), true);
	}

	public default Object packetEntityMetadata(Entity entity, Object dataWatcher) {
		return this.packetEntityMetadata(entity.getEntityId(), dataWatcher, true);
	}

	public default Object packetEntityMetadata(Entity entity, Object dataWatcher, boolean bal) {
		return this.packetEntityMetadata(entity.getEntityId(), dataWatcher, bal);
	}

	public default Object packetEntityMetadata(int entityId, Object dataWatcher) {
		return this.packetEntityMetadata(entityId, dataWatcher, true);
	}

	public Object packetEntityMetadata(int entityId, Object dataWatcher, boolean bal);

	public Object packetEntityDestroy(int... ids);

	public Object packetSpawnEntity(Object entity, int id);

	public Object packetNamedEntitySpawn(Object player);

	public Object packetSpawnEntityLiving(Object entityLiving);

	public Object packetPlayerListHeaderFooter(String header, String footer);

	public Object packetBlockChange(World world, Position position);

	public Object packetBlockChange(World world, int x, int y, int z);

	public Object packetScoreboardObjective();

	public Object packetScoreboardDisplayObjective(int id, Object scoreboardObjective);

	public Object packetScoreboardTeam();

	public Object packetScoreboardScore(Action action, String player, String line, int score);

	public Object packetEntityHeadRotation(Entity entity);

	public Object packetHeldItemSlot(int slot);

	public Object packetExp(float exp, int total, int toNextLevel);

	public Object packetPlayerInfo(PlayerInfoType type, Player player);

	public Object packetResourcePackSend(String url, String hash, boolean requireRP, String prompt);

	public Object packetPosition(double x, double y, double z, float yaw, float pitch);

	public Object packetRespawn(Player player);

	public Object packetTitle(TitleAction action, String text, int fadeIn, int stay, int fadeOut);

	public default Object packetTitle(TitleAction action, String text) {
		return this.packetTitle(action, text, 20, 60, 20);
	}

	public Object packetChat(ChatType type, Object chatBase, UUID uuid);

	public default Object packetChat(ChatType type, Object chatBase) {
		return this.packetChat(type, chatBase, NmsProvider.serverUuid);
	}

	public Object packetChat(ChatType type, String text, UUID uuid);

	public default Object packetChat(ChatType type, String text) {
		return this.packetChat(type, text, NmsProvider.serverUuid);
	}

	public void postToMainThread(Runnable runnable);

	public Object getMinecraftServer();

	public Thread getServerThread();

	public double[] getServerTPS();

	public Object toIChatBaseComponent(List<Component> components);

	public Object toIChatBaseComponent(Component component);

	public Object toIChatBaseComponents(List<Component> components);

	public Object toIChatBaseComponents(Component component);

	public String fromIChatBaseComponent(Object component);

	public Object chatBase(String json);

	// TheMaterial utils
	public BlockDataStorage toMaterial(Object blockOrItemOrIBlockData);

	public boolean isTileEntity(Object chunk, int x, int y, int z);

	public String getNBTOfTile(Object chunk, int x, int y, int z);

	public void setNBTToTile(Object chunk, int x, int y, int z, String nbt);

	public Object toIBlockData(BlockDataStorage material);

	public Object toBlock(BlockDataStorage material);

	public Object toIBlockData(Object data);

	public Object toIBlockData(BlockState state);

	public ItemStack toItemStack(BlockDataStorage material);

	// Position utils
	public Object getChunk(World world, int x, int z);

	public Chunk toBukkitChunk(Object nmsChunk);

	public default void setBlock(Object chunk, int x, int y, int z, Object IblockDataOrBlock) {
		this.setBlock(chunk, x, y, z, IblockDataOrBlock, 0);
	}

	public void setBlock(Object chunk, int x, int y, int z, Object block, int data);

	public Object getBlock(Object chunk, int x, int y, int z);

	/**
	 * @apiNote 1.7.10 and older support
	 */
	@Deprecated
	public byte getData(Object chunk, int x, int y, int z);

	public void updateLightAt(Object chunk, int x, int y, int z);

	public void updatePhysics(Object objChunk, int x, int y, int z, Object IblockDataOrBlock);

	public int getCombinedId(Object IblockDataOrBlock);

	public Object blockPosition(int blockX, int blockY, int blockZ);

	public int getPing(Player player);

	public Object getPlayerConnection(Player player);

	public Object getConnectionNetwork(Object playercon);

	public Object getNetworkChannel(Object network);

	public int getContainerId(Object container);

	public int incrementStateId(Object container);

	public Object packetSetSlot(int id, int slot, int changeId, Object itemStack);

	public Object packetOpenWindow(int id, String legacy, int size, String title);

	public void closeGUI(Player player, Object container, boolean closePacket);

	public void setSlot(Object container, int slot, Object item);

	public void setGUITitle(Player player, Object container, String legacy, int size, String title);

	public void openGUI(Player player, Object container, String legacy, int size, String title, ItemStack[] items);

	public Object createContainer(Inventory inv, Player player);

	public Object getSlotItem(Object container, int slot);

	public void openAnvilGUI(Player player, Object container, String title, ItemStack[] items);

	public String getAnvilRenameText(Object anvil);

	public boolean processInvClickPacket(Player player, HolderGUI gui, Object provPacket);

	public boolean processServerListPing(String player, Object channel, Object packet);

	// NBT Utils
	public Object setString(Object nbt, String path, String value);

	public Object setInteger(Object nbt, String path, int value);

	public Object setDouble(Object nbt, String path, double value);

	public Object setLong(Object nbt, String path, long value);

	public Object setShort(Object nbt, String path, short value);

	public Object setFloat(Object nbt, String path, float value);

	public Object setBoolean(Object nbt, String path, boolean value);

	public Object setIntArray(Object nbt, String path, int[] value);

	public Object setByteArray(Object nbt, String path, byte[] value);

	public Object setNBTBase(Object nbt, String path, Object value);

	public Object setByte(Object nbt, String path, byte value);

	public String getString(Object nbt, String path);

	public int getInteger(Object nbt, String path);

	public double getDouble(Object nbt, String path);

	public long getLong(Object nbt, String path);

	public short getShort(Object nbt, String path);

	public float getFloat(Object nbt, String path);

	public byte getByte(Object nbt, String path);

	public boolean getBoolean(Object nbt, String path);

	public int[] getIntArray(Object nbt, String path);

	public byte[] getByteArray(Object nbt, String path);

	public Object getNBTBase(Object nbt, String path);

	public Set<String> getKeys(Object nbt);

	public boolean hasKey(Object nbt, String path);

	public void removeKey(Object nbt, String path);

	public int getEntityId(Object entity);

	public default int getEntityId(Entity entity) {
		return entity.getEntityId();
	}

	public int getContainerStateId(Object container);

	public void loadParticles();

	public Collection<? extends Player> getOnlinePlayers();

	public String getGameProfileValues(Object profile);

	public Object createGameProfile(UUID uuid, String name, String values);
}
