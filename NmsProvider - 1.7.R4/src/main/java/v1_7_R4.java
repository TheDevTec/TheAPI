import me.devtec.shared.Pair;
import me.devtec.shared.Ref;
import me.devtec.shared.components.*;
import me.devtec.shared.events.EventManager;
import me.devtec.shared.json.Json;
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
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.io.netty.channel.Channel;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_7_R4.CraftChunk;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.chunkio.ChunkIOExecutor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_7_R4.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.Map.Entry;

public class v1_7_R4 implements NmsProvider {
    private MinecraftServer server = MinecraftServer.getServer();
    private static ChatComponentText empty = new ChatComponentText("");
    private static Field channel = Ref.field(NetworkManager.class, "m");
    private static Field posX = Ref.field(PacketPlayOutBlockChange.class, "a"), posY = Ref.field(PacketPlayOutBlockChange.class, "b"), posZ = Ref.field(PacketPlayOutBlockChange.class, "c");
    private static Field score_a = Ref.field(PacketPlayOutScoreboardScore.class, "a"), score_b = Ref.field(PacketPlayOutScoreboardScore.class, "b"),
            score_c = Ref.field(PacketPlayOutScoreboardScore.class, "c"), score_d = Ref.field(PacketPlayOutScoreboardScore.class, "d");

    @Override
    public Collection<? extends Player> getOnlinePlayers() {
        Object value = Ref.invoke(Bukkit.getServer(), "getOnlinePlayers");
        if (value instanceof Collection) {
            return (Collection<? extends Player>) value;
        }
        Player[] players = (Player[]) value;
        return players.length == 0 ? Collections.emptyList() : Arrays.asList(players);
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
    public Object getScoreboardAction(Action type) {
        return type.getId();
    }

    @Override
    public int getEntityId(Object entity) {
        return ((net.minecraft.server.v1_7_R4.Entity) entity).getId();
    }

    @Override
    public Object getEnumScoreboardHealthDisplay(DisplayType type) {
        return null;
    }

    @Override
    public Object getNBT(ItemStack itemStack) {
        net.minecraft.server.v1_7_R4.ItemStack item = (net.minecraft.server.v1_7_R4.ItemStack) asNMSItem(itemStack);
        NBTTagCompound nbt = item.getTag();
        if (nbt == null)
            item.setTag(nbt = new NBTTagCompound());
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
        if (nbt instanceof NBTEdit)
            nbt = ((NBTEdit) nbt).getNBT();
        net.minecraft.server.v1_7_R4.ItemStack i = (net.minecraft.server.v1_7_R4.ItemStack) asNMSItem(stack);
        i.setTag((NBTTagCompound) nbt);
        return asBukkitItem(i);
    }

    private static final net.minecraft.server.v1_7_R4.ItemStack air = CraftItemStack.asNMSCopy(new ItemStack(Material.AIR));

    @Override
    public Object asNMSItem(ItemStack stack) {
        if (stack == null)
            return v1_7_R4.air;
        return CraftItemStack.asNMSCopy(stack);
    }

    @Override
    public ItemStack asBukkitItem(Object stack) {
        return CraftItemStack.asCraftMirror((net.minecraft.server.v1_7_R4.ItemStack) stack);
    }

    @Override
    public Object packetOpenWindow(int id, String legacy, int size, Component title) {
        return new PacketPlayOutOpenWindow(id, legacy.equals("minecraft:chest") ? 0 : 8, title.toString(), size, false);
    }

    @Override
    public int getContainerId(Object container) {
        return ((Container) container).windowId;
    }

    @Override
    public Object packetResourcePackSend(String url, String hash, boolean requireRP, Component prompt) {
        return null;
    }

    @Override
    public Object packetSetSlot(int container, int slot, int stateId, Object itemStack) {
        return new PacketPlayOutSetSlot(container, slot, (net.minecraft.server.v1_7_R4.ItemStack) (itemStack == null ? asNMSItem(null) : itemStack));
    }

    public Object packetSetSlot(int container, int slot, Object itemStack) {
        return this.packetSetSlot(container, slot, 0, itemStack);
    }

    @Override
    public Object packetEntityMetadata(int entityId, Object dataWatcher, boolean bal) {
        return new PacketPlayOutEntityMetadata(entityId, (net.minecraft.server.v1_7_R4.DataWatcher) dataWatcher, bal);
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
        return new PacketPlayOutNamedEntitySpawn((EntityHuman) player);
    }

    @Override
    public Object packetSpawnEntityLiving(Object entityLiving) {
        return new PacketPlayOutSpawnEntityLiving((EntityLiving) entityLiving);
    }

    @Override
    public Object packetPlayerListHeaderFooter(Component header, Component footer) {
        return null;
    }

    @Override
    public Object packetBlockChange(int x, int y, int z, Object iblockdata, int data) {
        PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange();
        packet.data = data;
        packet.block = iblockdata == null ? Blocks.AIR : (Block) iblockdata;
        try {
            v1_7_R4.posX.set(packet, x);
            v1_7_R4.posY.set(packet, y);
            v1_7_R4.posZ.set(packet, z);
        } catch (Exception e) {
        }
        return packet;
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
        PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();
        try {
            v1_7_R4.score_a.set(packet, line);
            v1_7_R4.score_b.set(packet, player);
            v1_7_R4.score_c.set(packet, score);
            v1_7_R4.score_d.set(packet, getScoreboardAction(action));
        } catch (Exception err) {
        }
        return packet;
    }

    @Override
    public Object packetTitle(TitleAction action, Component text, int fadeIn, int stay, int fadeOut) {
        if (action == TitleAction.ACTIONBAR)
            return this.packetChat(ChatType.GAME_INFO, text, null);
        return null;
    }

    @Override
    public Object packetChat(ChatType type, Object chatBase, UUID uuid) {
        return new PacketPlayOutChat((IChatBaseComponent) chatBase, type.toByte());
    }

    @Override
    public Object packetChat(ChatType type, Component text, UUID uuid) {
        return this.packetChat(type, this.toIChatBaseComponent(text), uuid);
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

    private IChatBaseComponent convert(Component c) {
        ChatComponentText current = new ChatComponentText(c.toString()); // legacy
        ChatModifier modif = current.getChatModifier();
        if (c.getColor() != null)
            modif.setColor(EnumChatFormat.valueOf(c.getColor().toUpperCase()));
        if (c.getClickEvent() != null)
            modif.setChatClickable(new ChatClickable(EnumClickAction.valueOf(c.getClickEvent().getAction().name()), c.getClickEvent().getValue()));
        if (c.getHoverEvent() != null)
            switch (c.getHoverEvent().getAction()) {
                case SHOW_ITEM:
                    modif = modif.a(new ChatHoverable(EnumHoverAction.SHOW_ITEM, ChatSerializer.a(Json.writer().simpleWrite(c.getHoverEvent().getValue().toJsonMap()))));
                    break;
                default:
                    modif = modif.a(new ChatHoverable(EnumHoverAction.SHOW_TEXT, (IChatBaseComponent) this.toIChatBaseComponent(c.getHoverEvent().getValue())));
                    break;
            }
        modif.setBold(c.isBold());
        modif.setItalic(c.isItalic());
        modif.setRandom(c.isObfuscated());
        modif.setUnderline(c.isUnderlined());
        modif.setStrikethrough(c.isStrikethrough());
        return current.setChatModifier(modif);
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
            return new IChatBaseComponent[]{empty};
        if (co instanceof ComponentItem || co instanceof ComponentEntity)
            return new IChatBaseComponent[]{ChatSerializer.a(Json.writer().simpleWrite(co.toJsonMap()))};
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

    @SuppressWarnings("unchecked")
    @Override
    public Object toIChatBaseComponent(Component co) {
        if (co == null)
            return empty;
        if (co instanceof ComponentItem || co instanceof ComponentEntity)
            return ChatSerializer.a(Json.writer().simpleWrite(co.toJsonMap()));
        ChatComponentText main = new ChatComponentText("");
        List<IChatBaseComponent> chat = new ArrayList<>();
        if (co.getText() != null && !co.getText().isEmpty())
            chat.add(convert(co));
        if (co.getExtra() != null)
            for (Component c : co.getExtra()) {
                chat.add(convert(c));
                if (c.getExtra() != null)
                    addConverted(chat, c.getExtra());
            }
        main.a().addAll(chat);
        return main.a().isEmpty() ? empty : main;
    }

    @Override
    public Object toIChatBaseComponent(List<Component> cc) {
        ChatComponentText main = new ChatComponentText("");
        for (Component c : cc)
            main.addSibling((IChatBaseComponent) this.toIChatBaseComponent(c));
        return main.a().isEmpty() ? empty : main;
    }

    @Override
    public Object chatBase(String json) {
        return ChatSerializer.a(json);
    }

    @Override
    public Component fromIChatBaseComponent(Object componentObject) {
        if (componentObject == null)
            return Component.EMPTY_COMPONENT;
        IChatBaseComponent component = (IChatBaseComponent) componentObject;
        if (component.e().isEmpty()) {
            Component comp = new Component("");
            if (!component.e().isEmpty()) {
                List<Component> extra = new ArrayList<>();
                for (Object base : component.a())
                    extra.add(fromIChatBaseComponent(base));
                comp.setExtra(extra);
            }
            return comp;
        }
        Component comp = new Component(component.e().replaceAll("§[A-Fa-f0-9K-Ok-oRr]", ""));
        ChatModifier modif = component.getChatModifier();
        if (modif.a() != null)
            comp.setColor(modif.a().name().toLowerCase());

        if (modif.h() != null)
            comp.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(modif.h().a().name()), modif.h().b()));

        if (modif.i() != null)
            switch (HoverEvent.Action.valueOf(modif.i().a().b().toUpperCase())) {
                case SHOW_ITEM:
                    ComponentItem compEntity = ComponentItem.fromJson(modif.i().b().e());
                    if (compEntity != null)
                        comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, ComponentItem.fromJson(modif.i().b().e())));
                    break;
                default:
                    comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, fromIChatBaseComponent(modif.i().b())));
                    break;
            }
        comp.setBold(modif.b());
        comp.setItalic(modif.c());
        comp.setObfuscated(modif.d());
        comp.setUnderlined(modif.e());
        comp.setStrikethrough(modif.f());

        if (!component.e().isEmpty()) {
            List<Component> extra = new ArrayList<>();
            for (Object base : component.a())
                extra.add(fromIChatBaseComponent(base));
            comp.setExtra(extra);
        }
        return comp;
    }

    @Override
    public BlockDataStorage toMaterial(Object block) {
        if (block instanceof Block)
            return new BlockDataStorage(CraftMagicNumbers.getMaterial((Block) block));
        return new BlockDataStorage(Material.AIR);
    }

    @Override
    public Object toIBlockData(BlockDataStorage material) {
        if (material == null || material.getType() == null || material.getType() == Material.AIR)
            return Blocks.AIR;
        return CraftMagicNumbers.getBlock(material.getType());
    }

    @Override
    public Object toBlock(BlockDataStorage material) {
        if (material == null || material.getType() == null || material.getType() == Material.AIR)
            return Blocks.AIR;
        return CraftMagicNumbers.getBlock(material.getType());
    }

    @Override
    public ItemStack toItemStack(BlockDataStorage material) {
        Item item = CraftMagicNumbers.getItem(material.getType());
        ItemStack itemStack = CraftItemStack.asBukkitCopy(new net.minecraft.server.v1_7_R4.ItemStack(item));
        itemStack.getData().setData(material.getItemData());
        return itemStack;
    }

    private static Field chunkLoader = Ref.field(ChunkProviderServer.class, "f");

    @Override
    public Object getChunk(World world, int x, int z) {
        WorldServer sworld = ((CraftWorld) world).getHandle();
        net.minecraft.server.v1_7_R4.Chunk loaded = ((ChunkProviderServer) sworld.L()).getChunkIfLoaded(x, z);
        if (loaded == null)
            try {
                net.minecraft.server.v1_7_R4.Chunk chunk;
                chunk = ((IChunkLoader) Ref.get(sworld.L(), chunkLoader)).a(sworld, x, z);
                if (chunk != null) {
                    chunk.lastSaved = sworld.getTime();
                    if (((ChunkProviderServer) sworld.L()).chunkProvider != null)
                        ((ChunkProviderServer) sworld.L()).chunkProvider.recreateStructures(x, z);
                }
                if (chunk != null) {
                    ((ChunkProviderServer) sworld.L()).chunks.put(ChunkCoordIntPair.a(x, z), chunk);
                    postToMainThread(() -> {
                        chunk.addEntities();
                    });
                    loaded = chunk;
                }
            } catch (Exception e) {
            }
        if (loaded == null) { // generate new chunk
            ChunkRegionLoader loader = null;
            if ((IChunkLoader) Ref.get(sworld.L(), chunkLoader) instanceof ChunkRegionLoader)
                loader = (ChunkRegionLoader) Ref.get(sworld.L(), chunkLoader);

            if (loader != null && loader.chunkExists(sworld, x, z))
                loaded = ChunkIOExecutor.syncChunkLoad(sworld, loader, (ChunkProviderServer) sworld.L(), x, z);
            else
                loaded = ((ChunkProviderServer) sworld.L()).originalGetChunkAt(x, z);
            loaded = ((ChunkProviderServer) sworld.L()).chunkProvider.getOrCreateChunk(x, z);
            ((ChunkProviderServer) sworld.L()).chunks.put(ChunkCoordIntPair.a(x, z), loaded);
        }
        return loaded;
    }

    private static Field tileEntityBlock = Ref.field(TileEntity.class, "h");
    private static Field isCachedInWorld = Ref.field(net.minecraft.server.v1_7_R4.World.class, "M");
    private static Field tileEntityWorld = Ref.field(net.minecraft.server.v1_7_R4.World.class, "a");

    @SuppressWarnings("unchecked")
    @Override
    public void setBlock(Object objChunk, int x, int y, int z, Object block, int data) {
        net.minecraft.server.v1_7_R4.Chunk chunk = (net.minecraft.server.v1_7_R4.Chunk) objChunk;
        if (y < 0)
            return;
        ChunkSection sc = chunk.getSections()[y >> 4];
        if (sc == null)
            return;

        ChunkPosition pos = new ChunkPosition(x & 15, y & 15, z & 15);
        Block iblock = block == null ? Blocks.AIR : (Block) block;

        boolean onlyModifyState = iblock instanceof IContainer;

        // REMOVE TILE ENTITY IF NOT SAME TYPE
        TileEntity ent = onlyModifyState ? (TileEntity) chunk.tileEntities.get(pos) : (TileEntity) chunk.tileEntities.remove(pos);
        if (ent != null) {
            boolean shouldSkip = true;
            if (!onlyModifyState) {
                shouldSkip = false;
                chunk.tileEntities.remove(pos);
            } else if (onlyModifyState && !ent.q().getClass().equals(iblock.getClass())) {
                shouldSkip = false;
                onlyModifyState = false;
            }
            if (!shouldSkip) {
                ent.s();
                if ((boolean) Ref.get(chunk.world, isCachedInWorld)) {
                    @SuppressWarnings("rawtypes")
                    List list = (List) Ref.get(chunk.world, tileEntityWorld);
                    for (int l = 0; l < list.size(); ++l) {
                        TileEntity state = (TileEntity) list.get(l);
                        if (!state.r() && state.x == x && state.y == y && state.z == z) {
                            list.remove(l);
                            break;
                        }
                    }
                }
                if (chunk.world.captureBlockStates) {
                    Iterator<BlockState> iterator = chunk.world.capturedBlockStates.iterator();
                    while (iterator.hasNext()) {
                        BlockState state = iterator.next();
                        if (state.getX() == x && state.getY() == y && state.getZ() == z)
                            iterator.remove();
                    }
                }
            }
        }

        sc.setTypeId(x & 15, y & 15, z & 15, iblock);
        sc.setData(x & 15, y & 15, z & 15, data);

        // ADD TILE ENTITY
        if (iblock instanceof IContainer && !onlyModifyState) {
            ent = ((IContainer) iblock).a(chunk.world, 0);
            chunk.tileEntities.put(pos, ent);
            ent.a(chunk.world);
            ent.x = x;
            ent.y = y;
            ent.z = z;
            Ref.set(ent, tileEntityBlock, iblock);
            Object packet = ent.getUpdatePacket();
            BukkitLoader.getPacketHandler().send(chunk.world.getWorld().getPlayers(), packet);
        }

        // MARK CHUNK TO SAVE
        chunk.mustSave = true;
    }

    @Override
    public void updatePhysics(Object objChunk, int x, int y, int z, Object iblockdata) {
        net.minecraft.server.v1_7_R4.Chunk chunk = (net.minecraft.server.v1_7_R4.Chunk) objChunk;

        doPhysicsAround((WorldServer) chunk.world, x, y, z, (Block) iblockdata);
    }

    private void doPhysicsAround(WorldServer world, int x, int y, int z, Block block) {
        doPhysics(world, x + BlockFace.WEST.getModX(), y, z + BlockFace.WEST.getModZ(), block);
        doPhysics(world, x + BlockFace.EAST.getModX(), y, z + BlockFace.EAST.getModZ(), block);
        doPhysics(world, x, y - 1, z, block);
        doPhysics(world, x, y + 1, z, block);
        doPhysics(world, x + BlockFace.NORTH.getModX(), y, z + BlockFace.NORTH.getModZ(), block);
        doPhysics(world, x + BlockFace.SOUTH.getModX(), y, z + BlockFace.SOUTH.getModZ(), block);
    }

    private void doPhysics(WorldServer world, int x, int y, int z, Block block) {
        Block state = world.getType(x, y, z);
        state.doPhysics(world, x, y, z, block);
        if (state instanceof BlockFalling)
            ((BlockFalling) state).onPlace(world, x, y, z);
    }

    @Override
    public void updateLightAt(Object chunk, int x, int y, int z) {
        net.minecraft.server.v1_7_R4.Chunk c = (net.minecraft.server.v1_7_R4.Chunk) chunk;
        c.initLighting();
    }

    @Override
    public Object getBlock(Object chunk, int x, int y, int z) {
        net.minecraft.server.v1_7_R4.Chunk c = (net.minecraft.server.v1_7_R4.Chunk) chunk;
        if (y < 0)
            return Blocks.AIR;
        ChunkSection sc = c.getSections()[y >> 4];
        if (sc == null)
            return Blocks.AIR;
        return sc.getTypeId(x & 15, y & 15, z & 15);
    }

    @Override
    public byte getData(Object chunk, int x, int y, int z) {
        net.minecraft.server.v1_7_R4.Chunk c = (net.minecraft.server.v1_7_R4.Chunk) chunk;
        if (y < 0)
            return 0;
        ChunkSection sc = c.getSections()[y >> 4];
        if (sc == null)
            return 0;
        return (byte) sc.getData(x & 15, y & 15, z & 15);
    }

    @Override
    public String getNBTOfTile(Object objChunk, int x, int y, int z) {
        net.minecraft.server.v1_7_R4.Chunk chunk = (net.minecraft.server.v1_7_R4.Chunk) objChunk;
        NBTTagCompound tag = new NBTTagCompound();
        chunk.e(x & 15, y & 15, z & 15).b(tag);
        return tag.toString();
    }

    @Override
    public void setNBTToTile(Object objChunk, int x, int y, int z, String nbt) {
        net.minecraft.server.v1_7_R4.Chunk chunk = (net.minecraft.server.v1_7_R4.Chunk) objChunk;
        TileEntity ent = chunk.e(x & 15, y & 15, z & 15);
        NBTTagCompound parsedNbt = (NBTTagCompound) parseNBT(nbt);
        parsedNbt.setInt("x", x);
        parsedNbt.setInt("y", y);
        parsedNbt.setInt("z", z);
        ent.a(parsedNbt);
        Object packet = ent.getUpdatePacket();
        BukkitLoader.getPacketHandler().send(chunk.world.getWorld().getPlayers(), packet);
    }

    @Override
    public boolean isTileEntity(Object objChunk, int x, int y, int z) {
        net.minecraft.server.v1_7_R4.Chunk chunk = (net.minecraft.server.v1_7_R4.Chunk) objChunk;
        return chunk.e(x & 15, y & 15, z & 15) != null;
    }

    @Override
    public int getCombinedId(Object IblockDataOrBlock) {
        return Block.getId((Block) IblockDataOrBlock);
    }

    @Override
    public Object blockPosition(int blockX, int blockY, int blockZ) {
        return new ChunkPosition(blockX, blockY, blockZ);
    }

    @Override
    public Object toIBlockData(BlockState state) {
        return null;
    }

    @Override
    public Object toIBlockData(Object data) {
        return null;
    }

    @Override
    public Chunk toBukkitChunk(Object nmsChunk) {
        return ((net.minecraft.server.v1_7_R4.Chunk) nmsChunk).bukkitChunk;
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
        try {
            return v1_7_R4.channel.get(network);
        } catch (Exception e) {
            return null;
        }
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
        ((Container) container).setItem(slot, (net.minecraft.server.v1_7_R4.ItemStack) item);
    }

    @Override
    public void setGUITitle(Player player, Object container, String legacy, int size, Component title) {
        int id = ((Container) container).windowId;
        BukkitLoader.getPacketHandler().send(player, packetOpenWindow(id, legacy, size, title));
        net.minecraft.server.v1_7_R4.ItemStack carried = ((CraftPlayer) player).getHandle().inventory.getCarried();
        if (carried != null && carried.count != 0)
            BukkitLoader.getPacketHandler().send(player, new PacketPlayOutSetSlot(id, -1, carried));
        int slot = 0;
        for (Object objItem : ((Container) container).a()) {
            if (slot == size)
                break;
            net.minecraft.server.v1_7_R4.ItemStack item = (net.minecraft.server.v1_7_R4.ItemStack) objItem;
            if (item != null && item.count != 0)
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
            int id = ((CraftPlayer) player).getHandle().nextContainerCounter();
            ContainerAnvil container = new ContainerAnvil(((CraftPlayer) player).getHandle().inventory, ((CraftWorld) player.getWorld()).getHandle(), 0, 0, 0, ((CraftPlayer) player).getHandle());
            container.windowId = id;
            postToMainThread(() -> {
                int slot = 0;
                for (ItemStack stack : inv.getContents())
                    container.getSlot(slot++).set((net.minecraft.server.v1_7_R4.ItemStack) asNMSItem(stack));
            });
            container.checkReachable = false;
            return container;
        }
        return new CraftContainer(inv, player, ((CraftPlayer) player).getHandle().nextContainerCounter());
    }

    @Override
    public Object getSlotItem(Object container, int slot) {
        return slot < 0 ? null : ((Container) container).getSlot(slot).getItem();
    }

    static Field renameText = Ref.field(ContainerAnvil.class, "n");

    @Override
    public String getAnvilRenameText(Object anvil) {
        try {
            return (String) v1_7_R4.renameText.get(anvil);
        } catch (Exception e) {
            return null;
        }
    }

    public static int c(final int quickCraftData) {
        return quickCraftData >> 2 & 3;
    }

    public static int d(final int quickCraftData) {
        return quickCraftData & 3;
    }

    private enum InventoryClickType {
        PICKUP, QUICK_MOVE, SWAP, CLONE, THROW, QUICK_CRAFT, PICKUP_ALL;
    }

    @Override
    public boolean processInvClickPacket(Player player, HolderGUI gui, Object provPacket) {
        PacketPlayInWindowClick packet = (PacketPlayInWindowClick) provPacket;
        int slot = packet.slot;

        Object container = gui.getContainer(player);
        if (container == null)
            return false;

        int id = packet.c();
        int mouseClick = packet.e();
        InventoryClickType type = InventoryClickType.values()[packet.h()];

        if (slot < -1 && slot != -999)
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
                    if (nPlayer.inventory.getCarried() == null) { // pickup half
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
                if (nPlayer.inventory.getCarried() == null && slot >= 0) {
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
                newItem = slot <= -1 ? new ItemStack(Material.AIR) : asBukkitItem(packet.g());
                oldItem = slot <= -1 ? new ItemStack(Material.AIR) : asBukkitItem(packet.g());
                break;
        }

        if (oldItem.getType() == Material.AIR && newItem.getType() == Material.AIR)
            return true;

        boolean cancel = false;
        int gameSlot = slot > gui.size() - 1 ? InventoryUtils.convertToPlayerInvSlot(slot - gui.size()) : slot;

        ClickType clickType = InventoryUtils.buildClick(type == InventoryClickType.QUICK_CRAFT ? 1 : type == InventoryClickType.QUICK_MOVE ? 2 : 0, mouseClick);
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
                BukkitLoader.getPacketHandler().send(player, packetSetSlot(id, slot, 0, c.getSlot(slot).getItem()));
                break;
        }
        return true;
    }

    private void processEvent(Container c, InventoryClickType type, HolderGUI gui, Player player, int slot, int gameSlot, ItemStack newItem, ItemStack oldItem, PacketPlayInWindowClick packet,
                              int mouseClick, ClickType clickType, EntityPlayer nPlayer) {
        net.minecraft.server.v1_7_R4.ItemStack result;
        switch (type) {
            case QUICK_MOVE: {
                ItemStack[] contents = slot < gui.size() ? player.getInventory().getContents() : gui.getInventory().getContents();
                boolean interactWithResultSlot = false;
                if (gui instanceof AnvilGUI && slot < gui.size() && slot == 2)
                    if (c.getSlot(2).isAllowed(nPlayer))
                        interactWithResultSlot = true;
                    else
                        return;
                Pair pairResult = slot < gui.size()
                        ? InventoryUtils.shift(slot, player, gui, clickType, gui instanceof AnvilGUI && slot != 2 ? DestinationType.PLAYER_FROM_ANVIL : DestinationType.PLAYER, null, contents, oldItem)
                        : InventoryUtils.shift(slot, player, gui, clickType, DestinationType.GUI, gui.getNotInterableSlots(player), contents, oldItem);
                @SuppressWarnings("unchecked")
                Map<Integer, ItemStack> modified = (Map<Integer, ItemStack>) pairResult.getValue();
                int remaining = (int) pairResult.getKey();

                if (!modified.isEmpty())
                    if (slot < gui.size()) {
                        for (Entry<Integer, ItemStack> modif : modified.entrySet())
                            nPlayer.inventory.setItem(modif.getKey(), (net.minecraft.server.v1_7_R4.ItemStack) asNMSItem(modif.getValue()));
                        if (remaining == 0) {
                            c.getSlot(gameSlot).set((net.minecraft.server.v1_7_R4.ItemStack) asNMSItem(null));
                            if (interactWithResultSlot) {
                                c.getSlot(0).set((net.minecraft.server.v1_7_R4.ItemStack) asNMSItem(null));
                                c.getSlot(1).set((net.minecraft.server.v1_7_R4.ItemStack) asNMSItem(null));
                            }
                        } else {
                            newItem.setAmount(remaining);
                            c.getSlot(gameSlot).set((net.minecraft.server.v1_7_R4.ItemStack) asNMSItem(newItem));
                        }
                    } else {
                        for (Entry<Integer, ItemStack> modif : modified.entrySet())
                            c.getSlot(modif.getKey()).set((net.minecraft.server.v1_7_R4.ItemStack) asNMSItem(modif.getValue())); // Visual & Nms side
                        // Plugin & Bukkit side
                        gui.getInventory().setContents(contents);
                        if (remaining == 0)
                            nPlayer.inventory.setItem(gameSlot, (net.minecraft.server.v1_7_R4.ItemStack) asNMSItem(null));
                        else {
                            newItem.setAmount(remaining);
                            nPlayer.inventory.setItem(gameSlot, (net.minecraft.server.v1_7_R4.ItemStack) asNMSItem(newItem));
                        }
                    }
                return;
            }
            default:
                result = processClick(gui, gui.getNotInterableSlots(player), c, slot, mouseClick, type, nPlayer);
                break;
        }
        postToMainThread(() -> {
            if (net.minecraft.server.v1_7_R4.ItemStack.matches(packet.g(), result)) {
                nPlayer.playerConnection.sendPacket(new PacketPlayOutTransaction(packet.c(), packet.f(), true));
                nPlayer.g = true;
                c.b();
                nPlayer.broadcastCarriedItem();
                nPlayer.g = false;
            } else {
                ((IntHashMap) Ref.get(nPlayer.playerConnection, "n")).a(c.windowId, packet.f());
                nPlayer.playerConnection.sendPacket(new PacketPlayOutTransaction(packet.c(), packet.f(), false));
                c.a(nPlayer, false);
                List<net.minecraft.server.v1_7_R4.ItemStack> arraylist = new ArrayList<>();

                for (Object element : c.c)
                    arraylist.add(((Slot) element).getItem());

                nPlayer.a(c, arraylist);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private net.minecraft.server.v1_7_R4.ItemStack processClick(HolderGUI gui, List<Integer> ignoredSlots, Container container, int slotIndex, int button, InventoryClickType actionType,
                                                                EntityPlayer player) {
        net.minecraft.server.v1_7_R4.ItemStack result = null;

        if (actionType == InventoryClickType.QUICK_CRAFT)
            processDragMove(gui, container, player, slotIndex, button);
        else {
            int u = (int) Ref.get(container, containerU);
            Set<Slot> mod = (Set<Slot>) Ref.get(container, containerV);
            if (u != 0) {
                Ref.set(container, containerU, u = 0);
                mod.clear();
            } else if (actionType == InventoryClickType.PICKUP && (button == 0 || button == 1)) {
                if (slotIndex == -999) {
                    if (player.inventory.getCarried() != null)
                        if (button == 0) {
                            net.minecraft.server.v1_7_R4.ItemStack carried = player.inventory.getCarried();
                            player.inventory.setCarried(null);
                            postToMainThread(() -> player.drop(carried, true));
                        } else if (player.inventory.getCarried().count > 0) {
                            net.minecraft.server.v1_7_R4.ItemStack itemstack4 = player.inventory.getCarried().a(1);
                            postToMainThread(() -> player.drop(itemstack4, true));
                        } else
                            player.inventory.setCarried(null);
                } else {
                    if (slotIndex < 0)
                        return null;

                    PlayerInventory playerinventory = player.inventory;

                    int k1;
                    net.minecraft.server.v1_7_R4.ItemStack itemstack1;
                    net.minecraft.server.v1_7_R4.ItemStack itemstack2;
                    Slot slot2 = (Slot) container.c.get(slotIndex);
                    if (slot2 != null) {
                        itemstack2 = slot2.getItem();
                        itemstack1 = playerinventory.getCarried();
                        if (itemstack2 != null)
                            result = itemstack2.cloneItemStack();
                        if (itemstack2 == null) {
                            if (itemstack1 != null && slot2.isAllowed(itemstack1)) {
                                k1 = button == 0 ? itemstack1.count : 1;
                                if (k1 > slot2.getMaxStackSize())
                                    k1 = slot2.getMaxStackSize();
                                if (itemstack1.count >= k1)
                                    slot2.set(itemstack1.a(k1));
                                if (itemstack1.count == 0)
                                    playerinventory.setCarried(null);
                                else
                                    BukkitLoader.getPacketHandler().send(player.getBukkitEntity(), BukkitLoader.getNmsProvider().packetSetSlot(-1, -1, 0, playerinventory.getCarried()));
                            }
                        } else if (slot2.isAllowed(player))
                            if (itemstack1 == null) {
                                k1 = button == 0 ? itemstack2.count : (itemstack2.count + 1) / 2;
                                playerinventory.setCarried(slot2.a(k1));
                                if (itemstack2.count == 0)
                                    slot2.set(null);
                                slot2.a(player, playerinventory.getCarried());
                            } else if (slot2.isAllowed(itemstack1)) {
                                if (itemstack2.getItem() == itemstack1.getItem() && itemstack2.getData() == itemstack1.getData()
                                        && net.minecraft.server.v1_7_R4.ItemStack.equals(itemstack2, itemstack1)) {
                                    k1 = button == 0 ? itemstack1.count : 1;
                                    if (k1 > slot2.getMaxStackSize() - itemstack2.count)
                                        k1 = slot2.getMaxStackSize() - itemstack2.count;

                                    if (k1 > itemstack1.getMaxStackSize() - itemstack2.count)
                                        k1 = itemstack1.getMaxStackSize() - itemstack2.count;

                                    itemstack1.a(k1);
                                    if (itemstack1.count == 0)
                                        playerinventory.setCarried(null);
                                    else
                                        BukkitLoader.getPacketHandler().send(player.getBukkitEntity(), BukkitLoader.getNmsProvider().packetSetSlot(-1, -1, 0, playerinventory.getCarried()));
                                    itemstack2.count += k1;
                                } else if (itemstack1.count <= slot2.getMaxStackSize()) {
                                    slot2.set(itemstack1);
                                    playerinventory.setCarried(itemstack2);
                                }
                            } else if (itemstack1.getMaxStackSize() > 1 && itemstack2.getItem() == itemstack1.getItem() && (!itemstack1.usesData() || itemstack1.getData() == itemstack2.getData())
                                    && net.minecraft.server.v1_7_R4.ItemStack.equals(itemstack2, itemstack1)) {
                                k1 = itemstack2.count;
                                int maxStack = Math.min(itemstack1.getMaxStackSize(), slot2.getMaxStackSize());
                                if (k1 > 0 && k1 + itemstack1.count <= maxStack) {
                                    itemstack1.count += k1;
                                    itemstack2 = slot2.a(k1);
                                    if (itemstack2.count == 0)
                                        slot2.set(null);

                                    slot2.a(player, playerinventory.getCarried());
                                } else
                                    BukkitLoader.getPacketHandler().send(player.getBukkitEntity(), BukkitLoader.getNmsProvider().packetSetSlot(-1, -1, 0, playerinventory.getCarried()));
                            }

                        slot2.f();
                        if (player instanceof EntityPlayer && slot2.getMaxStackSize() != 64) {
                            BukkitLoader.getPacketHandler().send(player.getBukkitEntity(), BukkitLoader.getNmsProvider().packetSetSlot(container.windowId, slot2.rawSlotIndex, 0, slot2.getItem()));
                            if (container.getBukkitView().getType() == InventoryType.WORKBENCH || container.getBukkitView().getType() == InventoryType.CRAFTING)
                                BukkitLoader.getPacketHandler().send(player.getBukkitEntity(), BukkitLoader.getNmsProvider().packetSetSlot(container.windowId, 0, 0, container.getSlot(0).getItem()));
                        }
                    }
                }
            } else if (actionType == InventoryClickType.SWAP) {
                if (slotIndex < 0)
                    return result;
                PlayerInventory playerinventory = player.inventory;
                Slot slot2 = (Slot) container.c.get(slotIndex);
                if (slot2.isAllowed(player)) {
                    net.minecraft.server.v1_7_R4.ItemStack itemstack1 = playerinventory.getItem(button);
                    boolean flag = itemstack1 == null || slot2.inventory == playerinventory && slot2.isAllowed(itemstack1);
                    int k1 = -1;
                    if (!flag) {
                        k1 = playerinventory.getFirstEmptySlotIndex();
                        flag |= k1 > -1;
                    }

                    if (slot2.hasItem() && flag) {
                        net.minecraft.server.v1_7_R4.ItemStack itemstack3 = slot2.getItem();
                        playerinventory.setItem(button, itemstack3.cloneItemStack());
                        if ((slot2.inventory != playerinventory || !slot2.isAllowed(itemstack1)) && itemstack1 != null) {
                            if (k1 > -1) {
                                playerinventory.pickup(itemstack1);
                                slot2.a(itemstack3.count);
                                slot2.set(null);
                                slot2.a(player, itemstack3);
                            }
                        } else {
                            slot2.a(itemstack3.count);
                            slot2.set(itemstack1);
                            slot2.a(player, itemstack3);
                        }
                    } else if (!slot2.hasItem() && itemstack1 != null && slot2.isAllowed(itemstack1)) {
                        playerinventory.setItem(button, null);
                        slot2.set(itemstack1);
                    }
                }
            } else if (actionType == InventoryClickType.CLONE && player.abilities.canInstantlyBuild && player.inventory.getCarried() == null && slotIndex >= 0) {
                Slot slot3 = container.getSlot(slotIndex);
                if (slot3 != null && slot3.hasItem()) {
                    net.minecraft.server.v1_7_R4.ItemStack itemstack2 = slot3.getItem().cloneItemStack();
                    itemstack2.count = itemstack2.getMaxStackSize();
                    player.inventory.setCarried(itemstack2);
                }
            } else if (actionType == InventoryClickType.THROW && player.inventory.getCarried() == null && slotIndex >= 0) {
                Slot slot2 = container.getSlot(slotIndex);
                if (slot2 != null && slot2.hasItem() && slot2.isAllowed(player)) {
                    net.minecraft.server.v1_7_R4.ItemStack itemstack2 = slot2.a(button == 0 ? 1 : slot2.getItem().count);
                    slot2.a(player, itemstack2);
                    postToMainThread(() -> player.drop(itemstack2, true));
                }
            } else if (actionType == InventoryClickType.PICKUP_ALL && slotIndex >= 0) {
                Slot slot2 = (Slot) container.c.get(slotIndex);
                net.minecraft.server.v1_7_R4.ItemStack itemstack1 = player.inventory.getCarried();
                if (itemstack1 != null && (slot2 == null || !slot2.hasItem() || !slot2.isAllowed(player))) {
                    List<Integer> ignoreSlots = ignoredSlots == null ? Collections.emptyList() : ignoredSlots;
                    List<Integer> corruptedSlots = ignoredSlots == null ? Collections.emptyList() : new ArrayList<>();
                    Map<Integer, ItemStack> modifiedSlots = new HashMap<>();
                    Map<Integer, ItemStack> modifiedSlotsPlayerInv = new HashMap<>();

                    int l = button == 0 ? 0 : container.c.size() - 1;
                    int i2 = button == 0 ? 1 : -1;

                    for (int l1 = 0; l1 < 2; ++l1)
                        for (int j2 = l; j2 >= 0 && j2 < container.c.size() && itemstack1.count < itemstack1.getMaxStackSize(); j2 += i2) {
                            Slot slot3 = (Slot) container.c.get(j2);
                            if (slot3.hasItem() && Container.a(slot3, itemstack1, true) && slot3.isAllowed(player) && container.a(itemstack1, slot3)) {
                                net.minecraft.server.v1_7_R4.ItemStack itemstack3 = slot3.getItem();
                                if (l1 != 0 || itemstack3.count != itemstack3.getMaxStackSize()) {
                                    if (j2 < gui.size() && ignoreSlots.contains(j2)) {
                                        corruptedSlots.add(j2);
                                        continue;
                                    }
                                    int count = Math.min(itemstack1.getMaxStackSize() - itemstack1.count, itemstack3.count);
                                    net.minecraft.server.v1_7_R4.ItemStack itemstack6 = slot3.a(count);
                                    itemstack1.count += count;
                                    if (itemstack6.count <= 0)
                                        slot3.set(null);
                                    slot3.a(player, itemstack6);
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
                        gui.onMultipleIteract(player.getBukkitEntity(), modifiedSlots, modifiedSlotsPlayerInv);
                    for (int s : corruptedSlots)
                        BukkitLoader.getPacketHandler().send(player.getBukkitEntity(), BukkitLoader.getNmsProvider().packetSetSlot(container.windowId, s, 0, ((Slot) container.c.get(s)).getItem()));
                }
                container.b();
            }
        }
        return result;
    }

    private Field containerU = Ref.field(Container.class, "g"), containerV = Ref.field(Container.class, "h"), containerT = Ref.field(Container.class, "dragType");

    @SuppressWarnings("unchecked")
    private void processDragMove(HolderGUI gui, Container container, EntityPlayer player, int slot, int mouseClick) {
        int previous = (int) Ref.get(container, containerU);
        int u = d(mouseClick);
        Set<Slot> mod = (Set<Slot>) Ref.get(container, containerV);
        if ((previous != 1 || u != 2) && previous != u || player.inventory.getCarried() == null) {
            mod.clear();
            u = 0;
        } else
            switch (u) {
                case 0: {
                    int t = c(mouseClick);
                    Ref.set(container, containerT, t);
                    if (Container.d(t)) {
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
                    final Slot bslot = container.getSlot(slot);
                    final net.minecraft.server.v1_7_R4.ItemStack itemstack = player.inventory.getCarried();
                    if (Container.a(bslot, itemstack, true) && bslot.isAllowed(itemstack) && itemstack.count > mod.size() && container.b(bslot))
                        mod.add(bslot);
                    break;
                }
                case 2:
                    if (!mod.isEmpty()) {
                        net.minecraft.server.v1_7_R4.ItemStack itemstack2 = player.inventory.getCarried();
                        if (itemstack2 == null) {
                            mod.clear();
                            Ref.set(container, containerU, 0);
                            return;
                        }
                        itemstack2 = itemstack2.cloneItemStack();
                        int t = (int) Ref.get(container, containerT);
                        int l = player.inventory.getCarried().count;
                        final Map<Integer, net.minecraft.server.v1_7_R4.ItemStack> draggedSlots = new HashMap<>();
                        for (Slot slot2 : mod) {
                            final net.minecraft.server.v1_7_R4.ItemStack itemstack3 = player.inventory.getCarried();
                            if (slot2 != null && Container.a(slot2, itemstack3, true) && slot2.isAllowed(itemstack3) && (t == 2 || itemstack3.count >= mod.size()) && container.b(slot2)) {

                                final int j1 = slot2.hasItem() ? slot2.getItem().count : 0;
                                final int k1 = Math.min(itemstack2.getMaxStackSize(), slot2.getMaxStackSize());
                                final int l2 = Math.min(a(mod, t, itemstack2) + j1, k1);
                                l -= l2 - j1;
                                net.minecraft.server.v1_7_R4.ItemStack stack = itemstack2.cloneItemStack();
                                stack.count = l2;
                                draggedSlots.put(slot2.rawSlotIndex, stack);
                            }
                        }
                        final InventoryView view = container.getBukkitView();
                        final org.bukkit.inventory.ItemStack newcursor = CraftItemStack.asCraftMirror(itemstack2);
                        newcursor.setAmount(l);
                        final Map<Integer, org.bukkit.inventory.ItemStack> guiSlots = new HashMap<>();
                        final Map<Integer, org.bukkit.inventory.ItemStack> playerSlots = new HashMap<>();
                        for (final Entry<Integer, net.minecraft.server.v1_7_R4.ItemStack> ditem : draggedSlots.entrySet())
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
                        for (final Entry<Integer, net.minecraft.server.v1_7_R4.ItemStack> dslot : draggedSlots.entrySet())
                            view.setItem(dslot.getKey(), CraftItemStack.asBukkitCopy(dslot.getValue()));
                        if (player.inventory.getCarried() != null)
                            player.updateInventory(container);
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

    public static int a(Set<Slot> c, int mode, net.minecraft.server.v1_7_R4.ItemStack stack) {
        int j;
        switch (mode) {
            case 0:
                j = MathHelper.d((float) stack.count / (float) c.size());
                break;
            case 1:
                j = 1;
                break;
            case 2:
                j = stack.getItem().getMaxStackSize();
                break;
            default:
                j = stack.count;
        }

        return j;
    }

    static Field field = Ref.field(PacketStatusOutServerInfo.class, "b");

    @Override
    public boolean processServerListPing(String player, Object channel, Object packet) {
        PacketStatusOutServerInfo status = (PacketStatusOutServerInfo) packet;
        ServerPing ping;
        try {
            ping = (ServerPing) v1_7_R4.field.get(status);
        } catch (Exception e) {
            return false;
        }

        List<GameProfileHandler> players = new ArrayList<>();
        if (ping.b().c() != null)
            for (GameProfile profile : ping.b().c())
                players.add(fromGameProfile(profile));

        ServerListPingEvent event = new ServerListPingEvent(getOnlinePlayers().size(), Bukkit.getMaxPlayers(), players, Bukkit.getMotd(), ping.d(),
                ((InetSocketAddress) ((Channel) channel).remoteAddress()).getAddress(), ping.c().a(), ping.c().b());
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
            ping.setServerInfo(new ServerPingServerData(event.getVersion(), event.getProtocol()));
        if (event.getFavicon() != null)
            ping.setFavicon(event.getFavicon());
        return false;
    }

    @Override
    public Object getNBT(Entity entity) {
        NBTTagCompound nbt = new NBTTagCompound();
        ((CraftEntity) entity).getHandle().e(nbt);
        return nbt;
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

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> getKeys(Object nbt) {
        return ((NBTTagCompound) nbt).c();
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
        return ((net.minecraft.server.v1_7_R4.Entity) entity).getDataWatcher();
    }

    @Override
    public int incrementStateId(Object container) {
        return 0;
    }

    @Override
    public Object packetEntityHeadRotation(Entity entity) {
        return new PacketPlayOutEntityHeadRotation((net.minecraft.server.v1_7_R4.Entity) getEntity(entity), (byte) (entity.getLocation().getYaw() * 256F / 360F));
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
        EntityPlayer entityPlayer = (EntityPlayer) getPlayer(player);
        switch (type) {
            case ADD_PLAYER:
                return PacketPlayOutPlayerInfo.addPlayer(entityPlayer);
            case REMOVE_PLAYER:
                return PacketPlayOutPlayerInfo.removePlayer(entityPlayer);
            case UPDATE_DISPLAY_NAME:
                return PacketPlayOutPlayerInfo.updateDisplayName(entityPlayer);
            case UPDATE_GAME_MODE:
                return PacketPlayOutPlayerInfo.updateGamemode(entityPlayer);
            case UPDATE_LATENCY:
                return PacketPlayOutPlayerInfo.updatePing(entityPlayer);
        }
        return null;
    }

    private static Field username = Ref.field(PacketPlayOutPlayerInfo.class, "username");
    private static Field player = Ref.field(PacketPlayOutPlayerInfo.class, "player");
    private static Field gamemode = Ref.field(PacketPlayOutPlayerInfo.class, "gamemode");
    private static Field ping = Ref.field(PacketPlayOutPlayerInfo.class, "ping");
    private static Field action = Ref.field(PacketPlayOutPlayerInfo.class, "action");

    @Override
    public Object packetPlayerInfo(PlayerInfoType type, GameProfileHandler gameProfile, int latency, GameMode gameMode, Component playerName) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();

        if (playerName == null)
            playerName = new Component(gameProfile.getUsername());

        if (gameMode == null)
            gameMode = GameMode.SURVIVAL;

        switch (type) {
            case ADD_PLAYER:
                Ref.set(packet, username, playerName.toString());
                Ref.set(packet, player, toGameProfile(gameProfile));
                Ref.set(packet, gamemode, net.minecraft.server.v1_7_R4.EnumGamemode.valueOf(gameMode.name()).getId());
                Ref.set(packet, ping, latency);
            case REMOVE_PLAYER:
                Ref.set(packet, action, 4);
                Ref.set(packet, username, playerName.toString());
                Ref.set(packet, player, toGameProfile(gameProfile));
            case UPDATE_DISPLAY_NAME:
                Ref.set(packet, action, 3);
                Ref.set(packet, username, playerName.toString());
                Ref.set(packet, player, toGameProfile(gameProfile));
            case UPDATE_GAME_MODE:
                Ref.set(packet, action, 3);
                Ref.set(packet, username, playerName.toString());
                Ref.set(packet, player, toGameProfile(gameProfile));
                Ref.set(packet, gamemode, net.minecraft.server.v1_7_R4.EnumGamemode.valueOf(gameMode.name()).getId());
            case UPDATE_LATENCY:
                Ref.set(packet, action, 3);
                Ref.set(packet, username, playerName.toString());
                Ref.set(packet, player, toGameProfile(gameProfile));
                Ref.set(packet, ping, latency);
        }
        return packet;
    }

    @Override
    public Object packetPosition(double x, double y, double z, float yaw, float pitch) {
        return new PacketPlayOutPosition(x, y, z, yaw, pitch, false);
    }

    @Override
    public Object packetRespawn(Player player) {
        EntityPlayer entityPlayer = (EntityPlayer) getPlayer(player);
        WorldServer worldserver = entityPlayer.r();
        byte actualDimension = (byte) worldserver.getWorld().getEnvironment().getId();
        return new PacketPlayOutRespawn((byte) (actualDimension >= 0 ? -1 : 0), worldserver.difficulty, worldserver.getWorldData().getType(), entityPlayer.playerInteractManager.getGameMode());
    }

    @Override
    public String getProviderName() {
        return "1_7_R4 (1.7.10)";
    }

    @Override
    public int getContainerStateId(Object container) {
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadParticles() {
        for (Entry<String, Object> entry : ((Map<String, Object>) Ref.getStatic(Ref.nms("", "PacketPlayOutWorldParticles$Particle"), "particleMap")).entrySet())
            me.devtec.theapi.bukkit.game.particles.Particle.identifier.put(entry.getKey().toUpperCase(), entry.getValue());
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