package me.devtec.theapi.bukkit.nms;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mojang.authlib.GameProfile;

import io.netty.channel.Channel;
import me.devtec.shared.Ref;
import me.devtec.shared.components.Component;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.events.EventManager;
import me.devtec.shared.scheduler.Tasker;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.BukkitLoader.InventoryClickType;
import me.devtec.theapi.bukkit.events.ServerListPingEvent;
import me.devtec.theapi.bukkit.events.ServerListPingEvent.PlayerProfile;
import me.devtec.theapi.bukkit.game.Position;
import me.devtec.theapi.bukkit.game.TheMaterial;
import me.devtec.theapi.bukkit.gui.AnvilGUI;
import me.devtec.theapi.bukkit.gui.GUI.ClickType;
import me.devtec.theapi.bukkit.gui.HolderGUI;
import me.devtec.theapi.bukkit.nms.utils.InventoryUtils;
import me.devtec.theapi.bukkit.nms.utils.InventoryUtils.DestinationType;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.ServerStatusResponse.Players;
import net.minecraft.network.ServerStatusResponse.Version;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SCloseWindowPacket;
import net.minecraft.network.play.server.SDestroyEntitiesPacket;
import net.minecraft.network.play.server.SDisplayObjectivePacket;
import net.minecraft.network.play.server.SEntityHeadLookPacket;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.network.play.server.SPlayerListHeaderFooterPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SScoreboardObjectivePacket;
import net.minecraft.network.play.server.SSendResourcePackPacket;
import net.minecraft.network.play.server.SSetExperiencePacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.network.play.server.SSpawnMobPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.network.play.server.SSpawnPlayerPacket;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.network.play.server.STitlePacket.Type;
import net.minecraft.network.play.server.SUpdateScorePacket;
import net.minecraft.network.status.server.SServerInfoPacket;
import net.minecraft.particles.ParticleType;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.server.ServerWorld;

public class v1_16_R3_Mohist implements NmsProvider {
	private static double[] tps = new double[] {20,20};
	public v1_16_R3_Mohist() {
		//init ticker for avarenge TPS
		new Tasker() {
			int five=5, fifteen=15;
			public void run() {
				double tps = MathHelper.func_76127_a(server.field_71311_j) * 1.0E-6D;
				if(--five==0) {
					five=5;
					v1_16_R3_Mohist.tps[0]=tps;
				}
				if(--fifteen==0) {
					fifteen=15;
					v1_16_R3_Mohist.tps[1]=tps;
				}
			}
		}.runRepeating(0, 20*60);
	}
	
	private static Field repairText = Ref.field(RepairContainer.class, "field_82857_m");
	private static Field channel = Ref.field(NetworkManager.class, "field_150746_k");
	private static Field field = Ref.field(SServerInfoPacket.class, "field_149296_b");
	private static Field tabHeader = Ref.field(SPlayerListHeaderFooterPacket.class, "field_179703_a"), tabFooter = Ref.field(SPlayerListHeaderFooterPacket.class, "field_179702_b");
	private static final DedicatedServer server = ((CraftServer)Bukkit.getServer()).getServer();

	@Override
	public Collection<? extends Player> getOnlinePlayers() {
		return Bukkit.getOnlinePlayers();
	}
	
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
	public int getEntityId(Object entity) {
		return ((net.minecraft.entity.Entity)entity).func_145782_y();
	}

	@Override
	public Object getScoreboardAction(Action type) {
		return ServerScoreboard.Action.valueOf(type.name());
	}

	@Override
	public Object getEnumScoreboardHealthDisplay(DisplayType type) {
		return ScoreCriteria.RenderType.valueOf(type.name());
	}

	@Override
	public Object getNBT(ItemStack itemStack) {
		return ((net.minecraft.item.ItemStack)asNMSItem(itemStack)).func_196082_o();
	}

	@Override
	public Object parseNBT(String json) {
		try {
			return JsonToNBT.func_180713_a(json);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public ItemStack setNBT(ItemStack stack, Object nbt) {
		if(nbt instanceof NBTEdit)nbt=((NBTEdit) nbt).getNBT();
		net.minecraft.item.ItemStack i = (net.minecraft.item.ItemStack)asNMSItem(stack);
		i.func_77982_d((CompoundNBT) nbt);
		return asBukkitItem(i);
	}

	@Override
	public Object asNMSItem(ItemStack stack) {
		if(stack==null)return net.minecraft.item.ItemStack.field_190927_a;
		return CraftItemStack.asNMSCopy(stack);
	}
	
	@Override
	public ItemStack asBukkitItem(Object stack) {
		return CraftItemStack.asBukkitCopy((net.minecraft.item.ItemStack) stack);
	}

	@Override
	public Object packetOpenWindow(int id, String legacy, int size, String title) {
		ContainerType<?> windowType = ContainerType.field_221507_a;
		switch (size) {
		case 0: {
			windowType = ContainerType.field_221514_h;
			break;
		}
		case 18 : {
			windowType = ContainerType.field_221508_b;
			break;
		}
		case 27 : {
			windowType = ContainerType.field_221509_c;
			break;
		}
		case 36 : {
			windowType = ContainerType.field_221510_d;
			break;
		}
		case 45 : {
			windowType = ContainerType.field_221511_e;
			break;
		}
		case 54 : {
			windowType = ContainerType.field_221512_f;
			break;
		}
		}
		return new SOpenWindowPacket(id, windowType, (ITextComponent)toIChatBaseComponent(ComponentAPI.fromString(title)));
	}
	
	public int getContainerId(Object container) {
		return ((net.minecraft.inventory.container.Container)container).field_75152_c;
	}
	
	@Override
	public Object packetResourcePackSend(String url, String hash, boolean requireRP, String prompt) {
		return new SSendResourcePackPacket(url, hash);
	}

	@Override
	public Object packetSetSlot(int container, int slot, int statusId, Object itemStack) {
		return new SSetSlotPacket(container, slot, (net.minecraft.item.ItemStack)(itemStack==null?asNMSItem(null):itemStack));
	}

	public Object packetSetSlot(int container, int slot, Object itemStack) {
		return packetSetSlot(container,slot,0,itemStack);
	}

	@Override
	public Object packetEntityMetadata(int entityId, Object dataWatcher, boolean bal) {
		return new SEntityMetadataPacket(entityId, (EntityDataManager) dataWatcher, bal);
	}

	@Override
	public Object packetEntityDestroy(int... ids) {
		return new SDestroyEntitiesPacket(ids);
	}

	@Override
	public Object packetSpawnEntity(Object entity, int id) {
		return new SSpawnObjectPacket((net.minecraft.entity.Entity) entity, id);
	}

	@Override
	public Object packetNamedEntitySpawn(Object player) {
		return new SSpawnPlayerPacket((ServerPlayerEntity)player);
	}

	@Override
	public Object packetSpawnEntityLiving(Object entityLiving) {
		return new SSpawnMobPacket((net.minecraft.entity.LivingEntity)entityLiving);
	}
	
	@Override
	public Object packetPlayerListHeaderFooter(String header, String footer) {
		SPlayerListHeaderFooterPacket packet = new SPlayerListHeaderFooterPacket();
		try {
			tabHeader.set(packet, toIChatBaseComponent(ComponentAPI.fromString(header)));
			tabFooter.set(packet, toIChatBaseComponent(ComponentAPI.fromString(footer)));
		}catch(Exception err) {}
		return packet;
	}

	@Override
	public Object packetBlockChange(World world, Position position) {
		return new SChangeBlockPacket((BlockPos) position.getBlockPosition(), (net.minecraft.block.BlockState) position.getIBlockData());
	}

	@Override
	public Object packetBlockChange(World world, int x, int y, int z) {
		return new SChangeBlockPacket(new BlockPos(x,y,z), (net.minecraft.block.BlockState) getBlock(getChunk(world, x>>4, z>>4), x, y, z));
	}

	@Override
	public Object packetScoreboardObjective() {
		return new SScoreboardObjectivePacket();
	}

	@Override
	public Object packetScoreboardDisplayObjective(int id, Object scoreboardObjective) {
		return new SDisplayObjectivePacket(id, scoreboardObjective==null?null:(ScoreObjective)scoreboardObjective);
	}

	@Override
	public Object packetScoreboardTeam() {
		return new STeamsPacket();
	}

	@Override
	public Object packetScoreboardScore(Action action, String player, String line, int score) {
		return new SUpdateScorePacket((ServerScoreboard.Action) getScoreboardAction(action), player, line, score);
	}

	@Override
	public Object packetTitle(TitleAction action, String text, int fadeIn, int stay, int fadeOut) {
		return new STitlePacket(Type.valueOf(action.name()), (ITextComponent)toIChatBaseComponent(ComponentAPI.fromString(text)), fadeIn, stay, fadeOut);
	}

	@Override
	public Object packetChat(ChatType type, Object chatBase, UUID uuid) {
		return new SChatPacket((ITextComponent)chatBase, net.minecraft.util.text.ChatType.valueOf(type.name()), uuid);
	}

	@Override
	public Object packetChat(ChatType type, String text, UUID uuid) {
		return packetChat(type, toIChatBaseComponent(ComponentAPI.fromString(text)), uuid);
	}

	@Override
	public void postToMainThread(Runnable runnable) {
		server.execute(runnable);
	}

	@Override
	public Object getMinecraftServer() {
		return server;
	}

	@Override
	public Thread getServerThread() {
		return server.func_213170_ax();
	}

	@Override
	public double[] getServerTPS() {
		return new double[] {MathHelper.func_76127_a(server.field_71311_j) * 1.0E-6D, tps[0], tps[1]};
	}
	
	private ITextComponent convert(Component c) {
		StringTextComponent current = new StringTextComponent(c.getText());
		Style modif = current.func_150256_b();
		if(c.getColor()!=null && !c.getColor().isEmpty()) {
			if(c.getColor().startsWith("#"))
				modif=modif.func_240718_a_(Color.func_240745_a_(c.getColor()));
			else {
				for(TextFormatting e : TextFormatting.values())
					if(e.toString().charAt(1)==c.colorToChar()) {
						modif=modif.func_240718_a_(Color.func_240744_a_(e));
						break;
					}
			}
		}
		if(c.getClickEvent()!=null)
			modif=modif.func_240715_a_(new ClickEvent(ClickEvent.Action.valueOf(c.getClickEvent().getAction().name()), c.getClickEvent().getValue()));
		if(c.getHoverEvent()!=null)
			modif=modif.func_240716_a_(HoverEvent.Action.func_150684_a(c.getHoverEvent().getAction().name().toLowerCase()).func_240670_a_((ITextComponent)toIChatBaseComponent(c.getHoverEvent().getValue())));
		if(c.isBold())
			modif=modif.func_240720_a_(TextFormatting.BOLD);
		if(c.isItalic())
			modif=modif.func_240720_a_(TextFormatting.ITALIC);
		if(c.isObfuscated())
			modif=modif.func_240720_a_(TextFormatting.OBFUSCATED);
		if(c.isUnderlined())
			modif=modif.func_240720_a_(TextFormatting.UNDERLINE);
		if(c.isStrikethrough())
			modif=modif.func_240720_a_(TextFormatting.STRIKETHROUGH);
		current.func_230530_a_(modif);
		return current;
	}

	@Override
	public Object toIChatBaseComponents(List<Component> components) {
		List<ITextComponent> chat = new ArrayList<>();
		chat.add(new StringTextComponent(""));
		for(Component c : components) {
			if(c.getText()==null||c.getText().isEmpty()) {
				if(c.getExtra()!=null)
					addConverted(chat, c.getExtra());
				continue;
			}
			chat.add(convert(c));
			if(c.getExtra()!=null)
				addConverted(chat, c.getExtra());
		}
		return chat.toArray(new ITextComponent[0]);
	}

	
	private void addConverted(List<ITextComponent> chat, List<Component> extra) {
		for(Component c : extra) {
			if(c.getText()==null||c.getText().isEmpty()) {
				if(c.getExtra()!=null)
					addConverted(chat, c.getExtra());
				continue;
			}
			chat.add(convert(c));
		}
	}

	@Override
	public Object toIChatBaseComponents(Component co) {
		List<ITextComponent> chat = new ArrayList<>();
		chat.add(new StringTextComponent(""));
		if(co.getText()!=null && !co.getText().isEmpty())
			chat.add(convert(co));
		if(co.getExtra()!=null)
			for(Component c : co.getExtra()) {
				if(c.getText()==null||c.getText().isEmpty()) {
					if(c.getExtra()!=null)
						addConverted(chat, c.getExtra());
					continue;
				}
				chat.add(convert(c));
				if(c.getExtra()!=null)
					addConverted(chat, c.getExtra());
			}
		return chat.toArray(new ITextComponent[0]);
	}

	@Override
	public Object toIChatBaseComponent(Component co) {
		StringTextComponent main = new StringTextComponent("");
		List<ITextComponent> chat = new ArrayList<>();
		if(co.getText()!=null && !co.getText().isEmpty())
			chat.add(convert(co));
		if(co.getExtra()!=null)
			for(Component c : co.getExtra()) {
				if(c.getText()==null||c.getText().isEmpty()) {
					if(c.getExtra()!=null)
						addConverted(chat, c.getExtra());
					continue;
				}
				chat.add(convert(c));
				if(c.getExtra()!=null)
					addConverted(chat, c.getExtra());
			}
		for(ITextComponent d : chat)
			main.func_230529_a_(d);
		return main.func_150253_a().isEmpty()?StringTextComponent.field_240750_d_:main;
	}

	@Override
	public Object toIChatBaseComponent(List<Component> cc) {
		StringTextComponent main = new StringTextComponent("");
		for(Component c : cc)
			main.func_230529_a_((ITextComponent)toIChatBaseComponent(c));
		return main.func_150253_a().isEmpty()?StringTextComponent.field_240750_d_:main;
	}

	@Override
	public Object chatBase(String json) {
		return ITextComponent.Serializer.func_240643_a_(json);
	}

	@Override
	public String fromIChatBaseComponent(Object component) {
		return CraftChatMessage.fromComponent((ITextComponent)component);
	}

	@Override
	public TheMaterial toMaterial(Object blockOrItemOrIBlockData) {
		if(blockOrItemOrIBlockData==null)return new TheMaterial(Material.AIR);
		if(blockOrItemOrIBlockData instanceof Block) {
			Block b = (Block)blockOrItemOrIBlockData;
			return new TheMaterial((ItemStack)CraftItemStack.asNewCraftStack(b.func_199767_j()));
		}
		if(blockOrItemOrIBlockData instanceof Item) {
			Item b = (Item)blockOrItemOrIBlockData;
			return new TheMaterial((ItemStack)CraftItemStack.asNewCraftStack(b));
		}
		if(blockOrItemOrIBlockData instanceof net.minecraft.block.BlockState) {
			net.minecraft.block.BlockState b = (net.minecraft.block.BlockState)blockOrItemOrIBlockData;
			return new TheMaterial((ItemStack)CraftItemStack.asNewCraftStack(b.func_177230_c().func_199767_j()));
		}
		return null;
	}

	@Override
	public Object toIBlockData(TheMaterial material) {
		if(material==null || material.getType()==null || material.getType()==Material.AIR)return Blocks.field_150350_a.func_176223_P();
		return ((CraftBlockData)Bukkit.createBlockData(material.getType(), material.getData()+"")).getState();
	}

	@Override
	public Object toItem(TheMaterial material) {
		if(material==null || material.getType()==null || material.getType()==Material.AIR)return Item.func_150898_a(Blocks.field_150350_a);
		return Item.func_150898_a(((CraftBlockData)Bukkit.createBlockData(material.getType(), material.getData()+"")).getState().func_177230_c());
	}
	
	@Override
	public Object toBlock(TheMaterial material) {
		if(material==null || material.getType()==null || material.getType()==Material.AIR)return Blocks.field_150350_a;
		return ((CraftBlockData)Bukkit.createBlockData(material.getType(), material.getData()+"")).getState().func_177230_c();
	}
	
	@Override
	public Object getChunk(World world, int x, int z) {
		return ((CraftChunk)world.getChunkAt(x, z)).getHandle();
	}

	@Override
	public void setBlock(Object chunk, int x, int y, int z, Object IblockData, int data) {
		net.minecraft.world.chunk.Chunk c = (net.minecraft.world.chunk.Chunk)chunk;
		ChunkSection sc = c.func_76587_i()[y>>4];
		if(sc==null) {
			c.func_76587_i()[y>>4]=sc=new ChunkSection(y >> 4 << 4);
		}
		BlockPos pos = new BlockPos(x,y,z);
		//REMOVE TILE ENTITY
		c.func_177434_r().remove(pos);
		
		sc.func_186049_g().func_222639_b(x&15, y&15, z&15, (net.minecraft.block.BlockState)IblockData);
		
		//ADD TILE ENTITY
		if(((net.minecraft.block.BlockState)IblockData).func_177230_c() instanceof ITileEntityProvider) {
			TileEntity ent = ((ITileEntityProvider) ((net.minecraft.block.BlockState)IblockData).func_177230_c()).func_196283_a_(c.func_177412_p());
			c.func_177434_r().put(pos,ent);
			Object packet = ent.func_189518_D_();
			Bukkit.getOnlinePlayers().forEach(player -> BukkitLoader.getPacketHandler().send(player, packet));
		}
	}

	@Override
	public void updateLightAt(Object chunk, int x, int y, int z) {
		net.minecraft.world.chunk.Chunk c = (net.minecraft.world.chunk.Chunk)chunk;
		c.func_177412_p().func_72863_F().func_212863_j_().func_215568_a(new BlockPos(x,y,z));
	}

	@Override
	public Object getBlock(Object chunk, int x, int y, int z) {
		net.minecraft.world.chunk.Chunk c = (net.minecraft.world.chunk.Chunk)chunk;
		ChunkSection sc = c.func_76587_i()[y>>4];
		if(sc==null)return Blocks.field_150350_a.func_176223_P();
		return sc.func_186049_g().func_186016_a(x&15, y&15, z&15);
	}

	@Override
	public int getData(Object chunk, int x, int y, int z) {
		return 0;
	}

	@Override
	public int getCombinedId(Object IblockDataOrBlock) {
		return Block.func_196246_j((net.minecraft.block.BlockState)IblockDataOrBlock);
	}

	@Override
	public Object blockPosition(int blockX, int blockY, int blockZ) {
		return new BlockPos(blockX, blockY, blockZ);
	}

	@Override
	public Object toIBlockData(Object data) {
		return ((CraftBlockData)data).getState();
	}

	@Override
	public Object toIBlockData(BlockState state) {
		return CraftMagicNumbers.getBlock(state.getType(),state.getRawData());
	}

	@Override
	public Object toBlock(Material type) {
		return CraftMagicNumbers.getBlock(type);
	}

	@Override
	public Object toItem(Material type, int data) {
		return CraftMagicNumbers.getItem(type, (short)data);
	}

	@Override
	public Object toIBlockData(Material type, int data) {
		return CraftMagicNumbers.getBlock(type, (byte)data);
	}

	@Override
	public Chunk toBukkitChunk(Object nmsChunk) {
		net.minecraft.world.chunk.Chunk chunk = (net.minecraft.world.chunk.Chunk)nmsChunk;
		return Bukkit.getWorld(chunk.func_177412_p().toString().substring(12, chunk.func_177412_p().toString().length()-1)).getChunkAt(chunk.func_76632_l().field_77276_a, chunk.func_76632_l().field_77275_b);
	}

	@Override
	public int getPing(Player player) {
		return ((ServerPlayerEntity)getPlayer(player)).field_71138_i;
	}

	@Override
	public Object getPlayerConnection(Player player) {
		return ((ServerPlayerEntity)getPlayer(player)).field_71135_a;
	}

	@Override
	public Object getConnectionNetwork(Object playercon) {
		return ((ServerPlayNetHandler)playercon).field_147371_a;
	}
	
	@Override
	public Object getNetworkChannel(Object network) {
		try {
			return channel.get(network);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public void closeGUI(Player player, Object container, boolean closePacket) {
		if(closePacket)
		BukkitLoader.getPacketHandler().send(player, new SCloseWindowPacket(BukkitLoader.getNmsProvider().getContainerId(container)));
		ServerPlayerEntity nmsPlayer = (ServerPlayerEntity)getPlayer(player);
		nmsPlayer.field_71070_bA=nmsPlayer.field_71070_bA;
	}

	@Override
	public void setSlot(Object container, int slot, Object item) {
		((net.minecraft.inventory.container.Container)container).func_75141_a(slot, (net.minecraft.item.ItemStack)item);
	}

	@Override
	public void setGUITitle(Player player, Object container, String legacy, int size, String title) {
		ServerPlayerEntity nmsPlayer = ((CraftPlayer)player).getHandle();
		int id = ((net.minecraft.inventory.container.Container)container).field_75152_c;
		NonNullList<net.minecraft.item.ItemStack> nmsItems = ((net.minecraft.inventory.container.Container)container).func_75138_a();
		BukkitLoader.getPacketHandler().send(player, packetOpenWindow(id,legacy,size,title));
		int i = 0;
		for(net.minecraft.item.ItemStack o : nmsItems) 
			BukkitLoader.getPacketHandler().send(player, packetSetSlot(id,i++, o));
		nmsPlayer.field_71070_bA=(net.minecraft.inventory.container.Container)container;
		((net.minecraft.inventory.container.Container)container).func_75134_a(nmsPlayer);
	}

	@Override
	public void openGUI(Player player, Object container, String legacy, int size, String title, ItemStack[] items) {
		ServerPlayerEntity nmsPlayer = ((CraftPlayer)player).getHandle();
		int id = ((net.minecraft.inventory.container.Container)container).field_75152_c;
		net.minecraft.item.ItemStack[] nmsItems = new net.minecraft.item.ItemStack[items.length];
		for(int i = 0; i < items.length; ++i) {
			ItemStack is = items[i];
			if(is==null||is.getType()==Material.AIR)continue;
			net.minecraft.item.ItemStack item = null;
			((net.minecraft.inventory.container.Container)container).func_75141_a(i,item=(net.minecraft.item.ItemStack) asNMSItem(is));
			nmsItems[i]=item;
		}
		BukkitLoader.getPacketHandler().send(player, packetOpenWindow(id,legacy,size,title));
		int i = 0;
		for(net.minecraft.item.ItemStack o : nmsItems) 
			BukkitLoader.getPacketHandler().send(player, packetSetSlot(id,i++, o));
		nmsPlayer.field_71070_bA=(net.minecraft.inventory.container.Container)container;
		((net.minecraft.inventory.container.Container)container).func_75134_a(nmsPlayer);
	}

	@Override
	public void openAnvilGUI(Player player, Object con, String title, ItemStack[] items) {
		RepairContainer container = (RepairContainer)con;
		ServerPlayerEntity nmsPlayer = ((CraftPlayer)player).getHandle();
		int id = container.field_75152_c;
		net.minecraft.item.ItemStack[] nmsItems = new net.minecraft.item.ItemStack[items.length];
		for(int i = 0; i < items.length; ++i) {
			ItemStack is = items[i];
			if(is==null||is.getType()==Material.AIR)continue;
			net.minecraft.item.ItemStack item = null;
			container.func_75141_a(i,item=(net.minecraft.item.ItemStack) asNMSItem(is));
			nmsItems[i]=item;
		}
		BukkitLoader.getPacketHandler().send(player, packetOpenWindow(id,"minecraft:anvil",0,title));
		int i = 0;
		for(net.minecraft.item.ItemStack o : nmsItems) 
			BukkitLoader.getPacketHandler().send(player, packetSetSlot(id,i++, o));
		nmsPlayer.field_71070_bA=(RepairContainer)container;
		((RepairContainer)container).func_75134_a(nmsPlayer);
	}

	@Override
	public Object createContainer(Inventory inv, Player player) {
		return inv.getType()==InventoryType.ANVIL?createAnvilContainer(inv, player):new CraftContainer(inv, ((CraftPlayer)player).getHandle(), (int)Ref.invoke(((CraftPlayer)player).getHandle(),"nextContainerCounterCB"));
	}

	@Override
	public Object getSlotItem(Object container, int slot) {
		return ((net.minecraft.inventory.container.Container)container).func_75139_a(slot).func_75211_c();
	}

	static BlockPos zero = new BlockPos(0,0,0);
	
	public Object createAnvilContainer(Inventory inv, Player player) {
		RepairContainer container = new RepairContainer(((CraftPlayer)player).getHandle().func_142013_aG(),((CraftPlayer)player).getHandle().field_71071_by,IWorldPosCallable.func_221488_a(((CraftPlayer)player).getHandle().field_70170_p, zero));
		for(int i = 0; i < 2; ++i)
			container.func_75139_a(i).func_75215_d((net.minecraft.item.ItemStack) asNMSItem(inv.getItem(i)));
		return container;
	}
	
	@Override
	public String getAnvilRenameText(Object anvil) {
		try {
			return (String)repairText.get(anvil);
		} catch (Exception e) {
			return "";
		}
	}
	
	@Override
	public boolean processInvClickPacket(Player player, HolderGUI gui, Object provPacket) {
		CClickWindowPacket packet = (CClickWindowPacket)provPacket;
		int slot = packet.func_149544_d();
		if(slot==-999)return false;
		
		int id = packet.func_149548_c();
		int mouseClick = packet.func_149543_e();
		InventoryClickType type = InventoryClickType.valueOf(packet.func_186993_f().name());
		
		Object container = gui.getContainer(player);
		ItemStack item = asBukkitItem(packet.func_149546_g());
		if((type==InventoryClickType.QUICK_MOVE||type==InventoryClickType.CLONE||type==InventoryClickType.THROW||item.getType().isAir()) && item.getType().isAir())
			item=asBukkitItem(getSlotItem(container, slot));
		boolean cancel = false;
		if(InventoryClickType.SWAP==type) {
			item=player.getInventory().getItem(mouseClick);
			mouseClick=0;
			cancel=true;
		}
		if(item==null)item=new ItemStack(Material.AIR);
		
		ItemStack before = player.getItemOnCursor();
		ClickType clickType = BukkitLoader.buildClick(item, type, slot, mouseClick);
		if(!cancel)
			cancel = BukkitLoader.useItem(player, item, gui, slot, clickType);
		if(!gui.isInsertable())cancel=true;
		
		int gameSlot = slot>gui.size()-1?InventoryUtils.convertToPlayerInvSlot(slot-gui.size()):slot;
		if(!cancel)cancel=gui.onIteractItem(player, item, clickType, gameSlot, slot<gui.size());
		else gui.onIteractItem(player, item, clickType, gameSlot, slot<gui.size());
		int position = 0;
		if(!(gui instanceof AnvilGUI) && !cancel && type==InventoryClickType.QUICK_MOVE) {
			ItemStack[] contents = slot<gui.size()?player.getInventory().getStorageContents():gui.getInventory().getStorageContents();
			List<Integer> modified = slot<gui.size()?InventoryUtils.shift(slot,player,gui,clickType,gui instanceof AnvilGUI?DestinationType.PLAYER_INV_ANVIL:DestinationType.PLAYER_INV_CUSTOM_INV,null, contents, item):InventoryUtils.shift(slot,player,gui,clickType,DestinationType.CUSTOM_INV,gui.getNotInterableSlots(player), contents, item);
			if(!modified.isEmpty()) {
				if(slot<gui.size()) {
					boolean canRemove = !modified.contains(-1);
					player.getInventory().setStorageContents(contents);
					if(canRemove) {
						gui.remove(gameSlot);
					}else {
						gui.getInventory().setItem(gameSlot, item);
					}
				}else {
					boolean canRemove = !modified.contains(-1);
					gui.getInventory().setStorageContents(contents);
					if(canRemove) {
						player.getInventory().setItem(gameSlot, null);
					}else {
						player.getInventory().setItem(gameSlot, item);
					}
				}
			}
			return true;
		}
		if(cancel) {
			//MOUSE
			BukkitLoader.getPacketHandler().send(player,packetSetSlot(-1, -1, asNMSItem(before)));
			switch(type) {
			case CLONE:
				return true;
			case SWAP:
			case QUICK_MOVE:
			case PICKUP_ALL:
				//TOP
				for(ItemStack cItem : gui.getInventory().getContents()) {
					BukkitLoader.getPacketHandler().send(player,packetSetSlot(id, position++, asNMSItem(cItem)));
				}
				//BUTTON
				player.updateInventory();
				return true;
			default:
				BukkitLoader.getPacketHandler().send(player,packetSetSlot(id, slot, getSlotItem(container,slot)));
				if(gui instanceof AnvilGUI) {
					//TOP
					for(ItemStack cItem : gui.getInventory().getContents()) {
						if(position!=slot)
						BukkitLoader.getPacketHandler().send(player,packetSetSlot(id, position++, asNMSItem(cItem)));
					}
					//BUTTON
					player.updateInventory();
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean processServerListPing(String player, Object channel, Object packet) {
		SServerInfoPacket status = (SServerInfoPacket)packet;
		ServerStatusResponse ping;
		try {
			ping = (ServerStatusResponse) field.get(status);
		} catch (Exception e) {
			return false;
		}
		List<PlayerProfile> players = new ArrayList<>();
		for (Player p : Bukkit.getOnlinePlayers())
			players.add(new PlayerProfile(p.getName(), p.getUniqueId()));
		ServerListPingEvent event = new ServerListPingEvent(Bukkit.getOnlinePlayers().size(),
				Bukkit.getMaxPlayers(), players, Bukkit.getMotd(), ping.func_151316_d(),
				((InetSocketAddress) ((Channel)channel).remoteAddress()).getAddress(), ping.func_151322_c().func_151303_a(), ping.func_151322_c().func_151304_b());
		EventManager.call(event);
		if (event.isCancelled())
			return true;
		Players playerSample = new Players(event.getMaxPlayers(), event.getOnlinePlayers());
		if (event.getPlayersText() != null) {
			GameProfile[] profiles = new GameProfile[event.getPlayersText().size()];
			int i = -1;
			for (PlayerProfile s : event.getPlayersText())
				profiles[++i] = new GameProfile(s.getUUID(), s.getName());
			playerSample.func_151330_a(profiles);
		} else
			playerSample.func_151330_a(new GameProfile[0]);
		ping.func_151319_a(playerSample);

		if (event.getMotd() != null)
			ping.func_151315_a((ITextComponent)toIChatBaseComponent(ComponentAPI.fromString(event.getMotd())));
		else
			ping.func_151315_a((ITextComponent)BukkitLoader.getNmsProvider().chatBase("{\"text\":\"\"}"));
		if(event.getVersion()!=null)
			ping.func_151321_a(new Version(event.getVersion(), event.getProtocol()));
		if (event.getFalvicon() != null)
			ping.func_151320_a(event.getFalvicon());
		return false;
	}
	
	public Object getNBT(Entity entity) {
		return ((CraftEntity)entity).getHandle().func_189511_e(new CompoundNBT());
	}

	@Override
	public Object setString(Object nbt, String path, String value) {
		((CompoundNBT)nbt).func_74778_a(path, value);
		return nbt;
	}

	@Override
	public Object setInteger(Object nbt, String path, int value) {
		((CompoundNBT)nbt).func_74768_a(path, value);
		return nbt;
	}

	@Override
	public Object setDouble(Object nbt, String path, double value) {
		((CompoundNBT)nbt).func_74780_a(path, value);
		return nbt;
	}

	@Override
	public Object setLong(Object nbt, String path, long value) {
		((CompoundNBT)nbt).func_74772_a(path, value);
		return nbt;
	}

	@Override
	public Object setShort(Object nbt, String path, short value) {
		((CompoundNBT)nbt).func_74777_a(path, value);
		return nbt;
	}

	@Override
	public Object setFloat(Object nbt, String path, float value) {
		((CompoundNBT)nbt).func_74776_a(path, value);
		return nbt;
	}

	@Override
	public Object setBoolean(Object nbt, String path, boolean value) {
		((CompoundNBT)nbt).func_74757_a(path, value);
		return nbt;
	}

	@Override
	public Object setIntArray(Object nbt, String path, int[] value) {
		((CompoundNBT)nbt).func_74783_a(path, value);
		return nbt;
	}

	@Override
	public Object setByteArray(Object nbt, String path, byte[] value) {
		((CompoundNBT)nbt).func_74773_a(path, value);
		return nbt;
	}

	@Override
	public Object setNBTBase(Object nbt, String path, Object value) {
		((CompoundNBT)nbt).func_218657_a(path, (INBT)value);
		return nbt;
	}

	@Override
	public String getString(Object nbt, String path) {
		return ((CompoundNBT)nbt).func_74779_i(path);
	}

	@Override
	public int getInteger(Object nbt, String path) {
		return ((CompoundNBT)nbt).func_74762_e(path);
	}

	@Override
	public double getDouble(Object nbt, String path) {
		return ((CompoundNBT)nbt).func_74769_h(path);
	}

	@Override
	public long getLong(Object nbt, String path) {
		return ((CompoundNBT)nbt).func_74763_f(path);
	}

	@Override
	public short getShort(Object nbt, String path) {
		return ((CompoundNBT)nbt).func_74765_d(path);
	}

	@Override
	public float getFloat(Object nbt, String path) {
		return ((CompoundNBT)nbt).func_74760_g(path);
	}

	@Override
	public boolean getBoolean(Object nbt, String path) {
		return ((CompoundNBT)nbt).func_74767_n(path);
	}

	@Override
	public int[] getIntArray(Object nbt, String path) {
		return ((CompoundNBT)nbt).func_74759_k(path);
	}

	@Override
	public byte[] getByteArray(Object nbt, String path) {
		return ((CompoundNBT)nbt).func_74770_j(path);
	}

	@Override
	public Object getNBTBase(Object nbt, String path) {
		return ((CompoundNBT)nbt).func_74781_a(path);
	}

	@Override
	public Set<String> getKeys(Object nbt) {
		return ((CompoundNBT)nbt).func_150296_c();
	}

	@Override
	public boolean hasKey(Object nbt, String path) {
		return ((CompoundNBT)nbt).func_74764_b(path);
	}

	@Override
	public void removeKey(Object nbt, String path) {
		((CompoundNBT)nbt).func_82580_o(path);
	}

	@Override
	public Object setByte(Object nbt, String path, byte value) {
		((CompoundNBT)nbt).func_74774_a(path, value);
		return nbt;
	}

	@Override
	public byte getByte(Object nbt, String path) {
		return ((CompoundNBT)nbt).func_74771_c(path);
	}

	@Override
	public Object getDataWatcher(Entity entity) {
		return ((CraftEntity)entity).getHandle().func_184212_Q();
	}

	@Override
	public Object getDataWatcher(Object entity) {
		return ((net.minecraft.entity.Entity)entity).func_184212_Q();
	}

	@Override
	public int incrementStateId(Object container) {
		return 0;
	}

	@Override
	public Object packetEntityHeadRotation(Entity entity) {
		return new SEntityHeadLookPacket((net.minecraft.entity.Entity) getEntity(entity), (byte)(entity.getLocation().getYaw()*256F/360F));
	}

	@Override
	public Object packetHeldItemSlot(int slot) {
		return new SHeldItemChangePacket(slot);
	}

	@Override
	public Object packetExp(float exp, int total, int toNextLevel) {
		return new SSetExperiencePacket(exp, total, toNextLevel);
	}

	@Override
	public Object packetPlayerInfo(PlayerInfoType type, Player player) {
		return new SPlayerListItemPacket(SPlayerListItemPacket.Action.valueOf(type.name()), (ServerPlayerEntity)getPlayer(player));
	}

	@Override
	public Object packetPosition(double x, double y, double z, float yaw, float pitch) {
		return new SPlayerPositionLookPacket(x, y, z, yaw, pitch, Collections.emptySet(), 0);
	}

	@Override
	public Object packetRespawn(Player player) {
		ServerPlayerEntity pp = (ServerPlayerEntity)getPlayer(player);
		ServerWorld worldserver = (ServerWorld) pp.func_130014_f_();
		return new SRespawnPacket(worldserver.func_230315_m_(), worldserver.func_234923_W_(), BiomeManager.func_235200_a_(player.getWorld().getSeed()), pp.field_71134_c.func_241815_c_(), pp.field_71134_c.func_73081_b(), false, player.getWorld().getWorldType()==WorldType.FLAT, true);
	}

	@Override
	public String getProviderName() {
		return "1_16_R3 (1.16.5) Forge - Mohist";
	}

	@Override
	public int getContainerStateId(Object container) {
		return 0;
	}

	@Override
	public void loadParticles() {
		for(Entry<RegistryKey<ParticleType<?>>, ParticleType<?>> s : Registry.field_212632_u.func_239659_c_())
			me.devtec.theapi.bukkit.game.particles.Particle.identifier.put(s.getKey().func_240901_a_().func_110623_a(), s.getValue());
	}

}
