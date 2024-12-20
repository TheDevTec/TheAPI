package me.devtec.theapi.bukkit.nms;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.devtec.shared.annotations.Comment;
import me.devtec.shared.components.Component;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.theapi.bukkit.game.BlockDataStorage;
import me.devtec.theapi.bukkit.game.Position;
import me.devtec.theapi.bukkit.gui.HolderGUI;

public interface NmsProvider {
    UUID serverUuid = UUID.randomUUID();

    @Getter
    enum Action {
        CHANGE(0), REMOVE(1);

        private final int id;

        Action(int i) {
            id = i;
        }

    }

    enum DisplayType {
        INTEGER, HEARTS
    }

    enum TitleAction {
        ACTIONBAR, CLEAR, RESET, SUBTITLE, TITLE, TIMES
    }

    enum PlayerInfoType {
        ADD_PLAYER, REMOVE_PLAYER, UPDATE_GAME_MODE, UPDATE_LATENCY, UPDATE_DISPLAY_NAME
    }

    enum ChatType {
        CHAT(0), SYSTEM(1), GAME_INFO(2);

        private final byte id;

        ChatType(int i) {
            id = (byte) i;
        }

        public byte toByte() {
            return id;
        }
    }

    /**
     * @return NmsProvider name (class instance name)
     */
    String getProviderName();

    /**
     * @return Convert Bukkit Entity to NMS Entity
     */
    Object getEntity(Entity entity);

    /**
     * @return Convert Bukkit LivingEntity to NMS EntityLiving
     */
    Object getEntityLiving(LivingEntity entity);

    /**
     * @return Convert Bukkit Player to NMS EntityPlayer
     */
    Object getPlayer(Player player);

    /**
     * @return Convert Bukkit World to NMS World
     */
    Object getWorld(World world);

    /**
     * @return Convert Bukkit Chunk to NMS Chunk
     */
    Object getChunk(Chunk chunk);

    /**
     * @return Convert Action to NMS ScoreboardAction
     */
    Object getScoreboardAction(Action type);

    /**
     * @return Convert DisplayType to NMS EnumScoreboardHealthDisplay
     */
    Object getEnumScoreboardHealthDisplay(DisplayType type);

    /**
     * @return Get Entity's NMS NBT
     */
    Object getNBT(Entity entity);

    /**
     * @return Get ItemStack NMS NBT
     */
    Object getNBT(ItemStack itemStack);

    /**
     * @return Parse json nbt to the NMS NBT
     */
    Object parseNBT(String json);

    /**
     * @return Parse json NBT and set to ItemStack, returns new ItemStack with new
     * NBT
     */
    default ItemStack setNBT(ItemStack stack, String nbt) {
        return this.setNBT(stack, parseNBT(nbt));
    }

    /**
     * @return Get NBT from NBTEdit and set to ItemStack, returns new ItemStack with
     * new NBT
     */
    default ItemStack setNBT(ItemStack stack, NBTEdit nbt) {
        return this.setNBT(stack, nbt.getNBT());
    }

    /**
     * @return Set NBT to ItemStack, returns new ItemStack with new NBT
     */
    ItemStack setNBT(ItemStack stack, Object nbt);

    /**
     * @return Convert Bukkit ItemStack to NMS ItemStack
     */
    Object asNMSItem(ItemStack stack);

    /**
     * @return Convert NMS ItemStack to Bukkit ItemStack
     */
    ItemStack asBukkitItem(Object stack);

    /**
     * @return Get DataWatcher of Bukkit Entity
     */
    Object getDataWatcher(Entity entity);

    /**
     * @return Get DataWatcher of NMS Entity
     */
    Object getDataWatcher(Object entity);

    default Object packetEntityMetadata(Entity entity) {
        return this.packetEntityMetadata(entity.getEntityId(), this.getDataWatcher(entity), true);
    }

    default Object packetEntityMetadata(Entity entity, Object dataWatcher) {
        return this.packetEntityMetadata(entity.getEntityId(), dataWatcher, true);
    }

    default Object packetEntityMetadata(Entity entity, Object dataWatcher, boolean bal) {
        return this.packetEntityMetadata(entity.getEntityId(), dataWatcher, bal);
    }

    default Object packetEntityMetadata(int entityId, Object dataWatcher) {
        return this.packetEntityMetadata(entityId, dataWatcher, true);
    }

    Object packetEntityMetadata(int entityId, Object dataWatcher, boolean bal);

    Object packetEntityDestroy(int... ids);

    Object packetSpawnEntity(Object entity, int id);

    Object packetNamedEntitySpawn(Object player);

    Object packetSpawnEntityLiving(Object entityLiving);

    default Object packetPlayerListHeaderFooter(String header, String footer) {
        return packetPlayerListHeaderFooter(ComponentAPI.fromString(header), ComponentAPI.fromString(footer));
    }

    Object packetPlayerListHeaderFooter(Component header, Component footer);

    /**
     * @apiNote @see
     * {@link NmsProvider#packetBlockChange(int, int, int, Object, int)}
     */
    default Object packetBlockChange(Position pos, Object iblockdata) {
        return packetBlockChange(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), iblockdata, 0);
    }

    /**
     * @apiNote @see
     * {@link NmsProvider#packetBlockChange(int, int, int, Object, int)}
     */
    default Object packetBlockChange(int x, int y, int z, Object iblockdata) {
        return packetBlockChange(x, y, z, iblockdata, 0);
    }

    /**
     * @apiNote @see
     * {@link NmsProvider#packetBlockChange(int, int, int, Object, int)}
     */
    default Object packetBlockChange(Position pos, Object iblockdata, int data) {
        return packetBlockChange(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), iblockdata, data);
    }

    Object packetBlockChange(int x, int y, int z, Object iblockdata, int data);

    Object packetScoreboardObjective();

    Object packetScoreboardDisplayObjective(int id, Object scoreboardObjective);

    Object packetScoreboardTeam();

    Object packetScoreboardScore(Action action, String player, String line, int score);

    Object packetEntityHeadRotation(Entity entity);

    Object packetHeldItemSlot(int slot);

    Object packetExp(float exp, int total, int toNextLevel);

    Object packetPlayerInfo(PlayerInfoType type, Player player);

    Object packetPlayerInfo(PlayerInfoType type, GameProfileHandler gameProfile, int latency, GameMode gameMode, Component playerName);

    Object packetResourcePackSend(String url, String hash, boolean shouldForce, Component prompt);

    Object packetPosition(double x, double y, double z, float yaw, float pitch);

    Object packetRespawn(Player player);

    Object packetTitle(TitleAction action, Component text, int fadeIn, int stay, int fadeOut);

    default Object packetTitle(TitleAction action, Component text) {
        return this.packetTitle(action, text, 20, 60, 20);
    }

    Object packetChat(ChatType type, Object chatBase, UUID uuid);

    default Object packetChat(ChatType type, Object chatBase) {
        return this.packetChat(type, chatBase, NmsProvider.serverUuid);
    }

    Object packetChat(ChatType type, Component text, UUID uuid);

    default Object packetChat(ChatType type, Component text) {
        return this.packetChat(type, text, NmsProvider.serverUuid);
    }

    void postToMainThread(Runnable runnable);

    Object getMinecraftServer();

    Thread getServerThread();

    double[] getServerTPS();

    Object toIChatBaseComponent(List<Component> components);

    Object toIChatBaseComponent(Component component);

    Object[] toIChatBaseComponents(List<Component> components);

    Object[] toIChatBaseComponents(Component component);

    Component fromIChatBaseComponent(Object component);

    Object chatBase(String json);

    // TheMaterial utils
    BlockDataStorage toMaterial(Object blockOrItemOrIBlockData);

    boolean isTileEntity(Object chunk, int x, int y, int z);

    String getNBTOfTile(Object chunk, int x, int y, int z);

    void setNBTToTile(Object chunk, int x, int y, int z, String nbt);

    Object toIBlockData(BlockDataStorage material);

    Object toBlock(BlockDataStorage material);

    Object toIBlockData(Object data);

    Object toIBlockData(BlockState state);

    ItemStack toItemStack(BlockDataStorage material);

    // Position utils
    Object getChunk(World world, int x, int z);

    Chunk toBukkitChunk(Object nmsChunk);

    default void setBlock(Object chunk, int x, int y, int z, Object IblockDataOrBlock) {
        this.setBlock(chunk, x, y, z, IblockDataOrBlock, 0);
    }

    void setBlock(Object chunk, int x, int y, int z, Object IblockDataOrBlock, int data);

    Object getBlock(Object chunk, int x, int y, int z);

    /**
     * @apiNote 1.7.10 and older support
     */
    @Deprecated
    byte getData(Object chunk, int x, int y, int z);

    void updateLightAt(Object chunk, int x, int y, int z);

    void updatePhysics(Object objChunk, int x, int y, int z, Object IblockDataOrBlock);

    int getCombinedId(Object IblockDataOrBlock);

    Object blockPosition(int blockX, int blockY, int blockZ);

    int getPing(Player player);

    Object getPlayerConnection(Player player);

    Object getConnectionNetwork(Object playercon);

    Object getNetworkChannel(Object network);

    int getContainerId(Object container);

    int incrementStateId(Object container);

    Object packetSetSlot(int id, int slot, int changeId, Object itemStack);

    Object packetOpenWindow(int id, String legacy, int size, Component title);

    void closeGUI(Player player, Object container, boolean closePacket);

    void setSlot(Object container, int slot, Object item);

    void setGUITitle(Player player, Object container, String legacy, int size, Component title);

    void openGUI(Player player, Object container, String legacy, int size, Component title);

    Object createContainer(Inventory inv, Player player);

    Object getSlotItem(Object container, int slot);

    void openAnvilGUI(Player player, Object container, Component title);

    String getAnvilRenameText(Object anvil);

    @Comment(comment = "Returns true if event is cancelled")
    boolean processInvClickPacket(Player player, HolderGUI gui, Object packet);

    @Comment(comment = "Returns true if packet should be cancelled")
    boolean processServerListPing(String player, Object channel, Object packet);

    // NBT Utils
    Object setString(Object nbt, String path, String value);

    Object setInteger(Object nbt, String path, int value);

    Object setDouble(Object nbt, String path, double value);

    Object setLong(Object nbt, String path, long value);

    Object setShort(Object nbt, String path, short value);

    Object setFloat(Object nbt, String path, float value);

    Object setBoolean(Object nbt, String path, boolean value);

    Object setIntArray(Object nbt, String path, int[] value);

    Object setByteArray(Object nbt, String path, byte[] value);

    Object setNBTBase(Object nbt, String path, Object value);

    Object setByte(Object nbt, String path, byte value);

    String getString(Object nbt, String path);

    int getInteger(Object nbt, String path);

    double getDouble(Object nbt, String path);

    long getLong(Object nbt, String path);

    short getShort(Object nbt, String path);

    float getFloat(Object nbt, String path);

    byte getByte(Object nbt, String path);

    boolean getBoolean(Object nbt, String path);

    int[] getIntArray(Object nbt, String path);

    byte[] getByteArray(Object nbt, String path);

    Object getNBTBase(Object nbt, String path);

    Set<String> getKeys(Object nbt);

    boolean hasKey(Object nbt, String path);

    void removeKey(Object nbt, String path);

    int getEntityId(Object entity);

    default int getEntityId(Entity entity) {
        return entity.getEntityId();
    }

    int getContainerStateId(Object container);

    void loadParticles();

    Collection<? extends Player> getOnlinePlayers();

    default Object getGameProfile(Player player) {
        return getGameProfile(getPlayer(player));
    }

    Object getGameProfile(Object nmsPlayer);

    Object toGameProfile(GameProfileHandler gameProfileHandler);

    GameProfileHandler fromGameProfile(Object gameProfile);
}
