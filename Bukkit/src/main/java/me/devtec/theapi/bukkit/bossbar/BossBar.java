package me.devtec.theapi.bukkit.bossbar;

import me.devtec.shared.Ref;
import me.devtec.shared.utility.StringUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 1.7.10 - 1.8.8
 *
 * Updated by StraikerinaCZ 12.12. 2021
 */
public class BossBar {
	private static final Class<?> c = Ref.nms("EntityWither");
	private static final Constructor<?> tpC = Ref.constructor(
			Ref.nmsOrOld("network.protocol.game.PacketPlayOutEntityTeleport", "PacketPlayOutEntityTeleport"));
	private static final Constructor<?> barOld = Ref.constructor(BossBar.c, Ref.nmsOrOld("world.level.World", "World"));
	private static final Method mLoc = Ref.method(Ref.nmsOrOld("world.entity.Entity", "Entity"), "setLocation",
			double.class, double.class, double.class, float.class, float.class);

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
		this.set(text, progress);
		BukkitLoader.bossbars.add(this);
	}

	public void move() {
		if (!this.holder.isOnline() || this.entityBar == null)
			return;
		Location loc = this.holder.getLocation();
		Object packet = Ref.newInstance(BossBar.tpC);
		Ref.set(packet, "a", this.entityId);
		Ref.set(packet, "b", (int) ((loc.getX() - 30) * 32D));
		Ref.set(packet, "c", (int) ((loc.getY() - 100) * 32D));
		Ref.set(packet, "d", (int) (loc.getZ() * 32D));
		Ref.set(packet, "e", (byte) 0);
		Ref.set(packet, "f", (byte) 0);
		BukkitLoader.getPacketHandler().send(this.holder, packet);
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public double getProgress() {
		return this.progress;
	}

	public String getTitle() {
		return this.title;
	}

	public void hide() {
		if (this.hidden)
			return;
		BukkitLoader.bossbars.remove(this);
		if (!this.holder.isOnline())
			return;
		this.hidden = true;
		BukkitLoader.getPacketHandler().send(this.holder,
				BukkitLoader.getNmsProvider().packetEntityDestroy(this.entityId));
	}

	public void show() {
		if (!this.hidden || !this.holder.isOnline())
			return;
		this.hidden = false;
		Location loc = this.holder.getLocation();
		Ref.invoke(this.entityBar, BossBar.mLoc, loc.getX() - 25, loc.getY() - 100, loc.getZ(), 0, 0);
		Ref.invoke(this.entityBar, "setInvisible", true);
		Ref.invoke(this.entityBar, "setCustomName", this.title);
		Ref.invoke(this.entityBar, "setHealth", (float) this.progress);
		BukkitLoader.getPacketHandler().send(this.holder,
				BukkitLoader.getNmsProvider().packetSpawnEntityLiving(this.entityBar));
		BukkitLoader.getPacketHandler().send(this.holder, BukkitLoader.getNmsProvider()
				.packetEntityMetadata(this.entityId, BukkitLoader.getNmsProvider().getDataWatcher(this.entityBar)));
		BukkitLoader.bossbars.add(this);
	}

	private void set(String text, double progress) {
		if (!this.holder.isOnline())
			return;
		if (progress != -1)
			this.progress = progress;
		if (text != null)
			this.title = StringUtils.colorize(text);
		boolean cr = false;
		if (this.entityBar == null) {
			Location loc = this.holder.getLocation();
			this.entityBar = Ref.newInstance(BossBar.barOld, BukkitLoader.getNmsProvider().getWorld(loc.getWorld()));
			Ref.invoke(this.entityBar, BossBar.mLoc, loc.getX() - 25, loc.getY() - 100, loc.getZ(), 0, 0);
			this.entityId = BukkitLoader.getNmsProvider().getEntityId(this.entityBar);
			cr = true;
		}
		Ref.invoke(this.entityBar, "setInvisible", true);
		Ref.invoke(this.entityBar, "setCustomName", this.title);
		Ref.invoke(this.entityBar, "setHealth", (float) this.progress);
		if (cr)
			BukkitLoader.getPacketHandler().send(this.holder,
					BukkitLoader.getNmsProvider().packetSpawnEntityLiving(this.entityBar));
		BukkitLoader.getPacketHandler().send(this.holder, BukkitLoader.getNmsProvider()
				.packetEntityMetadata(this.entityId, BukkitLoader.getNmsProvider().getDataWatcher(this.entityBar)));
	}

	public void remove() {
		this.hide();
		this.hidden = false;
		this.entityBar = null;
		this.entityId = 0;
	}

	public void setTitle(String text) {
		this.set(text, -1);
	}

	public void setProgress(double progress) {
		this.set(null, progress);
	}
}