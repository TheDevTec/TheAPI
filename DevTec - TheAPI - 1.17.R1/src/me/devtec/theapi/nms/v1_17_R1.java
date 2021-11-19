 package me.devtec.theapi.nms;

import java.util.List;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage;
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
import net.minecraft.EnumChatFormat;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatClickable;
import net.minecraft.network.chat.ChatClickable.EnumClickAction;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatHexColor;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.scores.criteria.IScoreboardCriteria.EnumScoreboardHealthDisplay;

public class v1_17_R1 implements NmsProvider {
	private static final MinecraftServer server = MinecraftServer.getServer();
	private static final sun.misc.Unsafe unsafe = (sun.misc.Unsafe) Ref.getNulled(Ref.field(sun.misc.Unsafe.class, "theUnsafe"));

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
		return type==Action.CHANGE?ScoreboardServer.Action.a:ScoreboardServer.Action.b;
	}

	@Override
	public Object getEnumScoreboardHealthDisplay(DisplayType type) {
		return type==DisplayType.INTEGER?EnumScoreboardHealthDisplay.a:EnumScoreboardHealthDisplay.b;
	}

	@Override
	public Object getNBT(ItemStack itemStack) {
		return ((net.minecraft.world.item.ItemStack)asNMSItem(itemStack)).getOrCreateTag();
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
		net.minecraft.world.item.ItemStack i = (net.minecraft.world.item.ItemStack)asNMSItem(stack);
		i.setTag((NBTTagCompound) nbt);
		return asBukkitItem(stack);
	}

	@Override
	public Object asNMSItem(ItemStack stack) {
		return CraftItemStack.asNMSCopy(stack);
	}

	@Override
	public ItemStack asBukkitItem(Object stack) {
		return CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack) stack);
	}

	@Override
	public Object packetEntityMetadata(int entityId, DataWatcher dataWatcher, boolean bal) {
		return new PacketPlayOutEntityMetadata(entityId, (net.minecraft.network.syncher.DataWatcher) dataWatcher.getDataWatcher(), bal);
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
		return new PacketPlayOutNamedEntitySpawn((EntityHuman)player);
	}

	@Override
	public Object packetSpawnEntityLiving(Object entityLiving) {
		return new PacketPlayOutSpawnEntityLiving((EntityLiving)entityLiving);
	}

	@Override
	public Object packetPlayerListHeaderFooter(String header, String footer) {
		return new PacketPlayOutPlayerListHeaderFooter((IChatBaseComponent)toIChatBaseComponent(ComponentAPI.toComponent(header, true)), (IChatBaseComponent)toIChatBaseComponent(ComponentAPI.toComponent(footer, true)));
	}

	@Override
	public Object packetBlockChange(World world, Position position) {
		return new PacketPlayOutBlockChange((net.minecraft.world.level.World)getWorld(world), (BlockPosition)position.getBlockPosition());
	}

	@Override
	public Object packetBlockChange(World world, int x, int y, int z) {
		return new PacketPlayOutBlockChange((net.minecraft.world.level.World)getWorld(world), new BlockPosition(x,y,z));
	}

	@Override
	public Object packetScoreboardObjective() {
		try {
			return unsafe.allocateInstance(PacketPlayOutScoreboardObjective.class);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Object packetScoreboardDisplayObjective() {
		try {
			return unsafe.allocateInstance(PacketPlayOutScoreboardDisplayObjective.class);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Object packetScoreboardTeam() {
		try {
			return unsafe.allocateInstance(PacketPlayOutScoreboardTeam.class);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Object packetScoreboardScore(Action action, String player, String line, int score) {
		return new PacketPlayOutScoreboardScore((net.minecraft.server.ScoreboardServer.Action) getScoreboardAction(action), player, line, score);
	}

	@Override
	public Object packetTitle(TitleAction action, String text, int fadeIn, int stay, int fadeOut) {
		switch(action) {
		case ACTIONBAR:
			return new ClientboundSetActionBarTextPacket((IChatBaseComponent)ComponentAPI.toIChatBaseComponent(ComponentAPI.toComponent(text, true)));
		case TITLE:
			return new ClientboundSetTitleTextPacket((IChatBaseComponent)ComponentAPI.toIChatBaseComponent(ComponentAPI.toComponent(text, true)));
		case SUBTITLE:
			return new ClientboundSetSubtitleTextPacket((IChatBaseComponent)ComponentAPI.toIChatBaseComponent(ComponentAPI.toComponent(text, true)));
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
		switch(type) {
		case CHAT:
			return new PacketPlayOutChat((IChatBaseComponent)chatBase, ChatMessageType.a, uuid);
		case GAME_INFO:
			return new PacketPlayOutChat((IChatBaseComponent)chatBase, ChatMessageType.c, uuid);
		case SYSTEM:
			return new PacketPlayOutChat((IChatBaseComponent)chatBase, ChatMessageType.b, uuid);
		}
		return null;
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
		return server.an;
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
				modif.setChatClickable(new ChatClickable(EnumClickAction.a, c.getUrl()));
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
				modif.setChatClickable(new ChatClickable(EnumClickAction.a, c.getUrl()));
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
