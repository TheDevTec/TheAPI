package me.devtec.theapi.utils.nms;

import java.util.List;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.components.Component;
import me.devtec.theapi.utils.nms.datawatcher.DataWatcher;
import me.devtec.theapi.utils.nms.nbt.NBTEdit;

public interface NmsProvider {
	
	static final UUID serverUuid = UUID.randomUUID();
	public enum Action {
		CHANGE, REMOVE
	}

	public enum DisplayType {
		INTEGER, HEARTS
	}
	
	public enum TitleAction {
		ACTIONBAR, CLEAR, RESET, SUBTITLE, TITLE, TIMES
	}

	public enum ChatType {
1		CHAT(0), SYSTEM(1), GAME_INFO(2);
		
		byte id;
		ChatType(int i) {
			id=(byte)i;
		}

		public byte toByte() {
			return id;
		}
	}

	public Object getEntity(Entity entity);
	public Object getEntityLiving(LivingEntity entity);
	public Object getPlayer(Player player);
	public Object getWorld(World world);
	public Object getChunk(Chunk chunk);

	public Object getScoreboardAction(Action type);
	public Object getEnumScoreboardHealthDisplay(DisplayType type);
	
	public  Object getNBT(ItemStack itemStack);
	public  Object parseNBT(String json);
	
	public default ItemStack setNBT(ItemStack stack, String nbt) {
		return setNBT(stack, parseNBT(nbt));
	}
	public default ItemStack setNBT(ItemStack stack, NBTEdit nbt) {
		return setNBT(stack, nbt.getNBT());
	}
	public ItemStack setNBT(ItemStack stack, Object nbt);
	
	public Object asNMSItem(ItemStack stack);
	public ItemStack asBukkitItem(Object stack);
	
	public default Object packetEntityMetadata(Entity entity) {
		return packetEntityMetadata(entity.getEntityId(), new DataWatcher(entity), true);
	}
	public default Object packetEntityMetadata(Entity entity, DataWatcher dataWatcher) {
		return packetEntityMetadata(entity.getEntityId(), dataWatcher, true);
	}
	public default Object packetEntityMetadata(Entity entity, DataWatcher dataWatcher, boolean bal) {
		return packetEntityMetadata(entity.getEntityId(), dataWatcher, bal);
	}
	public default Object packetEntityMetadata(int entityId, DataWatcher dataWatcher) {
		return packetEntityMetadata(entityId, dataWatcher, true);
	}
	public Object packetEntityMetadata(int entityId, DataWatcher dataWatcher, boolean bal);
	
	public Object packetEntityDestroy(int... ids);

	public Object packetSpawnEntity(Object entity, int id);
	public Object packetNamedEntitySpawn(Object player);
	public Object packetSpawnEntityLiving(Object entityLiving);
	
	public Object packetPlayerListHeaderFooter(String header, String footer);

	public Object packetBlockChange(World world, Position position);
	public Object packetBlockChange(World world, int x, int y, int z);
	
	public Object packetScoreboardObjective();
	public Object packetScoreboardDisplayObjective();
	public Object packetScoreboardTeam();
	public Object packetScoreboardScore(Action action, String player, String line, int score);

	public Object packetTitle(TitleAction action, String text, int fadeIn, int stay, int fadeOut);
	public default Object packetTitle(TitleAction action, String text) {
		return packetTitle(action, text, 20, 60, 20);
	}

	public Object packetChat(ChatType type, Object chatBase, UUID uuid);

	public default Object packetChat(ChatType type, Object chatBase) {
		return packetChat(type, chatBase, serverUuid);
	}

	public Object packetChat(ChatType type, String text, UUID uuid);

	public default Object packetChat(ChatType type, String text) {
		return packetChat(type, text, serverUuid);
	}
	
	public void postToMainThread(Runnable runnable);

	public Object getMinecraftServer();

	public Thread getServerThread();

	public double[] getServerTPS();

	public Object toIChatBaseComponent(List<Component> components);

	public Object toIChatBaseComponent(Component component);
	
	public String fromIChatBaseComponent(Object component);
	
	public Object chatBase(String json);
}
