package me.devtec.theapi.nms;

import java.util.List;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.components.Component;
import me.devtec.theapi.utils.components.ComponentAPI;
import me.devtec.theapi.utils.nms.NmsProvider;
import me.devtec.theapi.utils.nms.datawatcher.DataWatcher;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.ChatClickable;
import net.minecraft.server.v1_16_R3.ChatClickable.EnumClickAction;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.ChatHexColor;
import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.ChatModifier;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EnumChatFormat;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.IScoreboardCriteria.EnumScoreboardHealthDisplay;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.MojangsonParser;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockChange;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_16_R3.ScoreboardObjective;
import net.minecraft.server.v1_16_R3.ScoreboardServer;

public class v1_16_R3 implements NmsProvider {
	private static final MinecraftServer server = MinecraftServer.getServer();

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
	public Object getScoreboardAction(Action type) {
		return ScoreboardServer.Action.valueOf(type.name());
	}

	@Override
	public Object getEnumScoreboardHealthDisplay(DisplayType type) {
		return EnumScoreboardHealthDisplay.valueOf(type.name());
	}

	@Override
	public Object getNBT(ItemStack itemStack) {
		return ((net.minecraft.server.v1_16_R3.ItemStack)asNMSItem(itemStack)).getOrCreateTag();
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
		net.minecraft.server.v1_16_R3.ItemStack i = (net.minecraft.server.v1_16_R3.ItemStack)asNMSItem(stack);
		i.setTag((NBTTagCompound) nbt);
		return asBukkitItem(stack);
	}

	@Override
	public Object asNMSItem(ItemStack stack) {
		return CraftItemStack.asNMSCopy(stack);
	}

	@Override
	public ItemStack asBukkitItem(Object stack) {
		return CraftItemStack.asBukkitCopy((net.minecraft.server.v1_16_R3.ItemStack) stack);
	}

	@Override
	public Object packetEntityMetadata(int entityId, DataWatcher dataWatcher, boolean bal) {
		return new PacketPlayOutEntityMetadata(entityId, (net.minecraft.server.v1_16_R3.DataWatcher) dataWatcher.getDataWatcher(), bal);
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
		return new PacketPlayOutNamedEntitySpawn((EntityHuman)player);
	}

	@Override
	public Object packetSpawnEntityLiving(Object entityLiving) {
		return new PacketPlayOutSpawnEntityLiving((EntityLiving)entityLiving);
	}

	@Override
	public Object packetPlayerListHeaderFooter(String header, String footer) {
		PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
		packet.header=(IChatBaseComponent)toIChatBaseComponent(ComponentAPI.toComponent(header, true));
		packet.footer=(IChatBaseComponent)toIChatBaseComponent(ComponentAPI.toComponent(footer, true));
		return packet;
	}

	@Override
	public Object packetBlockChange(World world, Position position) {
		return new PacketPlayOutBlockChange((net.minecraft.server.v1_16_R3.World)getWorld(world), (BlockPosition)position.getBlockPosition());
	}

	@Override
	public Object packetBlockChange(World world, int x, int y, int z) {
		return new PacketPlayOutBlockChange((net.minecraft.server.v1_16_R3.World)getWorld(world), new BlockPosition(x,y,z));
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
		return new PacketPlayOutChat((IChatBaseComponent)chatBase, ChatMessageType.valueOf(type.name()), uuid);
	}

	@Override
	public Object packetChat(ChatType type, String text, UUID uuid) {
		return packetChat(type, ComponentAPI.toIChatBaseComponent(ComponentAPI.toComponent(text, false)), uuid);
	}

	@Override
	public void postToMainThread(Runnable runnable) {
		server.executeSync(runnable);
	}

	@Override
	public Object getMinecraftServer() {
		return server;
	}

	@Override
	public Thread getServerThread() {
		return server.serverThread;
	}

	@Override
	public double[] getServerTPS() {
		return server.recentTps;
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
				if(c.getColor().startsWith("#"))
					modif=modif.setColor(ChatHexColor.a(c.getColor()));
				else
					modif=modif.setColor(EnumChatFormat.a(c.getColor().charAt(0)));
			}
			if(c.getUrl()!=null)
				modif.setChatClickable(new ChatClickable(EnumClickAction.OPEN_URL, c.getUrl()));
			modif.setBold(c.isBold());
			modif.setItalic(c.isItalic());
			modif.setRandom(c.isObfuscated());
			modif.setUnderline(c.isUnderlined());
			modif.setStrikethrough(c.isStrikethrough());
			current.setChatModifier(modif);
			c=c.getExtra();
		}
		return main.getSiblings().isEmpty()?ChatComponentText.d:main;
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
				if(c.getColor().startsWith("#"))
					modif=modif.setColor(ChatHexColor.a(c.getColor()));
				else
					modif=modif.setColor(EnumChatFormat.a(c.getColor().charAt(0)));
			}
			if(c.getUrl()!=null)
				modif.setChatClickable(new ChatClickable(EnumClickAction.OPEN_URL, c.getUrl()));
			modif.setBold(c.isBold());
			modif.setItalic(c.isItalic());
			modif.setRandom(c.isObfuscated());
			modif.setUnderline(c.isUnderlined());
			modif.setStrikethrough(c.isStrikethrough());
			current.setChatModifier(modif);
		}
		return main.getSiblings().isEmpty()?ChatComponentText.d:main;
	}

	@Override
	public Object chatBase(String json) {
		return IChatBaseComponent.ChatSerializer.a(json);
	}

	@Override
	public String fromIChatBaseComponent(Object component) {
		return CraftChatMessage.fromComponent((IChatBaseComponent)component);
	}

}
