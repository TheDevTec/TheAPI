package me.devtec.theapi.bukkit.bossbar;

import me.devtec.shared.Ref;
import me.devtec.shared.scheduler.Tasker;
import me.devtec.shared.utility.ColorUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 1.7.10 - 1.8.8
 * <p>
 * Updated by Straikerinos 20.10. 2022
 */
public class BossBar {
    private static Class<?> witherClass;
    private static Constructor<?> teleportPacket;
    private static Constructor<?> entityWitcherConstructor;
    private static Constructor<?> dataWatcherConstructor;
    private static Method setLocation;
    private static Method setDataWatcherValue;
    private static Field dataWatcherField;

    private final Player holder;
    private World before;
    private boolean hidden;

    private String title;
    private double progress;

    private Object entityBar;
    private int entityId;

    static {
        if (Ref.isOlderThan(9)) {
            witherClass = Ref.nms("", "EntityWither");
            teleportPacket = Ref.constructor(Ref.nms("", "PacketPlayOutEntityTeleport"));
            entityWitcherConstructor = Ref.constructor(BossBar.witherClass, Ref.nms("", "World"));
            dataWatcherConstructor = Ref.constructor(Ref.nms("", "DataWatcher"), Ref.nms("", "Entity"));
            setLocation = Ref.method(Ref.nms("", "Entity"), "setLocation", double.class, double.class, double.class, float.class, float.class);
            setDataWatcherValue = Ref.method(Ref.nms("", "DataWatcher"), "a", int.class, Object.class);
            dataWatcherField = Ref.field(Ref.nms("", "PacketPlayOutSpawnEntityLiving"), "l"); // DataWatcher
            new Tasker() {

                @Override
                public void run() {
                    for (BossBar bar : JavaPlugin.getPlugin(BukkitLoader.class).bossbars)
                        bar.move();
                }
            }.runRepeating(1, 1);
        }
    }

    public BossBar(Player holder, String text, double progress) {
        this.holder = holder;
        if (!Ref.isOlderThan(9))
            throw new UnsupportedClassVersionError("This class is not supported for versions higher than 1.8.9");
        set(text, progress);
        JavaPlugin.getPlugin(BukkitLoader.class).bossbars.add(this);
    }

    public void move() {
        if (!holder.isOnline() || entityBar == null || hidden)
            return;
        Location loc = holder.getEyeLocation().add(holder.getEyeLocation().getDirection().normalize().multiply(60));
        if (loc.getY() < 1)
            loc.setY(1);
        if (!before.equals(loc.getWorld())) { // Switch world, respawn entity
            spawnBar(loc);
            return;
        }
        Object packet = Ref.newInstance(BossBar.teleportPacket);
        Ref.set(packet, "a", entityId);
        Ref.set(packet, "b", (int) (loc.getX() * 32D));
        Ref.set(packet, "c", (int) (loc.getY() * 32D));
        Ref.set(packet, "d", (int) (loc.getZ() * 32D));
        Ref.set(packet, "e", (byte) 0);
        Ref.set(packet, "f", (byte) 0);
        BukkitLoader.getPacketHandler().send(holder, packet);
    }

    public boolean isHidden() {
        return hidden;
    }

    public double getProgress() {
        return progress;
    }

    public String getTitle() {
        return title;
    }

    public void hide() {
        if (hidden)
            return;
        JavaPlugin.getPlugin(BukkitLoader.class).bossbars.remove(this);
        if (!holder.isOnline())
            return;
        hidden = true;
        BukkitLoader.getPacketHandler().send(holder, BukkitLoader.getNmsProvider().packetEntityDestroy(entityId));
    }

    public void show() {
        if (!hidden || !holder.isOnline())
            return;
        hidden = false;
        Location loc = holder.getEyeLocation().add(holder.getEyeLocation().getDirection().normalize().multiply(60));
        if (loc.getY() < 1)
            loc.setY(1);
        if (!before.equals(loc.getWorld())) { // Switch world, respawn entity
            spawnBar(loc);
            JavaPlugin.getPlugin(BukkitLoader.class).bossbars.add(this);
            return;
        }
        Object packet = BukkitLoader.getNmsProvider().packetSpawnEntityLiving(entityBar);
        Ref.set(packet, dataWatcherField, setupDataWatcher());
        BukkitLoader.getPacketHandler().send(holder, packet);
        JavaPlugin.getPlugin(BukkitLoader.class).bossbars.add(this);
    }

    private void set(String text, double progress) {
        if (!holder.isOnline())
            return;
        if (progress != -1)
            this.progress = progress * 200 / 100;
        if (text != null)
            title = ColorUtils.colorize(text);

        if (entityBar == null) {
            Location loc = holder.getEyeLocation().add(holder.getEyeLocation().getDirection().normalize().multiply(60));
            if (loc.getY() < 1)
                loc.setY(1);
            spawnBar(loc);
            return;
        }
        BukkitLoader.getPacketHandler().send(holder, BukkitLoader.getNmsProvider().packetEntityMetadata(entityId, setupDataWatcher()));
    }

    private void spawnBar(Location loc) {
        before = loc.getWorld();
        entityBar = Ref.newInstance(BossBar.entityWitcherConstructor, BukkitLoader.getNmsProvider().getWorld(loc.getWorld()));
        Ref.invoke(entityBar, BossBar.setLocation, loc.getX(), loc.getY(), loc.getZ(), 0, 0);
        entityId = BukkitLoader.getNmsProvider().getEntityId(entityBar);
        Object packet = BukkitLoader.getNmsProvider().packetSpawnEntityLiving(entityBar);
        Ref.set(packet, dataWatcherField, setupDataWatcher());
        BukkitLoader.getPacketHandler().send(holder, packet);
    }

    private Object setupDataWatcher() {
        Object watcher = Ref.newInstance(dataWatcherConstructor, (Object) null);
        Ref.invoke(watcher, setDataWatcherValue, 0, (byte) 0x20);
        Ref.invoke(watcher, setDataWatcherValue, 3, (byte) 1);
        Ref.invoke(watcher, setDataWatcherValue, 6, (float) progress);
        Ref.invoke(watcher, setDataWatcherValue, 2, title);
        Ref.invoke(watcher, setDataWatcherValue, 10, title);
        Ref.invoke(watcher, setDataWatcherValue, 20, 200000);
        return watcher;
    }

    public void remove() {
        hide();
        hidden = false;
        entityBar = null;
        entityId = 0;
    }

    public void setTitle(String text) {
        set(text, -1);
    }

    public void setProgress(double progress) {
        set(null, progress);
    }

    public Player getPlayer() {
        return holder;
    }
}