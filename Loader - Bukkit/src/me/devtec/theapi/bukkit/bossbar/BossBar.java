package me.devtec.theapi.bukkit.bossbar;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.devtec.shared.Ref;
import me.devtec.shared.utility.StringUtils;
import me.devtec.theapi.bukkit.BukkitLoader;

/**
 * 1.7.10 - 1.8.8
 *
 * Updated by StraikerinaCZ 12.12. 2021
 */
public class BossBar {
	private static final Class<?> c = Ref.nms("", "EntityWither");
	private static final Constructor<?> tpC = Ref.constructor(Ref.nms("", "PacketPlayOutEntityTeleport"));
	private static final Constructor<?> barOld = Ref.constructor(BossBar.c, Ref.nms("", "World"));
	private static final Method mLoc = Ref.method(Ref.nms("", "Entity"), "setLocation", double.class, double.class,
			double.class, float.class, float.class);

	private final Player holder;
	private boolean hidden;

	private String title;
	private double progress;

	private Object entityBar;
	private int entityId;

	public BossBar(Player holder, String text, double progress) {
		this.holder = holder;
		if (!Ref.isNewerThan(8)) {
			Bukkit.getConsoleSender()
					.sendMessage("[TheAPI - BossBar API] §4This class is not supported for versions higher than 1.8.8");
			return;
		}
		set(text, progress);
		BukkitLoader.bossbars.add(this);
	}

	public void move() {
		if (!holder.isOnline() || entityBar == null)
			return;
		Location loc = holder.getLocation();
		Object packet = Ref.newInstance(BossBar.tpC);
		Ref.set(packet, "a", entityId);
		Ref.set(packet, "b", (int) ((loc.getX() - 30) * 32D));
		Ref.set(packet, "c", (int) ((loc.getY() - 100) * 32D));
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
		BukkitLoader.bossbars.remove(this);
		if (!holder.isOnline())
			return;
		hidden = true;
		BukkitLoader.getPacketHandler().send(holder, BukkitLoader.getNmsProvider().packetEntityDestroy(entityId));
	}

	public void show() {
		if (!hidden || !holder.isOnline())
			return;
		hidden = false;
		Location loc = holder.getLocation();
		Ref.invoke(entityBar, BossBar.mLoc, loc.getX() - 25, loc.getY() - 100, loc.getZ(), 0, 0);
		Ref.invoke(entityBar, "setInvisible", true);
		Ref.invoke(entityBar, "setCustomName", title);
		Ref.invoke(entityBar, "setHealth", (float) progress);
		BukkitLoader.getPacketHandler().send(holder, BukkitLoader.getNmsProvider().packetSpawnEntityLiving(entityBar));
		BukkitLoader.getPacketHandler().send(holder, BukkitLoader.getNmsProvider().packetEntityMetadata(entityId,
				BukkitLoader.getNmsProvider().getDataWatcher(entityBar)));
		BukkitLoader.bossbars.add(this);
	}

	private void set(String text, double progress) {
		if (!holder.isOnline())
			return;
		if (progress != -1)
			this.progress = progress;
		if (text != null)
			title = StringUtils.colorize(text);
		boolean cr = false;
		if (entityBar == null) {
			Location loc = holder.getLocation();
			entityBar = Ref.newInstance(BossBar.barOld, BukkitLoader.getNmsProvider().getWorld(loc.getWorld()));
			Ref.invoke(entityBar, BossBar.mLoc, loc.getX() - 25, loc.getY() - 100, loc.getZ(), 0, 0);
			entityId = BukkitLoader.getNmsProvider().getEntityId(entityBar);
			cr = true;
		}
		Ref.invoke(entityBar, "setInvisible", true);
		Ref.invoke(entityBar, "setCustomName", title);
		Ref.invoke(entityBar, "setHealth", (float) this.progress);
		if (cr)
			BukkitLoader.getPacketHandler().send(holder,
					BukkitLoader.getNmsProvider().packetSpawnEntityLiving(entityBar));
		BukkitLoader.getPacketHandler().send(holder, BukkitLoader.getNmsProvider().packetEntityMetadata(entityId,
				BukkitLoader.getNmsProvider().getDataWatcher(entityBar)));
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
}