package me.devtec.theapi.nms;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftChunk;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.components.Component;
import me.devtec.theapi.utils.components.ComponentAPI;
import me.devtec.theapi.utils.nms.NmsProvider;
import me.devtec.theapi.utils.nms.datawatcher.DataWatcher;
import me.devtec.theapi.utils.reflections.Ref;
import net.minecraft.server.v1_7_R4.ChatClickable;
import net.minecraft.server.v1_7_R4.ChatComponentText;
import net.minecraft.server.v1_7_R4.ChatModifier;
import net.minecraft.server.v1_7_R4.ChatSerializer;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.EnumChatFormat;
import net.minecraft.server.v1_7_R4.EnumClickAction;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.MojangsonParser;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.PacketPlayOutBlockChange;
import net.minecraft.server.v1_7_R4.PacketPlayOutChat;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R4.ScoreboardObjective;

public class v1_7_R4 implements NmsProvider {
	private MinecraftServer server = MinecraftServer.getServer();
	private static final ChatComponentText empty = new ChatComponentText("");
	private static Field score_a = Ref.field(PacketPlayOutScoreboardScore.class, "a"), score_b = Ref.field(PacketPlayOutScoreboardScore.class, "b"), score_c = Ref.field(PacketPlayOutScoreboardScore.class, "c"), score_d = Ref.field(PacketPlayOutScoreboardScore.class, "d");

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
		return type.getId();
	}

	@Override
	public Object getEnumScoreboardHealthDisplay(DisplayType type) {
		return null;
	}

	@Override
	public Object getNBT(ItemStack itemStack) {
		net.minecraft.server.v1_7_R4.ItemStack item = ((net.minecraft.server.v1_7_R4.ItemStack)asNMSItem(itemStack));
		NBTTagCompound nbt = item.getTag();
		if(nbt==null)item.setTag(nbt=new NBTTagCompound());
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
		net.minecraft.server.v1_7_R4.ItemStack i = (net.minecraft.server.v1_7_R4.ItemStack)asNMSItem(stack);
		i.setTag((NBTTagCompound) nbt);
		return asBukkitItem(stack);
	}

	@Override
	public Object asNMSItem(ItemStack stack) {
		return CraftItemStack.asNMSCopy(stack);
	}

	@Override
	public ItemStack asBukkitItem(Object stack) {
		return CraftItemStack.asBukkitCopy((net.minecraft.server.v1_7_R4.ItemStack) stack);
	}

	@Override
	public Object packetEntityMetadata(int entityId, DataWatcher dataWatcher, boolean bal) {
		return new PacketPlayOutEntityMetadata(entityId, (net.minecraft.server.v1_7_R4.DataWatcher) dataWatcher.getDataWatcher(), bal);
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
		return new PacketPlayOutNamedEntitySpawn((EntityHuman)player);
	}

	@Override
	public Object packetSpawnEntityLiving(Object entityLiving) {
		return new PacketPlayOutSpawnEntityLiving((EntityLiving)entityLiving);
	}

	@Override
	public Object packetPlayerListHeaderFooter(String header, String footer) {
		return null;
	}

	@Override
	public Object packetBlockChange(World world, Position position) {
		return new PacketPlayOutBlockChange(position.getBlockX(),position.getBlockY(),position.getBlockZ(),(net.minecraft.server.v1_7_R4.World)getWorld(world));
	}

	@Override
	public Object packetBlockChange(World world, int x, int y, int z) {
		return new PacketPlayOutBlockChange(x,y,z,(net.minecraft.server.v1_7_R4.World)getWorld(world));
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
		PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();
		try {
			score_a.set(packet, line);
			score_b.set(packet, player);
			score_c.set(packet, score);
			score_d.set(packet, getScoreboardAction(action));
		}catch(Exception err) {}
		return packet;
	}

	@Override
	public Object packetTitle(TitleAction action, String text, int fadeIn, int stay, int fadeOut) {
		if(action==TitleAction.ACTIONBAR)return packetChat(ChatType.GAME_INFO, text, null);
		return null;
	}

	@Override
	public Object packetChat(ChatType type, Object chatBase, UUID uuid) {
		return new PacketPlayOutChat((IChatBaseComponent)chatBase, type.toByte());
	}

	@Override
	public Object packetChat(ChatType type, String text, UUID uuid) {
		return packetChat(type, ComponentAPI.toIChatBaseComponent(ComponentAPI.toComponent(text, false)), uuid);
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
				modif=modif.setColor(EnumChatFormat.valueOf(ChatColor.getByChar(c.getColor().charAt(0)).name()));
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
		return main.a().isEmpty()?empty:main;
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
				modif=modif.setColor(EnumChatFormat.valueOf(ChatColor.getByChar(c.getColor().charAt(0)).name()));
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
		return main.a().isEmpty()?empty:main;
	}

	@Override
	public Object chatBase(String json) {
		return ChatSerializer.a(json);
	}

	@Override
	public String fromIChatBaseComponent(Object component) {
		if(component==null)return null;
		if(component instanceof IChatBaseComponent[]) {
			IChatBaseComponent[] cchat = (IChatBaseComponent[])component;
			StringBuilder builder = new StringBuilder();
			for(IChatBaseComponent chat : cchat) {
				builder.append(asString(chat.getChatModifier())).append(chat.e());
				for(Object c : chat.a()) {
					builder.append(asString(((IChatBaseComponent)c).getChatModifier())).append(((IChatBaseComponent)c).e());
				}
			}
			return builder.toString();
		}
		if(component instanceof IChatBaseComponent) {
			IChatBaseComponent chat = (IChatBaseComponent)component;
			StringBuilder builder = new StringBuilder();
			builder.append(asString(chat.getChatModifier())).append(chat.e());
			for(Object c : chat.a()) {
				builder.append(asString(((IChatBaseComponent)c).getChatModifier())).append(((IChatBaseComponent)c).e());
			}
			return builder.toString();
		}
		return component.toString();
	}

	private StringBuilder asString(ChatModifier chatModifier) {
		StringBuilder builder = new StringBuilder();
		if(chatModifier.a()!=null)builder.append('§').append(chatModifier.a().getChar());
		if(chatModifier.b())builder.append('§').append('l');
		if(chatModifier.c())builder.append('§').append('o');
		if(chatModifier.d())builder.append('§').append('m');
		if(chatModifier.e())builder.append('§').append('n');
		if(chatModifier.f())builder.append('§').append('k');
		return builder;
	}

}